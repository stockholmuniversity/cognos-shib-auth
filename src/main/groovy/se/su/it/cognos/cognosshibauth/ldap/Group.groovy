package se.su.it.cognos.cognosshibauth.ldap

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IBaseClass
import com.cognos.CAM_AAA.authentication.IGroup
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace

public class Group extends UiClass implements IGroup {

  private Logger LOG = Logger.getLogger(Group.class.getName())

  GroupOfUniqueNames groupOfUniqueNames

  public Group(String dn) {
    this(GroupOfUniqueNames.getByDn(dn))
  }

  public Group(GroupOfUniqueNames groupOfUniqueNames) {
    super("${CognosShibAuthNamespace.namespaceId}:${UiClass.PREFIX_GROUP}:${groupOfUniqueNames.getDn()}")

    this.groupOfUniqueNames = groupOfUniqueNames

    addName(defaultLocale, groupOfUniqueNames.cn)
    addDescription(defaultLocale, groupOfUniqueNames.description)
  }

  public IBaseClass[] getMembers() {
    List<String> members = groupOfUniqueNames.uniqueMember

    members.collect { member ->
      new Account(member)
    } as IBaseClass[]
  }
}
