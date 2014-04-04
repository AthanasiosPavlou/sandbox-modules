package com.blipnip.app.client.mainapp.view.sandbox;


import java.util.ArrayList;

import com.blipnip.app.client.mainapp.presenter.sandbox.MainAppPresenterSandbox;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MainAppViewSandbox extends Composite implements MainAppPresenterSandbox.MainAppDisplay
{
	private BlipMaster loginInfo = null;
	
	private final DeckPanel contentLayoutPanel = new DeckPanel();
	private final VerticalPanel logPanel = new VerticalPanel();
	private final DockPanel dockPanel = new DockPanel();
	
	private final TextArea logTextArea = new TextArea();
	private final MenuBar appMenuBar = new MenuBar(false);
	private final MapComponent mapComponent = new MapComponent();
	private final VerticalPanel userLowerVerticalPanel = new VerticalPanel();
	private final HorizontalPanel userLowerHorizontalPanel = new HorizontalPanel();
	private final Image userImage = new Image("icons/blipnip_app_icons/mainapp/13_extra_actions_help/drawable-mdpi/ic_action_help.png");
	private final MenuItem mntmNewMenu;
	private final MenuItem mntmAbout;
	private final Label labelOverviewLabel = new Label("No Blip Selected");
	private final VerticalPanel userUpperVerticalPanel = new VerticalPanel();
	private final HorizontalPanel userUpperHorizontalPanel = new HorizontalPanel();
	private final Image liveBlipUserImage = new Image("icons/blipnip_app_icons/mainapp/06_social_person/drawable-mdpi/ic_action_person.png");
	private final HorizontalPanel mainActionHorizontalPanel = new HorizontalPanel();
	private final HorizontalPanel confirmHorizontalPanel = new HorizontalPanel();
	private final HorizontalPanel userImageHorizontalPanel = new HorizontalPanel();
	
	private final ToggleButton tglbtnQuickBlip = new ToggleButton("Quick Blip");
	private final ToggleButton tglbtnCreateNewBlip = new ToggleButton("New Blip...");
	private final ToggleButton tglbtnSetBlipTravelPath = new ToggleButton("Travel Path...");
	private final ToggleButton tglbtnSetBlipContent = new ToggleButton("Blip Content...");
	private final PushButton pshbtnDeleteBlip = new PushButton("Delete");
	private final PushButton pshbtnPreview = new PushButton("Preview");
	
	private final PushButton pshbtnPrevious = new PushButton("Prev");
	private final PushButton pshbtnNext = new PushButton("Next");
	private final PushButton pshbtnView = new PushButton("View Details");
	private final PushButton pshbtnRefresh = new PushButton("Refresh");
	private final PushButton pshbtnMyLocation = new PushButton("My Location");
	
	
	
	public MainAppViewSandbox()
	{
		contentLayoutPanel.setAnimationEnabled(false);
		
		initWidget(contentLayoutPanel);
		
	    //contentLayoutPanel.setSize("100%", "480px");
		contentLayoutPanel.setSize("100%", String.valueOf(Window.getClientHeight()-50)+"px");
		
		
	    tglbtnCreateNewBlip.setVisible(false);
	    tglbtnSetBlipTravelPath.setVisible(false);
	    tglbtnSetBlipContent.setVisible(false);
	    pshbtnDeleteBlip.setVisible(false);
	    pshbtnPreview.setVisible(false);
	    
	    VerticalPanel mainVetricalPanel = new VerticalPanel();
	    mainVetricalPanel.add(dockPanel);
	    dockPanel.setSize("100%", "100%");
	    userUpperVerticalPanel.setSpacing(2);
	    userUpperVerticalPanel.setStyleName("blipnip-gwt-Menu");
	    dockPanel.add(userUpperVerticalPanel, DockPanel.NORTH);
	    dockPanel.setCellHeight(userUpperVerticalPanel, "35px");
	    dockPanel.setCellWidth(userUpperVerticalPanel, "100%");
	    dockPanel.setCellVerticalAlignment(userUpperVerticalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    userUpperVerticalPanel.setSize("100%", "100%");
	    userUpperHorizontalPanel.setSpacing(2);
	    
	    userUpperVerticalPanel.add(userUpperHorizontalPanel);
	    userUpperVerticalPanel.setCellHorizontalAlignment(userUpperHorizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
	    userUpperVerticalPanel.setCellVerticalAlignment(userUpperHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    userUpperVerticalPanel.setCellHeight(userUpperHorizontalPanel, "100%");
	    userUpperVerticalPanel.setCellWidth(userUpperHorizontalPanel, "100%");
	    userUpperHorizontalPanel.setSize("100%", "30px");
	    userLowerHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    userImageHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    userImageHorizontalPanel.setSpacing(5);
	    
	    userUpperHorizontalPanel.add(userImageHorizontalPanel);
	    userUpperHorizontalPanel.setCellHorizontalAlignment(userImageHorizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
	    userImageHorizontalPanel.setSize("64px", "100%");
	    userUpperHorizontalPanel.setCellWidth(userImageHorizontalPanel, "32px");
	    userUpperHorizontalPanel.setCellHeight(userImageHorizontalPanel, "100%");
	    userImageHorizontalPanel.add(userImage);
	    userImageHorizontalPanel.setCellVerticalAlignment(userImage, HasVerticalAlignment.ALIGN_MIDDLE);
	    userImageHorizontalPanel.setCellHorizontalAlignment(userImage, HasHorizontalAlignment.ALIGN_CENTER);
	    userImage.setSize("32px", "32px");
	    userLowerHorizontalPanel.setCellWidth(userImage, "5%");
	    userLowerHorizontalPanel.setCellVerticalAlignment(userImage, HasVerticalAlignment.ALIGN_MIDDLE);
	    mainActionHorizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    mainActionHorizontalPanel.setSpacing(5);
	    mainActionHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    
	    userUpperHorizontalPanel.add(mainActionHorizontalPanel);
	    userUpperHorizontalPanel.setCellVerticalAlignment(mainActionHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    mainActionHorizontalPanel.setSize("100%", "100%");
	    userUpperHorizontalPanel.setCellHeight(mainActionHorizontalPanel, "100%");
	    userUpperHorizontalPanel.setCellWidth(mainActionHorizontalPanel, "60%");
	    pshbtnMyLocation.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/07_location_location_found/drawable-mdpi/ic_action_location_found.png"));
	    
	    mainActionHorizontalPanel.add(pshbtnMyLocation);
	    mainActionHorizontalPanel.setCellHorizontalAlignment(pshbtnMyLocation, HasHorizontalAlignment.ALIGN_CENTER);
	    mainActionHorizontalPanel.setCellVerticalAlignment(pshbtnMyLocation, HasVerticalAlignment.ALIGN_MIDDLE);
	    mainActionHorizontalPanel.setCellHeight(pshbtnMyLocation, "100%");
	    pshbtnMyLocation.setSize("64px", "100%");
	    mainActionHorizontalPanel.setCellWidth(pshbtnMyLocation, "5%");
	    tglbtnQuickBlip.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/07_location_place/drawable-mdpi-blipnip/ic_action_place.png"));
	    tglbtnQuickBlip.getDownFace().setText("Tap Map To Create!");
	    
	    mainActionHorizontalPanel.add(tglbtnQuickBlip);
	    mainActionHorizontalPanel.setCellHeight(tglbtnQuickBlip, "100%");
	    mainActionHorizontalPanel.setCellVerticalAlignment(tglbtnQuickBlip, HasVerticalAlignment.ALIGN_MIDDLE);
	    mainActionHorizontalPanel.setCellHorizontalAlignment(tglbtnQuickBlip, HasHorizontalAlignment.ALIGN_CENTER);
	    mainActionHorizontalPanel.setCellWidth(tglbtnQuickBlip, "5%");
	    tglbtnQuickBlip.setSize("64px", "100%");
	    tglbtnCreateNewBlip.getUpFace().setText("New Blip...");
	    tglbtnCreateNewBlip.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_new/drawable-mdpi/ic_action_new.png"));
	    tglbtnCreateNewBlip.getDownFace().setText("Tap Map to Create!");
	    
	    mainActionHorizontalPanel.add(tglbtnCreateNewBlip);
	    mainActionHorizontalPanel.setCellVerticalAlignment(tglbtnCreateNewBlip, HasVerticalAlignment.ALIGN_MIDDLE);
	    mainActionHorizontalPanel.setCellHorizontalAlignment(tglbtnCreateNewBlip, HasHorizontalAlignment.ALIGN_CENTER);
	    mainActionHorizontalPanel.setCellHeight(tglbtnCreateNewBlip, "100%");
	    mainActionHorizontalPanel.setCellWidth(tglbtnCreateNewBlip, "30%");
	    tglbtnCreateNewBlip.setSize("80%", "100%");
	    
	    
	    tglbtnSetBlipTravelPath.getUpFace().setText("Draw Path...");
	    tglbtnSetBlipTravelPath.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_edit/drawable-mdpi/ic_action_edit.png"));
	    tglbtnSetBlipTravelPath.getDownFace().setText("Tap Map To Draw!");
	    
	    mainActionHorizontalPanel.add(tglbtnSetBlipTravelPath);
	    mainActionHorizontalPanel.setCellVerticalAlignment(tglbtnSetBlipTravelPath, HasVerticalAlignment.ALIGN_MIDDLE);
	    mainActionHorizontalPanel.setCellHorizontalAlignment(tglbtnSetBlipTravelPath, HasHorizontalAlignment.ALIGN_CENTER);
	    tglbtnSetBlipTravelPath.setSize("80%", "100%");
	    mainActionHorizontalPanel.setCellHeight(tglbtnSetBlipTravelPath, "100%");
	    mainActionHorizontalPanel.setCellWidth(tglbtnSetBlipTravelPath, "30%");
	    tglbtnSetBlipContent.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/05_content_new_picture/drawable-mdpi/ic_action_new_picture.png"));
	    	    tglbtnSetBlipContent.getDownFace().setText("Publishing Blip!");
	    
	    	    mainActionHorizontalPanel.add(tglbtnSetBlipContent);
	    	    mainActionHorizontalPanel.setCellVerticalAlignment(tglbtnSetBlipContent, HasVerticalAlignment.ALIGN_MIDDLE);
	    	    mainActionHorizontalPanel.setCellHorizontalAlignment(tglbtnSetBlipContent, HasHorizontalAlignment.ALIGN_CENTER);
	    	    tglbtnSetBlipContent.setSize("80%", "100%");
	    	    mainActionHorizontalPanel.setCellHeight(tglbtnSetBlipContent, "100%");
	    	    mainActionHorizontalPanel.setCellWidth(tglbtnSetBlipContent, "30%");
	    confirmHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    confirmHorizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    confirmHorizontalPanel.setSpacing(5);
	    
	    userUpperHorizontalPanel.add(confirmHorizontalPanel);
	    userUpperHorizontalPanel.setCellVerticalAlignment(confirmHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    userUpperHorizontalPanel.setCellHorizontalAlignment(confirmHorizontalPanel, HasHorizontalAlignment.ALIGN_RIGHT);
	    confirmHorizontalPanel.setSize("70%", "100%");
	    userUpperHorizontalPanel.setCellWidth(confirmHorizontalPanel, "40%");
	    userUpperHorizontalPanel.setCellHeight(confirmHorizontalPanel, "100%");
	    userUpperHorizontalPanel.setCellHorizontalAlignment(pshbtnDeleteBlip, HasHorizontalAlignment.ALIGN_RIGHT);
	    userUpperHorizontalPanel.setCellHeight(pshbtnDeleteBlip, "100%");
	    userUpperHorizontalPanel.setCellWidth(pshbtnDeleteBlip, "15%");
	    pshbtnDeleteBlip.getUpFace().setText("Delete");
	    pshbtnDeleteBlip.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_discard/drawable-mdpi/ic_action_discard.png"));
	    pshbtnDeleteBlip.getDownFace().setText("Delete Selection");
	    pshbtnDeleteBlip.setStyleName("gwt-Button");
	    confirmHorizontalPanel.add(pshbtnDeleteBlip);
	    confirmHorizontalPanel.setCellHeight(pshbtnDeleteBlip, "100%");
	    confirmHorizontalPanel.setCellWidth(pshbtnDeleteBlip, "50%");
	    pshbtnDeleteBlip.setSize("80%", "100%");
	    userUpperHorizontalPanel.setCellHorizontalAlignment(pshbtnPreview, HasHorizontalAlignment.ALIGN_RIGHT);
	    pshbtnPreview.getUpFace().setText("Preview");
	    pshbtnPreview.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/09_media_play_over_video/drawable-mdpi/ic_action_play_over_video.png"));
	    pshbtnPreview.getDownFace().setText("Preview Blip Path");
	    pshbtnPreview.setStyleName("gwt-Button");
	    confirmHorizontalPanel.add(pshbtnPreview);
	    confirmHorizontalPanel.setCellHeight(pshbtnPreview, "100%");
	    confirmHorizontalPanel.setCellWidth(pshbtnPreview, "50%");
	    pshbtnPreview.setSize("80%", "100%");
	    userUpperHorizontalPanel.setCellHeight(pshbtnPreview, "100%");
	    userUpperHorizontalPanel.setCellWidth(pshbtnPreview, "5%");
	    dockPanel.add(mapComponent, DockPanel.CENTER);
	    dockPanel.setCellHeight(mapComponent, "100%");
	    dockPanel.setCellWidth(mapComponent, "100%");
	    dockPanel.setCellVerticalAlignment(mapComponent, HasVerticalAlignment.ALIGN_MIDDLE);
	    mapComponent.setSize("100%", "100%");
	    
	    userLowerVerticalPanel.setStyleName("blipnip-gwt-Menu");
	    userLowerVerticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    userLowerVerticalPanel.setSpacing(5);
	    
	    dockPanel.add(userLowerVerticalPanel, DockPanel.SOUTH);
	    dockPanel.setCellVerticalAlignment(userLowerVerticalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    userLowerVerticalPanel.setSize("100%", "100%");
	    userLowerHorizontalPanel.setSpacing(5);
	    
	    userLowerVerticalPanel.add(userLowerHorizontalPanel);
	    userLowerVerticalPanel.setCellVerticalAlignment(userLowerHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    userLowerVerticalPanel.setCellHeight(userLowerHorizontalPanel, "100%");
	    userLowerVerticalPanel.setCellWidth(userLowerHorizontalPanel, "100%");
	    userLowerVerticalPanel.setCellHorizontalAlignment(userLowerHorizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
	    userLowerHorizontalPanel.setSize("100%", "30px");
	    
	    userLowerHorizontalPanel.add(liveBlipUserImage);
	    userLowerHorizontalPanel.setCellHeight(liveBlipUserImage, "100%");
	    userLowerHorizontalPanel.setCellWidth(liveBlipUserImage, "32px");
	    liveBlipUserImage.setSize("32px", "32px");
	    pshbtnPrevious.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/02_navigation_previous_item/drawable-mdpi/ic_action_previous_item.png"));
	    pshbtnPrevious.setStyleName("gwt-Button");
	    
	    userLowerHorizontalPanel.add(pshbtnPrevious);
	    userLowerHorizontalPanel.setCellHeight(pshbtnPrevious, "100%");
	    userLowerHorizontalPanel.setCellWidth(pshbtnPrevious, "32px");
	    pshbtnPrevious.setSize("32px", "100%");
	    userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnPrevious, HasVerticalAlignment.ALIGN_MIDDLE);
	    pshbtnNext.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/02_navigation_next_item/drawable-mdpi/ic_action_next_item.png"));
	    pshbtnNext.setStyleName("gwt-Button");
	    
	    userLowerHorizontalPanel.add(pshbtnNext);
	    userLowerHorizontalPanel.setCellHeight(pshbtnNext, "100%");
	    userLowerHorizontalPanel.setCellWidth(pshbtnNext, "32px");
	    pshbtnNext.setSize("32px", "100%");
	    userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnNext, HasVerticalAlignment.ALIGN_MIDDLE);
	    labelOverviewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    userLowerHorizontalPanel.add(labelOverviewLabel);
	    userLowerHorizontalPanel.setCellHorizontalAlignment(labelOverviewLabel, HasHorizontalAlignment.ALIGN_CENTER);
	    labelOverviewLabel.setSize("100px", "80%");
	    userLowerHorizontalPanel.setCellHeight(labelOverviewLabel, "100%");
	    userLowerHorizontalPanel.setCellWidth(labelOverviewLabel, "100%");
	    userLowerHorizontalPanel.setCellVerticalAlignment(labelOverviewLabel, HasVerticalAlignment.ALIGN_MIDDLE);
	    pshbtnView.getUpFace().setHTML("View Details");
	    pshbtnView.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/13_extra_actions_about/drawable-mdpi/ic_action_about.png"));
	    pshbtnView.setStyleName("gwt-Button");
	    userLowerHorizontalPanel.add(pshbtnView);
	    
	    
	    userLowerHorizontalPanel.setCellHeight(pshbtnView, "100%");
	    userLowerHorizontalPanel.setCellHorizontalAlignment(pshbtnView, HasHorizontalAlignment.ALIGN_CENTER);
	    pshbtnView.setSize("32px", "100%");
	    userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnView, HasVerticalAlignment.ALIGN_MIDDLE);
	    userLowerHorizontalPanel.setCellWidth(pshbtnView, "32px");
	    pshbtnRefresh.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_refresh/drawable-mdpi/ic_action_refresh.png"));
	    pshbtnRefresh.setStyleName("gwt-Button");
	    
	    userLowerHorizontalPanel.add(pshbtnRefresh);
	    pshbtnRefresh.setSize("32px", "100%");
	    userLowerHorizontalPanel.setCellVerticalAlignment(pshbtnRefresh, HasVerticalAlignment.ALIGN_MIDDLE);
	    userLowerHorizontalPanel.setCellHorizontalAlignment(pshbtnRefresh, HasHorizontalAlignment.ALIGN_CENTER);
	    userLowerHorizontalPanel.setCellHeight(pshbtnRefresh, "100%");
	    userLowerHorizontalPanel.setCellWidth(pshbtnRefresh, "32px");
	    
	    contentLayoutPanel.add(mainVetricalPanel);
	    mainVetricalPanel.setSize("100%", "100%");
	    	
	    contentLayoutPanel.add(logPanel);
	    logPanel.setSize("100%", "100%");
	    logPanel.add(appMenuBar);
	    appMenuBar.setSize("99.8%", "30px");
	    MenuBar menuBar = new MenuBar(true);
	    
	    mntmNewMenu = new MenuItem("New menu", false, menuBar);
	    mntmNewMenu.setHTML("Menu");
	    
	    mntmAbout = new MenuItem("About", false, (Command) null);
	    mntmAbout.setScheduledCommand(new MyCommand(mntmAbout));
	    
	    menuBar.addItem(mntmAbout);
	    appMenuBar.addItem(mntmNewMenu);
	    logTextArea.setVisibleLines(20);
	    
	    logPanel.add(logTextArea);
	    logPanel.setCellHeight(logTextArea, "100%");
	    logPanel.setCellWidth(logTextArea, "100%");
	    logTextArea.setSize("100%", "100%");
	    
	    //System.out.println(contentLayoutPanel.getWidget(0));
	    contentLayoutPanel.showWidget(0);
	}
	
	private void insertImage(BlipMaster loginInfo)
	{
		if (loginInfo.getPictureUrl() != null && !loginInfo.getPictureUrl().equals(""))
		{
			userImage.setUrl(loginInfo.getPictureUrl());
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
		}
	}

	/**
	 * This class is used for holding the logic of the command that a menu
	 * item is holding. I think this breaks the mvp logic a bit, because here,
	 * in the presenter, we are defining that the view should have a widget
	 * which "reads" the command - i.e. not just having a more generic
	 * ClickHandler which can be applied to pretty much anything (apart from
	 * menu items apparently).
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

		  //MenuItem
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
	public Widget getMainAppMenubar()
	{
		return appMenuBar.asWidget();
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
	public HasClickHandlers getToggleButtonMapNavigation()
	{
		return tglbtnSetBlipTravelPath;
	}

	@Override
	public Boolean getToggleButtonMapNavigationIsDown()
	{
		return tglbtnSetBlipTravelPath.isDown();
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
	public HasClickHandlers getToggleButtonCreateNewBlip()
	{
		return tglbtnCreateNewBlip;
	}
	
		@Override
	public Boolean getToggleButtonCreateNewBlipIsDown()
	{
		return tglbtnCreateNewBlip.isDown();
	}

	@Override
	public HasClickHandlers getButtonPreviewBlip()
	{
		return pshbtnPreview;
	}

	@Override
	public HasClickHandlers getButtonDeleteBlip()
	{
		return pshbtnDeleteBlip;
	}

	@Override
	public HasClickHandlers getToggleButtonSetTravelPath()
	{
		return tglbtnSetBlipTravelPath;
	}

	@Override
	public Boolean getToggleButtonSetTravelPathIsDown()
	{
		return tglbtnSetBlipTravelPath.isDown();
	}

	@Override
	public void setToggleButtonSetTravelPathIsDown(boolean isDown)
	{
		tglbtnSetBlipTravelPath.setDown(isDown);		
	}

	@Override
	public HasClickHandlers getToggleButtonSetBipContent()
	{
		return tglbtnSetBlipContent;
	}

	@Override
	public Boolean getToggleButtonSetBipContentIsDown()
	{
		return tglbtnSetBlipContent.isDown();
	}

	@Override
	public void setToggleButtonCreateNewBlipIsDown(boolean isDown)
	{
		tglbtnCreateNewBlip.setDown(isDown);
		
	}

	@Override
	public void setToggleButtonSetBipContentIsDown(boolean isDown)
	{
		tglbtnSetBlipContent.setDown(isDown);
		
	}

	@Override
	public void setToggleButtonCreateNewBlipEnabled(boolean enable)
	{
		tglbtnCreateNewBlip.setEnabled(enable);
	}

	@Override
	public void setToggleButtonSetBipContentEnabled(boolean enable)
	{
		tglbtnSetBlipContent.setEnabled(enable);
	}

	@Override
	public void setToggleButtonSetTravelPathEnable(boolean enable)
	{
		tglbtnSetBlipTravelPath.setEnabled(enable);
	}

	@Override
	public void setButtonDeleteBlipEnable(boolean enable)
	{
		this.pshbtnDeleteBlip.setEnabled(enable);
	}

	@Override
	public void setButtonPreviewBlipEnable(boolean enabled)
	{
		this.pshbtnPreview.setEnabled(enabled);
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
		// TODO Auto-generated method stub
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

}

