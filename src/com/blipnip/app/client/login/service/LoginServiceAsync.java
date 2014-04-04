/**
 * Copyright (C) 2012-2013, Markus Sprunck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - The name of its contributor may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */


package com.blipnip.app.client.login.service;

import com.blipnip.app.shared.LoginInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>LoginService</code>.
 */
public interface LoginServiceAsync 
{
	void testLoginServerConnection(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getUserEmail(String token, AsyncCallback<String> callback);

	void useGoogleLoginService(String requestUri, AsyncCallback<LoginInfo> asyncCallback);

	void retrieveUserInfo(boolean isProduction, String provider, String token, AsyncCallback<LoginInfo> asyncCallback);
	
	//New stuff
	void startSession(LoginInfo info, AsyncCallback<LoginInfo> asyncCallback);
	
	void loginUsingSession(AsyncCallback<LoginInfo> asyncCallback);
	
	void removeUserSession(AsyncCallback<String> asyncCallback);

}
