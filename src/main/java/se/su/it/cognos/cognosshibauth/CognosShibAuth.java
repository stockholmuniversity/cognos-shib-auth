/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-04
 * Time: 12:54
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

public class CognosShibAuth implements INamespaceAuthenticationProvider2 {

  String namespaceFormat = null;
  String capabilities[] = null;
  String objectId = null;
  private Logger LOG = Logger.getLogger(ConfigHandler.class.getName());

  private ConfigHandler configHandler = null;

  public CognosShibAuth() {
    configHandler = ConfigHandler.instance();
  }

  public IVisa logon(IBiBusHeader2 iBiBusHeader2) throws UserRecoverableException, SystemRecoverableException,
          UnrecoverableException {
    CognosShibAuthVisa cognosShibAuthVisa = new CognosShibAuthVisa(configHandler);

    String remoteUser = getHeaderValue(iBiBusHeader2, configHandler.getHeaderRemoteUser(), true);
    String givenName = getHeaderValue(iBiBusHeader2, configHandler.getHeaderGivenName(), true);
    String surname = getHeaderValue(iBiBusHeader2, configHandler.getHeaderSurname(), true);
    Locale contentLocale = configHandler.getContentLocale();

    String entitlement = getHeaderValue(iBiBusHeader2, configHandler.getHeaderEntitlement(), false);

    CognosShibAuthAccount cognosShibAuthAccount =
            new CognosShibAuthAccount("u:" + remoteUser, remoteUser, givenName, surname, contentLocale);

    String mail = getHeaderValue(iBiBusHeader2, configHandler.getHeaderMail(), true);
    cognosShibAuthAccount.setEmail(mail);

    String businessPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderBusinessPhone(), false);
    cognosShibAuthAccount.setBusinessPhone(businessPhone);

    String homePhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderHomePhone(), false);
    cognosShibAuthAccount.setHomePhone(homePhone);

    String mobilePhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderMobilePhone(), false);
    cognosShibAuthAccount.setMobilePhone(mobilePhone);

    String faxPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderFaxPhone(), false);
    cognosShibAuthAccount.setFaxPhone(faxPhone);

    String pagerPhone = getHeaderValue(iBiBusHeader2, configHandler.getHeaderPagerPhone(), false);
    cognosShibAuthAccount.setPagerPhone(pagerPhone);

    String postalAddress = getHeaderValue(iBiBusHeader2, configHandler.getHeaderPostalAddress(), false);
    cognosShibAuthAccount.setPostalAddress(postalAddress);

    String gmaiRole = filterGmaiRole(entitlement);
    CognosShibAuthRole role = new CognosShibAuthRole("Cognos Shibb Authenticator:r:"+gmaiRole);
    role.addName(contentLocale, gmaiRole);
    cognosShibAuthVisa.addRole(role);

    String gmaiGroup = filterGmaiDepartment(entitlement);
    CognosShibAuthGroup group = new CognosShibAuthGroup("\"Cognos Shibb Authenticator:g:"+gmaiGroup);
    group.addMember(cognosShibAuthAccount);
    cognosShibAuthVisa.addGroup(group);

    cognosShibAuthVisa.init(cognosShibAuthAccount);

    return cognosShibAuthVisa;
  }

  private String getHeaderValue(IBiBusHeader2 iBiBusHeader2, String header, boolean required)
          throws SystemRecoverableException {
    String[] headerValue = iBiBusHeader2.getEnvVarValue(header); //TODO: Use getTrustedEnvVarValue when releasing stable.

    if(headerValue == null) {
      LOG.log(Level.INFO, "Header '" + header + "' not found.");
      if(required) { // Value not found in trusted environment variables.
        LOG.log(Level.SEVERE, "Header '" + header + "' required but not found, throwing SystemRecoverableException");
        throw new SystemRecoverableException("Missing required header '" + header + "'.", header);
      }
    }
    else {
      String values = "";
      for(String s : headerValue)
        values += s + ", ";
      LOG.log(Level.FINEST, "Values in '" + header + "': " + values);

      if(headerValue.length < 1)
        headerValue = null;
    }

    return headerValue == null ? null : headerValue[0];
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

  public void init(INamespaceConfiguration iNamespaceConfiguration) throws UnrecoverableException {
    LOG.log(Level.FINEST, "intit method reached");

    objectId = iNamespaceConfiguration.getID();
    LOG.log(Level.FINE, "ObjectID set to '" + objectId + "'.");

    List<String> capabilitiesList = configHandler.getCapabilities();
    capabilities = capabilitiesList.toArray(new String[capabilitiesList.size()]);
  }

  public void destroy() {}

  public String getNamespaceFormat() {
    return namespaceFormat;
  }

  public void setNamespaceFormat(String s) throws UnrecoverableException {
    namespaceFormat = s;
  }

  public String[] getCapabilities() {
    return capabilities;
  }

  public String getDescription(Locale locale) {
    return configHandler.getDescription(locale);
  }

  public Locale[] getAvailableDescriptionLocales() {
    List<Locale> locales = configHandler.getDescriptionLocales();
    return locales.toArray(new Locale[locales.size()]);
  }

  public IBaseClass[] getAncestors() {
    // TODO: Implement something smart.
    return null;
  }

  public boolean getHasChildren() {
    return false;
  }

  public String getName(Locale locale) {
    return configHandler.getName(locale);
  }

  public Locale[] getAvailableNameLocales() {
    List<Locale> locales = configHandler.getDescriptionLocales();
    return locales.toArray(new Locale[locales.size()]);
  }

  public String getObjectID() {
    return objectId;
  }

  private String filterGmaiRole(String gmai){
    // urn:mace:swami.se:gmai:su-ivs:analyst:departmentNumber=647
    int startGmai = gmai.indexOf("su-ivs:") + 6;
    int stopGmai = gmai.lastIndexOf(":");
    String role = gmai.substring(startGmai, stopGmai);
    return role;
  }

  private String filterGmaiDepartment(String gmai){
    // urn:mace:swami.se:gmai:su-ivs:analyst:departmentNumber=647
    int startGmai = gmai.indexOf("departmentNumber=") + 17;
    String group = gmai.substring(startGmai);
    return group;
  }



}
