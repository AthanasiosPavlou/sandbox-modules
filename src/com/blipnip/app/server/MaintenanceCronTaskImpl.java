package com.blipnip.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MaintenanceCronTaskImpl extends RemoteServiceServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	  {
		doExectute();
	  }
	
	public void doExectute()
	{
		//TODO
		
		System.out.println("@MaintenanceCronTaskImpl, @doExectute, Cron triggered.");
	}

}
