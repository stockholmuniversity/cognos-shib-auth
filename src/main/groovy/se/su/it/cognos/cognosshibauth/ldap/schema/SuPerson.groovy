package se.su.it.cognos.cognosshibauth.ldap.schema

import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.Gldapo
import gldapo.entry.GldapoEntry
import gldapo.schema.annotation.GldapoSchemaFilter
import static java.lang.ClassLoader.getSystemClassLoader
import java.util.logging.Logger
import java.util.logging.Level
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import static gldapo.filter.FilterUtil.eq

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 12:46
 */

class SuPerson extends SchemaBase {

@GldapoSchemaFilter("(objectClass=suPerson)")
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

  static SuPerson findByUid(String uid) {
    return find(base: "") {
      eq "uid", uid
    }
  }
}

