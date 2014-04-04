package com.blipnip.admin.client.entitymanager.service;

import java.util.ArrayList;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipMaster;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.objectify.Key;

public interface EntityManagerServiceAsync 
{

	void testPersistBlip(String blipMasterName, String blipType, String blipDescription, AsyncCallback<Void> callback);

	void searchBlipType(String searchString, AsyncCallback<ArrayList<Blip>> callback);

	void getBlipMasters(AsyncCallback<ArrayList<BlipMaster>> callback);
	
	void getBlipKeysForMaster(String blipMasterName, AsyncCallback<ArrayList<Key<Blip>>> callback);
	
	void getBlipHabitat(Long blipId, AsyncCallback<ArrayList<BlipHabitat>> callback);
}
