
package se.su.it.cognos.cognosshibauth.adapters;
import com.cognos.CAM_AAA.authentication.INamespaceFolder;


public class NamespaceFolder extends UiClass implements INamespaceFolder
{

  public NamespaceFolder(String theSearchPath)
  {
    super(theSearchPath);
  }

  public boolean getHasChildren()
  {
    return true;
  }
}
