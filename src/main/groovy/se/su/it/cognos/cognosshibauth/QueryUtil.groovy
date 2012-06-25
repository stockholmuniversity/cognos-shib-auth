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
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import com.cognos.CAM_AAA.authentication.ISearchFilterConditionalExpression
import com.cognos.CAM_AAA.authentication.ISearchFilterFunctionCall
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import se.su.it.cognos.cognosshibauth.memcached.Cache

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

  def List<IBaseClass> searchAxisChild(String baseObjectID, IQueryOption queryOption) {
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

    getPageOfResult(retList, queryOption.skipCount, queryOption.maxCount)
  }

  def List<IBaseClass> searchAxisDescendent(String baseObjectID, ISearchFilter filter, IQueryOption queryOption) {
    List<IBaseClass> retList = new ArrayList<IBaseClass>()

    List<String> objectTypes = findFilterObjectTypes(filter)

    objectTypes.each { objectType ->

      def ret = parseSearchFilter(filter, objectType)

      if (ret == Collection)
        retList = ret

      if (ret instanceof String) {
        def countLimit = configHandler.getIntEntry("ldap.count_limit", 500)

        if (objectType == 'account') {
          def cacheRet = Cache.instance.get("LDAP_${ret}") {
            SuPerson.findAll(filter : ret as String, pageSize: countLimit)?.collect {
              new Account(it)
            }
          }

          if (cacheRet instanceof Collection)
            cacheRet.each { retList << it }
        }
        else if (objectType == 'group') {
          def cacheRet = Cache.instance.get("LDAP_${ret}") {
            GroupOfUniqueNames.findAll(filter: ret as String, pageSize: countLimit)?.collect {
              new Group(it)
            }
          }

          if (cacheRet instanceof Collection)
            cacheRet.each { retList << it}
        }
      }
    }

    getPageOfResult(retList, queryOption.skipCount, queryOption.maxCount)
  }

  List getPageOfResult(List searchResult, long skipCount = 0, long maxCount = 0) {
    long start = skipCount ?: 0
    long stop = start + maxCount -1 ?:0

    if (stop > searchResult.size() -1)
      stop = searchResult.size() -1

    if (searchResult)
      searchResult[start..stop]
    else
      searchResult
  }

/* This code is kept to start building real paging from.

  HashMap pagedSearch(Class schema, HashMap options, Integer pageSize, Integer pageNumber) {
    SearchOptionParser parser = new SearchOptionParser(schema, options)
    GldapoDirectory directory = parser.directory as GldapoDirectory

    ContextMapper mapper = new GldapoContextMapper(schemaRegistration: schema.schemaRegistration, directory: directory)
    ContextMapperCallbackHandler handler = new ContextMapperCallbackHandler(mapper)

    javax.naming.directory.SearchControls jndiControls = parser.controls as javax.naming.directory.SearchControls
    jndiControls.returningAttributes = schema.schemaRegistration.attributeMappings*.value.attributeName


    try {
      PagedResultsRequestControl requestControl = new PagedResultsRequestControl(pageSize)

      directory.template.search(parser.base, parser.filter, jndiControls, handler, requestControl)
    } catch (LimitExceededException e) {
        // If the number of entries has hit the specified count limit OR
        // The server is unwilling to send more entries we will get here.
        // It's not really an error condition hence we just return what we found.
    }

   [requestControl: requestControl, handler: handler]
  }
*/

  def parseSearchFilter(ISearchFilter iSearchFilter, String objectType) {

    List<IBaseClass> ret = new ArrayList<IBaseClass>()

    String filter = null

    if (iSearchFilter instanceof ISearchFilterConditionalExpression) {
      def filterResults = iSearchFilter.filters.collect { parseSearchFilter(it, objectType) }

      if (filterResults.findAll {it instanceof String}.size() == filterResults.size()) { // All strings (probably ldap filters)
        filter = filterResults.collect { filterPart ->
          if (filterPart) {
            (filterPart as String).startsWith('(') ? "(${filterPart})" : filterPart
          }
          else
            ""
        }.join('')

        if (filter && filterResults.findAll {it}.size() > 1) { //Apply conditional expression if more than one filter part.
          if (iSearchFilter.operator == ISearchFilterConditionalExpression.ConditionalOr)
            filter = "(|${filter})"
          else
            filter = "(&${filter})"
        }
      }
      else {
        filterResults.each { filterResult ->
          if (filterResult instanceof Collection)
            ret.addAll filterResult
        }
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
          Role.findAllByName(value)?.each {
            ret << it
          }
        }
        if (objectType == 'folder') {
          switch (attribute) {
            case '@userName':
            case '@defaultName':
              folders?.values()?.findAll { folder ->
                folder.getName(configHandler.contentLocale) == value
              }?.each {
                ret << it
              }
              break
            case '@defaultDescription':
              folders?.values()?.findAll { folder ->
                folder.getDescription(configHandler.contentLocale) == value
              }?.each {
                ret << it
              }
              break
          }
        }
        if (objectType == 'namespace') {
          switch (attribute) {
            case '@userName':
            case '@defaultName':
              if (value == namespace?.getName(configHandler.contentLocale))
                ret << namespace
              break
            case '@defaultDescription':
              if (value == namespace?.getDescription(configHandler.contentLocale))
                ret << namespace
              break
          }
        }
      }
    }

    if (filter != null)
      return filter

    ret
  }
}
