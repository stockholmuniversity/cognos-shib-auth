package se.su.it.cognos.cognosshibauth.ldap.schema

import gldapo.schema.annotation.GldapoSchemaFilter
import gldapo.schema.annotation.GldapoNamingAttribute

/**
 * User: joakim
 * Date: 2011-05-10
 * Time: 08:37
 */

@GldapoSchemaFilter("objectClass=groupOfUniqueNames")
class GroupOfUniqueNames {

  @GldapoNamingAttribute
  String cn
  String description
  List<String> uniqueMember
}
