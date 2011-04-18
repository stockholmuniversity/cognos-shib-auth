package se.su.it.cognos.cognosshibauth.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
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
  private String headerMail = "";
  private String headerBusinessPhone = "";
  private String headerHomePhone = "";
  private String headerMobilePhone = "";
  private String headerFaxPhone = "";
  private String headerPagerPhone = "";
  private String headerPostalAddress = "";

  private Locale contentLocale = Locale.ENGLISH;
  private Locale procuctLocale = Locale.ENGLISH;

  protected ConfigHandler() {
    config.setFileName(CONFIG_FILENAME);
    try {
      config.load();
      LOG.log(Level.FINE, "Configuration loaded from '" +  config.getFileName() + "'.");
    } catch (ConfigurationException e) {
      LOG.log(Level.SEVERE, "Failed to load configuration from file '" +  config.getFileName() + "'.");
    }
    config.setReloadingStrategy(new FileChangedReloadingStrategy());

    load_header_values();

    contentLocale = load_locale("generic.content_locale", contentLocale);
    procuctLocale = load_locale("generic.product_locale", procuctLocale);
  }

  private Locale load_locale(String s, Locale defaultLocale) {
    String localeS = config.getString(s);
    Locale retLocale = defaultLocale;

    try {
      retLocale = new Locale(localeS);
      LOG.log(Level.FINEST, "Configured '" + s + "' = '" + retLocale + "'.");
    }
    catch(NullPointerException e) {
      LOG.log(Level.WARNING, "'" + s + "' not configured, falling back on default '" + defaultLocale.toString() + "'.");
    }

    return retLocale;
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
    headerSurname =       config.getString("headers.surname", headerSurname);
    headerMail =          config.getString("headers.mail", headerMail);
    headerBusinessPhone = config.getString("headers.business_phone", headerBusinessPhone);
    headerHomePhone =     config.getString("headers.home_phone", headerHomePhone);
    headerMobilePhone =   config.getString("headers.mobile_phone", headerMobilePhone);
    headerFaxPhone =      config.getString("headers.fax_phone", headerFaxPhone);
    headerPagerPhone =    config.getString("headers.pager_phone", headerPagerPhone);
    headerPostalAddress = config.getString("headers.postal_address", headerPostalAddress);
  }

  public List<String> getCapabilities() {
    List<String> list = config.getList("capabilities");
    return list;
  }

  public String getName(Locale locale) {
    String description = null;
    List<HierarchicalConfiguration> subnodeConfiguration =
            (List<HierarchicalConfiguration>) config.configurationsAt("names.name");

    for(HierarchicalConfiguration node : subnodeConfiguration) {
      Locale nodeLocale = new Locale(node.getString("locale"));
      if(nodeLocale.equals(locale)) {
        description = node.getString("text");
      }
    }
    return description;
  }

  public List<Locale> getNameLocales() {
    List<Locale> descriptionLocales = new ArrayList<Locale>();
    List<HierarchicalConfiguration> subnodeConfiguration =
            (List<HierarchicalConfiguration>) config.configurationsAt("names.name");

    for(HierarchicalConfiguration node : subnodeConfiguration) {
      Locale nodeLocale = new Locale(node.getString("locale"));
      descriptionLocales.add(nodeLocale);
    }
    return descriptionLocales;
  }

  public String getDescription(Locale locale) {
    String description = null;
    List<HierarchicalConfiguration> subnodeConfiguration =
            (List<HierarchicalConfiguration>) config.configurationsAt("descriptions.description");

    for(HierarchicalConfiguration node : subnodeConfiguration) {
      Locale nodeLocale = new Locale(node.getString("locale"));
      if(nodeLocale.equals(locale)) {
        description = node.getString("text");
      }
    }
    return description;
  }

  public List<Locale> getDescriptionLocales() {
    List<Locale> descriptionLocales = new ArrayList<Locale>();
    List<HierarchicalConfiguration> subnodeConfiguration =
            (List<HierarchicalConfiguration>) config.configurationsAt("descriptions.description");

    for(HierarchicalConfiguration node : subnodeConfiguration) {
      Locale nodeLocale = new Locale(node.getString("locale"));
      descriptionLocales.add(nodeLocale);
    }
    return descriptionLocales;
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

  public String getHeaderMail() {
    return headerMail;
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

  public Locale getContentLocale() {
    return contentLocale;
  }

  public Locale getProcuctLocale() {
    return procuctLocale;
  }
}
