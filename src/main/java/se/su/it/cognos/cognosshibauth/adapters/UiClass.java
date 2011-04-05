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
import java.util.Stack;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IUiClass;


public class UiClass implements IUiClass
{
	/**
	 * @param theObjectID
	 */
	public UiClass(String theObjectID)
	{
		super();
		names = null;
		descriptions = null;
		ancestors = null;
		objectID = theObjectID;
	}


	/**
	 * @param theLocale
	 * @param theDescription
	 */
	public void addDescription(Locale theLocale, String theDescription)
	{
		if (descriptions == null)
		{
			descriptions = new HashMap();
		}
		descriptions.put(theLocale, theDescription);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IUiClass#getDescription(java.util.Locale)
	 */
	public String getDescription(Locale theLocale)
	{
		if (descriptions != null)
		{
			return (String) descriptions.get(theLocale);
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IUiClass#getAvailableDescriptionLocales()
	 */
	public Locale[] getAvailableDescriptionLocales()
	{
		if (descriptions != null)
		{
			Set keySet = descriptions.keySet();
			Locale[] array = new Locale[keySet.size()];
			return (Locale[]) keySet.toArray(array);
		}
		return null;
	}


	/**
	 * @param theAncestor
	 */
	public void addAncestors(IBaseClass theAncestor)
	{
		if (ancestors == null)
		{
			ancestors = new Stack();
		}
		ancestors.push(theAncestor);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IBaseClass#getAncestors()
	 */
	public IBaseClass[] getAncestors()
	{
		if (ancestors != null)
		{
			IBaseClass[] array = new IBaseClass[ancestors.size()];
			return (IBaseClass[]) ancestors.toArray(array);
		}
		return null;
	}


	/**
	 * @param theLocale
	 * @param theName
	 */
	public void addName(Locale theLocale, String theName)
	{
		if (names == null)
		{
			names = new HashMap();
		}
		names.put(theLocale, theName);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IBaseClass#getHasChildren()
	 */
	public boolean getHasChildren()
	{
		return false;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IBaseClass#getName(java.util.Locale)
	 */
	public String getName(Locale theLocale)
	{
		if (names != null)
		{
			return (String) names.get(theLocale);
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IBaseClass#getAvailableNameLocales()
	 */
	public Locale[] getAvailableNameLocales()
	{
		if (names != null)
		{
			Set keySet = names.keySet();
			Locale[] array = new Locale[keySet.size()];
			return (Locale[]) keySet.toArray(array);
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IBaseClass#getObjectID()
	 */
	public String getObjectID()
	{
		return objectID;
	}


	/**
	 * @param theObjectID
	 */
	protected void setObjectID(String theObjectID)
	{
		objectID = theObjectID;
	}

	private String	objectID;
	private HashMap	names;
	private HashMap	descriptions;
	private Stack	ancestors;
}
