package com.blipnip.app.client.mainapp.presenter;


import java.util.ArrayList;
import java.util.Arrays;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.StyleMap;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.LocationUpdateEvent;
import org.gwtopenmaps.openlayers.client.event.LocationUpdateListener;
import org.gwtopenmaps.openlayers.client.event.MapClickListener;
import org.gwtopenmaps.openlayers.client.event.MapMoveListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureAddedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureUnselectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.geometry.Polygon;
import org.gwtopenmaps.openlayers.client.handler.PathHandlerOptions;
import org.gwtopenmaps.openlayers.client.handler.PointHandler;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.control.Geolocate;

import com.blipnip.app.client.Presenter;
import com.blipnip.app.client.mainapp.openlayers.MapComponent;
import com.blipnip.app.client.mainapp.service.MainAppServiceAsync;
import com.blipnip.app.client.mainapp.utils.BlipPreviewAnimator;
import com.blipnip.app.client.mainapp.utils.BlipPathBuilder;
import com.blipnip.app.client.mainapp.view.AddContentView;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipHabitat;
import com.blipnip.app.shared.BlipHabitat.BlipPoint;
import com.blipnip.app.shared.BlipMaster;
import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;



/**
 * This is the presenter, handling the app's main window, which
 * contains the map, and main controls. The two interfaces that
 * are defined, MainAppDisplay and AddBlipContentDisplay are
 * the ones that the views will implement for the display of the 
 * main window and the window that will pop up for populating
 * the blip content.
 * 
 * For displaying "live blips" i.e. blips that are in the Datastore and
 * need to be retrieved and displayed, LiveBlipHandler is used which
 * is initialized in go()->initActiveBlipsLayer() and scheduled to run
 * for a defined interval.
 * 
 * @author Thanos
 *
 */
public class MainAppPresenter implements Presenter
{

	/**
	 * This interface will be implemented by whatever AddContent 
	 * view is going to be used, i.e. the window pop up that will
	 * enable a user to populate a blip with an image.
	 * 
	 * @author Thanos
	 *
	 */
	public interface AddBlipContentDisplay
	{
		String getBlipImageURL();
		Long getContentId();
		
		void deleteContent();
		void showDialogBox(boolean show);
		void setBlipImage(String imageURL);
		
		HasClickHandlers getButtonSubmitContent();
		HasClickHandlers getButtonCancel();
		
		void setEnable(boolean enable);
	}
	
	/**
	 * This interface will be impleneted by whatever MainApp view
	 * is chosen to be used - i.e. the implementation of what the
	 * user will see.
	 * 
	 * @author Thanos
	 *
	 */
	public interface MainAppDisplay 
	{
		Widget asWidget();
		MapComponent getMapComponent();
		
		Widget getMainAppLogMenubar();
		
		HasClickHandlers getButtonCreateQuickBlip();
		Boolean getToggleButtonCreateQuickBlipIsDown();
		
		HasClickHandlers getButtonMyLocation();
		
		HasClickHandlers getLocationTextBox();
		String getlocationTextBoxText();
		
		HasFocusHandlers getFilterListBoxClickHandlers();
		HasChangeHandlers getFilterListBoxChangeHandlers();
		int getFilterListBoxSelectedItemIndex(); 
		String getFilterListBoxSelectedItem(int index); 
		
		HasClickHandlers getButtonPrevious();
		HasClickHandlers getButtonNext();
		HasClickHandlers getButtonView();
		HasClickHandlers getButtonRefresh();
		
		String getBlipOverview();
		String getliveBlipUserImageURL();

		void setUserInfoData(BlipMaster data);
		void setBlipsOnArea(ArrayList<Blip> blipList);
	
		void setToggleButtonCreateQuickBlipIsDown(boolean isDown);	
		void setToggleButtonCreateQuickBlipEnable(boolean enable);
		void setButtonMyLocationEnable(boolean enable);
		void setBlipOverview(String overviewText);
		void setLiveBlipUserImageURL(String url);
		void setFilterListBoxItemSelected(int itemSelected, boolean selected);
		
		void setLocationTextBoxText(String locationText);
		
	}
	
	
	/**
	 * Variables of general use for this presenter class
	 */
	@SuppressWarnings("unused")
	private final HandlerManager eventBus;
	private final MainAppServiceAsync rpcMainAppService;
	private final MainAppDisplay mainAppDisplay;
	private  AddBlipContentDisplay addBlipContentDisplay;
	
	/**
	 * Variables related to the map/map drawing
	 */
	private static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
	private final Vector vectorLayer = new Vector("Vector layer");
	private DrawFeature drawPointFeatureControl   = null;
	private SelectFeature clickSelectFeatureControl = null;
	private static BlipPathBuilder blipPathBuilder    = null;
	private static VectorFeature pathVectorFeature = null;
	private final Style drawStyle = new Style();           
	private final Style highlightedStyle = new Style(); 
	private Timer liveBlipHandler = null;
	private Geolocate geo  = new Geolocate();
	
	/**
	 * Variables related to Blip creation/content
	 */
	private LoginInfo loginInfo = null;
	private BlipMaster blipMaster = null;
	
	public MainAppPresenter(LoginInfo loginInfo, MainAppServiceAsync rpcService, HandlerManager eventBus, MainAppDisplay mainAppView) 
	{   
		this.loginInfo = loginInfo;
		this.rpcMainAppService = rpcService;
		this.eventBus = eventBus;
		this.mainAppDisplay = mainAppView;
		this.addBlipContentDisplay = null;
	}

	/**
	 * 
	 * This method is used for binding the presenter with the view (through the interface).
	 * Since we are using the interface to access the view, we can easily swap views leaving
	 * all other code unaffected.
	 * 
	 * Currently the logic here is to first get or set the blip master to/from the Datastore and
	 * then initialise the handlers for all the controls using the "<name>Logic()" methods.
	 * 
	 */
	public void bind() 
	{
		System.out.println("@MainAppPresenter, @bind, start");
		
		this.rpcMainAppService.initBlipMaster(loginInfo.getEmailAddress(), loginInfo.getUserName(),  loginInfo.getLoginRank(), loginInfo.getPictureUrl(), new AsyncCallback<BlipMaster>() 
		{
			@Override
			public void onFailure(Throwable caught)
			{
				blipMaster = null;
				System.out.println("@MainAppPresenter, @bind, @initBlipMaster, @onFailure, caught: "+caught.getMessage());
			}

			@Override
			public void onSuccess(BlipMaster result)
			{
			     blipMaster = result;
			     
			     /**
			      * If the Blip Master is successfully initialized (which means, either a new entry in the
			      * Datastore (if the login details do not exist), or an exisiting one retrieved if they do
			      * exist) then add all the app's logic.
			      * 
			      */
			     if (blipMaster != null)
			     {
			    	 	System.out.println("@MainAppPresenter, @bind, turning on..."); 
			    	 	
				    	 logMenuBarLogic();        		  // not curretly useful as it refers to the log panel, but leaving in for potential future use
				    	 upperBarActionLogic();		      // actions related to creating new blips, navigating on map and filtering blips (currently on upper bars) 
				    	 mapComponentLogic();         // map component logic for catching mouse events on map - is different to map logic
				    	 initActiveBlipsLayer();            // logic for lower bar - viewing/selecting blips on the map
			     }
			     else
			     {
			    	 	System.out.println("@MainAppPresenter, @bind, blipmaster is null");
			     }
			     
			     Scheduler.get().scheduleDeferred(new ScheduledCommand() 
			     {
				    	 @Override
				    	 public void execute()
				    	 {
				    		 mainAppDisplay.setUserInfoData(blipMaster);
				    	 }
			     });
			     System.out.println("@MainAppPresenter, @bind, @initBlipMaster, @onSuccess, getBlipMasterEmail: "+result.getBlipMasterEmail());
			}
		});
	}
	
	/**
	 * Actions related to creating new blips (currently on upper bars)
	 */
	private void upperBarActionLogic()
	{
		
		mainAppDisplay.getLocationTextBox().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenter, @upperBarActionLogic, @getLocationTextBox()");
				mainAppDisplay.setLocationTextBoxText("");
			}
		});
		
		
		mainAppDisplay.getFilterListBoxClickHandlers().addFocusHandler(new FocusHandler()
		{
			@Override
			public void onFocus(FocusEvent event)
			{
				System.out.println("@MainAppPresenter, @upperBarActionLogic, @getFilterListBoxFocusHandlers()"+event.toString());
				mainAppDisplay.setFilterListBoxItemSelected(0, false);
				
			}});
		
		mainAppDisplay.getFilterListBoxChangeHandlers().addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				System.out.println("@MainAppPresenter, @upperBarActionLogic, @getFilterListBoxChangeHandlers()"+event.toString());
			}
		});
		
		mainAppDisplay.getButtonMyLocation().addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenter, @upperBarActionLogic, @getButtonMyLocation()");
				
				if (mainAppDisplay.getlocationTextBoxText().equals(""))
				{
					System.out.println("@MainAppPresenter, @upperBarActionLogic, @getButtonMyLocation(), finding current location");
					
					geo.deactivate();  //TODO - easy way out by deactivating and re-activating
					geo.activate();      //http://openlayers.org/dev/examples/geolocation.html, 
											   //http://dev.openlayers.org/docs/files/OpenLayers/Control/Geolocate-js.html 
					
					mainAppDisplay.setLocationTextBoxText("search area, e.g. Piraeus, Athens, Greece");
					
				}
				else
				{
					findLocation(mainAppDisplay.getlocationTextBoxText());
				}
			}
		});
		
		// for logic related to this also check in vectorLayer.addVectorFeatureAddedListener
		mainAppDisplay.setToggleButtonCreateQuickBlipEnable(true);
		mainAppDisplay.getButtonCreateQuickBlip().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				// If create new blip button is down, activate "drawPointFeatureControl" control and deactivate other buttons 
				if (mainAppDisplay.getToggleButtonCreateQuickBlipIsDown())
				{
					System.out.println("@MainAppPresenter, @bind, @mainAppDisplay.getButtonCreateQuickBlip(), Button Down .");
					
					drawPointFeatureControl.activate();
					//clickSelectFeatureControl.deactivate();
					
				}
				else
				{
					System.out.println("@MainAppPresenter, @bind, @mainAppDisplay.getButtonCreateQuickBlip(), Button Up .");

					drawPointFeatureControl.deactivate();
					//clickSelectFeatureControl.activate();
				}
			}
		});
		
	}
	
	private void findLocation(String location)
	{
		String url = "http://open.mapquestapi.com/nominatim/v1/search?q="+location+"&format=json&polygon=0&addressdetails=1";
		// Send request to server and catch any errors.
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try 
		{
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(null, new RequestCallback() 
			{
				public void onError(Request request, Throwable exception) 
				{
					System.out.println("Couldn't retrieve JSON");
				}

				public void onResponseReceived(Request request, Response response) 
				{
					if (200 == response.getStatusCode()) 
					{
						if (!response.getText().equals("[]"))
						{
							System.out.println("Search for location successful:"+response.getText());
							LonLat  foundLocation = parse(response.getText());
							
							if (foundLocation != null)
							{
								mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(foundLocation);
							}
						}
						else
						{
							geo.deactivate();
							geo.activate();
							mainAppDisplay.setLocationTextBoxText("Oops, requested location not found, reverting to current location...");
						}
						
					} 
					else
					{
						System.out.println("Couldn't retrieve JSON (" + response.getStatusText()+ ")");
					}
				}
			});
		} 
		catch (RequestException e) 
		{
			System.out.println("Couldn't retrieve JSON");
		}
	}
	
	private LonLat parse(String json) 
	{
		LonLat lonLat = null;
		
		JSONValue value = JSONParser.parseStrict(json);
		JSONArray array = value.isArray();
		
		System.out.println(array.size());
		
		// Get the first result retrieved
		JSONValue val = array.get(0);
		System.out.println(val.toString());
		
		JSONObject obj = val.isObject();
		
		if (obj.get("lon") != null && obj.get("lat") != null)
		{
			lonLat = new LonLat(Double.valueOf(obj.get("lon").toString().replace("\"", "")), Double.valueOf(obj.get("lat").toString().replace("\"", "")));
			
			//transforming from "EPSG:4326" to "EPSG:900913" which is what the map created with openlayers is using
			lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection());	
		
			System.out.println("@MainAppPresenter, @parse, Created lon and lat: "+ lonLat.lon()+" "+lonLat.lat());
		}
		return lonLat;
	}
	
	/**
	 * TODO - Testing function...
	 */
	private void reverseGeocoding(LonLat pointCentre)
	{
		// Testing reverse geocoding using open street maps
		
		// http://www.tdmarketing.co.nz/blog/2011/03/04/reverse-geocoding-via-the-open-street-map-and-google-maps-apis/
		// http://www.gwtproject.org/doc/latest/tutorial/JSON.html
		
		
		// According to the terms of usage, dont use nominatim openstreetmap.org but rather from mapquest, please check following references:
		// http://wiki.openstreetmap.org/wiki/Nominatim#Usage_Policy 
		// http://open.mapquestapi.com/nominatim/
		//http://developer.mapquest.com/web/products/open/nominatim
		
		//String url = "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&lat=37.34&lon=-121.94";
		System.out.println(pointCentre.lat());
		String url = "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&lat="+pointCentre.lat()+"&lon="+pointCentre.lon();
		
		
		//String url = "http://nominatim.openstreetmap.org/reverse?format=json&lat=37.34&lon=-121.94";
		
		// Send request to server and catch any errors.
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try 
		{
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(null, new RequestCallback() 
			{
				public void onError(Request request, Throwable exception) 
				{
					System.out.println("Couldn't retrieve JSON");
				}

				public void onResponseReceived(Request request, Response response) 
				{
					if (200 == response.getStatusCode()) 
					{
						//System.out.println("Reverse Geocoding successful:"+JsonUtils.safeEval(response.getText().toString()));
						System.out.println("Reverse Geocoding successful:"+response.getText());
						
						/**
						 * Reverse Geocoding successful:
									"place_id":"23366922",
									"licence":"Data \u00a9 OpenStreetMap contributors, ODbL 1.0. http:\/\/www.openstreetmap.org\/copyright",
									"osm_type":"way",
									"osm_id":"8936657",
									"lat":"37.340441","lon":"-121.940468",
									"display_name":"Highland Court, Santa Clara, Santa Clara County, California, 95050, United States of America",
									"address":{"road":"Highland Court","city":"Santa Clara","county":"Santa Clara County","state":"California","postcode":"95050","country":"United States of America","country_code":"us"}}
						 */
					} 
					else
					{
						System.out.println("Couldn't retrieve JSON (" + response.getStatusText()+ ")");
					}
				}
			});
		} 
		catch (RequestException e) 
		{
			System.out.println("Couldn't retrieve JSON");
		}
	}

	private void deleteLayerFeature()
	{
		if (vectorLayer.getSelectedFeatures() != null && vectorLayer.getSelectedFeatures().length==1)
        {
			System.out.println("@MainAppPresenter, @bind, @getButtonDeleteBlip, deleting blip "+vectorLayer.getSelectedFeatures()[0].getFeatureId());
			
			// Delete feature added on layer
			vectorLayer.getSelectedFeatures()[0].destroy();
			
			// This section here is used in case only one point feature is allowed to be created 
			// (see vectorLayer.addVectorFeatureAddedListener in method "go()"). 
			
			// *This is now obsolete because delete button has been removed from interface*
			// In this case, when the delete button is clicked (which will result to the deletion of the feature on the map) we want 
			// the toggle button for feature creation to be re-enabled
			int countPointFeatures = 0;
			
			// -- Section: Adding this condition, cause if a path is here, the vectorLayer will not be null, thus need to pick the point
			if (vectorLayer.getFeatures() != null)
			{
				for (VectorFeature feature:vectorLayer.getFeatures())
				{
					System.out.println("@MainAppPresenter, @bind, @getButtonDeleteBlip, finding features: "+feature.getGeometry().getClassName());
					
					if (feature.getGeometry().getClassName().equals(Geometry.POINT_CLASS_NAME))
					{
						System.out.println("@MainAppPresenter, @bind, @getButtonDeleteBlip, finding features: "+feature.getFeatureId());
						countPointFeatures++;
					}
				}
			}
			if (countPointFeatures == 1)
			{
				mainAppDisplay.setToggleButtonCreateQuickBlipEnable(false);
			}
			else
			{
				mainAppDisplay.setToggleButtonCreateQuickBlipEnable(true);
			}
			// -- End of section
			
			// Delete feature held in BlipPathBuilder instance
			if (MainAppPresenter.blipPathBuilder != null)
			{
				// TODO - remove if using the MainAppPresenter.blipPathBuilder = null; statement?
				if (MainAppPresenter.blipPathBuilder.getPath() != null)
				{
					System.out.println("@MainAppPresenter, @bind, @getButtonDeleteBlip, Destroying path");
					MainAppPresenter.blipPathBuilder.getPath().destroy();
				}						
				MainAppPresenter.blipPathBuilder = null;
			}
        }
	}
	
	/**
	 * Create blip using defined path, and content
	 */
	private void setupContentViewHandlers()
	{
		addBlipContentDisplay.getButtonSubmitContent().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				addBlipContentDisplay.setEnable(false);
				System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.SubmitContent, @onClick");
				
				Long contentKey = addBlipContentDisplay.getContentId();
				if (contentKey != null)
				{
					System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.SubmitContent, content id: "+contentKey);
					
					BlipHabitat habitat = new BlipHabitat();
					
					ArrayList<Point> pathPoints = null;
					
					// The pop up has opened which means that blipPathBuilder is constructed, thus no need to check if its null.
					if (MainAppPresenter.blipPathBuilder.getCustomOrbit())
					{
						// This gets all the intermediate point created (including the user clicks) - TODO execute translatePathToPoints on server side
						pathPoints = new BlipPathBuilder().translatePathToPoints(MainAppPresenter.blipPathBuilder.getPath());
					}
					else
					{
						// The getPointList gets the user clicks that create a line or polygon in case we create a quick blip with a default orbit.
						// TODO  We can use this in either case and execute translatePathToPoints on server side.
						pathPoints = MainAppPresenter.blipPathBuilder.getPointList();
					}
					
					for(Point point:pathPoints)
					{
						habitat.getLoc().add(new BlipPoint(point.getX(),point.getY()));
					}
					
					rpcMainAppService.persistBlip(blipMaster.getBlipMasterEmail(), habitat, contentKey, "no_blip_type_defined", new AsyncCallback<Void>()
					{
						@Override
						public void onFailure(Throwable caught)
						{
							System.out.println(caught.getMessage());
							
							System.out.println("@MainAppPresenter, @bind,@addBlipContentDisplay.SubmitContent, onFailure");
							mainAppDisplay.getMapComponent().bringSimpleMapToFront();
							
							if (addBlipContentDisplay != null)
							{
								addBlipContentDisplay.deleteContent();
								addBlipContentDisplay.showDialogBox(false);
							}
						}

						@Override
						public void onSuccess(Void result)
						{
							System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.SubmitContent, onSuccess");
							mainAppDisplay.getMapComponent().bringSimpleMapToFront();
							
							if (addBlipContentDisplay != null)
							{
								// Delete feature held in BlipPathBuilder instance
								if (MainAppPresenter.blipPathBuilder != null)
								{									
									if (MainAppPresenter.blipPathBuilder.getPath() != null)
									{
										System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.SubmitContent, Destroying point and path");
										vectorLayer.getFeatureById(MainAppPresenter.blipPathBuilder.getPath().getFeatureId()).destroy();
										vectorLayer.getFeatureById(MainAppPresenter.blipPathBuilder.getFeatureId()).destroy();
										MainAppPresenter.blipPathBuilder = null;
									}						
								}
								addBlipContentDisplay.showDialogBox(false);
								
								// Run once on succesful submission of blip - after 15 seconds
								liveBlipHandler.schedule(15000);
								
								mainAppDisplay.setToggleButtonCreateQuickBlipEnable(true);
							}
						}
					});
					
				}
				else
				{
					System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.SubmitContent, no content id found");
				}
				//addBlipContentDisplay.showDialogBox(false);
			}
		});
		
		addBlipContentDisplay.getButtonCancel().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.getButtonCancel, @onClick");
				
				System.out.println("@MainAppPresenter, @bind, @addBlipContentDisplay.getButtonCancel, toggling: Button Up");
				
				mainAppDisplay.getMapComponent().bringSimpleMapToFront();
				
				if (addBlipContentDisplay != null)
				{
					addBlipContentDisplay.deleteContent();
					
					// Addition for quick blip button
					if (!MainAppPresenter.blipPathBuilder.getCustomOrbit())
					{
						deleteLayerFeature();
					}
					
					addBlipContentDisplay.showDialogBox(false);
				}
			}
		});
	}
	
	/**
	 * Method to preview the Blip moving on the Map
	 * 
	 * @param pathPoints
	 */
	@SuppressWarnings("unused")
	private void previewPath(ArrayList<Point> pathPoints)
	{
		VectorFeature blipToAnimate = vectorLayer.getFeatureById(MainAppPresenter.blipPathBuilder.getFeatureId());

		if (blipToAnimate != null)
		{
			System.out.println("@MainAppPresenter, @previewPath, Blip to animate: "+blipToAnimate.getFeatureId());

			Timer elapsedTimer = new BlipPreviewAnimator (blipToAnimate, pathPoints);

			elapsedTimer.scheduleRepeating(20);	
		}
		else
		{
			System.out.println("@MainAppPresenter, @previewPath, Blip to animate not found ("+MainAppPresenter.blipPathBuilder.getFeatureId()+")");
		}
	}
	
	
	/**
	 * Initializing layer that will contain all retrieved blips
	 */
	private void initActiveBlipsLayer()
	{
		// Adding the vector layer that will be used for retrieving blips
		Vector activeBlipsVectorLayer = new Vector("Active Blips Vector layer");
		
		//makes sure labels don't overleap
		activeBlipsVectorLayer.setTextRootRendererToVectorRootRenderer();
		
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addLayer(activeBlipsVectorLayer);

		// Initialize the live blips handler
		liveBlipHandler = new LiveBlipHandler (mainAppDisplay, activeBlipsVectorLayer, rpcMainAppService);
		
		// run once on execution of initActiveBlipsLayer()
		liveBlipHandler.run();
		
		// Schedule the timer i.e. for every 5 minutes (1000milliseconds * 60 = 60seconds * 5 = 5minutes) 
		liveBlipHandler.scheduleRepeating(1000*60*5);		
	}
	
	
	/**
	 * Define functionality for main menu bar - not currently used
	 */
	private void logMenuBarLogic()
	{
		System.out.println("@MainAppPresenter, @bind, @mainMenuBarLogic");
		MouseOverHandler handlerBringToBack = new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event)
			{
				System.out.println("@MainAppPresenter, @bind, @mainMenuBarLogic, @onMouseOver");
				//mainAppDisplay.getMapComponent().bringSimpleMapToBack();
			}
		};		
		mainAppDisplay.getMainAppLogMenubar().addDomHandler(handlerBringToBack, MouseOverEvent.getType());

		/**
		 * We could add a onClick event for a button to navigate somewhere outside 
		 * of the main app window using eventBus.fireEvent(new AdminHomeEvent());	
		 */
	}
	
	/**
	 * Defines anything happening on the map component - TODO towards deprecation...
	 */
	private void mapComponentLogic()
	{
		System.out.println("@MainAppPresenter, @bind, @mapComponentLogic");
		MouseOverHandler handlerBringToFront = new MouseOverHandler()
		{

			public void onMouseOver(MouseOverEvent event)
			{
				System.out.println("@MainAppPresenter, @bind, @mapComponentLogic, @onMouseOver");
				//mainAppDisplay.getMapComponent().bringSimpleMapToFront();
			}
		};
		mainAppDisplay.getMapComponent().addDomHandler(handlerBringToFront, MouseOverEvent.getType());
	}
		
	/**
	 * Helper method converting from "EPSG:900913" projection to "EPSG:4326" projection.
	 * "EPSG:900913" is what openstreet maps is using.
	 * 
	 * From StackOverflow: "...They are not the same. EPSG:4326 refers to WGS 84 whereas 
	 * EPSG:900913 refers to WGS84 Web Mercator. EPSG:4326 treats the earth as an ellipsoid 
	 * while EPSG:900913 treats it as a sphere. Also, coordinate values will be totally different, 
	 * EPSG:4326 has decimal degree values (-180 to 180 and -90 to 90) while EPSG:900913 
	 * has metric values (-20037508.34 to 20037508.34) on x/y axis coordinate system.
	 * 
	 * http://stackoverflow.com/questions/5784129/coordinates-not-accurate-when-converting-to-int
	 * http://docs.openlayers.org/library/spherical_mercator.html
	 * http://gis.stackexchange.com/questions/34276/whats-the-difference-between-epsg4326-and-epsg900913
	 * http://stackoverflow.com/questions/2601745/how-to-convert-vector-layer-coordinates-into-map-latitude-and-longitude-in-openl
	 * http://stackoverflow.com/questions/2673945/change-projection-in-openlayers-map
	 * 
	 * @param lon
	 * @param lat
	 * @return
	 */
	@SuppressWarnings("unused")
	private LonLat transformLonLatToEPSG4326(double lon, double lat)
	{	
		LonLat newLonLat = new LonLat(lon, lat);
		newLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());	
		
		return newLonLat;
		
	}
	
	/**
	 * Adding map initialisations (handlers and styles)
	 */
	@Override
	public void go(HasWidgets container)
	{
		bind();
		container.clear();
		container.add(mainAppDisplay.asWidget());
		initDrawStyle();
		initHighlightedStyle();
		
		// Adding the vector layer that will be used for drawing the vectors
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addLayer(vectorLayer);
		
		// Map handler for a moving the map event
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addMapMoveListener(new MapMoveListener()
		{
			@Override
			public void onMapMove(MapMoveEvent eventObject)
			{
				//TODO - leaving in for testing
				//LonLat mapCentreLonLat = mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getCenter();
				//mapCentreLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
				//System.out.println("@MainAppPresenter, @go, @ onMapMove, New Centre is, Lon:"+mapCentreLonLat.lon()+", Lat:"+mapCentreLonLat.lat());
			}
		});
		
		// What to do when clicking in the map area - related to the toggle buttons that are up/down.
		//http://stackoverflow.com/questions/4624301/adding-custom-markers-dynamically-to-map-using-openlayers
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addMapClickListener(new MapClickListener()
		{
			@Override
			public void onClick(MapClickEvent mapClickEvent)
			{
				System.out.println("@MainAppPresenter, @go ,@addMapClickListener, @onClick");	
			}	
		});
		
		// What to do when a new feature is added on the vector layer - currently simply do not allow insersion of another one
		vectorLayer.addVectorFeatureAddedListener(new VectorFeatureAddedListener()
		{
			@Override
			public void onFeatureAdded(FeatureAddedEvent eventObject)
			{
				if (eventObject.getVectorFeature().getGeometry().getClassName().equals(Geometry.POINT_CLASS_NAME))
				{
					clickSelectFeatureControl.select(vectorLayer.getFeatureById(eventObject.getVectorFeature().getFeatureId())); 

					drawPointFeatureControl.deactivate();
					//clickSelectFeatureControl.activate();

					if (mainAppDisplay.getToggleButtonCreateQuickBlipIsDown())
					{
						System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, Quick Blip Button Down and Map Clicked."); 

						//TODO create a simple orbit by creating a polygon with its centre being at the centre of the feature 
						VectorFeature[] vectorFeatures = vectorLayer.getFeatures();
						if (vectorFeatures.length ==1)
						{
							Point point = Point.narrowToPoint(vectorLayer.getFeatures()[0].getGeometry().getJSObject());

							int randomRadius = Random.nextInt(150); int minRadius = 25;
							// if the variable randomRadius more than minRadius, radius is assigned the value of randomRadius; otherwise, radius is assigned the value of minRadius
							int radius = (randomRadius > minRadius) ? randomRadius : minRadius;
							
							// Hardcoding sides to 60 (logic being that 1 side = 1 minute, also defined in MainAppServiceImpl>setTimesOnHabitatPath)
							Polygon orbit = Polygon.createRegularPolygon(point, radius, 15, 0);
							
							Point[] points = orbit.getVertices(false);
							
							System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, orbit points length:"+points.length);

							// Initialize blipPathBuilder, storing the feature id, starting coodrs and its blipMaster - starting from points[1] cause 0 is the centre.
							MainAppPresenter.blipPathBuilder = new BlipPathBuilder(vectorLayer.getFeatures()[0].getFeatureId(), points[1], blipMaster, false);

							MainAppPresenter.blipPathBuilder.getPointList().addAll(Arrays.asList(points));
							MainAppPresenter.blipPathBuilder.addPoint(points[0]); //adding the first so that the polygon will be closed.
							

							System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, Destroying path if not null");
							if (MainAppPresenter.pathVectorFeature != null)
							{
								MainAppPresenter.pathVectorFeature.destroy();
							}
							
							System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, Re-creating path");
							MainAppPresenter.pathVectorFeature = MainAppPresenter.blipPathBuilder.contructPath();

							System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, Adding path (as vector on layer)");
							
							//addting the orbit - not useful for application logic, it just gives us its visualisation to help us see that it was created correctly. 
							vectorLayer.addFeature(MainAppPresenter.pathVectorFeature);

							mainAppDisplay.getMapComponent().bringSimpleMapToBack();
							
							// Create pop up for adding content
							// Pass reverseGeocoding() parameter for translating centre to a meaningful address
							LonLat pointCentre = MainAppPresenter.pathVectorFeature.getCenterLonLat();
							pointCentre.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(),DEFAULT_PROJECTION.getProjectionCode());
							reverseGeocoding(pointCentre);
							addBlipContentDisplay = new AddContentView((long) 1);

							// Setup handlers for the pop up
							setupContentViewHandlers();

							// Show the pop up
							addBlipContentDisplay.showDialogBox(true);	
						}

						// No need to set Blip path (cause default was used) or content (cause it popped up)

						mainAppDisplay.setToggleButtonCreateQuickBlipEnable(false);
						mainAppDisplay.setToggleButtonCreateQuickBlipIsDown(false);
						System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, new point vector added: "+eventObject.getVectorFeature().getFeatureId()); 
					}
					else
					{
						System.out.println("@MainAppPresenter, @go, @vectorLayer.addVectorFeatureAddedListener, new vector added: "+eventObject.getVectorFeature().getGeometry().getClassName());
					}
				}
			}
		});
		
        vectorLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() //can also use for updating label that shows the number of selected features
        {
            public void onFeatureSelected(FeatureSelectedEvent eventObject) 
            {
                if (vectorLayer.getSelectedFeatures() != null)
                {
                  	eventObject.getVectorFeature().setStyle(highlightedStyle);
                  	//eventObject.getVectorFeature().redrawParent();
                  	vectorLayer.drawFeature(eventObject.getVectorFeature());
                		System.out.println("@MainAppPreseter, @addVectorFeatureSelectedListener, getSelectedFeature= "+eventObject.getVectorFeature().getFeatureId()+", features length: "+vectorLayer.getSelectedFeatures().length);
                }
                	
            }
        });
       
        vectorLayer.addVectorFeatureUnselectedListener(new VectorFeatureUnselectedListener() 
        {
            public void onFeatureUnselected(FeatureUnselectedEvent eventObject) 
            {
             	eventObject.getVectorFeature().setStyle(drawStyle);
    				//eventObject.getVectorFeature().redrawParent();
    				vectorLayer.drawFeature(eventObject.getVectorFeature());
                 System.out.println("@MainAppPreseter, @addVectorFeatureUnselectedListener, UnSelected: "+eventObject.getVectorFeature().getFeatureId());
               
            }
        });
        
        vectorLayer.setStyle(drawStyle);
            
        // http://dev.openlayers.org/docs/files/OpenLayers/Map-js.html#OpenLayers.Map.controls, http://dev.openlayers.org/docs/files/OpenLayers/Control-js.html
        // Remove zoom control
        // mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().removeControl(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.Zoom"));
        
        // Remove Layer Switcher
        //mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().removeControl(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.LayerSwitcher"));
        
        // Create the draw control
        drawPointFeatureControl = new DrawFeature(vectorLayer, new PointHandler(), initStyle(drawStyle));
        
        // Add the draw control
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(drawPointFeatureControl);
        
        // Create the select controls
        clickSelectFeatureControl = new SelectFeature(vectorLayer); //No options needed because no hover is needed
        clickSelectFeatureControl.setClickOut(false);                        //Do not deselect on click out
        clickSelectFeatureControl.setToggle(false);                           //Do not toggle when reclicked
        clickSelectFeatureControl.setMultiple(false);                         //Do not select multiple when clicked normally
        
        // Add the select feature control
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(clickSelectFeatureControl);
                
        //Create draw line control - TODO removing as will not be using now.
        //mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(new DrawFeature(vectorLayer, new PathHandler()));
        
        // Add geolocation control
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(geo);
        
        geo.activate();
        geo.addLocationUpdateListener(new LocationUpdateListener()
        {
			@Override
			public void onLocationUpdate(LocationUpdateEvent eventObject)
			{
				System.out.println("");
				System.out.println("@MainAppPresenter, @go, New Location Found: "+eventObject.getPoint());
				LonLat centre = mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getCenter();
				System.out.println("@MainAppPresenter, @go, new centre is, lon: "+centre.lon()+" lat: "+centre.lat());
			}
		});
        
        /**
		 * Any additional actions required when this class 
		 * is called (such as calling the service).
		 * 
		 * i.e. callService();
		 */
	}
	
	private void initDrawStyle()
	{
		//Create a style. We want a blue line.
	    drawStyle.setFillColor("#EB6C6C");
	    drawStyle.setPointRadius(15);
	    drawStyle.setStrokeWidth(3);
	    drawStyle.setStrokeColor("#999999");
	    drawStyle.setFillOpacity(0.5);
	}
	
	private void initHighlightedStyle()
	{
		highlightedStyle.setFillColor("#66FF6F");
		highlightedStyle.setPointRadius(20);
		highlightedStyle.setStrokeWidth(3);
		highlightedStyle.setStrokeColor("#666666");
		highlightedStyle.setFillOpacity(0.5);
	}
	
	
	private DrawFeatureOptions initStyle(Style style)
	{
	    // Create a StyleMap using the Style
	    StyleMap drawStyleMap = new StyleMap(style);
	
	    // Create PathHanlderOptions using this StyleMap
	    PathHandlerOptions phOpt = new PathHandlerOptions();
	    phOpt.setStyleMap(drawStyleMap);   
	
	    // Create DrawFeatureOptions and set the PathHandlerOptions (that have the StyleMap, that have the Style we wish)
	    DrawFeatureOptions drawFeatureOptions = new DrawFeatureOptions();
	    drawFeatureOptions.setHandlerOptions(phOpt);
	    return drawFeatureOptions;
	}
		
}
