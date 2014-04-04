package com.blipnip.app.client.mainapp.service;

import java.util.ArrayList;

import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipHabitat.BlipPoint;
import com.blipnip.app.shared.BlipMaster;
import com.blipnip.app.shared.BlipContent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.objectify.Key;

@RemoteServiceRelativePath("mainapp")
public interface MainAppService extends RemoteService
{
	ArrayList<Blip> searchBlipType(String searchString);
	
	BlipMaster initBlipMaster(String blipMasterEmail, String blipMasterUsername, String rank, String imageURL);
	
	BlipMaster updateBlipMasterWithBlips(String blipMasterName, Key<Blip> blipkey);
	
	Key<BlipHabitat> updateBlipWithHabitat(Blip blip, ArrayList<BlipPoint> mapPoints);
	
	ArrayList<BlipMaster> getBlipMasters();

	ArrayList<Key<Blip>> getBlipKeysForMaster(String blipMasterName);
	
	BlipHabitat getBlipHabitat(Key<Blip> blipKey);
	
	BlipContent getBlipContent(Key<Blip> blipKey);
	
	void persistBlip(String blipMasterId, BlipHabitat habitat, long contentId, String blipType);
	
	ArrayList<Blip>retrieveAllBlips();
}
