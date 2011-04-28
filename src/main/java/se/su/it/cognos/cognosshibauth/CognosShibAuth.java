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
import se.su.it.cognos.cognosshibauth.adapters.Account;
import se.su.it.cognos.cognosshibauth.adapters.Role;
import se.su.it.cognos.cognosshibauth.visa.Visa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CognosShibAuth extends CognosShibAuthBase implements INamespaceAuthenticationProvider2 {

  private Logger LOG = Logger.getLogger(CognosShibAuth.class.getName());

  public CognosShibAuth() {
    super(ConfigHandler.instance());
  }

  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException {
    Visa visa = new Visa(configHandler);

    String remoteUser = "jolu";//getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true);
    String givenName = "Joakim";//getHeaderValue(iBiBusHeader2, configHandler.getHeaderGivenName(), true);
    String surname = "Lundin";//getHeaderValue(iBiBusHeader2, configHandler.getHeaderSurname(), true);
    Locale contentLocale = configHandler.getContentLocale();

    String[] entitlements = getHeaderValues(iBiBusHeader2, configHandler.getHeaderEntitlement(), false);

    Account account =
            new Account(namespaceId + ":" + "u:" + remoteUser, remoteUser, givenName, surname, contentLocale);

    String mail = "foo@su.se";//getHeaderValue(iBiBusHeader2, configHandler.getHeaderMail(), true);
    account.setEmail(mail);

    String businessPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderBusinessPhone(), false);
    account.setBusinessPhone(businessPhone);

    String homePhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderHomePhone(), false);
    account.setHomePhone(homePhone);

    String mobilePhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderMobilePhone(), false);
    account.setMobilePhone(mobilePhone);

    String faxPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderFaxPhone(), false);
    account.setFaxPhone(faxPhone);

    String pagerPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderPagerPhone(), false);
    account.setPagerPhone(pagerPhone);

    String postalAddress = getHeaderValue(iBiBusHeader2, configHandler.getHeaderPostalAddress(), false);
    account.setPostalAddress(postalAddress);

    visa.init(account);

    for(String entitlement : entitlements) {
      String roleName = parseRoleFromEntitlementUri(entitlement);
      Role role = new Role(namespaceId, roleName, contentLocale);
      visa.addRole(role);
    }

    return visa;
  }

  private String parseRoleFromEntitlementUri(String entitlement) {
    return null;  //TODO: Parse role from gmai uri and return role name.
  }

  private String[] getHeaderValues(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {
    
    if(header == null || header.trim().length() == 0)
      return null;

    String[] headerValue = iBiBusHeader2.getEnvVarValue(header); //TODO: Use getTrustedEnvVarValue when releasing stable.

    if(headerValue == null) {
      LOG.log(Level.INFO, "Header '" + header + "' not found.");
      if(required) { // Value not found in trusted environment variables.
        LOG.log(Level.SEVERE, "Header '" + header + "' required but not found, throwing SystemRecoverableException");
        throw new SystemRecoverableException("Missing required header '" + header + "'.", header);
      }
    }
    else {
      String values = "";
      for(String s : headerValue)
        values += s + ", ";
      LOG.log(Level.FINEST, "Values in '" + header + "': " + values);

      if(headerValue.length < 1)
        headerValue = null;
    }

    if(headerValue != null)
      return headerValue;
    return null;
  }



  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {
    String[] headerValues = getHeaderValues(iBiBusHeader2, header, required);

    if(headerValues != null && headerValues.length > 0)
      return headerValues[0];
    return null;
  }

  private String filterGmaiRole(String gmai){
    // urn:mace:swami.se:gmai:su-ivs:analyst:departmentNumber=647
    int startGmai = gmai.indexOf("su-ivs:") + 6;
    int stopGmai = gmai.lastIndexOf(":");
    String role = gmai.substring(startGmai, stopGmai);
    return role;
  }

  private String filterGmaiDepartment(String gmai){
    // urn:mace:swami.se:gmai:su-ivs:analyst:departmentNumber=647
    int startGmai = gmai.indexOf("departmentNumber=") + 17;
    String group = gmai.substring(startGmai);
    return group;
  }
}
