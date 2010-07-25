<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.ArrayList"%>
<jsp:useBean id="categorizedNews" class="com.cis550.Beans.CategorizedNewsBean"
	scope="application">
	<jsp:setProperty name="categorizedNews" property="*" />
</jsp:useBean>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>News</title>
	</head>
	<body>
<%
	categorizedNews.setCategory("all");
	ArrayList<String> sites = categorizedNews.getSites();
	if (sites != null){
		for (String site: sites){
			out.print(site+"<BR/>");
		}%>
		<big><%=sites.size() %></big>
	<%} else {%>
		Cannot get sites!
	<%}
%>

<ul class="news-digg">
<li class="digg-count" id="main0">
<a href="/business_finance/Oil_Executive_Predicts_1_Gas_In_2009_1st_time_since_1999" id="diggs0" ><strong id="diggs-strong-0">80</strong> diggs </a>  </li>
<li class="digg-it" id="diglink0"><a href="/login" onclick="return(poppd(0, this))" id="diggit-9878001">digg it</a></li>
</ul></div>
<div class="news-summary" id="enclosure1" style="z-index:990">
<div class="news-body">
<h3>
<a href="http://msn.foxsports.com/nfl/story/8902814?MSNHPHMA"
 class="offsite ct-sports"> Interviews reveal details of Burress incident</a>
</h3><p><em class="source">msn.foxsports.com &#8212;</em> Find out the events leading up to the even that pretty much killed his career.<a href="/football/Interviews_reveal_details_of_Burress_incident" class="more">More&#8230;</a> <span class="topic">(<a href="/football">American & Canadian Football</a>)</span></p>
<div class="news-details">
<a href="/football/Interviews_reveal_details_of_Burress_incident" class="tool comments">4 Comments</a>
    <a href="/login" onclick="return popps(1, 9886648, this)" class="tool share">Share</a>
<div id="bury-tool_1" class="bury tool">
<a href="#" class="bury-link" id="bury-link_9886648">Bury</a>
</div><a id="fave1" class="tool fave" style="display: none" onclick="return fave(9886648,1,'73023a1fc46ddac2a0f81bad5fc92664', 0);" href="#">Favorite?</a><span class="tool user-info">
<a href="/users/BrokebackCasket"><img src="/users/BrokebackCasket/s.png" alt="BrokebackCasket" width="16" height="16" class="user-photo" />BrokebackCasket</a> made popular <span class="d"> <span class="d">3 min ago</span></span>
<br/>
</span>
</div></div>
</body>
</html>