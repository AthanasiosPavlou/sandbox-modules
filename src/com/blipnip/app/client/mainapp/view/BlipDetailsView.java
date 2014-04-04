package com.blipnip.app.client.mainapp.view;

import com.blipnip.app.client.mainapp.presenter.MainAppPresenter.MainAppDisplay;
import com.blipnip.app.shared.Blip;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;

public class BlipDetailsView extends DialogBox
{
	private final PushButton pshbtnClose = new PushButton("Close");
	private final Image image = new Image("icons/blipnip_app_icons/mainapp/05_content_picture/drawable-hdpi/ic_action_picture.png");
	private final Label blipDescriptionLabel = new Label();
	private final Label blipOverviewLabel = new Label();
	
	
	public Image getImage()
	{
		return image;
	}
	
	public Label getTxtrSampleBlipDescription()
	{
		return blipDescriptionLabel;
	}
	
	public Label getBlipOverviewLabel()
	{
		return blipOverviewLabel;
	}
	
	public PushButton getpshbtnClose()
	{
		return pshbtnClose;
	}

	/**
	 * @wbp.parser.constructor
	 */
	public BlipDetailsView()
	{
		setAutoHideOnHistoryEventsEnabled(true);
		setSize("100%", "100%");
		setGlassEnabled(true);
		setAnimationEnabled(false);
		
		SimplePanel simplePanel = new SimplePanel();
		setWidget(simplePanel);
		simplePanel.setSize("400px", "500px");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		simplePanel.setWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");
		
		VerticalPanel mainVerticalPanel = new VerticalPanel();
		mainVerticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainVerticalPanel.setStyleName("blipnip-gwt-view-blip-details");
		mainVerticalPanel.setSpacing(5);
		verticalPanel.add(mainVerticalPanel);
		verticalPanel.setCellVerticalAlignment(mainVerticalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(mainVerticalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellHeight(mainVerticalPanel, "90%");
		mainVerticalPanel.setSize("100%", "100%");
		
		SimplePanel blipOverviewPanel = new SimplePanel();
		mainVerticalPanel.add(blipOverviewPanel);
		mainVerticalPanel.setCellHeight(blipOverviewPanel, "5%");
		mainVerticalPanel.setCellWidth(blipOverviewPanel, "95%");
		blipOverviewPanel.setSize("100%", "100%");
		
		blipOverviewLabel.setStyleName("blipnip-gwt-view-blip-details-text");
		blipOverviewPanel.setWidget(blipOverviewLabel);
		blipOverviewLabel.setSize("100%", "100%");
		
		DeckPanel deckPanel = new DeckPanel();
		deckPanel.setAnimationEnabled(true);
		mainVerticalPanel.add(deckPanel);
		mainVerticalPanel.setCellHorizontalAlignment(deckPanel, HasHorizontalAlignment.ALIGN_CENTER);
		deckPanel.setSize("100%", "100%");
		mainVerticalPanel.setCellHeight(deckPanel, "85%");
		mainVerticalPanel.setCellWidth(deckPanel, "100%");
		
		VerticalPanel imageVerticalPanel = new VerticalPanel();
		imageVerticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		imageVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		deckPanel.add(imageVerticalPanel);
		imageVerticalPanel.setSize("100%", "100%");
		imageVerticalPanel.add(image);
		imageVerticalPanel.setCellHeight(image, "100%");
		imageVerticalPanel.setCellWidth(image, "100%");
		image.setSize("100%", "100%");
		deckPanel.showWidget(0);
		
		SimplePanel blipDescriptionPanel = new SimplePanel();
		mainVerticalPanel.add(blipDescriptionPanel);
		mainVerticalPanel.setCellHeight(blipDescriptionPanel, "10%");
		blipDescriptionPanel.setSize("100%", "100%");
		mainVerticalPanel.setCellWidth(blipDescriptionPanel, "95%");
		verticalPanel.setCellHeight(blipDescriptionPanel, "15%");
		blipDescriptionPanel.setWidget(blipDescriptionLabel);
		blipDescriptionLabel.setSize("100%", "100%");
		blipDescriptionLabel.setStyleName("blipnip-gwt-view-blip-details-text");
		blipDescriptionLabel.setText("Sample Blip Description");
		
		VerticalPanel bottomVerticalPanel = new VerticalPanel();
		bottomVerticalPanel.setStyleName("blipnip-gwt-Menu");
		bottomVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		bottomVerticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(bottomVerticalPanel);
		verticalPanel.setCellHeight(bottomVerticalPanel, "10%");
		bottomVerticalPanel.setSize("100%", "100%");
		
		HorizontalPanel buttonHorizontalPanel = new HorizontalPanel();
		buttonHorizontalPanel.setSpacing(2);
		buttonHorizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		bottomVerticalPanel.add(buttonHorizontalPanel);
		buttonHorizontalPanel.setSize("100%", "95%");

		HorizontalPanel nipHorizontalPanel = new HorizontalPanel();
		nipHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonHorizontalPanel.add(nipHorizontalPanel);
		buttonHorizontalPanel.setCellHeight(nipHorizontalPanel, "100%");
		buttonHorizontalPanel.setCellWidth(nipHorizontalPanel, "33%");
		nipHorizontalPanel.setSize("100%", "100%");
		
		PushButton pshbtnNip = new PushButton("Nip");
		pshbtnNip.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/03_rating_favorite/drawable-mdpi/ic_action_favorite.png"));
		nipHorizontalPanel.add(pshbtnNip);
		pshbtnNip.setSize("32px", "32px");
		
		HorizontalPanel socialHorizontalPanel = new HorizontalPanel();
		socialHorizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		socialHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonHorizontalPanel.add(socialHorizontalPanel);
		buttonHorizontalPanel.setCellHeight(socialHorizontalPanel, "100%");
		socialHorizontalPanel.setSize("100%", "100%");
		buttonHorizontalPanel.setCellWidth(socialHorizontalPanel, "33%");
		
		PushButton pshbtnShare = new PushButton("Share");
		pshbtnShare.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_share/drawable-mdpi/ic_action_share.png"));
		socialHorizontalPanel.add(pshbtnShare);
		pshbtnShare.setSize("32px", "32px");
		
		HorizontalPanel closeHorizontalPanel = new HorizontalPanel();
		closeHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		closeHorizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonHorizontalPanel.add(closeHorizontalPanel);
		closeHorizontalPanel.setSize("100%", "100%");
		buttonHorizontalPanel.setCellHeight(closeHorizontalPanel, "100%");
		buttonHorizontalPanel.setCellWidth(closeHorizontalPanel, "33%");
		
		
		pshbtnClose.getUpFace().setImage(new Image("icons/blipnip_app_icons/mainapp/01_core_cancel/drawable-mdpi/ic_action_cancel.png"));
		closeHorizontalPanel.add(pshbtnClose);
		pshbtnClose.setSize("32px", "32px");
		
		
	}
	
	public BlipDetailsView(final MainAppDisplay mainAppDisplay, Blip blip)
	{
		//by doing this, the map becomes unclickable - TODO nice little trick, may come in handy.
		mainAppDisplay.getMapComponent().bringSimpleMapToBack();
		
		final BlipDetailsView view = new BlipDetailsView();
		
		// Showing setup
		view.center();
		view.getElement().getFirstChildElement().getStyle().setZIndex(10);
		
		// Setting parameters
		if (blip.getBlipContent() != null)
		{
			view.getTxtrSampleBlipDescription().setText(blip.getBlipContent().getDescription());
			view.getBlipOverviewLabel().setText("f."+blip.getBlipMasterUsername()+" - "+blip.getBlipContent().getTitle());
			view.getImage().setSize("", "");
			view.getImage().setUrl(blip.getBlipContent().getImageUrl()+"=s350");
		}
		
		
		view.getpshbtnClose().addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				mainAppDisplay.getMapComponent().bringSimpleMapToFront();
				view.hide();
			}});
		
		view.show();
	}

	public BlipDetailsView(boolean autoHide)
	{
		super(autoHide);
	}

}
