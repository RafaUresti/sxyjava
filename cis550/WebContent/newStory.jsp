<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>New Story</title>
<link href="css/css_login.css" rel="stylesheet" type="text/css" />
</head>
<body background="images/backgnd.jpg">


<SCRIPT>
function validate(mnv) {
if (mnv=="") {
alert('Category is a required field. Please try again.');
event.returnValue=false;

}
else { 
	event.returnValue=true;
}
}
</SCRIPT>

<%if (session.getAttribute("name") == null){
		response.sendRedirect("login.html");
  }
 %>
 
<div id="header">
<div id="topnav">
<!--Register | About | Login-->
</div>
</div>

<div id="top_links">
<div id="top_links_position">
<body link="black" alink="black" vlink="black">
<a href="home.jsp"><font color="#000000">LATEST</font></a> | <a href="business.jsp"><font color="#000000">BUSINESS</font></a> | <a href="science.jsp"><font color="#000000">SCIENCE</font></a> | <a href="sports.jsp"><font color="#000000">SPORTS</font></a> | <a href="entertainment.jsp"><font color="#000000">ENTERTAINMENT</font></a> | <a href="top_rated.jsp"><font color="#000000">TOP RATED</font></a> | <a href="newStory.jsp"><font color="#000000">NEW STORY</font></a></div>
</div>
<div id="login_body">
<div id="login_body_position">
<h1>Create New Story</h1>
<br/>
<br/>
<br/>
<FORM METHOD=POST ACTION="newStory" onsubmit="validate(category.value);">
<table width="400" border="0">
  <tr>
    <td>Story Title:</td>
    
   </tr>
    <tr>
    <td><textarea cols="40" rows="2" name=title></textarea></td>
    </tr>
  </tr>
  <tr>
    <td>Story URL: </td></tr>
    <tr><td>
    <textarea cols="40" rows="2" name=url></textarea>
  </td>
  </tr>
  <tr>
    <td>Website Summary:</td>
    </tr>
    <tr><td>
    <textarea cols="40" rows="5" name="summary"></textarea>
  </td>
  </tr>
  <tr>
    <td>Category:<select name="category" size="1">
			<option selected value="">CATEGORY</option>
			<option value="Business">Business</option>
			<option value="Science">Science</option>
			<option value="Sports">Sports</option>
			<option value="Entertainment">Entertainment</option>
			<option value="Other">Other</option>
					</select></td>
  </tr>
  
  <tr>
  <td>
  <INPUT TYPE=SUBMIT value="Submit">
  </td>
  </tr>
  
</table>

</FORM>


</div>
</body>
</html>