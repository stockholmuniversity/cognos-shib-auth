package se.su.it.cognos.cognosshibauth.adapters;

import com.cognos.CAM_AAA.authentication.INamespaceFolder;
import com.cognos.CAM_AAA.authentication.IUiClass;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NamespaceFolder extends UiClass implements INamespaceFolder {

  private Locale defaultLocale = null;

  private List<IUiClass> children = null;

  public NamespaceFolder(String theSearchPath, String name) {
    super(theSearchPath + ":" + PREFIX_FOLDER + name);

    children = new ArrayList<IUiClass>();

    ConfigHandler configHandler = ConfigHandler.instance();
    defaultLocale = configHandler.getContentLocale();

    addName(defaultLocale, name);
  }

  public boolean getHasChildren() {
    return children.size() > 0;
  }

  public void addDescription(String description) {
    addDescription(defaultLocale, description);
  }

  public void addChild(IUiClass iUiClass) {
    children.add(iUiClass);
  }

  public List<IUiClass> getChildren() {
    return children;
  }
}
