package com.blipnip.app.client.mainapp.view;

import java.util.ArrayList;

import com.blipnip.app.client.mainapp.presenter.MainAppPresenter;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.blipnip.app.client.mainapp.openlayers.MapComponent;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipMaster;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.InlineHTML;

public class MainAppView extends Composite implements MainAppPresenter.MainAppDisplay
{
	private BlipMaster loginInfo = null;

	private final DeckPanel contentLayoutPanel = new DeckPanel();
	private final VerticalPanel logPanel = new VerticalPanel();

	private final TextArea logTextArea = new TextArea();
	private final MenuBar appLogMenuBar = new MenuBar(false);
	private final MapComponent mapComponent = new MapComponent();
	private final VerticalPanel userLowerVerticalPanel = new VerticalPanel();
	private final HorizontalPanel userLowerHorizontalPanel = new HorizontalPanel();
	private final Image userImage = new Image("icons/blipnip_app_icons/mainapp/13_extra_actions_help/drawable-mdpi/ic_action_help.png");
	private final MenuItem mntmNewMenu;
	private final MenuItem mntmAbout;
	private final Label labelOverviewLabel = new Label("Select a Blip on the Map\u2026");
	private final VerticalPanel userUpperVerticalPanel = new VerticalPanel();
	private final HorizontalPanel userUpperHorizontalPanel = new HorizontalPanel();
	private final Image liveBlipUserImage = new Image("icons/blipnip_app_icons/mainapp/06_social_person/drawable-mdpi/ic_action_person.png");
	private final HorizontalPanel mainActionHorizontalPanel = new HorizontalPanel();
	private final HorizontalPanel userImageHorizontalPanel = new HorizontalPanel();
	private final ToggleButton tglbtnQuickBlip = new ToggleButton("Quick Blip");
	private final PushButton pshbtnPrevious = new PushButton("Prev");
	private final PushButton pshbtnNext = new PushButton("Next");
	private final PushButton pshbtnView = new PushButton("View Details");
	private final PushButton pshbtnRefresh = new PushButton("Refresh");
	private final PushButton pshbtnMyLocation = new PushButton("My Location");
	private final HorizontalPanel filterHorizontalPanel = new HorizontalPanel();
	private final ListBox filterListBox = new ListBox();
	private final TextBox locationTextBox = new TextBox();
	private final HorizontalPanel locationHorizontalPanel = new HorizontalPanel();
	private final AbsolutePanel absolutePanel = new AbsolutePanel();
	private final VerticalPanel filterVerticalPanel = new VerticalPanel();
	private final HorizontalPanel attributionPanel = new HorizontalPanel();
	private final InlineHTML attribution = new InlineHTML("New InlineHTML");

	private static final int OFFSET = 20;
	private final HorizontalPanel liveBlipControlHorizontalPanel = new HorizontalPanel();
	private final HorizontalPanel refreshHorizontalPanel = new HorizontalPanel();

	public MainAppView()
	{
		contentLayoutPanel.setAnimationEnabled(false);

		initWidget(contentLayoutPanel);

		String osmAtrribution = "&copy; 2014 <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors";
		String cloudMadeAttribution = "Map data " + osmAtrribution + ", &copy; 2014 <a href=\"http://mapbox.com\">MapBox</a>";

		//Setting the size of the app's "window" - full screen, - <BOTTOM_OFFSET> points from the bottom
		appWindowSize();

		// Making sure the app resizes when window resizes.
		Window.addResizeHandler(new ResizeHandler() 
		{
			@Override
			public void onResize(ResizeEvent event) 
			{
				appWindowSize();
			}
		});

		userImage.setVisible(false);
		userImage.addLoadHandler(new LoadHandler() 
		{
			@Override
			public void onLoad(final LoadEvent event) 
			{
				final int newWidth = 32;
				final com.google.gwt.dom.client.Element element = event
						.getRelativeElement();
				if (element.equals(userImage.getElement())) 
				{
					final int originalHeight = userImage.getOffsetHeight();
					final int originalWidth = userImage.getOffsetWidth();
					if (originalHeight > originalWidth) 
					{
						userImage.setHeight(newWidth + "px");
					} 
					else 
					{
						userImage.setWidth(newWidth + "px");
					}
					userImage.setVisible(true);
				}
			}
		});


		VerticalPanel mainVetricalPanel = new VerticalPanel();
		mainVetricalPanel.add(absolutePanel);
		mainVetricalPanel.setCellHeight(absolutePanel, "100%");
		mainVetricalPanel.setCellWidth(absolutePanel, "100%");
		absolutePanel.setSize("100%", "100%");
		mapComponent.getOpenLayersMap().setStyleName("blipnip-gwt-Menu-Map");
		mapComponent.setStyleName("blipnip-gwt-Menu-Map");

		mapComponent.setTitle("mapComponent");
		absolutePanel.add(mapComponent,0,0);
		mapComponent.setSize("100%", "100%");
		userUpperVerticalPanel.setSpacing(2);
		userUpperVerticalPanel.setTitle("userUpperVerticalPanel");
		userUpperVerticalPanel.setStyleName("blipnip-gwt-Menu-Bar-alt");
		userUpperVerticalPanel.setSize("100%", "60px");
		userUpperVerticalPanel.add(userUpperHorizontalPanel);
		userUpperVerticalPanel.setCellVerticalAlignment(userUpperHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		userUpperVerticalPanel.setCellHeight(userUpperHorizontalPanel, "100%");
		userUpperVerticalPanel.setCellWidth(userUpperHorizontalPanel, "100%");
		userUpperHorizontalPanel.setSize("100%", "45px");
		userImageHorizontalPanel.setSpacing(2);
		userImageHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		filterVerticalPanel.setSize("100%", "15px");
		userLowerHorizontalPanel.setCellHeight(filterVerticalPanel, "100%");
		userLowerHorizontalPanel.setCellWidth(filterVerticalPanel, "100%");
		filterListBox.addItem("View all");
		filterListBox.addItem("Offers");
		filterListBox.addItem("Users");
		filterListBox.setSelectedIndex(0);
		filterListBox.setVisibleItemCount(1);
		filterVerticalPanel.add(filterHorizontalPanel);
		filterVerticalPanel.setCellVerticalAlignment(filterHorizontalPanel, HasVerticalAlignment.ALIGN_BOTTOM);
		filterVerticalPanel.setCellHeight(filterHorizontalPanel, "100%");
		filterVerticalPanel.setCellWidth(filterHorizontalPanel, "100%");
		userUpperVerticalPanel.add(filterVerticalPanel);
		userUpperVerticalPanel.setCellHeight(filterVerticalPanel, "100%");
		userUpperVerticalPanel.setCellWidth(filterVerticalPanel, "100%");

		absolutePanel.add(userUpperVerticalPanel,0,0);

		userUpperHorizontalPanel.add(userImageHorizontalPanel);
		userImageHorizontalPanel.setSize("100%", "100%");
		userUpperHorizontalPanel.setCellWidth(userImageHorizontalPanel, "50%");
		userUpperHorizontalPanel.setCellHeight(userImageHorizontalPanel, "100%");
		userImageHorizontalPanel.add(userImage);
		userImageHorizontalPanel.setCellHeight(userImage, "100%");
		userImageHorizontalPanel.setCellWidth(userImage, "32px");
		userImageHorizontalPanel.setCellVerticalAlignment(userImage, HasVerticalAlignment.ALIGN_MIDDLE);
		userImage.setSize("32px", "32px");
		mainActionHorizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mainActionHorizontalPanel.setSpacing(2);
		mainActionHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		userUpperHorizontalPanel.add(mainActionHorizontalPanel);
		userUpperHorizontalPanel.setCellHorizontalAlignment(mainActionHorizontalPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		userUpperHorizontalPanel.setCellVerticalAlignment(mainActionHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		mainActionHorizontalPanel.setSize("100%", "100%");
		userUpperHorizontalPanel.setCellHeight(mainActionHorizontalPanel, "100%");
		userUpperHorizontalPanel.setCellWidth(mainActionHorizontalPanel, "45%");
		locationTextBox.setAlignment(TextAlignment.LEFT);
		locationTextBox.setText("Find a location...");

		mainActionHorizontalPanel.add(locationTextBox);
		mainActionHorizontalPanel.setCellHorizontalAlignment(locationTextBox, HasHorizontalAlignment.ALIGN_RIGHT);
		mainActionHorizontalPanel.setCellHeight(locationTextBox, "100%");
		mainActionHorizontalPanel.setCellWidth(locationTextBox, "100%");
		locationTextBox.setSize("80%", "70%");
		tglbtnQuickBlip.setEnabled(false);
		tglbtnQuickBlip.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/07_location_place/drawable-mdpi-blipnip/ic_action_place.png"));
		mainActionHorizontalPanel.setCellHeight(tglbtnQuickBlip, "100%");
		mainActionHorizontalPanel.setCellWidth(tglbtnQuickBlip, "32px");
		userImageHorizontalPanel.setCellHorizontalAlignment(tglbtnQuickBlip, HasHorizontalAlignment.ALIGN_CENTER);
		userImageHorizontalPanel.setCellHeight(tglbtnQuickBlip, "100%");
		userImageHorizontalPanel.setCellWidth(tglbtnQuickBlip, "32px");
		tglbtnQuickBlip.getDownFace().setText("Tap Map");
		tglbtnQuickBlip.getDownFace().setImage(new Image("icons/blipnip_app_icons/mainapp/07_location_place/drawable-mdpi-blipnip-button-down/ic_action_place.png"));
		userImageHorizontalPanel.add(tglbtnQuickBlip);
		tglbtnQuickBlip.setSize("32px", "34px");
		locationHorizontalPanel.setSpacing(2);

		userUpperHorizontalPanel.add(locationHorizontalPanel);
		locationHorizontalPanel.setSize("100%", "100%");
		userUpperHorizontalPanel.setCellHeight(locationHorizontalPanel, "100%");
		userUpperHorizontalPanel.setCellWidth(locationHorizontalPanel, "5%");
		pshbtnMyLocation.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/07_location_location_found/drawable-mdpi/ic_action_location_found.png"));
		mainActionHorizontalPanel.setCellVerticalAlignment(pshbtnMyLocation, HasVerticalAlignment.ALIGN_MIDDLE);
		mainActionHorizontalPanel.setCellHeight(pshbtnMyLocation, "100%");
		userImageHorizontalPanel.setCellHeight(pshbtnMyLocation, "100%");
		userImageHorizontalPanel.setCellWidth(pshbtnMyLocation, "32px");
		mainActionHorizontalPanel.setCellHorizontalAlignment(pshbtnMyLocation, HasHorizontalAlignment.ALIGN_RIGHT);
		locationHorizontalPanel.add(pshbtnMyLocation);
		locationHorizontalPanel.setCellHorizontalAlignment(pshbtnMyLocation, HasHorizontalAlignment.ALIGN_RIGHT);
		locationHorizontalPanel.setCellHeight(pshbtnMyLocation, "100%");
		locationHorizontalPanel.setCellWidth(pshbtnMyLocation, "32px");
		pshbtnMyLocation.setSize("32px", "34px");
		mainActionHorizontalPanel.setCellWidth(pshbtnMyLocation, "10%");
		userLowerVerticalPanel.setSpacing(4);

		userLowerVerticalPanel.setTitle("userLowerVerticalPanel");
		userLowerVerticalPanel.setStyleName("blipnip-gwt-Menu-Bar-alt");
		userLowerVerticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		userLowerVerticalPanel.setSize("100%", "60px");
		userLowerHorizontalPanel.setCellWidth(userImage, "5%");
		userLowerHorizontalPanel.setCellVerticalAlignment(userImage, HasVerticalAlignment.ALIGN_MIDDLE);
		userLowerHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		userLowerVerticalPanel.add(userLowerHorizontalPanel);
		userLowerVerticalPanel.setCellVerticalAlignment(userLowerHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		userLowerVerticalPanel.setCellHeight(userLowerHorizontalPanel, "100%");
		userLowerVerticalPanel.setCellWidth(userLowerHorizontalPanel, "100%");
		userLowerHorizontalPanel.setSize("100%", "100%");

		filterHorizontalPanel.setCellHorizontalAlignment(pshbtnMyLocation, HasHorizontalAlignment.ALIGN_RIGHT);
		filterHorizontalPanel.setCellHeight(pshbtnMyLocation, "100%");
		filterHorizontalPanel.setCellWidth(pshbtnMyLocation, "70%");
		userUpperHorizontalPanel.setCellHorizontalAlignment(filterHorizontalPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		userUpperHorizontalPanel.setCellVerticalAlignment(filterHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		filterHorizontalPanel.setSize("100%", "100%");
		userUpperHorizontalPanel.setCellHeight(filterHorizontalPanel, "100%");
		userUpperHorizontalPanel.setCellWidth(filterHorizontalPanel, "20%");
		filterHorizontalPanel.setCellHeight(labelOverviewLabel, "100%");
		filterHorizontalPanel.setCellWidth(labelOverviewLabel, "90%");
		filterVerticalPanel.setCellHeight(labelOverviewLabel, "100%");
		filterVerticalPanel.setCellWidth(labelOverviewLabel, "100%");
		filterHorizontalPanel.add(labelOverviewLabel);
		filterHorizontalPanel.setCellVerticalAlignment(labelOverviewLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		labelOverviewLabel.setStyleName("blipnip-gwt-label");
		labelOverviewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		userLowerHorizontalPanel.setCellHorizontalAlignment(labelOverviewLabel, HasHorizontalAlignment.ALIGN_CENTER);
		labelOverviewLabel.setSize("100%", "70%");
		userLowerHorizontalPanel.setCellHeight(labelOverviewLabel, "100%");
		userLowerHorizontalPanel.setCellWidth(labelOverviewLabel, "100%");
		userLowerHorizontalPanel.setCellVerticalAlignment(labelOverviewLabel, HasVerticalAlignment.ALIGN_MIDDLE);

		filterHorizontalPanel.add(filterListBox);
		filterHorizontalPanel.setCellHorizontalAlignment(filterListBox, HasHorizontalAlignment.ALIGN_RIGHT);
		filterHorizontalPanel.setCellVerticalAlignment(filterListBox, HasVerticalAlignment.ALIGN_MIDDLE);
		filterHorizontalPanel.setCellHeight(filterListBox, "100%");
		filterHorizontalPanel.setCellWidth(filterListBox, "10%");
		filterListBox.setSize("80px", "100%");

		userLowerHorizontalPanel.add(liveBlipControlHorizontalPanel);
		userLowerHorizontalPanel.setCellWidth(liveBlipControlHorizontalPanel, "5%");
		userLowerHorizontalPanel.setCellHeight(liveBlipControlHorizontalPanel, "100%");
		userLowerHorizontalPanel.setCellVerticalAlignment(liveBlipControlHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		liveBlipControlHorizontalPanel.setSize("100%", "100%");
		userLowerHorizontalPanel.setCellHeight(liveBlipUserImage, "100%");
		userLowerHorizontalPanel.setCellWidth(liveBlipUserImage, "32px");
		liveBlipControlHorizontalPanel.add(liveBlipUserImage);
		liveBlipControlHorizontalPanel.setCellHeight(liveBlipUserImage, "100%");
		liveBlipControlHorizontalPanel.setCellWidth(liveBlipUserImage, "32px");
		liveBlipControlHorizontalPanel.setCellVerticalAlignment(liveBlipUserImage, HasVerticalAlignment.ALIGN_MIDDLE);
		liveBlipUserImage.setSize("32px", "32px");
		pshbtnPrevious.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/02_navigation_previous_item/drawable-mdpi/ic_action_previous_item.png"));
		liveBlipControlHorizontalPanel.add(pshbtnPrevious);
		liveBlipControlHorizontalPanel.setCellHeight(pshbtnPrevious, "100%");
		liveBlipControlHorizontalPanel.setCellWidth(pshbtnPrevious, "33%");
		liveBlipControlHorizontalPanel.setCellVerticalAlignment(pshbtnPrevious, HasVerticalAlignment.ALIGN_MIDDLE);
		pshbtnPrevious.setStyleName("gwt-Button");
		userLowerHorizontalPanel.setCellHeight(pshbtnPrevious, "100%");
		userLowerHorizontalPanel.setCellWidth(pshbtnPrevious, "32px");
		pshbtnPrevious.setSize("32px", "34px");
		userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnPrevious, HasVerticalAlignment.ALIGN_MIDDLE);
		pshbtnNext.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/02_navigation_next_item/drawable-mdpi/ic_action_next_item.png"));
		liveBlipControlHorizontalPanel.add(pshbtnNext);
		liveBlipControlHorizontalPanel.setCellHeight(pshbtnNext, "100%");
		liveBlipControlHorizontalPanel.setCellWidth(pshbtnNext, "33%");
		liveBlipControlHorizontalPanel.setCellVerticalAlignment(pshbtnNext, HasVerticalAlignment.ALIGN_MIDDLE);
		pshbtnNext.setStyleName("gwt-Button");
		userLowerHorizontalPanel.setCellHeight(pshbtnNext, "100%");
		userLowerHorizontalPanel.setCellWidth(pshbtnNext, "32px");
		pshbtnNext.setSize("32px", "34px");
		userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnNext, HasVerticalAlignment.ALIGN_MIDDLE);
		pshbtnView.getUpFace().setHTML("View Details");
		pshbtnView.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/13_extra_actions_about/drawable-mdpi/ic_action_about.png"));
		liveBlipControlHorizontalPanel.add(pshbtnView);
		liveBlipControlHorizontalPanel.setCellHeight(pshbtnView, "100%");
		liveBlipControlHorizontalPanel.setCellWidth(pshbtnView, "33%");
		liveBlipControlHorizontalPanel.setCellVerticalAlignment(pshbtnView, HasVerticalAlignment.ALIGN_MIDDLE);
		pshbtnView.setStyleName("gwt-Button");


		userLowerHorizontalPanel.setCellHeight(pshbtnView, "100%");
		userLowerHorizontalPanel.setCellHorizontalAlignment(pshbtnView, HasHorizontalAlignment.ALIGN_RIGHT);
		pshbtnView.setSize("32px", "34px");
		userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnView, HasVerticalAlignment.ALIGN_MIDDLE);
		userLowerHorizontalPanel.setCellWidth(pshbtnView, "32px");

		userLowerHorizontalPanel.add(refreshHorizontalPanel);
		userLowerHorizontalPanel.setCellHeight(refreshHorizontalPanel, "100%");
		userLowerHorizontalPanel.setCellWidth(refreshHorizontalPanel, "95%");
		userLowerHorizontalPanel.setCellVerticalAlignment(refreshHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		refreshHorizontalPanel.setSize("100%", "100%");
		pshbtnRefresh.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_refresh/drawable-mdpi/ic_action_refresh.png"));
		refreshHorizontalPanel.add(pshbtnRefresh);
		refreshHorizontalPanel.setCellHorizontalAlignment(pshbtnRefresh, HasHorizontalAlignment.ALIGN_RIGHT);
		refreshHorizontalPanel.setCellHeight(pshbtnRefresh, "100%");
		refreshHorizontalPanel.setCellWidth(pshbtnRefresh, "100%");
		refreshHorizontalPanel.setCellVerticalAlignment(pshbtnRefresh, HasVerticalAlignment.ALIGN_MIDDLE);
		pshbtnRefresh.setStyleName("gwt-Button");
		pshbtnRefresh.setSize("32px", "34px");
		userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnRefresh, HasVerticalAlignment.ALIGN_MIDDLE);
		userLowerHorizontalPanel.setCellHorizontalAlignment(pshbtnRefresh, HasHorizontalAlignment.ALIGN_RIGHT);
		userLowerHorizontalPanel.setCellHeight(pshbtnRefresh, "100%");
		userLowerHorizontalPanel.setCellWidth(pshbtnRefresh, "32px");

		absolutePanel.add(userLowerVerticalPanel,0,(Window.getClientHeight() - OFFSET - (60))); //TODO

		contentLayoutPanel.add(mainVetricalPanel);
		mainVetricalPanel.setSize("100%", "100%");
		attribution.setStyleName("blipnip-gwt-label-attribution");
		attribution.setHTML(cloudMadeAttribution);
		userLowerVerticalPanel.setCellHeight(attributionPanel, "100%");
		userLowerVerticalPanel.setCellWidth(attributionPanel, "100%");
		attributionPanel.setStyleName("blipnip-gwt-Menu-Bar-Attribution");
		attributionPanel.setSize("100%", "20px");
		attributionPanel.setTitle("attributionPanel");
		attributionPanel.add(attribution);
		attributionPanel.setCellHorizontalAlignment(attribution, HasHorizontalAlignment.ALIGN_RIGHT);
		attributionPanel.setCellHeight(attribution, "100%");
		attributionPanel.setCellWidth(attribution, "100%");
		attribution.setSize("100%", "100%");
		absolutePanel.add(attributionPanel,0,(Window.getClientHeight()- OFFSET - (60+20)));
		contentLayoutPanel.add(logPanel);
		logPanel.setSize("100%", "100%");
		logPanel.add(appLogMenuBar);
		logPanel.setCellHeight(appLogMenuBar, "100%");
		logPanel.setCellWidth(appLogMenuBar, "100%");
		appLogMenuBar.setSize("100%", "30px");
		MenuBar menuBar = new MenuBar(true);

		mntmNewMenu = new MenuItem("New menu", false, menuBar);
		mntmNewMenu.setHTML("Menu");

		mntmAbout = new MenuItem("About", false, (Command) null);
		mntmAbout.setScheduledCommand(new MyCommand(mntmAbout));

		menuBar.addItem(mntmAbout);
		appLogMenuBar.addItem(mntmNewMenu);
		logTextArea.setVisibleLines(20);

		logPanel.add(logTextArea);
		logPanel.setCellHeight(logTextArea, "100%");
		logPanel.setCellWidth(logTextArea, "100%");
		logTextArea.setSize("100%", "100%");

		//System.out.println(contentLayoutPanel.getWidget(0));
		contentLayoutPanel.showWidget(0);
	}
	
	private void appWindowSize()
	{
		String appWidth  =  String.valueOf(Window.getClientWidth()  - OFFSET)+"px";
		String appHeight =  String.valueOf(Window.getClientHeight() - OFFSET)+"px"; //+"px"

		mapComponent.getOpenLayersMap().setSize(appWidth, appHeight);
		contentLayoutPanel.setSize(appWidth, appHeight);
		absolutePanel.setSize(appWidth, appHeight);

		for (int widgetCount=0;widgetCount<absolutePanel.getWidgetCount();widgetCount++)
		{					  
			if (absolutePanel.getWidget(widgetCount).getTitle().equals(mapComponent.getTitle()))
			{
				absolutePanel.setWidgetPosition(absolutePanel.getWidget(widgetCount), 0, 0);
			}

			if (absolutePanel.getWidget(widgetCount).getTitle().equals(userUpperVerticalPanel.getTitle()))
			{
				absolutePanel.setWidgetPosition(absolutePanel.getWidget(widgetCount), 0, 0);
			}

			if (absolutePanel.getWidget(widgetCount).getTitle().equals(attributionPanel.getTitle()))
			{
				absolutePanel.setWidgetPosition(absolutePanel.getWidget(widgetCount), 0, (Window.getClientHeight() - OFFSET - (80)));
			}

			if (absolutePanel.getWidget(widgetCount).getTitle().equals(userLowerVerticalPanel.getTitle()))
			{
				absolutePanel.setWidgetPosition(absolutePanel.getWidget(widgetCount), 0, (Window.getClientHeight() - OFFSET - (60)));
			}

		}

		//http://dantwining.co.uk/2010/03/01/gwt-openlayers-and-full-screen-maps/
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
			@Override
			public void execute()
			{
				mapComponent.getOpenLayersMap().getMap().updateSize();
			}
		});
	}

	/**
	 * Helper method to resize user image
	 * 
	 * @param loginInfo
	 */
	private void insertImage(BlipMaster loginInfo)
	{
		if (loginInfo.getPictureUrl() != null && !loginInfo.getPictureUrl().equals(""))
		{
			userImage.setUrl(loginInfo.getPictureUrl());
		}
	}

	/**
	 * This class is used for implementing the logic that a menu
	 * item has (used in the log panel). I think this breaks the mvp 
	 * logic a bit, because here, in the view, we are defining that the 
	 * view should have a widget which "reads" the command 
	 * - i.e. not just having a generic ClickHandler which can be applied 
	 * to pretty much anything (apart from menu items apparently).
	 * 
	 *  So for this lack of "genericness" this whole menu bar will be probably 
	 *  be removed (unless further investigation says otherwise). Leaving in for prototyping.
	 * 
	 * @author Thanos
	 *
	 */
	private class MyCommand implements ScheduledCommand
	{
		private final MenuItem item;

		//Conctructing using a MenuItem
		public MyCommand(MenuItem item) 
		{
			this.item = item;
		}

		@Override
		public void execute()
		{
			System.out.println(this.item.getClass());

			if (this.item.getClass().equals(MenuItem.class))
			{
				System.out.println("@MainAppView, @MyCommand, @execute, This is menu item: "+this.item.getText());
			}
		}
	}

	@Override
	public MapComponent getMapComponent()
	{
		return mapComponent;
	}

	@Override
	public Widget getMainAppLogMenubar()
	{
		return appLogMenuBar.asWidget();
	}

	@Override
	public HasClickHandlers getButtonPrevious()
	{
		return pshbtnPrevious;
	}

	@Override
	public HasClickHandlers getButtonNext()
	{
		return pshbtnNext;
	}

	@Override
	public HasClickHandlers getButtonView()
	{
		return pshbtnView;
	}

	@Override
	public void setUserInfoData(BlipMaster data)
	{
		this.loginInfo = data;
		if (data != null)
		{
			insertImage(this.loginInfo);
		}

	}

	@Override
	public void setBlipsOnArea(ArrayList<Blip> blipList)
	{
		// TODO Auto-generated method stub		
	}

	@Override
	public String getBlipOverview()
	{
		return labelOverviewLabel.getText();
	}

	@Override
	public void setBlipOverview(String overviewText)
	{
		this.labelOverviewLabel.setText(overviewText);
	}

	@Override
	public String getliveBlipUserImageURL()
	{
		return this.liveBlipUserImage.getUrl();
	}

	@Override
	public void setLiveBlipUserImageURL(String url)
	{
		liveBlipUserImage.setUrl(url);

	}

	@Override
	public HasClickHandlers getButtonRefresh()
	{
		return this.pshbtnRefresh;
	}

	@Override
	public HasClickHandlers getButtonCreateQuickBlip()
	{
		return this.tglbtnQuickBlip;
	}

	@Override
	public void setToggleButtonCreateQuickBlipIsDown(boolean isDown)
	{
		this.tglbtnQuickBlip.setDown(isDown);
	}

	@Override
	public void setToggleButtonCreateQuickBlipEnable(boolean enable)
	{
		this.tglbtnQuickBlip.setEnabled(enable);
	}

	@Override
	public Boolean getToggleButtonCreateQuickBlipIsDown()
	{
		return this.tglbtnQuickBlip.isDown();
	}

	@Override
	public HasClickHandlers getButtonMyLocation()
	{
		return this.pshbtnMyLocation;
	}

	@Override
	public void setButtonMyLocationEnable(boolean enable)
	{
		this.pshbtnMyLocation.setEnabled(enable);

	}

	@Override
	public HasClickHandlers getLocationTextBox()
	{
		return this.locationTextBox;
	}

	@Override
	public HasFocusHandlers getFilterListBoxClickHandlers()
	{
		return this.filterListBox;
	}

	@Override
	public HasChangeHandlers getFilterListBoxChangeHandlers()
	{
		return this.filterListBox;
	}

	@Override
	public int getFilterListBoxSelectedItemIndex()
	{
		return this.filterListBox.getSelectedIndex();
	}

	@Override
	public String getFilterListBoxSelectedItem(int index)
	{
		return this.filterListBox.getItemText(index); 
	}

	@Override
	public void setLocationTextBoxText(String locationText)
	{
		this.locationTextBox.setText(locationText);

	}

	@Override
	public void setFilterListBoxItemSelected(int itemSelected, boolean selected)
	{
		this.filterListBox.setItemSelected(itemSelected, selected);	
	}

	@Override
	public String getlocationTextBoxText()
	{
		return this.locationTextBox.getText();

	}

}
