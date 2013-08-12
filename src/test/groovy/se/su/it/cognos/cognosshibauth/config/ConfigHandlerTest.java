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

package se.su.it.cognos.cognosshibauth.config;
/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-08
 * Time: 10:48
 */

import com.cognos.CAM_AAA.authentication.INamespace;
import com.cognos.CAM_AAA.authentication.INamespaceConfiguration;
import com.sun.corba.se.impl.orb.ParserTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import se.su.it.cognos.cognosshibauth.TestBaseClass;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class ConfigHandlerTest extends TestBaseClass {

  @Test
  public void testThatNameReturnsNameForLocale() throws Exception {
    Locale locale_sv = new Locale("sv_SE");
    ConfigHandler configHandler = ConfigHandler.instance();

    String sv_name = configHandler.getName(locale_sv);

    assertEquals("Swedish Name", sv_name);
  }

  @Test
  public void testThatGetCapabilitiesReturnsAllCapabilities() throws Exception {
    ConfigHandler configHandler = ConfigHandler.instance();

    List<String> list = configHandler.getCapabilities();

    assertTrue(list.contains(INamespace.CapabilityCaseSensitive));
    assertTrue(list.contains(INamespace.CapabilityContains));
    assertTrue(list.contains(INamespace.CapabilityEndsWith));
    assertTrue(list.contains(INamespace.CapabilityEquals));
    assertTrue(list.contains(INamespace.CapabilitySort));
    assertTrue(list.contains(INamespace.CapabilityStartsWith));
  }
}

