package se.su.it.cognos.cognosshibauth.memcached

import net.spy.memcached.AddrUtil
import net.spy.memcached.BinaryConnectionFactory
import net.spy.memcached.MemcachedClient
import java.util.logging.Logger
import java.util.logging.Level
import se.su.it.cognos.cognosshibauth.config.ConfigHandler

public class Cache {

  private static Logger LOG = Logger.getLogger(Cache.class.getName())
  private static Cache instance = null
  private static MemcachedClient[] m = null

  private static String namespace = "CognosShibAuth"
  private static int ttl = 300
  private static int clients = 20
  private static String host = "127.0.0.1"
  private static String port = "11211"

  private Random random = new Random()

  private Cache() {
    try {
      m= new MemcachedClient[clients]
      for (int i = 0; i < clients; i ++) {
        m[i] =  new MemcachedClient(
                  new BinaryConnectionFactory(),
                    AddrUtil.getAddresses("$host:$port"))
      }
    } catch (Exception e) {

    }
  }

  public static synchronized Cache getInstance() {
    LOG.log(Level.FINE, "Instance: " + instance)
    if (instance == null)
      instance = new Cache()
    instance
  }

  public void set(String key, final Object o) {
    getCache().set(namespace + key, ttl, o)
  }

  public void set(String key, int ttl, final Object o) {
    getCache().set(namespace + key, ttl, o)
  }

  public Object get(String key) {
    try {
      return getCache().get(namespace + key)
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while getting object from cache for key '$key': " + e.getMessage())
    }
    null
  }

  public Object delete(String key) {
    getCache().delete(namespace + key)
  }

  public MemcachedClient getCache() {
    MemcachedClient c= null
    try {
      c = m[random.nextInt(clients)]
    } catch(Exception e) { }
    c
  }

  static {
    def configHandler = ConfigHandler.instance()

    namespace = configHandler.getStringEntry("cache.namespace", namespace)
    host = configHandler.getStringEntry("cache.host", host)
    port = configHandler.getStringEntry("cache.port", port)
    ttl = configHandler.getIntEntry("cache.ttl", ttl)
    clients = configHandler.getIntEntry("cache.clients", clients)
  }
}