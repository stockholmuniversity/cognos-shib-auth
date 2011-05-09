package se.su.it.cognos.cognosshibauth.ldap;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IAccount;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.sukat.SUKAT;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult

public class Account extends UiClass implements IAccount {

  public Logger LOG = Logger.getLogger(Account.class.getName());

  public String businessPhone = null;
  public String email = null;
  public String faxPhone = null;
  public String givenName = null;
  public String homePhone = null;
  public String mobilePhone = null;
  public String pagerPhone = null;
  public String postalAddress = null;
  public String surname = null;
  public String userName = null;

  public Locale contentLocale;
  public Locale productLocale;

  public HashMap<String, List<String>> customProperties = null;

  public Account(String namespaceId, String dn) throws Exception {
    super("${namespaceId}:${UiClass.PREFIX_USER}:${dn}")

    productLocale = contentLocale = defaultLocale;

    customProperties = new HashMap<String, List<String>>();

    String ldapURL = configHandler.getStringEntry("adapters.url");

    SUKAT sukat = SUKAT.newInstance(ldapURL);
    SearchResult result = sukat.read(dn);
    Attributes attributes = result.getAttributes();
    Attribute businessPhone = attributes.get("telephoneNumber");
    Attribute email = attributes.get("mail");
    Attribute faxPhone = attributes.get("");
    Attribute givenName = attributes.get("givenName");
    Attribute homePhone = attributes.get("homePhone");
    Attribute mobilePhone = attributes.get("mobile");
    Attribute pagerPhone = attributes.get("");
    Attribute postalAddress = attributes.get("registeredAddress");
    Attribute surname = attributes.get("sn");
    Attribute userName = attributes.get("uid");
    Attribute entitlement = attributes.get("eduPersonEntitlement");

    addName(contentLocale, "");
    addDescription(contentLocale, "");
  }


  public String[] getCustomPropertyNames() {
    if (customProperties != null) {
      Set keySet = this.customProperties.keySet();
      return (String[]) keySet.toArray(new String[keySet.size()]);
    }
    return null;
  }

  public String[] getCustomPropertyValue(String theName) {
    List<String> list = customProperties.get(theName);
    if (list != null)
      return (String[]) list.toArray(new String[list.size()]);
    return null;
  }

  public void addCustomProperty(String theName, String theValue) {
    List<String> list = customProperties.get(theName);

    if (list == null) {
      list = new ArrayList<String>();
      customProperties.put(theName, list);
    }

    list.add(theValue);
  }

  public static Account fromSearchResult(String namespaceId, SearchResult result) throws Exception {
    if (result != null) {
      Account account = new Account(namespaceId, result.getNameInNamespace());
      return account;
    }
    return null;
  }

  public static Account fromUid(String uid, String namespaceId) throws Exception {
    ConfigHandler configHandler = ConfigHandler.instance();
    String ldapURL = configHandler.getStringEntry("adapters.url");

    SUKAT sukat = SUKAT.newInstance(ldapURL);
    SearchResult result = sukat.findUserByUid(uid);
    return Account.fromSearchResult(namespaceId, result);
  }
}