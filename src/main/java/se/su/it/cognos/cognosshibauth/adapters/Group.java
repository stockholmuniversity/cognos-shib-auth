package se.su.it.cognos.cognosshibauth.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IGroup;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.sukat.SUKAT;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class Group extends UiClass implements IGroup {

  private Logger LOG = Logger.getLogger(Group.class.getName());

  private List<IBaseClass> members = null;

  private String dn = null;

  public Group(String namespaceId, String dn) {
    super(namespaceId + ":" + PREFIX_GROUP + ":" + dn);

    this.dn = dn;
    members = new ArrayList<IBaseClass>();

    String ldapURL = configHandler.getStringEntry("ldap.url");

    try {
      SUKAT sukat = SUKAT.newInstance(ldapURL);

      try {
        SearchResult searchResult = sukat.read(dn);
        Attributes attributes = searchResult.getAttributes();

        Attribute cn = attributes.get("cn");
        addName(defaultLocale, (String) cn.get(0));
        
        Attribute description = attributes.get("description");
        addDescription(defaultLocale, (String) description.get(0));
      } catch (NamingException e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to establish ldap connection to server '" + ldapURL + "': " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void addMember(IBaseClass theMember) {
    members.add(theMember);
  }

  public IBaseClass[] getMembers() {
    return members.toArray(new IBaseClass[members.size()]);
  }
}
