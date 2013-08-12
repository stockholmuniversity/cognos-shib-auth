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

/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-19
 * Time: 07:10
 * To change this template use File | Settings | File Templates.
 */
package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

import com.cognos.CAM_AAA.authentication.UnrecoverableException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder;
import se.su.it.cognos.cognosshibauth.visa.Visa;


import java.util.logging.Level;
import java.util.logging.Logger;


import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchStep.SearchAxis

import static se.su.it.cognos.cognosshibauth.memcached.Cache.md5Sum
import se.su.it.cognos.cognosshibauth.query.FilterUtil

public class CognosShibAuthBase extends CognosShibAuthNamespace implements INamespaceAuthenticationProviderBase {

  private Logger LOG = Logger.getLogger(CognosShibAuthBase.class.getName());

  HashMap<String,NamespaceFolder> folders = null;

  CognosShibAuthBase() {
    super()

    folders = new HashMap<String,NamespaceFolder>();
  }

  @Override
  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    super.init(iNamespaceConfiguration);

    loadFolders();
  }

  @Override
  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    Visa visa = (Visa) iVisa;
    try {
      visa.destroy();
    } catch (UnrecoverableException e) {
      LOG.log(Level.SEVERE, "Failed to destroy visa '" + visa + "' during logout.");
      e.printStackTrace();
    }
  }

  @Override
  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {

    QueryResult result = new QueryResult();

    try {
      ISearchExpression expression = iQuery.searchExpression
      IQueryOption queryOption = iQuery.queryOption

      String baseObjectID = expression.objectID // The base for the search expression, null == root of namespace
      ISearchStep[] steps = expression.steps

      // TODO handle multiple steps:
      //   IBM Cognos Connection does not use more than one step. Authentication
      //   providers currently supported by IBM Cognos BI do not support more than one
      //   step, either. Only some Software Development Kit applications can generate
      //   queries containing more than one step, therefore a custom authentication provider
      //   is required to support such queries.
      //   From "IBM Cognos Custom Authentication Provider Version 10.1.1 Developer Guide"

      if (steps.length != 1) {
        throw new UnrecoverableException(
                "Internal Error",
                "Invalid search expression. Multiple steps is not supported for this namespace.");
      }

      int searchAxis = steps.first().axis
      ISearchFilter filter = steps.first().predicate

      def filterString = FilterUtil.filterToString(filter)

      def key = md5Sum("${baseObjectID}-${searchAxis}-${filterString}-${queryOption.skipCount}-${queryOption.maxCount}")

      List<IBaseClass> ret = Cache.getInstance().get(key,{
        getQueryResult(searchAxis, baseObjectID, filter, queryOption)
      })

      ret?.each { result.addObject(it) }
    }
    catch (Exception e) {
      //Fetch anything and do nothing (no stack traces in the gui for now)
      LOG.log(Level.SEVERE, "Failed while parsing search query: " + e.getMessage());
      e.printStackTrace();
    }
    return result
  }

  /**
   * Fetches the query results based on the search axis.
   *
   * @param searchAxis the search axis.
   * @param baseObjectID CAM-ID of the base for the search.
   * @param filter the search filter.
   * @param queryOption query options.
   * @return a list of objects matching the supplied axis and filter.
   */
  List<IBaseClass> getQueryResult(int searchAxis, String baseObjectID, ISearchFilter filter, IQueryOption queryOption) {
    def list = []

    QueryUtil queryUtil = new QueryUtil(this, folders)

    switch (searchAxis) {
      case SearchAxis.Self:
        list << queryUtil.searchAxisSelf(baseObjectID)
        break;

      case SearchAxis.Child:
        list.addAll queryUtil.searchAxisChild(baseObjectID, queryOption)
        break;

      case SearchAxis.Descendent:
        def ret = queryUtil.searchAxisDescendant(baseObjectID, filter, queryOption)

        ret?.each { list << it }
        break

      // Not yet implemented
      case SearchAxis.Ancestor:
      case SearchAxis.AncestorOrSelf:
      case SearchAxis.DescendentOrSelf:
      case SearchAxis.Parent:
      case SearchAxis.Unknown:
      default:
        break;
    }

    list
  }

  /**
   *  Loads the folders specified in configuration file
   */
  private void loadFolders() {
    List<HierarchicalConfiguration> foldersConfiguration = configHandler.getFoldersConfig();
    for(HierarchicalConfiguration folderConfiguration : foldersConfiguration) {
      NamespaceFolder namespaceFolder = NamespaceFolder.configEntryToFolder(folders, folderConfiguration);
      folders.put(namespaceFolder.getObjectID(), namespaceFolder);
    }
  }
}
