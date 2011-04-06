
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

  private Vector		roles;
  private Vector		groups;
  private IAccount	account;



  public CognosShibAuthVisa()
  {
    super();
    roles = null;
    groups = null;
  }

  public void init(IAccount theAccount) throws UnrecoverableException
  {
    account = theAccount;
  }


  public void destroy() throws UnrecoverableException
  {
    roles = null;
    groups = null;
    account = null;
  }


  public ITrustedCredential generateTrustedCredential(     // implementera ordentligt senare...
          IBiBusHeader theAuthRequest) throws UserRecoverableException,
          SystemRecoverableException, UnrecoverableException
  {
    boolean isValidCredentials = true;
    String[] theUsername = null;
    String[] thePassword = null;
    theUsername = theAuthRequest.getCredentialValue("username");
    if (theUsername == null && thePassword == null)
    {
       theUsername = new String[]{account.getUserName()};
    }
    else if (theUsername != null && theUsername.length == 1 && theUsername[0].equals(account.getUserName())){
      isValidCredentials = false; // nånBraKoll(theUsername[0]);
    }

    if (!isValidCredentials)
    {
      UserRecoverableException e = new UserRecoverableException(
              "Please type your credentials for authentication.",
              "The provided credentials are invalid.");
      throw e;
    }
    CognosShibAuthTrustedCredential tc = new CognosShibAuthTrustedCredential();
    tc.addCredentialValue("username", theUsername.toString());
    return tc;
  }

  public ICredential generateCredential(IBiBusHeader theAuthRequest)
          throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException
  {
    boolean validCredential = true; //nånBraKoll(account.getUserName());
    if(! validCredential){
      UnrecoverableException e = new UnrecoverableException(
              "Could not generate credentials for the user.",
              "Visa contains invalid credentials.");
      throw e;
    }
    else{
      CognosShibAuthCredential credentials = new CognosShibAuthCredential();
      credentials.addCredentialValue("username", account.getUserName());
      return credentials;
    }
  }

  public boolean isValid()
  {
    return true;
  }

  public IAccount getAccount()
  {
    return account;
  }


  public void addGroup(IGroup theGroup)
  {
    if (groups == null)
    {
      groups = new Vector();
    }
    groups.add(theGroup);
  }


  public IGroup[] getGroups()
  {
    if (groups != null)
    {
      IGroup[] array = new IGroup[groups.size()];
      return (IGroup[]) groups.toArray(array);
    }
    return null;
  }


  public void addRole(IRole theRole)
  {
    if (roles == null)
    {
      roles = new Vector();
    }
    roles.add(theRole);
  }



  public IRole[] getRoles()
  {
    if (roles != null)
    {
      IRole[] array = new IRole[roles.size()];
      return (IRole[]) roles.toArray(array);
    }
    return null;
  }

}
