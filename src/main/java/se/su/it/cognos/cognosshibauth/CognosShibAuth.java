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
import se.su.it.cognos.cognosshibauth.adapters.Group;
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

    String remoteUser = getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true);
    String givenName = getHeaderValue(iBiBusHeader2, configHandler.getHeaderGivenName(), true);
    String surname = getHeaderValue(iBiBusHeader2, configHandler.getHeaderSurname(), true);
    Locale contentLocale = configHandler.getContentLocale();

    String entitlement = getHeaderValue(iBiBusHeader2, configHandler.getHeaderEntitlement(), false);

    Account account =
            new Account("u:" + remoteUser, remoteUser, givenName, surname, contentLocale);

    String mail = getHeaderValue(iBiBusHeader2, configHandler.getHeaderMail(), true);
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

    String gmaiRole = filterGmaiRole(entitlement);
    Role role = new Role("Cognos Shibb Authenticator:r:"+gmaiRole);
    role.addName(contentLocale, gmaiRole);
    visa.addRole(role);

    String gmaiGroup = filterGmaiDepartment(entitlement);
    Group group = new Group("\"Cognos Shibb Authenticator:g:"+gmaiGroup);
    group.addMember(account);
    visa.addGroup(group);

    visa.init(account);

    return visa;
  }

  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {
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

    return headerValue == null ? null : headerValue[0];
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
