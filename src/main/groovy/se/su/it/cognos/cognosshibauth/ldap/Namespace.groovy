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

import com.cognos.CAM_AAA.authentication.INamespace
import com.cognos.CAM_AAA.authentication.INamespaceConfiguration
import com.cognos.CAM_AAA.authentication.UnrecoverableException

class Namespace extends UiClass implements INamespace {

  private String[] capabilities;
  private String namespaceFormat;
  private static long SerialVersionUID = 3L
  /**
   * Constructs a NameSpace instance
   *
   */
  public Namespace() {
    super(null);

    capabilities = new String[6];
    capabilities[0] = CapabilityCaseSensitive;
    capabilities[1] = CapabilityContains;
    capabilities[2] = CapabilityEquals;
    capabilities[3] = CapabilitySort;
    capabilities[4] = CapabilityStartsWith;
    capabilities[5] = CapabilityEndsWith;

    namespaceFormat = "http://developer.cognos.com/schemas/CAM/AAANamespaceFormat/2/";
  }
  /**
   * Constructs a NameSpace instance based on an objectId
   * @param theObjectID
   *
   */
  public Namespace(String theObjectID) {
    super(theObjectID);
    capabilities = new String[6];
    capabilities[0] = CapabilityCaseSensitive;
    capabilities[1] = CapabilityContains;
    capabilities[2] = CapabilityEquals;
    capabilities[3] = CapabilitySort;
    capabilities[4] = CapabilityStartsWith;
    capabilities[5] = CapabilityEndsWith;
    namespaceFormat = "http://developer.cognos.com/schemas/CAM/AAANamespaceFormat/2/";
  }
  @Override
  public void init(INamespaceConfiguration theNamespaceConfiguration)
          throws UnrecoverableException {
    setObjectID(theNamespaceConfiguration.getID());
    addName(theNamespaceConfiguration.getServerLocale(),
            theNamespaceConfiguration.getDisplayName());
  }
  @Override
  public void destroy() {
  }
  @Override
  public String getNamespaceFormat() {
    return namespaceFormat;
  }
  @Override
  public void setNamespaceFormat(String theNamespaceFormat) {
    namespaceFormat = theNamespaceFormat;
  }
  @Override
  public String[] getCapabilities() {
    return capabilities;
  }
  @Override
  public boolean getHasChildren() {
    return true;
  }
}
