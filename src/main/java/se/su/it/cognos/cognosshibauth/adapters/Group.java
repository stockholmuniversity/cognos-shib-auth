package se.su.it.cognos.cognosshibauth.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IGroup;

public class Group extends UiClass implements IGroup {
  private List<IBaseClass> members = null;

  private Locale defaultLocale = null;

  public Group(String theObjectID, String name, Locale defaultLocale) {
    super(theObjectID);

    members = new ArrayList<IBaseClass>();
    
    this.defaultLocale = defaultLocale;

    addName(defaultLocale, name);
  }

  public void addMember(IBaseClass theMember) {
    members.add(theMember);
  }

  public IBaseClass[] getMembers() {
    return members.toArray(new IBaseClass[members.size()]);
  }
}
