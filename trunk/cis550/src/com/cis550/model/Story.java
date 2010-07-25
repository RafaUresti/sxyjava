package com.cis550.model;

import java.util.ArrayList;

public class Story {
	private Site site;

	private ArrayList<Comment> comments;
	private String storyHTML = "";

	public Story(Site site, ArrayList<Comment> comments){
		this.site = site;
		this.comments = comments;
	}
	
	/**
	 * Generates the HTML representation of this Story
	 * @return the HTML representation of this Story
	 */
	public String generateHTML(String username){
		
		storyHTML = "<br/>"+site.generateHTML(false, username);
		storyHTML += "<h1>Comments:</h1>";
		for (Comment comment: comments){
			storyHTML += comment.generateHTML();
			storyHTML += "<br/>";
		}
		return storyHTML;
	}	

	public Site getSite() {
		return site;
	}
	public ArrayList<Comment> getComments() {
		return comments;
	}
	public String getStoryHTML() {
		return storyHTML;
	}
}
