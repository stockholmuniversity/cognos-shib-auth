package se.su.it.cognos.cognosshibauth.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-06
 * Time: 07:04
 * To change this template use File | Settings | File Templates.
 */
public class ConfigHandler {

  private static ConfigHandler _instance = null;
  private static String CONFIG_FILENAME = "shib_auth.xml";
  private Logger LOG = Logger.getLogger(ConfigHandler.class.getName());

  private XMLConfiguration config = new XMLConfiguration();

  private String headerRemoteUser = "REMOTE_USER";
  private String headerGivenName = "";
  private String headerSurname = "";
  private String headerEmail = "";
  private String headerBusinessPhone = "";
  private String headerHomePhone = "";
  private String headerMobilePhone = "";
  private String headerFaxPhone = "";
  private String headerPagerPhone = "";
  private String headerPostalAddress = "";

  protected ConfigHandler() {
    config.setFileName(CONFIG_FILENAME);
    try {
      config.load();
    } catch (ConfigurationException e) {
      LOG.log(Level.SEVERE, "Failed to load configuration from file \"" +  config.getFileName() + "\".");
    }
    config.setReloadingStrategy(new FileChangedReloadingStrategy());

    load_header_values();
  }

  public static ConfigHandler instance() {
    if(_instance == null) {
      _instance = new ConfigHandler();
    }
    return _instance;
  }

  private void load_header_values() {
    headerRemoteUser =    config.getString("headers.remote_user", headerRemoteUser);
    headerGivenName =     config.getString("headers.given_name", headerGivenName);
    headerSurname =        config.getString("headers.surname", headerSurname);
    headerEmail =          config.getString("headers.email", headerEmail);
    headerBusinessPhone = config.getString("headers.business_phone", headerBusinessPhone);
    headerHomePhone =     config.getString("headers.home_phone", headerHomePhone);
    headerMobilePhone =   config.getString("headers.mobile_phone", headerMobilePhone);
    headerFaxPhone =      config.getString("headers.fax_phone", headerFaxPhone);
    headerPagerPhone =    config.getString("headers.pager_phone", headerPagerPhone);
    headerPostalAddress = config.getString("headers.postal_address", headerPostalAddress);
  }

  public String getHeaderRemoteUser() {
    return headerRemoteUser;
  }

  public String getHeaderGivenName() {
    return headerGivenName;
  }

  public String getHeaderSurname() {
    return headerSurname;
  }

  public String getHeaderEmail() {
    return headerEmail;
  }

  public String getHeaderBusinessPhone() {
    return headerBusinessPhone;
  }

  public String getHeaderHomePhone() {
    return headerHomePhone;
  }

  public String getHeaderMobilePhone() {
    return headerMobilePhone;
  }

  public String getHeaderFaxPhone() {
    return headerFaxPhone;
  }

  public String getHeaderPagerPhone() {
    return headerPagerPhone;
  }

  public String getHeaderPostalAddress() {
    return headerPostalAddress;
  }
}
