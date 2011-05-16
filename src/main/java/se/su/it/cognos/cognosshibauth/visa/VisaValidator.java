package se.su.it.cognos.cognosshibauth.visa;

import com.cognos.CAM_AAA.authentication.IAccount;

/**
 * Interface for validating Visas.
 */
public interface VisaValidator {

  /**
   * Init the validator.
   *
   * @param iAccount the account to validate
   */
  public void init(IAccount iAccount);

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
