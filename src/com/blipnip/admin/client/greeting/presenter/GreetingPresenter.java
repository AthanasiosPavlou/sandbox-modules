package com.blipnip.admin.client.greeting.presenter;

import com.blipnip.admin.client.Presenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.blipnip.admin.client.entitymanager.event.AdminHomeEvent;
import com.blipnip.admin.client.entitymanager.service.EntityManagerServiceAsync;
import com.blipnip.admin.client.greeting.service.GreetServiceAsync;

/**
 * A presenter is bound to a view. Anything related with the view getting data, calling
 * handlers, setting data, takes place here. 
 * 
 * @author Thanos
 *
 */
public class GreetingPresenter implements Presenter
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
		 * Here we define all getters and setters.
		 * Using these, we will be accessing the view.
		 *  
		 * HasClickHandlers get<element-of-view>();
		 * 
		 * change as you see fit.
		 */
		HasClickHandlers getMainAdminButton();
		HasClickHandlers getQuickTestButton();
		HasClickHandlers getBlipMasterNameTextBox();
		HasClickHandlers geterrorLabel();
		
		HasValue<String> getBlipMasterNameText();
		HasValue<String> getBlipTypeText();
		HasValue<String> getBlipDescriptionText();
		
		
		/**
		 * 
		 * Method setData() is a simple way of getting model data into the view without the view having intrinsic 
		 * knowledge of the model itself. The data being displayed is directly tied to the complexity of our model. 
		 * A more complex model may lead to more data being displayed in a view. The beauty of using setData() is 
		 * that changes to the model can be made without updating the view code.
		 * 
		 * @param data
		 */
		void setData(String data);
	}

	private final GreetServiceAsync rpcGreetService;
	private final EntityManagerServiceAsync rpcEntityManagerService;
	private final HandlerManager eventBus;
	private final Display display;

	
	// The message displayed to the user when the server cannot be reached or returns an error.
	private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again.";
	
	
	public GreetingPresenter(GreetServiceAsync rpcGreetService,EntityManagerServiceAsync rpcEntityManagerService, HandlerManager eventBus, Display view) 
	{
		this.rpcGreetService = rpcGreetService;
		this.rpcEntityManagerService = rpcEntityManagerService;
		this.eventBus = eventBus;
		this.display = view;
	}
	
	/**
	 * 
	 * This method is used for binding the presenter with the view (through the interface - Display in this case).
	 * Since we are using the interface to access the view, we can easily swap views leaving
	 * all other code unaffected.
	 * 
	 */
	public void bind() 
	{
		/**
		 * TODO - Here we define anything happening in the display and eventBus.
		 */
		
		display.getMainAdminButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				eventBus.fireEvent(new AdminHomeEvent());	
		    }
	    });
		
		display.getQuickTestButton().addClickHandler(new ClickHandler() 
		{   
			public void onClick(ClickEvent event) 
		    {
				/**
				 * Adding logic on-click of the send button
				 */
				callService();
				
				/**
				 * Here the event bus is not needed but added as an example for its use. 
				 * Use the event bus for application-wide events such as change of application
				 * screen.
				 * 
				 * The event to be fired is defined in the controller.
				 */
		    }
	    });
		
		
	}

	/**
	 * Main method to be called when using the GreetingPresenter.
	 */
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
	}
	
	/**
	 * On click of the button executre these calls to services.
	 */
	private void callService()
	{
		/**
		 * Just a test, checking that a service can be called.
		 */
		rpcGreetService.greetServer(display.getBlipMasterNameText().getValue(), new AsyncCallback<String>() 
		{

			@Override
			public void onFailure(Throwable caught) 
			{
				System.out.println("@GreetingPresenter, @callService, @rpcGreetService, onFailure::"+caught);
				display.setData(SERVER_ERROR); // or could be something like -> display.showPopup(SEVER_ERROR);
			}

			@Override
			public void onSuccess(String result) 
			{
				System.out.println("@GreetingPresenter, @callService, @rpcGreetService, onSuccess::"+result);
				display.setData(result); // or could be something like -> display.showPopup(result);
			}
			
		});
		
		/**
		 * The actual service that needs to be called.
		 * It gets information provided by the user and saves it on the Datastore.
		 * 
		 * 
		 */
		rpcEntityManagerService.testPersistBlip(display.getBlipMasterNameText().getValue(), display.getBlipTypeText().getValue(), display.getBlipDescriptionText().getValue(), new AsyncCallback<Void>() 
		{
			@Override
			public void onFailure(Throwable caught)
			{
				System.out.println("@GreetingPresenter, @callService, @rpcEntityManagerService, onFailure");
			}

			@Override
			public void onSuccess(Void result)
			{
				System.out.println("@GreetingPresenter, @callService, @rpcEntityManagerService, onSuccess");
				//display.setData(result);
			}
			
		});
		
		
	}
	
	
}
