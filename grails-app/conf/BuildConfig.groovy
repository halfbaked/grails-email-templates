grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsCentral()
    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    //mavenLocal()
    //mavenCentral()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
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

    test(":spock:0.7", ":greenmail:1.3.3"){
      export = false
    }

    compile ":mail:1.0", ":build-test-data:2.0.3"
    runtime  ":hibernate:$grailsVersion"
  }
}