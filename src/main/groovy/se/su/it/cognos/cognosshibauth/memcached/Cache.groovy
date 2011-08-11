package se.su.it.cognos.cognosshibauth.memcached

import net.spy.memcached.AddrUtil
import net.spy.memcached.MemcachedClient
import java.util.logging.Logger
import java.util.logging.Level
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import net.spy.memcached.DefaultConnectionFactory

public class Cache {

  private static Logger LOG = Logger.getLogger(Cache.class.getName())
  private static Cache _instance = null
  private static MemcachedClient[] m = null

  private static String namespace = "CAM"
  private static int ttl = 300
  private static int clients = 20
  private static String host = "127.0.0.1"
  private static String port = "11211"

  private Random random = new Random()

  private Cache() {
    try {
      m= new MemcachedClient[clients]
      for (int i = 0; i < clients; i ++) {
        m[i] =  new MemcachedClient(new DefaultConnectionFactory(),
                    AddrUtil.getAddresses("$host:$port"))
      }
    } catch (Exception e) {

    }
  }

  public static synchronized Cache getInstance() {
    LOG.log(Level.FINE, "Instance: " + _instance)
    if (_instance == null)
      _instance = new Cache()
    _instance
  }

  public void set(String key, final Object o) {
    try {
      MemcachedClient mc = getCache();
      if(mc.isAlive())
        mc.set(namespace + key, ttl, o)
      else
        LOG.log(Level.SEVERE, "Memcached client not alive: " + mc.toString())
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while setting object to cache using key '$key' and ttl '$ttl': " + e.getMessage())
    }
  }

  public void set(String key, int ttl, final Object o) {
    try {
      MemcachedClient mc = getCache();
      if(mc.isAlive())
        mc.set(namespace + key, ttl, o)
      else
        LOG.log(Level.SEVERE, "Memcached client not alive: " + mc.toString())
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while setting object to cache using key '$key' and ttl '$ttl': " + e.getMessage())
    }
  }

  public Object get(String key) {
    try {
      MemcachedClient mc = getCache();
      if(mc.isAlive())
        return mc.get(namespace + key)
      else
        LOG.log(Level.SEVERE, "Memcached client not alive: " + mc.toString())
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while getting object from cache for key '$key': " + e.getMessage())
    }
    null
  }

  public Object delete(String key) {
    try {
      MemcachedClient mc = getCache();
      if(mc.isAlive())
        return mc.delete(namespace + key)
      else
        LOG.log(Level.SEVERE, "Memcached client not alive: " + mc.toString())
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while deleting object from cache for key '$key': " + e.getMessage())
    }
    null
  }

  public MemcachedClient getCache() {
    MemcachedClient c = null
    try {
      c = m[random.nextInt(clients)]
    } catch(Exception e) {
      LOG.log(Level.SEVERE, "Failed to get memcached client: " + e.getMessage())
    }
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