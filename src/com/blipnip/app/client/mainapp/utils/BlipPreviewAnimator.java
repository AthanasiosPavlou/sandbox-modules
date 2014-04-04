package com.blipnip.app.client.mainapp.utils;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;

import com.google.gwt.user.client.Timer;

/**
 * 
 * This is a simple class that animates the blip on the defined path.
 * It is doing this by moving the "blip" feature one x,y point at a time
 * as defined in "path points" array.
 * 
 * @author Thanos
 *
 */
public class BlipPreviewAnimator extends Timer
{
	@SuppressWarnings("unused")
	private long startTime = -1;
	private VectorFeature blipToAnimate = null;
	private ArrayList<Point> pathPoints = null;
	private static int index = 0;

	public BlipPreviewAnimator (VectorFeature blipToAnimate, ArrayList<Point> pathPoints)
	{
		this.blipToAnimate = blipToAnimate;
		this.pathPoints = pathPoints;
		BlipPreviewAnimator.index = 0;
		
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void run()
	{
			showElapsed();
	}

	/**
	 * Show the current elapsed time in the elapsedLabel widget.
	 */
	private void showElapsed ()
	{
		//double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
		//com.google.gwt.i18n.client.NumberFormat n = NumberFormat.getFormat("#,##0.000");    
		//System.out.println("Elapsed: " + n.format(elapsedTime));
		
		if (pathPoints != null && !pathPoints.isEmpty())
		{
			if (index <= pathPoints.size()-1)
			{
				//System.out.println("index:"+index+"pathPoints size"+pathPoints.size());
				Point currentPoint = this.pathPoints.get(BlipPreviewAnimator.index);
				blipToAnimate.move(new LonLat(currentPoint.getX(), currentPoint.getY()));
				BlipPreviewAnimator.index++;
			}
			else
			{
				super.cancel();
			}
		}
		else
		{
			super.cancel();
		}
		

	}

}
