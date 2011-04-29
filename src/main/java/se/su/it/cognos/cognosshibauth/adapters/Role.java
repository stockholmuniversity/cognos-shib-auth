package se.su.it.cognos.cognosshibauth.adapters;
import java.util.*;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;
import org.apache.commons.lang.ArrayUtils;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.sukat.SUKAT;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class Role extends UiClass implements IRole {

  private List<IBaseClass> members = null;

  private Locale defaultLocale = null;

  public Role(String namespaceId, String name) {
    super(namespaceId + ":" + PREFIX_ROLE + name);
    
    members = new ArrayList<IBaseClass>();

    ConfigHandler configHandler = ConfigHandler.instance();
    defaultLocale = configHandler.getContentLocale();

    addName(defaultLocale, name);
  }

  public void addMember(IBaseClass theMember) {
    members.add(theMember);
  }

  public IBaseClass[] getMembers() {
    ConfigHandler configHandler = ConfigHandler.instance();

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix");
    String gmaiApplication = configHandler.getStringEntry("gmai.application");

    String gmaiUrn = gmaiPrefix + ":" + gmaiApplication + ":" + getName(defaultLocale);

    List<IBaseClass> accounts = new ArrayList<IBaseClass>();
    try {
      SUKAT sukat = SUKAT.newInstance("ldap://ldap.su.se");
      NamingEnumeration<SearchResult> results = sukat.search("dc=su,dc=se", "eduPersonEntitlement=" + gmaiUrn);
      while(results.hasMoreElements()) {
        Account account = Account.fromSearchResult(null, results.next());
        accounts.add(account);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return accounts.toArray(new Account[accounts.size()]);
  }

  public static Collection<Role> fromSearchResults(String namespaceId, NamingEnumeration<SearchResult> results) {
    HashMap<String, Role> roleNames = new HashMap<String, Role>();

    while(results.hasMoreElements()) {
      try {
        SearchResult result = results.next();

        Attributes attributes = result.getAttributes();
        Attribute entitlement = attributes.get("eduPersonEntitlement");

        try {
          NamingEnumeration entries = entitlement.getAll();
          while(entries.hasMoreElements()) {
            Object obj = entries.next();
            if(obj instanceof String) {
              String roleName = parseRoleFromEntitlementUri((String) obj);

              if(roleName != null)
                roleNames.put(roleName, new Role(namespaceId, roleName));
            }
          }
        } catch (NamingException e) {
          e.printStackTrace();
        }
      } catch (NamingException e) {
        e.printStackTrace();
      }
    }

    return roleNames.values();
  }

  public static String parseRoleFromEntitlementUri(String entitlement) {
    ConfigHandler configHandler = ConfigHandler.instance();

    String gmaiPrefix = configHandler.getStringEntry("gmai.prefix");
    String gmaiApplication = configHandler.getStringEntry("gmai.application");

    if(entitlement != null && entitlement.startsWith(gmaiPrefix + ":" + gmaiApplication + ":")) {
      String subS = entitlement.substring((gmaiPrefix + ":" + gmaiApplication + ":").length());
      subS.indexOf(":");
      return subS;
    }
    return null;
  }
}
