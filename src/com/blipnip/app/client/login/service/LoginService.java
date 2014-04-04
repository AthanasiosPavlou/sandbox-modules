package com.blipnip.app.client.login.service;


import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService 
{	
	String testLoginServerConnection(String name) throws IllegalArgumentException;
	
	String getUserEmail(String token);	
	
	LoginInfo useGoogleLoginService(String requestUri);	
	
	LoginInfo retrieveUserInfo(boolean isProduction, String provider, String token);
	
	//New stuff
	LoginInfo startSession(LoginInfo info);
	
	LoginInfo loginUsingSession();
    
    String removeUserSession();
}
