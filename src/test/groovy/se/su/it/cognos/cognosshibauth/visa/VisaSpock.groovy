package se.su.it.cognos.cognosshibauth.visa;

import static org.junit.Assert.*
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import se.su.it.cognos.cognosshibauth.visa.validator.DummyVisaValidator

import java.lang.reflect.Field
import com.cognos.CAM_AAA.authentication.IAccount
import org.spockframework.runtime.Sputnik
import org.junit.runner.RunWith
import spock.lang.Specification
import com.cognos.CAM_AAA.authentication.IBiBusHeader
import spock.lang.Unroll
import se.su.it.cognos.cognosshibauth.ldap.Account
import com.cognos.CAM_AAA.authentication.UnrecoverableException

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

  def "Test generateCredentials no exception"() {
    setup:
    Visa target = new Visa(null)

    IAccount account = [ getUserName: { 'jolu' } ] as IAccount
    VisaValidator visaValidator = [ isValid: { true }] as VisaValidator

    Visa.metaClass.setProperty(Visa, target, 'visaValidator', visaValidator, false, true)
    Visa.metaClass.setProperty(Visa, target, 'account', account, false, true)

    when:
    def result = target.generateCredential(null)

    then:
    notThrown(UnrecoverableException)
    result.getCredentialValue('username').first() == 'jolu'
  }

  def "Test generateCredentials throws exception for invalid visa"() {
    setup:
    Visa target = new Visa(null)

    IAccount account = [ getUserName: { 'jolu' } ] as IAccount
    VisaValidator visaValidator = [ isValid: { false }] as VisaValidator

    Visa.metaClass.setProperty(Visa, target, 'visaValidator', visaValidator, false, true)
    Visa.metaClass.setProperty(Visa, target, 'account', account, false, true)

    when:
    def result = target.generateCredential(null)

    then:
    thrown(UnrecoverableException)
  }
}