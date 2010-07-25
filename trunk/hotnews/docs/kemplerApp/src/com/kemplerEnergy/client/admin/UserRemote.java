package com.kemplerEnergy.client.admin;

import java.util.ArrayList;
import java.util.List;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.kemplerEnergy.model.admin.Group;
import com.kemplerEnergy.model.admin.User;

/**
 * User remote service
 * @author Ji
 *
 */
public interface UserRemote extends RemoteService {
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		private static UserRemoteAsync instance;
		public static UserRemoteAsync getInstance(){
			if (instance == null) {
				instance = (UserRemoteAsync) GWT.create(UserRemote.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "UserRemote");
			}
			return instance;
		}
	}
	

	/**
	 * Save a list of users
	 */
	public void saveUser(ArrayList<User> user);
	
	public ArrayList<Group> getGroupList(); 
}
