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
import java.util.Set;
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.ICredential;


public class CognosShibAuthCredential implements ICredential
{
	/**
	 * 
	 */
	public CognosShibAuthCredential()
	{
		super();
		credentials = null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.ICredential#getCredentialNames()
	 */
	public String[] getCredentialNames()
	{
		if (credentials != null)
		{
			Set keySet = credentials.keySet();
			String[] array = new String[keySet.size()];
			return (String[]) keySet.toArray(array);
		}
		return null;
	}


	/**
	 * @param theName
	 * @param theValue
	 */
	public void addCredentialValue(String theName, String theValue)
	{
		if (credentials == null)
		{
			credentials = new HashMap();
		}
		Vector v = (Vector) this.credentials.get(theName);
		if (v == null)
		{
			v = new Vector();
			this.credentials.put(theName, v);
		}
		v.add(theValue);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.ICredential#getCredentialValue(java.lang.String)
	 */
	public String[] getCredentialValue(String theName)
	{
		if (credentials != null)
		{
			Vector v = (Vector) this.credentials.get(theName);
			if (v != null)
			{
				return (String[]) v.toArray(new String[v.size()]);
			}
		}
		return null;
	}

	private HashMap	credentials;
}
