directories {
  directory1 {
    defaultDirectory = true
    url = "ldap://sukat-ldap.it.su.se"
    base = "dc=su,dc=se"
    userDn = ""
    password = ""
    searchControls {
      countLimit: 500
      timeLimit: 120000
      searchScope: "subtree"
    }
  }

}
// An array of class objects that are the Gldap Schema Classes
schemas = []

typemappings = []

