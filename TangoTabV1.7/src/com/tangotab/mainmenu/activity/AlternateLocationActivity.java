package com.tangotab.mainmenu.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;

public class AlternateLocationActivity extends Activity {
	private String alternateZipCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alternatelocation);
		final EditText alternateZip=(EditText)findViewById(R.id.alternateZip);
		Button searchOffer=(Button)findViewById(R.id.searchOffer);
		Button topMenu = (Button) findViewById(R.id.topMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		Button search = (Button) findViewById(R.id.topSearchMenuButton);
		Button map =(Button)findViewById(R.id.map);
		
		/**
		 * Menu button onclick listener
		 */
		topMenu.setOnClickListener(new OnClickListener() 
		{
		
		@Override
		public void onClick(View v)
		{
			/*
			 * Starting main menu activity
			 */
			Intent mainMenuIntent = new Intent(AlternateLocationActivity.this,MainMenuActivity.class);
			mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainMenuIntent);
		}
	});
		/*
		 *me button onclick listener.
		 */
		meButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent meIntent=new Intent(AlternateLocationActivity.this, MeActivity.class);
				meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(meIntent);
			}
		});
		/*
		 * near me button onclick listener
		 */
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent=new Intent(AlternateLocationActivity.this, NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});
		
		/**
		 * Search button onclick listener
		 */
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(AlternateLocationActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
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
					 showDialog(1);				
			}
		});
		
		/*
		 * Search Button Functionality
		 */
		searchOffer.setOnClickListener(new OnClickListener()
		{
			

			@Override
			public void onClick(View v) 
			{
				alternateZipCode=alternateZip.getText().toString();
				
				
				Log.v("alternateZipCode", alternateZipCode);
				
				
				
				if(!ValidationUtil.isNullOrEmpty(alternateZipCode))
				{
					if(ValidationUtil.validateCanadZip(alternateZipCode)||ValidationUtil.validateUSAZip(alternateZipCode))
					{
						SharedPreferences spf=getSharedPreferences("alternateZipCode", 0);
						SharedPreferences.Editor edits=  spf.edit();
						edits.putString("alternateZipCode", alternateZipCode);
						edits.commit();	
						
						SharedPreferences spc2 = getSharedPreferences("UserDetails", 0);
						SharedPreferences.Editor edits2 = spc2.edit();
						edits2.putString("alternateZip", alternateZipCode);
						edits2.commit();

						SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
						SharedPreferences.Editor edits11 = spc11.edit();
						edits11.putString("SelectedLocation", "Alternate");				
						edits11.commit();
						
						Intent nearMeIntent=new Intent(AlternateLocationActivity.this,NearMeActivity.class);
						nearMeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						nearMeIntent.putExtra("SearchBy","alternateZipCode");
						nearMeIntent.putExtra("alternateZipCode", alternateZipCode);						
						startActivity(nearMeIntent);
					}
					else
						showDialog(AppConstant.VALIDATE_ZIPCODE);
				}
				else
				{
					showDialog(2);
				}
			}
		});
	}
	
	/*
	 * Check For Internet Availability
	 */
	private boolean checkInternetConnection() 
	{
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())? true:false;
		
	}
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		
		case 1:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(AlternateLocationActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});
			return ab3.create();
		case 2:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(AlternateLocationActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("Please Enter Code For Search.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab15.create();
		case AppConstant.VALIDATE_ZIPCODE:
			AlertDialog.Builder ab16 = new AlertDialog.Builder(AlternateLocationActivity.this);
			ab16.setTitle("TangoTab");
			ab16.setMessage("Please Enter Valid Zipcode");
			ab16.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab16.create();
		}
		return null;
	}

}