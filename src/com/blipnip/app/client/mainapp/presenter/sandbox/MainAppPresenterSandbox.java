package com.blipnip.app.client.mainapp.presenter.sandbox;

import java.util.ArrayList;
import java.util.List;

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
import com.blipnip.app.client.mainapp.presenter.LiveBlipHandler;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
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
public class MainAppPresenterSandbox implements Presenter
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
		
		Widget getMainAppMenubar();
		
		HasClickHandlers getToggleButtonCreateNewBlip();
		Boolean getToggleButtonCreateNewBlipIsDown();
		
		HasClickHandlers getToggleButtonSetTravelPath();
		Boolean getToggleButtonSetTravelPathIsDown();
		
		HasClickHandlers getToggleButtonSetBipContent();
		Boolean getToggleButtonSetBipContentIsDown();
		
		HasClickHandlers getButtonCreateQuickBlip();
		Boolean getToggleButtonCreateQuickBlipIsDown();
		
		HasClickHandlers getButtonMyLocation();
		HasClickHandlers getButtonPreviewBlip();
		HasClickHandlers getButtonDeleteBlip();
		
		HasClickHandlers getButtonPrevious();
		HasClickHandlers getButtonNext();
		HasClickHandlers getButtonView();
		HasClickHandlers getButtonRefresh();
		
		
		String getBlipOverview();
		String getliveBlipUserImageURL();
		
		HasClickHandlers getToggleButtonMapNavigation();
		Boolean getToggleButtonMapNavigationIsDown();

		void setUserInfoData(BlipMaster data);
		void setBlipsOnArea(ArrayList<Blip> blipList);
		
		void setToggleButtonCreateNewBlipIsDown(boolean isDown);
		void setToggleButtonSetBipContentIsDown(boolean isDown);
		void setToggleButtonSetTravelPathIsDown(boolean isDown);
		void setToggleButtonCreateQuickBlipIsDown(boolean isDown);
		
		void setToggleButtonCreateNewBlipEnabled(boolean enable);
		void setToggleButtonSetBipContentEnabled(boolean enable);
		void setToggleButtonSetTravelPathEnable(boolean enable);
		void setToggleButtonCreateQuickBlipEnable(boolean enable);
		
		void setButtonMyLocationEnable(boolean enable);
		void setButtonDeleteBlipEnable(boolean enable);
		void setButtonPreviewBlipEnable(boolean enable);
		void setBlipOverview(String overviewText);
		void setLiveBlipUserImageURL(String url);
		
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
	private VectorFeature[] vectorFeatureArray = null;
	private Integer vectorFeatureArrayIndex     = null;
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
	
	public MainAppPresenterSandbox(LoginInfo loginInfo, MainAppServiceAsync rpcService, HandlerManager eventBus, MainAppDisplay mainAppView) 
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
		System.out.println("@MainAppPresenterSandbox, @bind, start");
		
		this.rpcMainAppService.initBlipMaster(loginInfo.getEmailAddress(), loginInfo.getUserName(),  loginInfo.getLoginRank(), loginInfo.getPictureUrl(), new AsyncCallback<BlipMaster>() 
		{
			@Override
			public void onFailure(Throwable caught)
			{
				blipMaster = null;
				System.out.println("@MainAppPresenterSandbox, @bind, @initBlipMaster, @onFailure, caught: "+caught.getMessage());
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
				    	 mainMenuBarLogic();          // not curretly useful, but leaving in for potential future use
				    	 newBlipActionLogic();		   // actions related to creating new blips (currently on upper bars) 
				    	 mapComponentLogic();      // map component logic - is different to map logic
				    	 //lowerUserActionBarLogic(); // used for viewing blips on the map
			     }
			     else
			     {
			    	 	System.out.println("@MainAppPresenterSandbox, @bind, blipmaster is null");
			     }
			     
			     mainAppDisplay.setUserInfoData(blipMaster);
			     System.out.println("@MainAppPresenterSandbox, @bind, @initBlipMaster, @onSuccess, getBlipMasterEmail: "+result.getBlipMasterEmail());
			}
		});
	}
	
	/**
	 * Actions related to creating new blips (currently on upper bars)
	 */
	private void newBlipActionLogic()
	{
		mainAppDisplay.getButtonMyLocation().addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @newBlipActionLogic, @getButtonMyLocation()");
				
				//TODO - easy way out by deactivating and re-activating
				//http://openlayers.org/dev/examples/geolocation.html
				//http://dev.openlayers.org/docs/files/OpenLayers/Control/Geolocate-js.html 
				
				geo.deactivate();
				geo.activate();
				
				
				// Testing reverse geocoding using open street maps
				
				// http://www.tdmarketing.co.nz/blog/2011/03/04/reverse-geocoding-via-the-open-street-map-and-google-maps-apis/
				// http://www.gwtproject.org/doc/latest/tutorial/JSON.html
				
				
				// According to the terms of usage, dont use nominatim openstreetmap.org but rather from mapquest, please check following references:
				// http://wiki.openstreetmap.org/wiki/Nominatim#Usage_Policy 
				// http://open.mapquestapi.com/nominatim/
				//http://developer.mapquest.com/web/products/open/nominatim
				
				String url = "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&lat=37.34&lon=-121.94";
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
		});
		
		// logic related to this also in vectorLayer.addVectorFeatureAddedListener
		mainAppDisplay.getToggleButtonCreateNewBlip().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				// If create new blip button is down, activate "drawPointFeatureControl" control and deactivate other buttons 
				if (mainAppDisplay.getToggleButtonCreateNewBlipIsDown())
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getToggleButtonCreateNewBlip(), Button Down");
					
					drawPointFeatureControl.activate();
					clickSelectFeatureControl.deactivate();
					
					mainAppDisplay.setToggleButtonCreateQuickBlipEnable(false);
					mainAppDisplay.setToggleButtonSetBipContentEnabled(false);
					mainAppDisplay.setToggleButtonSetTravelPathEnable(false);
					
					mainAppDisplay.setButtonDeleteBlipEnable(false);
					mainAppDisplay.setButtonPreviewBlipEnable(false);
				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getToggleButtonCreateNewBlip(), Button Up");

					drawPointFeatureControl.deactivate();
					clickSelectFeatureControl.activate();
					
					mainAppDisplay.setToggleButtonCreateQuickBlipEnable(true);
					mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
					mainAppDisplay.setToggleButtonSetTravelPathEnable(true);
					
					mainAppDisplay.setButtonDeleteBlipEnable(true);
					mainAppDisplay.setButtonPreviewBlipEnable(true);
				}
			}
		});
		
		// logic related to this also in vectorLayer.addVectorFeatureAddedListener
		mainAppDisplay.getButtonCreateQuickBlip().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				// If create new blip button is down, activate "drawPointFeatureControl" control and deactivate other buttons 
				if (mainAppDisplay.getToggleButtonCreateQuickBlipIsDown())
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getButtonCreateQuickBlip(), Button Down");
					
					drawPointFeatureControl.activate();
					clickSelectFeatureControl.deactivate();
					
					mainAppDisplay.setToggleButtonCreateNewBlipEnabled(false);
					mainAppDisplay.setToggleButtonSetBipContentEnabled(false);
					mainAppDisplay.setToggleButtonSetTravelPathEnable(false);
					
					mainAppDisplay.setButtonDeleteBlipEnable(false);
					mainAppDisplay.setButtonPreviewBlipEnable(false);
				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getButtonCreateQuickBlip(), Button Up");

					drawPointFeatureControl.deactivate();
					clickSelectFeatureControl.activate();
					
					mainAppDisplay.setToggleButtonCreateNewBlipEnabled(true);
					mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
					mainAppDisplay.setToggleButtonSetTravelPathEnable(true);
					
					mainAppDisplay.setButtonDeleteBlipEnable(true);
					mainAppDisplay.setButtonPreviewBlipEnable(true);
				}
			}
		});
		
		mainAppDisplay.getToggleButtonSetTravelPath().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (mainAppDisplay.getToggleButtonSetTravelPathIsDown())
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getToggleButtonSetTravelPathIsDown(), Button Down");
					
					// Disable mainAppDisplay navigation control - Note that class is the javascript one - not the java one
					mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.Navigation").deactivate();

					// Disable draw feature control
					drawPointFeatureControl.deactivate();

					// Disable select feature control
					clickSelectFeatureControl.deactivate();
					
					// Disable other buttons
					mainAppDisplay.setButtonDeleteBlipEnable(false);
					mainAppDisplay.setButtonPreviewBlipEnable(false);
					
					// If Blip path Builder is null, and if the selected feature is a point then initialize the Blip Path Builder
					// Also the "on map clicked" event in method go() is triggered in parallel to this that is handling adding
					// a new point and refreshing the blip path.
					if (MainAppPresenterSandbox.blipPathBuilder == null)
					{
						// If one and only one feature of the vectorLayer is selected AND if that feature is a point then
						// disable toggle buttons, get the location of the feature and initialize the blipPathBuilder.
						if (vectorLayer.getSelectedFeatures() != null && vectorLayer.getSelectedFeatures().length==1 
							&& vectorLayer.getSelectedFeatures()[0].getGeometry().getClassName().equals(Geometry.POINT_CLASS_NAME))
						{
							VectorFeature[] vectorFeature = vectorLayer.getSelectedFeatures();

							mainAppDisplay.setToggleButtonCreateNewBlipEnabled(false);
							mainAppDisplay.setToggleButtonSetBipContentEnabled(false);

							String featureId = vectorFeature[0].getFeatureId();

							Point rootFeaturePoint = new Point(vectorFeature[0].getCenterLonLat().lon(), vectorFeature[0].getCenterLonLat().lat());

							// Initialize blipPathBuilder, storing the feature id, starting coodrs and its blipMaster.
							MainAppPresenterSandbox.blipPathBuilder = new BlipPathBuilder(featureId, rootFeaturePoint, blipMaster, true);
						}
						else // 
						{
							mainAppDisplay.setToggleButtonSetTravelPathIsDown(false);
							System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getToggleButtonSetTravelPathIsDown(), Button Up");

							mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
							mainAppDisplay.setButtonDeleteBlipEnable(true);
							mainAppDisplay.setButtonPreviewBlipEnable(true);
							
							mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.Navigation").activate();
							drawPointFeatureControl.deactivate();
							clickSelectFeatureControl.activate();	
						}
					}
					else
					{
						mainAppDisplay.setToggleButtonCreateNewBlipEnabled(false);
						mainAppDisplay.setToggleButtonSetBipContentEnabled(false);
						
						if (MainAppPresenterSandbox.blipPathBuilder.getPath() != null)
						{
							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorLayer.getFeatureById(MainAppPresenterSandbox.blipPathBuilder.getFeatureId())); 
						}
					}
				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @mainAppDisplay.getToggleButtonSetTravelPathIsDown(), Button Up");
					
					mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
					mainAppDisplay.setButtonDeleteBlipEnable(true);
					mainAppDisplay.setButtonPreviewBlipEnable(true);
					
					if (MainAppPresenterSandbox.blipPathBuilder.getPath() != null)
					{
						clickSelectFeatureControl.unselectAll(null);
						//clickSelectFeatureControl.select(MainAppPresenterSandbox.blipPathBuilder.getPath()); 
						clickSelectFeatureControl.select(vectorLayer.getFeatureById(MainAppPresenterSandbox.blipPathBuilder.getFeatureId())); 
					}
					
					mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.Navigation").activate();
					drawPointFeatureControl.deactivate();
					clickSelectFeatureControl.activate();					
				}
			}});
		
		mainAppDisplay.getButtonDeleteBlip().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				deleteLayerFeature();
			}
		});
		
		mainAppDisplay.getButtonPreviewBlip().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (MainAppPresenterSandbox.blipPathBuilder != null && !MainAppPresenterSandbox.blipPathBuilder.getPointList().isEmpty())
				{
					 List<Point>  pointArray = MainAppPresenterSandbox.blipPathBuilder.getPointList();
					 
					 //Displaying all points comprising the LineString feature, for testing
					 System.out.println("@MainAppPresenterSandbox, @bind ,Cast to integer");
					 for (int index=0;index<pointArray.size();index++)
					 {
						 System.out.println(index+".onClick : "+(int)pointArray.get(index).getX()+", "+ (int)pointArray.get(index).getY());
						 //LonLat transfomedLonLat = new LonLat(pointArray.get(index).getX(), pointArray.get(index).getY());
						 //transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
						 //System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());
					 }
					 
					 // Here, the LineString is converted to a set of points, i.e having a two point LineString (asterisks) the intermediate points (dashes) are calculated  *---*
					 ArrayList<Point> pathPoints = new BlipPathBuilder().translatePathToPoints(MainAppPresenterSandbox.blipPathBuilder.getPath());
					 previewPath(pathPoints);
				}
			}});
		
		
		mainAppDisplay.getToggleButtonSetBipContent().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (mainAppDisplay.getToggleButtonSetBipContentIsDown())
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @getToggleButtonSetBipContent, Button Down");
					mainAppDisplay.getMapComponent().bringSimpleMapToBack();
					
					if (MainAppPresenterSandbox.blipPathBuilder != null)
					{
						if (MainAppPresenterSandbox.blipPathBuilder.getPath() != null)
						{
							System.out.println("@MainAppPresenterSandbox, @bind, @getToggleButtonSetBipContent, Blip Found: ("+MainAppPresenterSandbox.blipPathBuilder.getFeatureId()+"), with path: "+ MainAppPresenterSandbox.blipPathBuilder.getPath().toString());
							System.out.println("@MainAppPresenterSandbox, @bind, @getToggleButtonSetBipContent, opening \" Add content \" View ");
							
							// Create pop up for adding content
							addBlipContentDisplay = (AddBlipContentDisplay) new AddContentView((long) 1);
							
							// Setup handlers for the pop up
							setupContentViewHandlers();
							
							// Show the pop up
							addBlipContentDisplay.showDialogBox(true);
						}
						else
						{
							System.out.println("@MainAppPresenterSandbox, @bind, @getToggleButtonSetBipContent, Please set blip travel path before setting its content");
							mainAppDisplay.setToggleButtonSetBipContentIsDown(false);
						}
					}
					else
					{
						System.out.println("@MainAppPresenterSandbox, @bind, @getToggleButtonSetBipContent, No blip found on map, please insert a blip first.");	
						mainAppDisplay.setToggleButtonSetBipContentIsDown(false);
						
					}

				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @getToggleButtonSetBipContent, Button Up");
					mainAppDisplay.getMapComponent().bringSimpleMapToFront();
					
					if (addBlipContentDisplay != null)
					{
						addBlipContentDisplay.showDialogBox(false);
					}
					
					mainAppDisplay.setToggleButtonSetTravelPathEnable(true);
					mainAppDisplay.setButtonDeleteBlipEnable(true);
					mainAppDisplay.setButtonPreviewBlipEnable(true);
				}
				
			}
			
		});
	}

	private void deleteLayerFeature()
	{
		if (vectorLayer.getSelectedFeatures() != null && vectorLayer.getSelectedFeatures().length==1)
        {
			System.out.println("@MainAppPresenterSandbox, @bind, @getButtonDeleteBlip, deleting blip "+vectorLayer.getSelectedFeatures()[0].getFeatureId());
			
			// Delete feature added on layer
			vectorLayer.getSelectedFeatures()[0].destroy();
			
			// This section here is used in case only one point feature is allowed to be created (see vectorLayer.addVectorFeatureAddedListener in method "go()"). 
			// In this case, when the delete button is clicked (which will result to the deletion of the feature on the map) we want the toggle button for feature creation to be re-enabled
			int countPointFeatures = 0;
			
			// -- Section: Adding this condition, cause if a path is here, the vectorLayer will not be null, thus need to pick the point
			if (vectorLayer.getFeatures() != null)
			{
				for (VectorFeature feature:vectorLayer.getFeatures())
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @getButtonDeleteBlip, finding features: "+feature.getGeometry().getClassName());
					
					if (feature.getGeometry().getClassName().equals(Geometry.POINT_CLASS_NAME))
					{
						System.out.println("@MainAppPresenterSandbox, @bind, @getButtonDeleteBlip, finding features: "+feature.getFeatureId());
						countPointFeatures++;
					}
				}
			}
			if (countPointFeatures == 1)
			{
				mainAppDisplay.setToggleButtonCreateQuickBlipEnable(false);
				mainAppDisplay.setToggleButtonCreateNewBlipEnabled(false);
			}
			else
			{
				mainAppDisplay.setToggleButtonCreateQuickBlipEnable(true);
				mainAppDisplay.setToggleButtonCreateNewBlipEnabled(true);
			}
			mainAppDisplay.setToggleButtonSetTravelPathEnable(true);
			mainAppDisplay.setButtonPreviewBlipEnable(false);
			// -- End of section
			
			// Delete feature held in BlipPathBuilder instance
			if (MainAppPresenterSandbox.blipPathBuilder != null)
			{
				// TODO - remove if using the MainAppPresenterSandbox.blipPathBuilder = null; statement?
				if (MainAppPresenterSandbox.blipPathBuilder.getPath() != null)
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @getButtonDeleteBlip, Destroying path");
					MainAppPresenterSandbox.blipPathBuilder.getPath().destroy();
				}						
				MainAppPresenterSandbox.blipPathBuilder = null;
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
				System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.SubmitContent, @onClick");
				
				Long contentKey = addBlipContentDisplay.getContentId();
				if (contentKey != null)
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.SubmitContent, content id: "+contentKey);
					
					BlipHabitat habitat = new BlipHabitat();
					
					ArrayList<Point> pathPoints = null;
					
					// The pop up has opened which means that blipPathBuilder is constructed, thus no need to check if its null.
					if (MainAppPresenterSandbox.blipPathBuilder.getCustomOrbit())
					{
						// This gets all the intermediate point created (including the user clicks) - TODO execute translatePathToPoints on server side
						pathPoints = new BlipPathBuilder().translatePathToPoints(MainAppPresenterSandbox.blipPathBuilder.getPath());
					}
					else
					{
						// The getPointList gets the user clicks that create a line or polygon in case we create a quick blip with a default orbit.
						// TODO  We can use this in either case and execute translatePathToPoints on server side.
						pathPoints = MainAppPresenterSandbox.blipPathBuilder.getPointList();
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
							
							System.out.println("@MainAppPresenterSandbox, @bind,@addBlipContentDisplay.SubmitContent, onFailure");
							
							mainAppDisplay.setToggleButtonSetBipContentIsDown(false);
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
							System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.SubmitContent, onSuccess");
							mainAppDisplay.setToggleButtonSetBipContentIsDown(false);
							mainAppDisplay.getMapComponent().bringSimpleMapToFront();
							
							if (addBlipContentDisplay != null)
							{
								// Delete feature held in BlipPathBuilder instance
								if (MainAppPresenterSandbox.blipPathBuilder != null)
								{
									// Addition for quick blip button
									if (!MainAppPresenterSandbox.blipPathBuilder.getCustomOrbit())
									{
										mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
									}
									
									if (MainAppPresenterSandbox.blipPathBuilder.getPath() != null)
									{
										System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.SubmitContent, Destroying point and path");
										vectorLayer.getFeatureById(MainAppPresenterSandbox.blipPathBuilder.getPath().getFeatureId()).destroy();
										vectorLayer.getFeatureById(MainAppPresenterSandbox.blipPathBuilder.getFeatureId()).destroy();
										MainAppPresenterSandbox.blipPathBuilder = null;
									}						
								}
								addBlipContentDisplay.showDialogBox(false);
								
								// Run once on execution of initActiveBlipsLayer() - after 15 seconds
								liveBlipHandler.schedule(15000);
								
								mainAppDisplay.setToggleButtonCreateQuickBlipEnable(true);
								mainAppDisplay.setToggleButtonCreateNewBlipEnabled(true);
								mainAppDisplay.setToggleButtonSetTravelPathEnable(true);
								mainAppDisplay.setButtonPreviewBlipEnable(false);
							}
						}
					});
					
				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.SubmitContent, mo content id found");
				}
				//addBlipContentDisplay.showDialogBox(false);
			}
		});
		
		addBlipContentDisplay.getButtonCancel().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.getButtonCancel, @onClick");
				
				System.out.println("@MainAppPresenterSandbox, @bind, @addBlipContentDisplay.getButtonCancel, toggling: Button Up");
				mainAppDisplay.setToggleButtonSetBipContentIsDown(false);
				mainAppDisplay.getMapComponent().bringSimpleMapToFront();
				
				if (addBlipContentDisplay != null)
				{
					addBlipContentDisplay.deleteContent();
					mainAppDisplay.setButtonDeleteBlipEnable(true);
					
					// Addition for quick blip button
					if (!MainAppPresenterSandbox.blipPathBuilder.getCustomOrbit())
					{
						deleteLayerFeature();
						mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
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
	private void previewPath(ArrayList<Point> pathPoints)
	{
		VectorFeature blipToAnimate = vectorLayer.getFeatureById(MainAppPresenterSandbox.blipPathBuilder.getFeatureId());

		if (blipToAnimate != null)
		{
			System.out.println("@MainAppPresenterSandbox, @previewPath, Blip to animate: "+blipToAnimate.getFeatureId());

			Timer elapsedTimer = new BlipPreviewAnimator (blipToAnimate, pathPoints);

			elapsedTimer.scheduleRepeating(20);	
		}
		else
		{
			System.out.println("@MainAppPresenterSandbox, @previewPath, Blip to animate not found ("+MainAppPresenterSandbox.blipPathBuilder.getFeatureId()+")");
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
		liveBlipHandler = new LiveBlipHandler ((com.blipnip.app.client.mainapp.presenter.MainAppPresenter.MainAppDisplay) mainAppDisplay, activeBlipsVectorLayer, rpcMainAppService);
		
		// run once on execution of initActiveBlipsLayer()
		liveBlipHandler.run();
		
		// Schedule the timer i.e. for every 2 seconds (2000 milliseconds) 200000 
		liveBlipHandler.scheduleRepeating(1000*60*5);		
	}
	
	
	/**
	 * Define functionality for main menu bar - not currently used
	 */
	private void mainMenuBarLogic()
	{
		System.out.println("@MainAppPresenterSandbox, @bind, @mainMenuBarLogic");
		MouseOverHandler handlerBringToBack = new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @bind, @mainMenuBarLogic, @onMouseOver");
				mainAppDisplay.getMapComponent().bringSimpleMapToBack();
			}
		};		
		mainAppDisplay.getMainAppMenubar().addDomHandler(handlerBringToBack, MouseOverEvent.getType());

		/**
		 * We could add a onClick event for a button to navigate somewhere outside 
		 * of the main app window using eventBus.fireEvent(new AdminHomeEvent());	
		 */
	}
	
	/**
	 * Defines anything happening on the map component
	 */
	private void mapComponentLogic()
	{
		System.out.println("@MainAppPresenterSandbox, @bind, @mapComponentLogic");
		//Map map = mainAppDisplay.getMapComponent().getOpenLayersMap().getMap();
		
		MouseOverHandler handlerBringToFront = new MouseOverHandler()
		{

			public void onMouseOver(MouseOverEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @bind, @mapComponentLogic, @onMouseOver");
				mainAppDisplay.getMapComponent().bringSimpleMapToFront();
			}
		};
		mainAppDisplay.getMapComponent().addDomHandler(handlerBringToFront, MouseOverEvent.getType());
		
	}
	
	/**
	 *  Defines functionality for user actions on lower bar
	 */
	@SuppressWarnings("unused")
	private void lowerUserActionBarLogic()
	{
		mainAppDisplay.getButtonNext().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @bind, @userActionBarLogic, @getButtonNext");
				
				vectorFeatureArray = vectorLayer.getFeatures();
				if (vectorFeatureArray != null)
				{
					if (vectorFeatureArrayIndex == null)
					{
						if (vectorLayer.getFeatures() != null && vectorLayer.getFeatures().length>0)
						{
							vectorFeatureArrayIndex = new Integer(0);
							System.out.println("index:"+vectorFeatureArrayIndex+", feature: "+vectorFeatureArray[vectorFeatureArrayIndex].getFeatureId());

							LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureArrayIndex]);

							//mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat());
						}
						else
						{
							System.out.println("@MainAppPresenterSandbox, @bind, @userActionBarLogic, @getButtonPrevious, No vectors found...");
						}
					}
					else
					{
						if (vectorFeatureArrayIndex < vectorLayer.getFeatures().length-1)
						{
							vectorFeatureArrayIndex++;
							System.out.println("index:"+vectorFeatureArrayIndex+", feature: "+vectorFeatureArray[vectorFeatureArrayIndex].getFeatureId());

							LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureArrayIndex]);

							//mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat());
						}
						else
						{
							vectorFeatureArrayIndex = 0;
							System.out.println("index:"+vectorFeatureArrayIndex+", feature: "+vectorFeatureArray[vectorFeatureArrayIndex].getFeatureId());

							LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureArrayIndex]);

							//mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat());

						}
					}
				}
				else
				{
					vectorFeatureArrayIndex = null;
				}
			}
				

		});
		
		mainAppDisplay.getButtonPrevious().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @bind, @userActionBarLogic, @getButtonPrevious");

				vectorFeatureArray = vectorLayer.getFeatures();

				if (vectorFeatureArray != null)
				{
					if (vectorFeatureArrayIndex == null)
					{
						if (vectorLayer.getFeatures() != null && vectorLayer.getFeatures().length>0)
						{
							vectorFeatureArrayIndex = new Integer(0);
							System.out.println("index:"+vectorFeatureArrayIndex+", feature: "+vectorFeatureArray[vectorFeatureArrayIndex].getFeatureId());

							LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureArrayIndex]);

							//mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat());
						}
						else
						{
							System.out.println("@MainAppPresenterSandbox, @bind, @userActionBarLogic, @getButtonPrevious, No vectors found...");
						}
					}
					else
					{			
						if (vectorFeatureArrayIndex > 0)
						{
							vectorFeatureArrayIndex--;
							System.out.println("index:"+vectorFeatureArrayIndex+", feature: "+vectorFeatureArray[vectorFeatureArrayIndex].getFeatureId());

							LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());


							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureArrayIndex]);

							//mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat());
						}
						else
						{
							vectorFeatureArrayIndex = vectorLayer.getFeatures().length-1;
							System.out.println("index:"+vectorFeatureArrayIndex+", feature: "+vectorFeatureArray[vectorFeatureArrayIndex].getFeatureId());

							LonLat transfomedLonLat = new LonLat(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lon(), vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat().lat());
							transfomedLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
							System.out.println("Lon:"+transfomedLonLat.lon()+", Lat:"+transfomedLonLat.lat());

							clickSelectFeatureControl.unselectAll(null);
							clickSelectFeatureControl.select(vectorFeatureArray[vectorFeatureArrayIndex]);

							//mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().setCenter(vectorFeatureArray[vectorFeatureArrayIndex].getCenterLonLat());
						}
					}
				}
				else
				{
					vectorFeatureArrayIndex = null;
				}

					
					
				}
		});
		
		mainAppDisplay.getButtonView().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@MainAppPresenterSandbox, @bind, @userActionBarLogic, @getButtonView");
			}
		});
	}
	
	/**
	 * Helper method converting from "EPSG:900913" projection to "EPSG:4326" projection.
	 * "EPSG:900913" is what openstreet maps is using.
	 * 
	 * They are not the same. EPSG:4326 refers to WGS 84 whereas EPSG:900913 refers to WGS84 Web Mercator. 
	 * EPSG:4326 treats the earth as an ellipsoid while EPSG:900913 treats it as a sphere. 
	 * Also, coordinate values will be totally different, EPSG:4326 has decimal degree values 
	 * (-180 to 180 and -90 to 90) while EPSG:900913 has metric values (-20037508.34 to 20037508.34)
	 * on x/y axis coordinate system.
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
	private LonLat transformLonLat(double lon, double lat)
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
		
		initActiveBlipsLayer();
		
		// Adding the vector layer that will be used for drawing the vectors
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addLayer(vectorLayer);
		
		// Map handler for a moving the map event
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addMapMoveListener(new MapMoveListener()
		{
			@Override
			public void onMapMove(MapMoveEvent eventObject)
			{
				LonLat mapCentreLonLat = mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getCenter();
				mapCentreLonLat.transform(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getProjection(), DEFAULT_PROJECTION.getProjectionCode());				
				
				System.out.println("@MainAppPresenterSandbox, @go, @ onMapMove, New Centre is, Lon:"+mapCentreLonLat.lon()+", Lat:"+mapCentreLonLat.lat());
				
			}
		});
		
		
		// What to do when clicking in the map area - related to the toggle buttons that are up/down.
		//http://stackoverflow.com/questions/4624301/adding-custom-markers-dynamically-to-map-using-openlayers
		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addMapClickListener(new MapClickListener()
		{
			@Override
			public void onClick(MapClickEvent mapClickEvent)
			{
				System.out.println("@MainAppPresenterSandbox, @go ,@addMapClickListener, @onClick");
				
				if (mainAppDisplay.getToggleButtonCreateNewBlipIsDown())
				{
					if (vectorLayer != null && vectorLayer.getFeatures() != null && vectorLayer.getFeatures().length>1)
					{
						if (vectorLayer.getFeatures()[0].getGeometry().equals(Geometry.POINT_CLASS_NAME))
						{
							System.out.println("@MainAppPresenterSandbox, @go ,@undo");
						}
					}					
				}
				
				if (mainAppDisplay.getToggleButtonSetTravelPathIsDown()) 
				{
					System.out.println("@getToggleButtonSetTravelPathIsDown");

					// If one feature is selected then...
					if (vectorLayer.getSelectedFeatures() != null && vectorLayer.getSelectedFeatures().length==1)
					{
						VectorFeature[] vectorFeature = vectorLayer.getSelectedFeatures();
					    System.out.println("@Only one feature selected, geometry: "+vectorFeature[0].getGeometry());
						
					    // If selection is of class point then...
					    if (vectorFeature[0].getGeometry().getClassName().equals(Geometry.POINT_CLASS_NAME))
						{
							String featureId = vectorFeature[0].getFeatureId();

							Point latestClickOnMap = new Point(mapClickEvent.getLonLat().lon(), mapClickEvent.getLonLat().lat());

							System.out.println("@Feature Id: "+featureId+", [Lon,Lat] ="+"["+latestClickOnMap.getX()+", "+latestClickOnMap.getY()+"]");
							System.out.println(MainAppPresenterSandbox.blipPathBuilder.getFeatureId());

							// If the feature id stored in blipPathBuilder is the same as the selected feature id then...
							if (MainAppPresenterSandbox.blipPathBuilder.getFeatureId().equals(featureId))
							{
								if (MainAppPresenterSandbox.blipPathBuilder.getLastPoint() != null)
								{
									// if the last click on the map does not equal the last point in blipPathBuilder
									// add the new point, destroy the previous vector and create a new one with 
									// the latest point included.
									if (!MainAppPresenterSandbox.blipPathBuilder.getLastPoint().equals(latestClickOnMap))
									{
										MainAppPresenterSandbox.blipPathBuilder.addPoint(latestClickOnMap);

										System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Destroying path if not null");
										if (MainAppPresenterSandbox.pathVectorFeature != null)
										{
											MainAppPresenterSandbox.pathVectorFeature.destroy();
										}
										System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Re-creating path");
										MainAppPresenterSandbox.pathVectorFeature = MainAppPresenterSandbox.blipPathBuilder.contructPath();

										System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Adding path (as vector on layer)");
										vectorLayer.addFeature(MainAppPresenterSandbox.pathVectorFeature);
									}
									else
									{
										System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Last point equals current point - not adding");
									} 
								}
								else
								{
									System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, BlipPathFeature.getLastPoint() is null");
								}
							}
							else
							{
								System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, blipPathBuilder.featureId is different to selected feature");
							}
						}
						else
						{
							System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Please select one Blip to set its travel path");
						}
					}
					else
					{
						System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Please select Blip (Point) and not any other type of vector");
					}	
				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @go, @MapClickListener, Button not down");
				}
		}});
		
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
					clickSelectFeatureControl.activate();
					
					if (mainAppDisplay.getToggleButtonCreateQuickBlipIsDown())
					{
						System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, Quick Blip Button Down and Map Clicked."); 
						
						//TODO create simple orbit by creating a polygon with its centre being the centre of the feature 
						VectorFeature[] vectorFeatures = vectorLayer.getFeatures();
						if (vectorFeatures.length ==1)
						{
							Point point = Point.narrowToPoint(vectorLayer.getFeatures()[0].getGeometry().getJSObject());
							
							// Hardcoding sides to 600, this will be set by the time in seconds.
							Polygon orbit = Polygon.createRegularPolygon(point, 150, 600, 0);
							
							Point[] points = orbit.getVertices(false);
							System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, orbit points length:"+points.length);
							
							// Initialize blipPathBuilder, storing the feature id, starting coodrs and its blipMaster.
							MainAppPresenterSandbox.blipPathBuilder = new BlipPathBuilder(vectorLayer.getFeatures()[0].getFeatureId(), points[1], blipMaster, false);
							
							for (Point orbitPoint:points)
							{
								MainAppPresenterSandbox.blipPathBuilder.addPoint(orbitPoint);
							}
							
							System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, Destroying path if not null");
							if (MainAppPresenterSandbox.pathVectorFeature != null)
							{
								MainAppPresenterSandbox.pathVectorFeature.destroy();
							}
							System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, Re-creating path");
							
							MainAppPresenterSandbox.pathVectorFeature = MainAppPresenterSandbox.blipPathBuilder.contructPath();

							System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, Adding path (as vector on layer)");
							vectorLayer.addFeature(MainAppPresenterSandbox.pathVectorFeature);
									
							// Create pop up for adding content
							mainAppDisplay.getMapComponent().bringSimpleMapToBack();
							addBlipContentDisplay = (AddBlipContentDisplay) new AddContentView((long) 1);
							
							// Setup handlers for the pop up
							setupContentViewHandlers();
							
							// Show the pop up
							addBlipContentDisplay.showDialogBox(true);	
						}
						
						// No need to set Blip path (cause default was used) or content (cause it popped up)
						mainAppDisplay.setToggleButtonSetBipContentEnabled(false);
						mainAppDisplay.setToggleButtonSetTravelPathEnable(false);
					}
					else
					{
						mainAppDisplay.setToggleButtonSetBipContentEnabled(true);
						mainAppDisplay.setToggleButtonSetTravelPathEnable(true);
					}
					mainAppDisplay.setToggleButtonCreateQuickBlipEnable(false);
					mainAppDisplay.setToggleButtonCreateQuickBlipIsDown(false);
					
					mainAppDisplay.setToggleButtonCreateNewBlipEnabled(false);
					mainAppDisplay.setToggleButtonCreateNewBlipIsDown(false);
					
					
					
					mainAppDisplay.setButtonDeleteBlipEnable(true);
					mainAppDisplay.setButtonPreviewBlipEnable(true);
					System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, new point vector added: "+eventObject.getVectorFeature().getFeatureId()); 
				}
				else
				{
					System.out.println("@MainAppPresenterSandbox, @go, @vectorLayer.addVectorFeatureAddedListener, new vector added: "+eventObject.getVectorFeature().getGeometry().getClassName());
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
                  	eventObject.getVectorFeature().redrawParent();
                		System.out.println("@MainAppPreseter, @addVectorFeatureSelectedListener, getSelectedFeature= "+eventObject.getVectorFeature().getFeatureId()+", features length: "+vectorLayer.getSelectedFeatures().length);
                }
                	
            }
        });
       
        vectorLayer.addVectorFeatureUnselectedListener(new VectorFeatureUnselectedListener() 
        {
            public void onFeatureUnselected(FeatureUnselectedEvent eventObject) 
            {
             	eventObject.getVectorFeature().setStyle(drawStyle);
    				eventObject.getVectorFeature().redrawParent();
                 System.out.println("@MainAppPreseter, @addVectorFeatureUnselectedListener, UnSelected: "+eventObject.getVectorFeature().getFeatureId());
               
            }
        });
        
        vectorLayer.setStyle(drawStyle);
            
        // http://dev.openlayers.org/docs/files/OpenLayers/Map-js.html#OpenLayers.Map.controls, http://dev.openlayers.org/docs/files/OpenLayers/Control-js.html
       
        // Remove zoom control
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().removeControl(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.Zoom"));
        
        // Remove Layer Switcher
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().removeControl(mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getControlsByClass("OpenLayers.Control.LayerSwitcher"));
        
        // Create the draw controls
        drawPointFeatureControl = new DrawFeature(vectorLayer, new PointHandler(), initStyle());
        
        // Add the control
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(drawPointFeatureControl);
        
        // Create the select controls
        clickSelectFeatureControl = new SelectFeature(vectorLayer); //No options needed because no hover is needed
        clickSelectFeatureControl.setClickOut(false);                         //Do not deselect on click out
        clickSelectFeatureControl.setToggle(false);                           //Do not toggle when reclicked
        clickSelectFeatureControl.setMultiple(false);                         //Do not select multiple when clicked normally
        //clickSelectFeatureControl.setToggleKey("ctrlKey");                //Do toggle the selection when user holds CTRL key
        //clickSelectFeatureControl.setMultipleKey("shiftKey");            //Do select multiple features when user holds SHIFT key
        
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(clickSelectFeatureControl);
                
        //Create draw line control
        //drawLineFeatureControl = new DrawFeature(vectorLayer, new PathHandler());
        //mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(drawLineFeatureControl);
        
		/**
		 * Any additional actions required when this class 
		 * is called (such as calling the service).
		 * 
		 * i.e. callService();
		 */
		//mainAppDisplay.setUserInfoData(blipMaster);
        
        mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().addControl(geo);
        
        geo.activate();
        geo.addLocationUpdateListener(new LocationUpdateListener()
        {
			@Override
			public void onLocationUpdate(LocationUpdateEvent eventObject)
			{
				System.out.println("");
				System.out.println("@MainAppPresenterSandbox, @go, New Location Found: "+eventObject.getPoint());
				LonLat centre = mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().getCenter();
				System.out.println("@MainAppPresenterSandbox, @go, new centre is, lon: "+centre.lon()+" lat: "+centre.lat());
			}
		});
        
        	    //http://dantwining.co.uk/2010/03/01/gwt-openlayers-and-full-screen-maps/
      		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
      		{
      		    @Override
      		    public void execute()
      		    {
      		    		mainAppDisplay.getMapComponent().getOpenLayersMap().getMap().updateSize();
      		    }
      		});
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
		//drawStyle.setStrokeDashstyle("dash");
	}
	
	
	private DrawFeatureOptions initStyle()
	{
	    // Create a StyleMap using the Style
	    StyleMap drawStyleMap = new StyleMap(drawStyle);
	
	    // Create PathHanlderOptions using this StyleMap
	    PathHandlerOptions phOpt = new PathHandlerOptions();
	    phOpt.setStyleMap(drawStyleMap);   
	
	    // Create DrawFeatureOptions and set the PathHandlerOptions (that have the StyleMap, that have the Style we wish)
	    DrawFeatureOptions drawFeatureOptions = new DrawFeatureOptions();
	    drawFeatureOptions.setHandlerOptions(phOpt);
	    return drawFeatureOptions;
	}
		
}
