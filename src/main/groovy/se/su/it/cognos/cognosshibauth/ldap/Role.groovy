package se.su.it.cognos.cognosshibauth.ldap;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;

import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson;

public class Role extends UiClass implements IRole {

  public Role(String namespaceId, String name) {
    super("${namespaceId}:${UiClass.PREFIX_ROLE}:${name}")

    addName(defaultLocale, name)
  }

  public IBaseClass[] getMembers() {
    ConfigHandler configHandler = ConfigHandler.instance();

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix");
    String gmaiApplication = configHandler.getStringEntry("gmai.application");

    String gmaiUrn = "${gmaiPrefix}:${gmaiApplication}:${getName(defaultLocale)}"

    List<SuPerson> suPersons = SuPerson.findAll(filter: "eduPersonEntitlement=${gmaiUrn}")

    suPersons.collect { suPerson ->
      new Account(namespaceId, suPerson)
    } as IBaseClass[]
  }

  static List<Role> findAllByFilter(String namespacaId, String filter) {
    List<SuPerson> suPersons = SuPerson.findAll(filter: filter)

    List<String> entitlements = new ArrayList<String>()
    suPersons.each { suPerson ->
      entitlements.addAll suPerson.eduPersonEntitlement
    }

    entitlements.unique().collect { entitlement ->
      new Role(namespacaId, parseRoleFromEntitlementUri(entitlement))
    }
  }

  static String parseRoleFromEntitlementUri(String entitlement) {
    ConfigHandler configHandler = ConfigHandler.instance()

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix")
    String gmaiApplication = configHandler.getStringEntry("gmai.application")

    if(entitlement != null && entitlement.startsWith("${gmaiPrefix}:${gmaiApplication}:")) {
      String subS = entitlement.substring(("${gmaiPrefix}:${gmaiApplication}:").length())
      subS.indexOf(":")
      return subS
    }
    return null
  }
}
