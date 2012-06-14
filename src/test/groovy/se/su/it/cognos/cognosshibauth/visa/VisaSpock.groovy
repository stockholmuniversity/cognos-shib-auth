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
import com.cognos.CAM_AAA.authentication.UnrecoverableException
import com.cognos.CAM_AAA.authentication.SystemRecoverableException
import com.cognos.CAM_AAA.authentication.ITrustedCredential

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
    target.generateCredential(null)

    then:
    thrown(UnrecoverableException)
  }

  def "Test generateTrustedCredentials throws exception for invalid visa"() {
    setup:
    Visa target = new Visa(null)

    IAccount account = [ getUserName: { 'jolu' } ] as IAccount
    VisaValidator visaValidator = [ isValid: { false }] as VisaValidator

    Visa.metaClass.setProperty(Visa, target, 'visaValidator', visaValidator, false, true)
    Visa.metaClass.setProperty(Visa, target, 'account', account, false, true)

    IBiBusHeader iBiBusHeader = Mock()
    iBiBusHeader.getTrustedEnvVarValue('username') >> 'jolu'

    when:
    target.generateTrustedCredential(iBiBusHeader)

    then:
    thrown(UnrecoverableException)
  }

  def "Test generateTrustedCredentials throws exception for no username"() {
    setup:
    Visa target = new Visa(null)

    IAccount account = [ getUserName: { 'jolu' } ] as IAccount
    VisaValidator visaValidator = [ isValid: { false }] as VisaValidator

    Visa.metaClass.setProperty(Visa, target, 'visaValidator', visaValidator, false, true)
    Visa.metaClass.setProperty(Visa, target, 'account', account, false, true)

    IBiBusHeader iBiBusHeader = Mock()

    when:
    target.generateTrustedCredential(iBiBusHeader)

    then:
    thrown(SystemRecoverableException)
  }

  def "Test generateTrustedCredentials throws exception for username != account.userName"() {
    setup:
    Visa target = new Visa(null)

    IAccount account = [ getUserName: { 'jolu' } ] as IAccount
    VisaValidator visaValidator = [ isValid: { false }] as VisaValidator

    Visa.metaClass.setProperty(Visa, target, 'visaValidator', visaValidator, false, true)
    Visa.metaClass.setProperty(Visa, target, 'account', account, false, true)

    IBiBusHeader iBiBusHeader = Mock()
    iBiBusHeader.getTrustedEnvVarValue('username') >> ['foo']

    when:
    target.generateTrustedCredential(iBiBusHeader)

    then:
    thrown(UnrecoverableException)
  }

  @Unroll
  def "Test generateTrustedCredentials gets correct username for ( #env, #trusted, #cred, #cookie, #form, #accountUser )"() {
    setup:
    Visa target = new Visa(null)

    IAccount account = [ getUserName: { accountUser } ] as IAccount
    VisaValidator visaValidator = [ isValid: { true }] as VisaValidator

    Visa.metaClass.setProperty(Visa, target, 'visaValidator', visaValidator, false, true)
    Visa.metaClass.setProperty(Visa, target, 'account', account, false, true)

    IBiBusHeader iBiBusHeader = Mock()
    iBiBusHeader.getEnvVarValue('username') >> [env]
    iBiBusHeader.getTrustedEnvVarValue('username') >> [trusted]
    iBiBusHeader.getCredentialValue('username') >> [cred]
    iBiBusHeader.getCookieValue('username') >> [cookie]
    iBiBusHeader.getFormFieldValue('username') >> [form]

    when:
    ITrustedCredential trustedCredential = target.generateTrustedCredential(iBiBusHeader)

    then:
    trustedCredential.getCredentialValue('username').first() == expected

    where:
    env     | trusted | cred    | cookie  | form    | accountUser | expected
    'usr1'  | null    | null    | null    | null    | 'usr1'      | 'usr1'
    null    | 'usr1'  | null    | null    | null    | 'usr1'      | 'usr1'
    null    | null  | 'usr1'    | null    | null    | 'usr1'      | 'usr1'

    'usr1'  | null    | null    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    null    | 'usr1'  | null    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    null    | null  | 'usr1'    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'

    'usr1'  | null    | 'dummy' | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | 'dummy' | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'

    'usr1'  | null    | 'dummy' | null    | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | null    | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | 'dummy' | null    | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | null    | 'dummy' | 'usr1'      | 'usr1'

    'usr1'  | null    | 'dummy' | 'dummy' | null    | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | 'dummy' | null    | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | 'dummy' | 'dummy' | null    | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | 'dummy' | null    | 'usr1'      | 'usr1'

    'usr1'  | null    | 'dummy' | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | 'dummy' | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
    'dummy' | 'usr1'  | null    | 'dummy' | 'dummy' | 'usr1'      | 'usr1'
  }
}