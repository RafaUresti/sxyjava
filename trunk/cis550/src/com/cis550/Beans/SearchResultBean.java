package com.cis550.Beans;

import java.util.ArrayList;

import com.cis550.model.Site;

public class SearchResultBean {
	private static ArrayList<String> siteHTMLs = new ArrayList<String>();
	public static void setSitesFound(ArrayList<Site> sitesFound, String username){
		siteHTMLs = Site.generateSiteHTMLs(sitesFound, username);
	}
	public ArrayList<String> getSiteHTMLs(){
		return siteHTMLs;
	}
}
