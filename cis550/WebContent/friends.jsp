<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="addFriendBean" class="com.cis550.Beans.AddFriendBean"
	scope="application">
	<jsp:setProperty name="addFriendBean" property="*" />
</jsp:useBean>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Friends</title>
<link href="css/css1.css" rel="stylesheet" type="text/css" />
</head>
<body background="images/backgnd.jpg">



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
</div>
<br/>
<h1>&nbsp&nbspYour Friends Page</h1>

<br/>
<form method="post" action="AddFriend">
<TABLE BORDER=0>
<TR>
<TD> 
&nbsp&nbspEnter Friend to Add: 
</TD>
<td>
<input type="text" name="friend" size="20"> 
</td>
<td>
</td>
<td>
<input type="submit" value="Add" name="submit" /> 
</td>
</tr>
</TABLE>
</form>
<br/>
<br/>
<br/>
<% String username = (String)session.getAttribute("name");
if (username == null){
	response.sendRedirect("login.html");
}
else{
	addFriendBean.setUsername(username);
	out.print(addFriendBean.generateFriendHTMLs());
}
%>
</div>
</body>
</html>