package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 11:45
 */
class AccountTest {

  @Test
  def void test1() {
    Account account = Account.createFromDn("uid=jolu,dc=it,dc=su,dc=se")
    assertEquals("jolu", account.getUserName())
  }

  @Test
  def void test2() {
    Account account = Account.findByUid("jolu")
    assertEquals("jolu", account.getUserName())
  }

  @Test
  def void test3() {
    SuPerson sp = SuPerson.find(filter:"(uid=jolu)")
    assertEquals("jolu", sp.uid)
  }

}
