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
import se.su.it.cognos.cognosshibauth.ldap.schema.SchemaBase
import gldapo.Gldapo
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames;

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
      ISearchExpression expression = iQuery.getSearchExpression();
      String objectID = expression.getObjectID();
      ISearchStep[] steps = expression.getSteps();

      //TODO handle hierarchical steps
      if (steps.length != 1) {
        throw new UnrecoverableException(
                "Internal Error",
                "Invalid search expression. Multiple steps is not supported for this namespace.");
      }

      int searchType = steps.first().axis
      ISearchFilter filter = steps[0].getPredicate();

      def key = "${objectID}-${searchType}-${filter?.getSearchFilterType()}"

      def closure = { getQueryResult(searchType, objectID, filter) }

      List<IBaseClass> ret = null
      if(searchType != SearchAxis.Descendent)
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

  def getQueryResult(int searchType, String objectID, ISearchFilter filter) {
    def list = []

    switch (searchType) {
      case SearchAxis.Self:
        if (objectID == null) {
          if (filter == null || true) {
            list.add(this);
          }
          if (searchType == SearchAxis.Self) {
            return list;
          }
        }
        else if (isUser(objectID) && filter == null) {
          String dn = camIdToName(objectID);
          if (dn != null && !dn.trim().empty) {
            Account account = Account.createFromDn(dn);
            list << account
          }
        }
        else if (isRole(objectID)) {
          Role role = new Role(camIdToName(objectID));
          list << role
        }
        else if (isGroup(objectID)) {
          Group group = new Group(camIdToName(objectID));
          list  << group
        }
        else if (isFolder(objectID)) {
          list << folders.get(objectID)
        }
        break;
      case SearchAxis.Child:
        if (objectID == null) {
          for (NamespaceFolder folder: folders.values()) {
            if (folder.getAncestors().length <= 0) {
              list << folder
            }
          }
        }
        else if (isFolder(objectID) && folders.containsKey(objectID)) {
          NamespaceFolder folder = folders.get(objectID);
          for (IUiClass child: folder.getChildren()) {
            list << child
          }
        }
        else if (isRole(objectID)) {
          Role role = new Role(camIdToName(objectID));
          for (IBaseClass member: role.getMembers()) {
            list << member
          }
        }
        break;
      case SearchAxis.Descendent:
        def schemaBaseClasses = parseSearchFilter(filter)
        schemaBaseClasses.each { schemaBaseClass ->
          if (schemaBaseClass instanceof SuPerson)
            list << new Account(schemaBaseClass)
          else if(schemaBaseClass instanceof GroupOfUniqueNames)
            list << new Group(schemaBaseClass)
        }
        break;
      default:
        break;
    }
    list
  }

  def parseSearchFilter(ISearchFilter iSearchFilter) {

    def value = "", attribute = "", not = ""

    switch (iSearchFilter.searchFilterType) {
      case ISearchFilter.ConditionalExpression:
        ISearchFilterConditionalExpression item = (ISearchFilterConditionalExpression) iSearchFilter

        def list = []
        def queries = []
        def closures = []
        item.filters.each { subFilter ->
          def ret = parseSearchFilter(subFilter)
          if (ret != null) {
            if(ret instanceof GString)
              queries << ret
            else if (ret instanceof Collection) {
              ret.each { itm ->
                if (itm instanceof Closure)
                  closures << itm
                else
                  list << itm
              }
            }
            else if (ret instanceof Closure)
              closures << ret
          }
        }

        if(item.operator == "or") {
          return closures
        }
        else {
          closures.each { closure ->
            queries.each { query ->
              def entries = closure(query)
              if (entries != null)
                list.addAll(entries)
            }
          }
        }
        return list
        break;

      case ISearchFilter.FunctionCall:
        ISearchFilterFunctionCall item = (ISearchFilterFunctionCall) iSearchFilter
        attribute = item.parameters.first()
        switch (item.functionName) {
          case ISearchFilterFunctionCall.Contains:
            value = "*${item.parameters[1]}*"
            break;
          case ISearchFilterFunctionCall.StartsWith:
            value = "${item.parameters[1]}*"
            break;
          case ISearchFilterFunctionCall.EndsWith:
            value = "*${item.parameters[1]}"
            break;
        }
        break;

      case ISearchFilter.RelationalExpression:
        ISearchFilterRelationExpression item = (ISearchFilterRelationExpression) iSearchFilter
        attribute = item.propertyName
        value = item.constraint

        if (item.operator == ISearchFilterRelationExpression.NotEqual)
          not = "!"
        break;
    }

    switch (attribute) {
      case "@objectClass":
        return { query ->
          if(value != null && value == "account")
            SuPerson.findAll(filter: query)
          //else if(value != null && value == "group")
          //  GroupOfUniqueNames.findAll(filter: query)
        }
        break;
      case "@defaultName":
      case "@userName":
      case "@name":
      default:
        attribute = "uid"
        break;
    }

    "(${not}${attribute}=${value})"
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

  public static byte[] toBytes(Object object){
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    try{
      java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
      oos.writeObject(object);
      oos.flush();
      oos.close();
      baos.flush();
      baos.close();
    }catch(java.io.IOException ioe){
    }
    return baos.toByteArray();
  }


  public static Object toObject(byte[] bytes){
    Object object = null;
    try{
      java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
      java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
      object = ois.readObject();
      ois.close();
      bais.close();
    }catch(java.io.IOException ioe){
    }catch(java.lang.ClassNotFoundException cnfe){
    }
    return object;
  }

}