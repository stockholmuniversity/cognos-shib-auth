package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.IBaseClass;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: hdrys
 * Date: 2011-04-05
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class CognosShibAuthBaseClass implements IBaseClass{

  private IBaseClass[] ancestors;
  private String name;
  private Locale[] availableNameLocales;
  private String objectID;

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

  public void setAncestors(IBaseClass[] ancestors) {
    this.ancestors = ancestors;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAvailableNameLocales(Locale[] availableNameLocales) {
    this.availableNameLocales = availableNameLocales;
  }

  public void setObjectID(String objectID) {
    this.objectID = objectID;
  }
}
