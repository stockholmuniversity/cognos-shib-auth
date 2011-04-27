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
import se.su.it.cognos.cognosshibauth.adapters.NamespaceFolder;
import se.su.it.cognos.cognosshibauth.visa.Visa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CognosShibAuthBase extends CognosShibAuthNamespace implements INamespaceAuthenticationProviderBase {

  private Logger LOG = Logger.getLogger(CognosShibAuthBase.class.getName());

 public CognosShibAuthBase(ConfigHandler configHandler) {
    super(configHandler);
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
		case ISearchStep.SearchAxis.DescendentOrSelf :
		  {
		    if (objectID == null) {
			  if (filter == null || true) {//this.matchesFilter(filter)) {
                result.addObject(this);
				// Add current namespace
			  }

              if (searchType == ISearchStep.SearchAxis.Self) {
				return result;
              }
              else {
                //sqlCondition.append(QueryUtil.getSqlCondition(filter));
              }
            }
            else if (objectID.equals("u:") && filter == null) {
              result.addObject(visa.getAccount());
            }
            else if (objectID.startsWith(namespaceId + ":u:") && objectID.equals(visa.getAccount().getObjectID())) {
              if (filter == null || true) {//this.matchesFilter(filter)) {
                result.addObject(visa.getAccount());
                // Add current user
              }
              return result;
            }
            else if (objectID.startsWith(namespaceId + ":u:") || objectID.startsWith(namespaceId + ":r:")) {
              result.addObject(visa.getRoles()[0]);
            }
            else if (objectID.startsWith(namespaceId + ":u:") || objectID.startsWith(namespaceId + ":g:")) {
              result.addObject(visa.getGroups()[0]);
            }
          }
          break;
        case ISearchStep.SearchAxis.Child :
          if(objectID == null) {
            result.addObject(new NamespaceFolder(namespaceId + ":f:FooFolder", "Foo Folder", Locale.ENGLISH));
            result.addObject(visa.getAccount());
            result.addObject(visa.getRoles()[0]);
            result.addObject(visa.getGroups()[0]);
          }
          break;
        default :
        {
          //sqlCondition.append(QueryUtil.getSqlCondition(filter));
        }
        break;
      }
      //QueryUtil.query(MS_JDBCDriver.driver, visa.getConnection(),
      //        sqlCondition.toString(), theQuery.getQueryOption(),
      //        theQuery.getProperties(), theQuery.getSortProperties(),
      //        result, this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result;
  }
}