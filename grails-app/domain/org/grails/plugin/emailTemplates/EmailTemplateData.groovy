package org.grails.plugin.emailTemplates


class EmailTemplateData {

  def grailsApplication

  Date dateCreated
  Date lastUpdated

  String code
  String subject 
  String body
  String name
  String bccEmails
  Locale locale 
  Boolean defaultForCode = false
  Boolean enabled = true 
  EmailTemplateLayout layout

  static constraints = {
    body widget:'textarea'
    locale nullable:true
    bccEmails nullable:true, validator: { val, obj -> 
      if (!val) return true
      val.split('.').every { it ==~ /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[A-Za-z]{2,4}/ } // should be an email
    }
    layout nullable:true
  }

  static mapping = {
    body type:"text"
    sort "name"
  }

  def getEmail() {
    grailsApplication.mainContext.getBean(code)
  }

  EmailTemplateData clone() {
    return new EmailTemplateData(code:code, subject:subject, body:body, name:name, locale:null)
  }

  def static disableAll(){
    EmailTemplateData.withNewTransaction {
      EmailTemplateData.list().each { it.enabled = false }
    }
  }

  def static enableAll(){
    EmailTemplateData.withNewTransaction {
      EmailTemplateData.list().each { it.enabled = true }
    }
  }

}
