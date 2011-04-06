package se.su.it.cognos.cognosshibauth.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.rmi.Remote;
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
  private Logger LOG = new Logger.getLogger(ConfigHandler.class.getName());

  private XMLConfiguration config = new XMLConfiguration();

  private String HEADER_REMOTE_USER = "REMOTE_USER";
  private String HEADER_GIVEN_NAME = "";
  private String HEADER_SURNAME = "";
  private String HEADER_EMAIL = "";
  private String HEADER_BUSINESS_PHONE = "";
  private String HEADER_HOME_PHONE = "";
  private String HEADER_MOBILE_PHONE = "";
  private String HEADER_FAX_PHONE = "";
  private String HEADER_PAGER_PHONE = "";
  private String HEADER_POSTAL_ADDRESS = "";

  protected ConfigHandler() {
    config.setFileName(CONFIG_FILENAME);
    try {
      config.load();
    } catch (ConfigurationException e) {
      LOG.log(Level.SEVERE, "Failed to load configuration from file \"" +  config.getFileName() + "\".");
    }
    config.setReloadingStrategy(new FileChangedReloadingStrategy());

    HEADER_REMOTE_USER = config.getString("headers.remote_user", HEADER_REMOTE_USER);
    HEADER_GIVEN_NAME = config.getString("headers.given_name", HEADER_GIVEN_NAME);
    HEADER_SURNAME = config.getString("headers.surname", HEADER_SURNAME);
    HEADER_EMAIL = config.getString("headers.email", HEADER_EMAIL);
    HEADER_BUSINESS_PHONE = config.getString("headers.business_phone", HEADER_BUSINESS_PHONE);
    HEADER_HOME_PHONE = config.getString("headers.home_phone", HEADER_HOME_PHONE);
    HEADER_MOBILE_PHONE = config.getString("headers.mobile_phone", HEADER_MOBILE_PHONE);
    HEADER_FAX_PHONE = config.getString("headers.fax_phone", HEADER_FAX_PHONE);
    HEADER_PAGER_PHONE = config.getString("headers.pager_phone", HEADER_PAGER_PHONE);
    HEADER_POSTAL_ADDRESS = config.getString("headers.postal_address", HEADER_POSTAL_ADDRESS);
  }

  public static ConfigHandler instance() {
    if(_instance == null) {
      _instance = new ConfigHandler();
    }
    return _instance;
  }
}
