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

import java.util.logging.Logger

import com.cognos.CAM_AAA.authentication.IBaseClass
import com.cognos.CAM_AAA.authentication.IGroup
import se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace
import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression
import javax.naming.directory.InvalidAttributesException

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
    Set<String> members = groupOfUniqueNames.uniqueMember

    members.collect { member ->
      IBaseClass result = null

      if (member.startsWith('cn')) { //Probably a Group
        def key = createObjectId(UiClass.PREFIX_GROUP, member)
        result = Cache.getInstance().get(key, { new Group(member) } )
      }
      else if (member.startsWith('uid')) {//Probably a User
        def key = createObjectId(UiClass.PREFIX_USER, member)
        result = Cache.getInstance().get(key, { Account.createFromDn(member) } )
      }

      result
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

    if (attribute && value) {
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
    }

    filter
  }
}
