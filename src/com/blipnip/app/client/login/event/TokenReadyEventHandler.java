package com.blipnip.app.client.login.event;

import com.google.gwt.event.shared.EventHandler;

public interface TokenReadyEventHandler extends EventHandler
{
	void onTokenReady(TokenReadyEvent event);
}
