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
import org.junit.Before
import org.codehaus.groovy.runtime.InvokerHelper
import com.cognos.CAM_AAA.authentication.ISearchStep.SearchAxis
import gldapo.Gldapo
import se.su.it.cognos.cognosshibauth.ldap.schema.SchemaBase
import org.springframework.ldap.core.DistinguishedName

public class CognosShibAuthBaseTest extends TestBaseClass {

  private CognosShibAuthBase target = new CognosShibAuthBase();

  @Before
  void setUp() {
    target.metaClass.publicGetResult = { int searchType, String objectID, ISearchFilter filter ->
      delegate.getQueryResult(searchType, objectID, filter)
    }
    
    super.setUp()
  }

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
  void testGetQueryResultReturnsACollection() {
    assert target.publicGetResult(0, null, null) instanceof Collection
  }

  @Test
  void testTextSearch() {
    def relationExpression = new MockFor(ISearchFilterRelationExpression.class)
    relationExpression.demand.getSearchFilterType { ISearchFilter.RelationalExpression }
    relationExpression.demand.getPropertyName { "@objectClass" }
    relationExpression.demand.getConstraint { "account" }
    relationExpression.demand.getOperator { "=" }

    def functionCall = new MockFor(ISearchFilterFunctionCall.class)
    functionCall.demand.getSearchFilterType { ISearchFilter.FunctionCall }
    functionCall.demand.getParameters(1..3) { ["@defaultName", "jolu"] }
    functionCall.demand.getFunctionName { ISearchFilterFunctionCall.EndsWith }
    functionCall.demand.getParameters(1..3) { ["@defaultName", "jolu"] }

    def topFilter = new MockFor(ISearchFilterConditionalExpression.class)
    topFilter.demand.getSearchFilterType { ISearchFilter.ConditionalExpression }
    topFilter.demand.getFilters(1..3) {
      [relationExpression.proxyInstance(), functionCall.proxyInstance()]
    }
    topFilter.demand.getOperator(1..3) { "and" }


    def list = target.getQueryResult(SearchAxis.Descendent, "Cognos Shib Authenticator:f:Users", topFilter.proxyInstance())

    assert 1 == list.size()
  }
}
