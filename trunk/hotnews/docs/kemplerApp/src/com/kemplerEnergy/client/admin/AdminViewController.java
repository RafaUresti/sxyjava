package com.kemplerEnergy.client.admin;



public class AdminViewController  {
	private AdminListener listener;
//	private AdministratorStartPanel AllMenu = new AdministratorStartPanel(this);
	
	public AdminViewController(AdminListener listener) {
		this.listener = listener;
	}
	
	public AdminListener getListener(){
		return listener;
	}

	public void OnSignIn() {
		// TODO Auto-generated method stub
		
	}

}