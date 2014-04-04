package com.blipnip.app.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blipnip.app.shared.BlipContent;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;


/**
 * This class is paired with the FormPanel that must submit to a servlet
 * that extends HttpServlet. RemoteServiceServlet cannot be used.
 * 
 * @author Thanos
 *
 */
@SuppressWarnings("serial")
public class UploadServiceImpl extends HttpServlet
{

	//Start Blobstore and Objectify Sessions
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	static
	{
		ObjectifyService.register(BlipContent.class);
	}

	//Override the doPost method to store the Blob's meta-data
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		
		System.out.println("@UploadServiceImpl, @doPost, blobs.size= "+blobs.size());
		System.out.println("@UploadServiceImpl, @doPost, req.getServletPath()= "+req.getServletPath());
		
		BlobKey blobKey = blobs.get("upload").get(0);
	  
		if (blobKey != null)
		{
			// Get the paramters from the request to populate the BlipContent object
			BlipContent blipContent = new BlipContent();
			
			// Setting the blobKey that links to metadata related to this blob
			blipContent.setBlobKey(blobKey.getKeyString());
			
			// Testing retrieval of BlipContent Object metadata using blobkey
			BlobInfoFactory infoFactory = new BlobInfoFactory();
			BlobInfo info = infoFactory.loadBlobInfo(new BlobKey(blipContent.getBlobKey())); 
			System.out.println("@UploadServiceImpl, @doPost, BlobInfo File name: "+ info.getFilename());
			
			// Set storing time for BlipContent object 
			blipContent.setTimeStored(System.currentTimeMillis());	
			
			// Set blip title
			blipContent.setTitle(req.getParameter("blipTitleTextBox"));
			
			// Set blip description
			blipContent.setDescription(req.getParameter("blipDescriptionTextBox"));
			
			//Use Google Image services to get an image url for dynamically changing size, crop etc
			blipContent.setImageUrl(generateImageURL(blobKey));
			
			// Map the ImageURL to the blobservice servlet, which will serve the image
			blipContent.setFullBlobImageUrl("/blipnip_app/blobservice?blob-key=" + blobKey.getKeyString());
	
			// Here we set the duration
			blipContent.setDuration(Long.parseLong(req.getParameter("durationHiddenTextBox").trim()));
			
			// When the UploadServiceImpl is called the form does not contain a blip id, thus false
			blipContent.setAttachedToBlip(false);
			
			// All done, now save entity in DataStore
			Key<BlipContent> imagekey = ofy().save().entity(blipContent).now();
			
			System.out.println("Image saved on:"+readableTime(blipContent.getTimeStored()));
			System.out.println("@UploadServiceImpl, @doPost, imageKey:"+ imagekey+ ", picture id="+blipContent.getId());
			System.out.println("\n");
			
			//Redirect recursively to this servlet (calls doGet) //http://blipniptest.com:8888/blipnip_app/uploadservice
			//res.sendRedirect(LoginServiceImpl.DEV_APP_MAIN_DOMAIN_NAME+"?id="+picture.id+"#main");
	
			System.out.println("@UploadServiceImpl, @doPost, blipDescriptionTextBox: "+req.getParameter("blipDescriptionTextBox"));
			System.out.println("@UploadServiceImpl, @doPost, hidden text box MODULE BASE URL: "+req.getParameter("hiddenAppURLTextBox"));
			System.out.println("@UploadServiceImpl, @doPost, duration hidden text box: "+req.getParameter("durationHiddenTextBox"));
			
			System.out.println("@UploadServiceImpl, @doPost, image key: "+imagekey.getRaw().getId());
			
			Key<BlipContent> picKey = com.googlecode.objectify.Key.create(BlipContent.class, blipContent.getId());
			System.out.println("@UploadServiceImpl, @doPost, image key from id (should be same as above): "+ picKey.getRaw().getId());
			
			// Redirect recursively to this servlet (calls doGet)
		    res.sendRedirect(req.getParameter("hiddenAppURLTextBox")+"/blipnip_app/uploadservice?id=" + blipContent.getId());
		}
		else
		{
			System.out.println("@UploadServiceImpl, @doPost, No Blob Key Created");
		}
	}
	
	/**
	 * Although we can use the original image's url 
	 * we can use GAE Images Java API.
	 * 
	 * The getServingUrl() method allows you to generate a stable, dedicated URL for serving web-suitable 
	 * image thumbnails. You simply store a single copy of your original image in Blobstore, and then request 
	 * a high-performance per-image URL. This special URL can serve that image resized and/or cropped 
	 * automatically, and serving from this URL does not incur any CPU or dynamic serving load on your
	 *  application (though bandwidth is still charged as usual).
	 *  
	 *  The URL returned by this method is always public, but not guessable; private URLs are not currently supported. 
	 *  If you wish to stop serving the URL, delete the underlying blob key. This takes up to 24 hours to take effect.
	 * 
	 * To remove when running on local dev server delete the folder war/WEB-INF/appengine-generated
	 * refs:
	 * https://developers.google.com/appengine/docs/java/images/
	 * https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/images/ImagesService
	 * http://stackoverflow.com/questions/13783324/getting-the-original-image-back-with-imageservices-getservingurl
	 * 
	 */
	private String generateImageURL(BlobKey imageBlobKey)
	{
		String url = null;
		
		//First set the serving options
		ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(imageBlobKey);
		servingUrlOptions.blobKey(imageBlobKey);
		
		// Then create the service and get the serving url
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		
		url = imagesService.getServingUrl(servingUrlOptions);
		System.out.println(url);
		
		return url;
	}

 	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// Send the meta-data id back to the client in the HttpServletResponse response
		String id = req.getParameter("id");
		
		resp.setHeader("Content-Type", "text/html");
		resp.getWriter().println(id);
		
		System.out.println("@UploadServiceImpl, @doGet, Getting image Id (key): "+id);

	}
 	
 	private String readableTime(long systemMilliSecs)
 	{
 		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss aa");

        Date resultdate = new Date(systemMilliSecs);
        return sdf.format(resultdate);
 	}

}