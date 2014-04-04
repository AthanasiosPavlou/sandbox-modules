package com.blipnip.app.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blipnip.app.client.mainapp.service.BlobService;
import com.blipnip.app.shared.BlipContent;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class BlobServiceImpl extends RemoteServiceServlet implements BlobService
{
	// Start a GAE BlobstoreService session and Objectify session
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	// Register the Objectify Service for the BlipContent entity
	static
	{
		ObjectifyService.register(BlipContent.class);
	}

	/**
	 * Generate a Blobstore Upload URL from the GAE BlobstoreService
	 */
	@Override
	public String getBlobStoreUploadUrl()
	{
		// Map the UploadURL to the uploadservice which 
		// will be called by submitting the FormPanel
		return blobstoreService.createUploadUrl("/blipnip_app/uploadservice");
	}

	/**
	 * Retrieve the Blob's BlipContent meta-data from the Datastore using Objectify
	 */
	@Override
	public BlipContent getPicture(String id)
	{
		long longId = Long.parseLong(id);	
		BlipContent blipContent = ofy().load().type(BlipContent.class).id(longId).now();
		return blipContent;
	}
	
	@Override
	public String getBlobFileName(String pictureId)
	{
		long longId = Long.parseLong(pictureId);	
		BlipContent blipContent = ofy().load().type(BlipContent.class).id(longId).now();
		return new BlobInfoFactory().loadBlobInfo(new BlobKey(blipContent.getBlobKey())).getFilename(); 
	}
	
	@Override
	public void deleteImage(String pictureId)
	{
		long longId = Long.parseLong(pictureId);	
		BlipContent blipContent = ofy().load().type(BlipContent.class).id(longId).now();
		BlobKey blobKey = new BlobKey(blipContent.getBlobKey()); 
		
		// First delete the actual blob
		blobstoreService.delete(blobKey);
		
		// Then delete the BlipContent entity (which has a blob key, pointing to the blob just deleted)
		ofy().delete().type(BlipContent.class).id(longId).now();
	}

	/**
	 * Override doGet to serve blobs.  This will be called 
	 * automatically by the Image Widget
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		blobstoreService.serve(blobKey, resp);
	}
	
}