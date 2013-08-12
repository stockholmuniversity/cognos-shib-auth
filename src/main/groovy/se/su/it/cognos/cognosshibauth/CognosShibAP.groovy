/*
 * Copyright (c) 2013, IT Services, Stockholm University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Stockholm University nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package se.su.it.cognos.cognosshibauth

import java.util.logging.Level
import java.util.logging.Logger
import se.su.it.cognos.cognosshibauth.ldap.Account
import se.su.it.cognos.cognosshibauth.visa.Visa
import com.cognos.CAM_AAA.authentication.*

/**
 * User: Joakim Lundin (joakim.lundin@it.su.se)
 * Date: 2011-05-09
 * Time: 06:55
 */
class CognosShibAP extends CognosShibAuthBase implements INamespaceAuthenticationProvider2 {

  private Logger LOG = Logger.getLogger(CognosShibAP.class.getName())

  @Override
  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException {
    Visa visa = new Visa(configHandler)

    String remoteUser = getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true)
    remoteUser = remoteUser?.replaceAll(/@.*/, "")

    Account account = Account.findByUid(remoteUser)
    visa.init account

    // Add users roles to visa.
    account.roles?.each { role ->
      visa.addRole role
    }

    // Add users groups to visa.
    account.groups?.each { group ->
      visa.addGroup group
    }

    visa
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

    values?.first()
  }
}
