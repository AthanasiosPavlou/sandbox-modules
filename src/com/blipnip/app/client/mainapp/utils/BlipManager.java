package com.blipnip.app.client.mainapp.utils;

import org.gwtopenmaps.openlayers.client.layer.Vector;

public class BlipManager
{

	private final Vector newBlipVectorLayer;
	
		public Vector getNewBlipVectorLayer()
	{
		return newBlipVectorLayer;
	}
	
	public BlipManager(Vector vectorLayer)
	{
		this.newBlipVectorLayer = vectorLayer;
	}

}
