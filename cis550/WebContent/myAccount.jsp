<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="accountBean" class="com.cis550.Beans.AccountBean"
	scope="application">
	<jsp:setProperty name="accountBean" property="*" />
</jsp:useBean>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>My Account</title>
<link href="css/css1.css" rel="stylesheet" type="text/css" />
</head>
<body background="images/backgnd.jpg">
<body>


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
<table width="684">
<tr><td width="29"> </td> 
<td width="200"> </td> <td width="200"> </td>
<td width="200"><a href="friends.jsp">My Friends Link!!</a></td>
</tr>
<tr><td width="29"> </td> 
<td width="200"> </td> <td width="200"> </td>
<td width="200"><a href="recommendation.jsp">Recommendations Link!!</a></td>
</tr>
</tr>
<tr><td width="29"> </td> 
<td width="200"> </td> <td width="200"> </td>
<td width="200"><form method="get" action="RssFeed"><input type="submit" value="RSS"></form></td>
</tr>


</table>
<%
String name = (String)session.getAttribute("name");
if (name == null){
	response.sendRedirect("login.html");
	}
else {
	accountBean.setUsername(name);
	out.print(accountBean.generateAccountHTML());
}
	%>


</div>
</body>
</html>