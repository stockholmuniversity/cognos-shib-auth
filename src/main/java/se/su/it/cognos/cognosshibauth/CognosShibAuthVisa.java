package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-05
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */

public class CognosShibAuthVisa implements IVisa {

    private ICredential credential;
    private ITrustedCredential trustedCredential;
    private IAccount account;
    private IGroup[] groups;
    private IRole[] roles;

  public CognosShibAuthVisa(IAccount iAccount, IGroup iGroup, IRole iRole){


  }



  public ITrustedCredential generateTrustedCredential(IBiBusHeader iBiBusHeader) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {
    return trustedCredential;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public ICredential generateCredential(IBiBusHeader iBiBusHeader) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {

    return credential;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isValid() {
    return true;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public IAccount getAccount() {
    return account;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public IGroup[] getGroups() {
    return groups;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public IRole[] getRoles() {
    return roles;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setCredential(ICredential iCredential) {
    this.credential = iCredential;
  }

  public void setTrustedCredential(ITrustedCredential iTrustedCredential) {
    this.trustedCredential = iTrustedCredential;
  }

  public void setAccount(IAccount iAccount) {
    this.account = iAccount;
  }

  public void setGroups(IGroup[] groups) {
    this.groups = groups;
  }

  public void setRoles(IRole[] roles) {
    this.roles = roles;
  }
}
