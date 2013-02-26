package org.grails.plugin.emailTemplates

abstract class EmailTemplate {

  /*
   * the name of the email
   */
  abstract String getName()

  /*
   * What the body content of the email will be when sent. It supports Markdown formatting and Mustache templating.
   */
  abstract String getBody()

  /*
   * what the subject of the email will be when sent
   */
  abstract String getSubject()

  /*
   * A map of data keys illustrating what data can be injected into the body of the email template
   */
  abstract Map dataKeys()

  /*
   * Retrieves the recipients for this email, given the original dataMessage. 
   * It can return a string, or an array of strings.
   */
  abstract getRecipients(dataMessage)

  /* 
   * Builds a test data message. This is very useful when testing email templates work, and how they look.
   */
  abstract buildTestDataMessage()

  /* 
   * Build the data that will be available to the email template when it will be processed 
   */
  abstract Map buildScopes(dataMessage)

  def mailService
  def markdown
  def mustache

  /* 
   * If defined, the emailTemplate will become event driven, listening for the event, and sending the email.
   */ 
  def listener

  def sendEmail(String recipient, def scopes, def emailTemplateData) {
    log.debug "Sending email recipient $recipient, scopes $scopes, subject: $emailTemplateData.subject"
    
    //def bccEmailsArray = email.bccEmails?.split(',')
    if(!(recipient ==~ /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[A-Za-z]{2,4}/)) {
      return
    } 

    def bodyForMarkdown = compileMustache(new StringReader(emailTemplateData.body), scopes)
    def bodyHtml = markdown.markdownToHtml(bodyForMarkdown)

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
        $e
      """
    }
  }
  
  /*
   * Sends the test email
   */
  void sendTest(String recipient, EmailTemplateData emailTemplateData) {
    def dataMessage = buildTestDataMessage()
    sendEmail(recipient, buildScopes(dataMessage), emailTemplateData)
  }

  /* 
   * Sends an email given only the dataMessage. It will build everything else from the methods defined in the subclasses.
   */
  void send(dataMessage) {
    try {
      def recipients = getRecipients(dataMessage)
      switch(recipients) {
        case String:
          sendEmail(recipients, buildScopes(dataMessage), getTemplateData())
          break
        case List:
          recipients.each { sendEmail(it, buildScopes(dataMessage), getTemplateData()) }
          break
        default:
          log.error "Invalid returned from getRecipients ${recipients?.class.name}"                  
      }
    } catch (e) {
      log.debug "exception in send $e"      
    }
  }

  /*
   * Returns the persisted email template data from the database. Persisting this data to the database allows users to easily 
   * customize the email templates
   */
  def getTemplateData() {
    return EmailTemplateData.findByCode(getEmailCode())
  }

  /*
   * Builds default template data object. 
   */
  def createDefaultTemplate() {    
    new EmailTemplateData(
      code: getEmailCode(),
      name: name,
      subject: subject,
      body: body
    )    
  }

  /*
   * Generally used on startup to ensure every email template has a corresponding EmailTemplateData in the database that
   * users are free to customize
   */
  def persistEmailTemplateDataIfDoesNotExist(){
    if (!EmailTemplateData.findByCode(getEmailCode())) {
      createDefaultTemplate().save(failOnError:true, flush:true)
    } 
  }

  private def compileMustache(Reader reader, def scopes) {
    def writer = new StringWriter()
    mustache
      .compile(reader, "mustacheOutput")
      .execute(writer, scopes)
    writer.buffer.toString()
  }

  /* 
   * Returns the unique identifier for a particular email template. This is used to associate the email template with
   * a particular EmailTemplateData.
   */
  def getEmailCode() {
    this.class.name
  }

}
