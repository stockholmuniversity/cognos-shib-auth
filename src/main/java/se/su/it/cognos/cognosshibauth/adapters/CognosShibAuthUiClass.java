package se.su.it.cognos.cognosshibauth.adapters;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IUiClass;

public class CognosShibAuthUiClass implements IUiClass {

  private Logger LOG = Logger.getLogger(CognosShibAuthUiClass.class.getName());

  private String objectID = null;
  private Stack	ancestors = null;

  private HashMap<Locale, String> names = null;
  private HashMap<Locale, String> descriptions = null;

  public CognosShibAuthUiClass(String theObjectID) {
    LOG.log(Level.FINEST, "Creating new UiClass with objectID '" + theObjectID + "'.");

    names = new HashMap<Locale, String>();
    descriptions = new HashMap<Locale, String>();

    objectID = theObjectID;
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
    if (ancestors == null)
    {
      ancestors = new Stack();
    }
    ancestors.push(theAncestor);
  }


  public IBaseClass[] getAncestors() {
    if (ancestors != null)
    {
      IBaseClass[] array = new IBaseClass[ancestors.size()];
      return (IBaseClass[]) ancestors.toArray(array);
    }
    return null;
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
