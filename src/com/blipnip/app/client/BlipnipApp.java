package com.blipnip.app.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.blipnip.app.client.login.service.LoginService;
import com.blipnip.app.client.login.service.LoginServiceAsync;
import com.blipnip.app.client.mainapp.service.MainAppService;
import com.blipnip.app.client.mainapp.service.MainAppServiceAsync;
import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 *   We use this safe execution method so that when there is a problem in module 
 *   loading, the exception wont pop up on the browser in case this was used by a 
 *   user and not a developer. It will rather get thrown on the console and the web 
 *   page will just not load it. So this is immediately a step up for user interaction 
 *   - next step to actually provide a dialog that says "There was an error loading up
 *   Please send error log..." so we can check it out.
 *   
 *   To test this try calling a presenter that is present in another module that is not 
 *   inherited (thus module trying to load will fail because it will not find the class)
 *   Another way to test this, you can for example set in the html file to load up open 
 *   layers maps from the local js file and point to a wrong folder.
 * 
 * @author Thanos
 *
 */
public class BlipnipApp implements EntryPoint
{
	private static Logger rootLogger = Logger.getLogger("");

	/** Create a remote service proxy to talk to the server-side Greeting service. */
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	private final MainAppServiceAsync mainAppService = GWT.create(MainAppService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		rootLogger.log(Level.INFO, "*Application Start*");
		safeExecution();
	}

	private void safeExecution()
	{
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() 
		{  
			public void onUncaughtException(Throwable e) 
			{  
				rootLogger.log(Level.SEVERE, "Failed to load Blipnip module.", e);  
			}
		});

		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute() 
			{

				/**
				 * Instead of simply calling startApplication, we call
				 * startAppSession, which checks for the existence of 
				 * a session cookie (called "sid").
				 */
				startAppSession();
				//startApplication();
			}
		});
	}

	/**
	 * This method "wraps" the application in a session if a cookie 
	 * that is still valid exists. If the cookie is not found or if it has 
	 * expired then the application starts with no further action.
	 * If the cookie is there and is valid, then the LoginInfo stored
	 * in the cookie are retrieved and set to the MainAppController.
	 */
	private void startAppSession()
	{
		String sessionID = Cookies.getCookie("sid");
		if (sessionID == null)
		{
			System.out.println("@BlipnipApp, @startAppSession, sessionID==null");
			startApplication();
		} 
		else
		{
			checkWithServerIfSessionIdIsStillLegal(sessionID);
		}
	}

	private void checkWithServerIfSessionIdIsStillLegal(String sessionID)
	{
		loginService.loginUsingSession(new AsyncCallback<LoginInfo>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				startApplication();
			}

			@Override
			public void onSuccess(LoginInfo result)
			{
				if (result == null)
				{
					System.out.println("@BlipnipApp, @checkWithServerIfSessionIdIsStillLegal, no user info retrieved from session (result=null)");
					startApplication();
				} 
				else
				{
					if (!result.getUserName().equals(""))
					{

						System.out.println("@BlipnipApp, @checkWithServerIfSessionIdIsStillLegal, user info retrieved from session: "+result);
						HandlerManager eventBus = new HandlerManager(null);
						MainAppController appViewer = new MainAppController(loginService, mainAppService, eventBus);
						appViewer.setLoginInfo(result);
						appViewer.go(RootPanel.get("mainContainer"));
					} 
					else
					{
						startApplication();
					}
				}
			}
		});
	}

	/**
	 * This is the entry point method.
	 * @wbp.parser.entryPoint
	 */
	private void startApplication()
	{	
		HandlerManager eventBus = new HandlerManager(null);
		MainAppController appViewer = new MainAppController(loginService, mainAppService, eventBus);
		appViewer.go(RootPanel.get("mainContainer"));
	}
}
