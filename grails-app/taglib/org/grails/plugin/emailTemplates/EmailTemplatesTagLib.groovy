package org.grails.plugin.emailTemplates

class EmailTemplatesTagLib {

  static namespace = 'emailTemplates'

  // Had to split the sendTest into two tags as had to place the modal outside the form
  def sendTestBtn = { 
    out << """
<!-- Button to trigger modal -->
<a href="#sendTestModal" data-toggle="modal" style="background-color:#eee;margin-left:10px;margin-right:10px;margin-top:5px;padding:8px;border-radius:4px;">Send Test</i></a>
    """
  }

  def sendTestModal = { 
    out << """
<!-- Modal -->
<div id="sendTestModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="myModalLabel">Send Test Email</h3>
  </div>
  <div class="modal-body">
    <p>You can send a test email using this template to an email address to verify how it looks.</p>    
    <input placeholder="recipient email" name="testEmailRecipient" type="email" required /> <br />
    <a id="sendTestEmailBtn" href="#" class="btn btn-primary">Send</a>
  </div>
  <script>    
    jQuery("#sendTestEmailBtn").click(function(event) {
      jQuery.ajax({
        url:'/emailTemplates/sendTestEmail',
        type:'POST',
        data: {
          recipient: jQuery("input[name=testEmailRecipient]").val(),
          subject: jQuery("input[name=subject]").val(),
          body: jQuery("textarea[name=body]").val(),
          id: jQuery("input[name=id]").val()
        }
      })
      .done(function(resp){
        jQuery("#sendTestModal").modal('hide');
      })
      .fail(function(resp){
        jQuery("#sendTestModal").modal('hide');
        alert("Error sending email");
      });               
    });
  </script>
</div>
    """
  }

  def emailFormattingHelp = { attrs ->
    out << """
<!-- Button to trigger modal -->
<a href="#formattingModal" data-toggle="modal" style="background-color:#eee;margin-left:10px;margin-right:10px;margin-top:5px;padding:8px;border-radius:4px;">Formatting</i></a>
 
<!-- Modal -->
<div id="formattingModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="myModalLabel">Formatting Help</h3>
  </div>
  <div class="modal-body">
    <div><p> Stratus5 uses the popular Markdown for formatting. Here are the basics. See the <a href="http://daringfireball.net/projects/markdown/syntax" target="_blank">complete syntax</a>. </p> 
    <hr> <p class="quiet">First Level Header</p> <code> Making Scrambled Eggs: A Primer<br> =============================== </code> <hr> <p class="quiet">Second Level Header</p> <code> 1.1: Preparation<br> ---------------- </code> <hr> <p class="quiet">Paragraphs</p> <code> Add two new lines to start a new paragraph. Crack two eggs into the bowl and whisk. </code> <hr> <p class="quiet">Bold</p> <code> **Carefully** crack the eggs. </code> <hr> <p class="quiet">Emphasis</p> <code> Whisk the eggs *vigorously*. </code> <hr> <p class="quiet">Lists</p> <code> Ingredients:<br><br> - Eggs<br> - Oil<br> - *Optional:* milk </code> <hr> <p class="quiet">Links</p> <code> To download a PDF version of the recipe, [click here](https://example.com/scrambled-eggs.pdf). </code> <hr> <p class="quiet">Images</p> <code> ![The Finished Dish](https://example.com/eggs.png) </code> </div>
  </div>
</div>
    """
  }

  def emailParametersHelp = { attrs ->
    out << """
<!-- Button to trigger modal -->
<a href="#parametersModal" data-toggle="modal" style="background-color:#eee;margin-left:10px;margin-right:10px;margin-top:5px;padding:8px;border-radius:4px;">Parameters</i></a>

<!-- Modal -->
<div id="parametersModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="myModalLabel">Dynamic Data Parameters Help</h3>
  </div>
  <div class="modal-body">
    <div>
      <p>
        You can include dynamic data in your emails. Dynamic data is included using the mustache templating language. See the <a href="http://mustache.github.com/mustache.5.html" target="_blank">offical manual</a> for the complete syntax. 
        Below is a list of the data you can include in this email
      </p>
      <hr>
""" 
    def other = []
    attrs.scopes.each { key, value ->

      if (!value) { other << key }
      else {
        out << """<hr><p class="quiet" style="font-weight:bold;">${key}</p>"""
        value.each { 
          out << """<code>{{ $key.$it }}</code><br>"""  
        }
      }
    }
    if (other) {
      out << """<hr> <p class="quiet" style="font-weight:bold;">Other</p> """
      other.each {
        out << """
          <code>{{ $it }}</code>
        """
      }
    }
    out << """
    </div>
  </div> 
</div>
    """
  }
}

