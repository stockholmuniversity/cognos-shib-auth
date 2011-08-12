package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import org.junit.Before
import se.su.it.cognos.cognosshibauth.memcached.Cache

class GroupTest {

  @Before
  void before() {
    Cache.getInstance().disable()
  }

  @Test
  void testFindByMember() {
    SuPerson mockSuPerson = new SuPerson()
    mockSuPerson.metaClass.getDn = { "uid=test1,dc=it,dc=su,dc=se" }

    GroupOfUniqueNames mockGroupOfUniqueNames = new GroupOfUniqueNames()
    mockGroupOfUniqueNames.metaClass.getDn = { "cn=group1,dc=it,dc=su,dc=se" }

    Account.metaClass.static.createTestAccount << { new Account(mockSuPerson) }

    GroupOfUniqueNames.metaClass.'static'.findAllByUniqueMember = { String memberDn ->
      [mockGroupOfUniqueNames]
    }

    def account = Account.createTestAccount()
    def groups = Group.findByMember(account)

    assert groups.contains(new Group(mockGroupOfUniqueNames))
  }
}
