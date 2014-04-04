package com.blipnip.admin.client.entitymanager.presenter;

import java.util.ArrayList;

import com.blipnip.admin.client.Presenter;
import com.blipnip.admin.client.entitymanager.service.EntityManagerServiceAsync;
import com.blipnip.admin.client.greeting.event.GreetEvent;
import com.blipnip.app.shared.Blip;
import com.blipnip.app.shared.BlipMaster;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.objectify.Key;

/**
 * A presenter is bound to a view. Anything related with the view getting data, calling
 * handlers, setting data, takes place here. 
 * 
 * @author sdepdev
 *
 */
public class EntityManagerPresenter implements Presenter
{

	/**
	 * We are defining this interface in this class for ease of use (we could 
	 * have it a separate file but its easier this way).
	 * This interface describes the View that will be bound by this presenter.
	 * Thus, the view must implement it.
	 * 
	 * @author Thanos
	 *
	 */
	public interface Display 
	{
		Widget asWidget();
		
		/**
		 * TODO - Here we define all getters and setters.
		 * Using these, we will be accessing the view.
		 *  
		 * HasClickHandlers get<element-of-view>();
		 * 
		 * change as you see fit.
		 */
		HasClickHandlers getPersistButton();
		HasClickHandlers getSearchByEmailButton();
		HasClickHandlers getRemoveNipButton();
		HasClickHandlers getRemoveBlipButton();
		HasClickHandlers getHomeButton();
		HasChangeHandlers getEntryIdListBox();
		HasValue<String> getEmailText();
		HasClickHandlers geterrorLabel();
		
		int getEntryIdListBoxSelectedItemIndex(); 
		String getEntryIdListBoxSelectedItem(int index); 
		
		ArrayList<String> getSelectedEntryIdList();
		ArrayList<String> getSelectedBlipIdList();
		ArrayList<String> getSelectedNipIdList();
		
		/**
		 * 
		 * Method setData() is a simple way of getting model data into the view without the view having intrinsic 
		 * knowledge of the model itself. The data being displayed is directly tied to the complexity of our model. 
		 * A more complex model may lead to more data being displayed in a view. The beauty of using setData() is 
		 * that changes to the model can be made without updating the view code.
		 * 
		 * @param data
		 */
		void setUserData(ArrayList<BlipMaster> data);
		void setBlipData(ArrayList<Key<Blip>> data);
		void setNipData(String data);
	}

	private final EntityManagerServiceAsync rpcEntityManagerService;
	private final HandlerManager eventBus;
	private final Display display;

    public EntityManagerPresenter(EntityManagerServiceAsync rpcService, HandlerManager eventBus, Display view) 
	{
		System.out.println("EntityManagerPresenter");
		this.rpcEntityManagerService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}
	
	/**
	 * 
	 * This method is used for binding the presenter with the view (through the interface).
	 * Since we are using the interface to access the view, we can easily swap views leaving
	 * all other code unaffected.
	 * 
	 */
	public void bind() 
	{
		/**
		 * TODO - Here we define anything happening in the display and eventBus.
		 */
		
		display.getEntryIdListBox().addChangeHandler(new ChangeHandler()
		 {
			  public void onChange(ChangeEvent event)
			  {
				  System.out.println("@EntityManagerPresenter, @bind, getEntryIdListBox: "+event.toString());
				  
				  int selectedIndex = display.getEntryIdListBoxSelectedItemIndex();
				  if (selectedIndex >= 0)
				  {
					  System.out.println("@EntityManagerPresenter, @bind, callGetBlipKeys ");
					  callGetBlipKeys(display.getEntryIdListBoxSelectedItem(selectedIndex));
					  //Window.alert("Something got selected " + listBox.getValue(selectedIndex));
				  }
				   
			  }
			 });
		
		display.getPersistButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				/**
				 * Adding logic on-click of the send button
				 * 
				 * call<name>Service();
				 */
		    }
	    });
		
		display.getSearchByEmailButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				callGetMastersService();
		    }
	    });
		
		
		display.getRemoveBlipButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				/**
				 * Adding logic on-click of the send button
				 * 
				 * call<name>Service();
				 */
		    }
	    });
		
		display.getRemoveNipButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				/**
				 * Adding logic on-click of the send button
				 * 
				 * call<name>Service();
				 */
		    }
	    });
		
		
		display.getHomeButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				/**
				 * Adding logic on-click of the send button
				 * 
				 * call<name>Service();
				 */
				
				/**
				 * Here the event bus is needed because we are changing screens
				 */
				eventBus.fireEvent(new GreetEvent());
		    }
	    });

	}

	@Override
	public void go(final HasWidgets container) 
	{
		bind();
		container.clear();
		container.add(display.asWidget());
		
		/**
		 * Any additional actions required when this class 
		 * is called (such as calling the service).
		 * 
		 * i.e. callService();
		 */
		
		callGetMastersService();
	}
	
	private void callGetMastersService()
	{	
		rpcEntityManagerService.getBlipMasters(new AsyncCallback<ArrayList<BlipMaster>>() 
		{

			@Override
			public void onFailure(Throwable caught)
			{
				System.out.println("@EntityManagerPresenter, @callGetMastersService, onFailure: "+caught.getMessage());
				
			}

			@Override
			public void onSuccess(ArrayList<BlipMaster> result)
			{
				for (BlipMaster currentMaster:result)
				{
					System.out.println("@EntityManagerPresenter, @callGetMastersService, onSuccess: "+currentMaster.getBlipMasterEmail());
					display.setUserData(result);
				}
			}
		});			
	}
	
	private void callGetBlipKeys(String blipMasterName)
	{	
		rpcEntityManagerService.getBlipKeysForMaster(blipMasterName, new AsyncCallback<ArrayList<Key<Blip>>>() 
		{

			@Override
			public void onFailure(Throwable caught)
			{
				System.out.println("@EntityManagerPresenter, @callGetBlipKeys, onFailure: "+caught.getMessage());

			}

			@Override
			public void onSuccess(ArrayList<Key<Blip>> result)
			{
				for (Key<Blip> currentBlipKey:result)
				{
					System.out.println("@EntityManagerPresenter, @callGetBlipKeys, onSuccess: "+currentBlipKey.getId());
				}
				display.setBlipData(result);
			}
		});
	}
	
	
}
