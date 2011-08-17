package se.su.it.cognos.cognosshibauth

import org.junit.Before
import se.su.it.cognos.cognosshibauth.memcached.Cache
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.ldap.Account;

public class TestBaseClass {
  SuPerson mockSuPerson = new SuPerson()

  @Before
  void setUp() {
    Cache.getInstance().disable()

    mockSuPerson.metaClass.getDn = { "uid=test1,dc=it,dc=su,dc=se" }
    mockSuPerson.metaClass.getUid = { "test1" }

    SuPerson.metaClass.static.getByDn = { dn -> mockSuPerson }
    SuPerson.metaClass.static.findByUid << { uid -> mockSuPerson }

    SuPerson.metaClass.static.findAll = { filter -> mockSuPerson }

    Account.metaClass.static.createTestAccount << { new Account(mockSuPerson) }
  }
}
