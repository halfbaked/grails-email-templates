Email Templates Plugin for Grails
=================================

Build mustache and markdown based email templates quickly and easily in Grails

Installation
------------

Install this plugin by placing the appropriate line in the BuildConfig of your project

  compile ":email-templates:0.16"

Getting Started
---------------
You place the email templates for your application in grails-app/emailTemplates. You can look in the source code for the plugin
to see an example email template _ResetPasswordEmailTemplate_. To quickly get started, you can copy this email template into your own app and alter it to suit your application.

An Email Template Definition
----------------------------

Defining an email template, you must provide a number of attributes and functions

* *name* - the name of your email template
* *subject* - what will appear as the subject of your email
* *body* - the content that will appear as the body of your email
* *Map buildScopes(dataMessage)* - builds the data that will be available to an email template when it is processed
* *Map dataKeys()* -  A map of data keys illustrating what data can be injected into the body of the email template
* *getRecipients(dataMessage)* - Retrieves the recipients for this email, given the original dataMessage. 
* *buildTestDataMessage* - Builds a test data message. This is very useful when testing email templates work, and how they look.
* listener (optional)* - defines an event that the emailTemplate will be sent on. For this reason, Email Templates depends on the event functionality of Platform Core.

Event Driven Email Templates
----------------------------
You can define a listener map in your emailTemplate class that will indicate the event the email should be sent on. You can also define the namespace of that event, and whether to 
wait a specified period of time after the event has been fired before sending the email.

Example:
    def listener = [topic: "passwordResetRequested", namespace: "emailTemplates", delay:3000 /* wait 3 seconds */]


User Interface
--------------
There is a user interface for this plugin available as a separate plugin called email-templates-ui.

Attachments
-----------
You can specify how to build attachments for a particular email template by overriding the getAttachments(data) method.
