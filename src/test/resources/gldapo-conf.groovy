directories {
  directory1 {
    defaultDirectory = true
    url = "ldap://sukat-ldap.it.su.se"
    base = "dc=su,dc=se"
    searchControls {
      countLimit: 500
      timeLimit: 120000
      searchScope: "subtree"
    }
  }

}
// An array of class objects that are the Gldap Schema Classes
schemas = [
        se.su.it.cognos.cognosshibauth.ldap.schema.SuPerson,
        se.su.it.cognos.cognosshibauth.ldap.schema.GroupOfUniqueNames
]

typemappings = []

