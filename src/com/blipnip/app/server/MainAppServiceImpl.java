package com.blipnip.app.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.blipnip.app.client.mainapp.service.MainAppService;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipHabitat.BlipPoint;
import com.blipnip.app.shared.BlipMaster;
import com.blipnip.app.shared.BlipContent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

public class MainAppServiceImpl extends RemoteServiceServlet implements MainAppService
{
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 5257101191309109211L;

	/**
	 * on restart jetty Type 'com.google.appengine.api.datastore.Entity' was not included in the set of types
	 * if one of those is missing
	 */
	static 
	{
		ObjectifyService.register(BlipMaster.class);
		ObjectifyService.register(Blip.class);
		ObjectifyService.register(BlipHabitat.class);
		ObjectifyService.register(BlipContent.class);
	}


	/**
	 * Attempt to retrieve BlipMaster based on e-mail. If it exists then set its fields and return.
	 * If it doesnt exist, create a new one, set its fields and and save in the Datastore (database).
	 * 
	 * @param blipMasterName: Name of BlipMaster
	 * @param imageURL
	 * @return
	 */
	@Override
	public BlipMaster initBlipMaster(String blipMasterEmail,String blipMasterUsername, String rank, String imageURL)
	{
		Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMasterEmail);

		BlipMaster blipMaster = null;

		if (query != null)
		{
			if (query.count() ==1)
			{
				blipMaster = query.first().now();
				blipMaster.setBlipMasterEmail(blipMasterEmail);
				blipMaster.setBlipCreatorUserName(blipMasterUsername);
				blipMaster.setLoginRank(rank);
				blipMaster.setPictureUrl(imageURL);
				blipMaster.setTimeStored(System.currentTimeMillis());	
				ofy().save().entity(blipMaster).now();
			}
			else
			{
				blipMaster = new BlipMaster();
				blipMaster.setBlipMasterEmail(blipMasterEmail);
				blipMaster.setBlipCreatorUserName(blipMasterUsername);
				blipMaster.setLoginRank(rank);
				blipMaster.setPictureUrl(imageURL);
				blipMaster.setTimeStored(System.currentTimeMillis());	
				ofy().save().entity(blipMaster).now();
			}
		}
		else
		{
			System.out.println("@MainAppServiceImpl, @initBlipMaster, null query");
		}
		return blipMaster;
	}

	/**
	 * Attach a blip (key) to a blip master.
	 */
	@Override
	public BlipMaster updateBlipMasterWithBlips(String blipMasterName, Key<Blip> blipkey)
	{
		Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMasterName);

		BlipMaster blipMaster = null;

		if (query != null)
		{
			if (query.count() ==1)
			{
				blipMaster = query.first().now();

				// Add the blip on blipMaster as well and save
				blipMaster.setBlip(blipkey);
				ofy().save().entity(blipMaster).now();
			}
			else if (query.count() >1)
			{
				System.out.println("@MainAppServiceImpl, @updateBlipMaster, error multiple Blip Master found with same e-mail, please check integrity.");
			}
			else
			{
				System.out.println("@MainAppServiceImpl, @updateBlipMaster, Blip Master not found");
			}
		}
		else
		{
			System.out.println("@MainAppServiceImpl, @updateBlipMaster, null query");
		}
		return blipMaster;
	}

	/**
	 * Attach a habitat to a blip.
	 */
	@Override
	public Key<BlipHabitat> updateBlipWithHabitat(Blip blip, ArrayList<BlipPoint> mapPoints)
	{
		Key<BlipHabitat> habitatKey  = null;
		BlipHabitat blipHabitat = blip.getBlipHabitat();

		if (blipHabitat != null)
		{
			BlipHabitat habitat = new BlipHabitat();

			habitat.setLoc(mapPoints);

			habitatKey  = ofy().save().entity(habitat).now();

			blip.setBlipHabitat(habitatKey);

			ofy().save().entity(blip).now();

			ArrayList<BlipPoint> pointsList = (ArrayList<BlipPoint>)blip.getBlipHabitat().getLoc();
			for (BlipPoint geoPoint:pointsList)
			{
				System.out.println("@MainAppServiceImpl, @updateBlipWithHabitat, habitat of blip: "+" lat: "+geoPoint.getX()+", lon: "+geoPoint.getY());
			}

		}
		else
		{
			System.out.println("@MainAppServiceImpl, @updateBlipWithHabitat, Blip Habitat List is null ");
		}
		return habitatKey;
	}

	/**
	 * Get blip keys given a blip master.
	 */
	@Override
	public ArrayList<Key<Blip>> getBlipKeysForMaster(String blipMasterName)
	{
		ArrayList<Key<Blip>> blipKeyList = null;

		Query<BlipMaster> query = ofy().load().type(BlipMaster.class).filter("blipCreatorEmail", blipMasterName);
		if (query.count() == 1)
		{
			BlipMaster blipMaster = query.list().get(0);
			blipKeyList = blipMaster.getBlipKeyList();
		}
		else
		{
			System.out.println("@MainAppServiceImpl, @getBlipsPerMaster, aborting more than one Blip Masters found");
		}
		return blipKeyList;
	}

	/**
	 * Given a blip key, get the blip habitat
	 */
	@Override
	public BlipHabitat getBlipHabitat(Key<Blip> blipKey)
	{
		return ofy().load().key(blipKey).safe().getBlipHabitat();
	}

	/**
	 * Given a blip key, get the blip picture
	 */
	@Override
	public BlipContent getBlipContent(Key<Blip> blipKey)
	{
		return ofy().load().key(blipKey).safe().getBlipContent();
	}

	/**
	 * Search by blip type
	 */
	@Override
	public ArrayList<Blip> searchBlipType(String searchString) 
	{
		Query<Blip> q = ofy().load().type(Blip.class).filter("blipType", searchString);

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
	@Override
	public ArrayList<BlipMaster> getBlipMasters() 
	{	
		return new ArrayList<BlipMaster>(ofy().load().type(BlipMaster.class).list());		
	}

	/**
	 * TODO add override to use from service
	 * @return
	 */
	public ArrayList<Blip> getBlips() 
	{	
		return new ArrayList<Blip>(ofy().load().type(Blip.class).list());		
	}

	/**
	 * Get a BlipMaster 
	 * 
	 * @param blipMaster
	 * @return
	 */
	@SuppressWarnings("unused")
	private BlipMaster getBlipMaster(Key<BlipMaster> blipMasterKey) 
	{	 
		return ofy().load().key(blipMasterKey).safe();
	}


	/**
	 * Save a Blip in the Datastore
	 */
	@Override
	public void persistBlip(String blipMasterId,  BlipHabitat habitat, long contentId, String blipType)
	{
		ofy().transact(new PersistBlipTransaction(blipMasterId,  habitat, contentId,  blipType));
	}

	/**
	 * Retrieves all Blips in the Datastore
	 */
	@Override
	public ArrayList<Blip>retrieveAllBlips()
	{
		long currentTime = System.currentTimeMillis();

		ArrayList<Blip> blipList = new ArrayList<Blip>();

		ArrayList<Blip> currentBlipList = getBlips();

		if (currentBlipList != null && currentBlipList.size()>0)
		{
			for (Blip blip:currentBlipList)
			{
				if (blip != null)
				{
					BlipHabitat blipHabitat = blip.getBlipHabitat();
					if (blipHabitat != null)
					{
						if (!blipHabitat.getTimeAtLocMap().isEmpty())
						{
							if (findBlipTimeLocation(blipHabitat, currentTime))
							{
								blipList.add(blip);
							}
						}
						// No need to save...
						//ofy().save().entity(blipHabitat).now();
					}
					else
					{
						System.out.println("@MainAppServiceImpl, @retrieveAllBlips, blip habitat null or empty");
					}
				}
				else
				{
					System.out.println("@MainAppServiceImpl, @retrieveAllBlips, blip null or empty");
				}
			}
		}
		else
		{
			System.out.println("@MainAppServiceImpl, @retrieveAllBlips, blip list null or empty");
		}

		return blipList;
	}

	/**
	 * The date (and time) of the request (currentDateLong) is converted from milliseconds
	 * to a date format and then we search in the map to find a matching date.
	 * 
	 * This is built upon the premise of the entries of Map<Long, BlipPoint> being one
	 * second apart i.e. entry 0 having a date 28/12/12 14:00:43, entry 1 having a date
	 * 28/12/12 14:00:44 etc.
	 *  
	 * @param blipHabitat
	 * @param currentDateLong
	 */
	private  boolean findBlipTimeLocation(BlipHabitat blipHabitat, Long currentDateLong) 
	{
		boolean found = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		Date currentDate = new Date(currentDateLong);
		String currentDateFormatted = dateFormat.format(currentDate); 

		Map<Long, BlipPoint> dateEvents = blipHabitat.getTimeAtLocMap();
		for (Map.Entry<Long, BlipPoint> entry : dateEvents.entrySet()) 
		{
			Date blipDate = new Date(entry.getKey());	    		

			//if(blipDate.before(currentDate)) //<- do this to retrieve all... :)
			if(currentDate.before(blipDate))
			//if(dateFormat.format(currentDate).equals(dateFormat.format(blipDate)))
			{
				System.out.println("\n @MainAppServiceImpl, @findBlipTimeLocation, Event " + dateFormat.format(blipDate) + " is on the specified date"+"("+currentDateFormatted+") \n");
				
				//We are setting the value but not saving in the datastore
				blipHabitat.setX(entry.getValue().getX());
				blipHabitat.setY(entry.getValue().getY());
				found = true;
				break;
			}
		}
		return found;
	}

}
