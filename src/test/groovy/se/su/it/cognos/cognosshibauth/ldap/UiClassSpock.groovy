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
