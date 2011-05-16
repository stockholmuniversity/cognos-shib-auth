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

  /**
   * Creates a new Visa.
   *
   * @param configHandler the ConfigHandler used for this object.
   */
  Visa(ConfigHandler configHandler) {
    LOG.log(Level.FINEST, "Creating a Visa.");
    roles = null;
    groups = null;
    this.configHandler = configHandler;
  }

  /**
   * Inits the Visa.
   *
   * @param account the account
   * @throws UnrecoverableException thrown if something goes wrong, if say the VisaValidator fails to load.
   */
  void init(IAccount account) throws UnrecoverableException {
    this.account = account;
    
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

  /**
   * Destroys the Visa.
   */
  void destroy() {
    LOG.log(Level.FINEST, "Destroying Visa for '" + account.getUserName() + "'.");
    roles = null;
    groups = null;
    account = null;
    visaValidator.destroy();
  }

  @Override
  public ITrustedCredential generateTrustedCredential(IBiBusHeader theAuthRequest)
          throws UnrecoverableException {
    //TODO: Implement something smart
    LOG.log(Level.FINEST, "Generating trusted credentials.");
    return null;
  }

  @Override
  public ICredential generateCredential(IBiBusHeader theAuthRequest)
          throws UnrecoverableException {
    //TODO: Implement something smart
    LOG.log(Level.FINEST, "Generating credentials.");
    return null;
  }

  @Override
  public boolean isValid() {
    LOG.log(Level.FINEST, "Checking isValid.");
    return visaValidator.isValid();
  }

  @Override
  public IAccount getAccount() {
    LOG.log(Level.FINEST, "Getting account for '" + account.getUserName() + "'.");
    return account;
  }

  /**
   * Adds a group to the Visa.
   *
   * @param theGroup the group to add.
   */
  public void addGroup(IGroup theGroup) {
    LOG.log(Level.FINEST, "Adding group to Visa for '" + account.getUserName() + "'.");
    if (groups == null)
      groups = new ArrayList<IGroup>();
    groups.add(theGroup);
  }

  @Override
  public IGroup[] getGroups() {
    LOG.log(Level.FINEST, "Getting groups from Visa for '" + account.getUserName() + "'.");
    if (groups != null)
      return groups.toArray(new IGroup[groups.size()]);
    return null;
  }

  /**
   * Adds a role to the visa.
   *
   * @param theRole the role fto add.
   */
  public void addRole(IRole theRole) {
    LOG.log(Level.FINEST, "Adding role to Visa for '" + account.getUserName() + "'.");
    if (roles == null)
      roles = new ArrayList<IRole>();
    roles.add(theRole);
  }

  @Override
  public IRole[] getRoles() {
    LOG.log(Level.FINEST, "Getting roles from Visa for '" + account.getUserName() + "'.");
    if (roles != null)
      return roles.toArray(new IRole[roles.size()]);
    return null;
  }

}
