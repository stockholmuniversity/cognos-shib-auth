package se.su.it.cognos.cognosshibauth.visa.validator;

import com.cognos.CAM_AAA.authentication.IAccount;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthAccount;
import se.su.it.cognos.cognosshibauth.visa.VisaValidator;

/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-18
 * Time: 09:04
 * To change this template use File | Settings | File Templates.
 */
public class DummyVisaValidator implements VisaValidator {

  @Override
  public void init(IAccount iAccount) {
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void destroy() {
  }
}
