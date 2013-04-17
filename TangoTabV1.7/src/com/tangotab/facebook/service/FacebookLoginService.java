package com.tangotab.facebook.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.facebook.dao.FacebookLoginDao;

public class FacebookLoginService
{
	
	private FacebookLoginDao facebookLoginDao;
	
	public FacebookLoginService() 
	{
		facebookLoginDao = new FacebookLoginDao();
	}

	public Map<String, String> checkForUser(String userName, String facebookId) throws ConnectTimeoutException, TangoTabException 
	{
		Log.v("Invoking the Method checkForUser() with username and FacebookId :",userName+","+facebookId);
		String message = null;
		Map<String, String> response = new HashMap<String, String>();
		try {
			response = facebookLoginDao.checkForUser(userName,facebookId);
		}catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occured at checkForUser() method",e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (TangoTabException e)
		{
			Log.e("Exception ", "Exception occured at checkForUser() method",e);
			throw new TangoTabException("FacebookLoginService", "checkForUser", e);
		}
		return response;
	}
	
	public void signUpToTangoTab(StringEntity signupDetailsRequest) throws ConnectTimeoutException, TangoTabException
	{
		Log.v("signUpToTangoTab","Invoking the Method signUpToTangoTab()");
		String message = null;
		try {
			facebookLoginDao.signUpToTangoTab(signupDetailsRequest);
			Log.v("message is ", message);
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occured at signUpToTangoTab() method",e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (TangoTabException e)
		{
			Log.e("Exception ", "Exception occured at signUpToTangoTab() method",e);
			throw new TangoTabException("FacebookLoginService", "signUpToTangoTab", e);
		}
	}

	public void updateZipForUser(String userName, String postalZip,String workZip) throws ConnectTimeoutException ,TangoTabException 
	{
		Log.v("updateZipForUser","Invoking the Method updateZipForUser()");
		String message = null;
		try {
			message = facebookLoginDao.updateZipForUser(userName,postalZip,workZip);
			Log.v("message is ", message);
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occured at updateZipForUser() method",e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (TangoTabException e)
		{
			Log.e("Exception ", "Exception occured at updateZipForUser() method",e);
			throw new TangoTabException("FacebookLoginService", "updateZipForUser", e);
		}
	}
	/**
	 * Update the social network information for given user.
	 * @param userName
	 * @param facebook
	 * @param twitter
	 * @throws ConnectTimeoutException
	 * @throws TangoTabException
	 */
	public void updateSocialPreferenceUser(String userName, String facebook,String twitter) throws ConnectTimeoutException ,TangoTabException 
	{
		Log.v("updateSocialPreferenceUser","Invoking the Method updateSocialPreferenceUser()");
		String message = null;
		try {
			message = facebookLoginDao.updateSocialPreferences(userName,facebook,twitter);
			Log.v("message is ", message);
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occured at updateSocialPreferenceUser() method",e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (TangoTabException e)
		{
			Log.e("Exception ", "Exception occured at updateSocialPreferenceUser() method",e);
			throw new TangoTabException("FacebookLoginService", "updateSocialPreferenceUser", e);
		}
	}
}