package com.blipnip.admin.client.entitymanager.event;

import com.google.gwt.event.shared.GwtEvent;

public class AdminHomeEvent extends GwtEvent<AdminHomeEventHandler> 

{
  public static Type<AdminHomeEventHandler> TYPE = new Type<AdminHomeEventHandler>();
  
  @Override
  public Type<AdminHomeEventHandler> getAssociatedType() 
  {
    return TYPE;
  }

  @Override
  protected void dispatch(AdminHomeEventHandler handler) 
  {
    handler.onAdminHome(this);
  }
}
