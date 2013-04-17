package com.tangotab.mainmenu.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.CreateSqliteHelper;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
import com.tangotab.signUp.activity.PrivacyPolicyActivity;
import com.tangotab.signUp.activity.TermsOfUseActivity;

/**
 * Main Menu activity
 * @author Dillip.Lenka
 *
 */
public class MainMenuActivity extends Activity
{

	public TangoTabBaseApplication application;
	CreateSqliteHelper csh;
	SQLiteDatabase db;
	private String zipCode;
	private String workZip;
	private String alternateZipCode;
	private String alternateLocationSearch;
	private GoogleAnalyticsTracker tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topmenu);
		
		application = (TangoTabBaseApplication) getApplication();
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.MAIN_MENU_SCREEN);
		tracker.trackEvent("MainMenuScreen", "TrackEvent", "mainmenuscreen", 1);

		Button settings = (Button) findViewById(R.id.mySettingsBtn);
		Button myoffers = (Button) findViewById(R.id.myOffersBtn);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button logOut = (Button) findViewById(R.id.logOutBtn);
		Button myPhilanthropy = (Button) findViewById(R.id.myPhilBtn);
		Button shareTangoTab = (Button) findViewById(R.id.shareTangoTabBtn);
		Button privacyPolicy = (Button) findViewById(R.id.privacyPolicyBtn);
		Button termsAndCondBtn = (Button) findViewById(R.id.termsAndCondBtn);
		Button search = (Button) findViewById(R.id.topSearchMenuButton);
		Button editSearchFilter = (Button) findViewById(R.id.editSearchBtn);
		Button currentLocation =(Button)findViewById(R.id.currentLocationBtn);		
		ImageView userPicture = (ImageView) findViewById(R.id.facebookImage);
		Button homeButton =(Button) findViewById(R.id.homeBtn);
		Button workButton =(Button) findViewById(R.id.workBtn);
		Button alternateButton=(Button)findViewById(R.id.alternateBtn);
		Button myViewOffer = (Button) findViewById(R.id.mapViewOfferBtn);
		userPicture.setVisibility(View.VISIBLE);
		TextView fbUserName = (TextView) findViewById(R.id.facebookUserNameText);
		//Button topMenu = (Button)findViewById(R.id.topMenuButton);
		Button map =(Button)findViewById(R.id.topMapMenuButton);
		TextView VersionInfo=(TextView)findViewById(R.id.versionInfo);
		
		/**
		 * Get the Version Information.
		 */
		PackageInfo pInfo = null;
		try 
		{
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} 
		catch (NameNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		VersionInfo.setText("Version Info: "+pInfo.versionName);

		/**
		 * Get the Zip code from shared prefernces.
		 */
		SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
		zipCode = spc1.getString("postal", "");
		workZip = spc1.getString("workZip", "");

		SharedPreferences aspf=getSharedPreferences("Alternate", 0);
		alternateLocationSearch=aspf.getString("alternateZipfromSearch", "");
		
		SharedPreferences spf=getSharedPreferences("alternateZipCode", 0);
		alternateZipCode=spf.getString("alternateZipCode", "");
		Log.v("alternateZipCode>>>",alternateZipCode);
		
		if (ValidationUtil.isNullOrEmpty(alternateZipCode)
				&& ValidationUtil.isNullOrEmpty(alternateLocationSearch)) {
			alternateButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent alternateIntent = new Intent(MainMenuActivity.this,
							AlternateLocationActivity.class);
					startActivity(alternateIntent);

				}
			});

		} else {
			if (ValidationUtil.isNullOrEmpty(alternateLocationSearch)) {
				alternateButton.setBackgroundResource(R.drawable.alternateselector);
				alternateButton.setText("      ( "+alternateZipCode+" )");
				if (!ValidationUtil.isNullOrEmpty(alternateZipCode)) {
					alternateButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent nearMeIntent = new Intent(
									MainMenuActivity.this, NearMeActivity.class);
							nearMeIntent
									.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							nearMeIntent.putExtra("SearchBy",
									"alternateZipCode");
							nearMeIntent.putExtra("alternateZipCode",
									alternateZipCode);
							SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
							SharedPreferences.Editor edits = spc11.edit();
							edits.putString("SelectedLocation", "Alternate");				
							edits.commit();
							startActivity(nearMeIntent);
						}
					});
				}

			} else {
				alternateZipCode = alternateLocationSearch;
				alternateButton.setBackgroundResource(R.drawable.alternateselector);
				alternateButton.setText("      ( "+alternateZipCode+" )");
				if (!ValidationUtil.isNullOrEmpty(alternateZipCode)) {
					alternateButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent nearMeIntent = new Intent(
									MainMenuActivity.this, NearMeActivity.class);
							nearMeIntent
									.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							nearMeIntent.putExtra("SearchBy",
									"alternateZipCode");
							nearMeIntent.putExtra("alternateZipCode",
									alternateZipCode);
							SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
							SharedPreferences.Editor edits = spc11.edit();
							edits.putString("SelectedLocation", "Alternate");				
							edits.commit();
							startActivity(nearMeIntent);
						}
					});
				}

			}
		}
		if(!ValidationUtil.isNullOrEmpty(zipCode))
		{
			homeButton.setBackgroundResource(R.drawable.homeselector);
			homeButton.setText(" ( "+zipCode+" )");
		}
		else
		{
			homeButton.setBackgroundResource(R.drawable.homeaddselector);
		}
		if(!ValidationUtil.isNullOrEmpty(workZip))
		{
			workButton.setBackgroundResource(R.drawable.workbtnselector);
			workButton.setText(" ( "+workZip+" )");
		}
		else
		{
			workButton.setBackgroundResource(R.drawable.workclicktoaddselector);
		}
		/*
		 * settings button onclick listener
		 */
		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settingsIntent = new Intent(MainMenuActivity.this,SettingsActivity.class);
				settingsIntent.putExtra("frmPage", "MainMenuActivity");
				settingsIntent.putExtra("frmButton", "Settings");
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(settingsIntent);
			}
		});
		/*
		 * myoffers button onclick listener.
		 */
		myoffers.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) 
				{
					if(!ValidationUtil.isNullOrEmpty(application.getOffersList()))
						application.getOffersList().clear();
					Intent myoffersIntent = new Intent(MainMenuActivity.this,MyOffersActivity.class);
					myoffersIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myoffersIntent);
				}
			});

		
		/**
		 * Map button on click listener
		 */
		map.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				if(checkInternetConnection())
				{
					SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
					String lat = currentLocation.getString("locLat", "");
					String lang = currentLocation.getString("locLong", "");
					if(!ValidationUtil.isNullOrEmpty(lat) && !ValidationUtil.isNullOrEmpty(lang))
					{
						try
						{
							AppConstant.dev_lat = Double.valueOf(lat);
							AppConstant.dev_lang = Double.valueOf(lang);
						
						}catch(NumberFormatException e)
						{
						
							Log.e("Exception", "Exception occuerd when converting String int doubble", e);
						}
					}
					
					List<Address> addressList =null;
					
					Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
					try 
					{
						addressList = geocoder.getFromLocation(AppConstant.dev_lat, AppConstant.dev_lang, 1);
					} catch (IOException e)
					{
						Log.e("Exception:", "Exception occuerd at the time getting address list from Geo Coder.");
						e.printStackTrace();
					}
					
					if(!ValidationUtil.isNullOrEmpty(addressList))
					{
						Intent myOfferMapIntent = new Intent(getApplicationContext(), MappingActivity.class);
						myOfferMapIntent.putExtra("businessname", addressList.get(0).getAddressLine(0));
						myOfferMapIntent.putExtra("dealname",addressList.get(0).getAddressLine(0));
						myOfferMapIntent.putExtra("IsFromPlaceOrSearch", "mySettings");
						myOfferMapIntent.putExtra("fromPage", "mySettings");
						startActivity(myOfferMapIntent);
					}
				
				}else
					 showDialog(10);				
			}
		});
		/*
		 * near me button onclick listener
		 */
		nearMe.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Intent nearmeIntent = new Intent(MainMenuActivity.this,NearMeActivity.class);
					nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(nearmeIntent);
				}
			});
		
		/**
		 * My Philanthropy onclick listener
		 */
		myPhilanthropy.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					openMePage();
				}
			});
		
		/**
		 * Share TangoTab onclick Listener
		 */
		shareTangoTab.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					openMePage();
				}
			});

		/*
		 * me button onclick listener.
		 */
		meButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{			
					openMePage();
				}
			});
		
		/**
		 * Search button onclick listener
		 */
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(MainMenuActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});
		/**
		 * On click handler for edit search filter.
		 */
		editSearchFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(MainMenuActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				searchIntent.putExtra("editSearch", "EditSearch");				
				startActivity(searchIntent);
			}
		});
		/**
		 * On click handler for current location.
		 */
		currentLocation.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) 
				{
					Intent nearMeIntent=new Intent(MainMenuActivity.this,NearMeActivity.class);
					nearMeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
					SharedPreferences.Editor edits = spc11.edit();
					edits.putString("SelectedLocation", "Current Location");				
					edits.commit();
					startActivity(nearMeIntent);
				}
			});
		/**
		 * On click handler for Home button.
		 */
		homeButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) 
				{
					if(!ValidationUtil.isNullOrEmpty(zipCode))
					{
						Intent nearMeIntent=new Intent(MainMenuActivity.this,NearMeActivity.class);
						nearMeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						nearMeIntent.putExtra("SearchBy","HomeZip");
						SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
						SharedPreferences.Editor edits = spc11.edit();
						edits.putString("SelectedLocation", "Home");				
						edits.commit();
						startActivity(nearMeIntent);
					}
					else
					{
						Intent settingsIntent = new Intent(MainMenuActivity.this,SettingsActivity.class);
						settingsIntent.putExtra("frmPage", "MainMenuActivity");
						settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						settingsIntent.putExtra("FocusField", "Home");
						startActivity(settingsIntent);
					}
				}
			});
		
		/**
		 * On click handler for work zip button.
		 */
		workButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) 
				{
					if(!ValidationUtil.isNullOrEmpty(workZip))
					{
						Intent nearMeIntent=new Intent(MainMenuActivity.this,NearMeActivity.class);
						nearMeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						nearMeIntent.putExtra("SearchBy","WorkZip");
						SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
						SharedPreferences.Editor edits = spc11.edit();
						edits.putString("SelectedLocation", "Work");				
						edits.commit();
						startActivity(nearMeIntent);
					}
					else
					{
						Intent settingsIntent = new Intent(MainMenuActivity.this,SettingsActivity.class);
						settingsIntent.putExtra("frmPage", "MainMenuActivity");
						settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						settingsIntent.putExtra("FocusField", "Work");
						startActivity(settingsIntent);
					}
				}
			});
		
		
		/**
		 * On click handler for my view offer on top menu screen
		 */
		myViewOffer.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(final View v) {
				
				Vibrator myVib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				 myVib.vibrate(50);
				
				new Handler().postDelayed(new Runnable() {
					public void run() {
						v.setClickable(true);
					}
				}, 600);

				if (checkInternetConnection())
				{
					/**
					 * get list of deals from application.
					 */
					List<DealsDetailVo> mapDealsList = application. getDealsList();
					if (ValidationUtil.isNullOrEmpty(mapDealsList))
					{
						SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
						String lat = currentLocation.getString("locLat", "");
						String lang = currentLocation.getString("locLong", "");
						if(!ValidationUtil.isNullOrEmpty(lat) && !ValidationUtil.isNullOrEmpty(lang))
						{
							try
							{
								AppConstant.dev_lat = Double.valueOf(lat);
								AppConstant.dev_lang = Double.valueOf(lang);
							
							}catch(NumberFormatException e)
							{
							
								Log.e("Exception", "Exception occuerd when converting String int doubble", e);
							}
						}
						
						List<Address> addressList =null;
						
						Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
						try 
						{
							addressList = geocoder.getFromLocation(AppConstant.dev_lat, AppConstant.dev_lang, 1);
						} catch (IOException e)
						{
							Log.e("Exception:", "Exception occuerd at the time getting address list from Geo Coder.");
							e.printStackTrace();
						}
						
						if(!ValidationUtil.isNullOrEmpty(addressList))
						{
							Intent myOfferMapIntent = new Intent(getApplicationContext(), MappingActivity.class);
							myOfferMapIntent.putExtra("businessname", addressList.get(0).getAddressLine(0));
							myOfferMapIntent.putExtra("dealname",addressList.get(0).getAddressLine(0));
							myOfferMapIntent.putExtra("IsFromPlaceOrSearch", "mySettings");
							myOfferMapIntent.putExtra("fromPage", "mySettings");
							startActivity(myOfferMapIntent);
						}
					
					}
					else {

						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								runOnUiThread(new Runnable()
								{					
									@Override
									public void run() 
									{
										final Context appContext = getApplicationContext();
										/**
										 * Start map activity
										 */
									    Intent mapIntent = new Intent(appContext, MappingActivity.class);
									    startActivity(mapIntent);	
									}
								});
							}
						}).start();
						
					}
				} else
					showDialog(10);
			}

		});	
		
		/**
		 * code for displaying facebook profile image, his/her first name,last name of the user.
		 */
		SharedPreferences spc = getSharedPreferences("UserDetails", 0);
		String facebookId = spc.getString("fbID", "");
		/*
		 * Capitalization of First Character Of First Name and Last Name
		 * @Satyabrata.sarangi
		 */
		
		String firstName = spc.getString("firstName", "");
		char[] stringArrayFirstName = firstName.toCharArray();
		stringArrayFirstName[0] = Character.toUpperCase(stringArrayFirstName[0]);
		firstName = new String(stringArrayFirstName);
		
		String lastName = spc.getString("lastName", "");
		char[] stringArrayLastName = lastName.toCharArray();
		stringArrayLastName[0] = Character.toUpperCase(stringArrayLastName[0]);
		lastName = new String(stringArrayLastName);
		
		if (!ValidationUtil.isNullOrEmpty(firstName)
				&& !ValidationUtil.isNullOrEmpty(lastName)) {
			fbUserName.setText(firstName + " " + lastName);
		}
		if (!ValidationUtil.isNullOrEmpty(facebookId)) {
			URL img_value = null;
			try {
				img_value = new URL("http://graph.facebook.com/" + facebookId
						+ "/picture?type=large");
				Bitmap mIcon1 = BitmapFactory.decodeStream(img_value
						.openConnection().getInputStream());
				userPicture.setImageBitmap(mIcon1);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * logout onclick listener
		 */
		logOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doLogOut();
			}
		});
		
		/**
		 * privacy policy onclick listener
		 */
		privacyPolicy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent privacy=new Intent(getApplicationContext(),PrivacyPolicyActivity.class);
				startActivity(privacy);
			}
		});
		
		/**
		 * Terms and conditions onclick listener
		 */
		termsAndCondBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent termscondition=new Intent(getApplicationContext(),TermsOfUseActivity.class);
				startActivity(termscondition);
				
			}
		});
	}
	
	private void doLogOut()
	{	
		/**
		 * Clear the dfeals from the application.
		 */
		clearFromApplication();
		 SharedPreferences preferences = getSharedPreferences("OFFERSCROLL", 0);
		 SharedPreferences.Editor offersEdit = preferences.edit();
		 offersEdit.clear();
		 offersEdit.commit();
		/**
		 * Clear the login information from shared preferences.
		 */
		SharedPreferences spc = getSharedPreferences("UserName", 0);
		SharedPreferences.Editor edit = spc.edit();
		edit.clear();
		edit.commit();
		/**
		 * Clear the date field from naer me
		 */
		SharedPreferences datePreferences = getSharedPreferences("NearMeDate", 0);
		SharedPreferences.Editor dateEdit = datePreferences.edit();
		dateEdit.clear();
		dateEdit.commit();
		
		SharedPreferences customURL = getSharedPreferences("CustomURL", 0);
		SharedPreferences.Editor edits = customURL.edit();
		edits.clear();
		edits.commit();
		
		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		SharedPreferences.Editor edit1 = spc1.edit();
		edit1.clear();
		edit1.commit();
		
		SharedPreferences date = getSharedPreferences("SearchDate", 0);
		SharedPreferences.Editor ScrollEdit = date.edit();
		ScrollEdit.clear();
		ScrollEdit.commit();
		
		SharedPreferences range = getSharedPreferences("RangeBar", 0);
		SharedPreferences.Editor scrollEdit = range.edit();
		scrollEdit.clear();
		scrollEdit.commit();
		
		SharedPreferences spf=getSharedPreferences("alternateZipCode", 0);
		SharedPreferences.Editor editors=spf.edit();
		editors.clear();
		editors.commit();
		
		SharedPreferences aspf=getSharedPreferences("Alternate", 0);
		SharedPreferences.Editor editor1=aspf.edit();
		editor1.clear();
		editor1.commit();
		//alternateLocationSearch=aspf.getString("alternateZipfromSearch", "");
		
		SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
		SharedPreferences.Editor editor11=spc11.edit();
		editor11.clear();
		editor11.commit();
		
		csh = new CreateSqliteHelper(getApplicationContext());
		db = csh.getReadableDatabase();
		db.delete("LOGIN", "ID=" + 1, null);
		db.close();	
		
			Session session = Session.getActiveSession();
	        if (!ValidationUtil.isNull(session) && !session.isClosed()) {
	            session.closeAndClearTokenInformation();
	        }
	        /**
	         * Start new activity
	         */
	        Intent facebookLoginIntent = new Intent(getApplicationContext(), FacebookLogin.class);
	        facebookLoginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(facebookLoginIntent);
			setResult(RESULT_OK);
			finish();


		 
	}
	
	
	/**
	 * clear the information from the application object.
	 * 
	 */
	private void clearFromApplication()
	{
		if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
			application.getSearchList().clear();
		if(!ValidationUtil.isNullOrEmpty(application.getOffersList()))
			application.getOffersList().clear();
		if(!ValidationUtil.isNullOrEmpty(application.getDealsList()))
			application.getDealsList().clear();
		
		/**
		 * Put the scroll to top of the list.
		 */
		
		 SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
		 SharedPreferences.Editor ScrollEdit = preferences.edit();
		 ScrollEdit.clear();
		 ScrollEdit.commit();
		
		 /**
		 * Put the scroll to top of the list.
		 */
		 SharedPreferences searchPreferences = getSharedPreferences("SCROLL", 0);
		 SharedPreferences.Editor searchEdit = searchPreferences.edit();		
		 searchEdit.clear();
		 searchEdit.commit();
		}
	
	private void openMePage()
	{
		Intent meIntent = new Intent(MainMenuActivity.this,MeActivity.class);
		meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(meIntent);
	}
	/**
	 * This method will check the Internet connection for the application.
	 * 
	 * @return
	 */
	private boolean checkInternetConnection() 
	{
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())? true:false;
		
	}
	/**
	 * Display the Dialog message
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
		{
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(MainMenuActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage("No offers are found to display on map");
			ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab.create();
			
		case 10:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(MainMenuActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab15.create();
		
		}
		return null;
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e)
	{
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	        	Intent mainMenuIntent = new Intent(MainMenuActivity.this,MainMenuActivity.class);
				mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
	            return true;
	        case KeyEvent.KEYCODE_SEARCH:
	        	Intent searchIntent=new Intent(MainMenuActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
	            return true;
	    }

	    return super.onKeyDown(keycode, e);
	}
}
