package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import org.junit.Before
import se.su.it.cognos.cognosshibauth.memcached.Cache
import se.su.it.cognos.cognosshibauth.TestBaseClass

class GroupTest extends TestBaseClass {

  @Before
  void before() {
    Cache.getInstance().disable()
  }

  @Test
  void testFindByMember() {
    GroupOfUniqueNames mockGroupOfUniqueNames = new GroupOfUniqueNames()
    mockGroupOfUniqueNames.metaClass.getDn = { "cn=group1,dc=it,dc=su,dc=se" }

    GroupOfUniqueNames.metaClass.'static'.findAllByUniqueMember = { String memberDn ->
      [mockGroupOfUniqueNames]
    }

    def account = Account.createTestAccount()
    def groups = Group.findByMember(account)

    assert groups.contains(new Group(mockGroupOfUniqueNames))
  }

  @Test
  void testCreateGroup() {
    GroupOfUniqueNames mockGroupOfUniqueNames = new GroupOfUniqueNames()
    mockGroupOfUniqueNames.metaClass.getDn = { "cn=group1,dc=it,dc=su,dc=se" }

    assert new Group(mockGroupOfUniqueNames).objectID == "TestId:g:cn=group1,dc=it,dc=su,dc=se"
  }

  @Test(expected = NullPointerException.class)
  void testCreateGroupWithNull() {
    new Group(null as GroupOfUniqueNames)
  }
}
