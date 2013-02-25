package org.grails.plugin.emailTemplates


import org.grails.plugin.emailTemplates.test.EmailTemplatesPerson

import grails.plugin.spock.IntegrationSpec
import groovy.mock.interceptor.MockFor
import com.icegreen.greenmail.util.*


class ResetPasswordEmailTemplateSpec extends IntegrationSpec {

  def greenMail
  def resetPasswordEmailTemplate
  def grailsEvents
  def grailsEventsRegistry

  def setup() {
    greenMail.deleteAllMessages()
  }

  def "test the most used method: send"(){
    given:
    def person = EmailTemplatesPerson.buildWithoutSave(email:"eamonn@stratus5.com")    

    when:
    resetPasswordEmailTemplate.send(person)

    then:
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
    def person = EmailTemplatesPerson.buildWithoutSave(email:"eamonn@stratus5.com")    

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

  def "test sendEmail where mailService throws exception"(){
    given:
    def recipient = "eamonn@stratus5.com"
    def emailTemplateData = EmailTemplateData.buildWithoutSave()
    resetPasswordEmailTemplate.mailService = [
      sendMail: { throw new Exception("") }
    ]
    def scopes = [:]

    when:
    resetPasswordEmailTemplate.sendEmail(recipient, scopes, emailTemplateData)

    then:
    greenMail.getReceivedMessages().size() == 0
  }

}
