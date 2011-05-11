package se.su.it.cognos.cognosshibauth.ldap.schema

import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import java.util.logging.Logger
import java.util.logging.Level
import gldapo.Gldapo

/**
 * User: Joakim Lundin <joakim.lundin@it.su.se>
 * Date: 2011-05-10
 * Time: 12:51
 */
class SchemaBase {
  static {
    ConfigHandler configHandler = ConfigHandler.instance()
    def url = configHandler.getStringEntry("ldap.url", "ldap://sukat-ldap.it.su.se")
    def baseDn = configHandler.getStringEntry("ldap.base_dn", "dc=su,dc=se")
    def countLimit = configHandler.getIntEntry("ldap.count_limit", 500)
    def timeLimit = configHandler.getIntEntry("ldap.time_limit", 120000)

    Logger LOG = Logger.getLogger(SchemaBase.class.getName())
    LOG.log(Level.FINE, "Initializing Gldapo schema '${SchemaBase.class.getName()}'")

    Gldapo.initialize(
            directories: [
                    example: [
                            url: url,
                            base: baseDn,
                            searchControls: [
                                    countLimit: countLimit,
                                    timeLimit: timeLimit,
                                    searchScope: "subtree"
                            ]
                    ]
            ],
            schemas: [
                    se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson,
                    se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
            ]
    )
  }
}
