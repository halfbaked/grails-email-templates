package org.grails.plugin.emailTemplates

class EmailTemplatesController {

  
  def list = {               
    EmailTemplate.list(params)          
  }

}
