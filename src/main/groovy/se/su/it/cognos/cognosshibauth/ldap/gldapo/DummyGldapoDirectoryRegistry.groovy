package se.su.it.cognos.cognosshibauth.ldap.gldapo

import gldapo.GldapoDirectoryRegistry
import gldapo.GldapoDirectory
import gldapo.exception.GldapoNoDefaultDirectoryException
import gldapo.exception.GldapoDirectoryNotFoundException

/**
 * User: joakim
 * Date: 2011-05-11
 * Time: 13:27
 */
class DummyGldapoDirectoryRegistry extends GldapoDirectoryRegistry {

  GldapoDirectory directory = null;

  @Override
  GldapoDirectory put(String key, GldapoDirectory value) {
    directory = value
  }

  GldapoDirectory getDefaultDirectory() throws GldapoNoDefaultDirectoryException, GldapoDirectoryNotFoundException {
    directory
  }

  boolean isHasDefault() {
    directory != null
  }

  GldapoDirectory get(name) {
    directory
  }

  void leftShift(directory) {
    this.directory = directory
  }
}