package com.blipnip.admin.client.entitymanager.event;

import com.blipnip.admin.client.entitymanager.event.AdminHomeEvent;
import com.google.gwt.event.shared.EventHandler;

public interface AdminHomeEventHandler extends EventHandler {
  void onAdminHome(AdminHomeEvent event);
}
