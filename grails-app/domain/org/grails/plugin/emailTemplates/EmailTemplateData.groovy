package org.grails.plugin.emailTemplates


class EmailTemplateData {

  String code
  String subject 
  String body
  String name

  static constraints = {
  }

  static mapping = {
    body type:'text'
  }

}
