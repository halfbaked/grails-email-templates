package org.grails.plugin.emailTemplates.test

class EmailTemplatePerson {

  String firstName
  String lastName
  String email

  // Returns a map representation used in email templates
  Map asDataMap(){
    [
      firstName: firstName,
      lastName: lastName,
      email: email
    ]
  }

}
