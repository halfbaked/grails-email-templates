package org.grails.plugin.emailTemplates


import org.grails.plugin.emailTemplates.test.Person


class ResetPasswordEmailTemplate extends EmailTemplate {

  String CODE = 'PASSWORD_ASSISTANCE' // could try generate code from class name
  String name = 'Password assistance'
  String description = 'Sent when users forget the password, and request help'
  String subject = 'Password assistance: how to reset your password'
  String body = '''
Dear {{ person.first_name }},

To continue the password reset process for the account {{ person.email }} click on the link below.          

[{{ reset_password_link }}]({{ reset_password_link }})

If clicking doesn't seem to work, you can copy and paste the link into your browser's
address window, or retype it there. Once you have returned to our site, we will give instructions for resetting your password.

If you did not request to have your password reset you can safely ignore this email.
It is likely another user entered your email address by mistake while trying to reset a password. Rest assured your customer account is safe.

We will never e-mail you and ask you to disclose or verify your password, credit card, or banking account number. 
If you receive a suspicious e-mail with a link to update your account information,
do not click on the link - instead, report the e-mail to us for investigation.

The X-Men 
'''

  void send(Person person) {
    def resetPasswordLink = g.createLink(controller:'login', action:'changePassword', params:[tokenKey: 'XXXX'], absolute: true)
    sendEmail(person.email, [
      person: person.asDataMap(),
      reset_password_link: resetPasswordLink
    ])
  }

  void sendTest(String recipient) {
    def person = Person.buildWithoutSave(email:recipient)
    send(person)
  }
    
}
