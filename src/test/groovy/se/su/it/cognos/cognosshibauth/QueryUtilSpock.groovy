package se.su.it.cognos.cognosshibauth

import spock.lang.Specification
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression
import spock.lang.Unroll
import com.cognos.CAM_AAA.authentication.ISearchFilterFunctionCall
import com.cognos.CAM_AAA.authentication.INamespace
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder
import spock.lang.Shared
import com.cognos.CAM_AAA.authentication.INamespaceFolder
import se.su.it.cognos.cognosshibauth.ldap.Role
import com.cognos.CAM_AAA.authentication.IRole
import gldapo.GldapoSchemaRegistry
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson

@RunWith(Sputnik)
class QueryUtilSpock extends Specification {

  @Shared
  INamespace namespace = new CognosShibAuthNamespace()

  @Shared
  INamespaceFolder namespaceFolder1 = new NamespaceFolder(namespace.getObjectID(), 'foo')
  INamespaceFolder namespaceFolder2 = new NamespaceFolder(namespace.getObjectID(), 'bar')

  def setup() {
    //Disable Gldapo
    GldapoSchemaRegistry.metaClass.add = { Object registration -> return }

    def suPerson1 = new SuPerson(eduPersonEntitlement: ['urn:mace:swami.se:gmai:su-ivs:test-role'])
    def suPerson2 = new SuPerson(eduPersonEntitlement: ['urn:mace:swami.se:gmai:su-ivs:test-role2'])

    def suPersons = [suPerson1, suPerson2]
    SuPerson.metaClass.static.findAll = { Map options -> suPersons }
  }

  def cleanup() {
    SuPerson.metaClass = null
  }

  @Unroll
  def "Test that we can parse relationalExpressions for #objectType ( #attribute#operator#value )"() {
    setup:
    QueryUtil queryUtil = new QueryUtil(namespace, ['foo': namespaceFolder1, 'bar': namespaceFolder2])

    ISearchFilterRelationExpression relationExpression = Mock()
    relationExpression.constraint >> value
    relationExpression.operator >> operator
    relationExpression.propertyName >> attribute

    when:
    def result = queryUtil.parseSearchFilter(relationExpression, objectType)

    then:
    result == expected

    where:
    objectType  | attribute             | operator                                           | value          | expected
    'account'   | null                  | null                                               | null           | ""

    'account'   | '@objectClass'        | ISearchFilterRelationExpression.EqualTo            | 'foo'          | ""
    'account'   | '@objectClass'        | ISearchFilterRelationExpression.NotEqual           | 'foo'          | ""
    'account'   | '@objectClass'        | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | ""
    'account'   | '@objectClass'        | ISearchFilterRelationExpression.LessThan           | 'foo'          | ""
    'account'   | '@objectClass'        | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | ""
    'account'   | '@objectClass'        | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | ""

    'account'   | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'foo'          | "(uid=foo)"
    'account'   | '@defaultName'        | ISearchFilterRelationExpression.NotEqual           | 'foo'          | "(!uid=foo)"
    'account'   | '@defaultName'        | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | "(&(uid>=foo)(!uid=foo))"
    'account'   | '@defaultName'        | ISearchFilterRelationExpression.LessThan           | 'foo'          | "(&(uid<=foo)(!uid=foo))"
    'account'   | '@defaultName'        | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | "(uid>=foo)"
    'account'   | '@defaultName'        | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | "(uid<=foo)"

    'account'   | '@userName'           | ISearchFilterRelationExpression.EqualTo            | 'foo'          | "(uid=foo)"
    'account'   | '@userName'           | ISearchFilterRelationExpression.NotEqual           | 'foo'          | "(!uid=foo)"
    'account'   | '@userName'           | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | "(&(uid>=foo)(!uid=foo))"
    'account'   | '@userName'           | ISearchFilterRelationExpression.LessThan           | 'foo'          | "(&(uid<=foo)(!uid=foo))"
    'account'   | '@userName'           | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | "(uid>=foo)"
    'account'   | '@userName'           | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | "(uid<=foo)"

    'account'   | '@defaultDescription' | ISearchFilterRelationExpression.EqualTo            | 'foo'          | "(|(cn=foo)(mailLocalAddress=foo))"
    'account'   | '@defaultDescription' | ISearchFilterRelationExpression.NotEqual           | 'foo'          | "(|(!cn=foo)(!mailLocalAddress=foo))"
    'account'   | '@defaultDescription' | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | "(|(&(cn>=foo)(!cn=foo))(&(mailLocalAddress>=foo)(!mailLocalAddress=foo)))"
    'account'   | '@defaultDescription' | ISearchFilterRelationExpression.LessThan           | 'foo'          | "(|(&(cn<=foo)(!cn=foo))(&(mailLocalAddress<=foo)(!mailLocalAddress=foo)))"
    'account'   | '@defaultDescription' | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | "(|(cn>=foo)(mailLocalAddress>=foo))"
    'account'   | '@defaultDescription' | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | "(|(cn<=foo)(mailLocalAddress<=foo))"

    'group'     | null                  | null                                               | null           | ""

    'group'     | '@objectClass'        | ISearchFilterRelationExpression.EqualTo            | 'foo'          | ""
    'group'     | '@objectClass'        | ISearchFilterRelationExpression.NotEqual           | 'foo'          | ""
    'group'     | '@objectClass'        | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | ""
    'group'     | '@objectClass'        | ISearchFilterRelationExpression.LessThan           | 'foo'          | ""
    'group'     | '@objectClass'        | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | ""
    'group'     | '@objectClass'        | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | ""

    'group'     | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'foo'          | "(cn=foo)"
    'group'     | '@defaultName'        | ISearchFilterRelationExpression.NotEqual           | 'foo'          | "(!cn=foo)"
    'group'     | '@defaultName'        | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | "(&(cn>=foo)(!cn=foo))"
    'group'     | '@defaultName'        | ISearchFilterRelationExpression.LessThan           | 'foo'          | "(&(cn<=foo)(!cn=foo))"
    'group'     | '@defaultName'        | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | "(cn>=foo)"
    'group'     | '@defaultName'        | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | "(cn<=foo)"

    'group'     | '@userName'           | ISearchFilterRelationExpression.EqualTo            | 'foo'          | "(cn=foo)"
    'group'     | '@userName'           | ISearchFilterRelationExpression.NotEqual           | 'foo'          | "(!cn=foo)"
    'group'     | '@userName'           | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | "(&(cn>=foo)(!cn=foo))"
    'group'     | '@userName'           | ISearchFilterRelationExpression.LessThan           | 'foo'          | "(&(cn<=foo)(!cn=foo))"
    'group'     | '@userName'           | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | "(cn>=foo)"
    'group'     | '@userName'           | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | "(cn<=foo)"

    'group'     | '@defaultDescription' | ISearchFilterRelationExpression.EqualTo            | 'foo'          | "(description=foo)"
    'group'     | '@defaultDescription' | ISearchFilterRelationExpression.NotEqual           | 'foo'          | "(!description=foo)"
    'group'     | '@defaultDescription' | ISearchFilterRelationExpression.GreaterThan        | 'foo'          | "(&(description>=foo)(!description=foo))"
    'group'     | '@defaultDescription' | ISearchFilterRelationExpression.LessThan           | 'foo'          | "(&(description<=foo)(!description=foo))"
    'group'     | '@defaultDescription' | ISearchFilterRelationExpression.GreaterThanOrEqual | 'foo'          | "(description>=foo)"
    'group'     | '@defaultDescription' | ISearchFilterRelationExpression.LessThanOrEqual    | 'foo'          | "(description<=foo)"

    'role'      | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'no-test-role' | []
    'role'      | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'test-role'    | [new Role('test-role')]

    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'foo'          | []
    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'Swedish Name' | [namespace]
/*  TODO: Fix this! parseSearchFilter should be aware of operator for namespace.
    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.NotEqual           | 'Swedish Name' | []
    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.GreaterThan        | 'Swedish Name' | []
    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.LessThan           | 'Swedish Name' | []
    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.GreaterThanOrEqual | 'Swedish Name' | []
    'namespace' | '@defaultName'        | ISearchFilterRelationExpression.LessThanOrEqual    | 'Swedish Name' | []
    */

    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'noFolder'     | []
    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.EqualTo            | 'foo'          | [namespaceFolder1]
/*  TODO: Fix this! parseSearchFilter should be aware of operator for namespace.
    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.NotEqual           | 'Swedish Name' | []
    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.GreaterThan        | 'Swedish Name' | []
    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.LessThan           | 'Swedish Name' | []
    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.GreaterThanOrEqual | 'Swedish Name' | []
    'folder'    | '@defaultName'        | ISearchFilterRelationExpression.LessThanOrEqual    | 'Swedish Name' | []
    */
  }

  @Unroll
  def "Test that we can parse functionCall for #objectType ( #attribute #functionName #value )"() {
    setup:
    QueryUtil queryUtil = new QueryUtil(null, null)

    ISearchFilterFunctionCall functionCall = Mock()
    functionCall.functionName >> functionName
    functionCall.parameters >> [attribute, value]

    when:
    def result = queryUtil.parseSearchFilter(functionCall, objectType)

    then:
    result == expected

    where:
    objectType | attribute             | functionName                         | value | expected
    'account'  | null                  | null                                 | null  | ""
    'account'  | '@defaultName'        | ISearchFilterFunctionCall.Contains   | 'foo' | "(uid=*foo*)"
    'account'  | '@defaultName'        | ISearchFilterFunctionCall.StartsWith | 'foo' | "(uid=foo*)"
    'account'  | '@defaultName'        | ISearchFilterFunctionCall.EndsWith   | 'foo' | "(uid=*foo)"
    'account'  | '@userName'           | ISearchFilterFunctionCall.Contains   | 'foo' | "(uid=*foo*)"
    'account'  | '@userName'           | ISearchFilterFunctionCall.StartsWith | 'foo' | "(uid=foo*)"
    'account'  | '@userName'           | ISearchFilterFunctionCall.EndsWith   | 'foo' | "(uid=*foo)"
    'account'  | '@defaultDescription' | ISearchFilterFunctionCall.Contains   | 'foo' | "(|(cn=*foo*)(mailLocalAddress=*foo*))"
    'account'  | '@defaultDescription' | ISearchFilterFunctionCall.StartsWith | 'foo' | "(|(cn=foo*)(mailLocalAddress=foo*))"
    'account'  | '@defaultDescription' | ISearchFilterFunctionCall.EndsWith   | 'foo' | "(|(cn=*foo)(mailLocalAddress=*foo))"

    'group'    | null                  | null                                 | null  | ""
    'group'    | '@defaultName'        | ISearchFilterFunctionCall.Contains   | 'foo' | "(cn=*foo*)"
    'group'    | '@defaultName'        | ISearchFilterFunctionCall.StartsWith | 'foo' | "(cn=foo*)"
    'group'    | '@defaultName'        | ISearchFilterFunctionCall.EndsWith   | 'foo' | "(cn=*foo)"
    'group'    | '@userName'           | ISearchFilterFunctionCall.Contains   | 'foo' | "(cn=*foo*)"
    'group'    | '@userName'           | ISearchFilterFunctionCall.StartsWith | 'foo' | "(cn=foo*)"
    'group'    | '@userName'           | ISearchFilterFunctionCall.EndsWith   | 'foo' | "(cn=*foo)"
    'group'    | '@defaultDescription' | ISearchFilterFunctionCall.Contains   | 'foo' | "(description=*foo*)"
    'group'    | '@defaultDescription' | ISearchFilterFunctionCall.StartsWith | 'foo' | "(description=foo*)"
    'group'    | '@defaultDescription' | ISearchFilterFunctionCall.EndsWith   | 'foo' | "(description=*foo)"

    'role'     | null                  | null                                 | null           | []
    'role'     | '@defaultName'        | ISearchFilterFunctionCall.Contains   | 'foo' | []
    'role'     | '@defaultName'        | ISearchFilterFunctionCall.StartsWith | 'no-test-role' | []
    'role'     | '@defaultName'        | ISearchFilterFunctionCall.EndsWith   | 'no-test-role' | []
/*
    'role'     | '@defaultName'        | ISearchFilterFunctionCall.Contains   | 'test-role'    | [new Role('test-role')]
    'role'     | '@defaultName'        | ISearchFilterFunctionCall.StartsWith | 'test-role'    | [new Role('test-role')]
    'role'     | '@defaultName'        | ISearchFilterFunctionCall.EndsWith   | 'test-role'    | [new Role('test-role')]
    */
  }

  @Unroll
  def "Test that the paging returns #expected results for skip=#skip and max=#max for a list of size #list.size()"() {
    setup:
    QueryUtil queryUtil = new QueryUtil(null, null)

    when:
    List result = queryUtil.getPageOfResult(list, skip, max)

    then:
    result.size() == expected

    where:
    list                 | skip | max | expected
    []                   | 0    | 0   | 0
    ['a']                | 0    | 0   | 1
    ['a', 'b']           | 0    | 0   | 2
    ['a', 'b']           | 0    | 1   | 1
    ['a', 'b']           | 0    | 3   | 2
    ['a', 'b', 'c', 'd'] | 1    | 3   | 3
    ['a', 'b', 'c', 'd'] | 1    | 2   | 2
    ['a', 'b', 'c', 'd'] | 1    | 1   | 1
    ['a', 'b', 'c', 'd'] | 1    | 10  | 3
    ['a', 'b', 'c', 'd'] | 0    | 10  | 4
  }
}
