
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IAccount;

public class Account extends CognosShibAuthUiClass implements IAccount {

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

  public Account(String theObjectID, String userName, String givenName, String surname,
                 Locale contentLocale) {
    super(theObjectID);

    this.contentLocale = contentLocale;
    this.givenName = givenName;
    this.surname = surname;
    this.userName = userName;

    customProperties = new HashMap<String, List<String>>();

    addName(contentLocale, givenName + " " + surname);
    addDescription(contentLocale, "");
  }

  public String getBusinessPhone() {
    return businessPhone;
  }

  public String getEmail() {
    return email;
  }

  public Locale getContentLocale() {
    LOG.log(Level.FINEST, "Getting content locale for '" + this.userName + "'.");
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
    LOG.log(Level.FINEST, "Getting content locale for '" + this.userName + "'.");
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

  public void setProductLocale(Locale theProductLocale) {
    productLocale = theProductLocale;
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
}
