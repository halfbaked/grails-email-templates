package org.grails.plugin.emailTemplates


import org.grails.plugin.emailTemplates.test.EmailTemplatesPerson

import groovy.mock.interceptor.MockFor
import com.icegreen.greenmail.util.*


class ResetPasswordEmailTemplateWhereExceptionSpec extends grails.test.spock.IntegrationSpec {

  def greenMail
  def resetPasswordEmailTemplate
  def grailsEvents
  def grailsEventsRegistry
  def sessionFactory
  def mailService

  def setup() {
    greenMail.deleteAllMessages()
    resetPasswordEmailTemplate.mailService = [
      sendMail: { throw new Exception("") }
    ]
  }

  def cleanup() {
    resetPasswordEmailTemplate.mailService = mailService
  }

  def "test sendEmail where mailService throws exception"(){
    given: 
      def recipient = "eamonn@stratus5.com"
      def emailTemplateData = EmailTemplateData.buildWithoutSave()
      def scopes = [:]

    when:
      resetPasswordEmailTemplate.sendEmail(recipient, scopes, emailTemplateData)

    then:
      greenMail.getReceivedMessages().size() == 0
  }

}
