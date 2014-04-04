package com.blipnip.admin.client.greeting.event;

import com.google.gwt.event.shared.EventHandler;

public interface GreetEventHandler extends EventHandler 
{
  void onSendGreet(GreetEvent event);
}
