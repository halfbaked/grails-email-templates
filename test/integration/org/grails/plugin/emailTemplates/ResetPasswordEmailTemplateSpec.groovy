package org.grails.plugin.emailTemplates


import org.grails.plugin.emailTemplates.test.EmailTemplatesPerson

import groovy.mock.interceptor.MockFor
import com.icegreen.greenmail.util.*


class ResetPasswordEmailTemplateSpec extends grails.test.spock.IntegrationSpec {

  def greenMail
  def resetPasswordEmailTemplate
  def grailsEvents
  def grailsEventsRegistry
  def sessionFactory

  def setup() {
    greenMail.deleteAllMessages()
  }

  def "test the most used method: send"(){
    given:
      def person = EmailTemplatesPerson.buildWithoutSave(email:"eamonn@stratus5.com")    

    when:
      resetPasswordEmailTemplate.sendWithDataMessage(person)

    then:
      greenMail.waitForIncomingEmail(500, 1)
      greenMail.getReceivedMessages().size() == 1
  }

  def "test the sendTest"(){
    given: "a test recipient, and a test template"
      def testEmailAddress = "eamonn@stratus5.com"
      def emailDataTemplate = EmailTemplateData.build()

    when: "we send the test"
      resetPasswordEmailTemplate.sendTest(testEmailAddress, emailDataTemplate)

    then: "an email is successfully sent"
      greenMail.getReceivedMessages().size() == 1
  }

  def "test sendTest where email is invalid"(){
    given: "an invalid recipient email address"
      def invalidEmailAddress = "bademailaddress"
      def emailDataTemplate = EmailTemplateData.build()

    when:
      resetPasswordEmailTemplate.sendTest(invalidEmailAddress, emailDataTemplate)

    then: "no email is sent as recipient was invalid"
      greenMail.getReceivedMessages().size() == 0
  }

  def "test update emailTemplateData, then send email should use updated data"(){
    given:
      def person = EmailTemplatesPerson.buildWithoutSave()
      def testEmailAddress = "eamonn@stratus5.com"
      def resetPasswordEmailTemplateData = EmailTemplateData.findByCode(resetPasswordEmailTemplate.getEmailCode())

    when:
      resetPasswordEmailTemplateData.subject = "Howdy"
      resetPasswordEmailTemplateData.save(flush:true)
      resetPasswordEmailTemplate.sendTest(testEmailAddress, resetPasswordEmailTemplateData)

    then:
      greenMail.getReceivedMessages().size() == 1
      greenMail.getReceivedMessages()[0].subject == "Howdy"
  }

  def "test get data keys for email"(){
    when:
      def dataKeys = resetPasswordEmailTemplate.dataKeys()

    then:
      dataKeys.person != null
      dataKeys.resetPasswordLink == null
  }

  def "test email send when event is fired"(){
    given:
      def person = EmailTemplatesPerson.buildWithoutSave(email:"eamonn@stratus5.com", locale:new Locale("en"))   

    when:
      grailsEvents.event('emailTemplates', 'passwordResetRequested', person)

    then:
      greenMail.waitForIncomingEmail(50, 1)

    when:
      def message =  greenMail.getReceivedMessages()[0]
      def recipients = message.getAllRecipients()

    then:
      greenMail.util().getAddressList(recipients).contains(person.email)    
  }

  def "test sendEmail"(){
    given:
      def recipient = "eamonn@stratus5.com"
      def emailTemplateData = EmailTemplateData.buildWithoutSave()
      def scopes = [:]

    when:
      resetPasswordEmailTemplate.sendEmail(recipient, scopes, emailTemplateData)

    then:
      greenMail.getReceivedMessages().size() == 1

    when:
      def message =  greenMail.getReceivedMessages()[0]
      def recipients = message.getAllRecipients()

    then:
      greenMail.util().getAddressList(recipients).contains(recipient)    
  }

  def "test the send where there are multiple emailTemplateDatas of different locales but same code"(){
    given:
      def person = EmailTemplatesPerson.buildWithoutSave(email:"eamonn@stratus5.com", locale:new Locale("fr"))    
      def emailTemplateData = EmailTemplateData.build(locale:new Locale("fr"), subject:"french email", code:resetPasswordEmailTemplate.emailCode)
      def emailTemplateData2 = EmailTemplateData.build(locale:new Locale("sv"), subject:"swedish email", code:resetPasswordEmailTemplate.emailCode)
      sessionFactory.currentSession.flush()

    when:
      resetPasswordEmailTemplate.sendWithDataMessage(person)

    then:
      greenMail.getReceivedMessages().size() > 0
      greenMail.getReceivedMessages()[0].subject == "french email"
  }

  def "test send will send default where recipient has no locale"(){
    given: "a person without a locale"
      def person = EmailTemplatesPerson.buildWithoutSave(email:"eamonn@stratus5.com", locale: null)    
      def emailTemplateData = EmailTemplateData.build(locale:new Locale("fr"), subject:"french email", code:resetPasswordEmailTemplate.emailCode)
      def emailTemplateData2 = EmailTemplateData.build(locale:new Locale("sv"), subject:"swedish email", code:resetPasswordEmailTemplate.emailCode)
      sessionFactory.currentSession.flush()

    when: "we send the email template for this person"
      resetPasswordEmailTemplate.sendWithDataMessage(person)

    then: "a mail should have been sent"
      greenMail.getReceivedMessages().size() > 0

    when: "we look at the message"
      def message = greenMail.getReceivedMessages()[0]

    then: "we see the subject is the same as the default defined in the EmailTemplate artifact"
      message.subject == "Password assistance: how to reset your password"
  }

}
