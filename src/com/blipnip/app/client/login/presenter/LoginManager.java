package com.blipnip.app.client.login.presenter;

import com.blipnip.app.client.login.event.TokenReadyEvent;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;

/**
 * This is a bit complicated here - because of the mvp architecture.
 * So what is happening is that we arrive in this class when the onClick event of the LoginPresenter is triggered.
 * Once here, the AUTH takes over, and tries to get a token for us. This process is contained in the LoginCallback
 * for simplicity. The idea, is that in order to login with Facebook or Google, they (as providers) need to give us
 * a token (a long string of characters/numbers) that we can use to access user data.
 * 
 * Asynchrinonously, the LoginCallback will complete at some point and return to us a token (or empty if it fails).
 * Once we get this, we shoot this event in the notorious event bus, which is used to handle application wide events.
 * 
 * The event of the event bus, is caught by the MainAppController and it, in turn calls the service to retrieve user 
 * login information.
 * 
 * @author Thanos
 *
 */
public class LoginManager 
{
	private static final Auth AUTH = Auth.get();
	
	//https://developers.facebook.com/docs/reference/login/
	private static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/dialog/oauth";
	private static final String TEST_FACEBOOK_CLIENT_ID = "233875513415221";
	private static final String FACEBOOK_CLIENT_ID = "427389137361504";
	private static final String FACEBOOK_SCOPE       = "email,user_birthday";
	
	//http://stackoverflow.com/questions/7393852/name-email-from-googles-oauth-api, 
	//http://stackoverflow.com/questions/10664868/where-can-i-find-a-list-of-scopes-for-googles-oauth-2-0-api/15328600#15328600
	//https://developers.google.com/oauthplayground
	 private static final String GOOGLE_AUTH_URL  = "https://accounts.google.com/o/oauth2/auth";
	 private static final String GOOGLE_CLIENT_ID  ="529376660676.apps.googleusercontent.com";
	 private static final String TEST_GOOGLE_CLIENT_ID = "529376660676-hp9a75t54r5hic7rhuojeu97jlnc503c.apps.googleusercontent.com";
	 private static final String PLUS_ME_SCOPE       = "https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	

	private String provider;
	private HandlerManager eventBus;
	
	public LoginManager(String provider, HandlerManager eventBus)
	{
		this.provider        = provider;
		this.eventBus       = eventBus; 
	}

	/**
	 * Depending on provider, the authorization takes place using
	 * the client side authorization library gwt-oauth2. It attempts
	 * the login and on success the callback passes the authorization
	 * token in the event which in turn propagates to the 
	 * 
	 */
	public void doAuthorize() 
	{
		if (provider != null)
		{
			if (provider.toUpperCase().equals("FACEBOOK"))
			{	
				String facebookClientId = null;
				if (GWT.isProdMode())
	    			{
					facebookClientId = FACEBOOK_CLIENT_ID;
	    			}
	    			else
	    			{
	    				facebookClientId = TEST_FACEBOOK_CLIENT_ID;
	    			}
				
				final AuthRequest req = new AuthRequest(FACEBOOK_AUTH_URL,facebookClientId).withScopes(FACEBOOK_SCOPE).withScopeDelimiter(",");
				
				AUTH.clearAllTokens();
			
				AUTH.login(req, new LoginCallback());
				
				System.out.println("@LoginManager, @doAuthorize(), expires in: " + AUTH.expiresIn(req));
				
			}
			else if (provider.toUpperCase().equals("GOOGLE")) 
			{
				//Window.alert("No App Id available yet");
				//System.out.println("No App Id available yet");
			
				String googleClientId = null;
				if (GWT.isProdMode())
				{
					googleClientId = GOOGLE_CLIENT_ID;
				}
				else
				{
					googleClientId = TEST_GOOGLE_CLIENT_ID;
				}
				
				final AuthRequest req = new AuthRequest(GOOGLE_AUTH_URL, googleClientId).withScopes(PLUS_ME_SCOPE).withScopeDelimiter(" ");
				AUTH.clearAllTokens();
				AUTH.login(req, new LoginCallback());
				
			}
			else if (provider.toUpperCase().equals("NATIVE"))
			{
				Window.alert("Native login not implemented yet.");
				System.out.println("Native login not implemented yet");
			}
		}
		else
		{
			System.out.println("@LoginManager, Provider is null...");
		}
	}
	
	/**
	 * Helper class to implement the callback used by the authentication
	 * library AUTH.login(req, new LoginCallback())
	 * 
	 * @author Thanos
	 *
	 */
	private class LoginCallback implements Callback<String, Throwable>
	{
		public LoginCallback()
		{
			System.out.println("@LoginManager, @LoginCallback Constructor");
		}

		@Override
		public void onSuccess(final String token) 
		{
			System.out.println("@LoginManager, @LoginCallback @onSuccess -> token:" +token);
			eventBus.fireEvent(new TokenReadyEvent(provider, token));
		}

		@Override
		public void onFailure(final Throwable caught) 
		{
			System.out.println("@LoginManager, @LoginCallback @onFailure");
			eventBus.fireEvent(new TokenReadyEvent(provider, null));
		}
	}

}
