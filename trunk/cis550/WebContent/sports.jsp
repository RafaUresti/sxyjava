<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="categorizedNews" class="com.cis550.Beans.CategorizedNewsBean"
	scope="application">
	<jsp:setProperty name="categorizedNews" property="*" />
</jsp:useBean>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Sports News</title>
<link href="css/css_sports.css" rel="stylesheet" type="text/css" />
</head>
<body background="images/backgnd.jpg">

<body>
<div id="header">
<div id="topnav">
<a href="register.html"><font color="#AF8B40">Register</font></a> | <a href="about.html"><font color="#AF8B40">About</font></a> | <a href="login.html"><font color="#AF8B40">Login</font></a> | <a href="myAccount.jsp"><font color="#AF8B40">Account</font></a></div>
</div>

<div id="top_links">
<div id="top_links_position">
<body link="black" alink="black" vlink="black">
<a href="home.jsp"><font color="#000000">LATEST</font></a> | <a href="business.jsp"><font color="#000000">BUSINESS</font></a> | <a href="science.jsp"><font color="#000000">SCIENCE</font></a> | <a href="sports.jsp"><font color="#000000">SPORTS</font></a> | <a href="entertainment.jsp"><font color="#000000">ENTERTAINMENT</font></a> | <a href="top_rated.jsp"><font color="#000000">TOP RATED</font></a> | <a href="newStory.jsp"><font color="#000000">NEW STORY</font></a></div>
<div id="search_position">
<img src="images/search.gif" width="25" height="25" /> 
<form method="get" action="KeywordQuery">
<input id="search_width" type="text" name="search"> 
<input id="search_button" type="submit" value="Search" name="submit" /> 
</form> 


</div>
</div>

<div id="sport_body">
<div id="sport_body_position">
<h1> Sports News</h1>

</div>
<br/>
<br/>
<br/>
<%
	categorizedNews.setCategory("Sports", (String)session.getAttribute("name"));
	ArrayList<String> sites = categorizedNews.getSites();
	if (sites != null){
		for (String site: sites){
			out.print(site+"<BR/>");
		}
		if (sites.size() == 0){
			out.print("<big>There is no Sports news!</big><br/>");
			out.print("<big>You are welcome to share!<big>");
		}
	} else {%>
		Cannot get sites!
	<%}
%>
</div>
</body>
</html>