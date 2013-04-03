package org.grails.plugin.emailTemplates


class EmailTemplateLayout {

  Date dateCreated
  Date lastUpdated

  String name
  String body
  Boolean defaultLayout = false

  static mapping = {
    body type:"text"
    sort dateCreated:'desc'
  }

}
