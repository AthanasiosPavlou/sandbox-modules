package com.blipnip.app.shared;

import java.io.Serializable;

public class LoginInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String userName;

	private boolean logStatus = false;

	private String loginUrl;

	private String logoutUrl;

	private String emailAddress;

	private String nickname;

	private String pictureUrl;

	private String loginRank = "default";
	
	private String sessionId = null;
	
	public LoginInfo()
	{
		
	}
	
	public boolean isLogged()
	{
		return logStatus;
	}

	public void setLoggedStatus(boolean loggedIn)
	{
		this.logStatus = loggedIn;
	}

	public String getLoginUrl()
	{
		return loginUrl;
	}

	public void setLoginUrl(final String loginUrl)
	{
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl()
	{
		return logoutUrl;
	}

	public void setLogoutUrl(final String logoutUrl)
	{
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(final String nickname)
	{
		this.nickname = nickname;
	}

	public void setPictureUrl(final String pictureUrl)
	{
		this.pictureUrl = pictureUrl;

	}

	public String getPictureUrl()
	{
		return pictureUrl;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userId)
	{
		this.userName = userId;
	}
	
	public void setLoginRank(String rank)
	{
		this.loginRank = rank;
	}
	
	public String getLoginRank()
	{
		return this.loginRank;
	}
	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	
}
