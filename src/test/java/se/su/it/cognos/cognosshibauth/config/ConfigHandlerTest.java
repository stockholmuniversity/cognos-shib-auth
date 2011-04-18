package se.su.it.cognos.cognosshibauth.config;
/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-08
 * Time: 10:48
 */

import com.cognos.CAM_AAA.authentication.INamespace;
import com.cognos.CAM_AAA.authentication.INamespaceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import se.su.it.cognos.cognosshibauth.CognosShibAuth;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class ConfigHandlerTest {

  @Test
  public void testThatNameReturnsNameForLocale() throws Exception {
    Locale locale_sv = new Locale("sv_SE");
    ConfigHandler configHandler = new ConfigHandler();

    String sv_name = configHandler.getName(locale_sv);

    assertEquals("Swedish Name", sv_name);
  }

  @Test
  public void testThatGetCapabilitiesReturnsAllCapabilities() throws Exception {
    ConfigHandler configHandler = new ConfigHandler();

    List<String> list = configHandler.getCapabilities();

    assertTrue(list.contains(INamespace.CapabilityCaseSensitive));
    assertTrue(list.contains(INamespace.CapabilityContains));
    assertTrue(list.contains(INamespace.CapabilityEndsWith));
    assertTrue(list.contains(INamespace.CapabilityEquals));
    assertTrue(list.contains(INamespace.CapabilitySort));
    assertTrue(list.contains(INamespace.CapabilityStartsWith));
  }
}

