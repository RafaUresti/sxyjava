<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="recommendationBean" class="com.cis550.Beans.RecommendationBean"
	scope="application">
	<jsp:setProperty name="recommendationBean" property="*" />
</jsp:useBean>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommended Sites</title>
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
<h1> Recommendations for you : </h1> 

<% 
String username = (String)session.getAttribute("name");
if (username == null){
	response.sendRedirect("login.html");
	return;
}
recommendationBean.setUsernameCategory(username);
ArrayList<String> htmls = recommendationBean.getRecommendationSiteHTMLs();
String mostPopularHTML = recommendationBean.getMostPopularSitesHTML();
if (htmls.size() > 0){
	for (String html: htmls){
		out.print(html+"<br/>");
	}
} else {
	out.print(mostPopularHTML);
}
%>
</div>
</body>
</html>