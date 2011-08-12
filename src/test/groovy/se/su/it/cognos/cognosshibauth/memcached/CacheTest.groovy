package se.su.it.cognos.cognosshibauth.memcached;

import org.junit.Test
import net.spy.memcached.MemcachedClient

public class CacheTest {

  Cache target = Cache.getInstance()

  @Test
  public void testGetInstanceReturnsSingleton() throws Exception {
    Cache cache2 = Cache.getInstance()
    assert target == cache2
  }

  @Test
  public void testGetCacheReturnsAMemcachedClient() throws Exception {
    assert target.getCache() instanceof MemcachedClient
  }

  @Test
  public void testGetCacheReturnsARandomClient() throws Exception {
    def client1 = target.getCache()
    def client2 = target.getCache()
    def client3 = target.getCache()
    def client4 = target.getCache()
    
    assert !(client1 == client2 == client3 == client4)
  }

  @Test
  void testThatCacheReturnsClosureWhenDisabled() {
    target.disable()
    def expected = "foo"

    def ret = target.get("none", { expected })
    
    assert expected == ret
  }
}
