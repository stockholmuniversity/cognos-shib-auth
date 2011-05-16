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

  /**
   * Starts the "authentication dance" and gets header values from TrustedEnvVar in IBiBusHeader2.
   *
   * @param iBiBusHeader2 the IBiBusHeader2 object
   * @param header the header name
   * @param required true if this header should generate a SystemRecoverableException and start the
   * "authentication dance"
   * @return the value of the header
   * @throws SystemRecoverableException thrown to start the "authentication dance" if the header value is null
   */
  private String[] getHeaderValues(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {

    header = header?.trim()

    String[] headerValue = iBiBusHeader2.getTrustedEnvVarValue(header);

    if(headerValue == null) {
      LOG.log(Level.INFO, "Header '" + header + "' not found.");

      if(required) { // Start the "authentication dance"
        LOG.log(Level.SEVERE, "Header '${header}' required but not found, throwing SystemRecoverableException");
        throw new SystemRecoverableException("Missing required header '${header}'.", header);
      }
    }

    LOG.log(Level.FINEST, "Values in '${header}': " + headerValue?.join(","));

    headerValue
  }

  /**
   * Starts the "authentication dance" and gets header value from TrustedEnvVar in IBiBusHeader2.
   *
   * @param iBiBusHeader2 the IBiBusHeader2 object
   * @param header the header name
   * @param required true if this header should generate a SystemRecoverableException and start the
   * "authentication dance"
   * @return the value of the header
   * @throws SystemRecoverableException thrown to start the "authentication dance" if the header value is null
   */
  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {
    String[] values = getHeaderValues(iBiBusHeader2, header, required);

    values.empty ? null : values?.first()
  }
}
