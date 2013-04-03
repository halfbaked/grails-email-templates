import org.grails.plugin.emailTemplates.EmailTemplateData
import org.springframework.util.ReflectionUtils;

class EmailTemplateBootStrap {

  def grailsApplication
  def grailsEventsRegistry
  def emailTemplatesService

  def init = { servletContext ->
    grailsApplication.emailTemplateClasses.each { emailTemplateClass ->
      if (emailTemplateClass.isAbstract()) return
      def beanName = generateBeanNameFromClass(emailTemplateClass)
      def emailTemplate = grailsApplication.mainContext.getBean(beanName)
      emailTemplate.persistEmailTemplateDataIfDoesNotExist()
      def listener = emailTemplate.listener 
      if (listener && listener.topic) { 
        def sendMethod = emailTemplate.class.methods.find { it.name =~ /^sendWithDataMessage$/ }
        grailsEventsRegistry.on (listener.namespace, listener.topic, emailTemplate, sendMethod)
      }
    }
  }

  private generateBeanNameFromClass(def clazz) {
    clazz.fullName
  }

}
