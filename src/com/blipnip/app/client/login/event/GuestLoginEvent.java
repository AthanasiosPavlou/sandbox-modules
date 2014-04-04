package com.blipnip.app.client.login.event;

import com.google.gwt.event.shared.GwtEvent;

public class GuestLoginEvent extends GwtEvent<GuestLoginEventHandler>
{

	 public static Type<GuestLoginEventHandler> TYPE = new Type<GuestLoginEventHandler>();
	 

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GuestLoginEventHandler> getAssociatedType()
	{
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(GuestLoginEventHandler handler)
	{
		handler.onGuestLogin(this);
		
	}

}
