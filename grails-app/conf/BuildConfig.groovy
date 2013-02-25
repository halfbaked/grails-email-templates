grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" 
  repositories {
    grailsCentral()
    mavenCentral()
  }
  dependencies {
    runtime 'org.pegdown:pegdown:1.1.0',
            'com.github.spullara.mustache.java:compiler:0.8.9' 
  }

  plugins {
    build(":tomcat:$grailsVersion",
          ":release:2.0.3",
          ":rest-client-builder:1.0.2") {
      export = false
    }

    test(":spock:0.7", ":greenmail:1.3.3", ":code-coverage:1.2.5"){
      export = false
    }

    // Build test data is a handy way of building the TestDataMessage,
    // but other projects don't have to use it.
    compile(":build-test-data:2.0.3") { export = false }
    compile ":mail:1.0"     
    compile ":platform-core:1.0.RC5"
    runtime ":hibernate:$grailsVersion"

// For the UI. Have not yet made it platform-ui compatible.
//    compile ":platform-ui:1.0.RC3"
//    compile ":bootstrap-ui:1.0.RC4"
//    compile(":bootstrap-theme:1.0.RC3") 
//      excludes "bootstrap-ui"
//    }
    compile ":resources:1.2.RC3" 

  }
}

//grails.plugin.location.bootstrapui="../grails-bootstrap-ui"
//grails.plugin.location.bootstraptheme="../grails-bootstrap-theme"
