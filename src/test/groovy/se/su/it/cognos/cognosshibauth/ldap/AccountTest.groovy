package se.su.it.cognos.cognosshibauth.ldap

import org.junit.Test
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import org.junit.Before
import groovy.mock.interceptor.MockFor
import se.su.it.cognos.cognosshibauth.memcached.Cache
import se.su.it.cognos.cognosshibauth.TestBaseClass
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner
import org.mockito.Spy
import static org.powermock.api.mockito.PowerMockito.when
import org.powermock.core.classloader.annotations.PrepareForTest
import static org.powermock.api.mockito.PowerMockito.doNothing
import static org.powermock.api.mockito.PowerMockito.whenNew
import static org.mockito.Matchers.anyString
import org.mockito.Mock

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 11:45
 */
//@RunWith(PowerMockRunner.class)
class AccountTest extends TestBaseClass {

  @Spy
  Account target

/*
Test disabled in waiting for this issue to be resolved: https://issues.jboss.org/browse/JASSIST-142

  @Test
  @PrepareForTest(Role.class)
  void testGetRoles() {
    String e1 = "E1", e2 = "E2"
    @Mock Role r1
    @Mock Role r2

    when(target.getEduPersonEntitlements()).thenReturn([e1, e2])

    whenNew(Role.class).withArguments(e1).thenReturn(r1)
    whenNew(Role.class).withArguments(e2).thenReturn(r2)

    Role[] roles = account.getRoles()

    assert roles.size() == 2
  }
*/

  @Test
  void testDn() {
    Cache.getInstance().enable()
    SuPerson suPerson1 = SuPerson.findByUid("jolu")
    def dn1 = suPerson1.getDn()
    SuPerson suPerson2 = SuPerson.findByUid("jolu")
    def dn2 = suPerson2.getDn()

    assert dn1 == dn2

    Cache.getInstance().disable()
  }
}
