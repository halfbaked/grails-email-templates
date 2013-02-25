<!DOCTYPE html>
<html>
  <head>
    <title>Emails</title>
  </head>
  <body>
    <div class="block">
      <div class="row-fluid">
        <div class="span12">
          <header class="group">
            <h1 class="pull-left"><i class="icon-envelope"></i> Email Templates (${emailTemplateDataList.size()})</h1>           
          </header>
        </div>
      </div>
      <div class="row-fluid">
        <div class="filters span1">
        </div> 
        <div class="span10">
          <ezb:addOrDownload />
          <table class="results table table-bordered">
            <thead>
              <tr>
                <th>Name</th>
              </tr>
            </thead>
            <tbody>
            <g:each in="${emailTemplateDataList}" var="emailTemplateData">
            <tr>
              <td><g:link action="update" id="${emailTemplateData.id}">${emailTemplateData.name}</g:link></td>
            </tr>
            </g:each>
            </tbody>
          </table>
        </div> <!-- end span -->
      </div> <!-- end fluid row -->
    </div> <!-- end block -->
  </body>
</html>
