package se.su.it.cognos.cognosshibauth.ldap

import spock.lang.Specification
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Unroll

@RunWith(Sputnik)
class UiClassSpock extends Specification {

  @Unroll
  def "Test UiClass types: user=#isUser, group=#isGroup, role=#isRole, folder=#isFolder"() {
    setup:
    String type = ""
    if (isUser) type = UiClass.PREFIX_USER
    if (isGroup) type = UiClass.PREFIX_GROUP
    if (isRole) type = UiClass.PREFIX_ROLE
    if (isFolder) type = UiClass.PREFIX_FOLDER

    String objectId = "Cognos Shib Authenticator:${type}:Foobar"

    when:
    boolean user   = UiClass.isUser(objectId)
    boolean group  = UiClass.isGroup(objectId)
    boolean role   = UiClass.isRole(objectId)
    boolean folder = UiClass.isFolder(objectId)

    then:
    user == isUser
    group == isGroup
    role == isRole
    folder == isFolder

    where:
    isUser | isGroup | isRole | isFolder
    true   | false   | false  | false
    false  | true    | false  | false
    false  | false   | true   | false
    false  | false   | false  | true
  }
}
