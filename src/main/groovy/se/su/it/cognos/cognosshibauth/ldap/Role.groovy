package se.su.it.cognos.cognosshibauth.ldap;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;

import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace;

public class Role extends UiClass implements IRole {
  private static long SerialVersionUID = 6L
  public Role(String name) {
    super("${CognosShibAuthNamespace.namespaceId}:${UiClass.PREFIX_ROLE}:${name}")

    addName(defaultLocale, name)
  }

  public IBaseClass[] getMembers() {
    ConfigHandler configHandler = ConfigHandler.instance();

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix");
    String gmaiApplication = configHandler.getStringEntry("gmai.application");

    String gmaiUrn = "${gmaiPrefix}:${gmaiApplication}:${getName(defaultLocale)}"

    List<SuPerson> suPersons = SuPerson.findAll(filter: "(eduPersonEntitlement=${gmaiUrn})")

    suPersons.collect { suPerson ->
      new Account(suPerson)
    } as IBaseClass[]
  }

  static Role createFromUri(String gmaiUri) {
    String roleName = parseRoleFromEntitlementUri(gmaiUri)

    roleName != null ? new Role(roleName) : null
  }

  static List<Role> findAllByFilter(String filter) {
    List<SuPerson> suPersons = SuPerson.findAll(filter: filter)

    List<String> entitlements = new ArrayList<String>()
    suPersons.each { suPerson ->
      entitlements.addAll suPerson.eduPersonEntitlement
    }

    entitlements.unique().removeAll { entitlement ->
      !isApplicationRole(entitlement)
    }

    entitlements.collect { entitlement ->
      new Role(parseRoleFromEntitlementUri(entitlement))
    }
  }

  static String parseRoleFromEntitlementUri(String entitlement) {
    ConfigHandler configHandler = ConfigHandler.instance()

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix")
    String gmaiApplication = configHandler.getStringEntry("gmai.application")

    if(entitlement != null && isApplicationRole(entitlement)) {
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
}
