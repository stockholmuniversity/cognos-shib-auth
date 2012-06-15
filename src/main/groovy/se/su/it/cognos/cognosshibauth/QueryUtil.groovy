package se.su.it.cognos.cognosshibauth

import com.cognos.CAM_AAA.authentication.IBaseClass

import static se.su.it.cognos.cognosshibauth.ldap.UiClass.camIdToName
import static se.su.it.cognos.cognosshibauth.ldap.UiClass.isUser
import se.su.it.cognos.cognosshibauth.ldap.Account

import static se.su.it.cognos.cognosshibauth.ldap.UiClass.isGroup
import se.su.it.cognos.cognosshibauth.ldap.Group

import static se.su.it.cognos.cognosshibauth.ldap.UiClass.isRole
import se.su.it.cognos.cognosshibauth.ldap.Role

import static se.su.it.cognos.cognosshibauth.ldap.UiClass.isFolder
import com.cognos.CAM_AAA.authentication.INamespace
import com.cognos.CAM_AAA.authentication.INamespaceFolder
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder
import com.cognos.CAM_AAA.authentication.ISearchFilter
import com.cognos.CAM_AAA.authentication.IQueryOption

import static se.su.it.cognos.cognosshibauth.query.FilterUtil.findFilterObjectTypes
import gldapo.search.SearchControlProvider
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import com.cognos.CAM_AAA.authentication.ISearchFilterConditionalExpression
import com.cognos.CAM_AAA.authentication.ISearchFilterFunctionCall
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;


class QueryUtil {

  private ConfigHandler configHandler = null
  private INamespace namespace = null
  private Map<String, INamespaceFolder> folders = null

  def QueryUtil(INamespace namespace, Map<String, INamespaceFolder> folders) {
    this.namespace = namespace
    this.folders = folders

    this.configHandler = ConfigHandler.instance()
  }

  def IBaseClass searchAxisSelf(String baseObjectID) {
    IBaseClass ret = null

    String name = camIdToName(baseObjectID)

    if (baseObjectID == null)
      ret = namespace

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

    if (iSearchFilter instanceof ISearchFilterConditionalExpression) {
      def filterResults = iSearchFilter.filters.collect { parseSearchFilter(it, objectType) }

      if (filterResults.findAll {it instanceof String}.size() == filterResults.size()) {
        filter = filterResults.collect { (it as String).empty ? "" : "(${it})" }.join('')

        if (iSearchFilter.operator == ISearchFilterConditionalExpression.ConditionalOr)
          filter = "(|${filter})"
        else
          filter = "(&${filter})"
      }
    }
    else {
      String attribute = null, value = null, operator = null

      if (iSearchFilter instanceof ISearchFilterFunctionCall) {

        attribute = iSearchFilter.parameters.first()

        switch (iSearchFilter.functionName) {
          case ISearchFilterFunctionCall.Contains:
            value = "*${iSearchFilter.parameters[1]}*"
            break
          case ISearchFilterFunctionCall.StartsWith:
            value = "${iSearchFilter.parameters[1]}*"
            break
          case ISearchFilterFunctionCall.EndsWith:
            value = "*${iSearchFilter.parameters[1]}"
            break
        }
      }
      else if (iSearchFilter instanceof ISearchFilterRelationExpression) {
        attribute = iSearchFilter.propertyName
        value = iSearchFilter.constraint
        operator = iSearchFilter.operator
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
          if (value == namespace.getName(configHandler.contentLocale))
            ret << this
        }
      }
    }

    if (filter)
      return filter

    ret
  }
}
