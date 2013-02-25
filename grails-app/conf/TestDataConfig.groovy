testDataConfig {
  sampleData {
    'org.grails.plugin.emailTemplates.test.EmailTemplatesPerson' {
      def i = 1
      email = { -> "email${i++}@example.com" }
    }
  }
}

