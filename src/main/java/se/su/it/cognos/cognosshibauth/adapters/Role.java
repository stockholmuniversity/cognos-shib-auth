package se.su.it.cognos.cognosshibauth.adapters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

public class Role extends UiClass implements IRole {

  private List<IBaseClass> members = null;

  private Locale defaultLocale = null;

  public Role(String namespaceId, String name) {
    super(namespaceId + ":" + PREFIX_ROLE + name);
    
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
