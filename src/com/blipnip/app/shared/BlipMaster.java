package com.blipnip.app.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * A blip has one creator that cannot change. An blip can live in many habitats.
 * 
 * @author Thanos
 *
 */
@Entity
public class BlipMaster implements Serializable 
{

	/**
	 * You are strongly advised to place serialVersionUID on all classes that you intend 
	 * to store as @Serialize. Without this, any change to your classes will prevent stored 
	 * objects from being deserialized on fetch.
	 * 
	 * http://code.google.com/p/objectify-appengine/wiki/Entities#Serializing
	 */
	private static final long serialVersionUID = 7316383997459598928L;
	
	@Id
	@Index
	private String blipCreatorEmail;
	
	private String blipCreatorUserName;
	
	private ArrayList<Ref<Blip>> blips;
	
	private String pictureUrl;
	
	private String loginRank = "default";
	
	private long timeStored;	
	
	//This is here to retrieve all live blips for master
	//private ArrayList<Key<LiveBlip>
	
	/**
	 * Default empty construstor needed by Objectify
	 */
	public BlipMaster()
	{
		
	}
	
	/**
	 * "Masking" the references using this type of getter
	 * The user just calls getBlips which returns Blips - not references.
	 * 
	 * When 
	 * 
	 * @return
	 */
	public ArrayList<Blip> getBlips()
	{
		ArrayList<Blip> blipList = new ArrayList<Blip>();
		
		if (blips != null)
		{
			for (Ref<Blip> blipRef:blips)
			{
				blipList.add(blipRef.get());
			}
		}
		return blipList;
	}
	
	public ArrayList<Key<Blip>> getBlipKeyList()
	{
		ArrayList<Key<Blip>> blipKeyList = new ArrayList<Key<Blip>>();
		
		if (blips != null)
		{
			for (Ref<Blip> blipRef:blips)
			{
				blipKeyList.add(blipRef.getKey());
			}
		}
		return blipKeyList;
	}

	/**
	 * "Masking" the references using this type of setter
	 * The user just calls setBlip which sets a Blip - not references.
	 * 
	 * @param blip
	 */
	public void setBlip(Blip blip)
	{
		if (this.blips != null)
		{
			this.blips.add(Ref.create(blip));
		}
		else
		{
			this.blips = new ArrayList<Ref<Blip>>();
			this.blips.add(Ref.create(blip));
		}
		
	}
	
	/**
	 * Same as above but using key instead of blip object
	 * 
	 * @param blip
	 */
	public void setBlip(Key<Blip> blip)
	{
		if (this.blips != null)
		{
			this.blips.add(Ref.create(blip));
		}
		else
		{
			this.blips = new ArrayList<Ref<Blip>>();
			this.blips.add(Ref.create(blip));
		}
		
	}
	
	/**
	 * "Masking" the references using this type of setter
	 * The user just calls setBlip which sets a Blip - not references.
	 * 
	 * @param blipList
	 */
	public void setBlips(ArrayList<Blip> blipList)
	{
		for (Blip blip:blipList)
		{
			this.blips.add(Ref.create(blip));
		}
	}

	public String getBlipMasterEmail() 
	{
		return blipCreatorEmail;
	}

	public void setBlipMasterEmail(String creatorEmail) 
	{
		this.blipCreatorEmail = creatorEmail;
	}
	
	public String getBlipCreatorUserName()
	{
		return blipCreatorUserName;
	}

	public void setBlipCreatorUserName(String blipCreatorFirstName)
	{
		this.blipCreatorUserName = blipCreatorFirstName;
	}

	public String getPictureUrl()
	{
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl)
	{
		this.pictureUrl = pictureUrl;
	}

	public String getLoginRank()
	{
		return loginRank;
	}

	public void setLoginRank(String loginRank)
	{
		this.loginRank = loginRank;
	}

	public long getTimeStored()
	{
		return timeStored;
	}

	public void setTimeStored(long randNum)
	{
		this.timeStored = randNum;
	}
}
