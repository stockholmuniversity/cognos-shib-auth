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
import se.su.it.cognos.cognosshibauth.adapters.*;
import se.su.it.cognos.cognosshibauth.ldap.Account;
import se.su.it.cognos.cognosshibauth.ldap.Group;
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder;
import se.su.it.cognos.cognosshibauth.ldap.Role;
import se.su.it.cognos.cognosshibauth.visa.Visa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.sukat.SUKAT;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static se.su.it.cognos.cognosshibauth.ldap.UiClass.*;

public class CognosShibAuthBase extends CognosShibAuthNamespace implements INamespaceAuthenticationProviderBase {

  private Logger LOG = Logger.getLogger(CognosShibAuthBase.class.getName());

  HashMap<String,NamespaceFolder> folders = null;

  public CognosShibAuthBase(ConfigHandler configHandler) {
    super(configHandler);

    folders = new HashMap<String,NamespaceFolder>();
  }

  @Override
  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    super.init(iNamespaceConfiguration);
    
    loadFolders();
  }

  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    Visa visa = (Visa) iVisa;
    try {
      visa.destroy();
    } catch (UnrecoverableException e) {
      LOG.log(Level.SEVERE, "Failed to destroy visa '" + visa + "' during logout.");
      e.printStackTrace();
    }
  }

  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {

    // We can safely assume that we'll get back the same Visa that we issued.
    Visa visa = (Visa) iVisa;
	QueryResult result = new QueryResult();

    try {
      ISearchExpression expression = iQuery.getSearchExpression();
      String objectID = expression.getObjectID();
      ISearchStep[] steps = expression.getSteps();

      // It doesn't make sense to have multiple steps for this provider
      // since the objects are not hierarchical.
      if (steps.length != 1) {
        throw new UnrecoverableException(
                "Internal Error",
                "Invalid search expression. Multiple steps is not supported for this namespace.");
      }

	  int searchType = steps[0].getAxis();
	  ISearchFilter filter = steps[0].getPredicate();

	  switch (searchType) {
	    case ISearchStep.SearchAxis.Self :
		  {
		    if (objectID == null) {
			  if (filter == null || true) {
                result.addObject(this);
			  }

              if (searchType == ISearchStep.SearchAxis.Self) {
				return result;
              }
            }
            else if (isUser(objectID) && filter == null) {
              String uid = camIdToName(objectID);
              SUKAT sukat = SUKAT.newInstance(configHandler.getStringEntry("adapters.url"));
              Account account = Account.findByUid(namespaceId, uid);
              result.addObject(account);
            }
            else if (isRole(objectID)) {
              Role role = new Role(namespaceId, camIdToName(objectID));
              result.addObject(role);
            }
            else if (isGroup(objectID)) {
              Group group = new Group(namespaceId, camIdToName(objectID));
              result.addObject(group);
            }
            else if(isFolder(objectID)) {
              result.addObject(folders.get(objectID));
            }
          }
          break;
        case ISearchStep.SearchAxis.Child :
          if(objectID == null) {
            for(NamespaceFolder folder : folders.values()) {
              if(folder.getAncestors().length <= 0)
                result.addObject(folder);
            }
          }
          else if(isFolder(objectID) && folders.containsKey(objectID)) {
            NamespaceFolder folder = folders.get(objectID);
            for(IUiClass child : folder.getChildren())
              result.addObject(child);
          }
          else if(isRole(objectID)) {
            Role role = new Role(namespaceId, camIdToName(objectID));
            for(IBaseClass member : role.getMembers()) {
              result.addObject(member);
            }
          }
          break;
        case ISearchStep.SearchAxis.Descendent :
          //Involved in text searches.
          break;
        default :
        break;
      }
    }
    catch (Exception e) {
      //Fetch anything and do nothing (no stack traces in the gui for now)
      LOG.log(Level.SEVERE, "Failed while parsing search query: " + e.getMessage());
      e.printStackTrace();
    }
    return result;
  }

  private void loadFolders() {
    List<HierarchicalConfiguration> foldersConfiguration = configHandler.getFoldersConfig();

    for(HierarchicalConfiguration folderConfiguration : foldersConfiguration) {
      NamespaceFolder namespaceFolder = NamespaceFolder.configEntryToFolder(folders, folderConfiguration, namespaceId);

      folders.put(namespaceFolder.getObjectID(), namespaceFolder);
    }
  }
}