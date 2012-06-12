/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-19
 * Time: 07:10
 * To change this template use File | Settings | File Templates.
 */
package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

import com.cognos.CAM_AAA.authentication.UnrecoverableException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import se.su.it.cognos.cognosshibauth.ldap.Account;
import se.su.it.cognos.cognosshibauth.ldap.Group;
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder;
import se.su.it.cognos.cognosshibauth.ldap.Role;
import se.su.it.cognos.cognosshibauth.visa.Visa;


import java.util.logging.Level;
import java.util.logging.Logger;


import static se.su.it.cognos.cognosshibauth.ldap.UiClass.*
import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchStep.SearchAxis

import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import gldapo.search.SearchControlProvider
import java.security.MessageDigest;

public class CognosShibAuthBase extends CognosShibAuthNamespace implements INamespaceAuthenticationProviderBase {

  private Logger LOG = Logger.getLogger(CognosShibAuthBase.class.getName());

  HashMap<String,NamespaceFolder> folders = null;

  CognosShibAuthBase() {
    super()

    folders = new HashMap<String,NamespaceFolder>();
  }

  @Override
  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    super.init(iNamespaceConfiguration);

    loadFolders();
  }

  @Override
  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    Visa visa = (Visa) iVisa;
    try {
      visa.destroy();
    } catch (UnrecoverableException e) {
      LOG.log(Level.SEVERE, "Failed to destroy visa '" + visa + "' during logout.");
      e.printStackTrace();
    }
  }

  @Override
  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {

    QueryResult result = new QueryResult();

    try {
      ISearchExpression expression = iQuery.searchExpression
      IQueryOption queryOption = iQuery.queryOption

      String baseObjectID = expression.objectID // The base for the search expression, null == root of namespace
      ISearchStep[] steps = expression.steps

      // TODO handle multiple steps:
      //   IBM Cognos Connection does not use more than one step. Authentication
      //   providers currently supported by IBM Cognos BI do not support more than one
      //   step, either. Only some Software Development Kit applications can generate
      //   queries containing more than one step, therefore a custom authentication provider
      //   is required to support such queries.
      //   From "IBM Cognos Custom Authentication Provider Version 10.1.1 Developer Guide"

      if (steps.length != 1) {
        throw new UnrecoverableException(
                "Internal Error",
                "Invalid search expression. Multiple steps is not supported for this namespace.");
      }

      int searchAxis = steps.first().axis
      ISearchFilter filter = steps.first().predicate

      def key = "${baseObjectID}-${searchAxis}-${filter?.getSearchFilterType()}"

      def closure = { getQueryResult(searchAxis, baseObjectID, filter, queryOption) }

      List<IBaseClass> ret = null
      if(searchAxis != SearchAxis.Descendent)
        ret = Cache.getInstance().get(key, closure)
      else
        ret = closure()

      ret?.each { result.addObject(it) }
    }
    catch (Exception e) {
      //Fetch anything and do nothing (no stack traces in the gui for now)
      LOG.log(Level.SEVERE, "Failed while parsing search query: " + e.getMessage());
      e.printStackTrace();
    }
    return result
  }

  def getQueryResult(int searchAxis, String baseObjectID, ISearchFilter filter, IQueryOption queryOption) {
    def list = []

    switch (searchAxis) {
      case SearchAxis.Self:
        list << searchAxisSelf(baseObjectID)
        break;

      case SearchAxis.Child:
        list.addAll searchAxisChild(baseObjectID)
        break;

      case SearchAxis.Descendent:
        String key = "SEARCH_D_${baseObjectID}_${queryOption?.maxCount}_${queryOption?.skipCount}_${filterToString(filter)}"

        MessageDigest md5 = MessageDigest.getInstance("MD5")
        md5.update(key.bytes)
        def keyHash = new BigInteger(1, md5.digest()).toString(16)

        Cache.getInstance().get(keyHash, {list.addAll searchAxisDescendent(baseObjectID, filter, queryOption)})
        break

      // Not yet implemented
      case SearchAxis.Ancestor:
      case SearchAxis.AncestorOrSelf:
      case SearchAxis.DescendentOrSelf:
      case SearchAxis.Parent:
      case SearchAxis.Unknown:
      default:
        break;
    }

    list
  }

  String filterToString(ISearchFilter filter) {
    String ret = ""

    if (filter instanceof ISearchFilterConditionalExpression) {
      ret += filter.operator
      ret += filter.filters?.collect { filterToString(filter) }.join("")
    }
    else if (filter instanceof ISearchFilterFunctionCall) {
      ret += filter.functionName
      ret += filter.parameters.join("")
    }
    else if (filter instanceof ISearchFilterRelationExpression) {
      ret += filter.constraint
      ret += filter.operator
      ret += filter.propertyName
    }

    ret
  }
  
  def IBaseClass searchAxisSelf(String baseObjectID) {
    IBaseClass ret = null

    String name = camIdToName(baseObjectID)

    if (baseObjectID == null)
      ret = this

    else if (isUser(baseObjectID)) {
      String dn = name
      if (dn != null && !dn.trim().empty)
        ret = Account.createFromDn(dn)
    }

    else if (isGroup(baseObjectID)) {
      String dn = name
      if (dn != null && !dn.trim().empty)
        ret = new Group(dn)
    }

    else if (isRole(baseObjectID) && Role.exists(name))
      ret = new Role(name)

    else if (isFolder(baseObjectID))
      ret = folders.get(baseObjectID)

    ret
  }

  def List<IBaseClass> searchAxisChild(String baseObjectID) {
    List<IBaseClass> retList = new ArrayList<IBaseClass>()

    if (baseObjectID == null) // Root base
      retList.addAll folders?.values()?.findAll { it?.ancestors?.length <= 0 } ?: [] // Add all root folders.

    else if (isFolder(baseObjectID) && folders.containsKey(baseObjectID)) {
      NamespaceFolder baseFolder = folders.get(baseObjectID)

      retList.addAll baseFolder?.children ?: []
    }

    else if (isRole(baseObjectID)) {
      Role baseRole = new Role(camIdToName(baseObjectID))

      retList.addAll baseRole?.members ?: []
    }

    else if (isGroup(baseObjectID)) {
      Group baseGroup = new Group(camIdToName(baseObjectID))

      retList.addAll baseGroup?.members ?: []
    }

    retList
  }

  def List<IBaseClass> searchAxisDescendent(String baseObjectID, ISearchFilter filter, IQueryOption queryOption) {
    List<IBaseClass> retList = new ArrayList<IBaseClass>()

    List<String> objectTypes = findFilterObjectTypes(filter)

    objectTypes.each { objectType ->

      def ret = parseSearchFilter(filter, objectType)

      if (ret instanceof Collection) {
        long start = queryOption.skipCount ?: 0
        long stop = start + queryOption.maxCount ?:0

        if (start && stop)
          ret = ret[start..stop]

        retList.addAll ret
      }
      else if (ret instanceof String) {
        def searchControlProvider = [pageSize: queryOption.maxCount] as SearchControlProvider

        if (objectType == 'account') {
          retList.addAll SuPerson.findAll(filter : ret as String, controls: searchControlProvider).collect { new Account(it) }
        }
        else if (objectType == 'group') {
          retList.addAll GroupOfUniqueNames.findAll(filter : ret as String, controls: searchControlProvider).collect { new Group(it) }
        }
      }
    }

    retList
  }

  def parseSearchFilter(ISearchFilter iSearchFilter, String objectType) {

    List<IBaseClass> ret = new ArrayList<IBaseClass>()

    String filter = null

    if (iSearchFilter == ISearchFilter.ConditionalExpression) {
      ISearchFilterConditionalExpression item = iSearchFilter as ISearchFilterConditionalExpression
      def filterResults = item.filters.collect { parseSearchFilter(it, objectType) }

      if (filterResults.findAll {it instanceof String}.size() == filterResults.size()) {
        filter = filterResults.collect { (it as String).empty ? "" : "(${it})" }.join('')

        if (item.operator == ISearchFilterConditionalExpression.ConditionalOr)
          filter = "(|${filter})"
        else
          filter = "(&${filter})"
      }
    }
    else {
      String attribute = null, value = null, operator = null

      if (iSearchFilter.searchFilterType == ISearchFilter.FunctionCall) {
        ISearchFilterFunctionCall item = (ISearchFilterFunctionCall) iSearchFilter

        attribute = item.parameters.first()

        switch (item.functionName) {
          case ISearchFilterFunctionCall.Contains:
            value = "*${item.parameters[1]}*"
            break
          case ISearchFilterFunctionCall.StartsWith:
            value = "${item.parameters[1]}*"
            break
          case ISearchFilterFunctionCall.EndsWith:
            value = "*${item.parameters[1]}"
            break
        }
      }
      else if (iSearchFilter.searchFilterType == ISearchFilter.RelationalExpression) {
        ISearchFilterRelationExpression item = (ISearchFilterRelationExpression) iSearchFilter

        attribute = item.propertyName
        value = item.constraint
        operator = item.operator
      }

      if (attribute == "@objectClass")
        filter = ''
      else {
        if (objectType == 'account') {
          filter = Account.buildLdapFilter(attribute, value, operator)
        }
        if (objectType == 'group') {
          filter = Group.buildLdapFilter(attribute, value, operator)
        }
        if (objectType == 'role') {
          ret.addAll Role.findAllByName(value)
        }
        if (objectType == 'folder') {
          ret.addAll folders.values().findAll { it.getName(configHandler.contentLocale) }
        }
        if (objectType == 'namespace') {
          if (value == this.getName(configHandler.contentLocale))
            ret << this
        }
      }
    }

    if (filter)
      return filter

    ret
  }

  List<String> findFilterObjectTypes(ISearchFilter searchFilter) {
    List<String> ret = null

    if (searchFilter.searchFilterType == ISearchFilter.ConditionalExpression) {
      def filter = searchFilter as ISearchFilterConditionalExpression

      if (filter.operator == ISearchFilterConditionalExpression.ConditionalOr)
        ret = filter.filters.collect { findFilterObjectTypes(it) }
    }
    else
      ret = [ getFilterObjectType(searchFilter) ]

    ret = ret.unique()
    ret.removeAll {it == null}

    if (ret.empty)
      ret = ['account', 'group', 'role', 'folder', 'namespace']

    ret
  }

  String getFilterObjectType(ISearchFilter searchFilter) {
    String attribute = "", value = ""

    if (searchFilter.searchFilterType == ISearchFilter.RelationalExpression) {
      def filter = searchFilter as ISearchFilterRelationExpression
      attribute = filter.propertyName
      value = filter.constraint
    }
    else if (searchFilter.searchFilterType == ISearchFilter.FunctionCall) {
      def filter = searchFilter as ISearchFilterFunctionCall
      attribute = filter.parameters.first()
      value = filter.parameters[1]
    }

    if (attribute == "@objectClass" && value == null || attribute != "@objectClass")
      value = null

    value
  }

  /**
   *  Loads the folders specified in configuration file
   */
  private void loadFolders() {
    List<HierarchicalConfiguration> foldersConfiguration = configHandler.getFoldersConfig();
    for(HierarchicalConfiguration folderConfiguration : foldersConfiguration) {
      NamespaceFolder namespaceFolder = NamespaceFolder.configEntryToFolder(folders, folderConfiguration);
      folders.put(namespaceFolder.getObjectID(), namespaceFolder);
    }
  }
}