package com.blipnip.admin.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.blipnip.admin.client.entitymanager.event.AdminHomeEvent;
import com.blipnip.admin.client.entitymanager.event.AdminHomeEventHandler;
import com.blipnip.admin.client.entitymanager.presenter.EntityManagerPresenter;
import com.blipnip.admin.client.entitymanager.service.EntityManagerServiceAsync;
import com.blipnip.admin.client.entitymanager.view.EntityManagerView;
import com.blipnip.admin.client.greeting.event.GreetEvent;
import com.blipnip.admin.client.greeting.event.GreetEventHandler;
import com.blipnip.admin.client.greeting.presenter.GreetingPresenter;
import com.blipnip.admin.client.greeting.service.GreetServiceAsync;
import com.blipnip.admin.client.greeting.view.GreetingView;

public class AdminAppController implements Presenter, ValueChangeHandler<String> 
{

	private final String GREET_PAGE   = "greet";
	private final String PERSIST_PAGE = "persist";
	
	private final EntityManagerServiceAsync rpcEntityManagerService;
	private final GreetServiceAsync rpcGreetingService; 
	private final HandlerManager eventBus;
	private HasWidgets container;

	public AdminAppController(EntityManagerServiceAsync rpcPersistenceService, GreetServiceAsync rpcService, HandlerManager eventBus) 
	{
		this.rpcEntityManagerService = rpcPersistenceService;
		this.rpcGreetingService   = rpcService;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() 
	{
		//Register to receive History events (which will take care of the app's navigation)
		History.addValueChangeHandler(this);

		eventBus.addHandler(GreetEvent.TYPE, new GreetEventHandler() 
		{
			@Override
			public void onSendGreet(GreetEvent event) 
			{
				System.out.println("Event bus received GreetEvent event::"+event.toString());
				//doSendGreet();
				doGreet(); 
				
			}
		});  
		
		eventBus.addHandler(AdminHomeEvent.TYPE, new AdminHomeEventHandler() 
		{
			@Override
			public void onAdminHome(AdminHomeEvent event) 
			{
				System.out.println("Event bus received AdminHomeEvent event::"+event.toString());
				doPersistence();
			}

		});  
	}

	private void doGreet() 
	{
		History.newItem(GREET_PAGE);
	}
	
	private void doPersistence() 
	{
		History.newItem(PERSIST_PAGE);
	}

	public void go(final HasWidgets container) 
	{
		this.container = container;

		if ("".equals(History.getToken())) 
		{
			History.newItem(GREET_PAGE);
		}
		else 
		{
			History.fireCurrentHistoryState();
		}
	}

	/**
	 * 
	 */
	public void onValueChange(ValueChangeEvent<String> event) 
	{
		String token = event.getValue();

		if (token != null) 
		{
			Presenter presenter = null;

			if (token.equals(GREET_PAGE)) 
			{
				presenter = new GreetingPresenter(rpcGreetingService, rpcEntityManagerService, eventBus, new GreetingView());
			}
			if (token.equals(PERSIST_PAGE)) 
			{
				presenter = new EntityManagerPresenter(rpcEntityManagerService, eventBus, new EntityManagerView());
			}

			/**
			 * After presenter has been initialized, go execute it.
			 */
			if (presenter != null) 
			{
				presenter.go(container);
			}
		}
	} 
}
