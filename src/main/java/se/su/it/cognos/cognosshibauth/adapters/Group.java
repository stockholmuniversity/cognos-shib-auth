package se.su.it.cognos.cognosshibauth.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IGroup;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class Group extends UiClass implements IGroup {
  private List<IBaseClass> members = null;

  private Locale defaultLocale = null;

  public Group(String namespaceId, String name) {
    super(namespaceId + ":" + PREFIX_GROUP + name);

    members = new ArrayList<IBaseClass>();

    defaultLocale = configHandler.getContentLocale();

    addName(defaultLocale, name);
  }

  public void addMember(IBaseClass theMember) {
    members.add(theMember);
  }

  public IBaseClass[] getMembers() {
    return members.toArray(new IBaseClass[members.size()]);
  }

  public static Group fromSearchResult(String namespaceId, SearchResult result) {
    Attributes attributes = result.getAttributes();
    Attribute cn = attributes.get("cn");
    Attribute description = attributes.get("description");
    try {
      Group group = new Group(namespaceId, (String)cn.get(0));
      group.addDescription(Locale.ENGLISH, (String)description.get(0)); //TODO: Get some locale
      return group;
    } catch (NamingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
