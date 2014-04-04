package com.blipnip.admin.client.greeting.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("AdminGreet")
public interface GreetService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
}
