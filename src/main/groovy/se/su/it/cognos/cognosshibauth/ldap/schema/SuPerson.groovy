package se.su.it.cognos.cognosshibauth.ldap.schema

import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.Gldapo
import gldapo.entry.GldapoEntry
import gldapo.GldapoDirectory
import gldapo.schema.GldapoSchemaClassInjecto
import gldapo.schema.annotation.GldapoSchemaFilter
import static java.lang.ClassLoader.getSystemClassLoader
import java.util.logging.Logger
import java.util.logging.Level
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import static gldapo.filter.FilterUtil.eq
import se.su.it.cognos.cognosshibauth.memcached.Cache
import org.springframework.ldap.core.DistinguishedName

/**
 * User: joakim
 * Date: 2011-05-09
 * Time: 12:46
 */

@GldapoSchemaFilter("(objectClass=suPerson)")
class SuPerson extends SchemaBase implements Serializable {

  private static final long serialVersionUID = 3780390928840396076L ;

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
  Set<String> memberOf

  static SuPerson findByUid(String uid) {
    find(filter:"(uid=$uid)")
  }

  private void writeObject(ObjectOutputStream s) {
    s.defaultWriteObject()
  }

  private void readObject(ObjectInputStream s) {
    s.defaultReadObject()
  }
}



