package se.su.it.cognos.cognosshibauth.memcached;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

public class MyCache {
  private static final String NAMESPACE= "CognosShibAuth";
  private static MyCache instance = null;
  private static MemcachedClient[] m = null;

  private MyCache() {
    try {
      m= new MemcachedClient[21];
      for (int i = 0; i <= 20; i ++) {
        MemcachedClient c =  new MemcachedClient(
                new BinaryConnectionFactory(),
                AddrUtil.getAddresses("127.0.0.1:11211"));
        m[i] = c;
      }
    } catch (Exception e) {

    }
  }

  public static synchronized MyCache getInstance() {
    System.out.println("Instance: " + instance);
    if(instance == null) {
      instance = new MyCache();
    }
    return instance;
  }

  public void set(String key, int ttl, final Object o) {
    getCache().set(NAMESPACE + key, ttl, o);
  }

  public Object get(String key) {
    Object o = getCache().get(NAMESPACE + key);
    return o;
  }

  public Object delete(String key) {
    return getCache().delete(NAMESPACE + key);
  }

  public MemcachedClient getCache() {
    MemcachedClient c= null;
    try {
      int i = (int) (Math.random()* 20);
      c = m[i];
    } catch(Exception e) {

    }
    return c;
  }
}