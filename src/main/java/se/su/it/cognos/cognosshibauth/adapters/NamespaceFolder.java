package se.su.it.cognos.cognosshibauth.adapters;

import com.cognos.CAM_AAA.authentication.INamespaceFolder;
import com.cognos.CAM_AAA.authentication.IUiClass;
import org.apache.commons.configuration.HierarchicalConfiguration;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.sukat.SUKAT;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NamespaceFolder extends UiClass implements INamespaceFolder {

  private Logger LOG = Logger.getLogger(NamespaceFolder.class.getName());

  private Locale defaultLocale = null;

  private List<IUiClass> children = null;

  private List<String> userLdapFilters = null;
  private List<String> groupLdapFilters = null;
  private List<String> roleLdapFilters = null;

  private String ldapBaseDn = "";

  public NamespaceFolder(String parentId, String name) {
    super(parentId + ":" + PREFIX_FOLDER + ":" + name);

    children = new ArrayList<IUiClass>();

    userLdapFilters = new ArrayList<String>();
    groupLdapFilters = new ArrayList<String>();
    roleLdapFilters = new ArrayList<String>();
    defaultLocale = configHandler.getContentLocale();

    addName(defaultLocale, name);
  }

  public static NamespaceFolder configEntryToFolder(HashMap<String, NamespaceFolder> folders,
                                                    HierarchicalConfiguration folderEntry, String parentId) {
    String name = folderEntry.getString("name");
    String description = folderEntry.getString("description");

    NamespaceFolder folder = new NamespaceFolder(parentId, name);
    folder.addDescription(description);

    List<HierarchicalConfiguration> groups = folderEntry.configurationsAt("children.groups");
    for(HierarchicalConfiguration group : groups) {
      folder.addGroupLdapFilter(group.getString("ldap_filter"));
    }

    List<HierarchicalConfiguration> users = folderEntry.configurationsAt("children.users");
    for(HierarchicalConfiguration user : users) {
      folder.addUserLdapFilter(user.getString("ldap_filter"));
    }

    List<HierarchicalConfiguration> roles = folderEntry.configurationsAt("children.roles");
    for(HierarchicalConfiguration role : roles) {
      folder.addRoleLdapFilter(role.getString("ldap_filter"));
    }

    List<HierarchicalConfiguration> foldersConfig = folderEntry.configurationsAt("children.folder");
    for(HierarchicalConfiguration folderConfig : foldersConfig) {
      NamespaceFolder childFolder = configEntryToFolder(folders, folderConfig, folder.getObjectID());
      if(childFolder != null) {
        folder.addChild(childFolder);
        childFolder.addAncestors(folder);
        folders.put(childFolder.getObjectID(), childFolder);
      }
    }

    return folder;
  }

  public boolean getHasChildren() {
    return children.size() > 0;
  }

  public void addDescription(String description) {
    addDescription(defaultLocale, description);
  }

  public void addChild(IUiClass iUiClass) {
    children.add(iUiClass);
  }

  public List<IUiClass> getChildren() {
    List<IUiClass> retList = new ArrayList<IUiClass>();
    retList.addAll(children);
    retList.addAll(loadLdapGroups());
    retList.addAll(loadLdapRoles());
    retList.addAll(loadLdapUsers());
    return retList;
  }

  public List<IUiClass> loadLdapGroups() {
    List<IUiClass> groups = new ArrayList<IUiClass>();

    String ldapURL = configHandler.getStringEntry("ldap.url");
    ldapBaseDn = configHandler.getStringEntry("ldap.base_dn", "");
    SUKAT sukat = null;
    try {
      sukat = SUKAT.newInstance(ldapURL);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to establish ldap connection to server '" + ldapURL + "': " + e.getMessage());
      e.printStackTrace();
    }

    try {
      for(String filter : groupLdapFilters) {
        NamingEnumeration<SearchResult> results = sukat.search(ldapBaseDn, filter);
        while(results.hasMoreElements()) {
          SearchResult searchResult = results.next();
          Group group = new Group(null, searchResult.getNameInNamespace()); //TODO: Don't send null for namespaceId
          if(group != null)
            groups.add(group);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return groups;
  }

  public List<IUiClass> loadLdapRoles() {
    List<IUiClass> roles = new ArrayList<IUiClass>();

    String ldapURL = configHandler.getStringEntry("ldap.url");
    ldapBaseDn = configHandler.getStringEntry("ldap.base_dn", "");
    SUKAT sukat = null;
    try {
      sukat = SUKAT.newInstance(ldapURL);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to establish ldap connection to server '" + ldapURL + "': " + e.getMessage());
      e.printStackTrace();
    }

    try {
      for(String filter : roleLdapFilters) {
        NamingEnumeration<SearchResult> results = sukat.search(ldapBaseDn, filter);
        Collection<Role> roleList = Role.fromSearchResults(null, results); //TODO: Don't send null for namespaceId
        for(Role role : roleList) {
          if(role != null)
            roles.add(role);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return roles;
  }

  public List<IUiClass> loadLdapUsers() {
    List<IUiClass> accounts = new ArrayList<IUiClass>();

    String ldapURL = configHandler.getStringEntry("ldap.url");
    ldapBaseDn = configHandler.getStringEntry("ldap.base_dn", "");
    SUKAT sukat = null;
    try {
      sukat = SUKAT.newInstance(ldapURL);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to establish ldap connection to server '" + ldapURL + "': " + e.getMessage());
      e.printStackTrace();
    }

    try {
      for(String filter : userLdapFilters) {
        NamingEnumeration<SearchResult> results = sukat.search(ldapBaseDn, filter);
        while(results.hasMoreElements()) {
          Account account = Account.fromSearchResult(null, results.next()); //TODO: Don't send null for namespaceId
          if(account != null)
            accounts.add(account);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return accounts;
  }

  public List<String> getUserLdapFilters() {
    return userLdapFilters;
  }

  public void addUserLdapFilter(String userLdapFilter) {
    if(userLdapFilter != null)
      userLdapFilters.add(userLdapFilter);
  }

  public List<String> getGroupLdapFilters() {
    return groupLdapFilters;
  }

  public void addGroupLdapFilter(String groupLdapFilter) {
    if(groupLdapFilter != null)
      groupLdapFilters.add(groupLdapFilter);
  }

  public List<String> getRoleLdapFilters() {
    return roleLdapFilters;
  }

  public void addRoleLdapFilter(String roleLdapFilter) {
    if(roleLdapFilter != null)
      roleLdapFilters.add(roleLdapFilter);
  }
}
