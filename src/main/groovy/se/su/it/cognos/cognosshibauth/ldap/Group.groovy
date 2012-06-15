package se.su.it.cognos.cognosshibauth.ldap

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IBaseClass
import com.cognos.CAM_AAA.authentication.IGroup
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace
import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression

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
    this(GroupOfUniqueNames.getByDn(dn) as GroupOfUniqueNames)
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

  static String buildLdapFilter(String attribute, String value, String operator = null) {
    String filter = ""

    switch (attribute) {
      case '@userName':
      case '@defaultName':
        attribute = 'cn'
        break
      case '@defaultDescription':
        attribute = 'description'
        break
    }

    switch (operator) {
      case ISearchFilterRelationExpression.NotEqual:
        filter = "(!${attribute}=${value})"
        break
      case ISearchFilterRelationExpression.GreaterThan:
        filter = "(&(${attribute}>=${value})(!${attribute}=${value}))"
        break
      case ISearchFilterRelationExpression.GreaterThanOrEqual:
        filter = "(${attribute}>=${value})"
        break
      case ISearchFilterRelationExpression.LessThan:
        filter = "(&(${attribute}<=${value})(!${attribute}=${value}))"
        break
      case ISearchFilterRelationExpression.LessThanOrEqual:
        filter = "(${attribute}<=${value})"
        break
      case ISearchFilterRelationExpression.EqualTo:
      default:
        filter = "(${attribute}=${value})"
        break
    }

    filter
  }
}
