/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-19
 * Time: 07:10
 * To change this template use File | Settings | File Templates.
 */
package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

import com.cognos.CAM_AAA.authentication.SystemRecoverableException;
import com.cognos.CAM_AAA.authentication.UnrecoverableException;
import com.cognos.CAM_AAA.authentication.UserRecoverableException;
import org.apache.commons.lang.ArrayUtils;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthAccount;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthGroup;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthRole;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthVisa;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CognosShibAuthBase extends CognosShibAuthNamespace implements INamespaceAuthenticationProviderBase {

  private Logger LOG = Logger.getLogger(CognosShibAuthBase.class.getName());

 public CognosShibAuthBase(ConfigHandler configHandler) {
    super(configHandler);
  }

  public void logoff(IVisa iVisa, IBiBusHeader iBiBusHeader) {
    CognosShibAuthVisa cognosShibAuthVisa = (CognosShibAuthVisa) iVisa;
    try {
      cognosShibAuthVisa.destroy();
    } catch (UnrecoverableException e) {
      LOG.log(Level.SEVERE, "Failed to destroy visa '" + cognosShibAuthVisa + "' during logout.");
      e.printStackTrace();
    }
  }

  public IQueryResult search(IVisa iVisa, IQuery iQuery) throws UnrecoverableException {

    // We can safely assume that we'll get back the same Visa that we issued.
    CognosShibAuthVisa visa = (CognosShibAuthVisa) iVisa;
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
            else if (objectID.startsWith("u:") && objectID.equals(visa.getAccount().getObjectID())) {
              if (filter == null || true) {//this.matchesFilter(filter)) {
                result.addObject(visa.getAccount());
                // Add current user
              }
              return result;
            }
            else if (objectID.startsWith("u:") || objectID.startsWith("r:")) {
              //String sqlID = objectID.substring(2);
              //sqlCondition.append(QueryUtil.getSqlCondition(filter));
              //if (sqlCondition.length() > 0) {
              //  sqlCondition.append(" AND ");
              //}
              //sqlCondition.append("uid = " + sqlID);
            }
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
    result.addObject(visa.getAccount());
    return result;
  }
}