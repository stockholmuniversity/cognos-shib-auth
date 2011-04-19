package se.su.it.cognos.cognosshibauth.adapters;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cognos.CAM_AAA.authentication.IBaseClass;
import com.cognos.CAM_AAA.authentication.IRole;

public class CognosShibAuthRole extends CognosShibAuthUiClass implements IRole {

  private List<IBaseClass> members = null;

  public CognosShibAuthRole(String theSearchPath) {
    super(theSearchPath);
    members = new ArrayList<IBaseClass>();
  }

  public void addMember(IBaseClass theMember) {
    members.add(theMember);
  }

  public IBaseClass[] getMembers() {
    return members.toArray(new IBaseClass[members.size()]);
  }
}
