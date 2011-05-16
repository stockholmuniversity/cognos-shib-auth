package se.su.it.cognos.cognosshibauth

import com.cognos.CAM_AAA.authentication.INamespaceAuthenticationProvider2
import com.cognos.CAM_AAA.authentication.IVisa
import com.cognos.CAM_AAA.authentication.IBiBusHeader2
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import com.cognos.CAM_AAA.authentication.UserRecoverableException
import com.cognos.CAM_AAA.authentication.UnrecoverableException
import se.su.it.cognos.cognosshibauth.visa.Visa
import se.su.it.cognos.cognosshibauth.ldap.Account
import com.cognos.CAM_AAA.authentication.SystemRecoverableException
import java.util.logging.Level
import java.util.logging.Logger
import se.su.it.cognos.cognosshibauth.ldap.Role

/**
 * User: Joakim Lundin (joakim.lundin@it.su.se)
 * Date: 2011-05-09
 * Time: 06:55
 */
class CognosShibAP extends CognosShibAuthBase implements INamespaceAuthenticationProvider2 {

  private Logger LOG = Logger.getLogger(CognosShibAP.class.getName());

  @Override
  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException {
    Visa visa = new Visa(configHandler)

    String remoteUser = getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true)
    remoteUser = remoteUser?.replaceAll(/@.*/, "")

    Account account = Account.findByUid(remoteUser)
    visa.init account

    return visa;
  }

  private String[] getHeaderValues(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {

    if(header == null || header.trim().length() == 0)
      return new String[0];

    String[] headerValue = iBiBusHeader2.getTrustedEnvVarValue(header); //TODO: Use getTrustedEnvVarValue when releasing stable.

    if(headerValue == null) {
      LOG.log(Level.INFO, "Header '" + header + "' not found.");
      if(required) { // Value not found in trusted environment variables.
        LOG.log(Level.SEVERE, "Header '" + header + "' required but not found, throwing SystemRecoverableException");
        throw new SystemRecoverableException("Missing required header '" + header + "'.", header);
      }
    }
    else {
      String values = "";
      headerValue.each { s ->
        values += s + ", "
      }
      LOG.log(Level.FINEST, "Values in '" + header + "': " + values);

      if(headerValue.length < 1)
        headerValue = null;
    }

    if(headerValue != null)
      return headerValue;
    return new String[0];
  }

  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {
    String[] headerValues = getHeaderValues(iBiBusHeader2, header, required);

    if(headerValues != null && headerValues.length > 0)
      return headerValues[0];
    return null;
  }
}
