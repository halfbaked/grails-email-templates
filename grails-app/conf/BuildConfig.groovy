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
            'com.github.spullara.mustache.java:compiler:0.8.10' 
    compile 'org.codehaus.jackson:jackson-core-asl:1.8.3'
    compile 'org.codehaus.jackson:jackson-mapper-asl:1.8.3'            
  }

  plugins {
    compile(":tomcat:$grailsVersion",
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

    compile ":resources:1.2.RC3" 
  }
}

