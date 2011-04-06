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
import com.cognos.CAM_AAA.authentication.INamespaceFolder;


public class CognosShibAuthNamespaceFolder extends UiClass implements INamespaceFolder
{
	/**
	 * @param theSearchPath
	 */
	public CognosShibAuthNamespaceFolder(String theSearchPath)
	{
		super(theSearchPath);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cognos.CAM_AAA.authentication.IBaseClass#getHasChildren()
	 */
	public boolean getHasChildren()
	{
		return true;
	}
}
