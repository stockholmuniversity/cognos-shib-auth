
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IAccount;
import sun.util.LocaleServiceProviderPool;


public class CognosShibAuthAccount extends CognosShibAuthUiClass implements IAccount {

  private Logger LOG = Logger.getLogger(CognosShibAuthAccount.class.getName());

  private String	businessPhone;
  private String	email;
  private Locale	contentLocale;
  private Locale	productLocale;
  private String	faxPhone;
  private String	givenName;
  private String	homePhone;
  private String	mobilePhone;
  private String	pagerPhone;
  private String	postalAddress;
  private String	surname;
  private String	userName;
  private HashMap	customProperties;



  public CognosShibAuthAccount(String theObjectID, String userName, String givenName, String surname, Locale contentLocale) {
    super(theObjectID);
    businessPhone = null;
    email = null;
    this.contentLocale = contentLocale;
    productLocale = null;
    faxPhone = null;
    this.givenName = givenName;
    homePhone = null;
    mobilePhone = null;
    pagerPhone = null;
    postalAddress = null;
    this.surname = surname;
    this.userName = userName;
    customProperties = null;

    addName(contentLocale, givenName + " " + surname);
    addDescription(contentLocale, "");
  }

  public String getBusinessPhone()
  {
    return businessPhone;
  }

  public String getEmail()
  {
    return email;
  }

  public Locale getContentLocale() {
    LOG.log(Level.FINEST, "Getting content locale for '" + this.userName + "'.");
    return contentLocale;
  }


  public String getFaxPhone()
  {
    return faxPhone;
  }

  public String getGivenName()
  {
    return givenName;
  }

  public String getHomePhone()
  {
    return homePhone;
  }


  public String getMobilePhone()
  {
    return mobilePhone;
  }

  public String getPagerPhone()
  {
    return pagerPhone;
  }

  public String getPostalAddress()
  {
    return postalAddress;
  }

  public Locale getProductLocale() {
    LOG.log(Level.FINEST, "Getting content locale for '" + this.userName + "'.");
    return productLocale;
  }


  public String getSurname()
  {
    return surname;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setBusinessPhone(String theBusinessPhone)
  {
    businessPhone = theBusinessPhone;
  }


  public void setContentLocale(Locale theContentLocale)
  {
    contentLocale = theContentLocale;
  }


  public void setEmail(String theEmail)
  {
    email = theEmail;
  }

  public void setFaxPhone(String theFaxPhone)
  {
    faxPhone = theFaxPhone;
  }


  public void setGivenName(String theGivenName)
  {
    givenName = theGivenName;
  }


  public void setHomePhone(String theHomephone)
  {
    homePhone = theHomephone;
  }


  public void setMobilePhone(String theMobilePhone)
  {
    mobilePhone = theMobilePhone;
  }

  public void setPagerPhone(String thePagerPhone)
  {
    pagerPhone = thePagerPhone;
  }

  public void setPostalAddress(String thePostalAddress)
  {
    postalAddress = thePostalAddress;
  }


  public void setProductLocale(Locale theProductLocale)
  {
    productLocale = theProductLocale;
  }

  public void setSurname(String theSurname)
  {
    surname = theSurname;
  }

  public void setUserName(String theUsername)
  {
    userName = theUsername;
  }

  public String[] getCustomPropertyNames()
  {
    if (customProperties != null)
    {
      Set keySet = this.customProperties.keySet();
      return (String[]) keySet.toArray(new String[keySet.size()]);
    }
    return null;
  }

  public String[] getCustomPropertyValue(String theName)
  {
    if (customProperties != null)
    {
      Vector v = (Vector) this.customProperties.get(theName);
      if (v != null)
      {
        return (String[]) v.toArray(new String[v.size()]);
      }
    }
    return null;
  }


  public void addCustomProperty(String theName, String theValue)
  {
    if (customProperties == null)
    {
      customProperties = new HashMap();
    }
    Vector v = (Vector) this.customProperties.get(theName);
    if (v == null)
    {
      v = new Vector();
      this.customProperties.put(theName, v);
    }
    v.add(theValue);
  }


}
