package se.su.it.cognos.cognosshibauth

import spock.lang.Specification
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import com.cognos.CAM_AAA.authentication.IQuery
import com.cognos.CAM_AAA.authentication.ISearchExpression
import com.cognos.CAM_AAA.authentication.IQueryOption
import com.cognos.CAM_AAA.authentication.ISearchStep
import spock.lang.Shared
import se.su.it.cognos.cognosshibauth.ldap.Role
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder
import se.su.it.cognos.cognosshibauth.ldap.Group
import se.su.it.cognos.cognosshibauth.ldap.Account
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import spock.lang.Unroll
import gldapo.GldapoSchemaRegistry
import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchFilterFunctionCall
import com.cognos.CAM_AAA.authentication.ISearchFilter

@RunWith(Sputnik)
class SearchSpock extends Specification {

  @Shared
  CognosShibAuthBase target = new CognosShibAuthBase()

  @Shared
  def mockRole = null

  @Shared
  GroupOfUniqueNames groupOfUniqueNames = null

  @Shared
  def suPerson = null

  @Shared
  def mockGroup = null

  @Shared
  def mockAccount = null

  @Shared
  NamespaceFolder mockFolder = null

  @Shared
  NamespaceFolder mockSubFolder = null

  def setup() {

    //Disable Gldapo
    GldapoSchemaRegistry.metaClass.add = { Object registration -> return }

    //Disable cache
    Cache.instance.disable()
    Cache.metaClass.get = { key, function -> function() }

    mockFolder = new NamespaceFolder(null, "test-folder")
    mockSubFolder = new NamespaceFolder(null, "test-folder2")

/*
      suPerson = Mock(SuPerson)
      suPerson.uid >> "jolu"
      suPerson.givenName >> "Joakim"
      suPerson.sn >> "Lundin"
      suPerson.eduPersonEntitlement >> ['urn:mace:swami.se:gmai:su-ivs:test-role']

      suPerson.getDn() >> "uid=jolu,dc=it,dc=su,dc=se"
*/
    suPerson = new SuPerson(
            uid: "jolu",
            givenName: 'joakim',
            sn: 'Lundin',
            eduPersonEntitlement: ['urn:mace:swami.se:gmai:su-ivs:test-role']
    )

    suPerson.metaClass.getDn = { "uid=jolu,dc=it,dc=su,dc=se" }

    def suPersons = [suPerson]

    SuPerson.metaClass.static.getByDn = { String dn ->
      suPersons.find { it.getDn() == dn }
    }

    SuPerson.metaClass.static.findAll = { Map options ->
      def ret = []

      def matcher = (options.filter =~ /.*\((.*)=(.*)\).*/)
          if (matcher.matches()) {
            String attr = matcher[0][1]
            String val = matcher[0][2]
            ret = suPersons.findAll { it[attr] == val.replaceAll(/\*/, "") }
          }

      ret
    }

    groupOfUniqueNames = new GroupOfUniqueNames(
            cn: "it-utveckling-employees",
            description: "Anställda på Team Utveckling",
            uniqueMember: ["uid=jolu,dc=it,dc=su,dc=se", "uid=hdrys,dc=it,dc=su,dc=se"]
    )
    groupOfUniqueNames.metaClass.getDn = { "cn=it-utveckling-employees,dc=it,dc=su,dc=se" }

    def groupsOfUniqueNames = [groupOfUniqueNames]

    GroupOfUniqueNames.metaClass.static.getByDn = { String dn ->
      groupsOfUniqueNames.find { it.getDn() == dn }
    }

    GroupOfUniqueNames.metaClass.static.findAll = { Map options -> [] }

    target.folders.put(mockFolder.objectID, mockFolder)

    mockRole = new Role("test-role")
    mockAccount = new Account(suPerson)
    mockGroup = new Group(groupOfUniqueNames)
    mockFolder.addChild(mockSubFolder)

    Role.metaClass.static.exists = { String roleName ->
      roleName.startsWith("test")
    }

    Role.metaClass.getMembers = {
      String roleName = delegate.getName()

      suPersons.findAll {
        it.eduPersonEntitlement.contains("urn:mace:swami.se:gmai:su-ivs:${roleName}" as String)
      }.collect {new Account(it)}
    }

    Group.metaClass.getMembers = {
      Set<String> uniqueMembers = delegate.groupOfUniqueNames.uniqueMember

      suPersons.findAll {
        uniqueMembers.contains(it.getDn())
      }.collect {new Account(it)}
    }
  }

  def cleanup() {
    SuPerson.metaClass = null
    Group.metaClass = null
  }

  def "Test for basic error handling when searching with null"() {
    when:
    def result = target.search(null, null)

    then:
    notThrown(NullPointerException)

    and:
    result.objects == null || result.objects.length == 0
  }

  @Unroll
  def "Search with axis Self for object: #objectId"() {
    setup:
    ISearchStep searchStep = Mock()
    searchStep.getAxis() >> ISearchStep.SearchAxis.Self
    searchStep.getPredicate() >> null

    ISearchExpression searchExpression = Mock()
    searchExpression.getObjectID() >> objectId
    searchExpression.getSteps() >> [searchStep]

    IQueryOption queryOption = Mock()
    queryOption.maxCount >> 100
    queryOption.skipCount >> 0

    IQuery query = Mock()
    query.getSearchExpression() >> searchExpression
    query.getQueryOption() >> ( queryOption )

    when:
    def result = target.search(null, query)

    then:
    notThrown(NullPointerException)

    and:
    result.objects?.first() == expected

    where:
    objectId                                         | expected
    null                                             | target
    "null:r:test-role"                               | mockRole
    "null:g:${groupOfUniqueNames.getDn()}" as String | mockGroup
    "null:u:${suPerson.getDn()}" as String           | mockAccount
    mockFolder.objectID                              | mockFolder
    "null:r:missing-role"                            | null
    "null:u:missing-account,dc=it,dc=su,dc=se"       | null
    "null:g:missing-group,dc=it,dc=su,dc=se"         | null
    "null:f:missing-folder"                          | null
  }

  @Unroll
  def "Search with axis Child for object: #objectId"() {
    setup:
    ISearchStep searchStep = Mock()
    searchStep.getAxis() >> ISearchStep.SearchAxis.Child
    searchStep.getPredicate() >> null

    ISearchExpression searchExpression = Mock()
    searchExpression.getObjectID() >> objectId
    searchExpression.getSteps() >> [searchStep]

    IQueryOption queryOption = Mock()
    queryOption.maxCount >> 100
    queryOption.skipCount >> 0

    IQuery query = Mock()
    query.getSearchExpression() >> searchExpression
    query.getQueryOption() >> ( queryOption )

    when:
    def result = target.search(null, query)

    then:
    notThrown(NullPointerException)

    and:
    (result.objects?.toList() ?: []).containsAll(expected)

    where:
    objectId                                         | expected
    null                                             | [mockFolder]
    "null:r:test-role"                               | [mockAccount]
    "null:g:${groupOfUniqueNames.getDn()}" as String | [mockAccount]
    "null:u:${suPerson.getDn()}" as String           | []
    mockFolder.objectID                              | [mockSubFolder]
    "null:r:missing-role"                            | []
    "null:u:missing-account,dc=it,dc=su,dc=se"       | []
    "null:g:missing-group,dc=it,dc=su,dc=se"         | []
    "null:f:missing-folder"                          | []
  }

  @Unroll
  def "Search with axis Descendent and ISearchFilterFunctionCall( #attribute, #value )"() {
    setup:
    ISearchFilterFunctionCall searchFilterFunctionCall = Mock()
    searchFilterFunctionCall.getFunctionName() >> ISearchFilterFunctionCall.Contains
    searchFilterFunctionCall.getParameters() >> ( [attribute, value] as String[] )
    searchFilterFunctionCall.getSearchFilterType() >> ISearchFilter.FunctionCall

    ISearchStep searchStep = Mock()
    searchStep.getAxis() >> ISearchStep.SearchAxis.Descendent
    searchStep.getPredicate() >> searchFilterFunctionCall

    ISearchExpression searchExpression = Mock()
    searchExpression.getObjectID() >> objectId
    searchExpression.getSteps() >> [searchStep]

    IQueryOption queryOption = Mock()
    queryOption.maxCount >> 100
    queryOption.skipCount >> 0

    IQuery query = Mock()
    query.getSearchExpression() >> searchExpression
    query.getQueryOption() >> ( queryOption )

    when:
    def result = target.search(null, query)

    then:
    notThrown(NullPointerException)

    and:
    (result.objects?.toList() ?: []).containsAll(expected)

    where:
    objectId | expected        | attribute   | value
    null     | []              | null        | null
    null     | [mockAccount]   | '@userName' | 'jolu'
    null     | []              | '@userName' | null
    null     | []              | '@userName' | 'nonExisting'
  }
}