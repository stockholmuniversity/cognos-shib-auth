
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;


public class CognosShibAuthRole extends CognosShibAuthUiClass implements IRole
{

  private Vector	members;

  public CognosShibAuthRole(String theSearchPath)
  {
    super(theSearchPath);
    members = null;
  }

  public void addMember(IBaseClass theMember)
  {
    if (members == null)
    {
      members = new Vector();
    }
    members.add(theMember);
  }

  public IBaseClass[] getMembers()
  {
    if (members != null)
    {
      IBaseClass[] array = new IBaseClass[members.size()];
      return (IBaseClass[]) members.toArray(array);
    }
    return null;
  }


}
