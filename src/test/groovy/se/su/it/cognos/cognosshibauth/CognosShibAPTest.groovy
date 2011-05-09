package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.INamespaceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import se.su.it.sukat.SUKAT;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
public class CognosShibAPTest {

  @Test
  public void testThatInitSetsObjectIdFromNamespaceConfiguration() throws Exception {
    String objectId = "TestId";
    CognosShibAP cognosShibAP = new CognosShibAP();
    INamespaceConfiguration iNamespaceConfiguration = mock(INamespaceConfiguration.class);

    when(iNamespaceConfiguration.getID()).thenReturn(objectId);

    cognosShibAP.init(iNamespaceConfiguration);

    assertEquals(objectId, cognosShibAP.getObjectID());
  }
}
