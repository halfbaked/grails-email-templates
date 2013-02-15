package org.grails.plugin.emailTemplates


import org.grails.plugin.emailTemplates.test.EmailTemplatesPerson

import grails.plugin.spock.IntegrationSpec
import com.icegreen.greenmail.util.*


class ResetPasswordEmailTemplateSpec extends IntegrationSpec {

  def greenMail
  def resetPasswordEmailTemplate

  def setup() {
    greenMail.deleteAllMessages()
  }

  def "test the most used method: send"(){
    given:
    def person = EmailTemplatesPerson.buildWithoutSave()

    when:
    resetPasswordEmailTemplate.send(person)

    then:
    greenMail.getReceivedMessages().size() == 1
  }

  def "test the sendTest"(){
    given:
    def testEmailAddress = "eamonn@stratus5.com"

    when:
    resetPasswordEmailTemplate.sendTest(testEmailAddress)

    then:
    greenMail.getReceivedMessages().size() == 1
  }

  def "test sendTest where email is invalid"(){
    given:
    def invalidEmailAddress = "bademailaddress"

    when:
    resetPasswordEmailTemplate.sendTest(invalidEmailAddress)

    then:
    greenMail.getReceivedMessages().size() == 0
  }

  def "test update emailTemplateData, then send email should use updated data"(){
    given:
    def person = EmailTemplatesPerson.buildWithoutSave()
    def testEmailAddress = "eamonn@stratus5.com"
    EmailTemplateData.list().each {
      println "Email template data: $it.subject, $it.body, $it.code"
    }
    def resetPasswordEmailTemplateData = EmailTemplateData.findByCode(resetPasswordEmailTemplate.CODE)

    when:
    println "current subject: $resetPasswordEmailTemplateData.subject"
    resetPasswordEmailTemplateData.subject = "Howdy"
    resetPasswordEmailTemplateData.save(flush:true)
    resetPasswordEmailTemplate.sendTest(testEmailAddress)

    then:
    greenMail.getReceivedMessages().size() == 1
    greenMail.getReceivedMessages()[0].subject == "Howdy"
  }

}
