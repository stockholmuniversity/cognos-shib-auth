package se.su.it.cognos.cognosshibauth.ldap;

import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IAccount;

import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson

public class Account extends UiClass implements IAccount {

  public Logger LOG = Logger.getLogger(Account.class.getName());

  SuPerson suPerson

  public Locale contentLocale;
  public Locale productLocale;

  public HashMap<String, List<String>> customProperties = null;

  public Account(String namespaceId, String dn) throws Exception {
    this(namespaceId, SuPerson.getByDn(dn))
  }

  public Account(String namespaceId, SuPerson suPerson) throws Exception {
    super("${namespaceId}:${UiClass.PREFIX_USER}:${suPerson.getDn()}")

    productLocale = contentLocale = defaultLocale

    customProperties = new HashMap<String, List<String>>()

    this.suPerson = suPerson

    addName(contentLocale, "${suPerson.givenName} ${suPerson.sn}")
    addDescription(contentLocale, "")
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

  public static Account findByUid(String namespaceId, String uid) throws Exception {
    SuPerson suPerson1 = SuPerson.find(filter: "(uid=${uid})")
    return new Account(namespaceId, suPerson1);
  }

  @Override
  String getBusinessPhone() {
    suPerson.telephoneNumber
  }

  @Override
  String getEmail() {
    suPerson.mail
  }

  @Override
  Locale getContentLocale() {
    contentLocale
  }

  @Override
  String getFaxPhone() {
    suPerson.faxPhone
  }

  @Override
  String getGivenName() {
    suPerson.givenName
  }

  @Override
  String getHomePhone() {
    suPerson.homePhone
  }

  @Override
  String getMobilePhone() {
    suPerson.mobile
  }

  @Override
  String getPagerPhone() {
    suPerson.pagerPhone
  }

  @Override
  String getPostalAddress() {
    suPerson.registeredAddress
  }

  @Override
  Locale getProductLocale() {
    productLocale
  }

  @Override
  String getSurname() {
    suPerson.sn
  }

  @Override
  String getUserName() {
    suPerson.uid
  }
}