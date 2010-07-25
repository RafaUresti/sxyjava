<jsp:useBean id="ListParticipant" class="ListParticipant" />
<%@ include file="/includes/header.jsp" %>
<link rel="stylesheet" type="text/css" href="../includes/Style.css" />
<%
String svCmdSearch = request.getParameter("cmdSearch");
String svSporeID = "";
String svMRN = "";
String svLName = "";
String svTTRequestMRN = "";
String svIMAbstractionSporeID = "";
String svSCProcurementSporeID = "";
%>
<font size="3" face="Verdana, Arial, Helvetica, sans-serif">Participant</font><br>
<form name="form" method="post" action="ListParticipant.jsp">
<table width="800" border="0" class="s10table">
  <tr>
    <td colspan="2"><input type="button" name="cmdAddNew" value="Add New Participant" 
    onClick="javascript:window.location='EditParticipant.jsp';"></td>
  </tr>
  <tr>
    <td width="200">SPORE ID</td>
    <td><input type="text" name="fzSporeID" value="<%=svSporeID%>" size="16"></td>
  </tr>
  <tr>
    <td width="200">MRN</td>
    <td><input type="text" name="fzMRN" value="<%=svMRN%>" size="16"></td>
  </tr>
  <tr>
    <td width="200">Last Name</td>
    <td><input type="text" name="fzLName" value="<%=svLName%>" size="16"></td>
  </tr>
  <tr><td colspan="2"><input type="submit" name="cmdSearch" value="Search"></td></tr>
</table>
</form>
<%
  String msg = "";
  if(request.getParameter("msg") != null)
  {
  	msg = request.getParameter("msg");
  }
  
  if(msg.length() > 0)
  {
  %>  <font face="Verdana, Arial, Helvetica, sans-serif" size="2" color=red>
  <%=msg %></font><br>
  <%
  } 
  
  if(svCmdSearch!=null && svCmdSearch.equals("Search"))
  {
    svSporeID = request.getParameter("fzSporeID");
    svMRN = request.getParameter("fzMRN");
    svLName = request.getParameter("fzLName");
    
    if(ListParticipant.doQuery(svSporeID, svMRN, svLName))
    {
  	if(ListParticipant.next())
  	{%>
  	<table width="100%" border="0" class="s10table">
  	  <tr bgcolor="#CCFFCC">
  	  	<th></th>
        <th>SPORE ID</th>
  	    <th>MRN</th>
        <th>Last Name</th>
  	    <th>First Name</th>
        <th>Modified Date</th>
        <th>TT Request Form</th>
  	    <th>SC Tissue Procurement</th>
        <th>IM Abstraction Form</th>
        <th>View/Update</th>
        <th>Delete</th>
  	  </tr>
  	  <%
  	  boolean bvFlag = false;
      String svBgcolor;
  	  int cnt = 1;
  	  do{
  	    bvFlag = !bvFlag;
  	    svBgcolor=bvFlag?"EEEEEE":"DDDDDD";%> 
        <tr bgcolor="<%=svBgcolor%>"> 
  	    <td><%=cnt%></td>
          <td><%=ListParticipant.get("SporeID")%></td>
  	      <td><%=ListParticipant.get("MRN")%></td>
          <td><%=ListParticipant.get("LName")%></td>
  	      <td><%=ListParticipant.get("FName")%></td>
          <td><%=ListParticipant.get("ModifiedDate")%></td>
          <td>
      <%svTTRequestMRN = ListParticipant.get("TTRequestMRN");
        if(svTTRequestMRN.length() > 0)
        {%>
          <a href="ListTTRequestForm.jsp?fzMRN=<%=ListParticipant.get("MRN")%>&cmdSearch=Search"><img src="../images/EditBtn.gif" border="0" alt="Update"></a>
      <%} %>&nbsp;
          </td>
          <td>
      <%svSCProcurementSporeID = ListParticipant.get("SCProcurementSporeID");
        if(svSCProcurementSporeID.length() > 0)
        {%>
          <a href="ListSCProcurementForm.jsp?fzSporeID=<%=svSCProcurementSporeID%>&cmdSearch=Search"><img src="../images/EditBtn.gif" border="0" alt="Update"></a>
      <%} %>&nbsp;
          </td>
          <td>
      <%svIMAbstractionSporeID = ListParticipant.get("IMAbstractionSporeID");
        if(svIMAbstractionSporeID.length() > 0)
        {%>
          <a href="ListIMAbstractionForm.jsp?fzSporeID=<%=svIMAbstractionSporeID%>&cmdSearch=Search"><img src="../images/EditBtn.gif" border="0" alt="Update"></a>
      <%} %>&nbsp;
          </td>
          <td><a href="EditParticipant.jsp?fzSporeID=<%=ListParticipant.get("SporeID")%>"><img src="../images/EditBtn.gif" border="0" alt="Update"></a>
          </td>
          <td><a href="../SvDoParticipant?fzSporeID=<%=ListParticipant.get("SporeID")%>"
           onclick="return confirmLink('All data and forms of the participant <%=ListParticipant.get("SporeID")%> will be permanently deleted. Are you sure you want to delete them?')"><img src="../images/DelBtn.gif" border="0" alt="Delete"></a>
          </td>
  	  </tr>
  	  <%	cnt++;
  	      }while(ListParticipant.next());
  	  %>
  	</table>
  <%
      }else
      {%>
       <font face="Verdana, Arial, Helvetica, sans-serif" size="2" color=red>No records have been found.</font>
  <%
      }
    }else if (ListParticipant.svErrMsg != null)
    {
     %><%=ListParticipant.svErrMsg%><%
    }
    ListParticipant.close();
  }
%>
<%@ include file="/includes/footer.jsp" %>
<SCRIPT language="javascript" src="../includes/IsType.js"></SCRIPT>
