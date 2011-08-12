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
import se.su.it.cognos.cognosshibauth.memcached.Cache

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 12:46
 */

@GldapoSchemaFilter("(objectClass=suPerson)")
class SuPerson extends SchemaBase {

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
    Cache.getInstance().get("GLDAPO-SuPerson:uid=$uid", { find(filter:"(uid=$uid)") })
  }

  static SuPerson getByDnCached(dn) {
    Cache.getInstance().get("GLDAPO-SuPerson:$dn", { SuPerson.getByDn(dn) })
  }
}



