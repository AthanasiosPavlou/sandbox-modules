package com.blipnip.admin.server.entitymanager.service;

import java.util.ArrayList;
import java.util.List;

import com.blipnip.admin.client.entitymanager.service.EntityManagerService;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipContent;
import com.blipnip.app.shared.BlipHabitat.BlipPoint;
import com.blipnip.app.shared.BlipMaster;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Key;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class EntityManagerServiceImpl extends RemoteServiceServlet implements EntityManagerService 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6821064785268659380L;

	/**
	 * This is the preferred way to register all entities used according to Objectify
	 * 
	 * on restart jetty Type 'com.google.appengine.api.datastore.Entity' was not included in the set of types
	 * if one of those is missing
	 * 
	 * http://code.google.com/p/objectify-appengine/wiki/Entities#Registering_Entities
	 */
	static 
	{
		ObjectifyService.register(BlipMaster.class);
		ObjectifyService.register(Blip.class);
		ObjectifyService.register(BlipHabitat.class);
		ObjectifyService.register(BlipContent.class);
	}
	
	/**
	 * Method must be present in Service Interface and Async Interface
	 */
	public void testPersistBlip(String blipMasterName, String blipType, String blipDesciption) 
	{
		System.out.println("@EntityManagerServiceImpl, @testPersistBlip"); 
		
		Blip blip = new Blip();
		
		/**
		 * The Key for blip will be auto generated when it gets saved and does not need to be set.
		 * 
		 * Use setters to populate the blip object
		 * 
		 */
		blip.setBlipMasterUsername(blipDesciption);
		blip.setBlipType(blipType);
		

		 // Add creator for this blip, add default one if no username is provived - for testing
		if (blipMasterName == null || blipMasterName.equals(""))
		{
			blipMasterName = "default@sample.com";
		}
		
		BlipMaster blipMaster = initBlipMaster(blipMasterName);
		Key<BlipMaster> blipCreatorKey = ofy().save().entity(blipMaster).now();
		blip.setBlipMaster(blipCreatorKey);
		
		// Add all habitats for this blip http://code.google.com/p/objectify-appengine/wiki/BasicOperations
		 updateBlipWithHabitat(blip, new java.util.Random().nextDouble(), new java.util.Random().nextDouble());
		
		// All done, now save entity in DataStore
		Key<Blip> blipkey = ofy().save().entity(blip).now();

		// Update BlipMaster with newly created blip
		updateBlipMasterWithBlips(blipMasterName, blipkey);
		
		//Key.create(animalCreatorKey, Blip.class, animalid)		
		System.out.println("@EntityManagerServiceImpl, @testPersistBlip, Save Complete -> "+blip.toString());
	}

	/**
	 * Attempt to retrieve BlipMaster. If it exists then set its fields and return.
	 * If it doesnt exist, create a new one, set its fields and and save in the Datastore (database).
	 * 
	 * @param blipMasterName: Name of BlipMaster
	 * @param imageURL
	 * @return
	 */
	private BlipMaster initBlipMaster(String blipMasterName)
	{
		Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMasterName);
		
		BlipMaster blipMaster = null;
		
		if (query != null)
		{
			if (query.count() ==1)
			{
				blipMaster = query.first().now();
				blipMaster.setBlipMasterEmail(blipMasterName);
				blipMaster.setTimeStored(System.currentTimeMillis());		
			}
			else
			{
				blipMaster = new BlipMaster();
				blipMaster.setBlipMasterEmail(blipMasterName);
				blipMaster.setTimeStored(System.currentTimeMillis());				
			}
		}
		else
		{
			System.out.println("@EntityManagerServiceImpl, @initBlipMaster, null query");
		}
		return blipMaster;
	}
	
	private BlipMaster updateBlipMasterWithBlips(String blipMasterName, Key<Blip> blipkey)
	{
		Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMasterName);
		
		BlipMaster blipMaster = null;
		
		if (query != null)
		{
			if (query.count() ==1)
			{
				blipMaster = query.first().now();
				
				// ...add the blip on blipMaster as well and save
				blipMaster.setBlip(blipkey);
				
				// Setting two more times to test behaviour 
				blipMaster.setBlip(blipkey);
				blipMaster.setBlip(blipkey);
				
				// Saving blipMaster entity
				ofy().save().entity(blipMaster).now();
			}
			else if (query.count() >1)
			{
				System.out.println("@EntityManagerServiceImpl, @updateBlipMaster, error multiple Blip Master found with same e-mail, please check integrity.");
			}
			else
			{
				System.out.println("@EntityManagerServiceImpl, @updateBlipMaster, Blip Master not found");
			}
		}
		else
		{
			System.out.println("@EntityManagerServiceImpl, @updateBlipMaster, null query");
		}
		return blipMaster;
	}
	
	private void updateBlipWithHabitat(Blip blip, double lon, double lat)
	{
		BlipHabitat habitat = new BlipHabitat();
		habitat.setX(lat);	
		habitat.setY(lon);
		
		habitat.getLoc().add(new BlipPoint(lat, lon));
		habitat.getLoc().add(new BlipPoint(lat, lon));
		habitat.getLoc().add(new BlipPoint(lat, lon));
		//BlipPoint geoPt = habitat.new BlipPoint(lat, lon);
		//ArrayList<BlipPoint> geoPtList = new ArrayList<BlipPoint>();
		//geoPtList.add(geoPt);
		//habitat.setLoc(geoPtList);
		
		Key<BlipHabitat> habitatKey  = ofy().save().entity(habitat).now();
		
		blip.setBlipHabitat(habitatKey);
		
		ofy().save().entity(blip).now();
		
		
//		List<Key<BlipHabitat>> blipHabitatList = blip.getBlipHabitats();
//		
//		if (blipHabitatList != null)
//		{
//			blip.getBlipHabitats().add(habitatKey);
//		}
//		else
//		{
//			System.out.println("@EntityManagerServiceImpl, @updateBlipWithHabitat, Blip Habitat List is null ");
//		}
	}
	
	
	/**
	 * Search by blip type
	 */
	public ArrayList<Blip> searchBlipType(String searchString) 
	{
		Query<Blip> q = ofy().load().type(Blip.class).filter("species", searchString);
		
		ArrayList<Blip> blips = new ArrayList<Blip>();
		
		//Loop the query results and add to the array
		for (Blip fetched : q) 
		{
			blips.add(fetched);
		}
		
		return blips;
	}
	
	/**
	 * Get all user ids
	 */
	public ArrayList<BlipMaster> getBlipMasters() 
	{
		//getTestAll();
		
		return new ArrayList<BlipMaster>(ofy().load().type(BlipMaster.class).list());		
	}
	
	/**
	 * Get all user ids
	 */
	public void getTestAll() 
	{
		System.out.println("Get all BlipMasters");
		List<BlipMaster> blipMasterList =  ofy().load().type(BlipMaster.class).list();
		for (BlipMaster blipMaster:blipMasterList)
		{
			System.out.println( "This is: "+blipMaster.getBlipMasterEmail() );
			
			Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMaster.getBlipMasterEmail());
			
			Key<BlipMaster> keyBlipMaster = query.keys().first().now();
			
			System.out.println( "This is query key: "+keyBlipMaster);
			
			//ArrayList<Key<Blip>> blipList = blipMaster.getBlipList();
			ArrayList<Key<Blip>> blipList = getBlipKeysForMaster(blipMaster.getBlipMasterEmail());
			
			for (Key<Blip> blipKey:blipList)
			{
				System.out.println( "From: "+blipMaster.getBlipMasterEmail()+", This is blip (id): "+blipKey.getId());
				//For each key get Blip entity 
				LoadResult<Blip> myBlip = ofy().load().type( Blip.class).parent(blipMaster).id(blipKey.getId());
				System.out.println("Blip Id: "+myBlip.now().getId()+", Example desciption: "+myBlip.now().getBlipMasterUsername());
			}
		}
		
		System.out.println("Get all blips");
		List<Blip> blipList =  ofy().load().type(Blip.class).list();
		for (Blip blip:blipList)
		{
			System.out.println("This is blip->blipmaster: "+ blip.getBlipMaster() );
		}		
	}
	
	public ArrayList<Key<Blip>> getBlipKeysForMaster(String blipMasterName)
	{
		ArrayList<Key<Blip>> blipKeyList = null;
		
		Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMasterName);
		if (query.count() == 1)
		{
			BlipMaster blipMaster = query.list().get(0);
			blipKeyList = blipMaster.getBlipKeyList();
		}
		else if (query.count() >1)
		{
			System.out.println("@EntityManagerServiceImpl, @getBlipsPerMaster, aborting more than one Blip Masters found");
		}
		else
		{
			System.out.println("@EntityManagerServiceImpl, @getBlipsPerMaster, aborting no Blip Masters found");
		}
		return blipKeyList;
	}
	
	public ArrayList<BlipHabitat> getBlipHabitat(Long blipId)
	{
		return null;
	}

}
