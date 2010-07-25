package com.kemplerEnergy.client.admin;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.kemplerEnergy.exception.AuthorizationException;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.exception.RPCException;
import com.kemplerEnergy.model.ShipMode;

public interface ShipModeRemote extends RemoteService {
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		private static ShipModeRemoteAsync instance;
		public static ShipModeRemoteAsync getInstance(){
			if (instance == null) {
				instance = (ShipModeRemoteAsync) GWT.create(ShipModeRemote.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "/ShipModeRemote");
			}
			return instance;
		}
	}
	

	/**
	 * Return the list of all available shipMode
	 */
	public ArrayList<ShipMode> getAllShipMode() throws RPCException, AuthorizationException;
	
	/**
	 * Save the shipmode either new or modified
	 * @param mode
	 * @throws RPCException
	 * @throws AuthorizationException
	 */
	public void saveShipMode(ShipMode mode) throws RPCException, AuthorizationException;

}
