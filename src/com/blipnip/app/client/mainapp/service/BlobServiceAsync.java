package com.blipnip.app.client.mainapp.service;

import com.blipnip.app.shared.BlipContent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BlobServiceAsync
{

	void getBlobStoreUploadUrl(AsyncCallback<String> callback);

	void getPicture(String id, AsyncCallback<BlipContent> callback);
	
	void  getBlobFileName(String pictureId, AsyncCallback<String> callback);
	  
	void deleteImage(String pictureId, AsyncCallback<Void> callback);

}