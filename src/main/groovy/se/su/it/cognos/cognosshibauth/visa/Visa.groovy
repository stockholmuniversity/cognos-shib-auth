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

package se.su.it.cognos.cognosshibauth.visa;

import com.cognos.CAM_AAA.authentication.*;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger
import se.su.it.cognos.cognosshibauth.ldap.Account
import se.su.it.cognos.cognosshibauth.adapters.TrustedCredential
import se.su.it.cognos.cognosshibauth.adapters.Credential;

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

    LOG.log(Level.FINEST, "Initing new account for '${account?.getUserName() ?: 'null'}'")

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

    visaValidator.init(this);
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
  ITrustedCredential generateTrustedCredential(IBiBusHeader theAuthRequest) throws UnrecoverableException {
    LOG.finest "Generating trusted credentials."

    if ( !this.valid ) {
      LOG.severe "Visa not valid for ${account?.userName}."

      throw new UnrecoverableException(
              "Could not generate credentials for the user '${account?.userName}'.",
              "Visa contains invalid credentials.")
    }

    TrustedCredential trustedCredential = new TrustedCredential()
    trustedCredential.addCredentialValue('username', account?.userName)
    trustedCredential
  }

  @Override
  ICredential generateCredential(IBiBusHeader theAuthRequest) throws UnrecoverableException {

    LOG.finest "Generating credentials for ${account?.userName}."

    if (! this.valid) {
      throw new UnrecoverableException(
              "Could not generate credentials for the user '${account?.userName}'.",
              "Visa contains invalid credentials.")
    }

    Credential credential = new Credential()
    credential.addCredentialValue('username', account?.userName)
    credential
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
