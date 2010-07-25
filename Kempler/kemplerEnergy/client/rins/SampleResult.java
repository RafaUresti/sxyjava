package com.kemplerEnergy.client.rins;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;


public class SampleResult implements IsSerializable { // have to implements IsSerializable
	public int type;
	public String name;
	public ArrayList<String> resultArrayList;
	
	//we also have to have constructor for serialization
	public SampleResult(){
		type = 0;
		name = "";
		resultArrayList=new ArrayList<String>();
	}
}
