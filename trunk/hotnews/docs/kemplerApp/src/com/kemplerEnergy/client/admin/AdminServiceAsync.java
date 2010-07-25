package com.kemplerEnergy.client.admin;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminServiceAsync
{	
	void SignIn(String name,String password, AsyncCallback<String> callback );
}
