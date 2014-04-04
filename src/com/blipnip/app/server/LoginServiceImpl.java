package com.blipnip.app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.blipnip.app.client.login.service.LoginService;
import com.blipnip.app.shared.LoginInfo;
import com.blipnip.commons.shared.generic.FieldVerifier;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	public static final String DEV_APP_MAIN_DOMAIN_NAME = "http://blipniptest.com:8888/BlipNip.html?gwt.codesvr=blipniptest.com:9997";
	public static final String APP_MAIN_DOMAIN_NAME = "http://blipnip-app.appspot.com";
	
	@SuppressWarnings("unused")
	private static final String DEV_APP_MAIN_IP = "http://blipniptest.com:8888/BlipNip.html?gwt.codesvr=blipniptest.com:9997";
	
	private static final String FACEBOOK_PROVIDER = "FACEBOOK";
	private static final String FACEBOOK_API_URL = "https://graph.facebook.com/me?access_token=";
	
	private static final String GOOGLE_PROVIDER = "GOOGLE";
	private static final String GOOGLE_API_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=";
	
	private static Logger log = Logger.getLogger(LoginServiceImpl.class.getCanonicalName());

	@Override
	public String testLoginServerConnection(String input) throws IllegalArgumentException 
	{
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) 
		{
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"+ userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) 
	{
		if (html == null) 
		{
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	/**
	 * Get email method for Google login
	 */
	@Override
	public String getUserEmail(final String token) 
	{
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (null != user) 
		{
			return user.getEmail();
		} 
		else 
		{
			return "noreply@sample.com";
		}
	}

	@Override
	public LoginInfo useGoogleLoginService(final String requestUri) 
	{
		
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		final LoginInfo loginInfo = new LoginInfo();
		
		if (user != null) 
		{
			loginInfo.setLoggedStatus(true);
			loginInfo.setNickname(user.getEmail());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
		} 
		else 
		{
			loginInfo.setLoggedStatus(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
	}

	/**
	 * Retrieving user info for a given provider.
	 */
	@Override
	public LoginInfo retrieveUserInfo(final boolean isProduction, final String provider, final String token) 
	{
		LoginInfo retrievedInfoObject = null;
		if (provider.toUpperCase().equals(FACEBOOK_PROVIDER))
		{
			System.out.println("@LoginServiceImpl, @retrieveUserInfo, "+"Got token:"+token);
			
			String url = FACEBOOK_API_URL+token;
			final StringBuffer urlResponse = getResponse(url);
			System.out.println("@LoginServiceImpl, @retrieveUserInfo(FACEBOOK), urlResponse: "+ urlResponse);
			retrievedInfoObject = parseFacebookResponse(isProduction, urlResponse, token);	
		}

		if (provider.toUpperCase().equals(GOOGLE_PROVIDER))
		{
			System.out.println("LoginInfo retrieveUserInfo-> "+"Got token:"+token);
			
			String url = GOOGLE_API_URL+token;
			final StringBuffer urlResponse = getResponse(url); 
			System.out.println("@LoginServiceImpl, @retrieveUserInfo, (GOOGLE), urlResponse: "+ urlResponse);
			retrievedInfoObject = parseGoogleResponse(isProduction, urlResponse, token);
			
		}
		return retrievedInfoObject;
	}
	
	/**
	 * Method that wraps the url response in a StringBuffer.
	 * 
	 * @param url
	 * @return
	 */
	private StringBuffer getResponse(String url)
	{
		StringBuffer stringResponse = new StringBuffer();
		
		if (url != null) 
		{
			try 
			{
				final URL u = new URL(url);
				final URLConnection uc = u.openConnection();
				final int end = 1000;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try 
				{
					isr = new InputStreamReader(uc.getInputStream());
					br = new BufferedReader(isr);
					final int chk = 0;
					while ((url = br.readLine()) != null) 
					{
						if ((chk >= 0) && ((chk < end))) 
						{
							stringResponse.append(url).append('\n');
						}
					}
				} 
				catch (final java.net.ConnectException cex) 
				{
					stringResponse.append(cex.getMessage());
				} 
				catch (final Exception ex) 
				{
					log.log(Level.SEVERE, ex.getMessage());
				} 
				finally 
				{
					try 
					{
						br.close();
					} 
					catch (final Exception ex) 
					{
						log.log(Level.SEVERE, ex.getMessage());
					}
				}
			} 
			catch (final Exception e) 
			{
				log.log(Level.SEVERE, e.getMessage());
			}
		}
		return stringResponse;
	}
	
	/**
	 * 
	 * Method to parse the response url received from facebook.
	 * To parse the response jackson-core and jackson-mapping
	 * libraries are utilised.
	 * 
	 * @param isProduction
	 * @param urlResponse
	 * @param token
	 * @return
	 */
	private LoginInfo parseFacebookResponse(boolean isProduction, StringBuffer urlResponse, String token)
	{
		final LoginInfo loginInfo = new LoginInfo();
		
		try 
		{
			final JsonFactory jsonFactory = new JsonFactory();
			JsonParser jsonParser = jsonFactory.createJsonParser(urlResponse.toString());
			
			ObjectMapper mapper = new ObjectMapper(); // just need one
		    // Got a Java class that data maps to nicely? If so: FacebookGraph graph = mapper.readValue(url, FaceBookGraph.class);
		    // Or: if no class (and don't need one), just map to Map.class, which is what we are doing here.
		    @SuppressWarnings("unchecked")
			Map<String,Object> map = mapper.readValue(jsonParser, Map.class);
			
		    for (Map.Entry<String, Object> entry : map.entrySet())
		    {
				System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
				
				String fieldname = entry.getKey();
				
				if ("picture".equals(fieldname)) 
				{
					loginInfo.setPictureUrl((String) entry.getValue());
				} 
				else if ("first_name".equals(fieldname)) 
				{
					loginInfo.setNickname((String) entry.getValue());
				} 
				else if ("email".equals(fieldname)) 
				{
					loginInfo.setEmailAddress((String)entry.getValue());
				}
				else if ("username".equals(fieldname)) 
				{
					String userName = (String)entry.getValue();
					loginInfo.setUserName(userName);
					
					// with redirest=false, a json response is retrieved -> https://developers.facebook.com/docs/reference/api/using-pictures/
					//String jsonImageUrl = "https://graph.facebook.com/"+loginInfo.getUserId()+"/picture?redirect=false";
					
					String imageUrl =  "http://graph.facebook.com/"+userName+"/picture?width=128&height=128";
					//String imageUrl =  "https://graph.facebook.com/"+userName+"/picture?type=square";
					
					loginInfo.setPictureUrl(imageUrl);
					System.out.println("@LoginServiceImpl, @parseFacebookResponse, Image URL: "+ imageUrl);
					
					// Currently logging out using: //https://www.facebook.com/logout.php?next=YOUR_REDIRECT_URL&access_token=USER_ACCESS_TOKEN
					if (isProduction)
					{
						loginInfo.setLogoutUrl("https://www.facebook.com/logout.php?next="+APP_MAIN_DOMAIN_NAME+"&access_token="+token);
					}
					else
					{
						loginInfo.setLogoutUrl("https://www.facebook.com/logout.php?next="+DEV_APP_MAIN_DOMAIN_NAME+"&access_token="+token);
					}
					
					/**
					 * Info on revoking login: https://developers.facebook.com/docs/facebook-login/permissions/#revokelogin
					   DELETE /{user-id}/permissions, by issuing a POST request to https://graph.facebook.com/COMMENT_ID?method=delete.
					   loginInfo.setLogoutUrl("https://graph.facebook.com/"+jp.getText()+"/permissions/?method=delete&access_token="+token);
					 * 
					 */
				}
			}
		    
		    /**
		     * If the mapper is not used above (which is utilizing the jackson-mapper-asl-1.9.13.jar)
		     * we can do it by more generic parsing. However, more complex scenarios, such as a key
		     * mapping to an object are not covered.
		     * 
		     *  jsonParser.nextToken();
				while (jsonParser.nextToken() != null)//JsonToken.END_OBJECT) 
				{
					and use the above conditions.	    
				}
		     */			
		} 
		catch (final JsonParseException e) 
		{
			log.log(Level.SEVERE, e.getMessage());
		} 
		catch (final IOException e) 
		{
			log.log(Level.SEVERE, e.getMessage());
		}
		
		loginInfo.setLoggedStatus(true);
		
		return loginInfo;
	}
	
	/**
	 * Method to parse the response url from Google
	 * 
	 * @param urlResponse
	 * @param token
	 * @return
	 */
	private LoginInfo parseGoogleResponse(boolean isProduction, StringBuffer urlResponse, String token)
	{
		final LoginInfo loginInfo = new LoginInfo();
		
		// Setting to nothing in case no image retrieved or no "picture" element found
		loginInfo.setPictureUrl("");
		
		try 
		{
			final JsonFactory f = new JsonFactory();
			JsonParser jp;
			jp = f.createJsonParser(urlResponse.toString());
			jp.nextToken();
			while (jp.nextToken() != JsonToken.END_OBJECT) 
			{
				final String fieldname = jp.getCurrentName();

				System.out.println("parseGoogleResponse->field name:"+fieldname);

				jp.nextToken();
				if ("picture".equals(fieldname)) 
				{
					//http://stackoverflow.com/questions/9128700/getting-google-profile-picture-url-with-user-id
					//http://stackoverflow.com/questions/17045596/how-to-retrieve-google-oauth2-profile-thumbnail-picture
					loginInfo.setPictureUrl(jp.getText()+"?sz=128");
				} 
				else if ("name".equals(fieldname)) 
				{
					loginInfo.setNickname(jp.getText());
				} 
				else if ("given_name".equals(fieldname))
				{
					loginInfo.setUserName(jp.getText());
				}
				else if ("email".equals(fieldname)) 
				{
					loginInfo.setEmailAddress(jp.getText());
					
					// Currently logging out using: https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=[http://www.mysite.com]
					//http://stackoverflow.com/questions/17050575/logout-link-with-return-url-oauth
					if (isProduction)
					{
						String url = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue="+APP_MAIN_DOMAIN_NAME;
						loginInfo.setLogoutUrl(url);
					}
					else
					{
						String url = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue="+DEV_APP_MAIN_DOMAIN_NAME;
						loginInfo.setLogoutUrl(url);
					}
				}
			}
		} 
		catch (final JsonParseException e) 
		{
			log.log(Level.SEVERE, e.getMessage());
		} 
		catch (final IOException e) 
		{
			log.log(Level.SEVERE, e.getMessage());
		}
		
		loginInfo.setLoggedStatus(true);
		return loginInfo;
	}
	
	
	/**
	 *																	* 
	 * 	  Methods related to session preserving    *
	 * 																	*
	 * 																	*
	 * 																	*
	 */
	
	/**
	* Storing response in session
	*/
	 @Override
	    public LoginInfo startSession(LoginInfo info)
	    {
		    
			return storeUserInSession(info);
	    }
	
     @Override
	    public LoginInfo loginUsingSession()
	    {
	        return getUserAlreadyFromSession();
	    }
	 
	    @Override
	    public String removeUserSession() throws IllegalArgumentException
	    {
	        return deleteUserFromSession();
	    }
	 
	    private LoginInfo getUserAlreadyFromSession()
	    {
	    		System.out.println("@LoginServiceImpl, @getUserAlreadyFromSession");
	    	
	    		LoginInfo user = null;
	        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
	        HttpSession session = httpServletRequest.getSession();
	        
	        Object userObj = session.getAttribute("user");
	        if (userObj != null && userObj instanceof LoginInfo)
	        {
	            user = (LoginInfo) userObj;
	        }
	        return user;
	    }
	 
	    private LoginInfo storeUserInSession(LoginInfo user)
	    {
	    		System.out.println("@LoginServiceImpl, @storeUserInSession, Storing user session for user: "+user.getEmailAddress()); 		
	        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
	        HttpSession session = httpServletRequest.getSession(true);
	        session.setAttribute("user", user);
	        user.setSessionId(session.getId());
	        return user;
	    }
	 
	    private String deleteUserFromSession()
	    {
	    		String operationSuccess = "false";
	    
	    		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
	        HttpSession session = httpServletRequest.getSession();
	     	
	        Object userObj = session.getAttribute("user");
	        if (userObj != null && userObj instanceof LoginInfo)
	        {
		        	LoginInfo info = (LoginInfo)session.getAttribute("user");
		        System.out.println("@LoginServiceImpl, @deleteUserFromSession, attribute 'user'--> email address: " +info.getEmailAddress()+" (session ID:"+session.getId()+")"); 
		     	
		        session.removeAttribute("user");
		     	session.invalidate();
		     	operationSuccess = "true";
	        }
	        else
	        {
	        		System.out.println("@LoginServiceImpl, @deleteUserFromSession,  no user attribute stored in session.(session ID: "+session.getId()+")"); 
	        }	
	        
	        return operationSuccess;
	    }
	
}
