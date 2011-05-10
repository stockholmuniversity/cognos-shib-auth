package se.su.it.cognos.cognosshibauth.ldap.schema

import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.Gldapo
import gldapo.entry.GldapoEntry
import gldapo.schema.annotation.GldapoSchemaFilter
import static java.lang.ClassLoader.getSystemClassLoader

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 12:46
 */

@GldapoSchemaFilter("objectClass=suPerson")
class SuPerson {

  @GldapoNamingAttribute
  String uid
  String givenName
  String sn
  String mail
  String mobile
  String homePhone
  String telephoneNumber
  String faxPhone
  String pagerPhone
  String registeredAddress
  Set<String> eduPersonEntitlement

  static {
    ClassLoader classLoader = getSystemClassLoader()
    File file = new File(classLoader.getResource("gldapo-conf.groovy").getFile())
    Gldapo.initialize(file.toURL())
  }
}

