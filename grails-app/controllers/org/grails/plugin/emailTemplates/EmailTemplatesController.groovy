package org.grails.plugin.emailTemplates

class EmailTemplatesController {

  def list = {               
    //params.max = Math.min(params.max ? params.int('max') : 10, 100)
    [emailTemplateDataList: EmailTemplateData.list(params)]          
  }

  def update = {
    def emailTemplateData = EmailTemplateData.get(params.id)
    if (!emailTemplateData){
      response.sendError(404)
    } else {     
      if (request.method == 'POST') { 
        emailTemplateData.properties = params
        if (!emailTemplateData.hasErrors() && emailTemplateData.save(flush:true)){
          redirect action:'list'
        }
      }
      [emailTemplateData: emailTemplateData]
    } 
  } 

  def sendTestEmail = {
    def emailTemplateData = EmailTemplateData.get(params.id)
    if (!emailTemplateData){
      response.sendError(404)
    } else {
      def testEmailTemplateData = new EmailTemplateData(subject:params.subject, body: params.body)
      emailTemplateData.email.sendTest(params.recipient, testEmailTemplateData)
      response.status = 200
      render "Email sent"
    }
  }

}
