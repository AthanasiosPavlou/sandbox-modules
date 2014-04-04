package com.blipnip.app.client.mainapp.utils;

import java.util.ArrayList;
import java.util.Map;

import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.blipnip.app.shared.Blip;
import com.google.gwt.user.client.Timer;

public class StrokeAnimation extends Timer
{
	private Vector activeBlipsVectorLayer =null;
	private VectorFeature vectorFeature = null;
	private  ArrayList<Blip> blipListForLayer = null;
	private  Map<Long, VectorFeature> blipFeatureMap = null;
	private  long  animationDuration;
	private  long startTime;
	private boolean adjustedStrokeOpacityMaxReached = false;
	private boolean adjustedStrokeOpacityMinReached = false;

	private double defaultStrokeOpacity;
	private double adjustedStrokeOpacity;
	private final static double opacityInterval = 0.1;

	public StrokeAnimation(ArrayList<Blip> blipListForLayer, Map<Long, VectorFeature> blipFeatureMap, long animationDuration, double defaultStrokeOpacity)
	{
		//if (animateSingleRandomBlip)
		//{
		//	this.randomlySelectedBlip = blipListForLayer.get(new Random().nextInt(blipListForLayer.size()));
	   //}
		this.blipListForLayer = blipListForLayer;
		this.blipFeatureMap = blipFeatureMap;
		this.startTime = System.currentTimeMillis();
		this.animationDuration = animationDuration;
		this.defaultStrokeOpacity = defaultStrokeOpacity;
		this.adjustedStrokeOpacity = defaultStrokeOpacity;
	}
	
	public StrokeAnimation(VectorFeature vectorFeature,Vector activeBlipsVectorLayer, long animationDuration, double defaultStrokeOpacity)
	{
		this.vectorFeature = vectorFeature;
		this.activeBlipsVectorLayer = activeBlipsVectorLayer;
		this.startTime = System.currentTimeMillis();
		this.animationDuration = animationDuration;
		this.defaultStrokeOpacity = defaultStrokeOpacity;
		this.adjustedStrokeOpacity = defaultStrokeOpacity;
	}

	@Override
	public void run()
	{
		if (this.blipListForLayer != null)
		{
			animateBlipList();
		}
		else if (this.vectorFeature != null)
		{
			animateBlip();
		}

	}

	private void animateBlip()
	{
		long currentTime = System.currentTimeMillis();

		if (currentTime - startTime > animationDuration)
		{
			//System.out.println("@StokeAnimation,  @animateBlip, Cancelling singleStrokeTimer...");
			this.cancel();

			vectorFeature.getStyle().setStrokeOpacity(defaultStrokeOpacity);
			vectorFeature.getStyle().setStrokeWidth(5);
			activeBlipsVectorLayer.drawFeature(vectorFeature);
		}
		else
		{
			if (!adjustedStrokeOpacityMaxReached)
			{
				adjustedStrokeOpacity = adjustedStrokeOpacity+opacityInterval;
				if (adjustedStrokeOpacity >=0.9)
				{
					adjustedStrokeOpacityMaxReached = true;
					adjustedStrokeOpacity = adjustedStrokeOpacity-opacityInterval;
				}
			}
			else if (!adjustedStrokeOpacityMinReached) 
			{
				adjustedStrokeOpacity = adjustedStrokeOpacity-opacityInterval;
				if (adjustedStrokeOpacity <=defaultStrokeOpacity)
				{
					adjustedStrokeOpacityMinReached = true;
					adjustedStrokeOpacity = adjustedStrokeOpacity+opacityInterval;
				}
			}
			
			vectorFeature.getStyle().setStrokeOpacity(adjustedStrokeOpacity);
			vectorFeature.getStyle().setStrokeWidth(5+(adjustedStrokeOpacity*3));
			activeBlipsVectorLayer.drawFeature(vectorFeature);
			
			//http://dev.openlayers.org/apidocs/files/OpenLayers/Layer/Vector-js.html#OpenLayers.Layer.Vector.drawFeature
			//http://lists.osgeo.org/pipermail/openlayers-users/2008-July/006587.html
		}
	}

	private void animateBlipList()
	{
		long currentTime = System.currentTimeMillis();

		if (currentTime - startTime > animationDuration)
		{

			System.out.println("@StokeAnimation,  @animateBlip, Cancelling strokeTimer...");
			this.cancel();

			for (Blip blip:blipListForLayer)
			{
				if (blipFeatureMap.get(blip.getId()) != null)
				{
					blipFeatureMap.get(blip.getId()).getStyle().setStrokeOpacity(defaultStrokeOpacity);
					activeBlipsVectorLayer.drawFeature(blipFeatureMap.get(blip.getId()));
				} 
			}
		}
		else
		{
			if (!adjustedStrokeOpacityMaxReached)
			{
				adjustedStrokeOpacity = adjustedStrokeOpacity+opacityInterval;
				if (adjustedStrokeOpacity >=1){adjustedStrokeOpacityMaxReached = true;}
			}
			else if (!adjustedStrokeOpacityMinReached) 
			{
				adjustedStrokeOpacity = adjustedStrokeOpacity-opacityInterval;
				if (adjustedStrokeOpacity <=defaultStrokeOpacity){adjustedStrokeOpacityMinReached = true;}
			}

			for (Blip blip:blipListForLayer)
			{
				if (blipFeatureMap.get(blip.getId()) != null)
				{
					blipFeatureMap.get(blip.getId()).getStyle().setStrokeOpacity(adjustedStrokeOpacity);
					activeBlipsVectorLayer.drawFeature(blipFeatureMap.get(blip.getId()));
				}
			}
		}
	}

}
