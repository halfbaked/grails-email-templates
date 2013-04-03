import org.grails.plugin.emailTemplates.EmailTemplateArtefactHandler


class EmailTemplatesGrailsPlugin {

  def version = "0.1.4"
  def grailsVersion = "2.0 > *"

  // the other plugins this plugin depends on
  def dependsOn = [:]
  def pluginExcludes = [
    "grails-app/emailTemplates/org/grails/plugin/emailTemplates/ResetPasswordEmailTemplate.groovy",
    "grails-app/domain/org/grails/plugin/emailTemplates/test/*",
    "grails-app/conf/TestDataConfig.groovy"
  ]

  def title = "Email Templates" 
  def author = "Eamonn O'Connell"
  def authorEmail = "@34m0"
  def description = '''
    Quickly and easily use markdown and mustache in your emails
  '''

  def documentation = "http://grails.org/plugin/email-templates"
  def license = "APACHE"
  def developers = [ [ name: "Eamonn O'Connell", email: "eamonnoconnell@gmail.com" ] ]
  def issueManagement = [ system: "GIT", url: "https://github.com/halfbaked/grails-email-templates/issues" ]
  def scm = [ url: "https://github.com/halfbaked/grails-email-templates" ]

  def watchedResources = "field:./grails-app/emailTemplates/**/*EmailTemplate.groovy"
  def artefacts = [ EmailTemplateArtefactHandler ]

  def doWithSpring = { ctx ->          
    application.emailTemplateClasses.each { emailTemplateClass ->           
      if (emailTemplateClass.isAbstract()) return
      def beanName = emailTemplateClass.fullName
      "$beanName"(emailTemplateClass.clazz) { bean ->
        bean.autowire = true
        markdown = new org.pegdown.PegDownProcessor()
        mustache = new com.github.mustachejava.DefaultMustacheFactory()
      }
      def shortBeanName = generateShortBeanNameFromClass(emailTemplateClass)
      springConfig.addAlias shortBeanName, beanName 
    }      
  }

  def doWithDynamicMethods = { ctx ->
  
    try {
      for (domainClass in application.domainClasses) {
         domainClass.metaClass.static.dataKeys = {
           delegate.newInstance().asDataMap().keySet()
         }
      }
    } catch (Exception e){
      println "Error $e"
    }
    
  }

  def onChange = { event ->
  
    if(application.isArtefactOfType(EmailTemplateArtefactHandler.TYPE, event.source)) {
      def oldClass = application.getEmailTemplateClass(event.source.name)
      application.addArtefact(EmailTemplateArtefactHandler.TYPE, event.source)

      application.emailTemplateClasses.each {
        if (it.clazz != event.source && oldClass.clazz.isAssignableFrom(it.clazz)) {
          def newClass = application.classLoader.reloadClass(it.clazz.name)
          application.addArtefact(EmailTemplateArtefactHandler.TYPE, newClass)
        }
      }
    }
    
  }

  def doWithConfigOptions = {
    'locale.default'(type: String, defaultValue: 'en')
  }

  private generateShortBeanNameFromClass(def clazz) {
    clazz.shortName[0].toLowerCase() + clazz.shortName[1..-1]
  }

}
