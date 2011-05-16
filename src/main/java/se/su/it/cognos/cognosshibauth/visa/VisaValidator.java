package se.su.it.cognos.cognosshibauth.visa;

import com.cognos.CAM_AAA.authentication.IVisa;

/**
 * Interface for validating Visas.
 */
public interface VisaValidator {

  /**
   * Init the validator.
   *
   * @param iVisa the account to validate
   */
  public void init(IVisa iVisa);

  /**
   * The method called upon validation.
   *
   * @return true if Visa is valid.
   */
  public boolean isValid();

  /**
   * Destroy the validator.
   */
  public void destroy();
}
