package se.su.it.cognos.cognosshibauth.ldap;

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
  static final PREFIX_FOLDER = "f"
  static final PREFIX_GROUP = "g"
  static final PREFIX_ROLE = "r"
  static final PREFIX_USER = "u"

  private Logger LOG = Logger.getLogger(UiClass.class.getName());

  def objectID = null;

  private Stack<IBaseClass>	ancestors = null;

  private HashMap<Locale, String> names = null;
  private HashMap<Locale, String> descriptions = null;

  protected Locale defaultLocale = null;

  protected ConfigHandler configHandler = null;

  public UiClass(theObjectID) {
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

  public static boolean isFolder(String objectId) {
    return isType(objectId, PREFIX_FOLDER);
  }

  public static boolean isGroup(String objectId) {
    return isType(objectId, PREFIX_GROUP);
  }

  public static boolean isRole(String objectId) {
    return isType(objectId, PREFIX_ROLE);
  }

  public static boolean isUser(String objectId) {
    return isType(objectId, PREFIX_USER);
  }

  private static boolean isType(String objectId, String type) {
    try {
      int index = objectId.lastIndexOf(":");
      char typeC = objectId.charAt(index-1);
      return type.equals(String.valueOf(typeC));
    } catch (Throwable t){
      //Swallow error and return false.
    }
    return false;
  }

  public static String camIdToName(String camId) {
    try {
      return camId.substring(camId.lastIndexOf(":")+1);
    } catch(Throwable t) {
      //Swallow everything and return null
      return null;
    }
  }
}
