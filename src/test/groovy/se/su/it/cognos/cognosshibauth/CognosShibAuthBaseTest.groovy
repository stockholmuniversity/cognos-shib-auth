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

package se.su.it.cognos.cognosshibauth

import org.junit.Test
import se.su.it.cognos.cognosshibauth.ldap.Account
import se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson
import com.cognos.CAM_AAA.authentication.*
import static junit.framework.Assert.assertEquals
import groovy.mock.interceptor.MockFor
import se.su.it.cognos.cognosshibauth.ldap.UiClass
import se.su.it.cognos.cognosshibauth.memcached.Cache
import static junit.framework.Assert.assertNull
import static junit.framework.Assert.assertNotNull
import org.junit.Before
import org.codehaus.groovy.runtime.InvokerHelper
import com.cognos.CAM_AAA.authentication.ISearchStep.SearchAxis
import gldapo.Gldapo
import se.su.it.cognos.cognosshibauth.ldap.schema.SchemaBase
import org.springframework.ldap.core.DistinguishedName

public class CognosShibAuthBaseTest extends TestBaseClass {

  private CognosShibAuthBase target = new CognosShibAuthBase();

  @Before
  void setUp() {
    target.metaClass.publicGetResult = { int searchType, String objectID, ISearchFilter filter, IQueryOption queryOption ->
      delegate.getQueryResult(searchType, objectID, filter, queryOption)
    }

    super.setUp()
  }

  @Test
  void testGetQueryResultReturnsACollection() {
    assert target.publicGetResult(0, null, null, null) instanceof Collection
  }
}
