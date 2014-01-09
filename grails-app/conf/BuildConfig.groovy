grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.dependency.resolver = "maven" // maven or ivy

grails.project.dependency.resolution = {

  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }

  log "warn" 

  repositories {
    grailsCentral()
    mavenCentral()
		mavenLocal()
    grailsPlugins()
    grailsHome()
    mavenRepo "http://maven.springframework.org/milestone/"
    mavenRepo "https://oss.sonatype.org/content/repositories/releases/"
  }

  dependencies {
    runtime 'org.pegdown:pegdown:1.1.0',
            'com.github.spullara.mustache.java:compiler:0.8.10' 
    compile 'org.codehaus.jackson:jackson-core-asl:1.8.3'
    compile 'org.codehaus.jackson:jackson-mapper-asl:1.8.3'            
  }

  plugins {
    compile(":tomcat:7.0.42",
          ":release:3.0.1",
          ":rest-client-builder:1.0.3") {
			excludes "spring-test"
      export = false
    }

    test(":greenmail:1.3.4"){
      export = false
    }

    // Build test data is a handy way of building the TestDataMessage,
    // but other projects don't have to use it.
    compile(":build-test-data:2.0.8") { export = false }
    compile (":mail:1.0.1", ":platform-core:1.0.RC5") { excludes "spring-test" }
    runtime ":hibernate:3.6.10.6"

  }

}

