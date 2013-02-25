package org.grails.plugin.emailTemplates


class EmailTemplateData {

  def grailsApplication

  String code
  String subject 
  String body
  String name

  static constraints = {
    body widget:'textarea'
  }

  static mapping = {
    body type:'text'
  }

  def getEmail() {
    grailsApplication.mainContext.getBean(code)
  }

}
