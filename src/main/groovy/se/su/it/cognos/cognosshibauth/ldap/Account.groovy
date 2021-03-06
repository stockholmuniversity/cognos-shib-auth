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

import com.cognos.CAM_AAA.authentication.IAccount

import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson

import com.cognos.CAM_AAA.authentication.UnrecoverableException
import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression
import javax.security.auth.login.FailedLoginException

public class Account extends UiClass implements IAccount {

  private static Logger LOG = Logger.getLogger(Account.class.getName())

  private static long SerialVersionUID = 1L

  SuPerson suPerson

  Locale contentLocale
  Locale productLocale

  HashMap<String, List<String>> customProperties

  /**
   * Constructs an Account instance
   * @param SuPerson suPerson
   */
  public Account(SuPerson suPerson) {
    super(createObjectId(UiClass.PREFIX_USER, suPerson.getDn()))

    productLocale = contentLocale = defaultLocale

    customProperties = new HashMap<String, List<String>>()

    this.suPerson = suPerson

    addName(contentLocale, "${suPerson.givenName} ${suPerson.sn}")
    addDescription(contentLocale, "") //TODO: link to "Kontohantering"?
  }

/**
 * Create an Account from dn
 * @param String dn identifier in sukat
 * @return A new Account instance based on what is fetched from sukat from dn
 */
  public static Account createFromDn(String dn) {
    String objectId = createObjectId(UiClass.PREFIX_USER, dn)
    Cache.getInstance().get(objectId, { new Account(SuPerson.getByDn(dn)) } ) as Account
  }

  @Override
  public String[] getCustomPropertyNames() {
    if (customProperties != null) {
      Set keySet = this.customProperties.keySet();
      return (String[]) keySet.toArray(new String[keySet.size()]);
    }
    return null;
  }

  @Override
  public String[] getCustomPropertyValue(String theName) {
    List<String> list = customProperties.get(theName);
    if (list != null)
      return (String[]) list.toArray(new String[list.size()]);
    return null;
  }

  /**
   * Add property to the Accounts custom property HashMap
   * @param String theName
   * @param String theValue
   */
  public void addCustomProperty(String theName, String theValue) {
    List<String> list = customProperties.get(theName);

    if (list == null) {
      list = new ArrayList<String>();
      customProperties.put(theName, list);
    }

    list.add(theValue);
  }

  /**
   * Find an Account by uid
   * @param String uid
   * @return A new Account instance based on what is fetched from sukat from uid
   */
  static Account findByUid(String uid) {
    if(uid == null)
      throw new UnrecoverableException("Cannot create account", "User uid == null")

    SuPerson suPerson1 = SuPerson.findByUid(uid)

    if (suPerson1 == null)
      throw new UnrecoverableException("Cannot create account", "User uid='$uid' not found in LDAP.")

    Cache.getInstance().get(createObjectId(UiClass.PREFIX_USER, suPerson1.getDn()), { new Account(suPerson1) } )
  }

  @Override
  String getBusinessPhone() {
    suPerson.telephoneNumber
  }

  @Override
  String getEmail() {
    suPerson.mail
  }

  @Override
  Locale getContentLocale() {
    contentLocale
  }

  @Override
  String getFaxPhone() {
    suPerson.faxPhone
  }

  @Override
  String getGivenName() {
    suPerson.givenName
  }

  @Override
  String getHomePhone() {
    suPerson.homePhone
  }

  @Override
  String getMobilePhone() {
    suPerson.mobile
  }

  @Override
  String getPagerPhone() {
    suPerson.pagerPhone
  }

  @Override
  String getPostalAddress() {
    suPerson.registeredAddress
  }

  @Override
  Locale getProductLocale() {
    productLocale
  }

  @Override
  String getSurname() {
    suPerson.sn
  }

  @Override
  String getUserName() {
    suPerson.uid
  }
  /**
   * Fetches the Accounts eduPersonEntitlement strings in a Set
   * @return Set<String> eduPersonEntitlement
   */
  Set<String> getEduPersonEntitlements() {
    suPerson.eduPersonEntitlement
  }

  /**
   * Get all roles for this account
   *
   * @return list of roles for this account
   */
  List<Role> getRoles() {
    def list = getEduPersonEntitlements()?.collect { entitlement ->
      Role.createFromUri(entitlement)
    }
    list?.removeAll {it == null}
    list
  }

  /**
   * Get all groups for this account
   *
   * @return list of groups for this account
   */
  List<Group> getGroups() {
    def groups = suPerson?.memberOf?.collect {
      try {
        new Group(it)
      } catch(Exception e) {
        LOG.warning("Failed to add group with dn ${it} to user ${objectID}.")
      }
    } ?: []

    groups?.removeAll { it == null }

    groups
  }

  static String buildLdapFilter(String attribute, String value, String operator = null) {
    String filter = ""

    switch (attribute) {
      case '@userName':
      case '@defaultName':
        filter = createFilterPart('uid', value, operator)
        break
      case '@defaultDescription':
        String part1 = createFilterPart('cn', value, operator)
        String part2 = createFilterPart('mailLocalAddress', value, operator)
        filter = "(|${part1}${part2})"
        break
    }

    filter
  }

  private static String createFilterPart(String attribute, String value, String operator = null) {

    String filter = ""

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
