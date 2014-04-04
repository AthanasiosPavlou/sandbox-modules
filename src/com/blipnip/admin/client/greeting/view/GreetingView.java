package com.blipnip.admin.client.greeting.view;

import com.blipnip.admin.client.greeting.presenter.GreetingPresenter;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;

/**
 * Given that we are certain that for this sceen we need a button, a text field 
 * and an error label - all of which are defined that as needed by the interface -
 * we can write (and use) whatever implementation we want.  
 * 
 * @author Thanos
 *
 */
public class GreetingView extends Composite implements GreetingPresenter.Display//TODO implements <xyz>Presenter.Display
{
	private CellPanel cellPanelContainer;
	
	private final Button quickTestButton = new Button("QuickTest");
	private final TextBox blipMasterNameTextBox = new TextBox();
	private final Label errorLabel  = new Label();
	private final Label quickTestLabel = new Label("Quick Test Entity Persistence");
	private final HorizontalPanel blipMasterHorizontalPanel = new HorizontalPanel();
	private final Button mainAdminButton = new Button("New button");
	private final Label blipMasterNameLabel = new Label("Blip Master Name");
	private final VerticalPanel masterNameVerticalPanel = new VerticalPanel();
	private final VerticalPanel blipTypeVerticalPanel = new VerticalPanel();
	private final Label blipTypeLabel = new Label("Blip Type");
	private final TextBox blipTypeTextBox = new TextBox();
	private final VerticalPanel blipDescriptionVerticalPanel = new VerticalPanel();
	private final Label blipDescriptionLabel = new Label("Blip Description");
	private final TextBox blipDescriptionTextBox = new TextBox();
	private final DecoratorPanel quickTestDecoratorPanel = new DecoratorPanel();
	private final VerticalPanel quickTestVerticalPanel = new VerticalPanel();
	
	public GreetingView()
	{
		SimplePanel contentLayoutPanel = new SimplePanel();
	    initWidget(contentLayoutPanel);
	    contentLayoutPanel.setSize("576px", "256px");
	    //contentLayoutPanel.setSize("296px", "159px");
	    
	    // For this example we use a cell panel as the container that may be
	    // a DockPanel a HorizontalPanel or a VerticalPanel
	    
	    VerticalPanel container = (VerticalPanel) defineUI();

	    contentLayoutPanel.add(container);
	    cellPanelContainer.setWidth("562px");
	    
	    cellPanelContainer.add(quickTestDecoratorPanel);
	    quickTestVerticalPanel.setSpacing(10);
	    quickTestVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    quickTestDecoratorPanel.setWidget(quickTestVerticalPanel);
	    quickTestVerticalPanel.add(quickTestLabel);
	    cellPanelContainer.setCellVerticalAlignment(blipMasterHorizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	    quickTestVerticalPanel.add(blipMasterHorizontalPanel);
	    blipMasterHorizontalPanel.add(masterNameVerticalPanel);
	    masterNameVerticalPanel.add(blipMasterNameLabel);
	    blipMasterHorizontalPanel.setCellVerticalAlignment(blipMasterNameLabel, HasVerticalAlignment.ALIGN_MIDDLE);
	    blipMasterNameTextBox.setText("default@sample.com");
	    masterNameVerticalPanel.add(blipMasterNameTextBox);
	    blipMasterHorizontalPanel.setCellVerticalAlignment(blipMasterNameTextBox, HasVerticalAlignment.ALIGN_MIDDLE);
	    
	    blipMasterHorizontalPanel.add(blipTypeVerticalPanel);
	    
	    blipTypeVerticalPanel.add(blipTypeLabel);
	    blipTypeTextBox.setText("#standard#custom#shop");
	    
	    blipTypeVerticalPanel.add(blipTypeTextBox);
	    blipMasterHorizontalPanel.add(blipDescriptionVerticalPanel);
	    
	    blipDescriptionVerticalPanel.add(blipDescriptionLabel);
	    blipDescriptionTextBox.setText("This is a short description");
	    
	    blipDescriptionVerticalPanel.add(blipDescriptionTextBox);
	    quickTestVerticalPanel.add(quickTestButton);
	    quickTestButton.setText("Quick Save Blip");
	    mainAdminButton.setText("Go To Main Admin");
	    cellPanelContainer.add(mainAdminButton);
	    mainAdminButton.setWidth("175px");
	}

	/**
	 * Here we define our UI as we want for this implementation.
	 * 
	 * @return
	 */
	private CellPanel defineUI()
	{
		//TODO define all UI Stuff here - not needed if GWT Designer is used as it will do the work for us.

		cellPanelContainer = new VerticalPanel();
		cellPanelContainer.setSpacing(10);
		cellPanelContainer.setCellVerticalAlignment(quickTestLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		cellPanelContainer.setCellHorizontalAlignment(quickTestLabel, HasHorizontalAlignment.ALIGN_CENTER);
		cellPanelContainer.setCellVerticalAlignment(blipMasterNameTextBox, HasVerticalAlignment.ALIGN_MIDDLE);
		cellPanelContainer.setCellHorizontalAlignment(blipMasterNameTextBox, HasHorizontalAlignment.ALIGN_CENTER);
		cellPanelContainer.setCellVerticalAlignment(quickTestButton, HasVerticalAlignment.ALIGN_MIDDLE);
		cellPanelContainer.setCellHorizontalAlignment(quickTestButton, HasHorizontalAlignment.ALIGN_CENTER);
		cellPanelContainer.add(errorLabel);
		
		((VerticalPanel) cellPanelContainer).setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		((VerticalPanel) cellPanelContainer).setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		
		return cellPanelContainer;
	}
	

	/**
	 * From here onwards, is the implementation of 
	 * whatever interface is defined.
	 * 
	 */
	
	@Override
	public HasClickHandlers getQuickTestButton() 
	{
		// TODO Auto-generated method stub
		return quickTestButton;
	}

	@Override
	public HasClickHandlers getBlipMasterNameTextBox() 
	{
		// TODO Auto-generated method stub
		return blipMasterNameTextBox;
	}

	@Override
	public HasClickHandlers geterrorLabel() 
	{
		// TODO Auto-generated method stub
		return errorLabel;
	}

	@Override
	public void setData(String data) 
	{
		// TODO - Set whatever data is needed. Could be of different type (not String) 
		//but this is defined in the interface for now.
		
		System.out.println("View received data:"+data);
		
	}

	@Override
	public HasValue<String> getBlipMasterNameText() 
	{
		// TODO Auto-generated method stub
		return blipMasterNameTextBox;
	}

	@Override
	public HasClickHandlers getMainAdminButton()
	{
		// TODO Auto-generated method stub
		return this.mainAdminButton;
	}

	@Override
	public HasValue<String> getBlipTypeText()
	{
		// TODO Auto-generated method stub
		return this.blipTypeTextBox;
	}

	@Override
	public HasValue<String> getBlipDescriptionText()
	{
		// TODO Auto-generated method stub
		return this.blipDescriptionTextBox;
	}



}
