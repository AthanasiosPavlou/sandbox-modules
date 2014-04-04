package com.blipnip.app.client.mainapp.view.sandbox;

import com.blipnip.app.client.mainapp.imageupload.ImageUploadComponent;
import com.blipnip.app.client.mainapp.presenter.MainAppPresenter.AddBlipContentDisplay;
import com.blipnip.app.client.mainapp.service.BlobService;
import com.blipnip.app.client.mainapp.service.BlobServiceAsync;
import com.blipnip.app.shared.BlipContent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
//import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel;
//import com.google.gwt.user.client.ui.FlexTable;

public class SampleAddContentView extends DialogBox implements AddBlipContentDisplay
{

	private final DialogBox dialogBox = new DialogBox();

	// Create a table to layout the content
	VerticalPanel dialogContents = new VerticalPanel();
	private final HorizontalPanel blipContentButtonsHorizontalPanel = new HorizontalPanel();
	private final Button cancelButton = new Button("Cancel");
	private final Button submitButton = new Button("OK");
	private final Image image = new Image();
	private final FocusPanel focusPanel = new FocusPanel();
	private final Label blipDescriptionLabel = new Label("Description (i.e. #niceview#nighttime)");
	private final TextBox blipDescriptionTextBox = new TextBox();
	private final FileUpload fileUpload = new FileUpload();
	private final VerticalPanel blipImageVerticalPanel = new VerticalPanel();
	private final VerticalPanel blipDescriptionVerticalPanel = new VerticalPanel();
	private final FormPanel uploadForm = new FormPanel();//new FormPanel(new NamedFrame("_self"));
	private final VerticalPanel mainVerticalPanel = new VerticalPanel();
	private ImageUploadComponent iuc;
	
	// Use an RPC call to the Blob Service to get the blobstore upload url
	  BlobServiceAsync blobService = GWT.create(BlobService.class);
	
	public SampleAddContentView()
	{
	
		createDialogBox("Dialog Box Text");

		fileUpload.addChangeHandler(new ChangeHandler()
		{
		    @Override
		    public void onChange(ChangeEvent event) 
		    {
			    	blobService.getBlobStoreUploadUrl(new AsyncCallback<String>()
			    	{
	
			    		@Override
			    		public void onSuccess(String result)
			    		{			    			
			    			// Set the form action to the newly created blobstore upload URL
	
			    			System.out.println("@AddContentView, @submitButton, @onClick, @blobService, @onSuccess: "+result.toString());
			    			uploadForm.setAction(result.toString());
	
			    			// Submit the form to complete the upload
			    			uploadForm.submit();
			    			
			    			//reset after submission
			    			uploadForm.reset(); 
	
			    		}
	
			    		@Override
			    		public void onFailure(Throwable caught)
			    		{
			    			caught.printStackTrace();
			    			System.out.println("@AddContentView, @submitButton, @onClick, @blobService, @onFailure: "+caught.getMessage());
			    		}
			    	});
		    } });
		
		submitButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				System.out.println("@AddContentView, @submitButton, @onClick");
			}
		});

		// Add an event handler to the form.
		uploadForm.addSubmitHandler(new FormPanel.SubmitHandler()
		{
			public void onSubmit(SubmitEvent event)
			{
				// This event is fired just before the form is submitted.
				System.out.println("@AddContentView, @uploadForm, @addSubmitHandler");
			}
		});

		uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler()
		{
			// When the form submission is successfully completed, this event is fired.
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event)
			{
				//The submit complete Event Results will contain the unique
				//identifier for the picture's meta-data.  Trim it to remove
				//trailing spaces and line breaks
				System.out.println("@AddContentView, @uploadForm, @addSubmitCompleteHandler, @event.getResults: " + event.getResults());
				getPicture(event.getResults().trim());

			}

		});

	}

	public void getPicture(String id)
	{

		//Make another call to the Blob Service to retrieve the meta-data
		blobService.getPicture(id, new AsyncCallback<BlipContent>()
		{

			@Override
			public void onSuccess(BlipContent result)
			{
				System.out.println("@AddContentView, @getPicture, @blobService.getPicture, Image URL:  "+result.getFullBlobImageUrl());

				image.setUrl(result.getFullBlobImageUrl());
				//image.setWidth("10px");
				//setBlipImage(image.getUrl());
				
				//Use Getters from the BlipContent object to load the FlexTable
				//resultsTable.setWidget(0, 0, image);
				//resultsTable.setText(1, 0, result.getTitle());
				//resultsTable.setText(2, 0, result.getDescription());

			}

			@Override
			public void onFailure(Throwable caught)
			{
				caught.printStackTrace();
			}
		});

	}
	
	
	/**
	 * Create the dialog box for this example.
	 *
	 * @return the new dialog box
	 */
	private DialogBox createDialogBox(String dialogBoxText)
	{
		dialogBox.setGlassEnabled(true);
		dialogBox.setText("Add Blip's Content");

		dialogContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dialogBox.setWidget(dialogContents);
		dialogContents.setSize("239px", "416px");
		
		dialogContents.add(uploadForm);
		uploadForm.setSize("100%", "100%");
		uploadForm.setWidget(mainVerticalPanel);
		
		iuc = new ImageUploadComponent(focusPanel); 
		
		 //The upload form, when submitted, will trigger an HTTP call to the
		  // servlet.  The following parameters must be set
		  uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		  uploadForm.setMethod(FormPanel.METHOD_POST);
		  
		mainVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainVerticalPanel.setSize("100%", "100%");
		mainVerticalPanel.add(blipDescriptionVerticalPanel);
		blipDescriptionVerticalPanel.setSize("235px", "50px");
		blipDescriptionVerticalPanel.add(blipDescriptionLabel);
		blipDescriptionLabel.setSize("95%", "100%");
		blipDescriptionVerticalPanel.add(blipDescriptionTextBox);
		blipDescriptionTextBox.setSize("95%", "23px");
		blipDescriptionTextBox.setName("blipDescriptionTextBox");
		blipDescriptionTextBox.setText("http://blipniptest.com:8888");
		blipImageVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainVerticalPanel.add(blipImageVerticalPanel);
		blipImageVerticalPanel.setSize("95%", "251px");
		blipImageVerticalPanel.setSpacing(7);
		dialogContents.setCellHorizontalAlignment(blipImageVerticalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		blipImageVerticalPanel.add(focusPanel);
		blipImageVerticalPanel.setCellHeight(focusPanel, "33%");
		blipImageVerticalPanel.setCellWidth(focusPanel, "100%");
		focusPanel.setSize("80px", "78px");
		
				focusPanel.setWidget(image);
				image.setSize("100%", "100%");
				image.setUrl("add_image_icon.png");
				dialogContents.setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
		blipImageVerticalPanel.add(fileUpload);
		blipImageVerticalPanel.setCellHeight(fileUpload, "33%");
		blipImageVerticalPanel.setCellWidth(fileUpload, "100%");
		blipImageVerticalPanel.setCellHorizontalAlignment(fileUpload, HasHorizontalAlignment.ALIGN_CENTER);
		fileUpload.setSize("100%", "31px");
		fileUpload.setName("upload");
		blipImageVerticalPanel.add(iuc);
		blipImageVerticalPanel.setCellHeight(iuc, "33%");
		blipImageVerticalPanel.setCellWidth(iuc, "100%");
		iuc.setSize("100%", "38px");
				mainVerticalPanel.add(blipContentButtonsHorizontalPanel);
				blipContentButtonsHorizontalPanel.setSpacing(2);
				
				blipContentButtonsHorizontalPanel.add(submitButton);
				dialogContents.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_CENTER);
				
				cancelButton.setText("Cancel");
				blipContentButtonsHorizontalPanel.add(cancelButton);

		// Return the dialog box
		return dialogBox;
	}

	@Override
	public String getBlipImageURL()
	{
		// TODO Auto-generated method stub
		return image.getUrl();
	}

	@Override
	public HasClickHandlers getButtonSubmitContent()
	{
		// TODO Auto-generated method stub
		return this.submitButton;
	}

	@Override
	public HasClickHandlers getButtonCancel()
	{
		// TODO Auto-generated method stub
		return this.cancelButton;
	}

	@Override
	public void showDialogBox(boolean show)
	{
		if (show == true)
		{
			dialogBox.show();
		}
		else
		{
			dialogBox.hide();
		}
		
		
	}

	@Override
	public void setBlipImage(String imageURL)
	{
		image.setUrl(fileUpload.getFilename());
	}

	@Override
	public Long getContentId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteContent()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnable(boolean enable)
	{
		this.submitButton.setEnabled(enable);
		
	}

}
