/**
 * Licensed Materials - Property of IBM
 * 
 * IBM Cognos Products: CAMAAA
 * 
 * (C) Copyright IBM Corp. 2005, 2010
 * 
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.IAccount;


public class Account extends UiClass implements IAccount
{
	/**
	 * Creates a IBM Cognos 8 Account
	 * 
	 * @param theObjectID
	 *            The unique identifier for the account.
	 */
	public Account(String theObjectID)
	{
		super(theObjectID);
		businessPhone = null;
		email = null;
		contentLocale = null;
		productLocale = null;
		faxPhone = null;
		givenName = null;
		homePhone = null;
		mobilePhone = null;
		pagerPhone = null;
		postalAddress = null;
		surname = null;
		userName = null;
		customProperties = null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getBusinessPhone()
	 */
	public String getBusinessPhone()
	{
		return businessPhone;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getEmail()
	 */
	public String getEmail()
	{
		return email;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getContentLocale()
	 */
	public Locale getContentLocale()
	{
		return contentLocale;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getFaxPhone()
	 */
	public String getFaxPhone()
	{
		return faxPhone;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getGivenName()
	 */
	public String getGivenName()
	{
		return givenName;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getHomePhone()
	 */
	public String getHomePhone()
	{
		return homePhone;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getMobilePhone()
	 */
	public String getMobilePhone()
	{
		return mobilePhone;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getPagerPhone()
	 */
	public String getPagerPhone()
	{
		return pagerPhone;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getPostalAddress()
	 */
	public String getPostalAddress()
	{
		return postalAddress;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getProductLocale()
	 */
	public Locale getProductLocale()
	{
		return productLocale;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getSurname()
	 */
	public String getSurname()
	{
		return surname;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getUserName()
	 */
	public String getUserName()
	{
		return userName;
	}


	/**
	 * Sets the business phone property.
	 * 
	 * @param theBusinessPhone
	 *            The business phone number value.
	 */
	public void setBusinessPhone(String theBusinessPhone)
	{
		businessPhone = theBusinessPhone;
	}


	/**
	 * Sets the content locale property.
	 * 
	 * @param theContentLocale
	 *            The content locale value.
	 */
	public void setContentLocale(Locale theContentLocale)
	{
		contentLocale = theContentLocale;
	}


	/**
	 * Sets the email property.
	 * 
	 * @param theEmail
	 *            The email value.
	 */
	public void setEmail(String theEmail)
	{
		email = theEmail;
	}


	/**
	 * Sets the fax phone number property.
	 * 
	 * @param theFaxPhone
	 *            The fax phone number value.
	 */
	public void setFaxPhone(String theFaxPhone)
	{
		faxPhone = theFaxPhone;
	}


	/**
	 * Sets the given name property.
	 * 
	 * @param theGivenName
	 *            The given name value.
	 */
	public void setGivenName(String theGivenName)
	{
		givenName = theGivenName;
	}


	/**
	 * Sets the home phone number property.
	 * 
	 * @param theHomephone
	 *            The home phone number value.
	 */
	public void setHomePhone(String theHomephone)
	{
		homePhone = theHomephone;
	}


	/**
	 * Sets the mobile phone number property.
	 * 
	 * @param theMobilePhone
	 *            The mobile phone number value.
	 */
	public void setMobilePhone(String theMobilePhone)
	{
		mobilePhone = theMobilePhone;
	}


	/**
	 * Sets the pager phone number property.
	 * 
	 * @param thePagerPhone
	 *            The pager phone number value.
	 */
	public void setPagerPhone(String thePagerPhone)
	{
		pagerPhone = thePagerPhone;
	}


	/**
	 * Sets the postal address property.
	 * 
	 * @param thePostalAddress
	 *            The postal address value.
	 */
	public void setPostalAddress(String thePostalAddress)
	{
		postalAddress = thePostalAddress;
	}


	/**
	 * @param theProductLocale
	 */
	public void setProductLocale(Locale theProductLocale)
	{
		productLocale = theProductLocale;
	}


	/**
	 * @param theSurname
	 */
	public void setSurname(String theSurname)
	{
		surname = theSurname;
	}


	/**
	 * @param theUsername
	 */
	public void setUserName(String theUsername)
	{
		userName = theUsername;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getCustomPropertyNames()
	 */
	public String[] getCustomPropertyNames()
	{
		if (customProperties != null)
		{
			Set keySet = this.customProperties.keySet();
			return (String[]) keySet.toArray(new String[keySet.size()]);
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IAccount#getCustomPropertyValue(java.lang.String)
	 */
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


	/**
	 * Adds a custom property for an account.
	 * 
	 * @param theName
	 *            The name of the property to add.
	 * @param theValue
	 *            The value of the property to add.
	 */
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
}
