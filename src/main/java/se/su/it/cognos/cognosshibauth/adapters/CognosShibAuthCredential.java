package se.su.it.cognos.cognosshibauth.adapters;

import java.util.*;

import com.cognos.CAM_AAA.authentication.ICredential;

public class CognosShibAuthCredential implements ICredential {

  private HashMap<String, List<String>> credentials = null;

  public CognosShibAuthCredential() {
    super();
    credentials = new HashMap<String, List<String>>();
  }

  public String[] getCredentialNames() {
    Set<String> keySet = credentials.keySet();
    return keySet.toArray(new String[keySet.size()]);
  }

  public void addCredentialValue(String theName, String theValue) {
    List<String> list = credentials.get(theName);

    if (list == null) {
      list = new ArrayList<String>();
      credentials.put(theName, list);
    }

    list.add(theValue);
  }

  public String[] getCredentialValue(String theName) {
    List<String> list = credentials.get(theName);
    if (list != null)
      return (String[]) list.toArray(new String[list.size()]);
    return null;
  }
}
