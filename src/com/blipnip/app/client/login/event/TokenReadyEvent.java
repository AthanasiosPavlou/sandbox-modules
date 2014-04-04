package com.blipnip.app.client.login.event;

import com.google.gwt.event.shared.GwtEvent;

public class TokenReadyEvent extends GwtEvent<TokenReadyEventHandler> 
{

	public static Type<TokenReadyEventHandler> TYPE = new Type<TokenReadyEventHandler>();
	
	private static String token = null; 
	private static String provider = null;
	
	public static String getProvider()
	{
		return provider;
	}

	public static void setProvider(String provider)
	{
		TokenReadyEvent.provider = provider;
	}

	public static String getToken()
	{
		return token;
	}

	public static void setToken(String token)
	{
		TokenReadyEvent.token = token;
	}

	public TokenReadyEvent(String provider, String token)
	{
		TokenReadyEvent.setToken(token);
		TokenReadyEvent.setProvider(provider);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TokenReadyEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(TokenReadyEventHandler handler)
	{
		handler.onTokenReady(this);
	}

}
