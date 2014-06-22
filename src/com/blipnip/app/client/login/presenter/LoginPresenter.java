package com.blipnip.app.client.login.presenter;

import java.util.Date;

import com.blipnip.app.client.Presenter;
import com.blipnip.app.client.login.event.GuestLoginEvent;
import com.blipnip.app.client.login.service.LoginServiceAsync;
import com.blipnip.app.client.mainapp.event.MainAppEvent;
import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class LoginPresenter implements Presenter
{

	public interface Display 
	{
		Widget asWidget();
		
		HasClickHandlers getGoButton();
		HasClickHandlers getFacebookLoginButton();
		HasClickHandlers getGoogleLoginButton();
		HasClickHandlers getGuestLoginButton();
		HasClickHandlers getSignInLink();
		
		void setVisibleGuestLoginButton(boolean isVisible);
		
		String getSignInLinkTitle();
		void setUserInfoData(LoginInfo data);
	}

	private final LoginServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	
	private LoginInfo info = null;

	public LoginPresenter(LoginInfo loginInfo, LoginServiceAsync rpcService, HandlerManager eventBus, Display view) 
	{
		System.out.println("@LoginPresenter, @Constuctor, loginInfo= "+loginInfo);
		
		this.info = loginInfo;
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}
	
	/**
	 * 
	 * This method is used for binding the presenter with the view (through the interface).
	 * Since we are using the interface to access the view, we can easily swap views leaving
	 * all other code unaffected.
	 * 
	 */
	public void bind() 
	{	
		//Handlers for login buttons
		display.getFacebookLoginButton().addClickHandler(new LoginButtonHandler("FACEBOOK", rpcService));
		
		display.getGoogleLoginButton().addClickHandler(new LoginButtonHandler("GOOGLE", rpcService));
		
		// Handler for the "guest user", which is a user that does not need to login in order to access the main app. 
		display.getGuestLoginButton().addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@LoginPresenter, @bind, @getGuestLoginButton, onClick");
				eventBus.fireEvent(new GuestLoginEvent());	
			}
		});
		
		/**
		 * Testing behaviour - delete
		 */
		display.getSignInLink().addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				 System.out.println("@LoginPresenter, @bind, @getSignInLink, onClick");
				//eventBus.fireEvent(new LoginEvent());	
			}
		}
		);
		

		/**
		 * Handler (currently for Go! button) which navigates in the main app.
		 * Once its is Clicked, then a new session starts. Note, that the Go!
		 * button is initially disabled - it gets enabled only when login data is
		 * successfully retrieved.
		 */
		display.getGoButton().addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				rpcService.startSession(info, new AsyncCallback<LoginInfo>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						System.out.println("@LoginPresenter, @startSession, @onFailure, Could not start session.");
					}

					@Override
					public void onSuccess(LoginInfo result)
					{
						System.out.println("@LoginPresenter, @startSession, @onSuccess, result.getSessionId() : "+result.getSessionId());
						
						final long DURATION = 1000 * 60 * 60 * 24 * 1;
                        Date expires = new Date(System.currentTimeMillis() + DURATION);
                        Cookies.setCookie("sid", result.getSessionId(), expires, null, "/", false);
					}}); 
				
				
				System.out.println("@LoginPresenter, @bind, @getGoButton, onClick");
				eventBus.fireEvent(new MainAppEvent());	
			}
		});
	}

	/**
	 * The very first method that gets called.
	 */
	@Override
	public void go(final HasWidgets container) 
	{
		container.clear();
		container.add(display.asWidget());
		bind();
		
		/**
		 * Add any additional actions required when this class  is called (such as calling the service).
		 */
		
		/**
		 * Set the userInfoData for displaying the user name, image on the login page
		 * and should the object info not be null, enable the Go! buton, which is disabled by default.
		 */
		
		display.setUserInfoData(info);
		
		/**
		 * Enabling the guest button, because login in facebook/google
		 * via the Oauth library used is not working when used in tablets/mobile devices.
		 */
		if (GWT.isProdMode())
		{
			//change
			display.setVisibleGuestLoginButton(true);
		}
		else
		{
			display.setVisibleGuestLoginButton(true);
		}
	}
	
	/**
	 * Helper class that creates handlers for login buttons (facebook, google or native).
	 * 
	 * @author Thanos
	 *
	 */
	private class LoginButtonHandler implements ClickHandler 
	{
		private static final String TYPE_NATIVE       = "NATIVE";
		private static final String TYPE_FACEBOOK = "FACEBOOK";
		private static final String TYPE_GOOGLE     = "GOOGLE";
		
		private LoginManager login = null;
		private String loginType = null;
		//private LoginServiceAsync loginService;
		
		public LoginButtonHandler(String loginType, LoginServiceAsync loginService)
		{
			//this.loginService = loginService;
			this.loginType = loginType;
		}
		
		/**
		 * When clicking one of the login buttons, this handler kicks in which calls LoginManager.
		 * LoginManager, using the OAuth library, attempts to authenticate the user.
		 * 
		 * Upon successfull authentication the TokenReadyEvent is called, which takes us back
		 * to the MainAppController.
		 * 
		 */
		@Override
		public void onClick(ClickEvent event) 
		{
			
			if (loginType.toUpperCase().equals(TYPE_FACEBOOK) || loginType.toUpperCase().equals(TYPE_GOOGLE) || loginType.toUpperCase().equals(TYPE_NATIVE))
			{
				System.out.println("@LoginPresenter, @LoginButtonHandler, @onClick, Authorising");
				
				this.login = new LoginManager(loginType, eventBus);
			    this.login.doAuthorize();			
			}
			
			/**
			 * TODO - this is not needed because the LoginCallback of the LoginManager, 
			 * fires an event-bus event eventBus.fireEvent(new TokenReadyEvent(provider, token));
			 * which gives control back to the MainAppController
			 *  
			 */
			if (login != null)
			{
				System.out.println("@LoginPresenter, @LoginButtonHandler, @onClick, Firing LoginEvent (LEAVING BLANK, Not needed)");
				
				//eventBus.fireEvent(new LoginEvent());
			}
		}	
	}
}
