package se.su.it.cognos.cognosshibauth.ldap

import com.cognos.CAM_AAA.authentication.INamespaceFolder
import com.cognos.CAM_AAA.authentication.IUiClass
import java.util.logging.Logger
import org.apache.commons.configuration.HierarchicalConfiguration
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace
import se.su.it.cognos.cognosshibauth.memcached.Cache

public class NamespaceFolder extends UiClass implements INamespaceFolder {

  private Logger LOG = Logger.getLogger(NamespaceFolder.class.getName())
  private static long SerialVersionUID = 4L

  List<UiClass> folders = []
  List<UiClass> userLdapFilters = []
  List<UiClass> groupLdapFilters = []
  List<UiClass> roleLdapFilters = []

  public NamespaceFolder(String parentId, String name) {
    super("${parentId}:${UiClass.PREFIX_FOLDER}:${name}")

    addName(defaultLocale, name)
  }

  public static NamespaceFolder configEntryToFolder(HashMap<String, NamespaceFolder> folders,
                                                    HierarchicalConfiguration folderEntry,
                                                    String parentId = CognosShibAuthNamespace.namespaceId) {
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
    return folders.size() > 0;
  }

  public void addDescription(String description) {
    addDescription(defaultLocale, description);
  }

  public void addChild(IUiClass iUiClass) {
    folders.add(iUiClass);
  }

  List<UiClass> getChildren() {
    List<UiClass> retList = new ArrayList<UiClass>()
    retList.addAll getFolders()
    retList.addAll loadLdapGroups()
    retList.addAll loadLdapRoles()
    retList.addAll loadLdapUsers()
    return retList
  }

  public List<IUiClass> loadLdapGroups() {
    def groups = []
    groupLdapFilters.each { filter ->
      List<GroupOfUniqueNames> groupOfUniqueNamesList = GroupOfUniqueNames.findAll(filter: filter)
      groups.addAll groupOfUniqueNamesList.collect { groupOfUniqueName ->
        def key = createObjectId(UiClass.PREFIX_GROUP, groupOfUniqueName.getDn())
        Cache.getInstance().get(key, { new Group(groupOfUniqueName) })
      }
    }

    groups
  }

  public List<IUiClass> loadLdapRoles() {
    def roles = []

    roleLdapFilters.each { filter ->
      List<Role> roleList = Role.findAllByFilter(filter)
      roles.addAll roleList
    }

    roles
  }

  public List<IUiClass> loadLdapUsers() {
    def users = []

    userLdapFilters.each { filter ->
      List<SuPerson> suPersons = SuPerson.findAll(filter: filter)
      users.addAll suPersons.collect { suPerson ->
        def key = createObjectId(UiClass.PREFIX_USER, suPerson.getDn())
        Cache.getInstance().get(key, { new Account(suPerson) })
      }
    }

    users
  }

  public void addUserLdapFilter(String userLdapFilter) {
    if(userLdapFilter != null)
      userLdapFilters.add(userLdapFilter);
  }

  public void addGroupLdapFilter(String groupLdapFilter) {
    if(groupLdapFilter != null)
      groupLdapFilters.add(groupLdapFilter);
  }

  public void addRoleLdapFilter(String roleLdapFilter) {
    if(roleLdapFilter != null)
      roleLdapFilters.add(roleLdapFilter);
  }
}
