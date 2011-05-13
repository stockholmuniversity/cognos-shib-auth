package se.su.it.cognos.cognosshibauth.ldap

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IAccount

import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace

public class Account extends UiClass implements IAccount {

  Logger LOG = Logger.getLogger(Account.class.getName())

  SuPerson suPerson

  Locale contentLocale
  Locale productLocale

  HashMap<String, List<String>> customProperties

  public Account(SuPerson suPerson) {
    super("${CognosShibAuthNamespace.namespaceId}:${UiClass.PREFIX_USER}:${suPerson.getDn()}")

    productLocale = contentLocale = defaultLocale

    customProperties = new HashMap<String, List<String>>()

    this.suPerson = suPerson

    addName(contentLocale, "${suPerson.givenName} ${suPerson.sn}")
    addDescription(contentLocale, "") //TODO: link to "Kontohantering"?
  }

  public static Account createFromDn(String dn) {
    new Account(SuPerson.getByDn(dn))
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

  static Account findByUid(String uid) {
    SuPerson suPerson1 = SuPerson.findByUid(uid)
    return new Account(suPerson1)
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

  String getEduPersonEntitlements() {
    suPerson.eduPersonEntitlement
  }
}
