package se.su.it.cognos.cognosshibauth.adapters;

import com.cognos.CAM_AAA.authentication.INamespaceFolder;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;

import java.util.Locale;

public class NamespaceFolder extends UiClass implements INamespaceFolder {

  private Locale defaultLocale = null;

  public NamespaceFolder(String theSearchPath, String name) {
    super(theSearchPath + ":" + PREFIX_FOLDER + name);

    ConfigHandler configHandler = ConfigHandler.instance();
    defaultLocale = configHandler.getContentLocale();

    addName(defaultLocale, name);
  }

  public boolean getHasChildren() {
    return true;
  }
}
