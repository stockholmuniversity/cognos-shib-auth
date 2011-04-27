package se.su.it.cognos.cognosshibauth.adapters;

import com.cognos.CAM_AAA.authentication.INamespaceFolder;
import java.util.Locale;

public class NamespaceFolder extends UiClass implements INamespaceFolder {

  private Locale defaultLocale = null;

  public NamespaceFolder(String theSearchPath, String name, Locale defaultLocale) {
    super(theSearchPath);

    this.defaultLocale = defaultLocale;

    addName(defaultLocale, name);
  }

  public boolean getHasChildren() {
    return true;
  }
}
