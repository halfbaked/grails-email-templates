testDataConfig {
  sampleData {
    'org.grails.plugin.emailTemplates.test.Person' {
      def i = 1
      email = { -> "email${i++}@example.com" }
    }
  }
}

