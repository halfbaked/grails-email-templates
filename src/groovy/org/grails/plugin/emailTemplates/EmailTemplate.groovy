package org.grails.plugin.emailTemplates

abstract class EmailTemplate {

  abstract String getName()
  abstract String getBody()
  abstract getSubject()
  abstract void sendTest(String recipient)

  def mailService
  def markdown
  def mustache

  def sendEmail(String recipient, def scopes) {
    //def cloudInfo = CloudInfo.get(1)
    //def bccEmailsArray = email.bccEmails?.split(',')
    def emailTemplateData = getTemplateData()
    if(!(recipient ==~ /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[A-Za-z]{2,4}/)) {
      return
    } 

    def bodyForMustache = markdown.markdownToHtml(emailTemplateData.body)
    def bodyHtml = compileMustache(new StringReader(bodyForMustache), scopes)

    try {
      mailService.sendMail {
        to recipient
        subject emailTemplateData.subject
        html bodyHtml
//        if(bccEmailsArray){ bcc bccEmailsArray }
      } 
    } catch (e) {
      log.error """
        Error sending email $name
        To: $recipient
      """
    }
  }

  def getTemplateData() {
    return EmailTemplateData.findByCode(CODE)
  }

  def createDefaultTemplate() {    
    new EmailTemplateData(
      code: CODE,
      name: name,
      subject: subject,
      body: body
    )    
  }

  def persistEmailTemplateDataIfDoesNotExist(){
    if (!EmailTemplateData.findByCode(CODE)) {
      createDefaultTemplate().save()
    } 
  }

  private def compileMustache(Reader reader, def scopes) {
    def writer = new StringWriter()
    mustache
      .compile(reader, "mustacheOutput")
      .execute(writer, scopes)
    writer.buffer.toString()
  }

}
