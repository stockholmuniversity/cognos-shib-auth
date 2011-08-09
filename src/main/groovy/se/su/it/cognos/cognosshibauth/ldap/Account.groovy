package se.su.it.cognos.cognosshibauth.ldap

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IAccount

import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace

public class Account extends UiClass implements IAccount {

  private static Logger LOG = Logger.getLogger(Account.class.getName())

  SuPerson suPerson

  Locale contentLocale
  Locale productLocale

  HashMap<String, List<String>> customProperties

  /**
   * Constructs an Account instance
   * @param SuPerson suPerson
   */
  public Account(SuPerson suPerson) {
    super("${CognosShibAuthNamespace.namespaceId}:${UiClass.PREFIX_USER}:${suPerson.getDn()}")

    productLocale = contentLocale = defaultLocale

    customProperties = new HashMap<String, List<String>>()

    this.suPerson = suPerson

    addName(contentLocale, "${suPerson.givenName} ${suPerson.sn}")
    addDescription(contentLocale, "") //TODO: link to "Kontohantering"?
  }
/**
 * Create an Account from dn
 * @param String dn identifier in sukat
 * @return A new Account instance based on what is fetched from sukat from dn
 */
  public static Account createFromDn(String dn) {
    new Account(SuPerson.getByDn(dn))
  }
  @Override
  public String[] getCustomPropertyNames() {
    if (customProperties != null) {
      Set keySet = this.customProperties.keySet();
      return (String[]) keySet.toArray(new String[keySet.size()]);
    }
    return null;
  }
  @Override
  public String[] getCustomPropertyValue(String theName) {
    List<String> list = customProperties.get(theName);
    if (list != null)
      return (String[]) list.toArray(new String[list.size()]);
    return null;
  }
  /**
   * Add property to the Accounts custom property HashMap
   * @param String theName
   * @param String theValue
   */
  public void addCustomProperty(String theName, String theValue) {
    List<String> list = customProperties.get(theName);

    if (list == null) {
      list = new ArrayList<String>();
      customProperties.put(theName, list);
    }

    list.add(theValue);
  }
  /**
   * Find an Account by uid
   * @param String uid
   * @return A new Account instance based on what is fetched from sukat from uid
   */
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
  /**
   * Fetches the Accounts eduPersonEntitlement strings in a Set
   * @return Set<String> eduPersonEntitlement
   */
  Set<String> getEduPersonEntitlements() {
    suPerson.eduPersonEntitlement
  }

  /**
   * Get all roles for this account
   *
   * @return list of roles for this account
   */
  List<Role> getRoles() {
    getEduPersonEntitlements().collect { entitlement ->
      Role.createFromUri(entitlement)
    }.removeAll {it == null}
  }

  /**
   * Get all groups for this account
   *
   * @return list of groups for this account
   */
  List<Group> getGroups() {
    Group.findByMember(this)
  }
}
