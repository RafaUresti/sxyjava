package com.cis550.Beans;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.cis550.model.Site;
import com.cis550.util.SQLConn;


public class RecommendationBean {

	private ArrayList<String> siteHTMLs = null;
	private String mostPopularHTML = "";
	private String username;
	public void setUsernameCategory(String username){
		this.username = username;
		SQLConn sqlConn = new SQLConn();
		/*		// other sites of people who commented same as u
		String sql = "select distinct c3.siteId " + 
				"from Comments c1, Comments c2, Comments c3 " + 
				"where c1.siteId=c2.siteId and c1.username='"+username+"' and " +
				"c2.username<>c1.username and c3.username=c2.username and " +
				"c3.siteId<>c1.siteId";
		//other sites of people who voted as same as u
		String voteSiteIds="select distinct v3.siteId " + 
				"from Votes v1, Votes v2, Votes V3\r\n" + 
				"where v1.siteId=v2.siteId and v1.username='"+username+"'and " +
				"v2.username<>v1.username and v3.username=v2.username " +
				"and v3.siteId<>v1.siteId ";
		//sites u posted and others commented and other sites they commented 
		String voteSiteTd3="select distinct c3.siteId " + 
				"from Sites s1, Comments c2, Comments c3 " + 
				"where s1.siteId=c2.siteId and s1.username=name and " +
				"c2.username<>s1.username and c3.username=c2.username " +
				"and c3.siteId<>s1.siteId";
		//others sites of people who voted on what you commented on
		String voteSiteIds2="select distinct s3.siteId " + 
				"from Comments c1, Sites s2, Sites s3 " + 
				"where c1.siteId=s2.siteId and c1.username='"+username+"'and " +
				"s2.username<>c1.username and s3.username=s2.username " +
				"and s3.siteId<>c1.siteId";
		//other sites of people who commented on what you voted on

		//other sites of people who commented 
		 */		
		String sharedCategories = "SELECT DISTINCT category FROM Sites WHERE username='"+username+"'";
		String votedCategories = "SELECT DISTINCT S.category FROM Sites S, Votes V WHERE S.username='"+username+"'";
		String commentedCategories = "SELECT DISTINCT S.category FROM Comments C, Sites S WHERE C.username='"+username+"'";
		String interestedCategories = sharedCategories+" union "+votedCategories+" union "+commentedCategories;
		String sharedSiteCommentUsers = "SELECT DISTINCT C.username from Sites S, Comments C WHERE S.username='"+username+"'" +
		" and S.siteId=C.siteId and C.username<>S.username";
		//system.out.println("sharedSiteCommentUsers =" +sharedSiteCommentUsers);
		String sharedSiteVotedUsers = "SELECT DISTINCT V.username from Sites S, Votes V WHERE S.username='"+username+"'" +
		" and S.siteId=V.siteId and S.username<>V.username";
		//system.out.println("sharedSiteVotedUsers =" +sharedSiteVotedUsers);

		String commentedSiteCommentUsers = "SELECT DISTINCT C2.username from Comments C1, Comments C2 WHERE C1.username='"+username+"'" +
		" and C1.siteId=C2.siteId and C1.username<>C2.username";
		//system.out.println("commentedSiteCommentUsers =" +commentedSiteCommentUsers);

		String commentedSiteVotedUsers = "SELECT DISTINCT V.username from Comments C, Votes V WHERE C.username='"+username+"'" +
		" and C.siteId=V.siteId and C.username<>V.username";
		//system.out.println("commentedSiteVotedUsers =" +commentedSiteVotedUsers);

		String votedSiteCommentUsers="SELECT DISTINCT C.username from Votes V, Comments C WHERE V.username='"+username+"'" +
		" and V.siteId=C.siteId and V.username<>C.username";
		//system.out.println("votedSiteCommentUsers =" +votedSiteCommentUsers);

		String votedSiteVotedUsers = "SELECT DISTINCT V2.username from Votes V1, Votes V2 WHERE V1.username='"+username+"'" +
		" and V1.siteId=V2.siteId and V1.username<>V2.username";
		//system.out.println("votedSiteVotedUsers =" +votedSiteVotedUsers);

		String allRelatedUsers = sharedSiteCommentUsers + " union " + sharedSiteVotedUsers + " union " + commentedSiteCommentUsers +
		" union " + commentedSiteVotedUsers +  " union " + votedSiteCommentUsers +  " union " + votedSiteVotedUsers; 
		//system.out.println("allRelatedUsers =" +allRelatedUsers);

		String allRelatedCommentedSites = "SELECT C.siteId from Comments C, ("+allRelatedUsers+") U where C.username=U.username";
		//system.out.println("allRelatedCommentedSites =" +allRelatedCommentedSites);

		String allRelatedVotedSites = "SELECT V.siteId from Votes V, ("+allRelatedUsers+") U where V.username=U.username";
		//system.out.println("allRelatedVotedSites =" +allRelatedVotedSites);

		String allRelatedSharedSites = "SELECT S.siteId from Sites S, ("+allRelatedUsers+") U where S.username=U.username";
		//system.out.println("allRelatedSharedSites =" +allRelatedSharedSites);

		String allRelatedSites = allRelatedCommentedSites + " union " + allRelatedVotedSites + " union " + allRelatedSharedSites;
		//system.out.println("allRelatedSites =" +allRelatedSites);




		try {
			Class.forName(SQLConn.MYSQL_JDBC_DRIVER);
			Connection conn = sqlConn.getConnection();
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(interestedCategories);
			ArrayList<String> categories = new ArrayList<String>();

			while (results.next()){
				categories.add(results.getString("Category"));
			}
			String allRelatedSitesSameCategory = "SELECT S.* FROM Sites S, ("+allRelatedSites+") A where S.siteId=A.siteId";
			for (int i = 0; i < categories.size(); i++){
				if (i == 0){
					allRelatedSitesSameCategory += " and (";
				}
				allRelatedSitesSameCategory += "S.category='"+categories.get(i)+"'";
				if (i != categories.size() - 1){
					allRelatedSitesSameCategory += " OR ";
				} else {
					allRelatedSitesSameCategory += ")";
				}
			}
			//system.out.println("allRelatedSitesSameCategory =" +allRelatedSitesSameCategory);

			String allSharedSites = "SELECT siteId from Sites WHERE username='"+username+"'";
			System.out.println("allSharedSites =" +allSharedSites);

			String allCommentedSites = "SELECT siteId from Comments WHERE username='"+username+"'";
			System.out.println("allCommentedSites =" +allCommentedSites);

			String allVotedSites = "SELECT siteId from Votes WHERE username='"+username+"'";
			System.out.println("allVotedSites =" +allVotedSites);

			String allViewedSites = allSharedSites + " union " + allCommentedSites + " union " + allVotedSites;
			System.out.println("allViewedSites =" +allViewedSites);
			System.out.println("allRelatedSitesSameCategory = " + allRelatedSitesSameCategory);
			String allRecommendedSites = "SELECT R.* from ("+allRelatedSitesSameCategory+") R WHERE R.siteId NOT IN ("+allViewedSites+")";
			System.out.println("allRecommendedSites =" +allRecommendedSites);

			results = statement.executeQuery(allRecommendedSites);
			ArrayList<Site> sites = Site.buildSites(results);
			/*results = statement.executeQuery(voteSiteTd3);
			ArrayList<Site> sites2 = Site.buildSites(results);
			results= statement.executeQuery(voteSiteIds);
			ArrayList<Site> sites3 = Site.buildSites(results);
			results= statement.executeQuery(voteSiteIds2);
			ArrayList<Site> sites4 = Site.buildSites(results);
			Set<Site> egSet= new HashSet<Site>(sites);
			egSet.addAll(sites2);
			egSet.addAll(sites3);
			egSet.addAll(sites4);*/
			Collections.sort(sites, new Comparator<Site>(){
				@Override
				public int compare(Site s1, Site s2) {
					return s2.getVotes()-s1.getVotes();
				}
			});
			siteHTMLs = Site.generateSiteHTMLs(sites, username);
			if (siteHTMLs.size() == 0){
				generateMostPopularSitesHTML(sqlConn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} finally{
			try {
				sqlConn.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> getRecommendationSiteHTMLs(){
			return siteHTMLs;
	}
	
	private void generateMostPopularSitesHTML(SQLConn sqlConn) throws SQLException {
		 
		 Connection conn = sqlConn.getConnection();
		 Statement statement = conn.createStatement();
		 String siteVotes = "select s.*, count(v.username) as votes " +
			"from Sites s, Votes v where s.siteId=v.siteId group by v.siteId " +
			"union select s.*, 0 as votes from Sites s where s.siteId " +
			"not in (select siteId from votes)";
		 String scienceSiteVotes = "SELECT * FROM ("+siteVotes+") x" + " WHERE x.category='"+"Science"+"'";
		 //System.out.println("scienceSiteVotes=  " + scienceSiteVotes);
		 
		 String businessSiteVotes = "SELECT * FROM ("+siteVotes+") x"  + " WHERE x.category='"+"Business"+"'";
		 String sportsSiteVotes = "SELECT * FROM ("+siteVotes+") x"  + " WHERE x.category='"+"Sports"+"'";
		 String entertainmentSiteVotes = "SELECT * FROM ("+siteVotes+") x"  + " WHERE x.category='"+"Entertainment"+"'";

		 String scienceSiteVotesSql = "select j.* from ("+scienceSiteVotes+") j where j.votes = (select MAX(k.votes) from ("+ scienceSiteVotes + ") k)";
		 //System.out.println("scienceSiteVotesSql=  " + scienceSiteVotesSql);
		 String businessSiteVotesSql = "select j.* from ("+businessSiteVotes+") j where j.votes = (select MAX(k.votes) from ("+ businessSiteVotes + ") k)";
		 String sportsSiteVotesSql = "select j.* from ("+sportsSiteVotes+") j where j.votes = (select MAX(k.votes) from ("+ sportsSiteVotes + ") k)";
		 String entertainmentSiteVotesSql = "select j.* from ("+entertainmentSiteVotes+") j where j.votes = (select MAX(k.votes) from ("+ entertainmentSiteVotes + ") k)";

		 String siteComments = "select s.*, count(c.username) as comments " +
			"from Sites s, Comments c where s.siteId=c.siteId group by c.siteId " +
			"union select s.*, 0 as comments from Sites s where s.siteId " +
			"not in (select siteId from Comments)";

		 String scienceSiteComments = "SELECT * FROM ("+siteComments+") x"  + " WHERE x.category='"+"Science"+"'";
		 //System.out.println("scienceSiteComments=  " + scienceSiteComments);

		 String businessSiteComments = "SELECT * FROM ("+siteComments+") x"  + " WHERE x.category='"+"Business"+"'";
		 String sportsSiteComments = "SELECT * FROM ("+siteComments+") x"  + " WHERE x.category='"+"Sports"+"'";
		 String entertainmentSiteComments = "SELECT * FROM ("+siteComments+") x"  + " WHERE x.category='"+"Entertainment"+"'";

		 String scienceSiteCommentsSql = "select j.* from ("+scienceSiteComments+") j where j.comments = (select MAX(k.comments) from ("+ scienceSiteComments + ") k)";
		 String businessSiteCommentsSql = "select j.* from ("+businessSiteComments+") j where j.comments = (select MAX(k.comments) from ("+ businessSiteComments + ") k)";
		 String sportsSiteCommentsSql = "select j.* from ("+sportsSiteComments+") j where j.comments = (select MAX(k.comments) from ("+ sportsSiteComments + ") k)";
		 String entertainmentSiteCommentsSql = "select j.* from ("+entertainmentSiteComments+") j where j.comments = (select MAX(k.comments) from ("+ entertainmentSiteComments + ") k)";
		 
		 ResultSet results = statement.executeQuery(scienceSiteVotesSql);
		 ArrayList<Site> sites = Site.buildSites(results);
		 ArrayList<String> siteHTMLs = Site.generateSiteHTMLs(sites, username);
		 mostPopularHTML = "<h1>Science Category</h1>Most popular:<br/>" + siteHTMLs.get(0);
		 results = statement.executeQuery(scienceSiteCommentsSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 if (siteHTMLs.size() >0)
		 mostPopularHTML += "<br/>Most commented:<br/>" + siteHTMLs.get(0);
		 
		 results = statement.executeQuery(businessSiteVotesSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 mostPopularHTML += "<h1>Business Category</h1>Most popular:<br/>" + siteHTMLs.get(0);
		 results = statement.executeQuery(businessSiteCommentsSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 if (siteHTMLs.size() >0)
		 mostPopularHTML += "<br/>Most commented:<br/>" + siteHTMLs.get(0);
		 
		 results = statement.executeQuery(sportsSiteVotesSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 mostPopularHTML += "<h1>Sports Category</h1>Most popular:<br/>" + siteHTMLs.get(0);
		 results = statement.executeQuery(sportsSiteCommentsSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 if (siteHTMLs.size() >0)
		 mostPopularHTML += "<br/>Most commented:<br/>" + siteHTMLs.get(0);
		 
		 results = statement.executeQuery(entertainmentSiteVotesSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 mostPopularHTML += "<h1>Entertainment Category</h1>Most popular:<br/>" + siteHTMLs.get(0);
		 results = statement.executeQuery(entertainmentSiteCommentsSql);
		 sites = Site.buildSites(results);
		 siteHTMLs = Site.generateSiteHTMLs(sites, username); 
		 if (siteHTMLs.size() >0)
		 mostPopularHTML += "<br/>Most commented:<br/>" + siteHTMLs.get(0);
	}
	public String getMostPopularSitesHTML(){
		return mostPopularHTML;
	}
	public static void main(String[] args){
		new RecommendationBean().setUsernameCategory("aaaa");
	}
}

