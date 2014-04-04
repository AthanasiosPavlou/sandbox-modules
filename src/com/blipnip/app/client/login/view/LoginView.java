package com.blipnip.app.client.login.view;

import com.blipnip.app.client.login.presenter.LoginPresenter;
import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;


public class LoginView  extends Composite implements LoginPresenter.Display
{
	private CellPanel cellPanelContainer;
	
	private final FlexTable loginOptionsflexTable = new FlexTable();
    private final Image facebookImage = new Image("images/large/sign_in_facebook.png");
    private final Image googleImage = new Image("images/large/sign_in_google.png");
    private final Image guestImage = new Image("images/small/guest.png");
    
	private final HTML devInfoHtml = new HTML("*Dev Note: You need to set <a href=\"http://blipniptest.com:8888/Blipnip.html?gwt.codesvr=blipniptest.com:9997#login\">"
																			+ "a domain name</a> (i.e. not a plain IP address)  in order to use Facebook Login.", true); 
    private final HTML prodInfoHtml = new HTML("Please use <a href=\"https://www.google.com/intl/en/chrome/browser/\">Chrome</a> for viewing on desktops"
    																		    + " or <a href=\"http://dolphin.com/\">Dolphin</a> for mobile devices. ", true); 
    
	private final Anchor signInLink = new Anchor("");
	private final Image profileImage = new Image();
    private final SimplePanel simplePanel = new SimplePanel();
    private final Button goButton = new Button("Go!");

    private final DecoratorPanel decoratorPanelWest = new DecoratorPanel();
    private final Label blipnipVersionLabel = new Label("");
    private final DecoratorPanel decoratorPanelSouth = new DecoratorPanel();
    private final SimplePanel logoSimplePanel = new SimplePanel();
    private final Image image = new Image("icons/blipnip_app_icons/mainapp/generic/logo/logo.png");
	 
	public LoginView()
	{
		SimplePanel contentLayoutPanel = new SimplePanel();
	    initWidget(contentLayoutPanel);
	    contentLayoutPanel.setSize("637px", "538px");
	  
	    VerticalPanel container = (VerticalPanel) defineUI();
	    
	    contentLayoutPanel.add(container);
	    
	    DockPanel loginDockPanel = new DockPanel();
	    loginDockPanel.setSpacing(5);
	    cellPanelContainer.add(loginDockPanel);
	    loginDockPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    loginDockPanel.setSize("586px", "519px");
	    blipnipVersionLabel.setStyleName("h1");
	    
	    loginDockPanel.add(blipnipVersionLabel, DockPanel.NORTH);
	    
	    loginDockPanel.add(decoratorPanelWest, DockPanel.CENTER);
	    decoratorPanelWest.setWidth("330px");
	    profileImage.setUrl("icons/blipnip_app_icons/mainapp/06_social_person/drawable-xxhdpi/ic_action_person.png");
	    
	    simplePanel.setWidget(profileImage);
	    profileImage.setSize("96px", "96px");
	    
	    VerticalPanel infoVerticalPanel = new VerticalPanel();
	    decoratorPanelWest.setWidget(infoVerticalPanel);
	    infoVerticalPanel.setSpacing(2);
	    infoVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    infoVerticalPanel.setSize("317px", "403px");
	    infoVerticalPanel.add(signInLink);
	    
	    infoVerticalPanel.add(logoSimplePanel);
	    logoSimplePanel.setSize("100%", "100%");
	    
	    logoSimplePanel.setWidget(image);
	    image.setSize("300px", "150px");
	    
	    infoVerticalPanel.add(simplePanel);
	    infoVerticalPanel.setCellVerticalAlignment(simplePanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    simplePanel.setSize("301px", "105px");
	    infoVerticalPanel.setCellHorizontalAlignment(simplePanel, HasHorizontalAlignment.ALIGN_CENTER);
	    infoVerticalPanel.setCellVerticalAlignment(profileImage, HasVerticalAlignment.ALIGN_MIDDLE);
	    infoVerticalPanel.setCellHorizontalAlignment(profileImage, HasHorizontalAlignment.ALIGN_CENTER);
	    
	    infoVerticalPanel.add(loginOptionsflexTable);
	    infoVerticalPanel.setCellVerticalAlignment(loginOptionsflexTable, HasVerticalAlignment.ALIGN_MIDDLE);
	    loginOptionsflexTable.setSize("110px", "27px");
	    Label label = new Label("");
	    loginOptionsflexTable.setWidget(0, 0, label);
	    label.setSize("70px", "18px");
	    facebookImage.setTitle("Login with Facebook");
	    loginOptionsflexTable.setWidget(1, 0, facebookImage);
	    facebookImage.setWidth("180px");
	    googleImage.setTitle("Login with Google");
	    loginOptionsflexTable.setWidget(2,0,googleImage);
	    googleImage.setWidth("180px");
	    guestImage.setTitle("Login as Guest");
	    loginOptionsflexTable.setWidget(3,0,guestImage);
	    loginOptionsflexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
	    loginOptionsflexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
	    loginOptionsflexTable.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
	    loginOptionsflexTable.getCellFormatter().setVerticalAlignment(3, 0, HasVerticalAlignment.ALIGN_MIDDLE);
	    loginOptionsflexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
	    loginOptionsflexTable.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_MIDDLE);
	    loginOptionsflexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
	    loginOptionsflexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE);
	    
	    goButton.setText("Not Signed In...");
	    infoVerticalPanel.add(goButton);
	    goButton.setSize("135px", "35px");
	    goButton.setEnabled(false);
	    loginDockPanel.add(decoratorPanelSouth, DockPanel.SOUTH);
	    decoratorPanelSouth.setWidget(devInfoHtml);
	    devInfoHtml.setWidth("372px");
	    
	    setVisibleGuestLoginButton(false);

	}
	
	/**
	 * Here we define our UI as we want for this implementation.
	 * 
	 * @return
	 */
	private CellPanel defineUI()
	{
		//TODO define all UI Stuff here - not needed if GWT Designer is used as it will do the work for us.

		cellPanelContainer = new VerticalPanel();		
	    cellPanelContainer.setSize("598px", "530px");
		return cellPanelContainer;
	}

	@Override
	public HasClickHandlers getFacebookLoginButton()
	{
		return facebookImage;
	}

	@Override
	public HasClickHandlers getGoogleLoginButton()
	{
		return googleImage;
	}
	
	@Override
	public HasClickHandlers getGuestLoginButton()
	{
		return guestImage;
	}
 
	@Override
	public void setUserInfoData(LoginInfo data)
	{
		if (data != null)
		{
			prepareLogin(data);
			
		}
		else
		{
			prepareLogin(null);
		}
	}
	
	/**
	 * Depending on whether loginInfo is null or 
	 * populated perform actions.
	 * 
	 * @param loginInfo
	 */
	private void prepareLogin(LoginInfo loginInfo)
	{
		if (loginInfo != null)
		{
			System.out.println("@LoginView, @onSuccessfulLoginWork, loginInfo->"+ loginInfo.toString());
		
			signInLink.setText(loginInfo.getNickname());
			
			if (loginInfo.getNickname() != null)
			{
				System.out.println("@LoginView @onSuccessfulLoginWork, getNickName ->"+loginInfo.getNickname());
				setupLinkForLogout(loginInfo);
				insertImage(loginInfo);
				goButton.setText("Start BlipNip");
				goButton.setEnabled(true);
			}
		}
		else
		{
			setupLinkForLogin(); 
		}
	}

	private void setupLinkForLogin() 
	{
		signInLink.setText("Please, sign in!");
		signInLink.setHref(null);
		signInLink.setVisible(true);
		signInLink.setTitle("Sign in");
	}

	private void setupLinkForLogout(final LoginInfo loginInfo) 
	{
		signInLink.setHref(loginInfo.getLogoutUrl());
		signInLink.setText("Sign out, "+loginInfo.getNickname());
		signInLink.setTitle("Sign out");
	}
	
	private void insertImage(LoginInfo loginInfo)
	{
		if (loginInfo.getPictureUrl() != null && !loginInfo.getPictureUrl().equals(""))
		{
			profileImage.setUrl(loginInfo.getPictureUrl());
			profileImage.setVisible(false);
			profileImage.addLoadHandler(new LoadHandler() 
			{
				@Override
				public void onLoad(final LoadEvent event) 
				{
					final int newWidth = 96;
					final com.google.gwt.dom.client.Element element = event
							.getRelativeElement();
					if (element.equals(profileImage.getElement())) 
					{
						final int originalHeight = profileImage.getOffsetHeight();
						final int originalWidth = profileImage.getOffsetWidth();
						if (originalHeight > originalWidth) 
						{
							profileImage.setHeight(newWidth + "px");
						} 
						else 
						{
							profileImage.setWidth(newWidth + "px");
						}
						profileImage.setVisible(true);
					}
				}
			});
		}
		
	}

	@Override
	public HasClickHandlers getGoButton()
	{
		return this.goButton;
	}



	@Override
	public HasClickHandlers getSignInLink()
	{
		return signInLink;
	}

	@Override
	public String getSignInLinkTitle()
	{
		return signInLink.getTitle();
	}

	@Override
	public void setVisibleGuestLoginButton(boolean isVisible)
	{
		if (isVisible)
		{
			this.guestImage.setVisible(isVisible);
			this.decoratorPanelSouth.setWidget(devInfoHtml);
			this.devInfoHtml.setWidth("372px");
		}
		else
		{
			this.guestImage.setVisible(isVisible);
			this.decoratorPanelSouth.setWidget(prodInfoHtml);
			this.devInfoHtml.setWidth("372px");
		}
		
	}
	
	

}
