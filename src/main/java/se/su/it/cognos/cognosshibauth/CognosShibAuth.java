/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-04
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

import com.cognos.CAM_AAA.authentication.SystemRecoverableException;
import com.cognos.CAM_AAA.authentication.UnrecoverableException;
import com.cognos.CAM_AAA.authentication.UserRecoverableException;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthAccount;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthVisa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CognosShibAuth implements INamespaceAuthenticationProvider2 {

  String namespaceFormat = null;
  String capabilities[] = null;
  String objectId = null;
  private Logger LOG = Logger.getLogger(ConfigHandler.class.getName());

  private ConfigHandler configHandler = null;

  public CognosShibAuth() {
    configHandler = ConfigHandler.instance();
  }

  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException {
    CognosShibAuthVisa cognosShibAuthVisa = new CognosShibAuthVisa();

    // TODO: make the required part configurable.
    String remoteUser = getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true);
    CognosShibAuthAccount cognosShibAuthAccount = new CognosShibAuthAccount("u:" + remoteUser);
    cognosShibAuthAccount.setUserName(remoteUser);
    LOG.log(Level.FINE, "Username '" + remoteUser + "' set from " + configHandler.getHeaderRemoteUser());

    String givenName = getHeaderValue(iBiBusHeader2, configHandler.getHeaderGivenName(), false);
    cognosShibAuthAccount.setGivenName(givenName);

    String surname = getHeaderValue(iBiBusHeader2, configHandler.getHeaderSurname(), false);
    cognosShibAuthAccount.setSurname(surname);

    String email = getHeaderValue(iBiBusHeader2, configHandler.getHeaderEmail(), false);
    cognosShibAuthAccount.setEmail(email);

    String businessPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderBusinessPhone(), false);
    cognosShibAuthAccount.setBusinessPhone(businessPhone);

    String homePhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderHomePhone(), false);
    cognosShibAuthAccount.setHomePhone(homePhone);

    String mobilePhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderMobilePhone(), false);
    cognosShibAuthAccount.setMobilePhone(mobilePhone);

    String faxPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderFaxPhone(), false);
    cognosShibAuthAccount.setFaxPhone(faxPhone);

    String pagerPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderPagerPhone(), false);
    cognosShibAuthAccount.setPagerPhone(pagerPhone);

    String postalAddress = getHeaderValue(iBiBusHeader2, configHandler.getHeaderPostalAddress(), false);
    cognosShibAuthAccount.setPostalAddress(postalAddress);

    cognosShibAuthVisa.init(cognosShibAuthAccount);

    return cognosShibAuthVisa;
  }

  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required) throws SystemRecoverableException {
    String[] headerValue = iBiBusHeader2.getTrustedEnvVarValue(header);

    if (headerValue == null && required) { // Value not found in trusted environment variables.
      LOG.log(Level.SEVERE, "Header '" + header + "' not found in TrustedEnvVar, throwing SystemRecoverableException");
      throw new SystemRecoverableException("Missing required header '" + header + "'.", header);
    }

    return headerValue[0];
  }

  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    // TODO: Implement something smart.
  }

  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {
    CognosShibAuthVisa visa = (CognosShibAuthVisa) iVisa;
    QueryResult result = new QueryResult();
    try{
      ISearchExpression expression = iQuery.getSearchExpression();
      String objectID = expression.getObjectID();
      ISearchStep[] steps = expression.getSteps();
      // It doesn't make sense to have multiple steps for this provider
      // since the objects are not hierarchical.
      if (steps.length != 1){
        throw new UnrecoverableException(
                "Internal Error",
                "Invalid search expression. Multiple steps is not supported for this namespace.");
      }



    }
    catch (Exception e){
      e.printStackTrace();
    }

    return result;
  }

  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    LOG.log(Level.FINEST, "intit method reached");

    objectId = iNamespaceConfiguration.getID();
    LOG.log(Level.FINE, "ObjectID set to '" + objectId + "'.");


    //TODO: Make these configurable.
    capabilities = new String[6];
    capabilities[0] = CapabilityCaseSensitive;
    capabilities[1] = CapabilityContains;
    capabilities[2] = CapabilityEquals;
    capabilities[3] = CapabilitySort;
    capabilities[4] = CapabilityStartsWith;
    capabilities[5] = CapabilityEndsWith;
  }

  public void destroy() {}

  public String getNamespaceFormat() {
    return namespaceFormat;
  }

  public void setNamespaceFormat(String s) throws UnrecoverableException {
    namespaceFormat = s;
  }

  public String[] getCapabilities() {
    return capabilities;
  }

  public String getDescription(Locale locale) {
    return configHandler.getDescription(locale);
  }

  public Locale[] getAvailableDescriptionLocales() {
    List<Locale> locales = configHandler.getDescriptionLocales();
    return locales.toArray(new Locale[locales.size()]);
  }

  public IBaseClass[] getAncestors() {
    // TODO: Implement something smart.
    return null;
  }

  public boolean getHasChildren() {
    return false;
  }

  public String getName(Locale locale) {
    return configHandler.getName(locale);
  }

  public Locale[] getAvailableNameLocales() {
    List<Locale> locales = configHandler.getDescriptionLocales();
    return locales.toArray(new Locale[locales.size()]);
  }

  public String getObjectID() {
    return objectId;
  }
}
