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

package se.su.it.cognos.cognosshibauth.memcached;

import org.junit.Test
import net.spy.memcached.MemcachedClient
import se.su.it.cognos.cognosshibauth.TestBaseClass

public class CacheTest extends TestBaseClass {

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
