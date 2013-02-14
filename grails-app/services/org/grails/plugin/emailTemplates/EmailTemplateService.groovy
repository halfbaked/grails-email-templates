package org.grails.plugin.emailTemplates

class EmailTemplatesService {

  def sendEmail(String recipient, EmailTemplateData emailTemplateData, def scopes) {
    def cloudInfo = CloudInfo.get(1)
    def bccEmailsArray = email.bccEmails?.split(',')
    def bodyForMustache = markdown.markdownToHtml(email.body)
    def bodyHtml = compileMustache(new StringReader(bodyForMustache), scopes)

    try {
      mailService.sendMail {
        to recipient.email
        from cloudInfo.email                            
        subject email.subject
        html bodyHtml
        if(bccEmailsArray){ bcc bccEmailsArray }
      } 
      log.info "Email[$email.name] sent to [$recipient.email]"
    } catch (e) {
      log.error """
        Error sending email $email.name
        To: $recipient?.email
        From: $cloudInfo?.email
      """
    }
  }

}
