//This package must be a sub package of client
package com.blipnip.app.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

/**
 *  
 * Implemented so far:
 * 
 * A blip has one creator that cannot change. An blip can live in many habitats.
 * 
 * @author sdepdev
 *
 */
@Entity
@Cache
public class Blip implements Serializable 
{
	
	/**
	 * You are strongly advised to place serialVersionUID on all classes that you intend 
	 * to store as @Serialize. Without this, any change to your classes will prevent stored 
	 * objects from being deserialized on fetch.
	 * 
	 * http://code.google.com/p/objectify-appengine/wiki/Entities#Serializing
	 */
	private static final long serialVersionUID = 2679472782718390219L;

	/**
	 * Along with the @Id and kind (i.e. Blip.class), the @Parent field defines the key (identity) of the entity.
	 * Construct a key to the car like this: Key.create(animalCreatorKey, Blip.class, animalid)
	 * The @Parent field can be Key, Key<?>, or Ref<?>
	 */
	@Parent 
	Key<BlipMaster> blipMaster;
	
	@Id
	Long id;

	/**
	    The @index annotation is used with Objectify version 4.0 upwards.
		Objectify previously indexed automatically all properties. Now we have to define 
		the one that is to be indexed. Indexing allows for a property to be searchable
		An indexable property is 2 writes. so for example when storing an animal record
		we will have 2 writes for storing the record and 2 for indexing the blipType property.
		If blipUsername was indexable as well then the total writes would be 6 (+2 for indexing the blipUsername).
		https://code.google.com/p/objectify-appengine/wiki/Queries
		http://localhost:8888/_ah/admin/datastore <- to check writes. 
	**/ 
	
	@Index
	private String blipType;
	
	@Index
	private String blipUsername;
	
	private String blipUserPictureURL;
	
	@Load 
	private Ref<BlipHabitat> blipHabitat;
	
	
	/**
	 * Even Key<?>s are not very convenient when you are working with graphs of entities. 
	 * Objectify provides Ref<?>, which works just like a Key<?> but allows you to directly 
	 * access the actual entity object as well
	 */
	@Load
	private Ref<BlipContent> blipContent;
	
	/**
	 * Default empty construstor needed by Objectify
	 */
	public Blip()
	{
		
	}
	
	public Long getId()
	{
		return id;
	}
	
	public Key<BlipMaster> getBlipMaster() 
	{
		return blipMaster;
	}


	public void setBlipMaster(Key<BlipMaster> blipMasterKey) 
	{
		this.blipMaster = blipMasterKey;
	}
		
	public void setBlipType(String type) 
	{
		this.blipType = type;
	}

	public String getBlipType() 
	{
		return blipType;
	}

	public void setBlipMasterUsername(String description) 
	{
		this.blipUsername = description;
	}

	public String getBlipMasterUsername() 
	{
		return blipUsername;
	}

	public String getBlipUserPictureURL()
	{
		return blipUserPictureURL;
	}

	public void setBlipUserPictureURL(String blipUserPictureURL)
	{
		this.blipUserPictureURL = blipUserPictureURL;
	}

	public BlipHabitat getBlipHabitat() 
	{
		return blipHabitat.get();
	}

	/**
	 * Creating reference using entity
	 * 
	 * @param blipHabitat
	 */
	public void setBlipHabitat(BlipHabitat blipHabitat) 
	{
		this.blipHabitat = Ref.create(blipHabitat);
	}
	
	/**
	 * Creating reference using key 
	 * 
	 * @param blipHabitatKey
	 */
	public void setBlipHabitat(Key<BlipHabitat> blipHabitatKey) 
	{
		this.blipHabitat = Ref.create(blipHabitatKey);
	}

	public BlipContent getBlipContent()
	{
		return blipContent.get();
	}

	public void setBlipContent(BlipContent blipContent)
	{
		this.blipContent = Ref.create(blipContent);
	}
	
	public void setPicture(Key<BlipContent> pictureKey)
	{
		this.blipContent = Ref.create(pictureKey);
	}
	
}
