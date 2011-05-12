package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse

/**
 * User: joakim
 * Date: 2011-05-11
 * Time: 14:50
 */
class UiClassTest {

  @Test
  void testIsUserPassesUser() {
    def CAMID = "FooBar:u:uid=jolu,dc=it,dc=su,dc=se"
    assertTrue(UiClass.isUser(CAMID))
  }

  @Test
  void testIsUserFalseForNonUser() {
    def CAMID = "FooBar:f:uid=jolu,dc=it,dc=su,dc=se"
    assertFalse(UiClass.isUser(CAMID))
  }

  @Test
  void testIsUserFalseForNull() {
    def CAMID = null
    assertFalse(UiClass.isUser(CAMID))
  }
}
