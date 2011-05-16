package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.INamespaceConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import com.cognos.CAM_AAA.authentication.IBiBusHeader
import org.powermock.core.classloader.annotations.PrepareForTest
import static org.powermock.api.mockito.PowerMockito.spy
import static org.powermock.api.mockito.PowerMockito.doReturn
import com.cognos.CAM_AAA.authentication.IBiBusHeader2

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
