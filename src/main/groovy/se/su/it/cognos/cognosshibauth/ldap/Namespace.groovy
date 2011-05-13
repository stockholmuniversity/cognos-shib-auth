package se.su.it.cognos.cognosshibauth.ldap

import com.cognos.CAM_AAA.authentication.INamespace
import com.cognos.CAM_AAA.authentication.INamespaceConfiguration
import com.cognos.CAM_AAA.authentication.UnrecoverableException

class Namespace extends UiClass implements INamespace {

  private String[] capabilities;
  private String namespaceFormat;

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

  public void init(INamespaceConfiguration theNamespaceConfiguration)
          throws UnrecoverableException {
    setObjectID(theNamespaceConfiguration.getID());
    addName(theNamespaceConfiguration.getServerLocale(),
            theNamespaceConfiguration.getDisplayName());
  }

  public void destroy() {
  }

  public String getNamespaceFormat() {
    return namespaceFormat;
  }

  public void setNamespaceFormat(String theNamespaceFormat) {
    namespaceFormat = theNamespaceFormat;
  }

  public String[] getCapabilities() {
    return capabilities;
  }

  public boolean getHasChildren() {
    return true;
  }
}
