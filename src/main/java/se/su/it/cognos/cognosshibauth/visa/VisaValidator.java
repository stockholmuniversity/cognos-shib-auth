package se.su.it.cognos.cognosshibauth.visa;

import com.cognos.CAM_AAA.authentication.IAccount;
import se.su.it.cognos.cognosshibauth.adapters.CognosShibAuthAccount;

/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-18
 * Time: 08:57
 * To change this template use File | Settings | File Templates.
 */
public interface VisaValidator {

  public void init(IAccount iAccount);

  public boolean isValid();

  public void destroy();
}
