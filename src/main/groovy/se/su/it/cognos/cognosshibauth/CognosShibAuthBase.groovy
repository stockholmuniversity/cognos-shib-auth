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
import se.su.it.cognos.cognosshibauth.ldap.Account;
import se.su.it.cognos.cognosshibauth.ldap.Group;
import se.su.it.cognos.cognosshibauth.ldap.NamespaceFolder;
import se.su.it.cognos.cognosshibauth.ldap.Role;
import se.su.it.cognos.cognosshibauth.visa.Visa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import static se.su.it.cognos.cognosshibauth.ldap.UiClass.*
import se.su.it.cognos.cognosshibauth.memcached.MyCache;

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
      ISearchExpression expression = iQuery.getSearchExpression();
      String objectID = expression.getObjectID();
      ISearchStep[] steps = expression.getSteps();


      //TODO handle hierarchical steps
      if (steps.length != 1) {
        throw new UnrecoverableException(
                "Internal Error",
                "Invalid search expression. Multiple steps is not supported for this namespace.");
      }

      int searchType = steps.first().axis
      ISearchFilter filter = steps[0].getPredicate();

      int filterType = filter == null ? 0 : filter.getSearchFilterType()

      String key = objectID + searchType + filterType;


      ArrayList ret = MyCache.getInstance().get(key);
      if(ret != null){
        Iterator iter = ret.iterator();
        while(iter.hasNext()){
          result.addObject(iter.next());
        }
      }
      else{
        ret = new ArrayList();
        switch (searchType) {
          case ISearchStep.SearchAxis.Self :
            if (objectID == null) {
              if (filter == null || true) {
                result.addObject(this);
                ret.add(this);
              }
              if (searchType == ISearchStep.SearchAxis.Self) {
                return result;
              }
            }
            else if (isUser(objectID) && filter == null) {
              String dn = camIdToName(objectID);
              Account account = Account.createFromDn(dn);
              ret.add(account);
              result.addObject(account);
            }
            else if (isRole(objectID)) {
              Role role = new Role(camIdToName(objectID));
              ret.add(role);
              result.addObject(role);
            }
            else if (isGroup(objectID)) {
              Group group = new Group(camIdToName(objectID));
              ret.add(group);
              result.addObject(group);
            }
            else if(isFolder(objectID)) {
              ret.add(folders.get(objectID));
              result.addObject(folders.get(objectID));
            }
            break;
          case ISearchStep.SearchAxis.Child :
            if(objectID == null) {
              for(NamespaceFolder folder : folders.values()) {
                if(folder.getAncestors().length <= 0){
                  ret.add(folder);
                  result.addObject(folder);
                }
              }
            }
            else if(isFolder(objectID) && folders.containsKey(objectID)) {
              NamespaceFolder folder = folders.get(objectID);
              for(IUiClass child : folder.getChildren()){
                ret.add(child);
                result.addObject(child);
              }
            }
            else if(isRole(objectID)) {
              Role role = new Role(camIdToName(objectID));
              for(IBaseClass member : role.getMembers()) {
                ret.add(member);
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
        MyCache.instance.set(key, 3600, ret);
      }

    }
    catch (Exception e) {
      //Fetch anything and do nothing (no stack traces in the gui for now)
      LOG.log(Level.SEVERE, "Failed while parsing search query: " + e.getMessage());
      e.printStackTrace();
    }
    return result;

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

  public static byte[] toBytes(Object object){
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    try{
      java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
      oos.writeObject(object);
      oos.flush();
      oos.close();
      baos.flush();
      baos.close();
    }catch(java.io.IOException ioe){
    }
    return baos.toByteArray();
  }


  public static Object toObject(byte[] bytes){
    Object object = null;
    try{
      java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
      java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
      object = ois.readObject();
      ois.close();
      bais.close();
    }catch(java.io.IOException ioe){
    }catch(java.lang.ClassNotFoundException cnfe){
    }
    return object;
  }

}