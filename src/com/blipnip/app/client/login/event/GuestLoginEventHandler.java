package com.blipnip.app.client.login.event;

import com.google.gwt.event.shared.EventHandler;

public interface GuestLoginEventHandler extends EventHandler
{
	void onGuestLogin(GuestLoginEvent event);
}
