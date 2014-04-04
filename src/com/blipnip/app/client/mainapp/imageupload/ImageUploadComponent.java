package com.blipnip.app.client.mainapp.imageupload;

import gwtupload.client.PreloadedImage;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;
import gwtupload.client.SingleUploader;


public class ImageUploadComponent extends Composite
{

	private SimplePanel simplePanel;
	private VerticalPanel verticalPanel;
	private FocusPanel panelImages;
	private SingleUploader singleUploader;
	private PreloadedImage img = null;
	
	public PreloadedImage getImg()
	{
		return img;
	}
	
	private OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() 
	{
		public void onLoad(PreloadedImage img) 
		{
			
			img.setWidth("30px");
			panelImages.add(img);
		}
	};

	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() 
	{
		@Override
		public void onFinish(IUploader uploader) 
		{
			if (uploader.getStatus() == Status.SUCCESS) 
			{
				System.out.println("@ImageUploadComponent, @OnFinishUploaderHandler, @onFinish->SUCCESS, URL: "+uploader.fileUrl());
				new PreloadedImage(uploader.fileUrl(), showImage);
			}
			else
			{
				System.out.println("@ImageUploadComponent, @OnFinishUploaderHandler, @onFinish->OTHER, URL: "+uploader.fileUrl());
			}
		}
	};

	public ImageUploadComponent(FocusPanel p)
	{
		this.panelImages = new FocusPanel();
		//this.panelImages.setSize("100%", "100%");
		
		
		this.singleUploader = new SingleUploader(FileInputType.LABEL);
		this.singleUploader.setTitle("testinggg");
		this.singleUploader.setAutoSubmit(true);
		this.singleUploader.setValidExtensions("jpg", "gif", "png");
		//this.singleUploader.getFileInput().getWidget().setSize("100%", "100%");
		this.singleUploader.avoidRepeatFiles(true);

		
		this.singleUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		
		this.verticalPanel = new VerticalPanel();
		this.verticalPanel.add(panelImages);
		this.verticalPanel.add(singleUploader);
		
		this.simplePanel = new SimplePanel();
		this.simplePanel.add(this.verticalPanel);
		
		this.initWidget(simplePanel);
	}

}
