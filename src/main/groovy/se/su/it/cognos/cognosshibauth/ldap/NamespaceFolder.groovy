/*
 * Copyright (c) 2013, IT Services, Stockholm University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Stockholm University nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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

  private static Logger LOG = Logger.getLogger(NamespaceFolder.class.getName())
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
      def countLimit = configHandler.getIntEntry("ldap.count_limit", 500)

      List<GroupOfUniqueNames> groupOfUniqueNamesList = GroupOfUniqueNames.findAll(filter: filter, pageSize: countLimit)
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
      def countLimit = configHandler.getIntEntry("ldap.count_limit", 500)

      List<SuPerson> suPersons = SuPerson.findAll(filter: filter, pageSearch: countLimit)
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
