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
    target.metaClass.publicGetResult = { int searchType, String objectID, ISearchFilter filter, IQueryOption queryOption ->
      delegate.getQueryResult(searchType, objectID, filter, queryOption)
    }
    
    super.setUp()
  }

  @Test
  void testGetQueryResultReturnsACollection() {
    assert target.publicGetResult(0, null, null, null) instanceof Collection
  }
}
