package se.su.it.cognos.cognosshibauth.visa;
/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-18
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import org.powermock.modules.junit4.PowerMockRunner;
import se.su.it.cognos.cognosshibauth.ldap.Account;
import se.su.it.cognos.cognosshibauth.config.ConfigHandler;
import se.su.it.cognos.cognosshibauth.visa.validator.DummyVisaValidator;

import java.lang.reflect.Field;

@RunWith(PowerMockRunner.class)
public class VisaTest {
/*  @Mock
  Account account;

  @Test
  public void testThatInitLoadsVisaValidatorFromConf() throws Exception {
    ConfigHandler configHandler = ConfigHandler.instance();
    Visa visa = new Visa(configHandler);

    when(account.getUserName()).thenReturn("test");

    visa.init(account);

    Field field = Visa.class.getDeclaredField("visaValidator");
    field.setAccessible(true);
    Object obj = field.get(visa);

    assertTrue(obj instanceof DummyVisaValidator);
  } */
}