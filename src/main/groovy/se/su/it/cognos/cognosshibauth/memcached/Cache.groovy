/*
 * Copyright (c) 2013, IT Services, Stockholm University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Stockholm University nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package se.su.it.cognos.cognosshibauth.memcached

import net.spy.memcached.AddrUtil
import net.spy.memcached.MemcachedClient
import java.util.logging.Logger
import java.util.logging.Level
import se.su.it.cognos.cognosshibauth.config.ConfigHandler
import net.spy.memcached.DefaultConnectionFactory
import java.security.MessageDigest

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
  private boolean enabled = true

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
    if (enabled)
      set(namespace + key, ttl, o)
  }

  public void set(String key, int ttl, final Object o) {
    if (!enabled)
      return

    try {
      MemcachedClient mc = getCache();
      if(mc.availableServers.size() <= 0)
        mc.set(namespace + key, ttl, o)
      else
        LOG.log(Level.SEVERE, "Memcached client not alive: " + mc.toString())
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while setting object to cache using key '$key' and ttl '$ttl': " + e.getMessage())
    }
  }

  public Object get(String key) {
    if (!enabled)
      return null

    try {
      MemcachedClient mc = getCache()
      if(mc.availableServers.size() <= 0)
        return mc.get(namespace + key)
      else
        LOG.log(Level.SEVERE, "Memcached client not alive: " + mc.toString())
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error while getting object from cache for key '$key': " + e.getMessage())
    }
    null
  }

  public Object get(key, function) {
    if (!enabled)
      return function()

    def value = null

    try {
      MemcachedClient mc = getCache()

      // If running without memcached we fetch the value on each call, very bad.
      if (mc.availableServers.size() <= 0) {
        LOG.warning ("Running app without active memcached, performace will be seriously degraded.")
        return function()
      }

      key = key?.replaceAll(/ /, "")
      if(!key) {
        throw new IllegalArgumentException("Missing parameter key")
      }

      //Get object from cache
      value = mc?.get(key)

      // Unless alreay found in cache we refresh the value
      if (value == null) {
        LOG.finest ("Cache key: $key was not found in the cache, adding $key to cache.")
        value = function()
        if (value != null)
          mc?.set(key, ttl, value)
      } else {
        LOG.finest ("Cache key: $key was found in the cache, returning $key value from cache.")
      }
    }
    catch (Exception e) {
      LOG.severe("Error while getting object from cache for key '$key': " + e.getMessage())
    }
    value
  }

  public Object delete(String key) {
    if (!enabled)
      return

    try {
      MemcachedClient mc = getCache();
      if(mc.availableServers.size() <= 0)
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

  def enable() {
    enabled = true
  }

  def disable() {
    enabled = false
  }

  static String md5Sum(String key) {
    MessageDigest md5 = MessageDigest.getInstance("MD5")
    md5.update(key.bytes)
    new BigInteger(1, md5.digest()).toString(16)
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
