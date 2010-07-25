package com.kemplerEnergy.client.admin;
import com.google.gwt.user.client.rpc.RemoteService;

public interface AdminService extends RemoteService
{	
	String SignIn(String name,String password );
}
