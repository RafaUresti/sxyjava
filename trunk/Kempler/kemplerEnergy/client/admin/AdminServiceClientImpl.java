package com.kemplerEnergy.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class AdminServiceClientImpl implements AdminListener{
	
	private class SignInCallback implements AsyncCallback{
		public void onFailure(Throwable throwable){ GWT.log("error sign in",throwable); }
		public void onSuccess(Object obj){
			String re = (String)obj;
			if (re.equals("right"))
			{
				controller.OnSignIn();
			}
			else
				Window.alert("wrong username or password");
			
		}
	}
	
	private AdminServiceAsync admininService;
	private AdminViewController controller = new AdminViewController( this );
/*	public AdminServiceClientImpl (String URL)
	{
		admininService=(AdminServiceAsync)GWT.create(AdminService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget)admininService;
		endpoint.setServiceEntryPoint( URL );
	}*/
	
	public void onSignIn(String name, String password) {
		admininService.SignIn( name, password, new SignInCallback() );
	}
	
	public AdminViewController getView(){
		return controller;
	}

	public void setView(AdminViewController controller) {
		this.controller = controller;
	}


}
