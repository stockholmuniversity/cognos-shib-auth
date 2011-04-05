package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.IAccount;
import com.cognos.CAM_AAA.authentication.IBaseClass;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: hdrys
 * Date: 2011-04-05
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class CognosShibAuthAccount implements IAccount {

  private String businessPhone;
  private String email;
  private Locale contentLocale;
  private String faxPhone;
  private String givenName;
  private String homePhone;
  private String mobilePhone;
  private String pagerPhone;
  private String postalAddress;
  private Locale productLocale;
  private String surname;
  private String userName;
  private String[] customPropertyNames;
  private String[] customPropertyValue;
  private String description;
  private Locale[] availableDescriptionLocales;
  private IBaseClass[] ancestors;
  private String name;
  private Locale[] availableNameLocales;
  private String objectID;


  public String getBusinessPhone() {
    return businessPhone;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getEmail() {
    return email;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public Locale getContentLocale() {
    return contentLocale;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getFaxPhone() {
    return faxPhone;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getGivenName() {
    return givenName;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getHomePhone() {
    return homePhone;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getMobilePhone() {
    return mobilePhone;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getPagerPhone() {
    return pagerPhone;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getPostalAddress() {
    return postalAddress;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public Locale getProductLocale() {
    return productLocale;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getSurname() {
    return surname;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getUserName() {
    return userName;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String[] getCustomPropertyNames() {
    return customPropertyNames;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String[] getCustomPropertyValue(String s) {
    return customPropertyValue;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getDescription(Locale locale) {
    return description;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public Locale[] getAvailableDescriptionLocales() {
    return availableDescriptionLocales;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public IBaseClass[] getAncestors() {
    return ancestors;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public boolean getHasChildren() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getName(Locale locale) {
    return name;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public Locale[] getAvailableNameLocales() {
    return availableNameLocales;  //To change body of implemented methods use File | Settings | File Templates.
  }
  public String getObjectID() {
    return objectID;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setBusinessPhone(String businessPhone) {
    this.businessPhone = businessPhone;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setContentLocale(Locale contentLocale) {
    this.contentLocale = contentLocale;
  }

  public void setFaxPhone(String faxPhone) {
    this.faxPhone = faxPhone;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public void setPagerPhone(String pagerPhone) {
    this.pagerPhone = pagerPhone;
  }

  public void setPostalAddress(String postalAddress) {
    this.postalAddress = postalAddress;
  }

  public void setProductLocale(Locale productLocale) {
    this.productLocale = productLocale;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setCustomPropertyNames(String[] customPropertyNames) {
    this.customPropertyNames = customPropertyNames;
  }

  public void setCustomPropertyValue(String[] customPropertyValue) {
    this.customPropertyValue = customPropertyValue;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setAvailableDescriptionLocales(Locale[] availableDescriptionLocales) {
    this.availableDescriptionLocales = availableDescriptionLocales;
  }

  public void setAncestors(IBaseClass[] ancestors) {
    this.ancestors = ancestors;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAvailableNameLocales(Locale[] availableNameLocales) {
    this.availableNameLocales = availableNameLocales;
  }

  public void setObjectID(String objectID) {
    this.objectID = objectID;
  }
}
