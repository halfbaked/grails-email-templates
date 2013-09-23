package org.grails.plugin.emailTemplates


class EmailTemplateSpec extends spock.lang.Specification {

  def "test isFullHtmlDocument for partial HTML document"(){
    given: "we have a partial html document"
      def partialDocument = "<p>Dear John,</p>"
    when: "We check the document is a full html document"
      def check = EmailTemplate.isFullHtmlDocument(partialDocument)
    then: "the result should be false"
      check == false
  }

  def "test isFullHtmlDocument for full HTML document"(){
    given: "we have a partial html document"
      def doc = "<html><head></head><body><p>Dear John,</p></body></html>"
    when: "We check the document is a full html document"
      def check = EmailTemplate.isFullHtmlDocument(doc)
    then: "the result should be false"
      check == true
  }

  def "test ensureFullHtmlDocument for partial HTML document"(){
    given: "we have a partial html document"
      def doc = "<p>Dear John,</p>"
    when: "We check the document is a full html document"
      doc = EmailTemplate.ensureIsFullHtmlDocument(doc)
    then: "The document should now actually be a full document"
      EmailTemplate.isFullHtmlDocument(doc)
  }

  def "test isEmail for various inputs"(){
    expect:
      EmailTemplate.isEmail(email) == expected
    where:
     email                        | expected
     "eamonn@stratus5.com"        | true
     "eamonnoconnell@gmail.com"   | true
     "eamonn.oconnell@gmail.com"  | true
     "@£aafdj"                    | false
     "asdfas@zxcvzlkvjad"         | false
  }

}
