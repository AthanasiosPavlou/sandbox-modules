package com.blipnip.admin.client.entitymanager.view;

import java.util.ArrayList;

import com.blipnip.admin.client.entitymanager.presenter.EntityManagerPresenter;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipMaster;
import com.google.gwt.event.dom.client.HasChangeHandlers;
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
import com.google.gwt.user.client.ui.ListBox;
import com.googlecode.objectify.Key;

/**
 * Given that we are certain that for this sceen we need a button, a text field 
 * and an error label - all of which are defined that as needed by the interface -
 * we can write (and use) whatever implementation we want.  
 * 
 * @author Thanos
 *
 */
public class EntityManagerView extends Composite implements EntityManagerPresenter.Display//TODO implements <xyz>Presenter.Display (need to manually add the import)
{
	private CellPanel cellPanelContainer;
	
	private final Button  persistButton  = new Button("Save User");
	private final TextBox firstNameTextBox = new TextBox();
	private final Label   errorLabel     = new Label();
	
	private final Label   firstNameLabel    = new Label("First Name");
	private final HorizontalPanel operationsPanel = new HorizontalPanel();
	private final Button searchByEmailButton = new Button("Search By E-mail");
	private final Label lastNameLabel = new Label("Last Name");
	private final TextBox lastNameTextBox = new TextBox();
	private final Label emailLabel = new Label("E-mail (indexed)");
	private final TextBox emailTextBox = new TextBox();
	private final Label blipListLabel = new Label("Blip List (Blips Published)");
	private final ListBox blipListBox = new ListBox();
	private final Label NipListLabel = new Label("Nip List (Blips Grabbed)");
	private final ListBox nipListBox = new ListBox();
	private final Button removeNipButton = new Button("Remove Nip(s)");
	private final HorizontalPanel emailAndIdHorizontalPanel = new HorizontalPanel();
	private final Label entryIdLabel = new Label("All Entry Ids (e-mail)");
	private final VerticalPanel emailVerticalPanel = new VerticalPanel();
	private final VerticalPanel entryIdVerticalPanel = new VerticalPanel();
	private final HorizontalPanel firstNameLastNameHorizontalPanel = new HorizontalPanel();
	private final VerticalPanel firstNameVerticalPanel = new VerticalPanel();
	private final VerticalPanel lastNameVerticalPanel = new VerticalPanel();
	private final HorizontalPanel blipNipHorizontalPanel = new HorizontalPanel();
	private final VerticalPanel blipVerticalPanel = new VerticalPanel();
	private final VerticalPanel nipVerticalPanel = new VerticalPanel();
	private final Button removeBlipButton = new Button("Remove Selected Blip(s)");
	private final HorizontalPanel backHorizontalPanel = new HorizontalPanel();
	private final Button homeButton = new Button("Home");
	private final HorizontalPanel toolPanel = new HorizontalPanel();
	private final ListBox entryIdListBox = new ListBox();
	
	@SuppressWarnings("deprecation")
	public EntityManagerView()
	{
		entryIdListBox.setVisibleItemCount(1);
		blipVerticalPanel.add(blipListLabel);
		blipListBox.setMultipleSelect(true);
		blipVerticalPanel.add(blipListBox);
		blipListBox.setWidth("152px");
		blipListBox.setVisibleItemCount(5);
		
		blipVerticalPanel.add(removeBlipButton);
		nipVerticalPanel.add(NipListLabel);
		nipListBox.setMultipleSelect(true);
		nipVerticalPanel.add(nipListBox);
		nipListBox.setWidth("148px");
		nipListBox.setVisibleItemCount(5);
		SimplePanel contentLayoutPanel = new SimplePanel();
	    initWidget(contentLayoutPanel);
	    contentLayoutPanel.setSize("395px", "415px");
	    
	    // For this example we use a cell panel as the container that may be
	    // a DockPanel a HorizontalPanel or a VerticalPanel
	    
	    VerticalPanel container = (VerticalPanel) defineUI();

	    contentLayoutPanel.add(container);
	    cellPanelContainer.setSize("391px", "408px");
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
		emailAndIdHorizontalPanel.setSpacing(5);
		
		cellPanelContainer.add(emailAndIdHorizontalPanel);
		emailAndIdHorizontalPanel.setWidth("382px");
		emailAndIdHorizontalPanel.add(entryIdVerticalPanel);
		entryIdVerticalPanel.setSize("173px", "52px");
		entryIdVerticalPanel.add(entryIdLabel);
		emailAndIdHorizontalPanel.setCellVerticalAlignment(entryIdLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		entryIdVerticalPanel.add(entryIdListBox);
		entryIdListBox.setWidth("174px");
		
		emailAndIdHorizontalPanel.add(emailVerticalPanel);
		emailAndIdHorizontalPanel.setCellHorizontalAlignment(emailVerticalPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		emailAndIdHorizontalPanel.setCellVerticalAlignment(emailLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		emailVerticalPanel.add(emailLabel);
		emailLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		cellPanelContainer.setCellVerticalAlignment(emailLabel, HasVerticalAlignment.ALIGN_BOTTOM);
		emailVerticalPanel.add(emailTextBox);
		firstNameLastNameHorizontalPanel.setSpacing(5);
		
		cellPanelContainer.add(firstNameLastNameHorizontalPanel);
		firstNameLastNameHorizontalPanel.setWidth("382px");
		
		firstNameLastNameHorizontalPanel.add(firstNameVerticalPanel);
		firstNameVerticalPanel.add(firstNameLabel);
		firstNameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		cellPanelContainer.setCellVerticalAlignment(firstNameLabel, HasVerticalAlignment.ALIGN_BOTTOM);
		firstNameVerticalPanel.add(firstNameTextBox);
		lastNameVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		firstNameLastNameHorizontalPanel.add(lastNameVerticalPanel);
		firstNameLastNameHorizontalPanel.setCellHorizontalAlignment(lastNameVerticalPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		lastNameVerticalPanel.add(lastNameLabel);
		lastNameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		cellPanelContainer.setCellVerticalAlignment(lastNameLabel, HasVerticalAlignment.ALIGN_BOTTOM);
		lastNameVerticalPanel.add(lastNameTextBox);
		blipNipHorizontalPanel.setSpacing(5);
		
		cellPanelContainer.add(blipNipHorizontalPanel);
		blipNipHorizontalPanel.setWidth("381px");
		
		blipNipHorizontalPanel.add(blipVerticalPanel);
		cellPanelContainer.setCellVerticalAlignment(blipListLabel, HasVerticalAlignment.ALIGN_BOTTOM);
		
		blipNipHorizontalPanel.add(nipVerticalPanel);
		blipNipHorizontalPanel.setCellHorizontalAlignment(nipVerticalPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		cellPanelContainer.setCellVerticalAlignment(NipListLabel, HasVerticalAlignment.ALIGN_BOTTOM);
		nipVerticalPanel.add(removeNipButton);
		removeNipButton.setText("Remove Selected Nip(s)");
		toolPanel.setSpacing(5);
		toolPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		cellPanelContainer.add(toolPanel);
		toolPanel.setWidth("382px");
		backHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toolPanel.add(backHorizontalPanel);
		cellPanelContainer.setCellHorizontalAlignment(backHorizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		homeButton.setText("Home");
		
		backHorizontalPanel.add(homeButton);
		operationsPanel.setSpacing(1);
		toolPanel.add(operationsPanel);
		toolPanel.setCellHorizontalAlignment(operationsPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		operationsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		operationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellPanelContainer.setCellHorizontalAlignment(operationsPanel, HasHorizontalAlignment.ALIGN_CENTER);
		operationsPanel.add(persistButton);
		cellPanelContainer.setCellVerticalAlignment(persistButton, HasVerticalAlignment.ALIGN_MIDDLE);
		cellPanelContainer.setCellHorizontalAlignment(persistButton, HasHorizontalAlignment.ALIGN_CENTER);
		
		operationsPanel.add(searchByEmailButton);
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
	public HasClickHandlers geterrorLabel() 
	{
		// TODO Auto-generated method stub
		return errorLabel;
	}

	@Override
	public void setUserData(ArrayList<BlipMaster> data) 
	{
		entryIdListBox.clear();
		for (BlipMaster master:data)
		{
			entryIdListBox.addItem(master.getBlipMasterEmail());
		}
		
		System.out.println("@EntityManagerView, @setUserData:"+data);
	}
	
	@Override
	public void setBlipData(ArrayList<Key<Blip>> data) 
	{
		blipListBox.clear();
		for (Key<Blip> blipKey:data)
		{
			blipListBox.addItem(String.valueOf(blipKey.getId()));
		}
		
		System.out.println("View received data:"+data);
	}

	@Override
	public void setNipData(String data) 
	{
		// TODO Auto-generated method stub
		System.out.println("View received data:"+data);
	}
	
	@Override
	public HasValue<String> getEmailText() 
	{
		// TODO Auto-generated method stub
		return emailTextBox;
	}
	
	/**
	 * Get selected item(s) from list box
	 * 
	 * @param currentListBox
	 * @return
	 */
	private ArrayList<String> getSelectedItems(ListBox currentListBox) 
	{
		ArrayList<String> selectedItems = new ArrayList<String>();
	    
		for (int i = 0; i < currentListBox.getItemCount(); i++) 
	    {
	        if (currentListBox.isItemSelected(i)) 
	        {
	            selectedItems.add(currentListBox.getItemText(i));
	        }
	    }
	    return selectedItems;
	}

	@Override
	public ArrayList<String> getSelectedEntryIdList() 
	{
		// TODO Auto-generated method stub
		return getSelectedItems(entryIdListBox);
	}

	@Override
	public ArrayList<String> getSelectedBlipIdList() 
	{
		// TODO Auto-generated method stub
		return getSelectedItems(blipListBox);
	}

	@Override
	public ArrayList<String> getSelectedNipIdList() 
	{
		// TODO Auto-generated method stub
		return getSelectedItems(nipListBox);
	}

	@Override
	public HasClickHandlers getSearchByEmailButton() 
	{
		// TODO Auto-generated method stub
		return searchByEmailButton;
	}
	
	@Override
	public HasClickHandlers getPersistButton() 
	{
		// TODO Auto-generated method stub
		return persistButton;
	}
	
	@Override
	public HasClickHandlers getRemoveNipButton() 
	{
		// TODO Auto-generated method stub
		return removeNipButton;
	}

	@Override
	public HasClickHandlers getRemoveBlipButton() 
	{
		// TODO Auto-generated method stub
		return removeBlipButton;
	}

	@Override
	public HasClickHandlers getHomeButton() 
	{
		// TODO Auto-generated method stub
		return homeButton;
	}

	@Override
	public HasChangeHandlers getEntryIdListBox()
	{
		// TODO Auto-generated method stub
		return entryIdListBox;
	}

	@Override
	public int getEntryIdListBoxSelectedItemIndex()
	{
		// TODO Auto-generated method stub
		return entryIdListBox.getSelectedIndex();
	}

	@Override
	public String getEntryIdListBoxSelectedItem(int index)
	{
		// TODO Auto-generated method stub
		return entryIdListBox.getItemText(index);
	}
}
