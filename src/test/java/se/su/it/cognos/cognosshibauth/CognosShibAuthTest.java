package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.INamespaceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class CognosShibAuthTest {

  @Test
  public void testThatInitSetsObjectIdFromNamespaceConfiguration() throws Exception {
    String objectId = "TestId";
    CognosShibAuth cognosShibAuth = new CognosShibAuth();
    INamespaceConfiguration iNamespaceConfiguration = mock(INamespaceConfiguration.class);

    when(iNamespaceConfiguration.getID()).thenReturn(objectId);

    cognosShibAuth.init(iNamespaceConfiguration);

    assertEquals(objectId, cognosShibAuth.getObjectID());
  }
}
