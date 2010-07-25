package com.upenn.cceb.rpsc.dataentry;

import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.upenn.cceb.rpsc.general.DBA;

public class ListParticipant
{
	public String  svErrMsg;
	public String  svSqlMsg;

	private String svSporeID;
	private String svLName;
	private String svFName;
	private String svDOB;
	private String svGenderID;
	private String svMRN;
	private String svHispanicYesNoID;
	private String svEthWhite;
	private String svEthBlack;
	private String svEthAsian;
	private String svEthPacific;
	private String svEthIndian;
	private String svBloodYesNoID;
	private String svConsentYesNoID;
	private String svTTRequestMRN;
	private String svIMAbstractionSporeID;
	private String svSCProcurementSporeID;
	private DBA    objDBA = null;
	private SimpleDateFormat objInForm= new SimpleDateFormat ("yyyy-mm-dd");
	private SimpleDateFormat objOutForm= new SimpleDateFormat ("mm/dd/yyyy");
	private Date dvDate = new Date();
  
  public boolean doQuery(String szSporeID)
  {
  	return doQuery(szSporeID, "", "");
  }
  
  public boolean doQuery(String szSporeID, String szMRN, String szLName)
  {
  	boolean bvFirstClause = true;
  	objDBA = new DBA();
    
    svErrMsg = objDBA.Open();
    if (svErrMsg != null)
    { return false; }
    
    String svSql = "";
    svSql = "SELECT a.SporeID, a.LName, a.FName, a.DOB, a.GenderID, a.MRN, ";
    svSql = svSql + " a.HispanicYesNoID, a.EthWhite, a.EthBlack, a.EthAsian, ";
    svSql = svSql + " a.EthPacific, a.EthIndian, a.ConsentYesNoID, a.BloodYesNoID,  a.ModifiedDate, ";
    svSql = svSql + " b.MRN AS TTRequestMRN, c.SporeID AS IMAbstractionSporeID, ";
    svSql = svSql + " d.SporeID AS SCProcurementSporeID ";
    svSql = svSql + " FROM tbl_participant a ";
    svSql = svSql + " LEFT JOIN tbl_tt_request_form b on a.MRN=b.MRN ";
    svSql = svSql + " LEFT JOIN tbl_im_abstraction_form c on a.SporeID=c.SporeID ";
    svSql = svSql + " LEFT JOIN tbl_sc_procurement_form d on a.SporeID=d.SporeID ";
            
    if(szSporeID.length() > 0)
    {
    	svSql = svSql + " WHERE a.SporeID =" + szSporeID;
    	bvFirstClause = false;
    }
    if(szMRN.length() > 0)
    {
    	if(bvFirstClause)
    	{
    		svSql = svSql + " WHERE ";
    	}else
    	{
    		svSql = svSql + " AND ";
    	}
    	svSql = svSql +"a.MRN='" + szMRN + "' ";
    	bvFirstClause = false;
    }
    if(szLName.length() > 0)
    {
    	if(bvFirstClause)
    	{
    		svSql = svSql + " WHERE ";
    	}else
    	{
    		svSql = svSql + " AND ";
    	}
    	svSql = svSql +"a.LName='" + szLName + "' ";
    	bvFirstClause = false;
    }
    svSql = svSql + " GROUP BY a.LName, a.FName, a.DOB, a.GenderID, a.MRN, ";
    svSql = svSql + " a.HispanicYesNoID, a.EthWhite, a.EthBlack, a.EthAsian, ";
    svSql = svSql + " a.EthPacific, a.EthIndian, a.ConsentYesNoID, a.ModifiedDate, ";
    svSql = svSql + " a.SporeID, b.MRN, c.SporeID, d.SporeID "; 
    svSqlMsg = svSql;
    svErrMsg = objDBA.doQuery(svSql);

    if (svErrMsg != null)
    {
        svErrMsg = svSql + "<BR>" + svErrMsg;
        return false;
    }

    return true;
  }

  public boolean close()
  {
    svErrMsg = objDBA.Close();
    if (svErrMsg != null)
    { return false; }
    return true;
  }

  public boolean next()
  {
    try
    {
      if (objDBA.rs.next())
      {
      	svSporeID = objDBA.rs.getString("SporeID");
      	svLName = objDBA.rs.getString("LName").toUpperCase();
      	svFName = objDBA.rs.getString("FName").toUpperCase();   

      	if(objDBA.rs.getString("DOB") != null)
      	{
        	dvDate = objInForm.parse(objDBA.rs.getString("DOB"), new ParsePosition(0));
      		svDOB = objOutForm.format(dvDate);
      	}else
      	{
      		svDOB = "";
      	}
      	svGenderID = objDBA.rs.getString("GenderID");
      	svMRN = objDBA.rs.getString("MRN");
      	svHispanicYesNoID = objDBA.rs.getString("HispanicYesNoID");
      	svEthWhite = objDBA.rs.getString("EthWhite");
      	svEthBlack = objDBA.rs.getString("EthBlack");
      	svEthAsian = objDBA.rs.getString("EthAsian");
      	svEthPacific = objDBA.rs.getString("EthPacific");
      	svEthIndian = objDBA.rs.getString("EthIndian");
      	svConsentYesNoID = objDBA.rs.getString("ConsentYesNoID");
      	svBloodYesNoID = objDBA.rs.getString("BloodYesNoID");
      	svTTRequestMRN = objDBA.rs.getString("TTRequestMRN");
      	svIMAbstractionSporeID = objDBA.rs.getString("IMAbstractionSporeID");
      	svSCProcurementSporeID = objDBA.rs.getString("SCProcurementSporeID");
      }
      else
      {
        svErrMsg = null;
        return false;
      }
    }
    catch (SQLException SQLE)
    {
      svErrMsg = SQLE.getMessage();
      return false;
    }

    return true;
  }

  public String get(String szField)
  {
  	String svRtn = null;	
  	if (szField.equalsIgnoreCase("SporeID")){ svRtn = svSporeID; }
		if (szField.equalsIgnoreCase("LName")){ svRtn = svLName; }
		if (szField.equalsIgnoreCase("FName")){ svRtn = svFName; }
		if (szField.equalsIgnoreCase("DOB")){ svRtn = svDOB; }
		if (szField.equalsIgnoreCase("GenderID")){ svRtn = svGenderID; }
		if (szField.equalsIgnoreCase("MRN")){ svRtn = svMRN; }
		if (szField.equalsIgnoreCase("HispanicYesNoID")){ svRtn = svHispanicYesNoID; }
		if (szField.equalsIgnoreCase("EthWhite")){ svRtn = svEthWhite; }
		if (szField.equalsIgnoreCase("EthBlack")){ svRtn = svEthBlack; }
		if (szField.equalsIgnoreCase("EthAsian")){ svRtn = svEthAsian; }
		if (szField.equalsIgnoreCase("EthPacific")){ svRtn = svEthPacific; }
		if (szField.equalsIgnoreCase("EthIndian")){ svRtn = svEthIndian; }
		if (szField.equalsIgnoreCase("BloodYesNoID")){ svRtn = svBloodYesNoID; }
		if (szField.equalsIgnoreCase("ConsentYesNoID")){ svRtn = svConsentYesNoID; }
		if (szField.equalsIgnoreCase("TTRequestMRN")){ svRtn = svTTRequestMRN; }
		if (szField.equalsIgnoreCase("IMAbstractionSporeID")){ svRtn = svIMAbstractionSporeID; }
		if (szField.equalsIgnoreCase("SCProcurementSporeID")){ svRtn = svSCProcurementSporeID; }
    if(svRtn == null)
    {
    	svRtn = "";
    }
		return svRtn;
  }
  
  public void finalize()
  {
      if(objDBA.dbconn != null)
          objDBA.Close();
  }
}

