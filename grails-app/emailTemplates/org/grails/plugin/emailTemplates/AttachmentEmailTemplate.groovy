package org.grails.plugin.emailTemplates


import org.grails.plugin.emailTemplates.test.EmailTemplatesPerson


class AttachmentEmailTemplate extends EmailTemplate {


  String name = 'Attachment Email'
  String subject = 'Attachment Email'
  String body = '''
File attached
'''

  Map buildScopes(data){
		[ name: "hi" ]    
  }

  def buildTestDataMessage(){
		[ data: "data" ]    
  }
    
  Map dataKeys(){
    [
			[ name: "name" ]     
    ]
  }

  def getRecipients(data) {
    [[email:"ging", locale:null]]
  }

	def getAttachments(data) {
		[
			[ name: "bingo", type: "text", bytes: [0, 0, 0, 0, 0] as byte[] ]
		]
	}

}
