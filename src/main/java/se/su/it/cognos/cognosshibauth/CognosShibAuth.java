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


  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {
    return null;
  }

  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void destroy() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getNamespaceFormat() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setNamespaceFormat(String s) throws UnrecoverableException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public String[] getCapabilities() {
    return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getDescription(Locale locale) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Locale[] getAvailableDescriptionLocales() {
    return new Locale[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public IBaseClass[] getAncestors() {
    return new IBaseClass[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean getHasChildren() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getName(Locale locale) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Locale[] getAvailableNameLocales() {
    return new Locale[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getObjectID() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
