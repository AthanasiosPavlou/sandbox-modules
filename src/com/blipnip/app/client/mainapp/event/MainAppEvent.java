package com.blipnip.app.client.mainapp.event;

import com.google.gwt.event.shared.GwtEvent;


public class MainAppEvent  extends GwtEvent<MainAppEventHandler>
{

	public static Type<MainAppEventHandler> TYPE = new Type<MainAppEventHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<MainAppEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(MainAppEventHandler handler)
	{
		handler.onMainApp(this);
	}

}
