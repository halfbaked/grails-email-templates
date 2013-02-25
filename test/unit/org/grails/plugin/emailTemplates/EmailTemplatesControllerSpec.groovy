package org.grails.plugin.emailTemplates


import grails.buildtestdata.mixin.Build
import grails.test.mixin.*


@TestFor(EmailTemplatesController)
@Mock([EmailTemplateData])
@Build([EmailTemplateData])
class EmailTemplatesControllerSpec extends spock.lang.Specification {

  def "test list action"(){
    given: "there are two email templates"
    EmailTemplateData.build()
    EmailTemplateData.build()

    when:
    def model = controller.list()

    then:
    model.emailTemplateDataList.size() == 2
  }

  def "test update action"(){
    given: "there is an emailTemplateData"
    def emailTemplateData = EmailTemplateData.build()

    when: "we call the controller update action with params"
    controller.request.method = "POST"
    controller.params.id = emailTemplateData.id
    controller.params.subject = "Welcome home"
    controller.params.body = "Dear John,"
    controller.update()

    then: "the emailTemplateData should have been updated to the data in the params"
    response.status != 404
    response.redirectedUrl =~ /list/
    emailTemplateData.subject == "Welcome home"
    emailTemplateData.body == "Dear John,"
  }

  def "test update action where emailTemplateData does not exist"(){
    when: "we call the sendTestEmail action with a crazy id"
    controller.params.id = 111122222333
    controller.update()

    then: "we are told not it was found"
    response.status == 404
  }


  def "test sendTest action should be success"(){
    given: "there is an emailTemplateData"
    
    def emailTemplateData = EmailTemplateData.build()
    emailTemplateData.grailsApplication = [ mainContext: [ getBean: { 
     [
      sendTest: { r, etd -> } 
     ]
    } ] ]

    when: "we call the sendTestEmail action"
    controller.params.id = emailTemplateData.id
    controller.params.recipient = "eamonn@stratus5.com"
    controller.params.subject = "Howdy"
    controller.params.body = "test body"
    controller.sendTestEmail()

    then: "we get a success acknowledgement"
    response.status == 200
  }

  def "test sendTest action where emailTemplateData does not exist"(){
    when: "we call the sendTestEmail action with a crazy id"
    controller.params.id = 111122222333
    controller.sendTestEmail()

    then: "we are told it was not found"
    response.status == 404
  }

}
