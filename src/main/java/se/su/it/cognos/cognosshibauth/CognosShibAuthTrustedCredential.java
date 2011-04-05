package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.ITrustedCredential;

/**
 * Created by IntelliJ IDEA.
 * User: hdrys
 * Date: 2011-04-05
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public class CognosShibAuthTrustedCredential implements ITrustedCredential {

  private String[] credentialNames;
  private String[] credentialValue;

  public String[] getCredentialNames() {
    return credentialNames;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String[] getCredentialValue(String s) {
    return credentialValue;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public void setCredentialNames(String[] credentialNames) {
    this.credentialNames = credentialNames;
  }

  public void setCredentialValue(String[] credentialValue) {
    this.credentialValue = credentialValue;
  }
}
