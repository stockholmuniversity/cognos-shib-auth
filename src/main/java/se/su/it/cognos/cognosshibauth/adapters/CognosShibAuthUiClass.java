
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IUiClass;


public class CognosShibAuthUiClass implements IUiClass
{
  private String	objectID;
  private HashMap	names;
  private HashMap	descriptions;
  private Stack	ancestors;
  public CognosShibAuthUiClass(String theObjectID)
  {
    super();
    names = null;
    descriptions = null;
    ancestors = null;
    objectID = theObjectID;
  }


  public void addDescription(Locale theLocale, String theDescription)
  {
    if (descriptions == null)
    {
      descriptions = new HashMap();
    }
    descriptions.put(theLocale, theDescription);
  }


  public String getDescription(Locale theLocale)
  {
    if (descriptions != null)
    {
      return (String) descriptions.get(theLocale);
    }
    return null;
  }


  public Locale[] getAvailableDescriptionLocales()
  {
    if (descriptions != null)
    {
      Set keySet = descriptions.keySet();
      Locale[] array = new Locale[keySet.size()];
      return (Locale[]) keySet.toArray(array);
    }
    return null;
  }


  public void addAncestors(IBaseClass theAncestor)
  {
    if (ancestors == null)
    {
      ancestors = new Stack();
    }
    ancestors.push(theAncestor);
  }


  public IBaseClass[] getAncestors()
  {
    if (ancestors != null)
    {
      IBaseClass[] array = new IBaseClass[ancestors.size()];
      return (IBaseClass[]) ancestors.toArray(array);
    }
    return null;
  }


  public void addName(Locale theLocale, String theName)
  {
    if (names == null)
    {
      names = new HashMap();
    }
    names.put(theLocale, theName);
  }

  public boolean getHasChildren()
  {
    return false;
  }

  public String getName(Locale theLocale)
  {
    if (names != null)
    {
      return (String) names.get(theLocale);
    }
    return null;
  }


  public Locale[] getAvailableNameLocales()
  {
    if (names != null)
    {
      Set keySet = names.keySet();
      Locale[] array = new Locale[keySet.size()];
      return (Locale[]) keySet.toArray(array);
    }
    return null;
  }

  public String getObjectID()
  {
    return objectID;
  }


  protected void setObjectID(String theObjectID)
  {
    objectID = theObjectID;
  }

}
