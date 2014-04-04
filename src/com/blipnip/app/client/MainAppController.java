package com.blipnip.app.client;

import com.blipnip.app.client.Presenter;
import com.blipnip.app.client.login.event.GuestLoginEvent;
import com.blipnip.app.client.login.event.GuestLoginEventHandler;
import com.blipnip.app.client.login.event.LoginEvent;
import com.blipnip.app.client.login.event.LoginEventHandler;
import com.blipnip.app.client.login.event.TokenReadyEvent;
import com.blipnip.app.client.login.event.TokenReadyEventHandler;
import com.blipnip.app.client.login.presenter.LoginPresenter;
import com.blipnip.app.client.login.service.LoginServiceAsync;
import com.blipnip.app.client.login.view.LoginView;
import com.blipnip.app.client.mainapp.event.MainAppEvent;
import com.blipnip.app.client.mainapp.event.MainAppEventHandler;
import com.blipnip.app.client.mainapp.presenter.MainAppPresenter;
import com.blipnip.app.client.mainapp.service.MainAppServiceAsync;
import com.blipnip.app.client.mainapp.view.MainAppView;
import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class MainAppController implements Presenter, ValueChangeHandler<String> 
{

	private final String LOGIN_PAGE   = "login";
	private final String MAIN_PAGE    = "main";
	private final HandlerManager eventBus;
	
	private HasWidgets container;
	
	private static LoginInfo loginInfo;
	
	private final LoginServiceAsync rpcLoginService;
	private final MainAppServiceAsync rpcMainAppService;
	
	public LoginInfo getLoginInfo()
	{
		return MainAppController.loginInfo;
	}
	
	public void setLoginInfo(LoginInfo loginInfo)
	{
		MainAppController.loginInfo = loginInfo;
	}
	
	public MainAppController(LoginServiceAsync rpcLoginService, MainAppServiceAsync rpcMainAppService, HandlerManager eventBus)
	{
		MainAppController.loginInfo  = null;
		this.rpcMainAppService = rpcMainAppService;
		this.rpcLoginService      = rpcLoginService;
		this.eventBus                = eventBus;
		bind();
	}
	
	private void bind() 
	{
		//Register to receive History events (which will take care of the app's navigation)
		History.addValueChangeHandler(this);

		/**
		 * This handler is not needed because the login page is launched, either in the
		 * very beginning (method go() called directly from BlipNipApp) or by the initToken
		 * which creates a new LoginPresenter object with the retrieved login details.
		 */
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() 
		{
			@Override
			public void onLogin(LoginEvent event)
			{
				System.out.println("@MainAppController, @bind, LoginEvent");
				launchLoginPage();
			}
		});  
		
		/**
		 * This handler will be called by the LoginManager, once a login token 
		 * is successfully retrieved. The event will carry the token and pass
		 * it in the initToken method which will re-load the LoginPresenter with the login info.
		 * 
		 */
		eventBus.addHandler(TokenReadyEvent.TYPE, new TokenReadyEventHandler() 
		{
			@Override
			public void onTokenReady(TokenReadyEvent event)
			{
				System.out.println("@MainAppController, @bind, TokenReadyEvent");
				
				initToken(TokenReadyEvent.getProvider(), TokenReadyEvent.getToken());
			}
		});  
		
		/**
		 * This event is called when we navigate to the main app page
		 * currently fired from the Go! button of the LoginPresenter.
		 * 
		 */
		eventBus.addHandler(MainAppEvent.TYPE, new MainAppEventHandler()
		{
			@Override
			public void onMainApp(MainAppEvent event)
			{
				System.out.println("@MainAppController, @bind, MainAppEvent");
				launchMainAppPage();
				
			}}
		);
		
		eventBus.addHandler(GuestLoginEvent.TYPE, new GuestLoginEventHandler()
		{
			@Override
			public void onGuestLogin(GuestLoginEvent event)
			{
				LoginInfo info = new LoginInfo();
				
				info.setLoggedStatus(false);
				info.setEmailAddress("guest@guest.com");
				info.setUserName("guest");
				info.setNickname("Guest");
				info.setPictureUrl("icons/blipnip_app_icons/mainapp/06_social_person/drawable-mdpi/ic_action_person.png");
				
				MainAppController.loginInfo = info;
				System.out.println("@MainAppController, @bind, GuestLoginEvent");
				launchMainAppPage();
				
			}}
		);
		
	}
	
	/**
	 * When the signInLink is clicked - which means that the user wants
	 * to sign out, then the session needs to be destroyed. Thats why
	 * we are adding this hander here.
	 */
	private void removeSession()
	{

			System.out.println("@MainAppController, @handleSession, preparing to invalidate session...");
			rpcLoginService.removeUserSession(new AsyncCallback<String>()
			{
				@Override
				public void onFailure(Throwable caught)
				{
					System.out.println("@MainAppController, @getSignInLink, @removeUserSession, @onFailure: "+caught.getMessage());						
				}

				@Override
				public void onSuccess(String result)
				{
					System.out.println("@MainAppController, @getSignInLink, @removeUserSession, @onSuccess"+result);	
				}
			});
	}

	/**
	 * Helper method to launch login page
	 */
	private void launchLoginPage() 
	{
		History.newItem(LOGIN_PAGE);
	}
	
	/**
	 * Helper method to launch main page
	 */
	private void launchMainAppPage() 
	{
		History.newItem(MAIN_PAGE);
	}
	
	/**
	 * Having obtained the token (not to be confused with the navigation token), we use the service to get user login info 
	 * and we populate the MainAppController.loginInfo.
	 * 
	 * Upon successfull retrieval of the info, the LoginPresenter is initialized with the loginInfo.
	 * 
	 * @param provider
	 * @param token
	 */
	private void initToken(String provider, String token)
	{
		System.out.println("@MainAppController, @initToken, Initializing token");
			
			if (token != null && !token.isEmpty())
			{
				boolean isProduction = false;
				if (GWT.isProdMode())
	    			{
					isProduction = true;
	    			}
	    			else
	    			{
	    				isProduction = false;
	    			}			
				rpcLoginService.retrieveUserInfo(isProduction, provider, token, new AsyncCallback<LoginInfo>() 
				{
					@Override
					public void onFailure(final Throwable caught) 
					{  
						MainAppController.loginInfo = null;
						System.out.println("@MainAppController, @initToken, @loginService.retrieveUserInfo -> On Failure -> Not implemented...");
					}

					@Override
					public void onSuccess(final LoginInfo loginInfo) 
					{
						MainAppController.loginInfo = loginInfo;
						new LoginPresenter(loginInfo, rpcLoginService, eventBus, new LoginView()).go(container);
					}
				});
			}
	}
	
	
	/**
	 * 
	 * Whenever a History.newItem(XYZ_PAGE); is executed an event is created which calls
	 * this method. Then, by getting the event value, for example if newItem(LOGIN_PAGE)
	 * the value will be login, we start the appropriate presenter.
	 * 
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event)
	{
		String navigationToken = event.getValue();

		if (navigationToken != null) 
		{
			Presenter presenter = null;
			
			System.out.println("@MainAppController, @onValueChange presenter->"+navigationToken);
			
			/**
			 * This condition will fire only on the very first time that the app loads (or when 
			 * a user manually navigates to the url #login or when a redirect occurs for some 
			 * reason - not expected to happen with current usage scenarios).
			 */
			if (navigationToken.equals(LOGIN_PAGE)) 
			{
				System.out.println("@MainAppController, @onValueChange, "+LOGIN_PAGE);
				
				removeSession();
				
				if (MainAppController.loginInfo != null)
				{
					/**
					 * The loginInfo.setLoggedStatus is set, only from retrieveUserInfo service.
					 */
					if (MainAppController.loginInfo.isLogged())
					{
						System.out.println("@MainAppController, @MainAppController.loginInfo.isLogged(), "+MainAppController.loginInfo.isLogged());
						
						presenter = new LoginPresenter(MainAppController.loginInfo, rpcLoginService, eventBus, new LoginView());	
					}
					else
					{
						System.out.println("@MainAppController, @MainAppController.loginInfo.isLogged(), "+MainAppController.loginInfo.isLogged()); 
						
						presenter = new LoginPresenter(null, rpcLoginService, eventBus, new LoginView());
					}	
				}
				else
				{
					//On the first time app launches (or on page refresh) LoginInfo will not be available, therefore null is passed.
					presenter = new LoginPresenter(null, rpcLoginService, eventBus, new LoginView());	
				}				
			}
			
			/**
			 * This condition will fire when a user clicks the Go! button from the login page,
			 * or when a redirect occurs in the main app page (e.g. when submitting an image
			 * to the blobstore)
			 */
			if (navigationToken.equals(MAIN_PAGE)) 
			{
				System.out.println("@MainAppController, @onValueChange, "+MAIN_PAGE);
				
				if (MainAppController.loginInfo != null)
				{ 
					presenter = new MainAppPresenter(MainAppController.loginInfo, rpcMainAppService, eventBus, new MainAppView());	
				}
				else
				{
					System.out.println("@MainAppController, @onValueChange, MainAppController.loginInfo = null, not presenting main app.");
				}		
			}

			/**
			 * After presenter has been initialized, go execute it.
			 */
			if (presenter != null) 
			{
				presenter.go(container);
			}
		}
		
	}

	/**
	 * On the very first launch of the app, we have no navigation tokens,
	 * thus History.getToken() is equal to "" and it will always call the login page. 
	 * 
	 */
	@Override
	public void go(HasWidgets container)
	{
		this.container = container;

		if ("".equals(History.getToken())) 
		{
			launchLoginPage();//History.newItem(LOGIN_PAGE);
		}
		else 
		{
			History.fireCurrentHistoryState();
		}
		
	}

}
