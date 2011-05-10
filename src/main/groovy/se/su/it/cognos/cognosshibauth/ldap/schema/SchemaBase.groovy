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
    Logger LOG = Logger.getLogger(SchemaBase.class.getName())
    URL url = new URL(configHandler.getStringEntry("gldapo.conf", "gldapo-conf.groovy"))
    LOG.log(Level.FINE, "Initializing Gldapo schema '${SchemaBase.class.getName()}' from file '${url}'")
    Gldapo.initialize(url)
  }
}
