<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
    System.out.println( "Evaluating date now" );
   	java.util.Date date = new java.util.Date();
%>
<<<<<<< .mine
Hello!  The time is now <%= date %>
=======
Hello!  The time is now <%= date %>
<jsp:forward page="demo.jsp"/>
>>>>>>> .r41
</body>
</html>