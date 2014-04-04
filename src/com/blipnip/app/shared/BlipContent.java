package com.blipnip.app.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class BlipContent implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8635876892751689628L;
	
	@Id
	public Long id;
	
	private String title;
	private String description;
	private String fullBlobImageUrl;
	private String imageUrl;
	private boolean isAttachedToBlip;
	private long duration;
	
	/**
	 * When we insert a BlipContent object in the BlobStore a corresponding 
	 * record is created (automatically) in an entity called "__BlobInfo__" 
	 * (the entity is also created automatically). When this is created it 
	 * means that the blob has been saved in the Blobstore.
	 * 
	 * Blobs can't be modified after they're created, though they can be deleted. Each blob has a corresponding blob info record, 
	 * stored in the datastore, that provides details about the blob, such as its creation time and content type. 
	 * You can use the blob key to fetch blob info records and query their properties.
	 * 
	 *  https://developers.google.com/appengine/docs/java/blobstore/
	 */
	private String blobKeyString; 
	
	private long timeStored;	
	
	public BlipContent()
	{
		
	}
	
	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getId()
	{
		return id;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public String getFullBlobImageUrl()
	{
		return fullBlobImageUrl;
	}

	public void setFullBlobImageUrl(String imageUrl)
	{
		this.fullBlobImageUrl = imageUrl;

	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public boolean isAttachedToBlip()
	{
		return isAttachedToBlip;
	}

	public void setAttachedToBlip(boolean isAttachedToBlip)
	{
		this.isAttachedToBlip = isAttachedToBlip;
	}

	public String getBlobKey()
	{
		return blobKeyString.trim();
	}

	public void setBlobKey(String blobKey)
	{
		this.blobKeyString = blobKey.trim();
	}

	public long getTimeStored()
	{
		return timeStored;
	}

	public void setTimeStored(long randNum)
	{
		this.timeStored = randNum;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}
	
	//To get key, For objectify 4 use:

//	public com.googlecode.objectify.Key<BlipContent> getKey() 
//	{
//		return com.googlecode.objectify.Key.create(BlipContent.class, getId());
//	}
	
	//Or if the entity has a @Parent
	//public Key<Bar> getKey() 
	//{
	//  return Key.create(parentKey, Bar.class, id);
	//}
	
}