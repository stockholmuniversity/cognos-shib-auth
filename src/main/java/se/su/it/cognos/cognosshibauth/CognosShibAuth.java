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

import java.util.Locale;

public class CognosShibAuth implements INamespaceAuthenticationProvider2 {

  String namespaceFormat = null;
  String capabilities[] = new String[6];
  String objectId = null;

  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {

    CognosShibAuthVisa cognosShibAuthVisa = null;

    return cognosShibAuthVisa;
  }

  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    // TODO: Implement something smart.
  }

  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
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
