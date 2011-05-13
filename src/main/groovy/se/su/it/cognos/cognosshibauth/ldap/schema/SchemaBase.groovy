package se.su.it.cognos.cognosshibauth.ldap.schema

import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import java.util.logging.Logger
import java.util.logging.Level
import gldapo.Gldapo

import gldapo.GldapoSchemaRegistry
import gldapo.GldapoDirectory
import se.su.it.cognos.cognosshibauth.ldap.gldapo.DummyGldapoDirectoryRegistry

class SchemaBase {
  static {
    ConfigHandler configHandler = ConfigHandler.instance()
    def url = configHandler.getStringEntry("ldap.url", "ldap://sukat-ldap.it.su.se")
    def baseDn = configHandler.getStringEntry("ldap.base_dn", "dc=su,dc=se")
    def countLimit = configHandler.getIntEntry("ldap.count_limit", 500)
    def timeLimit = configHandler.getIntEntry("ldap.time_limit", 120000)

    Logger LOG = Logger.getLogger(SchemaBase.class.getName())
    LOG.log(Level.FINE, "Initializing Gldapo schema '${SchemaBase.class.getName()}'")

    Gldapo gldapo = new Gldapo()

    def gldapoDirectoryMap = [
      defaultDirectory: true,
      url: url,
      base: baseDn,
      searchControls: [
        countLimit: 500,
        timeLimit: 120000,
        searchScope: "subtree"
      ]
    ]

    GldapoSchemaRegistry gldapoSchemaRegistry = new GldapoSchemaRegistry(gldapo)
    gldapoSchemaRegistry.add(se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson)
    gldapoSchemaRegistry.add(se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames)
    gldapo.setSchemas(gldapoSchemaRegistry)

    DummyGldapoDirectoryRegistry gldapoDirectoryRegistry = new DummyGldapoDirectoryRegistry()
    GldapoDirectory gldapoDirectory = new GldapoDirectory("example", gldapoDirectoryMap)
    gldapoDirectoryRegistry.put("example", gldapoDirectory)
    gldapo.setDirectories(gldapoDirectoryRegistry)
  }
}
