package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: hdrys
 * Date: 2011-04-05
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class CognosShibAuthRole implements IRole{

  private IBaseClass[] members;
  private String description;
  private Locale[] availableDescriptionLocales;
  private IBaseClass[] ancestors;
  private String name;
  private Locale[] availableNameLocales;
  private String objectID;

  public IBaseClass[] getMembers() {
    return members;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getDescription(Locale locale) {
    return description;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Locale[] getAvailableDescriptionLocales() {
    return availableDescriptionLocales;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public IBaseClass[] getAncestors() {
    return ancestors;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean getHasChildren() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getName(Locale locale) {
    return name;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Locale[] getAvailableNameLocales() {
    return availableNameLocales;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getObjectID() {
    return objectID;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setMembers(IBaseClass[] members) {
    this.members = members;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setAvailableDescriptionLocales(Locale[] availableDescriptionLocales) {
    this.availableDescriptionLocales = availableDescriptionLocales;
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
