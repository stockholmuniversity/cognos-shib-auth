/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-19
 * Time: 07:01
 * To change this template use File | Settings | File Templates.
 */
package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

import com.cognos.CAM_AAA.authentication.UnrecoverableException;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CognosShibAuthNamespace implements INamespace {

  static String namespaceId = null

  protected String namespaceFormat = null;
  protected String[] capabilities = null;
  private Logger LOG = Logger.getLogger(CognosShibAuthNamespace.class.getName());

  protected ConfigHandler configHandler = null;

  CognosShibAuthNamespace() {
    this.configHandler = ConfigHandler.instance();
  }

  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    LOG.log(Level.FINEST, "intit method reached");

    namespaceId = iNamespaceConfiguration.getID()
    LOG.log(Level.FINE, "ObjectID set to '" + namespaceId + "'.")

    List<String> capabilitiesList = configHandler.getCapabilities();
    capabilities = capabilitiesList.toArray(new String[capabilitiesList.size()]);
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
    return configHandler.getDescription(locale);
  }

  public Locale[] getAvailableDescriptionLocales() {
    List<Locale> locales = configHandler.getDescriptionLocales();
    return locales.toArray(new Locale[locales.size()]);
  }

  public IBaseClass[] getAncestors() {
    // TODO: Implement something smart.
    return null;
  }

  public boolean getHasChildren() {
    return false;
  }

  public String getName(Locale locale) {
    return configHandler.getName(locale);
  }

  public Locale[] getAvailableNameLocales() {
    List<Locale> locales = configHandler.getDescriptionLocales();
    return locales.toArray(new Locale[locales.size()]);
  }

  public String getObjectID() {
    return namespaceId;
  }
}

