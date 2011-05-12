package se.su.it.cognos.cognosshibauth.ldap.schema

import gldapo.schema.annotation.GldapoSchemaFilter
import gldapo.schema.annotation.GldapoNamingAttribute

/**
 * User: Joakim Lundin <joakim.lundin@it.su.se>
 * Date: 2011-05-10
 * Time: 08:37
 */

@GldapoSchemaFilter("objectClass=groupOfUniqueNames")
class GroupOfUniqueNames extends SchemaBase {

  @GldapoNamingAttribute
  String cn
  String description
  Set<String> uniqueMember
}