package se.su.it.cognos.cognosshibauth.visa.validator;

import com.cognos.CAM_AAA.authentication.IVisa;
import se.su.it.cognos.cognosshibauth.visa.VisaValidator;

public class DummyVisaValidator implements VisaValidator {

  @Override
  public void init(IVisa iVisa) {
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void destroy() {
  }
}
