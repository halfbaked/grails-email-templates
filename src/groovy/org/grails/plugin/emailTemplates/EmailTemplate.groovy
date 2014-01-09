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

  // Email templates can be enabled/disabled on a number of different levels
  // At the email template data level, and the emailTemplate artifact itself
  // Only at the email template data level it is persisted

  /* 
   * If a listener Map is defined, the emailTemplate will become event driven, listening for the event, and sending the email.
   */
  def sendEmail(String recipientEmail, def scopes, def emailTemplateData) {
    log.info "EmailTemplate[$name] sendEmail with recipient[$recipientEmail] and subject[${emailTemplateData?.subject}] and scopes $scopes"    
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

    body = ensureIsFullHtmlDocument(body)

		def attachments = getAttachments()
    try {
      mailService.sendMail {
        to recipientEmail
        subject emailTemplateData.subject
        html body
        if(bccEmailsArray){ bcc bccEmailsArray }
				attachments.each { attachment ->
				  attach attachment.name, attachment.type, attachment.bytes
				}
      }
      log.info "EmailTemplate[$name] sent to $recipientEmail"
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
   * If the listener map defined a delay, the thread will sleep for the specified delay
   */
  void sendWithDataMessage(dataMessage) {
    if (!isEnabled()) return
    try {
      if (hasProperty("listener") && listener && listener.delay) { 
        log.trace "EmailTemplate[$name] pausing for $listener.delay ms"
        Thread.sleep(listener.delay) 
      } else { "no delay specified. No sleeping" }

      def scopes = buildScopes(dataMessage)
      log.trace "scopes built. getting recipients"
      getRecipients(dataMessage).each { recipient ->
        def templateData = getTemplateData(recipient.locale)
        if (templateData) {
          sendEmail(recipient.email, scopes, getTemplateData(recipient.locale))
        }
      }
    } catch (java.lang.InterruptedException ie) { 
      log.warn "EmailTemplate[$name] sleep interupted"
    } catch (e) {
      log.error "EmailTemplate[$name] exception in sendWithDataMessage $e.message", e  
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

  def enableAllTemplateDatas() {
    EmailTemplateData.withNewTransaction {
      getAllTemplateDatas().each { it.enabled = true }
    }
  }

  def disableAllTemplateDatas() {
    EmailTemplateData.withNewTransaction {
      getAllTemplateDatas().each { it.enabled = false }
    }
  }

  def getAllTemplateDatas(){
    EmailTemplateData.findAllByCode(getEmailCode())
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
        log.trace "EmailTemplateData [${getEmailCode()}] did not exist. Persisting"      
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
   * Determines if the email template is enabled. This saves the template for unnecessary processing.
   * Currently whether the email template is enabled or not is determined by whether any email templates are available
   */
  def isEnabled() {
    EmailTemplateData.countByCode(getEmailCode()) > 0
  }

  /* 
   * Returns the unique identifier for a particular email template. This is used to associate the email template with
   * a particular EmailTemplateData.
   */
  def getEmailCode() {
    this.class.name
  }

	// Default implementation, so that subclasses are not required to implement it
	def getAttachments(data) {
		[]
	}

  static Boolean isEmail(String email) {
    email ==~ /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[A-Za-z]{2,4}/
  }

  // If no HTML tags add them, to better ensure the email does not end up in a spam folder - or worse
  static String ensureIsFullHtmlDocument(String doc) {
    if(!isFullHtmlDocument(doc)){
      doc = "<html><head></head><body>$doc</body></html>"
    }
    doc
  }

  static Boolean isFullHtmlDocument(String document) {
    document.trim().startsWith("<html>")
  }

}
