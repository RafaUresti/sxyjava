<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="categorizedNews" class="com.cis550.Beans.CategorizedNewsBean"
	scope="application">
	<jsp:setProperty name="categorizedNews" property="*" />
</jsp:useBean>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="css/css_sports.css" rel="stylesheet" type="text/css" />
</head>
<body background="images/backgnd.jpg">


<body>
<div id="header">
<div id="topnav">
<a href="register.html"><font color="#AF8B40">Register</font></a> | <a href="about.html"><font color="#AF8B40">About</font></a> | <a href="login.html"><font color="#AF8B40">Login</font></a></div>
</div>

<div id="top_links">
<div id="top_links_position">
<body link="black" alink="black" vlink="black">
<a href="home.html"><font color="#000000">LATEST</font></a> | <a href="business.html"><font color="#000000">BUSINESS</font></a> | <a href="science.html"><font color="#000000">SCIENCE</font></a> | <a href="sports.html"><font color="#000000">SPORTS</font></a> | <a href="entertainment.html"><font color="#000000">ENTERTAINMENT</font></a> | <a href="top_rated.html"><font color="#000000">TOP RATED</font></a></div>
<div id="search_position">
<img src="images/search.gif" width="25" height="25" /> 
<form method="post" action="Search">
<input id="search_width" type="text" name="search"> 
<input id="search_button" type="submit" value="Search" name="submit" /> 
</form> 


</div>
</div>

<div id="sport_body">
<div id="sport_body_position">
<h1> Sports News</h1>

<%
	categorizedNews.setCategory("Sports", (String)session.getAttribute("name"));
	ArrayList<String> sites = categorizedNews.getSites();
	if (sites != null){
		for (String site: sites){
			out.print(site+"\n");
		}
	} else {
		out.print("Cannot get sites!");
	}
%>
</div>
</div>

</body>
</html>
