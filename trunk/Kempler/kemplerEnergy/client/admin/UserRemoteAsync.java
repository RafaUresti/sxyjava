package com.kemplerEnergy.client.admin;

import java.util.ArrayList;
import java.util.List;


import com.kemplerEnergy.model.admin.Group;
import com.kemplerEnergy.model.admin.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * User remote service
 * @author Ji
 *
 */
public interface UserRemoteAsync {
	/**
	 * Save a list of users
	 */
	public void saveUser(ArrayList<User> user, AsyncCallback<Void> callback);
	
	public void getGroupList(AsyncCallback<ArrayList<Group>> callback); 
}
