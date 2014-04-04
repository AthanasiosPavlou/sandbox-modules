package com.blipnip.app.client.mainapp.service;

import java.util.ArrayList;

import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipHabitat.BlipPoint;
import com.blipnip.app.shared.BlipMaster;
import com.blipnip.app.shared.BlipContent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.objectify.Key;

public interface MainAppServiceAsync
{
	void searchBlipType(String searchString, AsyncCallback<ArrayList<Blip>> callback);

	void initBlipMaster(String blipMasterEmail, String blipMasterUsername, String rank, String imageURL, AsyncCallback<BlipMaster> callback);
	
	void updateBlipMasterWithBlips(String blipMasterName, Key<Blip> blipkey, AsyncCallback<BlipMaster> callback);
	
	void updateBlipWithHabitat(Blip blip, ArrayList<BlipPoint> mapPoints, AsyncCallback<Key<BlipHabitat>> callback);
	
	void getBlipMasters(AsyncCallback<ArrayList<BlipMaster>> callback);
	
	void getBlipKeysForMaster(String blipMasterName, AsyncCallback<ArrayList<Key<Blip>>> callback);
	
	void getBlipHabitat(Key<Blip> blipKey, AsyncCallback<BlipHabitat> callback);
	
	void getBlipContent(Key<Blip> blipKey, AsyncCallback<BlipContent> callback);
	
	void persistBlip(String blipMasterId, BlipHabitat habitat, long contentId, String blipType, AsyncCallback<Void> callback);
	
	void retrieveAllBlips(AsyncCallback<ArrayList<Blip>> callback);
}
