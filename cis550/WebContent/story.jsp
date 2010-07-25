<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="storyBean" class="com.cis550.Beans.StoryBean"
	scope="application">
	<jsp:setProperty name="categorizedNews" property="*" />
</jsp:useBean>
<html>
<link href="css/css1.css" rel="stylesheet" type="text/css" />

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"><title>Story</title>
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
<h1></h1>
</div>
<%
	
	storyBean.setStory((String)request.getParameter("siteId"), (String)session.getAttribute("name"));
	out.print(storyBean.getStoryHTML());%>
</div>

<br/>
<br/>
<br/>
</body>
 </html>