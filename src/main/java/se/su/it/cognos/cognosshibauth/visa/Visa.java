package se.su.it.cognos.cognosshibauth.visa;

import com.cognos.CAM_AAA.authentication.*;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Visa implements IVisa {

  private final Logger LOG = Logger.getLogger(Visa.class.getName());

  private List<IRole> roles;
  private List<IGroup> groups;
  private IAccount account;

  private VisaValidator visaValidator = null;

  private ConfigHandler configHandler = null;

  public Visa(ConfigHandler configHandler) {
    LOG.log(Level.FINEST, "Creating a Visa.");
    roles = null;
    groups = null;
    this.configHandler = configHandler;
  }

  public void init(IAccount theAccount) throws UnrecoverableException {
    account = theAccount;
    
    LOG.log(Level.FINEST, "Initing new account for '" + account.getUserName() + "'");

    String visaValidatorClassName = configHandler.getVisaValidatorClass();
    ClassLoader classLoader = Visa.class.getClassLoader();

    try {
      visaValidator = (VisaValidator) classLoader.loadClass(visaValidatorClassName).newInstance();
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to load visa validator: " + e.getMessage() + " " + e.toString());
      for(StackTraceElement ste : e.getStackTrace())
        LOG.log(Level.SEVERE, "Failed to load visa validator: " + ste.getClassName() + " " + ste.getFileName() + " " + ste.getMethodName());
      e.printStackTrace();
      throw new UnrecoverableException("Failed to load visa validator.", e.getMessage());
    }

    visaValidator.init(account);
  }

  public void destroy() {
    LOG.log(Level.FINEST, "Destroying Visa for '" + account.getUserName() + "'.");
    roles = null;
    groups = null;
    account = null;
    visaValidator.destroy();
  }

  public ITrustedCredential generateTrustedCredential(IBiBusHeader theAuthRequest)
          throws UnrecoverableException {
    //TODO: Implement something smart
    LOG.log(Level.FINEST, "Generating trusted credentials.");
    return null;
  }

  public ICredential generateCredential(IBiBusHeader theAuthRequest)
          throws UnrecoverableException {
    //TODO: Implement something smart
    LOG.log(Level.FINEST, "Generating credentials.");
    return null;
  }

  public boolean isValid() {
    LOG.log(Level.FINEST, "Checking isValid.");
    return visaValidator.isValid();
  }

  public IAccount getAccount() {
    LOG.log(Level.FINEST, "Getting account for '" + account.getUserName() + "'.");
    return account;
  }

  public void addGroup(IGroup theGroup) {
    LOG.log(Level.FINEST, "Adding group to Visa for '" + account.getUserName() + "'.");
    if (groups == null)
      groups = new ArrayList<IGroup>();
    groups.add(theGroup);
  }

  public IGroup[] getGroups() {
    LOG.log(Level.FINEST, "Getting groups from Visa for '" + account.getUserName() + "'.");
    if (groups != null)
      return groups.toArray(new IGroup[groups.size()]);
    return null;
  }

  public void addRole(IRole theRole) {
    LOG.log(Level.FINEST, "Adding role to Visa for '" + account.getUserName() + "'.");
    if (roles == null)
      roles = new ArrayList<IRole>();
    roles.add(theRole);
  }

  public IRole[] getRoles() {
    LOG.log(Level.FINEST, "Getting roles from Visa for '" + account.getUserName() + "'.");
    if (roles != null)
      return roles.toArray(new IRole[roles.size()]);
    return null;
  }

}
