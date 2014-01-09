package org.grails.plugin.emailTemplates


class AttachmentEmailTemplateSpec extends grails.test.spock.IntegrationSpec {

	def attachmentEmailTemplate
	def resetPasswordEmailTemplate 

	def "test specifying a getAttachments method will add an attachment"(){

		given: "And email template with an attachment"
			def messageBuilder = Mock(grails.plugin.mail.MailMessageBuilder)
		  attachmentEmailTemplate.mailService = [
				sendMail: { closure ->
					closure.delegate = messageBuilder
					closure.call()
				}				
			]
	
		when: "we call send on the email template"
		  attachmentEmailTemplate.sendTest "eamonn@stratus5.com", attachmentEmailTemplate.createDefaultTemplate()

		then: "Then the email sent will contain an attachment"
			1 * messageBuilder.attach(_,_,_)
  }

	def "test email with no attachments method will continue to work"(){
		
		given: "An email template with no attachment"
			def mailService = Mock(grails.plugin.mail.MailService)
			resetPasswordEmailTemplate.mailService = mailService
		
		when: "we call send on the email template"	
			resetPasswordEmailTemplate.sendTest "eamonn@stratus5.com", resetPasswordEmailTemplate.createDefaultTemplate()

		then: "the email will be sent without error"
			1 * mailService.sendMail(_)
	}
  
}
