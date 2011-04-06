
package se.su.it.cognos.cognosshibauth.adapters;
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IGroup;


public class CognosShibAuthGroup extends CognosShibAuthUiClass implements IGroup
{
  private Vector	members;

  public CognosShibAuthGroup(String theObjectID)
  {
    super(theObjectID);
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
