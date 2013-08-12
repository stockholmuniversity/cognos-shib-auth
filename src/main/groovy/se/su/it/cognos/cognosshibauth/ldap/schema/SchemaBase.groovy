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

package se.su.it.cognos.cognosshibauth.ldap.schema

import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import java.util.logging.Logger
import java.util.logging.Level
import gldapo.Gldapo

import gldapo.GldapoSchemaRegistry
import gldapo.GldapoDirectory
import se.su.it.cognos.cognosshibauth.ldap.gldapo.DummyGldapoDirectoryRegistry

class SchemaBase implements Serializable {
  static {
    ConfigHandler configHandler = ConfigHandler.instance()
    def url = configHandler.getStringEntry("ldap.url", "ldap://sukat-ldap.it.su.se")
    def baseDn = configHandler.getStringEntry("ldap.base_dn", "dc=su,dc=se")
    def countLimit = configHandler.getIntEntry("ldap.count_limit", 500)
    def timeLimit = configHandler.getIntEntry("ldap.time_limit", 120000)

    Logger LOG = Logger.getLogger(SchemaBase.class.getName())
    LOG.log(Level.FINE, "Initializing Gldapo schema '${SchemaBase.class.getName()}'")

    Gldapo gldapo = new Gldapo()

    def gldapoDirectoryMap = [
      defaultDirectory: true,
      url: url,
      base: baseDn,
      searchControls: [
        countLimit: countLimit,
        timeLimit: timeLimit,
        searchScope: "subtree"
      ]
    ]

    GldapoSchemaRegistry gldapoSchemaRegistry = new GldapoSchemaRegistry(gldapo)
    gldapoSchemaRegistry.add(se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson)
    gldapoSchemaRegistry.add(se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames)
    gldapo.setSchemas(gldapoSchemaRegistry)

    DummyGldapoDirectoryRegistry gldapoDirectoryRegistry = new DummyGldapoDirectoryRegistry()
    GldapoDirectory gldapoDirectory = new GldapoDirectory("example", gldapoDirectoryMap)
    gldapoDirectoryRegistry.put("example", gldapoDirectory)
    gldapo.setDirectories(gldapoDirectoryRegistry)
  }
}
