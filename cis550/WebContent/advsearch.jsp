<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link href="css/css_login.css" rel="stylesheet" type="text/css" />
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
<h1>Advanced Search</h1>
<br/>
<br/>
<br/>
<FORM METHOD=GET ACTION="AdavancedQuery">
<table width="400" border="0">
  <tr>
    <td>Search String:</td>
    
   </tr>
    <tr>
    <td><textarea cols="40" rows="2" name=advsearch></textarea></td>
    </tr>
  </tr>
  <tr>
    <td>Popularity:</td></tr>
    <tr>    <td><select name="popularity" size="1">
			<option value="0"> >0</option>
			<option value="2"> >2 </option>
			<option value="5"> >5 </option>
			<option value="10"> >10 </option>
			<option value="100"> >100 </option>
					</select></td>
  </tr>
  <tr>
    <td>Date:</td>
    </tr>
    <tr>    <td><select name="time" size="1">
			<option value="Business">old</option>
			<option value="Science">new</option>
			
					</select></td>
  </tr>
  <tr>
    <td>Category:</td>
    </tr>
    <tr>
    <td><select name="category" size="1">
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