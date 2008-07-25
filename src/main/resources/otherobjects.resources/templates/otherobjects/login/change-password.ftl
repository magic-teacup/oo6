<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
<@oo.css "/site/static/css/screen.css" />
</head>

<body>
<h1>Change your password</h1>

 
<@oo.showFlashMessages />


<form method="post">

<#--
 -->
<@spring.bind "passwordChanger.changeRequestCode" /> 
<@spring.formInput "passwordChanger.changeRequestCode" />
<fieldset>

<p>
  <label for="test">New password:</label><br/>
  <@spring.bind "passwordChanger.newPassword" /> 
  <@spring.formInput "passwordChanger.newPassword" /><br/>
  <#list spring.status.errorMessages as error> <span class="oo-field-error">${error}</span> <br> </#list> 
</p>

<p>
  <label for="test">New password confirmation:</label><br/>
  <@spring.bind "passwordChanger.newPasswordRepeated" /> 
  <@spring.formInput "passwordChanger.newPasswordRepeated" /><br/>
  <#list spring.status.errorMessages as error> <span class="oo-field-error">${error}</span> <br> </#list> 
</p>

<p><input type="submit" value="Change password" /></p>
</fieldset>
</form>
</body>
</html>