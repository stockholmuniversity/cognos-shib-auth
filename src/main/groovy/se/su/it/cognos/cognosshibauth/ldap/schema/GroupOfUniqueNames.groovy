package se.su.it.cognos.cognosshibauth.ldap.schema

import gldapo.schema.annotation.GldapoSchemaFilter
import gldapo.schema.annotation.GldapoNamingAttribute
import se.su.it.cognos.cognosshibauth.memcached.Cache

/**
 * User: Joakim Lundin <joakim.lundin@it.su.se>
 * Date: 2011-05-10
 * Time: 08:37
 */

@GldapoSchemaFilter("(objectClass=groupOfUniqueNames)")
class GroupOfUniqueNames extends SchemaBase  implements Serializable {

  private static final long serialVersionUID = 4481281134744979887L ;

  @GldapoNamingAttribute
  String cn
  String description
  Set<String> uniqueMember

  static Collection<GroupOfUniqueNames> findAllByUniqueMember(String memberDn) {
    findAll(filter:"(uniqueMember=$memberDn)")
  }

  private void writeObject(ObjectOutputStream s) {
    s.defaultWriteObject()
  }

  private void readObject(ObjectInputStream s) {
    s.defaultReadObject()
  }
}