package com.blipnip.app.examples;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;
import org.scribe.model.Token;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.http.client.Response;

//http://beansgocrazy.blogspot.gr/2013/06/google-oauth-20-using-scribe.html
//https://github.com/fernandezpablo85/scribe-java/wiki/getting-started

public class TestScribe
{
	//Test Facebook API
	private static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/dialog/oauth";
	private static final String TEST_FACEBOOK_CLIENT_ID = "233875513415221";
	private static final String TEST_FACEBOOK_SECRET_KEY = "795ac16a6c836927180a77523f65d28f";
	private static final String FACEBOOK_SCOPE       = "email,user_birthday";
	private static final String TEST_FACEBOOK_SITE_URL = "http://blipniptest.com:8888/BlipNip.html?gwt.codesvr=blipniptest.com%3A9997";
	private static final Token EMPTY_TOKEN = null;
	
//	 private static final String GOOGLE_AUTH_URL  = "https://accounts.google.com/o/oauth2/auth";
//	 private static final String GOOGLE_CLIENT_ID  ="529376660676.apps.googleusercontent.com";
//	 private static final String GOOGLE_CLIENT_SECRET  ="hFheohLOXez2r1o7mBUhTlPc";
//	 private static final String TEST_GOOGLE_CLIENT_ID = "529376660676-hp9a75t54r5hic7rhuojeu97jlnc503c.apps.googleusercontent.com";
//	 private static final String PLUS_ME_SCOPE       = "https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
//	
	 // Test Google API
	 private static final String GOOGLE_AUTH_URL  = "https://accounts.google.com/o/oauth2/auth";
	 private static final String TEST_GOOGLE_CLIENT_ID  ="529376660676-hp9a75t54r5hic7rhuojeu97jlnc503c.apps.googleusercontent.com";
	 private static final String TEST_GOOGLE_SECRET_KEY  ="Nemug1lramUDaReFWsruj4XJ";
	 private static final String TEST_REDIRECT_URI = "http://blipniptest.com:8888/blipnip_app/oauthWindow.html";
	 private static final String PLUS_ME_SCOPE       = "https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	 
	 

	 /**
	  * This example is using OAuth2.
	  * http://stackoverflow.com/questions/8573272/oauth-problems-with-scribe
	 * @throws IOException 
	  */
	@Test
	public void buildFacebookService() throws IOException
	{
		OAuthService service = new ServiceBuilder()
        .provider(FacebookApi.class)
        .apiKey(TEST_FACEBOOK_CLIENT_ID)
        .apiSecret(TEST_FACEBOOK_SECRET_KEY)
        .scope(FACEBOOK_SCOPE)
        .callback(TEST_FACEBOOK_SITE_URL)
        .build();

		// Obtain the Authorization URL
	    System.out.println("Fetching the Authorization URL...");
	    String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
	    System.out.println("Got the Authorization URL! --> "+authorizationUrl);
	    
	    //CloseableHttpClient httpClient0 = HttpClients.createDefault();
	    DefaultHttpClient httpClient0 = new DefaultHttpClient();

	    HttpClientContext context = HttpClientContext.create();
	    HttpGet httpGet = new HttpGet("http://www.google.com/");
	    HttpResponse httpResponse = httpClient0.execute(httpGet, context);
	    try {
	        System.out.println("Response status: " + httpResponse.getStatusLine());
	        System.out.println("Last request URI: " + context.getRequest().getRequestLine());
	        List<URI> redirectLocations = context.getRedirectLocations();
	        if (redirectLocations != null) 
	        {
	            System.out.println("All intermediate redirects: ");
	            for (URI currentURI:redirectLocations)
	            {
	            	System.out.println("All intermediate redirects: " + currentURI);
	            }
	            
	        }
	        EntityUtils.consume(httpResponse.getEntity());
	    } finally {
	        //httpResponse.close();
	    }
	    
	    
	    //doGet(authorizationUrl) ;
	    
	    HttpClientHandler httpClient = new HttpClientHandler();
	    try
		{
			String redirectURL = httpClient.executeHttpClient(authorizationUrl, "GET");
			String yo = httpClient.executeHttpClient(redirectURL, "POST");
			System.out.println(yo);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
	    
//	    URLConnection con = null;
//		try
//		{
//			con = new URL( authorizationUrl ).openConnection();
//			System.out.println( "orignal url: " + con.getURL() );
//	    con.connect();
//	    System.out.println( "connected url: " + con.getURL() );
//		}
//		catch (MalformedURLException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		catch (IOException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	    
//	    InputStream is;
//		try
//		{
//			is = con.getInputStream();
//			System.out.println( "redirected url: " + con.getURL() );
//	    is.close();
//	    
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    
	    //http://blipniptest.com:8888/BlipNip.html?gwt.codesvr=blipniptest.com%3A9997&
	    //code=AQDmSCW0BZ8SrmLJgT4GmUGmp31eStp6b6ZmwYYBFAV5AFrxaSwFVS1Zgiw_SRkQo2PE7ty82uWyEZjk_ySLz5v4CxGZYYpxIo0UAtinqk3ilrpYwi9crgMoLkFeBT4DjXFrcdN17keRnmO7a3Ze5sAuGoCu03RRzgl9GplrAFKF7PUPkZgLv6SQXBQSXWrFVVQfK_y4qMVKe2D49bjb7clfaBJslyaiqWN4LGnHb-ID_itYQDklAMZf9z8I3JZE1k4OundPe3luhJfyQfCEJUdskyaOVWxL3oGYk_fR1u1QEovgGJDXs1BvWfcA0aAiVOY#_=_
	
	}
	
	
	
	
	
	
	
	@Test
	@Ignore
	/**
	 * This example is using OAuth1
	 * https://github.com/fernandezpablo85/scribe-java/blob/master/src/test/java/org/scribe/examples/GoogleExample.java
	 */
	public void test2()
	{
		OAuthService service = new ServiceBuilder()
        .provider(GoogleApi.class)
        .apiKey(TEST_GOOGLE_CLIENT_ID)
        .apiSecret(TEST_GOOGLE_SECRET_KEY)
        .scope(PLUS_ME_SCOPE)
        .callback("http://blipnip-app.appspot.com/blipnip_app/oauthWindow.html")
        .build();

		Token requestToken  = service.getRequestToken();
		
		System.out.println("Got Request token from service: "+requestToken);
		
		//String authUrl = service.getAuthorizationUrl(requestToken);
		
		
		//System.out.println(""+authUrl);
	}
	
	public static void doGet(String url) 
	{
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try 
		{
			Request response = builder.sendRequest(null, new RequestCallback() 
			{
				public void onError(Request request, Throwable exception) 
				{
					if (exception instanceof RequestTimeoutException) 
					{
						Window.alert("The request has timed out");
					} 
					else 
					{
						Window.alert(exception.getMessage());
					}
				}
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) 
					{
						HTML responseLabel=new HTML("<h3>Response is gonna be updated here</H3>");
						responseLabel.setHTML(response.getText());
						responseLabel.addStyleName("response");
						//RootPanel.get().add(responseLabel);
					} 
					else
					{
						Window.alert(""+response.getStatusCode());
					}
				}
			});
		} catch (RequestException e) 
		{
			// Code omitted for clarity
		}
	}

}
