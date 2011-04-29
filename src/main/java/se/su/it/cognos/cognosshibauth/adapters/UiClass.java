package se.su.it.cognos.cognosshibauth.adapters;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IUiClass;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

public class UiClass implements IUiClass {
  public static final String PREFIX_FOLDER = "f:";
  public static final String PREFIX_GROUP = "g:";
  public static final String PREFIX_ROLE = "r:";
  public static final String PREFIX_USER = "u:";

  private Logger LOG = Logger.getLogger(UiClass.class.getName());

  private String objectID = null;

  private Stack<IBaseClass>	ancestors = null;

  private HashMap<Locale, String> names = null;
  private HashMap<Locale, String> descriptions = null;

  protected Locale defaultLocale = null;

  protected ConfigHandler configHandler = null;

  public UiClass(String theObjectID) {
    LOG.log(Level.FINEST, "Creating new UiClass with objectID '" + theObjectID + "'.");

    names = new HashMap<Locale, String>();
    descriptions = new HashMap<Locale, String>();

    ancestors = new Stack<IBaseClass>();

    objectID = theObjectID;

    configHandler = ConfigHandler.instance();
    defaultLocale = configHandler.getContentLocale();
  }

  public void addDescription(Locale theLocale, String theDescription) {
    descriptions.put(theLocale, theDescription);
  }

  public String getDescription(Locale theLocale) {
    return descriptions.get(theLocale);
  }

  public Locale[] getAvailableDescriptionLocales() {
    Set<Locale> keySet = descriptions.keySet();
    return keySet.toArray(new Locale[keySet.size()]);
  }

  public void addAncestors(IBaseClass theAncestor) {
    ancestors.push(theAncestor);
  }

  public IBaseClass[] getAncestors() {
    return ancestors.toArray(new IBaseClass[ancestors.size()]);
  }

  public void addName(Locale theLocale, String theName) {
    names.put(theLocale, theName);
  }

  public boolean getHasChildren() {
    return false;
  }

  public String getName(Locale theLocale) {
    return names.get(theLocale);
  }

  public Locale[] getAvailableNameLocales() {
    Set<Locale> keySet = names.keySet();
    return keySet.toArray(new Locale[keySet.size()]);
  }

  public String getObjectID() {
    LOG.log(Level.FINEST, "Getting objectID '" + objectID + "'.");
    return objectID;
  }

  protected void setObjectID(String theObjectID) {
    LOG.log(Level.FINEST, "Setting objectID '" + theObjectID + "'.");
    objectID = theObjectID;
  }
}
