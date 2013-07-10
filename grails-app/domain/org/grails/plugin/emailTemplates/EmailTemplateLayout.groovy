package org.grails.plugin.emailTemplates


class EmailTemplateLayout {

  def grailsApplication

  Date dateCreated
  Date lastUpdated

  String name
  String body
  Boolean defaultLayout = false

  static mapping = {
    body type:"text"
    sort "name" 
  }

  def mergeTags(){
    grailsApplication.config.plugin?.emailTemplates?.defaultLayoutMergeTags ?: [:]
  }

}
