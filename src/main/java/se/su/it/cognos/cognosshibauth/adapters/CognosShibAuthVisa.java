
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.cognos.cognosshibauth.visa.VisaValidator;


public class CognosShibAuthVisa implements IVisa
{

  private Logger LOG = Logger.getLogger(CognosShibAuthVisa.class.getName());

  private Vector		roles;
  private Vector		groups;
  private IAccount	account;

  private VisaValidator visaValidator = null;

  private ConfigHandler configHandler = null;

  public CognosShibAuthVisa(ConfigHandler configHandler) {
    LOG.log(Level.FINEST, "Creating a Visa.");
    roles = null;
    groups = null;
    this.configHandler = configHandler;
  }

  public void init(IAccount theAccount) throws UnrecoverableException {
    account = theAccount;
    
    LOG.log(Level.FINEST, "Initing new account for '" + account.getUserName() + "'");

    String visaValidatorClassName = configHandler.getVisaValidatorClass();
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    try {
      visaValidator = (VisaValidator) classLoader.loadClass(visaValidatorClassName).newInstance();
    } catch (Exception e) {
      throw new UnrecoverableException("Failed to load visa validator.", e.getMessage());
    }
  }

  public void destroy() throws UnrecoverableException {
    LOG.log(Level.FINEST, "Destroying Visa for '" + account.getUserName() + "'.");
    roles = null;
    groups = null;
    account = null;
  }

  public ITrustedCredential generateTrustedCredential(     // implementera ordentligt senare...
          IBiBusHeader theAuthRequest) throws UserRecoverableException,
          SystemRecoverableException, UnrecoverableException
  {
    LOG.log(Level.FINEST, "Generating trusted credentials.");
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
    LOG.log(Level.FINEST, "Generating credentials.");
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
    LOG.log(Level.FINEST, "Checking isValid.");
    return true;
  }

  public IAccount getAccount() {
    LOG.log(Level.FINEST, "Getting account for '" + account.getUserName() + "'.");
    return account;
  }


  public void addGroup(IGroup theGroup) {
    LOG.log(Level.FINEST, "Adding group to Visa for '" + account.getUserName() + "'.");
    if (groups == null)
    {
      groups = new Vector();
    }
    groups.add(theGroup);
  }


  public IGroup[] getGroups() {
    LOG.log(Level.FINEST, "Getting groups from Visa for '" + account.getUserName() + "'.");
    if (groups != null)
    {
      IGroup[] array = new IGroup[groups.size()];
      return (IGroup[]) groups.toArray(array);
    }
    return null;
  }


  public void addRole(IRole theRole) {
    LOG.log(Level.FINEST, "Adding role to Visa for '" + account.getUserName() + "'.");
    if (roles == null)
    {
      roles = new Vector();
    }
    roles.add(theRole);
  }



  public IRole[] getRoles() {
    LOG.log(Level.FINEST, "Getting roles from Visa for '" + account.getUserName() + "'.");
    if (roles != null)
    {
      IRole[] array = new IRole[roles.size()];
      return (IRole[]) roles.toArray(array);
    }
    return null;
  }

}
