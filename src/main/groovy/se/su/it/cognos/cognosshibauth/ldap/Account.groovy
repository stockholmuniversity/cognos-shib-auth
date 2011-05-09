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

  private Logger LOG = Logger.getLogger(Account.class.getName());

  private String businessPhone = null;
  private String email = null;
  private String faxPhone = null;
  private String givenName = null;
  private String homePhone = null;
  private String mobilePhone = null;
  private String pagerPhone = null;
  private String postalAddress = null;
  private String surname = null;
  private String userName = null;

  private Locale	contentLocale;
  private Locale	productLocale;

  private HashMap<String, List<String>> customProperties = null;

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

  public String getBusinessPhone() {
    return businessPhone;
  }

  public String getEmail() {
    return email;
  }

  public Locale getContentLocale() {
    LOG.log(Level.FINEST, "Getting content locale for '${this.userName}'.");
    return contentLocale;
  }

  public String getFaxPhone() {
    return faxPhone;
  }

  public String getGivenName() {
    return givenName;
  }

  public String getHomePhone() {
    return homePhone;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public String getPagerPhone() {
    return pagerPhone;
  }

  public String getPostalAddress() {
    return postalAddress;
  }

  public Locale getProductLocale() {
    LOG.log(Level.FINEST, "Getting content locale for '${this.userName}'.");
    return productLocale;
  }

  public String getSurname() {
    return surname;
  }

  public String getUserName() {
    return userName;
  }

  public void setBusinessPhone(String theBusinessPhone) {
    businessPhone = theBusinessPhone;
  }

  public void setContentLocale(Locale theContentLocale) {
    contentLocale = theContentLocale;
  }

  public void setEmail(String theEmail) {
    email = theEmail;
  }

  public void setFaxPhone(String theFaxPhone) {
    faxPhone = theFaxPhone;
  }

  public void setGivenName(String theGivenName) {
    givenName = theGivenName;
  }

  public void setHomePhone(String theHomephone) {
    homePhone = theHomephone;
  }

  public void setMobilePhone(String theMobilePhone) {
    mobilePhone = theMobilePhone;
  }

  public void setPagerPhone(String thePagerPhone) {
    pagerPhone = thePagerPhone;
  }

  public void setPostalAddress(String thePostalAddress) {
    postalAddress = thePostalAddress;
  }

  public void setSurname(String theSurname) {
    surname = theSurname;
  }

  public void setUserName(String theUsername) {
    userName = theUsername;
  }

  public String[] getCustomPropertyNames() {
    if (customProperties != null)
    {
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
    if(result != null) {
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