<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add comment</title>

<link href="css/css_login.css" rel="stylesheet" type="text/css" /></head>
<body background="images/backgnd.jpg">

<%
String siteId = request.getParameter("siteId");
if (session.getAttribute("name") == null){
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
<h1>Add Comments</h1>
<br/>
<br/>
<br/>

<form action="AddComment" method="post">
  Comments:<br />
 <textarea cols="50" rows="3" name="comment"></textarea><br />
<%out.println("<INPUT TYPE=hidden NAME=\"siteId\" VALUE=\""+siteId+"\">"); %>
  <input type="submit" value="Submit" />
</form>

</body>
</html>