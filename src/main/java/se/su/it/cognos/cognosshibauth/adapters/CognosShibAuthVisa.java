
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


  public ITrustedCredential generateTrustedCredential(
          IBiBusHeader theAuthRequest) throws UserRecoverableException,
          SystemRecoverableException, UnrecoverableException
  {
    return null;
  }

  public ICredential generateCredential(IBiBusHeader theAuthRequest)
          throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException
  {
    return null;
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
