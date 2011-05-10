package se.su.it.cognos.cognosshibauth.ldap

import com.cognos.CAM_AAA.authentication.INamespaceFolder
import com.cognos.CAM_AAA.authentication.IUiClass
import java.util.logging.Logger
import org.apache.commons.configuration.HierarchicalConfiguration
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson

public class NamespaceFolder extends UiClass implements INamespaceFolder {

  private Logger LOG = Logger.getLogger(NamespaceFolder.class.getName())

  private List<IUiClass> children = null

  private List<String> userLdapFilters = null
  private List<String> groupLdapFilters = null
  private List<String> roleLdapFilters = null

  public NamespaceFolder(String parentId, String name) {
    super("${parentId}:${UiClass.PREFIX_FOLDER}:${name}")

    children = userLdapFilters = groupLdapFilters = roleLdapFilters = new ArrayList<String>()

    addName(defaultLocale, name)
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
    def groups = []
    groupLdapFilters.each { filter ->
      List<GroupOfUniqueNames> groupOfUniqueNamesList = GroupOfUniqueNames.findAll(filter: filter)
      groups.addAll groupOfUniqueNamesList.collect { groupOfUniqueName ->
        new Group(null, groupOfUniqueName) //TODO: Don't send null as namespaceId
      }
    }

    groups
  }

  public List<IUiClass> loadLdapRoles() {
    def roles = []

    roleLdapFilters.each { filter ->
      List<Role> roleList = Role.findAllByFilter(null, filter) //TODO: Don't send null as namespaceId
      roles.addAll roleList
    }

    roles
  }

  public List<IUiClass> loadLdapUsers() {
    def users = []

    userLdapFilters.each { filter ->
      List<SuPerson> suPersons = SuPerson.findAll(filter: filter)
      users.addAll suPersons.collect { suPerson ->
        new Account(null, suPerson)
      }
    }

    users
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
