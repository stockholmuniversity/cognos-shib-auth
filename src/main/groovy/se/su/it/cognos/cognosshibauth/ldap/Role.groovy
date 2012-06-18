package se.su.it.cognos.cognosshibauth.ldap;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;

import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace
import se.su.it.cognos.cognosshibauth.memcached.Cache;

public class Role extends UiClass implements IRole {

  private static long SerialVersionUID = 6L

  public Role(String name) {
    super("${CognosShibAuthNamespace.namespaceId}:${UiClass.PREFIX_ROLE}:${name}")

    addName(defaultLocale, name)
  }

  public IBaseClass[] getMembers() {
    def gmaiUrn = getGMAIUrn()

    List<SuPerson> suPersons = SuPerson.findAll(filter: "(eduPersonEntitlement=${gmaiUrn})")

    suPersons.collect { suPerson ->
      def key = createObjectId(UiClass.PREFIX_USER, suPerson.getDn())
      Cache.getInstance().get(key, { new Account(suPerson) })
    } as IBaseClass[]
  }

  static Role createFromUri(String gmaiUri) {
    String roleName = parseRoleFromEntitlementUri(gmaiUri)

    if (roleName != null) {
      def key = createObjectId(UiClass.PREFIX_ROLE, roleName)
      return Cache.getInstance().get(key, { new Role(roleName) })
    }
    null
  }

  static List<Role> findAllByFilter(String filter) {
    List<SuPerson> suPersons = SuPerson.findAll(filter: filter)

    List<String> entitlements = new ArrayList<String>()
    suPersons.each { suPerson ->
      entitlements.addAll suPerson.eduPersonEntitlement
    }

    entitlements = entitlements.unique()
    entitlements.removeAll { entitlement ->
      !isApplicationRole(entitlement)
    }

    entitlements.collect { entitlement ->
      def roleName = parseRoleFromEntitlementUri(entitlement)
      def key = createObjectId(UiClass.PREFIX_ROLE, roleName)
      Cache.getInstance().get(key, { new Role(roleName) })
    }
  }

  static List<Role> findAllByName(String name) {
    List<SuPerson> suPersons = SuPerson.findAll(filter: "(eduPersonEntitlement=urn:mace:swami.se:gmai:su-ivs:*)")

    List<String> entitlements = new ArrayList<String>()
    suPersons.each { suPerson ->
      entitlements.addAll suPerson.eduPersonEntitlement
    }

    entitlements = entitlements.unique()
    entitlements.removeAll { entitlement ->
      !isApplicationRole(entitlement)
    }

    List<Role> roles = new ArrayList<Role>()

    entitlements.each { entitlement ->
      def roleName = parseRoleFromEntitlementUri(entitlement)
      if (name == roleName) {
        def key = createObjectId(UiClass.PREFIX_ROLE, roleName)
        roles.add Cache.getInstance().get(key, { new Role(roleName) })
      }
    }

    roles
  }

  static String parseRoleFromEntitlementUri(String entitlement) {
    ConfigHandler configHandler = ConfigHandler.instance()

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix")
    String gmaiApplication = configHandler.getStringEntry("gmai.application")

    if(entitlement != null && isApplicationRole(entitlement)) { //TODO: Fix this to handle (or ignore) scope.
      String subS = entitlement.substring(("${gmaiPrefix}:${gmaiApplication}:").length())
      subS.indexOf(":")
      return subS
    }
    return null
  }

  static boolean isApplicationRole(entitlement) {
    ConfigHandler configHandler = ConfigHandler.instance()

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix")
    String gmaiApplication = configHandler.getStringEntry("gmai.application")

    entitlement.startsWith("${gmaiPrefix}:${gmaiApplication}:")
  }

  static boolean exists(String roleName) {
    List<Role> roles = findAllByName(roleName)

    roles && roles.size() > 0
  }

  private String getGMAIUrn() {
    ConfigHandler configHandler = ConfigHandler.instance();

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix");
    String gmaiApplication = configHandler.getStringEntry("gmai.application");

    "${gmaiPrefix}:${gmaiApplication}:${getName(defaultLocale)}"
  }
}
