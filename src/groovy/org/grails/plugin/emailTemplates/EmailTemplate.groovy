package org.grails.plugin.emailTemplates


import groovy.text.SimpleTemplateEngine
import org.hibernate.FlushMode


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
  def sessionFactory

  /* 
   * If defined, the emailTemplate will become event driven, listening for the event, and sending the email.
   */ 
  def listener

  def sendEmail(String recipientEmail, def scopes, def emailTemplateData) {
    log.info "Sending email recipient $recipientEmail, scopes $scopes, subject: ${emailTemplateData?.subject}"
    sessionFactory?.currentSession?.setFlushMode(FlushMode.COMMIT)
    if (!recipientEmail || !emailTemplateData) {
      log.warn """
        Could not send mail. Invalid arguments. 
        recipientEmail: $recipientEmail
        emailTemplateData: $emailTemplateData
      """
      return
    }
    def bccEmailsArray = emailTemplateData.bccEmails?.split(',').findAll { isEmail(it) }
    if(!isEmail(recipientEmail)) {
      return
    }

    def body = ""
    def layout = emailTemplateData.layout ?: EmailTemplateLayout.findByDefaultLayout(true)
    if (layout) {
      def engine = new SimpleTemplateEngine()
      body = engine.createTemplate(layout.body).make([emailContent: emailTemplateData.body]).toString()      
    } else {
      body = emailTemplateData.body
    }
    
    body = compileMustache(new StringReader(body), scopes)
    try {
      mailService.sendMail {
        to recipientEmail
        subject emailTemplateData.subject
        html body
        if(bccEmailsArray){ bcc bccEmailsArray }
      }
      log.info "Email sent to $recipientEmail"
    } catch (e) {
      log.error("error sending email $name", e)
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
  void sendWithDataMessage(dataMessage) {
    try {
      def scopes = buildScopes(dataMessage)
      log.trace "scopes built. getting recipients"
      getRecipients(dataMessage).each { recipient ->
        def templateData = getTemplateData(recipient.locale)
        if (templateData) {
          sendEmail(recipient.email, scopes, getTemplateData(recipient.locale))
        }
      }
    } catch (e) {
      log.error """
        Error: exception in send $e 
        $e.stackTrace
        $e.message
        $e.cause
      """  
      log.error("Error sending email $name",e)
      //throw(e)
    }
  }

  /*
   * Returns the persisted email template data from the database. Persisting this data to the database allows users to easily 
   * customize the email templates
   */
  def getTemplateData(Locale locale=null) {
    def emailTemplateData = EmailTemplateData.findEnabledByCodeAndLocale(getEmailCode(), locale)
    if (emailTemplateData) return emailTemplateData
    else if (locale?.variant) return getTemplateData(new Locale(locale.getLanguage(), locale.getCountry()))    
    else if (locale?.country) return getTemplateData(new Locale(locale.getLanguage()))
    else return EmailTemplateData.findEnabledByCodeAndDefaultForCode(getEmailCode(), true)    
  }

  /*
   * Builds default template data object. 
   */
  def createDefaultTemplate() {   
    new EmailTemplateData(
      code: getEmailCode(),
      name: name,
      subject: subject,
      body: body,
      defaultForCode: true
    )    
  }

  /*
   * Generally used on startup to ensure every email template has a corresponding EmailTemplateData in the database that
   * users are free to customize
   */
  def persistEmailTemplateDataIfDoesNotExist(){
    new EmailTemplateData().withTransaction {
      if (!EmailTemplateData.findByCode(getEmailCode())) {      
        createDefaultTemplate().save(failOnError:true, flush:true)
        log.debug "EmailTemplateData [${getEmailCode()}] did not exist. Persisting"      
      } 
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

  private Boolean isEmail(String email) {
    email ==~ /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[A-Za-z]{2,4}/
  }

}
