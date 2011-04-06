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
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.IAccount;
import com.cognos.CAM_AAA.authentication.IBiBusHeader;
import com.cognos.CAM_AAA.authentication.ICredential;
import com.cognos.CAM_AAA.authentication.IGroup;
import com.cognos.CAM_AAA.authentication.IRole;
import com.cognos.CAM_AAA.authentication.ITrustedCredential;
import com.cognos.CAM_AAA.authentication.IVisa;
import com.cognos.CAM_AAA.authentication.SystemRecoverableException;
import com.cognos.CAM_AAA.authentication.UnrecoverableException;
import com.cognos.CAM_AAA.authentication.UserRecoverableException;


public class CognosShibAuthVisa implements IVisa
{
	/**
	 * 
	 */
	public CognosShibAuthVisa()
	{
		super();
		roles = null;
		groups = null;
	}


	/**
	 * @param theAccount
	 * @throws UnrecoverableException
	 */
	public void init(IAccount theAccount) throws UnrecoverableException
	{
		account = theAccount;
	}


	/**
	 * @throws UnrecoverableException
	 */
	public void destroy() throws UnrecoverableException
	{
		roles = null;
		groups = null;
		account = null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IVisa#generateTrustedCredential(com.cognos.CAM_AAA.authentication.IBiBusHeader)
	 */
	public ITrustedCredential generateTrustedCredential(
			IBiBusHeader theAuthRequest) throws UserRecoverableException,
			SystemRecoverableException, UnrecoverableException
	{
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IVisa#generateCredential(com.cognos.CAM_AAA.authentication.IBiBusHeader)
	 */
	public ICredential generateCredential(IBiBusHeader theAuthRequest)
			throws UserRecoverableException, SystemRecoverableException,
			UnrecoverableException
	{
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IVisa#isValid()
	 */
	public boolean isValid()
	{
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IVisa#getAccount()
	 */
	public IAccount getAccount()
	{
		return account;
	}


	/**
	 * @param theGroup
	 */
	public void addGroup(IGroup theGroup)
	{
		if (groups == null)
		{
			groups = new Vector();
		}
		groups.add(theGroup);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IVisa#getGroups()
	 */
	public IGroup[] getGroups()
	{
		if (groups != null)
		{
			IGroup[] array = new IGroup[groups.size()];
			return (IGroup[]) groups.toArray(array);
		}
		return null;
	}


	/**
	 * @param theRole
	 */
	public void addRole(IRole theRole)
	{
		if (roles == null)
		{
			roles = new Vector();
		}
		roles.add(theRole);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IVisa#getRoles()
	 */
	public IRole[] getRoles()
	{
		if (roles != null)
		{
			IRole[] array = new IRole[roles.size()];
			return (IRole[]) roles.toArray(array);
		}
		return null;
	}

	private Vector		roles;
	private Vector		groups;
	private IAccount	account;
}
