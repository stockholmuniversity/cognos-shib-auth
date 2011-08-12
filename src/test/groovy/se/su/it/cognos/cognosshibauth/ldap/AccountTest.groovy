package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import org.junit.Before
import groovy.mock.interceptor.MockFor
import se.su.it.cognos.cognosshibauth.memcached.Cache

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 11:45
 */
class AccountTest {

  @Before
  def void before() {
    Cache.getInstance().disable()

    SuPerson suPersonMock = new SuPerson()
    suPersonMock.metaClass.getDn = { "uid=test1,dc=it,dc=su,dc=se" }
    suPersonMock.metaClass.getUid = { "test1" }

    SuPerson.metaClass.static.getByDn << { dn -> suPersonMock }
    SuPerson.metaClass.static.findByUid << { uid -> suPersonMock }
  }

  @Test
  def void testCreateFromDn() {
    Account account = Account.createFromDn("uid=test1,dc=it,dc=su,dc=se")
    assertEquals("test1", account.getUserName())
  }
}
