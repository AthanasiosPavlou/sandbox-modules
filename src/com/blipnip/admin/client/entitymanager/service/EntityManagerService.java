package com.blipnip.admin.client.entitymanager.service;

import java.util.ArrayList;

import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipMaster;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.objectify.Key;

@RemoteServiceRelativePath("persistentservice")
public interface EntityManagerService extends RemoteService {
	
	void testPersistBlip(String blipMasterName, String blipType, String blipDescription) throws IllegalArgumentException;

	ArrayList<Blip> searchBlipType(String searchString);
	
	ArrayList<BlipMaster> getBlipMasters();

	ArrayList<Key<Blip>> getBlipKeysForMaster(String blipMasterName);
	
	ArrayList<BlipHabitat> getBlipHabitat(Long blipId);
}