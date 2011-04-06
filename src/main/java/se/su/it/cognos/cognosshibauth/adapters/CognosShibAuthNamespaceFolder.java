
package se.su.it.cognos.cognosshibauth.adapters;
import com.cognos.CAM_AAA.authentication.INamespaceFolder;


public class CognosShibAuthNamespaceFolder extends CognosShibAuthUiClass implements INamespaceFolder
{

  public CognosShibAuthNamespaceFolder(String theSearchPath)
  {
    super(theSearchPath);
  }

  public boolean getHasChildren()
  {
    return true;
  }
}
