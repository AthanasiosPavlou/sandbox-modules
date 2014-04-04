package com.blipnip.app.client.mainapp.openlayers;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.control.Attribution;
import org.gwtopenmaps.openlayers.client.control.AttributionOptions;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.control.TouchNavigation;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.XYZ;
import org.gwtopenmaps.openlayers.client.layer.XYZOptions;
import org.gwtopenmaps.openlayers.client.event.EventHandler;
import org.gwtopenmaps.openlayers.client.event.EventObject;
import org.gwtopenmaps.openlayers.client.event.MapZoomListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;

/**
 * Simple class to create a composite of a simple panel and a map.
 * By making the map have 100% length and width on the panel
 * we can easily stick it in any layout construct (vertical panel, deck, etc).
 * 
 * @author Thanos
 *
 */
public class MapComponent extends Composite
{

	//private final SimplePanel simplePanel;
	private final MapWidget openLayersMap;

	//Set default zoom level
	private static final int ZOOM_LEVEL = 17; 
	
	public MapWidget getOpenLayersMap()
	{
		return openLayersMap;
	}
	
	public MapComponent()
	{
		this.openLayersMap = new MapWidget("100%","100%", initDefaultMapOptions());
		this.openLayersMap.setStylePrimaryName("blipnip-gwt-Menu-Map");
		addDefaultLayers();
		addDefaultControls();
		addDefaultHandlers();
		addDefaultMapCentre();
		this.initWidget(this.openLayersMap);
	}
	
	/**
	 * These are the initial map options
	 * 
	 * @return
	 */
	private MapOptions initDefaultMapOptions()
	{
		MapOptions options = new MapOptions();
		options.removeDefaultControls();
		options.setNumZoomLevels(16);
		options.setDisplayProjection(new Projection("EPSG:4326"));
		return options;
	}
	
	/**
	 * Adding controls and event handlers.  For a list of all events navigate to:
	 * http://dev.openlayers.org/docs/files/OpenLayers/Map-js.html#OpenLayers.Map.events
	 */
	public void addDefaultControls()
	{

		/**
			We define the navigation control, but we disable zoomwheel, so user cannot zoom in or out, but can still drag the map.
		    http://dev.openlayers.org/docs/files/OpenLayers/Control/Navigation-js.html#OpenLayers.Control.Navigation  
		    other example controls to add:
		    this.openLayersMap.getMap().addControl(new LayerSwitcher());
		    this.openLayersMap.getMap().addControl(new MousePosition());
		*/
		
		TouchNavigation touchNav = new TouchNavigation();
		this.openLayersMap.getMap().addControl(touchNav);
		
		AttributionOptions attrOptions = new AttributionOptions();
		Attribution attr = new Attribution(attrOptions);
		this.openLayersMap.getMap().addControl(attr);
		
		Navigation nav = new Navigation();
		nav.getJSObject().setProperty("zoomWheelEnabled", false);
		this.openLayersMap.getMap().addControl(nav);
		
		

	}
	
	/**
	 * Adding the default handlers. These are added here as the handlers that need to 
	 * exist independently of any other application logic.
	 * 
	 */
	private void addDefaultHandlers()
	{
				this.openLayersMap.getMap().addMapZoomListener(new MapZoomListener()
		{
			@Override
			public void onMapZoom(MapZoomEvent eventObject)
			{
				//TODO - Do nothing for now, leaving in for testing.
			}
		});
		
		/**
		    This event register is neeeded, because in case the user resizes the browser window, openlayer for some reason
			zooms in. So when it does that, we get back to the default zoom by using the zoomTo method.
		 */
		this.openLayersMap.getMap().getEvents().register("zoomend", this.openLayersMap.getMap(), new EventHandler() 
		{
            @Override
            public void onHandle(EventObject eventObject)
            {
            		/**
            			Using this technique here, because, in the event that the user double clicks on the map
	            		we need to wait till this action has completed and then revert to the default map zoom.
	            		If we didn't have this, the "revert" would not be successful.
	            		http://dantwining.co.uk/2010/03/01/gwt-openlayers-and-full-screen-maps/
            		 */
  				  Scheduler.get().scheduleDeferred(new ScheduledCommand() 
  			      {
  				        	@Override
  				        	public void execute()
  				        	{
  				        		openLayersMap.getMap().zoomTo(ZOOM_LEVEL);
  			    				System.out.println("@MapComponent, @zoomend, Reverting to Map Zoom: "+openLayersMap.getMap().getZoom());
  				        	}
  			      });
            }
        });
		
		/**
		 * This event register is needed because, on mobile devices (tested on ipad2 and ipad mini), after the update size
		 * triggered when the screen went from landscape to portait (in MainAppView) the upper tiles were white. In other
		 * words, the transition did not update the map-tiles-in-view to the new map size. So, by doing this, the map zooms in 
		 * when the update size is triggered, which "enforces" the updating of the map tiles. Also, because of the "zoomend"
		 * event the map will revert to the default zoom level after this.
		 */
		this.openLayersMap.getMap().getEvents().register("updatesize", this.openLayersMap.getMap(), new EventHandler() 
		{
            @Override
            public void onHandle(EventObject eventObject)
            {
  				  Scheduler.get().scheduleDeferred(new ScheduledCommand() 
  			      {
  				        	@Override
  				        	public void execute()
  				        	{
  				        		openLayersMap.getMap().zoomTo(ZOOM_LEVEL+1);
  			    				System.out.println("@MapComponent, @updatesize, Re-zooming after size update: "+openLayersMap.getMap().getZoom());
  				        	}
  			      });
            }
        });
		
		/**
		The following is useful if we want to convert map coordinates to pixel x,y coordinates.
		So, if we want to draw on top of a map layer, this seems to be a good way to do it.
		http://stackoverflow.com/questions/6032460/mousemove-events-in-gwt-openlayers 
		    
		this.openLayersMap.getMap().getEvents().register("mousemove", this.openLayersMap.getMap(), new EventHandler() 
		{
            @Override
            public void onHandle(EventObject eventObject)
            {
                 JSObject xy = eventObject.getJSObject().getProperty("xy");
                 Pixel px = Pixel.narrowToPixel(xy);
                 LonLat lonlat = openLayersMap.getMap().getLonLatFromPixel(px);
                 System.out.println("Caught mousemove event: "+lonlat);
            }
        });
		*/
	}
	
	
	/**
	 * Adding the default layers. In this case only one base layer (i.e. main layer that the user views)
	 * is enough. This can be any-of-many types including OSM (default), Google Maps, MapQuest
	 * CloudMade, MapBox etc. The best options seem to be CloudMade (500k tile loads free then charges)
	 * because the tiles can be fully customised and MapQuest cause is totally free, and looks much
	 * better than the default OSM layer. MapBox is the best looking, but with very few views 
	 * (3000 views = 15 tiles per view = 45.000 tiles as opposed to 500k tiles of MapQuest). This is because
	 * MapBox tiles, go together with usage of their API (i.e. draw stuff on map), whereas CloudMade charges
	 * purely for map tiles loading - which is what we want cause we use openlayers as our "drawing api"
	 * which is free. 
	 * 
	 * Note: All layers apart from Google, use OSM for map data (i.e. streets, buildings etc). After testing,
	 * OSM loads fine on most mobile devices, however when tested on xperia mini, the map was not
	 * displayed correctly. When using Google Maps however, it worked fine. But, Google Maps, cannot be 
	 * fully customizable (such as CloudMade or MapBox) is limited to 25.000 map loads after which need
	 * to pay and finally, map belongs to Google, so at any point they can put ads, or whatever else they
	 * like which can mess with the user experience.
	 */
	public void addDefaultLayers()
	{
		/**
			// Default OSM layers
			OSM mapnikOSM = OSM.Mapnik("Mapnik");
			OSM cycleOSM  = OSM.CycleMap("CycleMap");
			mapnikOSM.setIsBaseLayer(true);
			cycleOSM.setIsBaseLayer(true);
			this.openLayersMap.getMap().addLayer(mapnikOSM);
		    this.openLayersMap.getMap().addLayer(cycleOSM);
		*/
		
		/**
		    // Google Maps Tiles
			// Also in Blipnip.html insert: <script src="http://maps.google.com/maps/api/js?v=3&sensor=false"></script>
			// or <script src="http://maps.google.com/maps/api/js?&sensor=false"></script>
			// Keep in mind: http://gis.stackexchange.com/questions/51992/how-to-obtain-to-max-zoom-levels-for-google-layer-using-open-layers
			// and http://gis.stackexchange.com/questions/5964/how-to-apply-custom-google-map-style-in-openlayers
			GoogleV3Options gNormalOptions = new GoogleV3Options();
			gNormalOptions.setIsBaseLayer(true);
			gNormalOptions.setSmoothDragPan(false);
			gNormalOptions.setNumZoomLevels(17);
			gNormalOptions.setType(GoogleV3MapType.G_NORMAL_MAP);
			GoogleV3 gNormal = new GoogleV3("Google Normal", gNormalOptions);
	        this.openLayersMap.getMap().addLayer(gNormal);
        */
		
		/** 
		   // MapQuest - MapQuest-OSM Tiles - These are also free - Very good free alternative
	        XYZOptions mapQuestOption = new XYZOptions();
	        mapQuestOption.setIsBaseLayer(true);
	        mapQuestOption.setNumZoomLevels(17);
	        mapQuestOption.setSphericalMercator(true);
	        String[] tiles = new String[4];
	        tiles[0] = "http://otile1.mqcdn.com/tiles/1.0.0/map/${z}/${x}/${y}.png";
	        tiles[1] = "http://otile2.mqcdn.com/tiles/1.0.0/map/${z}/${x}/${y}.png";
	        tiles[2] = "http://otile3.mqcdn.com/tiles/1.0.0/map/${z}/${x}/${y}.png";
	        tiles[3] = "http://otile4.mqcdn.com/tiles/1.0.0/map/${z}/${x}/${y}.png";
	        XYZ mapQuest = new XYZ("mapQuest",tiles, mapQuestOption);
	        this.openLayersMap.getMap().addLayer(mapQuest);
        */
		
		/**
            // Openlayers, OpenStreetMaps blank and white - Free but do not zoom close enough
	        XYZOptions bwOSMOption = new XYZOptions();
	        bwOSMOption.setIsBaseLayer(true);
	        bwOSMOption.setSphericalMercator(true);
	        bwOSMOption.setNumZoomLevels(15);
	        String[] bwOSMtiles = new String[2];
	        bwOSMtiles[0] = "http://a.www.toolserver.org/tiles/bw-mapnik/${z}/${x}/${y}.png"; 
	        bwOSMtiles[1] = "http://b.www.toolserver.org/tiles/bw-mapnik/${z}/${x}/${y}.png";
	        XYZ bwOSM = new XYZ("bwOSM",bwOSMtiles, bwOSMOption);
	        this.openLayersMap.getMap().addLayer(bwOSM); 
         */
		
		/**
			// Unfortunately their policy has changed and will start charging from May 1st
            // CloudMade Tiles - Very good paid alternative, free 500,000 tile loads
			//String osmAtrribution = "&copy; 2014 <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors";
			//String cloudMadeAttribution = "Map data " + osmAtrribution + ", &copy; 2014 <a href=\"http://cloudmade.com\">CloudMade</a>";
	        XYZOptions cloudMadeOption = new XYZOptions();
	        cloudMadeOption.setIsBaseLayer(true);
	        cloudMadeOption.setSphericalMercator(true);
	        cloudMadeOption.setNumZoomLevels(19);
	        cloudMadeOption.setTransitionEffect(TransitionEffect.RESIZE);
	        //cloudMadeOption.setAttribution(cloudMadeAttribution);
	        String[] cloudMadetiles = new String[3];
	        cloudMadetiles[0] = "http://a.tile.cloudmade.com/3295ec94195346989f8d67502c88e0ab/120472/256/${z}/${x}/${y}.png"; //TODO - 256 for desktops, 64 for mobiles
	        cloudMadetiles[1] = "http://b.tile.cloudmade.com/3295ec94195346989f8d67502c88e0ab/120472/256/${z}/${x}/${y}.png"; 
	        cloudMadetiles[2] = "http://c.tile.cloudmade.com/3295ec94195346989f8d67502c88e0ab/120472/256/${z}/${x}/${y}.png"; 
	        XYZ cloudMade = new XYZ("cloudMade",cloudMadetiles, cloudMadeOption);
	        this.openLayersMap.getMap().addLayer(cloudMade); 
	       */
		
	        // MapBox is free for 3000 map loads - paid for more. Switching to this due to CloudMade's upcoming change of policy.
			// Nice and fairly cheap alternative. TileMill seems to be an awesome tool for complete map re-styling which
			// is a huge +. 
	        XYZOptions mapBoxOption = new XYZOptions();
	        mapBoxOption.setIsBaseLayer(true);
	        mapBoxOption.setSphericalMercator(true);
	        mapBoxOption.setNumZoomLevels(19);
	        mapBoxOption.setTransitionEffect(TransitionEffect.RESIZE);
	        //mapBoxOption.setAttribution(cloudMadeAttribution);
	        String[] mapBoxtiles = new String[3];
	        mapBoxtiles[0] = "http://a.tiles.mapbox.com/v3/thanos.hh91dmii/${z}/${x}/${y}.png"; 
	        mapBoxtiles[1] = "http://b.tiles.mapbox.com/v3/thanos.hh91dmii/${z}/${x}/${y}.png"; 
	        mapBoxtiles[2] = "http://c.tiles.mapbox.com/v3/thanos.hh91dmii/${z}/${x}/${y}.png"; 
	        XYZ mapBox = new XYZ("mapBox",mapBoxtiles, mapBoxOption);
	        this.openLayersMap.getMap().addLayer(mapBox); 
	        
	        
	}
	
	public void addDefaultMapCentre()
	{
		LonLat coords = MapComponent.getTransformedMapCoords(23.72616, 37.97706); // Preset to point to Monastiraki square
		this.openLayersMap.getMap().setCenter(coords, ZOOM_LEVEL);
	}
	
	/**
	 * Use this to update map options
	 * 
	 * @param mapOptions
	 */
	public void updateMapOptions(MapOptions mapOptions)
	{
		this.openLayersMap.getMap().setOptions(mapOptions);
	}
	
	/**
	 * Warning: In the case of OSM-Layers the method 'setCenter()' uses Gauss-Krueger (EPSG:900913)  coordinates, 
	 * thus we have to  transform normal latitude/longitude values into this projection before using map.setCenter
	 * Also see: http://docs.openlayers.org/library/spherical_mercator.html
	 * 
	 * @param lon
	 * @param lat
	 * @return
	 */
	public static LonLat getTransformedMapCoords(double lon, double lat)
	{
		LonLat lonLat = new LonLat(lon, lat);
		lonLat.transform("EPSG:4326", "EPSG:900913");
		return lonLat;
	}
	
	/**
	 * Making the z index high so that map comes to the front
	 * Example usage:
	 * public void onMouseOver(MouseOverEvent event)
		{
			map.bringToBack();
		}
	 * 
	 */
	public void bringSimpleMapToFront()
	{
		this.openLayersMap.setStyleName("blipnip-gwt-Menu-Map");
		//this.openLayersMap.getElement().getStyle().setZIndex(1); 
		//this.openLayersMap.getElement().getFirstChildElement().getStyle().setZIndex(1); // Setting a big number to bring to front
	}
	
	/**
	 *Lowering z index to push map back - and thus allow menu items to show in front of the map
	 */
	public void bringSimpleMapToBack()
	{
		this.openLayersMap.setStyleName("blipnip-gwt-Menu-Map-Back");
		//this.openLayersMap.getElement().getStyle().setZIndex(-1); 
		//this.openLayersMap.getElement().getFirstChildElement().getStyle().setZIndex(-1); // 
	}
}
