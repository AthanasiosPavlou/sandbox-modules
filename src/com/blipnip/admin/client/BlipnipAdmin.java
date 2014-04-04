package com.blipnip.admin.client;

import com.blipnip.admin.client.entitymanager.service.EntityManagerService;
import com.blipnip.admin.client.entitymanager.service.EntityManagerServiceAsync;
import com.blipnip.admin.client.greeting.service.GreetService;
import com.blipnip.admin.client.greeting.service.GreetServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BlipnipAdmin implements EntryPoint 
{
	// Create a remote service proxy to talk to the server-side Greeting service.
	private final GreetServiceAsync greetingService = GWT.create(GreetService.class);

	// Instantiate the interfaces to access methods in the interface
	private final EntityManagerServiceAsync persistentService = GWT.create(EntityManagerService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		HandlerManager eventBus = new HandlerManager(null);
	    AdminAppController appViewer = new AdminAppController(persistentService, greetingService, eventBus);
	    appViewer.go(RootPanel.get("mainContainer"));
	}
}
