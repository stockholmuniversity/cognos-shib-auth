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

import java.util.Locale;

public class CognosShibAuth implements INamespaceAuthenticationProvider2 {

  String namespaceFormat = null;
  String capabilities[] = null;
  String objectId = null;

  private ConfigHandler configHandler = null;

  public CognosShibAuth() {
    configHandler = ConfigHandler.instance();
  }

  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {

    CognosShibAuthVisa cognosShibAuthVisa = new CognosShibAuthVisa();
    CognosShibAuthAccount cognosShibAuthAccount = new CognosShibAuthAccount(objectId);

    // TODO: make the required part configurable.
    String remoteUser = getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true);
    cognosShibAuthAccount.setUserName(remoteUser);
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

  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required) throws UnrecoverableException {
    // 1 - Look for trusted credentials
    String[] headerValue = iBiBusHeader2.getTrustedCredentialValue("header");

    if (headerValue == null) { // 2 - Look for credentials coming from SDK request
      headerValue = iBiBusHeader2.getCredentialValue("header");
    }

    if (headerValue == null) { // 3 - Look for credentials in environment
      headerValue = iBiBusHeader2.getEnvVarValue("header");
    }

    if (headerValue == null && required) { // Value not found at all
      throw new UnrecoverableException("Header \"" + header + "\" not found.",
              "Missing required header \"" + header + "\".");
    }

    return headerValue.length > 0 ? headerValue[0] : null;
  }

  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    // TODO: Implement something smart.
  }

  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {
    return null;
  }

  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    objectId = iNamespaceConfiguration.getID();

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
    // TODO: Implement something smart.
    return "";
  }

  public Locale[] getAvailableDescriptionLocales() {
    // TODO: Implement something smart.
    return new Locale[]{Locale.ENGLISH};
  }

  public IBaseClass[] getAncestors() {
    // TODO: Implement something smart.
    return null;
  }

  public boolean getHasChildren() {
    return false;
  }

  public String getName(Locale locale) {
    // TODO: Implement something smart.
    return "";
  }

  public Locale[] getAvailableNameLocales() {
    // TODO: Implement something smart.
    return new Locale[]{Locale.ENGLISH};
  }

  public String getObjectID() {
    return objectId;
  }
}
