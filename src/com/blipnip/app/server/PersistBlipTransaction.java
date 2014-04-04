package com.blipnip.app.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipMaster;
import com.blipnip.app.shared.BlipContent;
import com.blipnip.app.shared.BlipHabitat.BlipPoint;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

/**
 * We use this class because we want to make sure that 
 * every operation of the "persist blip" process will be completed -
 * achieved by putting everything within a transaction context (vrun).
 * 
 * @author Thanos
 *
 */
public class PersistBlipTransaction extends VoidWork
{
	private String blipMasterId;
	private BlipHabitat habitat; 
	private long picture;
	private String blipType;

	public PersistBlipTransaction(String blipMasterId,  BlipHabitat habitat, long picture, String blipType)
	{
		this.blipMasterId = blipMasterId;
		this.habitat = habitat;
		this.picture = picture;
		this.blipType = blipType;
	}

	@Override
	public void vrun()
	{
		Blip blip = new Blip();
		
		Key<BlipMaster> blipMasterKey = com.googlecode.objectify.Key.create(BlipMaster.class, blipMasterId); //Or if the entity has a @Parent Key.create(parentKey, Bar.class, id);		
		Key<BlipContent> contentKey = com.googlecode.objectify.Key.create(BlipContent.class, picture);

		BlipMaster blipMaster = getBlipMaster(blipMasterKey);
		
		if (blipMaster !=null)
		{		
			blip.setBlipMaster(blipMasterKey);
		
			// Difference is 1 sec = 1000 - better to make it 1 minute (60000) as it makes more sense business wise.
			setTimesOnHabitatPath(System.currentTimeMillis(), 60000, habitat);
			
			Key<BlipHabitat> habitatKey  = ofy().save().entity(habitat).now();

			blip.setBlipHabitat(habitatKey);
			
			blip.setPicture(contentKey);
			
			blip.setBlipMasterUsername(blipMaster.getBlipCreatorUserName());
			blip.setBlipUserPictureURL(blipMaster.getPictureUrl());
			
			blip.setBlipType(blipType);
			
			// All done, now save entity in DataStore
			Key<Blip> blipkey = ofy().save().entity(blip).now();
			
			// Update "Picuture is attached to blip" field
			BlipContent tempPicture = ofy().load().key(contentKey).now();
			tempPicture.setAttachedToBlip(true);
			ofy().save().entity(tempPicture).now();
			
			// Update BlipMaster with newly created blip
			//updateBlipMasterWithBlips(blipMaster.getBlipMasterEmail(), blipkey);
			blipMaster.setBlip(blipkey);
			ofy().save().entity(blipMaster).now();
			
			System.out.println("@PersistBlipTransaction, @persistBlip, saved blip: "+blipkey);
			System.out.println("@PersistBlipTransaction, @persistBlip, updated BlipMaster: "+blipMaster.getBlipMasterEmail()+" with Blip Key: "+blipkey);
			
		}
		else
		{
			System.out.println("@PersistBlipTransaction, @persistBlip, blipMaster not found, not persisting Blip."); 
		}
	}
	
	/**
	 * Get a BlipMaster 
	 * 
	 * @param blipMaster
	 * @return
	 */
	private BlipMaster getBlipMaster(Key<BlipMaster> blipMasterKey) 
	{	  
		return ofy().load().key(blipMasterKey).safe();
	}
	
	/**
	 * Based on the time of creation of the blip (currentTime)
	 * the babitat time-location map will be populated with each entry
	 * differing from the previous time as defined in timeInterval.
	 * 
	 * TODO - locations of habitat result to x + 2, need to fix this.
	 * 
	 * @param habitat
	 */
	private void setTimesOnHabitatPath(long currentTime, long timeInterval, BlipHabitat habitat)
	{
		habitat.setTimeInterval(timeInterval);
		
		long futuretime = currentTime;
		
		for (BlipPoint blipPoint: habitat.getLoc())
		{
			habitat.getTimeAtLocMap().put(futuretime, blipPoint);
			futuretime = futuretime + timeInterval;
			
			//Date newDate = new Date(futuretime);
			//SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
			//System.out.println("@PersistBlipTransaction, @setTimesOnHabitatPath, new date: "+dateFormat.format(newDate)+" for location: "+blipPoint.getX()+", "+blipPoint.getY()); 
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		System.out.println("@PersistBlipTransaction, @setTimesOnHabitatPath, (locations:"+habitat.getLoc().size()+") start: "+dateFormat.format(new Date(currentTime))+", end: "+dateFormat.format(new Date(futuretime-timeInterval))); 
		
		
	}

}
