import org.grails.plugin.emailTemplates.EmailTemplateData

class EmailTemplateBootStrap {

  def grailsApplication

  def init = { servletContext ->
    grailsApplication.emailTemplateClasses.each { emailTemplateClass ->
      def beanName = generateBeanNameFromClass(emailTemplateClass)
      grailsApplication.mainContext.getBean(beanName).persistEmailTemplateDataIfDoesNotExist()
    }
  }

  private generateBeanNameFromClass(def clazz) {
    clazz.shortName[0].toLowerCase() + clazz.shortName[1..-1]
  }

}
