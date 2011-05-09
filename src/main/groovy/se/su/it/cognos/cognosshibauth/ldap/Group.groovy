package se.su.it.cognos.cognosshibauth.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IGroup;
import se.su.it.cognos.cognosshibauth.ldap.Account;
import se.su.it.cognos.cognosshibauth.ldap.UiClass;
import se.su.it.sukat.SUKAT;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class Group extends UiClass implements IGroup {

  private Logger LOG = Logger.getLogger(Group.class.getName());

  private String dn = null;
  private String namespaceId = null;

  public Group(String namespaceId, String dn) throws Exception {
    super("${namespaceId}:${UiClass.PREFIX_GROUP}:${dn}")

    this.dn = dn;
    this.namespaceId = namespaceId;

    String ldapURL = configHandler.getStringEntry("adapters.url");

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
        LOG.log(Level.SEVERE, "Failed to find group with dn=" + dn + ": " + e.getMessage());
        throw e;
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to establish adapters connection to server '" + ldapURL + "': " + e.getMessage());
      throw e;
    }
  }

  public IBaseClass[] getMembers() {
    String ldapURL = configHandler.getStringEntry("adapters.url");
    List<IBaseClass> members = new ArrayList<IBaseClass>();

    try {
      SUKAT sukat = SUKAT.newInstance(ldapURL);

      try {
        SearchResult searchResult = sukat.read(dn);
        Attributes attrs = searchResult.getAttributes();
        Attribute attr = attrs.get("uniqueMember");
        NamingEnumeration<?> ne = attr.getAll();
        while(ne.hasMoreElements()) {
          Object o = ne.next();
          Account account = Account.fromSearchResult(namespaceId, sukat.read((String) o));
          members.add(account);
        }
      } catch (NamingException e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to establish adapters connection to server '" + ldapURL + "': " + e.getMessage());
      e.printStackTrace();
    }
    return members.toArray(new IBaseClass[members.size()]);
  }
}
