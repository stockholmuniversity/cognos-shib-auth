package se.su.it.cognos.cognosshibauth.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IGroup;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

public class Group extends UiClass implements IGroup {
  private List<IBaseClass> members = null;

  private Locale defaultLocale = null;

  public Group(String theObjectID, String name) {
    super(theObjectID);

    members = new ArrayList<IBaseClass>();

    ConfigHandler configHandler = ConfigHandler.instance();
    defaultLocale = configHandler.getContentLocale();

    addName(defaultLocale, name);
  }

  public void addMember(IBaseClass theMember) {
    members.add(theMember);
  }

  public IBaseClass[] getMembers() {
    return members.toArray(new IBaseClass[members.size()]);
  }
}
