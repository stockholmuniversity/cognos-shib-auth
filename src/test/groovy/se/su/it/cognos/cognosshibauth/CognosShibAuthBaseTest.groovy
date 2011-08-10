package se.su.it.cognos.cognosshibauth

import org.junit.Test
import se.su.it.cognos.cognosshibauth.ldap.Account
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import com.cognos.CAM_AAA.authentication.*
import static junit.framework.Assert.assertEquals
import groovy.mock.interceptor.MockFor
import se.su.it.cognos.cognosshibauth.ldap.UiClass
import se.su.it.cognos.cognosshibauth.memcached.Cache
import static junit.framework.Assert.assertNull
import static junit.framework.Assert.assertNotNull

public class CognosShibAuthBaseTest {

  private CognosShibAuthBase target = new CognosShibAuthBase();

  @Test
  public void testSearch() throws Exception {

    def iVisa = new MockFor(IVisa.class)
    def iQuery = new MockFor(IQuery.class)

    def iSearchExpression = new MockFor(ISearchExpression.class)
    def iSearchStep = new MockFor(ISearchStep.class)

    SuPerson mockSuPerson = new SuPerson()
    mockSuPerson.metaClass.getDn = { "uid=test1,dc=it,dc=su,dc=se" }

    def mockAccount = new Account(mockSuPerson)


    String objectId = "TEST:u:uid=test,dc=it,dc=su,dc=se"
    def iSearchSteps = []
    iSearchSteps[0] = iSearchStep

    mockAccount.metaClass.objectID = { "fookaka" }

    iQuery.demand.getSearchExpression { iSearchExpression.proxyInstance() }

    iSearchStep.demand.getAxis(0..2) { ISearchStep.SearchAxis.Self }

    iSearchStep.demand.getPredicate { null }

    iSearchExpression.demand.getObjectID { objectId }
    iSearchExpression.demand.getSteps { [iSearchStep.proxyInstance()] }

    Account.metaClass.'static'.createFromDn = { String dn -> mockAccount }

    QueryResult result = target.search(iVisa.proxyInstance(), iQuery.proxyInstance())

    assert result.objects.first() instanceof Account
  }

  @Test
  void testCache() {
    String key = "TEST:u:uid=test,dc=it,dc=su,dc=se"

    Cache.getInstance().delete(key)

    Account account = (Account) Cache.getInstance().get(key)
    assertNull account

    account = Account.createFromDn(UiClass.camIdToName(key));
    Cache.getInstance().set(key, 1, account)

    def result = Cache.getInstance().get(key)
    assertNotNull(result)
    assertEquals(account.getUserName(), result.getUserName())
  }
}
