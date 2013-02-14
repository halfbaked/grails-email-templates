import org.grails.plugin.emailTemplates.EmailTemplateArtefactHandler


class EmailTemplatesGrailsPlugin {

    def version = "0.1"
    def grailsVersion = "2.0 > *"

    // the other plugins this plugin depends on
    def dependsOn = [:]
    def pluginExcludes = [
      "grails-app/emailTemplates/*",
      "grails-app/domain/org/grails/plugin/emailTemplates/test/*"
    ]

    // TODO Fill in these fields
    def title = "Email Templates" 
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
      Quickly and easily use markdown and mustache in your emails
    '''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/email-templates"

    def license = "APACHE"

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Eamonn O'Connell", email: "eamonnoconnell@gmail.com" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "GIT", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def watchedResources = "field:./grails-app/emailTemplates/**/*EmailTemplate.groovy"
    def artefacts = [ EmailTemplateArtefactHandler ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = { ctx ->          
      application.emailTemplateClasses.each { emailTemplateClass ->           
        //def beanName = emailTemplateClass.shortName
        //beanName = beanName[0].toLowerCase() + beanName[1..-1]
        def beanName = generateBeanNameFromClass(emailTemplateClass)
        "$beanName"(emailTemplateClass.clazz) { bean ->
          bean.autowire = true
          markdown = new org.pegdown.PegDownProcessor()
          mustache = new com.github.mustachejava.DefaultMustacheFactory()
        }
      }      
    }

    def doWithDynamicMethods = { ctx ->
    
      try {
        for (domainClass in application.domainClasses) {
           domainClass.metaClass.static.dataKeys = {
             delegate.newInstance().asDataMap().keySet()
           }
        }
        application.emailTemplateClasses.each { emailTemplateClass ->
          emailTemplateClass.metaClass.g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
        }
      } catch (Exception e){
        println "Error $e"
      }
      
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
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

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    private generateBeanNameFromClass(def clazz) {
      clazz.shortName[0].toLowerCase() + clazz.shortName[1..-1]
    }
}
