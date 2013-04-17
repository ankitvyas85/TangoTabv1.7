package com.tangotab.login.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gcm.GCMRegistrar;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.nearMe.activity.NearMeActivity;
/**
 * This activity will be our start activity 
 * 
 * <br> Class :SplashScreenActivity
 * <br> Layout:splashscreen.xml
 * 
 * @author dillip.lenka
 *
 */
		
public class SplashScreenActivity extends Activity
{
	private LocationManager locationManager;
	private GoogleAnalyticsTracker tracker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		/* getting location of device */
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SPLASH_SCREEN);
		tracker.trackEvent("SplashScreen", "TrackEvent", "splashscreen", 1);		
		
		SharedPreferences date = getSharedPreferences("SearchDate", 0);
		SharedPreferences.Editor ScrollEdit = date.edit();
		ScrollEdit.putString("dateSelected", "");
		ScrollEdit.commit();
		
		//Checks whether the the device supports GCM
		GCMRegistrar.checkDevice(this);

		/**
		 * verifies that the application manifest contains, meets 
		 * all the requirements for gcm.You can remove this after
		 * testing gcm successfully
		 */
		GCMRegistrar.checkManifest(this);
				
		final String regId = GCMRegistrar.getRegistrationId(this);
				
		Log.w("regId",regId);
		//Check whether the device was already registered
	if (regId.equals("")){		
		GCMRegistrar.register(getApplicationContext(),AppConstant.GCM_APP_ID);
		}
		
		new Handler().postDelayed(new Runnable()
		{
			public void run() 
			{
				SharedPreferences spc = getSharedPreferences("UserName", 0);
				String userName =	spc.getString("username", "");
				String password =spc.getString("password", "");
				
				if(!ValidationUtil.isNullOrEmpty(userName) && !ValidationUtil.isNullOrEmpty(password))
				{
					Intent nearIntent = new Intent(SplashScreenActivity.this, NearMeActivity.class);
					nearIntent.putExtra("frmPage", "splashScreen");
					nearIntent.putExtra("isGetStarted", "true");
					startActivity(nearIntent);	
				}
				else{
					Intent loginIntent = new Intent(SplashScreenActivity.this, FacebookLogin.class);
					startActivity(loginIntent);
				}
				
				finish();
			}
		}, 2000);
		
	}
	
	
	@SuppressWarnings("unused")
	private final LocationListener locationListener = new LocationListener()
	{ 
		public void onLocationChanged(Location location)
		{ 
			updateWithNewLocation(location);
			locationManager.removeUpdates(locationListener);
		}

		public void onProviderDisabled(String provider)
		{
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) 
		{
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			
		}
	};
	
	/**
	 * This method will be update with the new location.
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location)
	{
		if(location!=null)
		{
			/**
			 * Put the current location latitude and longitude into shared preferences.
			 */
			 SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
			 SharedPreferences.Editor edit = currentLocation.edit();
			 edit.putString("locLat", String.valueOf(location.getLatitude()));
			 edit.putString("locLong", String.valueOf(location.getLongitude()));
			 edit.commit();
		}
	}
}
