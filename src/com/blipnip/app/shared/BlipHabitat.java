package com.blipnip.app.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

/**
 * A blip has one creator that cannot change. An blip can live in many habitats..
 * 
 * @author sdepdev
 *
 */
@Entity
@Cache
public class BlipHabitat implements Serializable 
{
	/*
	 * TODO - check again whether to use embed or serialize
	 * Objectify supports embedded classes and collections of embedded classes. 
	 * This allows you to store structured data within a single POJO entity in a way that remains queryable. 
	 * 
	 * An alternative to @Embed is to @Serialize, which will let you store nearly any Java object graph.
	 * 
	 * This class here (BlipPoint) is defined as static so that new points can be added without
	 * having to instantiate BlipHabitat, i.e. new BlipPoint(lat, lon);
	 */
	public static class BlipPoint implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1252829754828039815L;
		private double x;
		private double y;
		
		public BlipPoint()	{ }
		public BlipPoint(double x, double y) { this.x = x;this.y = y;	}
		public double getX() { return x;}
		public void setX(double x) { this.x = x;}
		public double getY()	 { return y; }
		public void setY(double y) {	this.y = y;}
	}

	/**
	 * You are strongly advised to place serialVersionUID on all classes that you intend 
	 * to store as @Serialize. Without this, any change to your classes will prevent stored 
	 * objects from being deserialized on fetch.
	 * 
	 * http://code.google.com/p/objectify-appengine/wiki/Entities#Serializing
	 */
	private static final long serialVersionUID = 570727525269775373L;
	
	@Id
	private Long id;
	
	// This is the user "clicks" on the map defining all the line segments comprising the trajectory
	@Serialize 
	private List<BlipPoint> loc = new ArrayList<BlipPoint>();
	
	//private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private double coordX;
	private double coordY;
	
	// This is the map that contains the processed loc (i.e. the broken-down line segments to points).
	// We use LinkedHashMap so that we have predictable insersion order.
	@Serialize
	private LinkedHashMap<Long, BlipPoint> timeAtLocMap = new LinkedHashMap<Long, BlipPoint>();
	
	// This is the time interval from datetime x to datetime x+1 used in timeAtLocMap, in milliseconds
	private long timeInterval;
	
	/**
	 * Default empty construstor needed by Objectify
	 */
	public BlipHabitat()
	{
		
	}
	
	public Long getId() 
	{
		return id;
	}
	
	public void setId(Long id) 
	{
		this.id = id;
	}
	
	public List<BlipPoint> getLoc() 
	{
		return loc;
	}
	
	public void setLoc(ArrayList<BlipPoint> loc) 
	{
		this.loc = loc;
	}
	
	public LinkedHashMap<Long, BlipPoint> getTimeAtLocMap()
	{
		return timeAtLocMap;
	}

	public void setTimeAtLocMap(LinkedHashMap<Long, BlipPoint> timeAtLocMap)
	{
		this.timeAtLocMap = timeAtLocMap;
	}

	public long getTimeInterval()
	{
		return timeInterval;
	}

	public void setTimeInterval(long timeInterval)
	{
		this.timeInterval = timeInterval;
	}

	public double getX() 
	{
		return coordX;
	}
	
	public void setX(double lon) 
	{
		this.coordX = lon;
	}
	
	public double getY() 
	{
		return coordY;
	}
	
	public void setY(double lat) 
	{
		this.coordY = lat;
	}

}
