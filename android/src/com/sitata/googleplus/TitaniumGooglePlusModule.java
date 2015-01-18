/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.sitata.googleplus;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;



@Kroll.module(name="TitaniumGooglePlus", id="com.sitata.googleplus")
public class TitaniumGooglePlusModule extends KrollModule implements
		TiActivityResultHandler,
		com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
		com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
{

	// User did not pick account properly / hit cancel on dialog
	@Kroll.property
	public static final String SIGN_IN_CANCELLED = "tigp:signInCancelled";

	// There was an error during the sign in process or the user did not grant
	// permissions
	@Kroll.property
	public static final String SIGN_IN_ERROR = "tigp:signInError";

	// could be network is down
	@Kroll.constant
	public static final String IO_EXCEPTION = "tigp:ioException";

	// Some other type of unrecoverable exception has occurred.
	// Report and log the error as appropriate for your app.
	@Kroll.constant
	public static final String FATAL_EXCEPTION = "tigp:fatalException";

	// Standard Debugging variables
	private static final String TAG = "TitaniumGooglePlusModule";

	protected int recoveryRequestCode;
	private static final int RC_SIGN_IN = 99;
	private String mClientId;
	private String[] mScopes;
	private String mEmail;
	private KrollFunction successCallback;
	private KrollFunction errorCallback;

	/* Client used to interact with Google APIs. */
	private static GoogleApiClient mGoogleApiClient;

	private Boolean mIntentInProgress = false;
	private Boolean mFetchingToken = false;
	private Boolean mClearingAccount = false;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public TitaniumGooglePlusModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(TAG, "inside onAppCreate");
		// put module init code that needs to run when the application is created
	}

	@Override
	public void onStart(Activity activity) {
		super.onStart(activity);
		// We don't want to put the mGoogleApiClient.connect() here as it might
		// trigger an account chooser dialog before we actually want it.
	}

	@Override
	public void onStop(Activity activity) {
		super.onStop(activity);
		if (mGoogleApiClient.isConnected()) {
			Log.d(TAG, "On stop and disconnecting");
			mGoogleApiClient.disconnect();
		}
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(TAG, "OnConnectionFailed");

		// Called when there was an error connecting the client to the service.

		if (!mIntentInProgress && result.hasResolution()) {

			mIntentInProgress = true;

			// allow Google Play services to solicit any user interaction needed
			// to resolve sign in errors (for example by asking the user to
			// select an account, consent to permissions, enable networking,
			// etc).
			Activity activity = TiApplication.getAppCurrentActivity();
			TiActivitySupport support = (TiActivitySupport) activity;
			support.launchIntentSenderForResult(result.getResolution()
					.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0, null, this);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "Connected!");
		// We've resolved any connection errors. mGoogleApiClient can be used
		// to access Google APIs on behalf of the user.

		// After calling connect(), this method will be invoked asynchronously
		// when the connect request has successfully completed.
		if (mClearingAccount) {
			// if we intended to clear the account, mClearingAccount will have
			// been set to true prior to connecting
			clearAccount();
		} else {
			if (!mFetchingToken) {
				mFetchingToken = true;
				mEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
				fetchAccessToken();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.d(TAG, "On Suspended!");
		// Google Play services will trigger the onConnectionSuspended callback
		// if our Activity loses its service connection. Typically you will want
		// to attempt to reconnect when this happens in order to retrieve a new
		// ConnectionResult that can be resolved by the user.
		mGoogleApiClient.connect();
	}

	@Override
	public void onError(Activity activity, int requestCode, Exception e) {
		Logger.getLogger(TAG).info("ON ERROR called. " + requestCode);
		Log.d(TAG, "EXCEPTION: " + e.getMessage());

		if (requestCode == RC_SIGN_IN) {
			handleError(SIGN_IN_ERROR);
		}

	}

	@Override
	public void onResult(Activity activity, int thisRequestCode,
			int resultCode, Intent data) {
		Logger.getLogger(TAG).info(
				"On Result - Request Code is: " + thisRequestCode
						+ " - Result Code is: " + resultCode);


		if (thisRequestCode == RC_SIGN_IN) {
			mIntentInProgress = false;
			// At this point, the user has chosen an account and we need to try
			// to connect again

			// 0 when cancelled picking an account or when refusing permissions
			// -1 when picked account and already gave permissions

			if (resultCode == Activity.RESULT_OK) {
				// Because the resolution for the connection failure was started
				// with startIntentSenderForResult (launchIntentSenderForResult in
				// Titanium) and the code RC_SIGN_IN, we can
				// capture the result inside Activity.onActivityResult.
				if (!mGoogleApiClient.isConnecting()) {
					Log.d(TAG, "Connectiong again");
					mGoogleApiClient.connect();
				}
			} else {
				handleError(SIGN_IN_CANCELLED);
			}

		} else if (thisRequestCode == recoveryRequestCode) {
			mFetchingToken = false;
			// At this point, the user has chosen an account and we have
			// attempted to fetch information
			// but the user might have needed to grant permissions still and
			// we're coming back from that process.
			Logger.getLogger(TAG).info("Handling Recovery Request Result.");
			if (resultCode == Activity.RESULT_OK) {
				Bundle extra = data.getExtras();
				String oneTimeToken = extra.getString("authtoken");
				handleSignInSuccess(mEmail, oneTimeToken);
			} else {
				// if we made it to the permissions screen, the user might have
				// made a mistake on picking
				// the correct user account, so let's clear it first
				handleClearAccount();
				handleError(SIGN_IN_CANCELLED);
			}
		}
	}

	private void handleSignInSuccess(String email, String token) {
		mFetchingToken = false;
		HashMap<String, String> event = new HashMap<String, String>();
		event.put("accountId", email);
		event.put("accessToken", token);
		successCallback.call(getKrollObject(), event);

	}

	// Clear out the account selection
	private void clearAccount() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
		}
		mClearingAccount = false;
	}

	private void handleClearAccount() {
		// Reset the google api client and clear out any account selections
		// in case the user wants to pick a different one.
		mClearingAccount = true;
		if (!mGoogleApiClient.isConnected()) {
			mGoogleApiClient.connect();
		} else {
			clearAccount();
		}
	}


	private void handleError(String code) {
		mIntentInProgress = false;
		mFetchingToken = false;

		HashMap<String, String> event = new HashMap<String, String>();
		event.put("error", code);
		errorCallback.call(getKrollObject(), event);
	}


	private void handleRecoverableException(Intent recoveryIntent) {
		Logger.getLogger(TAG).info("Launchng recoverable intent.");

		// Use the intent in a custom dialog or just startActivityForResult.
		Activity activity = TiApplication.getAppCurrentActivity();
		TiActivitySupport support = (TiActivitySupport) activity;
		recoveryRequestCode = support.getUniqueResultCode();
		support.launchActivityForResult(recoveryIntent, recoveryRequestCode,
				this);

	}

	private void handleGooglePlayException(
			GooglePlayServicesAvailabilityException playEx) {
		// Use the dialog to present to the user.
		Activity activity = TiApplication.getAppCurrentActivity();
		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
				playEx.getConnectionStatusCode(), activity, -99); // -99 temporary request code
		dialog.show();
	}


	// Build the google api client
	private void buildClient() {
		Activity activity = TiApplication.getAppCurrentActivity();
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API);

		if (Arrays.asList(mScopes).contains("profile")) {
			Log.d(TAG, "Adding profile scope.");
			builder.addScope(Plus.SCOPE_PLUS_PROFILE);
		}

		mGoogleApiClient = builder.build();
	}

	// Now that mEmail exists and we know the accountId and have signed in to
	// GooglePlus Oauth,
	// use the GoogleAuthUtil to fetch the auth token
	private void fetchAccessToken() {
		String scopeStr = TextUtils.join(" ", mScopes);

		// This allows scope array to be specified as ['profile', 'email']
		// or something like: ['audience:server:client_id:<client_id>']
		String check = scopeStr.substring(0, 5);
		if (check != "oauth" || check != "audie") {
			// if token doesn't begin with 'oauth2:' or 'audience:'
			scopeStr = "oauth2:" + scopeStr;
		}

		FetchUserTokenTask task = new FetchUserTokenTask(TiApplication.getAppCurrentActivity(), mEmail, scopeStr);
		task.execute();
	}



	// Methods
	@Kroll.method
	public void signin(KrollDict props)
	{
		if (props.containsKey("success")) {
			successCallback = (KrollFunction) props.get("success");
		}
		if (props.containsKey("error")) {
			errorCallback = (KrollFunction) props.get("error");
		}

		if (mGoogleApiClient == null) {
			buildClient();
		}

		if (!mGoogleApiClient.isConnected()) {
			mClearingAccount = false;
			mGoogleApiClient.connect();
		}
	}

	@Kroll.method
	public void signout() {
		// TODO
	}

	@Kroll.method
	public void disconnect() {
		// TODO
	}

	@Kroll.method
	public Boolean isLoggedIn() {
		return false; // TODO
	}

	// Properties
	@Kroll.getProperty @Kroll.method
	public void setClientId(String value)
	{
		// This method is simply a stub for iOS since we don't
		// need a clientId;
		mClientId = value;
	}

	@Kroll.getProperty @Kroll.method
	public String getClientId() {
		// This method is simply a stub for iOS since we don't
		// need a clientId;
		return mClientId;
	}

	@Kroll.setProperty @Kroll.method
	public void setScopes(String[] value)
	{
		mScopes = value;
	}

	@Kroll.getProperty @Kroll.method
	public String[] getScopes() {
		return mScopes;
	}

	public class FetchUserTokenTask extends AsyncTask {
		Activity mActivity;
		String mScope;
		String mEmail;
		private static final String TAG = "TiGoogleAuthUtilModule";

		FetchUserTokenTask(Activity activity, String name, String scope) {
			this.mActivity = activity;
			this.mScope = scope;
			this.mEmail = name;
		}

		/**
		 * Executes the asynchronous job. This runs when you call execute() on
		 * the AsyncTask instance.
		 */
		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				String token = fetchToken();
				if (token != null) {
					Logger.getLogger(TAG).info(
							"Found token for email: " + mEmail + " - " + token);
					handleSignInSuccess(mEmail, token);
				}
			} catch (IOException e) {
				// The fetchToken() method handles Google-specific exceptions,
				// so this indicates something went wrong at a higher level.
				// TIP: Check for network connectivity before starting the
				// AsyncTask.
				Logger.getLogger(TAG).info("IOException: " + e.getMessage());
				handleError(IO_EXCEPTION);
			}
			return null;
		}

		/**
		 * Gets an authentication token from Google and handles any
		 * GoogleAuthException that may occur.
		 */
		protected String fetchToken() throws IOException {
			try {
				Logger.getLogger(TAG).info(
						"Fetching token from google using account: '" + mEmail
								+ "' and scope: '" + mScope + "'.");
				return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
			} catch (GooglePlayServicesAvailabilityException playEx) {
				Logger.getLogger(TAG).info(
						"Google Play Exception: " + playEx.getMessage());
				handleGooglePlayException(playEx);
			} catch (UserRecoverableAuthException userRecoverableException) {
				Logger.getLogger(TAG).info(
						"User Recoverable Exception: "
								+ userRecoverableException.getMessage());
				handleRecoverableException(userRecoverableException
						.getIntent());
			} catch (GoogleAuthException fatalException) {
				Logger.getLogger(TAG).info(
						"Fatal Exception: " + fatalException.getMessage());
				handleError(FATAL_EXCEPTION);
			}
			return null;
		}

	}



}

