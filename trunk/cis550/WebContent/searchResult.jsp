<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="searchResultBean" class="com.cis550.Beans.SearchResultBean"
	scope="application">
	<jsp:setProperty name="searchResultBean" property="*" />
</jsp:useBean>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Search Results</title>
<link href="css/css1.css" rel="stylesheet" type="text/css" />
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

<div id="login_body">
<div id="login_body_position">
</div>
<br/>
<h1> Search Results : 
<a href="advsearch.jsp"><font color="#00F"> Click me to go to Advanced Search!!!</font></a></h1>
<br/>
<%
ArrayList<String> siteHTMLs = searchResultBean.getSiteHTMLs();
if (siteHTMLs.size() == 0){
	out.print("No search keyword matching found!");
} else{
	for (String html:siteHTMLs){
		out.print("<br/>"+html); 
	}
}
%>
</div>

</body>
</html>