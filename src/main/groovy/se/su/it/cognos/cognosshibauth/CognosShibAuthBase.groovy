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


import static se.su.it.cognos.cognosshibauth.ldap.UiClass.*
import se.su.it.cognos.cognosshibauth.memcached.Cache
import com.cognos.CAM_AAA.authentication.ISearchStep.SearchAxis

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

      def key = "${baseObjectID}-${searchAxis}-${filter?.getSearchFilterType()}-${queryOption.skipCount}-${queryOption.maxCount}"

      def closure = { getQueryResult(searchAxis, baseObjectID, filter, queryOption) }

      List<IBaseClass> ret = null
      if(searchAxis != SearchAxis.Descendent)
        ret = Cache.getInstance().get(key, closure)
      else
        ret = closure()

      ret?.each { result.addObject(it) }
    }
    catch (Exception e) {
      //Fetch anything and do nothing (no stack traces in the gui for now)
      LOG.log(Level.SEVERE, "Failed while parsing search query: " + e.getMessage());
      e.printStackTrace();
    }
    return result
  }

  def getQueryResult(int searchAxis, String baseObjectID, ISearchFilter filter, IQueryOption queryOption) {
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
        def ret = queryUtil.searchAxisDescendent(baseObjectID, filter, queryOption)

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