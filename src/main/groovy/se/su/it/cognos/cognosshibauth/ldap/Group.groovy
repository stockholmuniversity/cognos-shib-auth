package se.su.it.cognos.cognosshibauth.ldap

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IBaseClass
import com.cognos.CAM_AAA.authentication.IGroup
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace
import se.su.it.cognos.cognosshibauth.memcached.Cache

public class Group extends UiClass implements IGroup {

  private static long SerialVersionUID = 2L

  private static Logger LOG = Logger.getLogger(Group.class.getName())

  GroupOfUniqueNames groupOfUniqueNames

  /**
   * Constructs a Group instance based on what is tetched from sukat by dn parameter
   *
   * @param String dn
   */
  public Group(String dn) {
    this(GroupOfUniqueNames.getByDn(dn))
  }

  /**
   * Constructs a Group instance based on GroupOfUniqueNames
   *
   * @param GroupOfUniqueNames groupOfUniqueNames
   */
  public Group(GroupOfUniqueNames groupOfUniqueNames) {
    super("${CognosShibAuthNamespace.namespaceId}:${UiClass.PREFIX_GROUP}:${groupOfUniqueNames.getDn()}")

    this.groupOfUniqueNames = groupOfUniqueNames

    addName(defaultLocale, groupOfUniqueNames.cn)
    addDescription(defaultLocale, groupOfUniqueNames.description)
  }

  /**
   * Finds groups by member.
   *
   * @param member the member to find groups by
   * @return a list of groups
   */
  static List<Group> findByMember(Account member) {
    def dn = member.getSuPerson().getDn()
    def groupOfUniqueNames = GroupOfUniqueNames.findAllByUniqueMember(dn.toString())

    groupOfUniqueNames.collect { group ->
      def key = createObjectId(UiClass.PREFIX_GROUP, group.getDn())
      Cache.getInstance().get(key, { new Group(group) } )
    }
  }

  @Override
  public IBaseClass[] getMembers() {
    List<String> members = groupOfUniqueNames.uniqueMember

    members.collect { member ->
      def key = createObjectId(UiClass.PREFIX_USER, member.getDn())
      Cache.getInstance().get(key, { new Account(member) } )
    } as IBaseClass[]
  }
}
