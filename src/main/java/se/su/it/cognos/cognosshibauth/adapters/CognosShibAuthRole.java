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

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;


public class CognosShibAuthRole extends UiClass implements IRole
{
	/**
	 * @param theSearchPath
	 */
	public CognosShibAuthRole(String theSearchPath)
	{
		super(theSearchPath);
		members = null;
	}


	/**
	 * @param theMember
	 */
	public void addMember(IBaseClass theMember)
	{
		if (members == null)
		{
			members = new Vector();
		}
		members.add(theMember);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IRole#getMembers()
	 */
	public IBaseClass[] getMembers()
	{
		if (members != null)
		{
			IBaseClass[] array = new IBaseClass[members.size()];
			return (IBaseClass[]) members.toArray(array);
		}
		return null;
	}

	private Vector	members;
}
