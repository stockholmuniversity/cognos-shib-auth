package se.su.it.cognos.cognosshibauth.ldap

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IBaseClass
import com.cognos.CAM_AAA.authentication.IGroup
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames

public class Group extends UiClass implements IGroup {

  private Logger LOG = Logger.getLogger(Group.class.getName())

  private String namespaceId = null

  GroupOfUniqueNames groupOfUniqueNames

  public Group(String namespaceId, String dn) {
    this(namespaceId, GroupOfUniqueNames.getByDn(dn))
  }

  public Group(String namespaceId, GroupOfUniqueNames groupOfUniqueNames) {
    super("${namespaceId}:${UiClass.PREFIX_GROUP}:${groupOfUniqueNames.getDn()}")

    this.groupOfUniqueNames = groupOfUniqueNames
    this.namespaceId = namespaceId

    addName(defaultLocale, groupOfUniqueNames.cn)
    addDescription(defaultLocale, groupOfUniqueNames.description)
  }

  public IBaseClass[] getMembers() {
    List<String> members = groupOfUniqueNames.uniqueMember

    members.collect { member ->
      new Account(namespaceId, member)
    } as IBaseClass[]
  }
}
