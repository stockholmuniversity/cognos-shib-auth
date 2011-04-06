
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.ICredential;


public class CognosShibAuthCredential implements ICredential
{
  private HashMap	credentials;

  public CognosShibAuthCredential()
  {
    super();
    credentials = null;
  }

  public String[] getCredentialNames()
  {
    if (credentials != null)
    {
      Set keySet = credentials.keySet();
      String[] array = new String[keySet.size()];
      return (String[]) keySet.toArray(array);
    }
    return null;
  }


  public void addCredentialValue(String theName, String theValue)
  {
    if (credentials == null)
    {
      credentials = new HashMap();
    }
    Vector v = (Vector) this.credentials.get(theName);
    if (v == null)
    {
      v = new Vector();
      this.credentials.put(theName, v);
    }
    v.add(theValue);
  }


  public String[] getCredentialValue(String theName)
  {
    if (credentials != null)
    {
      Vector v = (Vector) this.credentials.get(theName);
      if (v != null)
      {
        return (String[]) v.toArray(new String[v.size()]);
      }
    }
    return null;
  }

}
