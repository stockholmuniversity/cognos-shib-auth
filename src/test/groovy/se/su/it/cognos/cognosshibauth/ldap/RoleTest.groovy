package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import static junit.framework.Assert.assertEquals
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson

/**
 * User: joakim
 * Date: 2011-05-10
 * Time: 09:51
 */
class RoleTest {

  @Test
  void testParseRoleFromEntitlementUri() {
    def uri = "urn:mace:swami.se:gmai:su-ivs:role1"

    assertEquals("role1", Role.parseRoleFromEntitlementUri(uri))
  }
}
