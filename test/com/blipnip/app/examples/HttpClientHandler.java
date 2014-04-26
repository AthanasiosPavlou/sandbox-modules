package com.blipnip.app.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientHandler
{
	
	public HttpClientHandler()
	{

	}
	
	public String executeHttpClient(String url, String type) throws IOException
	{
		String responseBody = null;
		String currentUrl = null;
		
		//String username = "ericbruno";
		//String password = "mypassword";
		//String auth = username + ":" + password;
		//String encodedAuth = Base64.encodeBase64(auth.getBytes());
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
        try 
        {
        	     MyRedirectHandler handler = new MyRedirectHandler();
        	     httpclient.setRedirectHandler(handler);
        	  
        	     HttpResponse response = null;
            if (type.toUpperCase().equals("GET"))
            {
	            	HttpGet httpget = new HttpGet(url); 									
	            response = httpclient.execute(httpget);
            }
            else if (type.toUpperCase().equals("POST")) 
            {
            		HttpGet httpget = new HttpGet(url); 									
	            response = httpclient.execute(httpget);
            }
        	     
        	     

            HttpEntity entity = response.getEntity();
            responseBody = url;
            if(handler.lastRedirectedUri != null){
            	responseBody = handler.lastRedirectedUri.toString();
                
            }
            
            System.out.println(responseBody);
            
//            httpget.addHeader("Authorization", "Basic " + encodedAuth);
//            responseBody = httpclient.execute(httpget, defaultResponseHandler());
              
//            HttpContext context = new BasicHttpContext(); 
//            HttpResponse response = httpclient.execute(httpget, context); 
//            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)  throw new IOException(response.getStatusLine().toString());
//            HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
//            HttpHost currentHost = (HttpHost)  context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
//            currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
            
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println(currentUrl);
            
            
        }
        finally 
        {
        	
        }
        
        return responseBody;
	}
	

	private ResponseHandler<String> defaultResponseHandler()
	{
		// Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>()
        {
            public String handleResponse(final HttpResponse response)  throws ClientProtocolException, IOException 
            {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300)
                {
                    HttpEntity entity = response.getEntity();
                    
                    String statusString = response.getStatusLine().toString();
                    
                    Header[] headers = response.getAllHeaders();

//                    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
//                    String line = null;
//                    while ((line = rd.readLine()) != null) 
//                    {
//                        System.out.println(line);
//                    }
                    
                    return entity != null ? EntityUtils.toString(entity) : null;
                } 
                else
                {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        
        return responseHandler;
	}
	
	private static HttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
	    SSLContext sslContext = SSLContext.getInstance("SSL");
	    TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllTrustManager() };
	    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

	    SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext);
	    SchemeRegistry schemeRegistry = new SchemeRegistry();
	    schemeRegistry.register(new Scheme("https", 443, sslSocketFactory));
	    schemeRegistry.register(new Scheme("http", 80, new PlainSocketFactory()));

	    HttpParams params = new BasicHttpParams();
	    ClientConnectionManager cm = new org.apache.http.impl.conn.SingleClientConnManager(schemeRegistry);

	    // some pages require a user agent
	    AbstractHttpClient httpClient = new DefaultHttpClient(cm, params);
	    HttpProtocolParams.setUserAgent(httpClient.getParams(), "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:13.0) Gecko/20100101 Firefox/13.0.1");

	    //httpClient.setRedirectStrategy(new RedirectStrategy());

	    httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
	        @Override
	        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException 
	        {
	            if (response.containsHeader("Location")) 
	            {
	                Header[] locations = response.getHeaders("Location");
	                //if (locations.length > 0)
	                    //context.setAttribute(LAST_REDIRECT_URL, locations[0].getValue());
	            }
	        }
	    });

	    return httpClient;
	}
	
	private class MyRedirectHandler extends DefaultRedirectHandler 
	{
	    public URI lastRedirectedUri;

	    @Override
	    public boolean isRedirectRequested(HttpResponse response, HttpContext context) 
	    {
	        return super.isRedirectRequested(response, context);
	    }

	    @Override
	    public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException 
	    {

	        lastRedirectedUri = super.getLocationURI(response, context);

	        return lastRedirectedUri;
	    }

	}

}
