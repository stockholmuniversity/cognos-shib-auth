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

@RunWith(PowerMockRunner.class)
public class CognosShibAuthTest {

  @Test
  public void testThatInitSetsObjectIdFromNamespaceConfiguration() throws Exception {
    String objectId = "TestId";
    CognosShibAP cognosShibAP = new CognosShibAP();
    INamespaceConfiguration iNamespaceConfiguration = mock(INamespaceConfiguration.class);

    when(iNamespaceConfiguration.getID()).thenReturn(objectId);

    cognosShibAP.init(iNamespaceConfiguration);

    assertEquals(objectId, cognosShibAP.getObjectID());
  }

  @Test
  public void testTest() throws Exception {
    SUKAT sukat = SUKAT.newInstance("ldap://ldap.su.se");
    SearchResult sr = sukat.findGroupOfUniqueNamesByName("ivs-systemforvaltare");
    Attributes attrs = sr.getAttributes();
    Attribute attr = attrs.get("uniqueMember");
    NamingEnumeration<?> ne = attr.getAll();
    while(ne.hasMoreElements()) {
      Object o = ne.next();
      System.err.println(o.toString());
    }
    System.err.println(sr.getName());
    NamingEnumeration<SearchResult> ne2 = sukat.findByAttributeValue("dc=su,dc=se", "uniqueMember", "uid=jolu,dc=it,dc=su,dc=se");
    while(ne2.hasMoreElements()) {
      System.err.println(ne2.next().getNameInNamespace());
      System.err.println(ne2.next().getName());
    }
    SearchResult sr1 = sukat.read("uid=jolu,dc=it,dc=su,dc=se");
    System.err.println(sr1.getName());
  }
}
