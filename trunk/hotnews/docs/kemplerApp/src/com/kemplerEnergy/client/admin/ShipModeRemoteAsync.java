package com.kemplerEnergy.client.admin;

import java.util.ArrayList;

import com.kemplerEnergy.model.ShipMode;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ShipModeRemoteAsync {
	/**
	 * Return the list of all available shipMode
	 */
	public void getAllShipMode(AsyncCallback<ArrayList<ShipMode>> callback);
	
	/**
	 * Save the shipmode either new or modified
	 * @param mode
	 * @throws RPCException
	 * @throws AuthorizationException
	 */
	public void saveShipMode(ShipMode mode, AsyncCallback<Void> callback);

}
