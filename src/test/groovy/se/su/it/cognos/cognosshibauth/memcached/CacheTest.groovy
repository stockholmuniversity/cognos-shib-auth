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
}
