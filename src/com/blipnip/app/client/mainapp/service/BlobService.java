package com.blipnip.app.client.mainapp.service;

import com.blipnip.app.shared.BlipContent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("blobservice")
public interface BlobService extends RemoteService
{

  String getBlobStoreUploadUrl();

  BlipContent getPicture(String id);
  
  String getBlobFileName(String pictureId);
  
  void deleteImage(String pictureId);

}
