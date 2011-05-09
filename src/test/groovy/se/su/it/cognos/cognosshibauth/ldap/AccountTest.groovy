package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import static org.junit.Assert.assertTrue

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 11:45
 */
class AccountTest {

  @Test
  def void test1() {
    Account account = new Account(null, "uid=jolu,dc=it,dc=su,dc=se")
    assertTrue(true)
  }

}
