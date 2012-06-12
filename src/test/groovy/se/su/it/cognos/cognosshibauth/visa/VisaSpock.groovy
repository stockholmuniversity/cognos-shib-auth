package se.su.it.cognos.cognosshibauth.visa;

import static org.junit.Assert.*
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import se.su.it.cognos.cognosshibauth.visa.validator.DummyVisaValidator

import java.lang.reflect.Field
import com.cognos.CAM_AAA.authentication.IAccount
import org.spockframework.runtime.Sputnik
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(Sputnik)
public class VisaSpock extends Specification {

  def "Test that init loads VisaValidator from conf"() {
    setup:
    ConfigHandler configHandler = ConfigHandler.instance()
    Visa visa = new Visa(configHandler)

    IAccount account = [ getUserName: { 'test' } ] as IAccount

    visa.init(account)

    Field field = Visa.class.getDeclaredField("visaValidator")
    field.setAccessible(true)

    when:
    Object obj = field.get(visa)

    then:
    assertTrue(obj instanceof DummyVisaValidator)
  }
}