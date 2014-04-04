package com.blipnip.app.client.mainapp.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureUnselectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.geometry.Polygon;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.Style;

import com.blipnip.app.client.mainapp.presenter.MainAppPresenter.MainAppDisplay;
import com.blipnip.app.client.mainapp.service.MainAppServiceAsync;
import com.blipnip.app.client.mainapp.utils.StrokeAnimation;
import com.blipnip.app.client.mainapp.view.BlipDetailsView;
import com.blipnip.app.shared.Blip;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Periodically Running Logic
 *  In order to keep a user interface up to date, you sometimes want to perform an update periodically. 
 *  You might want to run a poll to the server to check for new data, or update some sort of animation on the screen. 
 *  In this case, use the Timer class scheduleRepeating() method
 *  
 *  i.e. 
 *  // Create a new Timer
 *   liveBlipHandler = new Timer ();
 * 
 *  // Schedule the Timer for every 1/2 second (500 milliseconds)
 *  liveBlipHandler.scheduleRepeating(500);
 *  
 * @author Thanos
 *
 */
public class LiveBlipHandler extends Timer
{
	
	@SuppressWarnings("unused")
	private static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
	@SuppressWarnings("unused")
	private static final Projection OSM = new Projection("EPSG:900913");
	
	private Integer vectorFeatureIndex;
	private Vector activeBlipsVectorLayer;
	private SelectFeature clickSelectFeatureControl;
	private MainAppDisplay mainAppDisplay;
	private MainAppServiceAsync rpcMainAppService;
	private ArrayList<Blip> currentBlipList;
	private Map<Long, VectorFeature> blipFeatureMap;
	
	private static long mainAnimationDuration = 1000*60*7;
	private static Timer singleStrokeTimer    = null;
	private static Timer strokeTimer             = null;
	private static Timer orbitTimer                = null;
	private static Timer orbitTimerSingleBlip = null;
	private long startTime = -1;
	
	private static final double defaultStrokeOpacity = 0.1;
	
	public LiveBlipHandler(MainAppDisplay mainAppDisplay, Vector activeBlipsVectorLayer, MainAppServiceAsync rpcMainAppService)
	{
		
		this.vectorFeatureIndex = null;
		
		this.activeBlipsVectorLayer = activeBlipsVectorLayer;
		this.mainAppDisplay = mainAppDisplay;
		this.rpcMainAppService = rpcMainAppService;
		this.clickSelectFeatureControl = new SelectFeature(activeBlipsVectorLayer);
		this.clickSelectFeatureControl.setAutoActivate(true);
		this.clickSelectFeatureControl.setClickOut(true);        //Do deselect on click out
		this.clickSelectFeatureControl.setToggle(false);         //Do not toggle when reclicked
		this.clickSelectFeatureControl.setMultiple(false);       //Do not select multiple when clicked normally
		
		// Add the control to the map - otherwise "clicks on features" wont be captured
		this.mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(this.clickSelectFeatureControl);
		//this.clickSelectFeatureControl.activate();
		
		this.startTime = System.currentTimeMillis();
		this.currentBlipList = null;
		this.blipFeatureMap = new HashMap<Long, VectorFeature>();
		
		lowerUserActionBarLogic();
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
		double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
		com.google.gwt.i18n.client.NumberFormat n = NumberFormat.getFormat("#,##0.000");    
		System.out.println("@LiveBlipHandler, @showElapsed, Elapsed: "+n.format(elapsedTime));	
		
		// Retrieve blips
		retrieveBlips();
	}
	
	/**
	 * Retrieving all Blips from the Datastore.
	 * 
	 */
	private void retrieveBlips()
	{
		rpcMainAppService.retrieveAllBlips(new AsyncCallback<ArrayList<Blip>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				System.out.println("@LiveBlipHandler, @retrieveAllStaticBlips, @retrieveAllBlips, onFailure");
			}

			@Override
			public void onSuccess(ArrayList<Blip> latestBlipList)
			{
				System.out.println("@LiveBlipHandler, @retrieveAllStaticBlips, @retrieveAllBlips, onSuccess");
								
				if (currentBlipList == null || currentBlipList.isEmpty())
				{
					// Entering this on the very first time this class loads
					drawBlipsOnMap(latestBlipList);
					
					// Update current list - the latestBlipList, will never be null - though it might be empty.
					currentBlipList = latestBlipList;
					
					// Update on map
					updateBlipsOnMap(currentBlipList);
				}
				else
				{
					// Entering on subsequent times (i.e. refreshing) 
					ArrayList<Blip> newBlips         = getNewblips(latestBlipList, currentBlipList);
					ArrayList<Blip> recurringBlips = getRecurringblips(latestBlipList, currentBlipList);
					
					if (!newBlips.isEmpty())
					{
						// Create the new blips on the layer
						drawBlipsOnMap(newBlips); 
						
						// Create a list of all blips that are now on the map
						ArrayList<Blip> availableBlips = new ArrayList<Blip>();
						availableBlips.addAll(recurringBlips);
						availableBlips.addAll(newBlips);
						
						// Update current list with all available blips (new and recurring)
						currentBlipList = availableBlips;
						
						// Update on map
						updateBlipsOnMap(currentBlipList);	
					}
					else
					{
						// Just update current list, if no new blips are here
						currentBlipList = recurringBlips;
						
						// Update on map
						updateBlipsOnMap(currentBlipList);	
					}			
				}
				
				// Perform maintenance on map blipFeatureMap - i.e. clean stale blips
				cleanUpBlipFeatureMap(currentBlipList);
			}
		});	
	}
	
	/**
	 * For all features on map, check the java map that keeps the feature-to-blip
	 * correspondence. If the blip is not found (using the currentBlipList), then 
	 * remove entry from map as well as feature. 
	 * 
	 * @param aBlipList
	 */
	private void cleanUpBlipFeatureMap(ArrayList<Blip> aBlipList)
	{
		//for each vector feature currently on the map
		if (activeBlipsVectorLayer.getFeatures() != null && activeBlipsVectorLayer.getFeatures().length >0)
		{
			for (VectorFeature vectorFeature:activeBlipsVectorLayer.getFeatures())
			{
				Blip mapBlip = findBlipFromFeature(vectorFeature);
				
				if (mapBlip == null)
				{
					// Remove blip entry
					 Long keyToRemove = null;
					 for (Map.Entry<Long, VectorFeature> entry:this.blipFeatureMap.entrySet())
					 {
						 //System.out.println("@LiveBlipHandler, @cleanUpBlipFeatureMap, entry value: "+entry.getValue().getFeatureId());
						 if (entry.getValue().getFeatureId().equals(vectorFeature.getFeatureId()))
						 {		 
							 keyToRemove = entry.getKey();
						 }
					 }
					 if (keyToRemove != null)
					 {
						 //System.out.println("@LiveBlipHandler, @cleanUpBlipFeatureMap, Key removing: "+keyToRemove);
						 // Remove key from java map
						 this.blipFeatureMap.remove(keyToRemove);
					 }
					  // and remove feature vector from ol map
					  activeBlipsVectorLayer.removeFeature(vectorFeature);
					  //System.out.println("@LiveBlipHandler, @cleanUpBlipFeatureMap, removeFeature: "+vectorFeature.getFeatureId());
				}				 
			}
		}
	}
	
	/**
	 * Given a vector feature this method checks in the map
	 * holding feature-blip Ids correspondence and if found
	 * returns the assosiated blip.
	 * 
	 * @param vectorFeature
	 * @return
	 */
	private Blip findBlipFromFeature(VectorFeature vectorFeature)
	{
		Blip foundBlip = null;
		if (this.blipFeatureMap!=null && !this.blipFeatureMap.isEmpty())
		{
			for (Map.Entry<Long, VectorFeature> entry:this.blipFeatureMap.entrySet())
			{
				if (entry.getValue().getFeatureId().equals(vectorFeature.getFeatureId()))
				{
					//Having found the id of the feature, we now iterate the avaiable
					//blips till we find the blip id.
					for (Blip blip:currentBlipList)
					{
						if (blip.getId().equals(entry.getKey()))
						{
							foundBlip = blip;
						}
					}
				}
			}
		}
		return foundBlip;
	}
	
	/**
	 * Creates a simpler array containing only Ids
	 * 
	 * @param blipList
	 * @return
	 */
	private ArrayList<Long> createBlipIdArray(ArrayList<Blip> blipList)
	{
 		ArrayList<Long> blipIdArray = new ArrayList<Long>();
		
		for (Blip blip:blipList)
		{
			blipIdArray.add(blip.getId());
		}
		return blipIdArray;
	}
	
	/**
	 * This method gets blips that exist both in the "fresh" list and in the
	 * previous list.
	 * 
	 * @param latestBlipList
	 * @param previousBlipList
	 * @return
	 */
	private ArrayList<Blip> getRecurringblips(ArrayList<Blip> latestBlipList, ArrayList<Blip> previousBlipList)
	{
		ArrayList<Long> latestBlipArray    = createBlipIdArray(latestBlipList);
		ArrayList<Long> previousBlipArray = createBlipIdArray(previousBlipList);
		
		/**
         * Retains only the elements in this collection that are contained in the specified collection (optional operation). 
         * In other words, removes from this collection all of its elements that are not contained in the specified collection.
         * 
         * First keep latest blips also found in previous blips i.e. recurring blips (blips that show on the map again)
         */
		ArrayList<Long> existingBlips = new ArrayList<Long>( latestBlipArray );
		existingBlips.retainAll( previousBlipArray );
		
		ArrayList<Blip> recurringBlipList = new ArrayList<Blip>();
        
        for (Long aNewBlipId:existingBlips)
        {
        		for (Blip aBlip:latestBlipList)
        		{
        			if (aBlip.getId().equals(aNewBlipId))
        			{
        				recurringBlipList.add(aBlip);
        			}
        		}
        }
		return recurringBlipList;
	}
			
	/**
	 * This method first finds all recurring blips, i.e. blips that are on the map 
	 * already and then it uses it to create and return a list that contains only
	 * the new blips that have been "freshly" retrieved, i.e. were not on the map
	 * before.
	 * 
	 * @param latestBlipList
	 * @param previousBlipList
	 * @return
	 */
	private ArrayList<Blip> getNewblips(ArrayList<Blip> latestBlipList, ArrayList<Blip> previousBlipList)
	{
		ArrayList<Long> latestBlipArray    = createBlipIdArray(latestBlipList);
		ArrayList<Long> previousBlipArray = createBlipIdArray(previousBlipList);
		
		/**
         * Retains only the elements in this collection that are contained in the specified collection (optional operation). 
         * In other words, removes from this collection all of its elements that are not contained in the specified collection.
         * 
         * First keep latest blips also found in previous blips i.e. recurring blips (blips that show on the map again)
         */
		ArrayList<Long> existingBlips = new ArrayList<Long>( latestBlipArray );
		existingBlips.retainAll( previousBlipArray );
		
        /**
         * Removes all of this collection's elements that are also contained in the specified collection (optional operation). 
         * After this call returns, this collection will contain no elements in common with the specified collection.
         * 
         * i.e. then remove recurring blips, and thus leave only brand new blips
         */
        ArrayList<Long> shinyNewBlips = new ArrayList<Long>();
		shinyNewBlips.addAll( latestBlipArray );
        shinyNewBlips.addAll( previousBlipArray );

        shinyNewBlips.removeAll( existingBlips );
        
        ArrayList<Blip> newBlipList = new ArrayList<Blip>();
        
        for (Long aNewBlipId:shinyNewBlips)
        {
        		for (Blip aBlip:latestBlipList)
        		{
        			if (aBlip.getId().equals(aNewBlipId))
        			{
        				newBlipList.add(aBlip);
        			}
        		}
        }
        return newBlipList;
	}
	
	/**
	 * 
	 * @param blipListForLayer
	 */
	private void drawBlipsOnMap(ArrayList<Blip> blipListForLayer)
	{
		//activeBlipsVectorLayer.removeAllFeatures();
		//activeBlipsVectorLayer.destroyFeatures();
		
		if (blipListForLayer != null && !blipListForLayer.isEmpty())
		{
			for (Blip blip:blipListForLayer)
			{
				//System.out.println("@LiveBlipHandler, @drawBlipsOnMap, x: "+ blip.getBlipHabitat().getX()+", y:"+blip.getBlipHabitat().getY());
				Point point = new Point(blip.getBlipHabitat().getX(),blip.getBlipHabitat().getY());
				
				VectorFeature vf = new VectorFeature(point);

				Point.narrowToPoint(vf.getGeometry().getJSObject());
				
				Style blipStyle = new Style(); 
				vf.setStyle(blipStyle);
				
				defaultStyle(blipStyle, blip);
				
				activeBlipsVectorLayer.addFeature(vf);
				
				blipFeatureMap.put(blip.getId(), vf);
			}
		}
		else
		{
			System.out.println("@LiveBlipHandler, @drawBlipsOnMap, result is empty or null");
		}
	}
	
	/**
	 * Updates the location of Blips on the map.
	 * Then, also applies a simple animation that rotates blips.
	 * 
	 * @param blipListForLayer
	 */
	private void updateBlipsOnMap(ArrayList<Blip> blipListForLayer)
	{
		if (blipListForLayer != null && !blipListForLayer.isEmpty())
		{
			// First update locations of Blips
			for (Blip blip:blipListForLayer)
			{
				VectorFeature vectorFeature = blipFeatureMap.get(blip.getId());
				
				// Update vector feature location
				vectorFeature.move(new LonLat(blip.getBlipHabitat().getX(),blip.getBlipHabitat().getY()));
				//System.out.println("@LiveBlipHandler, @updateBlipsOnMap, x: "+ blip.getBlipHabitat().getX()+", y:"+blip.getBlipHabitat().getY());
			}
			startAnimation(blipListForLayer);
		}
		else
		{
			System.out.println("@LiveBlipHandler, @updateBlipsOnMap, blipListForLayer is empty or null");
		}
	}
	
	private void startAnimation(ArrayList<Blip> blipListForLayer)
	{
		// Apply stroke animation
		if (strokeTimer != null)
		{	
			System.out.println("@LiveBlipHandler, Timer is already running, re-initializing...");
			
			// If orbitTimer was executed before, then cancel it first, then re-initialize it 
			strokeTimer.cancel();				
			strokeTimer = null;
			
			strokeAnimationBlip(2000);
			//strokeAnimationAllBlips(2000, 1000*60*5);
		}
		else
		{
			strokeAnimationBlip(2000);
			//strokeAnimationAllBlips(2000, 1000*60*5);
		}
		
		/**
			// Apply orbit animation - TODO removing for now
			if (orbitTimer != null)
			{	
				// If orbitTimer was executed before, then cancel it first, then re-initialize it 
				orbitTimer.cancel();				
				orbitTimer = null;
				
				// Apply a rotation animation that has all blips move around. TODO - Removing for now because it's too computationally intensive for many blips
			     orbitAmination(blipListForLayer, 20, 1000*3, 6); //1000(milliseconds) *60(=1 second) *1(= 1 minute)
				System.out.println("@LiveBlipHandler, Timer is already running");
			}
			else
			{
				orbitAmination(blipListForLayer, 20, 1000*3, 6);
			}
		*/
	}
	
	/**
	 * Rotate all blips around their axis - otherwise it will look as if its not rotating at all)
	 * 
	 * Method works nicely. However, don't know how it will perform when having to do this
	 * for 100-200 blips, so not using for now.
	 * 
	 * @param blipListForLayer
	 * @param repeatInterval
	 * @param animationDuration
	 * @param offset
	 */
	@SuppressWarnings("unused")
	private void orbitAmination(ArrayList<Blip> blipListForLayer, int repeatInterval, long animationDuration, int offset)
	{
		final ArrayList<Blip> blipListForLayerTimer = blipListForLayer;	
		final long duration = animationDuration;
		final int originOffset = offset;
		
		// Use orbitTimer to update
		LiveBlipHandler.orbitTimer = new Timer()
		{
			final long startTime = System.currentTimeMillis();
			
			@Override
			public void run()
			{
				long currentTime = System.currentTimeMillis();
				
				if (currentTime - startTime > duration)
				{
					
					System.out.println("@LiveBlipHandler,  @orbitAmination, Cancelling orbitTimer...");
					orbitTimer.cancel();
					orbitTimer = null;
				}
				else
				{
					Point origin = null;
					VectorFeature vectorFeature = null;
					Point point = null;
					int count = 0;
					for (Blip blip:blipListForLayerTimer)
					{
						// create a small offset, otherwise the origin will coinside with current location
						 origin = new Point(blip.getBlipHabitat().getX()+originOffset, blip.getBlipHabitat().getY()+originOffset);
						 
						// get point of vector
						 vectorFeature = blipFeatureMap.get(blip.getId());
						 point = Point.narrowToPoint(vectorFeature.getGeometry().getJSObject());//we need a Point object because this has the rotate method
						
						 // If blip "0" moves clockwise, blip "1" will move anticlockwise, blip "2" clockwise, blip "3" anticlockwise and so on.
						 // Doing it likes this gives a much nicer end-user experience.
						 if ( (count % 2) == 0)
						 {
						     //isEven = true
						    	// use rotate method
							point.rotate(-360 / 50, origin);
						 }
						 else
						 {
						     //isEven = false
						    	// use rotate method
						    point.rotate(360 / 50, origin);
						 }
						//draw it on map
						 activeBlipsVectorLayer.drawFeature(vectorFeature);	
						 count++;
					}
					//re-draw it on map
					//activeBlipsVectorLayer.redraw();
				}
			}
		};
	    orbitTimer.scheduleRepeating(repeatInterval);	
	}
	
	/**
	 * See strokeAnimationBlip. Not using because of poor performance when many Blips on map. 
	 * 
	 * @param repeatInterval
	 * @param animationDuration
	 */
	@SuppressWarnings("unused")
	private void strokeAnimationAllBlips(int repeatInterval, long animationDuration)
	{
		final long animationDurationTimer = animationDuration;
		
		LiveBlipHandler.strokeTimer = new Timer()
		{
			final long startTime = System.currentTimeMillis(); 
			
			@Override
			public void run()
			{
				long currentTime = System.currentTimeMillis();
				if (currentTime - startTime > animationDurationTimer)
				{
					System.out.println("@LiveBlipHandler, @strokeAmination, Cancelling strokeTimer...");
					LiveBlipHandler.strokeTimer.cancel();
					LiveBlipHandler.strokeTimer  = null;
				}
				else
				{
					if (!currentBlipList.isEmpty())
					{
						LiveBlipHandler.singleStrokeTimer = new StrokeAnimation(currentBlipList, blipFeatureMap, 2000, defaultStrokeOpacity); // total duration 2 secs
						LiveBlipHandler.singleStrokeTimer.scheduleRepeating(30); // repeat every 30 millisecs
					}
					
				}
			}
		};
		LiveBlipHandler.strokeTimer.scheduleRepeating(repeatInterval);	
	}
	
	/**
	 * This animation has two main components. The first is the high level animation timer which defines how long
	 * the animation process will be executed for (mainAnimationDuration) and how often will it be initiated (repeatInterval). 
	 * Each time the animation is re-executed, it spawns a "tiny" animation (second component) which animates a randomly 
	 * selected Blip each time. Nicely performs on approx 150 Blips.
	 * 
	 * @param repeatInterval
	 */
	private void strokeAnimationBlip(int repeatInterval)
	{
		LiveBlipHandler.strokeTimer = new Timer()
		{
			private long startTime = System.currentTimeMillis(); 
			
			@Override
			public void run()
			{
				long currentTime = System.currentTimeMillis();
				if (currentTime - startTime > mainAnimationDuration)
				{
					System.out.println("@LiveBlipHandler, @strokeAmination, Cancelling strokeTimer...");
					LiveBlipHandler.strokeTimer.cancel();
					LiveBlipHandler.strokeTimer  = null;
				}
				else
				{
					if (!currentBlipList.isEmpty())
					{
						Blip blip = currentBlipList.get(new Random().nextInt(currentBlipList.size()));
						VectorFeature vectorFeature = blipFeatureMap.get(blip.getId());
						LiveBlipHandler.singleStrokeTimer = new StrokeAnimation(vectorFeature, activeBlipsVectorLayer, 1000, defaultStrokeOpacity); // total duration 2 secs
						LiveBlipHandler.singleStrokeTimer.scheduleRepeating(30); // repeat every 30 millisecs
					}
				}
			}
		};
		LiveBlipHandler.strokeTimer.scheduleRepeating(repeatInterval);	
	}
	
	/**
	 * TODO - Built to use on feature select - same performance with overloaded method that
	 * picks a random feature.
	 * 
	 * @param repeatInterval
	 * @param vectorFeature
	 */
	@SuppressWarnings("unused")
	private void strokeAnimationBlip(int repeatInterval, final VectorFeature vectorFeature)
	{
		if (LiveBlipHandler.strokeTimer == null)
		{
			LiveBlipHandler.strokeTimer = new Timer()
			{
				private long startTime = System.currentTimeMillis(); 
				
				@Override
				public void run()
				{
					long currentTime = System.currentTimeMillis();
					if (currentTime - startTime > mainAnimationDuration)
					{
						System.out.println("@LiveBlipHandler, @strokeAmination, Cancelling strokeTimer...");
						LiveBlipHandler.strokeTimer.cancel();
						LiveBlipHandler.strokeTimer  = null;
					}
					else
					{
						if (!currentBlipList.isEmpty())
						{
							LiveBlipHandler.singleStrokeTimer = new StrokeAnimation(vectorFeature, activeBlipsVectorLayer, 1000, defaultStrokeOpacity); // total duration 2 secs
							LiveBlipHandler.singleStrokeTimer.scheduleRepeating(30); // repeat every 30 millisecs
						}
					}
				}
			};
			LiveBlipHandler.strokeTimer.scheduleRepeating(repeatInterval);	
		}
		else
		{
			// Cancel, nullify, and re-run.
			LiveBlipHandler.strokeTimer.cancel();
			LiveBlipHandler.strokeTimer = null;
			
			LiveBlipHandler.strokeTimer = new Timer()
			{
				private long startTime = System.currentTimeMillis(); 
				
				@Override
				public void run()
				{
					long currentTime = System.currentTimeMillis();
					if (currentTime - startTime > mainAnimationDuration)
					{
						System.out.println("@LiveBlipHandler, @strokeAmination, Cancelling strokeTimer...");
						LiveBlipHandler.strokeTimer.cancel();
						LiveBlipHandler.strokeTimer  = null;
					}
					else
					{
						if (!currentBlipList.isEmpty())
						{
							LiveBlipHandler.singleStrokeTimer = new StrokeAnimation(vectorFeature, activeBlipsVectorLayer, 1000, defaultStrokeOpacity); // total duration 2 secs
							LiveBlipHandler.singleStrokeTimer.scheduleRepeating(30); // repeat every 30 millisecs
						}
					}
				}
			};
			LiveBlipHandler.strokeTimer.scheduleRepeating(repeatInterval);
		}

	}	
	
	/**
	 * Rotate a blip around its axis (with an offset - otherwise it will look as if its not rotating at all)
	 * 
	 * @param blipListForLayer
	 * @param repeatInterval
	 * @param animationDuration
	 * @param offset
	 */
	@SuppressWarnings("unused")
	private void orbitAmination( Blip ablip, int repeatInterval, long animationDuration, int offset)
	{
		final Blip blip = ablip;	
		final long duration = animationDuration;
		final int originOffset = offset;
		
		// Use orbitTimer to update
		LiveBlipHandler.orbitTimerSingleBlip = new Timer()
		{
			final long startTime = System.currentTimeMillis();
			
			@Override
			public void run()
			{
				long currentTime = System.currentTimeMillis();
				
				if (currentTime - startTime > duration)
				{
					
					System.out.println("@LiveBlipHandler,  @orbitAmination, Cancelling orbitTimer...");
					LiveBlipHandler.orbitTimerSingleBlip.cancel();
					LiveBlipHandler.orbitTimerSingleBlip = null;
				}
				else
				{
					Point origin = null;
					VectorFeature vectorFeature = null;
					Point point = null;

					// create a small offset, otherwise the origin will coinside with current blip location
					origin = new Point(blip.getBlipHabitat().getX()+originOffset, blip.getBlipHabitat().getY()+originOffset);
						 
					// get point of vector
					vectorFeature = blipFeatureMap.get(blip.getId());
					point = Point.narrowToPoint(vectorFeature.getGeometry().getJSObject());//we need a Point object because this has the rotate method
						
					// use rotate method
					point.rotate(360 / 50, origin);
						 
					//draw it on map
					activeBlipsVectorLayer.drawFeature(vectorFeature);	

				}
				
			}
		};
		LiveBlipHandler.orbitTimerSingleBlip.scheduleRepeating(repeatInterval);	
	}
	
	/**
	 * 
	 * Deprecated
	 * 
	 * Method to create an orbit, by getting all the points of a polygons 
	 * circumfenrence. TODO - not efficient, using the rotate method instead
	 * 
	 * @param blipListForLayer
	 */
	@SuppressWarnings("unused")
	private void orbitAnimationUsingPolygon(ArrayList<Blip> blipListForLayer)
	{
		/**
		 * Testing animaton using polygon - not efficient
		 **/
		final ArrayList<Blip> blipListForLayerTimer = blipListForLayer;
		
		// Use orbitTimer to update
		Timer myTimer = new Timer()
		{
			private int orbitLocation = 0;
			@Override
			public void run()
			{
				for (Blip blip:blipListForLayerTimer)
				{
					//LonLat lonlat = new LonLat(blip.getBlipHabitat().getX(), blip.getBlipHabitat().getY());
					//System.out.println(lonlat.lat());
					//lonlat.transform(OSM.getProjectionCode(), DEFAULT_PROJECTION.getProjectionCode());
					//System.out.println(lonlat.lat());
					
					org.gwtopenmaps.openlayers.client.geometry.Polygon orbit = Polygon.createRegularPolygon(new Point(blip.getBlipHabitat().getX(), blip.getBlipHabitat().getY()), 4, 5, 0);
					if (orbitLocation <= orbit.getVertices(false).length-1)
					{
						blipFeatureMap.get(blip.getId()).move(new LonLat(orbit.getVertices(false)[orbitLocation].getX(), orbit.getVertices(false)[orbitLocation].getY()    )  );
						
					}
					else
					{	
						orbitLocation = 0;
						super.cancel();
					}
					orbit.destroy();
				}
				orbitLocation++;
			}
		};
	    myTimer.scheduleRepeating(20);
	}
	
	/**
	 * Method to update the image and label on the lower menu bar
	 * @param vectorFeature
	 */
	private void setBlipBar(VectorFeature vectorFeature)
	{
		Blip blip = findBlipFromFeature(vectorFeature);
		
		if (blip != null && blip.getBlipContent() != null)
		{
			//vectorFeature.getStyle().setPointRadius(50);
			mainAppDisplay.setBlipOverview("f."+blip.getBlipMasterUsername()+" - "+blip.getBlipContent().getTitle());
			mainAppDisplay.setLiveBlipUserImageURL(blip.getBlipUserPictureURL());
		}		
	}
	
	/**
	 *  Defines functionality for user actions on lower bar
	 */
	private void lowerUserActionBarLogic()
	{
		
		mainAppDisplay.getButtonNext().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonNext");
				
				VectorFeature[] vectorFeatureArray = activeBlipsVectorLayer.getFeatures();
				if (vectorFeatureArray != null)
				{
					if (vectorFeatureIndex == null)
					{
						if (activeBlipsVectorLayer.getFeatures() != null && activeBlipsVectorLayer.getFeatures().length>0)
						{
							vectorFeatureIndex = new Integer(0);
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonNext, index:"+vectorFeatureIndex+", feature: "+vectorFeatureArray[vectorFeatureIndex].getFeatureId());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureIndex]);
							
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureIndex].getCenterLonLat());
						}
						else
						{
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonNext, No vectors found...");
						}
					}
					else
					{
						if (vectorFeatureIndex < activeBlipsVectorLayer.getFeatures().length-1)
						{
							vectorFeatureIndex++;
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonNext, index:"+vectorFeatureIndex+", feature: "+vectorFeatureArray[vectorFeatureIndex].getFeatureId());
							
							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureIndex]);
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureIndex].getCenterLonLat());
						}
						else //cycle and start from 0
						{
							vectorFeatureIndex = 0;
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonNext, index:"+vectorFeatureIndex+", feature: "+vectorFeatureArray[vectorFeatureIndex].getFeatureId());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureIndex]);
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureIndex].getCenterLonLat());
						}
					}
				}
				else
				{
					vectorFeatureIndex = null;
				}
			}
		});
		
		mainAppDisplay.getButtonPrevious().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonPrevious");

				VectorFeature[] vectorFeatureArray = activeBlipsVectorLayer.getFeatures();

				if (vectorFeatureArray != null)
				{
					if (vectorFeatureIndex == null)
					{
						if (activeBlipsVectorLayer.getFeatures() != null && activeBlipsVectorLayer.getFeatures().length>0)
						{
							vectorFeatureIndex = new Integer(0);
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonPrevious, index:"+vectorFeatureIndex+", feature: "+vectorFeatureArray[vectorFeatureIndex].getFeatureId());
							//LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							//transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							//System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());
							
							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureIndex]);
							
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureIndex].getCenterLonLat());
						}
						else
						{
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonPrevious, No vectors found...");
						}
					}
					else
					{			
						if (vectorFeatureIndex > 0)
						{
							vectorFeatureIndex--;
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonPrevious, index:"+vectorFeatureIndex+", feature: "+vectorFeatureArray[vectorFeatureIndex].getFeatureId());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureIndex]);
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureIndex].getCenterLonLat());
						}
						else
						{
							vectorFeatureIndex = activeBlipsVectorLayer.getFeatures().length-1;
							System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonPrevious, index:"+vectorFeatureIndex+", feature: "+vectorFeatureArray[vectorFeatureIndex].getFeatureId());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureIndex]);
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureIndex].getCenterLonLat());
						}
					}
				}
				else
				{
					vectorFeatureIndex = null;
				}	
			}
		});
		
		mainAppDisplay.getButtonView().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@LiveBlipHandler, @lowerUserActionBarLogic, @getButtonView");
				
				VectorFeature[] vectorFeature  = activeBlipsVectorLayer.getSelectedFeatures();
				
				if (vectorFeature != null && vectorFeature.length ==1)
				{
					Blip blip = findBlipFromFeature(vectorFeature[0]);
					
					if (blip != null)
					{
						new BlipDetailsView(mainAppDisplay, blip);
						
					}	
				}
			}
		});
		
		mainAppDisplay.getButtonRefresh().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				// Retrieve blips
				retrieveBlips();
			}
		});
		
		activeBlipsVectorLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() //can also use for updating label that shows the number of selected features
        {
            public void onFeatureSelected(FeatureSelectedEvent eventObject) 
            {            		
                if (activeBlipsVectorLayer.getSelectedFeatures() != null)
                {
                		setBlipBar(eventObject.getVectorFeature());
                		                		
                		highlightedStyle(eventObject.getVectorFeature().getStyle(), null);
                  	
                		//strokeAnimationBlip(2000,eventObject.getVectorFeature());

                		activeBlipsVectorLayer.drawFeature(eventObject.getVectorFeature());
                		System.out.println("@MainAppPreseter, @VectorFeatureSelectedListener, getSelectedFeature= "+eventObject.getVectorFeature().getFeatureId()+", features length: "+activeBlipsVectorLayer.getSelectedFeatures().length);
                }
                	
            }
        });
		activeBlipsVectorLayer.addVectorFeatureUnselectedListener(new VectorFeatureUnselectedListener() 
        {
            public void onFeatureUnselected(FeatureUnselectedEvent eventObject) 
            {
            		defaultStyle(eventObject.getVectorFeature().getStyle(), null);
            		
            		activeBlipsVectorLayer.drawFeature(eventObject.getVectorFeature());
                System.out.println("@MainAppPreseter, @VectorFeatureUnselectedListener, UnSelected: "+eventObject.getVectorFeature().getFeatureId());
            }
        });
		
	}
	
	/**
	 * The default Blip style
	 * 
	 * @param style
	 * @param blip
	 */
	private void defaultStyle(Style style, Blip blip)
	{
		style.setGraphicName("circle");
		style.setFillColor("#6E6E6E");
		style.setPointRadius(15);
		style.setBackgroundGraphicZIndex(11);
		style.setFillOpacity(0.3);
		
		if (blip != null)
		{
			style.setBackgroundHeight(20);
			
			// In case something weird happened and content did not get stored
			if (blip.getBlipContent() != null && blip.getBlipContent().getImageUrl() != null)
			{
				if (blip.getBlipContent().getImageUrl() != null)
				style.setBackgroundGraphic(blip.getBlipContent().getImageUrl()+"=s40");
			}
			else
			{
				style.setBackgroundGraphic("icons/blipnip_app_icons/mainapp/05_content_picture/drawable-hdpi/ic_action_picture.png");
			}
			style.setLabel("f."+blip.getBlipMasterUsername());
		}
		
		style.setLabelYOffset(-((int)style.getPointRadiusAsDouble()+10));
		style.setFontWeight("200");
		style.setFontSize("6pt");
		style.setFontColor("#6F6F6F");
		
		style.setBackgroundHeight(20);
		style.setGraphicZIndex(16);
		style.setGraphicOpacity(0.2);
		
		style.setStrokeWidth(5);
		style.setStrokeColor("#FF6666");//FF6666 EB6C6C
		style.setStrokeOpacity(defaultStrokeOpacity);
	}
	
	/**
	 * The highlighted blip style
	 * 
	 * @param style
	 * @param blip
	 */
	private void highlightedStyle(Style style, Blip blip)
	{
		style.setGraphicName("square");
		style.setPointRadius(25);
		style.setBackgroundGraphicZIndex(16);
		style.setFillOpacity(0.3);
      	
		style.setLabelYOffset(-((int)style.getPointRadiusAsDouble()+10));
		style.setFontWeight("900");
		style.setFontSize("large");
		style.setFontColor("#66B2FF"); //66B2FF 5C83EF
		
		
		style.setBackgroundHeight(40);
		style.setGraphicZIndex(20);
		style.setGraphicOpacity(0.8);

		style.setStrokeWidth(5);
		style.setStrokeColor("#66B2FF");
		style.setStrokeOpacity(1);
	}

}
