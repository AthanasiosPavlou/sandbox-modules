package com.blipnip.app.client.mainapp.view;

import com.blipnip.app.client.mainapp.presenter.MainAppPresenter.AddBlipContentDisplay;
import com.blipnip.app.client.mainapp.service.BlobService;
import com.blipnip.app.client.mainapp.service.BlobServiceAsync;
import com.blipnip.app.shared.BlipContent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.blipnip.app.client.mainapp.utils.eureka.SmallTimeBox;

import java.util.Date;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public class AddContentView extends DialogBox implements AddBlipContentDisplay
{

	private final DialogBox dialogBox = new DialogBox();
	private static BlipContent blipContent = null;
	
	// Create a table to layout the content
	private final static String IMAGE_SIZE = "100px";
	private final Image image = new Image();
	VerticalPanel dialogContents = new VerticalPanel();
	private final HorizontalPanel blipContentButtonsHorizontalPanel = new HorizontalPanel();
	private final Button cancelButton = new Button("Cancel");
	private final Button submitButton = new Button("Publish");
	private final FocusPanel focusPanel = new FocusPanel();
	private final TextArea blipDescriptionTextBox = new TextArea();
	private final FileUpload fileUpload = new FileUpload();
	private final VerticalPanel blipImageVerticalPanel = new VerticalPanel();
	private final VerticalPanel blipDescriptionVerticalPanel = new VerticalPanel();
	private final FormPanel uploadForm = new FormPanel();//new FormPanel(new NamedFrame("_self"));
	private final VerticalPanel mainVerticalPanel = new VerticalPanel();

	// Use an RPC call to the Blob Service to get the blobstore upload url
	BlobServiceAsync blobService = GWT.create(BlobService.class);

	// Used to store URL of app when in production and when in dev. Its value
	// will be used when the form that its part of, gets submitted (using UploadServiceImpl, doPost)
	private final TextBox hiddenAppURLTextBox = new TextBox();
	private final TextBox durationHiddenTextBox = new TextBox();
	private final VerticalPanel blipDurationVerticalPanel = new VerticalPanel();
	private final Label blipDurationLabel = new Label("Blip Duration (hh:mm)");
	private final SmallTimeBox smallTimeBox = new SmallTimeBox((Date) null);
	private final HorizontalPanel horizontalPanel = new HorizontalPanel();

	// Once everything is done, return this id.
	private Long contentId = null;
	private final Label infoLabel = new Label("");
	private final TextBox blipTitleTextBox = new TextBox();
	private final Label blipTitlelabel = new Label("Blip Key Words (i.e. #athens)");
	
	public AddContentView(Long blipId)
	{	
		if (blipId == null)
		{
			System.out.println("@AddContentView, no Blip Id found.");
			
			blipDescriptionTextBox.setEnabled(false);
			fileUpload.setEnabled(false);
			submitButton.setEnabled(false);
		}
		else
		{
			System.out.println("@AddContentView, Blip Id found: "+blipId);
			
			blipDescriptionTextBox.setEnabled(true);
			fileUpload.setEnabled(true);
			submitButton.setEnabled(false);
		}
		createDialogBox(blipId);

		// If the fileUpload is clicked and a file is selected, it will change, thus this handler will fire.
		fileUpload.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				if (blipTitleTextBox.getValue().equals("") || blipDescriptionTextBox.getValue().equals("Say something about this Blip..."))
				{
					uploadForm.reset();
					
					infoLabel.setVisible(true);
        				infoLabel.setText("Please say something about this Blip first.");
        				        				
        				smallTimeBox.getHoursBox().setText("00");
        				smallTimeBox.getMinutesBox().setText("01");
        				
        				// Re-set the title
        				blipTitleTextBox.setValue("#blipnip#athens");
        				
        				// Set the duration of the blip (as retrieved by smallTimeBox)
					durationHiddenTextBox.setText(String.valueOf(smallTimeBox.getValue()));
					
					// Also after submission re-initialize the app URL of the hidden text box 
					// (otherwise it will be blank after submission)
					if (GWT.isProdMode())
					{
						hiddenAppURLTextBox.setText("http://blipnip-app.appspot.com");
					}
					else
					{
						hiddenAppURLTextBox.setText("http://blipniptest.com:8888");
					}
					
					submitButton.setEnabled(false);
				}
				else
				{
					infoLabel.setVisible(false);
    					infoLabel.setText("");
    					
					blobService.getBlobStoreUploadUrl(new AsyncCallback<String>()
					{
						@Override
						public void onSuccess(String result)
						{	
							System.out.println("@AddContentView, @submitButton, @onClick, @blobService, @onSuccess: "+result.toString());
	
							image.setSize(IMAGE_SIZE, IMAGE_SIZE);
							image.setUrl("icons/blipnip_app_icons/mainapp/04_collections_cloud/drawable-xxhdpi-edit/ic_action_cloud.png");
							
							
							contentId = null;
							
							// Set the form action to the newly created blobstore upload URL
							uploadForm.setAction(result.toString());
	
							// Set the duration of the blip (as retrieved by smallTimeBox)
							durationHiddenTextBox.setText(String.valueOf(smallTimeBox.getValue()));
	
							// Submit the form to complete the upload
							// Things to get submitted: blipDescriptionTextBox,  hiddenAppURLTextBox, fileUpload		    			
							uploadForm.submit();
	
							submitButton.setEnabled(true);
							
							//Reset after submission
							//uploadForm.reset();
	
							// Also after submission re-initialize the app URL of the hidden text box 
							// (otherwise it will be blank after submission)
							if (GWT.isProdMode())
							{
								hiddenAppURLTextBox.setText("http://blipnip-app.appspot.com");
							}
							else
							{
								hiddenAppURLTextBox.setText("http://blipniptest.com:8888");
							}
						}
	
						@Override
						public void onFailure(Throwable caught)
						{
							caught.printStackTrace();
							System.out.println("@AddContentView, @submitButton, @onClick, @blobService, @onFailure: "+caught.getMessage());
						}
					});
				}
			} 
		});

		// Add an event handler to the form.
		uploadForm.addSubmitHandler(new FormPanel.SubmitHandler()
		{
			public void onSubmit(SubmitEvent event)
			{
				// This event is fired just before the form is submitted.
				Scheduler.get().scheduleDeferred(new ScheduledCommand() 
			       {
				        	@Override
				        	public void execute()
				        	{
				        		// This event is fired just before the form is submitted.
								image.setUrl("icons/blipnip_app_icons/mainapp/generic/busy/busy.gif");
								System.out.println("@AddContentView, @uploadForm, @addSubmitHandler");
				        	}
			       });
			}
		});

		uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler()
		{
			// When the form submission is successfully completed, this event is fired.
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event)
			{
				//The submit complete Event Results will contain the unique
				//identifier for the content's (blipContent's) meta-data.  Trim it to remove trailing spaces and line breaks
				if (event != null)
				{
					System.out.println("@AddContentView, @uploadForm, @addSubmitCompleteHandler, @event.getResults: " + event.getResults().trim());			
					setContentId(Long.parseLong(event.getResults().trim()));
					getPicture(event.getResults().trim());
				}
				else
				{
					System.out.println("@AddContentView, @uploadForm, @addSubmitCompleteHandler, @event.getResults: "+" event is null");
				}
				
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
				blipContent = result;
				
				//image.setUrl(result.getFullBlobImageUrl());
				
				//image.setUrl("icons/blipnip_app_icons/mainapp/05_content_picture/drawable-xxhdpi/ic_action_picture.png");
				image.setSize("", "");
				image.setUrl(result.getImageUrl()+"=s"+String.valueOf(focusPanel.getOffsetHeight()-10));

				
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
		
		blobService.getBlobFileName(id, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught)
			{
				System.out.println("@AddContentView, @getBlobInfo,  onFailure:  "+caught.getMessage());
			}

			@Override
			public void onSuccess(String result)
			{
				System.out.println("@AddContentView, @getBlobInfo, onSuccess, getFileName:  "+result);
			}});

	}
	
	@Override
	public void deleteContent()
	{
		if (blipContent != null)
		{
			System.out.println("@AddContentView, @deleteImage, blipContent Id:  "+String.valueOf(blipContent.getId()));
			
			blobService.deleteImage(String.valueOf(blipContent.getId()), new AsyncCallback<Void>(){

				@Override
				public void onFailure(Throwable caught)
				{
					System.out.println("@AddContentView, @blobService.deleteImage, onFailure "+caught.getMessage());
				}

				@Override
				public void onSuccess(Void result)
				{
					System.out.println("@AddContentView, @blobService.deleteImage, onSuccess, image deleted ");
					blipContent = null;
					image.setUrl("icons/blipnip_app_icons/mainapp/05_content_picture/drawable-xxhdpi/ic_action_picture.png");
					
				}});
		}
		else
		{
			System.out.println("@AddContentView, @deleteImage, not deleting blipContent is null");
		}
	}


	/**
	 * Create the dialog box for this example.
	 *
	 * @return the new dialog box
	 */
	private DialogBox createDialogBox(Long blipId)
	{
		dialogBox.setAutoHideOnHistoryEventsEnabled(true);
		dialogBox.setGlassEnabled(true);
		
		if (blipId != null && blipId >0)
		{
			dialogBox.setText("Add Blip's Content ("+blipId+")");
		}
		else
		{
			dialogBox.setText("Add Blip's Content (No Blip Selected)");
		}

		dialogContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dialogBox.setWidget(dialogContents);
		dialogContents.setSize("243px", "370px");

		dialogContents.add(uploadForm);
		uploadForm.setSize("100%", "100%");
		mainVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		uploadForm.setWidget(mainVerticalPanel);

		//The upload form, when submitted, will trigger an HTTP call to the
		// servlet.  The following parameters must be set
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		mainVerticalPanel.setSize("100%", "100%");
		mainVerticalPanel.add(blipDescriptionVerticalPanel);
		blipDescriptionVerticalPanel.setSize("95%", "110px");
				infoLabel.setStyleName("serverResponseLabelError");
				
		blipDescriptionVerticalPanel.add(infoLabel);
		
		blipDescriptionVerticalPanel.add(blipTitlelabel);
		blipTitleTextBox.setText("#blipnip#athens");
		blipTitleTextBox.setMaxLength(60);
		blipTitleTextBox.setName("blipTitleTextBox");
		
		blipDescriptionVerticalPanel.add(blipTitleTextBox);
		blipTitleTextBox.setSize("95%", "10px");
		blipDescriptionTextBox.setAlignment(TextAlignment.LEFT);
		blipDescriptionTextBox.setText("Say something about this Blip...");
		blipDescriptionVerticalPanel.add(blipDescriptionTextBox);
		blipDescriptionTextBox.setSize("95%", "30px");
		blipDescriptionTextBox.setName("blipDescriptionTextBox");
		hiddenAppURLTextBox.setEnabled(true);
		hiddenAppURLTextBox.setVisible(false);
		hiddenAppURLTextBox.setName("hiddenAppURLTextBox");

		// Setting on initialization
		if (GWT.isProdMode())
		{
			hiddenAppURLTextBox.setText("http://blipnip-app.appspot.com");
		}
		else
		{
			hiddenAppURLTextBox.setText("http://blipniptest.com:8888");
		}


		blipDescriptionVerticalPanel.add(hiddenAppURLTextBox);
		hiddenAppURLTextBox.setSize("95%", "1px");
		blipImageVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainVerticalPanel.add(blipImageVerticalPanel);
		blipImageVerticalPanel.setSize("95%", "145px");
		blipImageVerticalPanel.setSpacing(7);
		dialogContents.setCellHorizontalAlignment(blipImageVerticalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		blipImageVerticalPanel.add(focusPanel);
		blipImageVerticalPanel.setCellHeight(focusPanel, "50%");
		blipImageVerticalPanel.setCellWidth(focusPanel, "100%");
		focusPanel.setSize("110px", "100px");

		focusPanel.setWidget(image);
		image.setSize(IMAGE_SIZE, IMAGE_SIZE);
		image.setUrl("icons/blipnip_app_icons/mainapp/05_content_picture/drawable-xxhdpi/ic_action_picture.png");
		dialogContents.setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
		blipImageVerticalPanel.add(fileUpload);
		blipImageVerticalPanel.setCellHeight(fileUpload, "50%");
		blipImageVerticalPanel.setCellWidth(fileUpload, "100%");
		fileUpload.setSize("90%", "25px");
		fileUpload.setName("upload");
		
		mainVerticalPanel.add(blipDurationVerticalPanel);
		blipDurationVerticalPanel.setSize("95%", "50px");
		durationHiddenTextBox.setVisible(false);
		durationHiddenTextBox.setEnabled(true);
		durationHiddenTextBox.setName("durationHiddenTextBox");
		blipDurationVerticalPanel.add(durationHiddenTextBox);
		durationHiddenTextBox.setSize("95%", "1px");
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		blipDurationVerticalPanel.add(horizontalPanel);
		horizontalPanel.setWidth("95%");
		blipDurationVerticalPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(blipDurationLabel);
		blipDurationLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		blipDurationVerticalPanel.setCellHeight(blipDurationLabel, "100%");
		blipDurationVerticalPanel.setCellWidth(blipDurationLabel, "100%");
		blipDurationVerticalPanel.setCellVerticalAlignment(blipDurationLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		blipDurationLabel.setHeight("20px");
		horizontalPanel.add(smallTimeBox);
		horizontalPanel.setCellHorizontalAlignment(smallTimeBox, HasHorizontalAlignment.ALIGN_CENTER);

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
	public String getBlipImageURL() { return image.getUrl();}

	@Override
	public HasClickHandlers getButtonSubmitContent()	{ return this.submitButton;	}

	@Override
	public HasClickHandlers getButtonCancel() { return this.cancelButton;	}

	@Override
	public void showDialogBox(boolean show)
	{
		if (show == true)
		{
			dialogBox.setPopupPosition(0, 0);
			dialogBox.show();
		}
		else
		{
			dialogBox.hide();
		}
	}

	@Override
	public void setBlipImage(String imageURL) {	image.setUrl(fileUpload.getFilename());	}

	@Override
	public Long getContentId()	{return contentId;	}

	public void setContentId(Long contentId)	{ this.contentId = contentId; }

	@Override
	public void setEnable(boolean enable)
	{
		this.submitButton.setEnabled(enable);
		
	}

}
