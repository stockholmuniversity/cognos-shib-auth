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
import se.su.it.cognos.cognosshibauth.adapters.Account;
import se.su.it.cognos.cognosshibauth.adapters.NamespaceFolder;
import se.su.it.cognos.cognosshibauth.adapters.UiClass;
import se.su.it.cognos.cognosshibauth.visa.Visa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.sukat.EnterpriseDirectory;
import se.su.it.sukat.SUKAT;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

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
          if(isFolder(objectID)) {
            
          }
		case ISearchStep.SearchAxis.DescendentOrSelf :
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
              result.addObject(visa.getAccount());
            }
            else if (isUser(objectID) && objectID.equals(visa.getAccount().getObjectID())) {
              if (filter == null || true) {
                result.addObject(visa.getAccount());
              }
              return result;
            }
            else if (isUser(objectID) || isRole(objectID)) {
              result.addObject(visa.getRoles()[0]);
            }
            else if (isUser(objectID) || isGroup(objectID)) {
              result.addObject(visa.getGroups()[0]);
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
          break;
        default :
        break;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result;
  }

  private boolean isFolder(String objectId) {
    if(objectId == null)
      return false;
    return objectId.startsWith(namespaceId + ":" + UiClass.PREFIX_FOLDER + ":");
  }

  private boolean isGroup(String objectId) {
    if(objectId == null)
      return false;
    return objectId.startsWith(namespaceId + ":" + UiClass.PREFIX_GROUP + ":");
  }

  private boolean isRole(String objectId) {
    if(objectId == null)
      return false;
    return objectId.startsWith(namespaceId + ":" + UiClass.PREFIX_ROLE + ":");
  }

  private boolean isUser(String objectId) {
    if(objectId == null)
      return false;
    return objectId.startsWith(namespaceId + ":" + UiClass.PREFIX_USER + ":");
  }

  private void loadFolders() {
    List<HierarchicalConfiguration> foldersConfiguration = configHandler.getFoldersConfig();

    for(HierarchicalConfiguration folderConfiguration : foldersConfiguration) {
      NamespaceFolder namespaceFolder = configEntryToFolder(folderConfiguration);

      folders.put(namespaceFolder.getObjectID(), namespaceFolder);
    }
  }

  private NamespaceFolder configEntryToFolder(HierarchicalConfiguration folderEntry) {
    String name = folderEntry.getString("name");
    String description = folderEntry.getString("description");

    //TODO: Handle CAMID conflicts
    NamespaceFolder folder = new NamespaceFolder(namespaceId, name);
    folder.addDescription(description);

    //TODO: Do something with the groups
    List<HierarchicalConfiguration> groups = folderEntry.configurationsAt("children.groups");
    for(HierarchicalConfiguration group : groups) {
      folder.addGroupLdapFilter(group.getString("ldap_filter"));
    }

    //TODO: Do something with the users
    List<HierarchicalConfiguration> users = folderEntry.configurationsAt("children.users");
    for(HierarchicalConfiguration user : users) {
      folder.addUserLdapFilter(user.getString("ldap_filter"));
    }

    //TODO: Do something with the roles
    List<HierarchicalConfiguration> roles = folderEntry.configurationsAt("children.roles");
    for(HierarchicalConfiguration role : roles) {
      folder.addRoleLdapFilter(role.getString("ldap_filter"));
    }

    List<HierarchicalConfiguration> foldersConfig = folderEntry.configurationsAt("children.folder");
    for(HierarchicalConfiguration folderConfig : foldersConfig) {
      NamespaceFolder childFolder = configEntryToFolder(folderConfig);
      if(childFolder != null) {
        folder.addChild(childFolder);
        childFolder.addAncestors(folder);
        folders.put(childFolder.getObjectID(), childFolder);
      }
    }

    return folder;
  }
}