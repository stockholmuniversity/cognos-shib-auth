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

package se.su.it.cognos.cognosshibauth.ldap;

import com.cognos.CAM_AAA.authentication.IBaseClass
import com.cognos.CAM_AAA.authentication.IUiClass
import java.util.logging.Level
import java.util.logging.Logger
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import se.su.it.cognos.cognosshibauth.CognosShibAuthNamespace
import se.su.it.cognos.cognosshibauth.memcached.Cache

public class UiClass implements IUiClass, Serializable {

  private static long serialVersionUID = 10L

  static final PREFIX_FOLDER = "f"
  static final PREFIX_GROUP = "g"
  static final PREFIX_ROLE = "r"
  static final PREFIX_USER = "u"

  private static Logger LOG = Logger.getLogger(UiClass.class.getName());

  transient protected ConfigHandler configHandler = null;

  String objectID = null;

  private Stack<IBaseClass>	ancestors = null;

  private HashMap<Locale, String> names = null;
  private HashMap<Locale, String> descriptions = null;

  protected Locale defaultLocale = null;

  public UiClass(){}

  public UiClass(theObjectID) {
    LOG.log(Level.FINEST, "Creating new UiClass with objectID '" + theObjectID + "'.");

    names = new HashMap<Locale, String>();
    descriptions = new HashMap<Locale, String>();

    ancestors = new Stack<IBaseClass>();

    objectID = theObjectID;

    configHandler = ConfigHandler.instance();
    defaultLocale = configHandler.getContentLocale();
  }

  public void addDescription(Locale theLocale, String theDescription) {
    descriptions.put(theLocale, theDescription);
  }

  public String getDescription(Locale theLocale) {
    return descriptions.get(theLocale);
  }

  public Locale[] getAvailableDescriptionLocales() {
    Set<Locale> keySet = descriptions.keySet();
    return keySet.toArray(new Locale[keySet.size()]);
  }

  public void addAncestors(IBaseClass theAncestor) {
    ancestors.push(theAncestor);
  }

  public IBaseClass[] getAncestors() {
    return ancestors.toArray(new IBaseClass[ancestors.size()]);
  }

  public void addName(Locale theLocale, String theName) {
    names.put(theLocale, theName);
  }

  public boolean getHasChildren() {
    return false;
  }

  public String getName(Locale theLocale) {
    return names.get(theLocale);
  }

  public String getName() {
    return names.get(defaultLocale);
  }

  public Locale[] getAvailableNameLocales() {
    Set<Locale> keySet = names.keySet();
    return keySet.toArray(new Locale[keySet.size()]);
  }

  public String getObjectID() {
    LOG.log(Level.FINEST, "Getting objectID '" + objectID + "'.");
    return objectID;
  }

  protected void setObjectID(String theObjectID) {
    LOG.log(Level.FINEST, "Setting objectID '" + theObjectID + "'.");
    objectID = theObjectID;
  }

  public static boolean isFolder(String objectId) {
    return isType(objectId, PREFIX_FOLDER);
  }

  public static boolean isGroup(String objectId) {
    return isType(objectId, PREFIX_GROUP);
  }

  public static boolean isRole(String objectId) {
    return isType(objectId, PREFIX_ROLE);
  }

  public static boolean isUser(String objectId) {
    return isType(objectId, PREFIX_USER);
  }

  private static boolean isType(String objectId, String type) {
    objectId ==~ /.*:$type:.*/
  }

  public static String camIdToName(String camId) {
    try {
      return camId.substring(camId.lastIndexOf(":")+1);
    } catch(Throwable t) {
      //Swallow everything and return null
      return null;
    }
  }

  @Override
  boolean equals(Object o) {
    if (o instanceof UiClass)
      return this.objectID?.equals(o.objectID)
    return super.equals(o)
  }

  public static String createObjectId(prefix, suffix) {
    "${CognosShibAuthNamespace.namespaceId}:${prefix}:${suffix}"
  }
}
