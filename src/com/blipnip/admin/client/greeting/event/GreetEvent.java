package com.blipnip.admin.client.greeting.event;

import com.google.gwt.event.shared.GwtEvent;


public class GreetEvent extends GwtEvent<GreetEventHandler> 
{
	  public static Type<GreetEventHandler> TYPE = new Type<GreetEventHandler>();
	  
	  @Override
	  public Type<GreetEventHandler> getAssociatedType() 
	  {
	    return TYPE;
	  }

	  @Override
	  protected void dispatch(GreetEventHandler handler) 
	  {
	    handler.onSendGreet(this);
	  }
	}

