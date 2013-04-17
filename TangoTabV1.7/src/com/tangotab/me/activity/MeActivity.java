package com.tangotab.me.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import com.nostra13.socialsharing.common.AuthListener;
import com.nostra13.socialsharing.common.LogoutListener;
import com.nostra13.socialsharing.common.PostListener;
import com.nostra13.socialsharing.facebook.FacebookEvents;
import com.nostra13.socialsharing.facebook.FacebookFacade;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.GraphUser;
import com.facebook.HttpMethod;
import com.facebook.LoginButton;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.constant.Constants;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.myOfferDetails.activity.MyoffersDetailActivity;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
import com.tangotab.twitter.activity.TwitterActivity;
import com.tangotab.twitter.util.TwitterUtils;
/**
 * 
 * @author Dillip.Lenka
 *
 */
public class MeActivity extends FacebookActivity implements OnClickListener,Handler.Callback
{
	private Button mBtnTwitterShare,facebookShare;
	private SharedPreferences prefs;
	private LinearLayout mePhil;
	private LinearLayout tangoTabPhil;
	private LinearLayout friendPhil;
	private LinearLayout NetwrkPhil;
	private static final int TWITTER_ID=40;
	private static final int TWEET_SENT=35;
	private static final int TWITTER_FAILED=77;
	private static final int TWITTER_POST_FAILED=78;
	private Map<String, String> response1 = new HashMap<String, String>();
	private StringBuilder description = new StringBuilder();
	Handler myhand,twitterHandler;
	final String TAG = getClass().getName();
	private OAuthConsumer consumer; 
	private OAuthProvider provider;
	boolean pageFinish=false;
	private Dialog dialog;
	private ProgressDialog progressDialog;
	private TangoTabBaseApplication application;
	private GoogleAnalyticsTracker tracker;
	private boolean twitter=false;
	FacebookFacade facebookFacade;
	public Handler postHandler;
	
	private SharedPreferences twitterPreferences;
	private SharedPreferences facebookPreferences;
	
	private SharedPreferences.Editor twitterEditor;
	private SharedPreferences.Editor facebookEditor;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.ME_SCREEN);
		tracker.trackEvent("MeScreen", "TrackEvent", "mescreen", 1);
		
		application = (TangoTabBaseApplication) getApplication();
		twitterHandler=new Handler(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mBtnTwitterShare=(Button)findViewById(R.id.btnTweeterShare);
		mBtnTwitterShare.setBackgroundResource(R.drawable.tweeterselector);
		mBtnTwitterShare.setOnClickListener(this);
		//Button topMeBtn = (Button) findViewById(R.id.topMeMenuButton);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		Button topMenuBtn = (Button) findViewById(R.id.topMenuButton);
		Button topSearchBtn = (Button) findViewById(R.id.topSearchMenuButton);
		Button btnMyOffers = (Button) findViewById(R.id.btnMyOffers);
		Button btnMySettings = (Button) findViewById(R.id.btnMySettings);
		
		mePhil = (LinearLayout) findViewById(R.id.linearLayoutMe);
		tangoTabPhil = (LinearLayout) findViewById(R.id.LinearLayoutPhilNumb);
		friendPhil = (LinearLayout) findViewById(R.id.LinearLayoutPhilNumbFriendText);
		NetwrkPhil = (LinearLayout) findViewById(R.id.LinearLayoutMyPhilNetwrk);
		Button map =(Button)findViewById(R.id.topMapMenuButton);
		facebookFacade = new FacebookFacade(this, Constants.FACEBOOK_APP_ID);
		
		twitterPreferences = getSharedPreferences(
				AppConstant.TWITTER_PREFERENCES, Context.MODE_PRIVATE);
		facebookPreferences = getSharedPreferences(
				AppConstant.FACEBOOK_PREFERENCES, Context.MODE_PRIVATE);
		
		twitterEditor = twitterPreferences.edit();
		facebookEditor = facebookPreferences.edit();
		
		if (TangoTabBaseApplication.getInstance().getShareflow() == null)
			
		{
			facebookEditor.putString("showpopup", "true");
			facebookEditor.putString("iconpopup", "true");
			facebookEditor.putString("iconfacebookon","true");
			twitterEditor.putString("showpopup", "true");
			twitterEditor.putString("iconpopup", "true");
			twitterEditor.putString("icontwitteron", "true");
			facebookEditor.commit();
			twitterEditor.commit();	
		}
		/*
		 *Me button onclick listener 
		 */
		/*topMeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent=new Intent(MeActivity.this, NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});*/
		/*
		 * near me button onclick listener
		 */
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent=new Intent(MeActivity.this, NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});
		
		/*
		 *me button onclick listener.
		 */
		topMenuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent meIntent=new Intent(MeActivity.this, MainMenuActivity.class);
				meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(meIntent);
			}
		});
		
		/*
		 *Top Search button onclick listener.
		 */
		topSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(MeActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});
		
		/*
		 * My offers button onclick listener
		 */
		btnMyOffers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!ValidationUtil.isNullOrEmpty(application.getOffersList()))
					application.getOffersList().clear();
				Intent myOffersIntent=new Intent(MeActivity.this, MyOffersActivity.class);
				myOffersIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myOffersIntent);
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
		 * My Settings button onclick listener
		 */
		btnMySettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mySettingsIntent=new Intent(MeActivity.this, SettingsActivity.class);
				mySettingsIntent.putExtra("frmPage", "MeActivity");
				mySettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mySettingsIntent);
			}
		});
		
		updateMyPhilanthropy();
		
		/*
		 *  Code for facebook sharing
		 */
		

		final Session session = Session.getActiveSession();
        if (!ValidationUtil.isNull(session) && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        
		facebookShare =(Button)findViewById(R.id.btnFacebookShare);
	
		facebookShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (facebookPreferences.getString("iconpopup", "true")
						.equalsIgnoreCase("true")) {
					showDialog(AppConstant.ICON_FACEBOOK_SHARE);
				} else if (facebookPreferences.getString("iconfacebookon",
						"true").equalsIgnoreCase("true")) {
					posttoFacebook();
				}

			}
		});
		postHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == AppConstant.SUCCESFULL_POST) {
					if (!isFinishing())
						showDialog(AppConstant.FACEBOOK_POST_SUCCESSFUL_DIALOG);
					/*
					 * if (isTwitterShare) { // Check if the user is already
					 * logged in twitter if
					 * (TwitterUtils.isAuthenticated(prefs)) { sendTweet();
					 * 
					 * } // Start twitter activity if user is not logged in else
					 * { if (progressDialog == null) { progressDialog = new
					 * ProgressDialog( ClaimOfferActivity.this);
					 * progressDialog.setMessage("Please wait..");
					 * progressDialog.setCancelable(false); }
					 * progressDialog.show(); startTwitterLogin(); } } else {
					 * new InsertDealAsyncTask().execute(dealsDetailVo); }
					 */
				}
			}
		};

	}

	@Override
	public void onClick(View v) {
		if(v==mBtnTwitterShare){

			if (twitterPreferences.getString("iconpopup", "true")
					.equalsIgnoreCase("true")) {
				showDialog(AppConstant.ICON_TWITTER_SHARE);
			} else if (twitterPreferences.getString("icontwitteron", "true")
					.equalsIgnoreCase("true")) {		
			posttoTwitter();
		}
		
		}
		
	}
	private void posttoTwitter() {
		
		SharedPreferences userPreferences = getSharedPreferences("UserDetails", 0);
		boolean isTwitterShareOn=userPreferences.getBoolean("isTwitterOn", false);
		//Check whether the twitter share option is on in settings
		
			//Check if the user is already logged in twitter
		if (TwitterUtils.isAuthenticated(prefs)) 
		{
    		sendTweet();
    	
    	}
		//Start twitter activity if user is not logged in
		else
    	{
    	/*	Intent twitterActivity=new Intent(MeActivity.this,TwitterActivity.class);
    		startActivityForResult(twitterActivity,1);*/
			if(progressDialog==null){
				progressDialog=new ProgressDialog(MeActivity.this);
				progressDialog.setMessage("Please wait..");
				progressDialog.setCancelable(false);
			}
			progressDialog.show();
			startTwitterLogin();
    	}
	}

	/**
	 * Displays a toast notification with No more offers
	 */
	private void showToastMessage(String message){
		Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
	}
	/**
	 * Start a asynctask and send a tweet
	 */
	private void sendTweet(){
		new SendTweetAsyncTask().execute();
	}
	/**
	 * AsyncTask for sending a tweet
	 * @author Ram.Donda
	 *
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=RESULT_CANCELED){
	     if(requestCode==1){
	    	 if(resultCode==RESULT_OK){
	    		 sendTweet();
	    	 }
	    	 else{
	    		 //howToastMessage("There was an error while connecting with twitter");
	    	   showDialog(TWITTER_FAILED);
	    	 }
	     }
		}
	}
	public class SendTweetAsyncTask extends AsyncTask<Void,Void,Integer>{
	      private final int SUCCESS=1,FAILED=0;
	      ProgressDialog mTweetProgressDialog;
			@Override
			protected Integer doInBackground(Void... params) {
				try {
					//TwitterUtils.sendTweet(prefs,dealsDetailVo.getBusinessName()+" "+dealsDetailVo.getDealName()+" ",dealsDetailVo.getImageUrl());
					TwitterUtils.sendTweet(prefs,description.toString(), null);
					return SUCCESS;
				} catch (Exception e) {
					e.printStackTrace();
					return FAILED;
				}
				
			}
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if(mTweetProgressDialog==null){
					mTweetProgressDialog=new ProgressDialog(MeActivity.this);
					mTweetProgressDialog.setMessage("Sending tweet..");
				}
				mTweetProgressDialog.show();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				mTweetProgressDialog.dismiss();
				if(result==SUCCESS){
					showDialog(TWEET_SENT);
					//showToastMessage("Tweet sent");
				}
				else{
					showDialog(TWITTER_POST_FAILED);
				//	showToastMessage("Sending tweet failed");
				}
			}
		}
	
	
		
	
	private void updateMyPhilanthropy(){
		
		SharedPreferences sharPreferences = getSharedPreferences("UserDetails", 0);

			Typeface font = Typeface.createFromAsset(getAssets(), "fonts/digi.ttf");
			String me = sharPreferences.getString("mePhil", "0");
			
			if(!me.equals("0")){
			String meFormatted=createPhilontrophyFormatString(me);
			for(int meresult=0;meresult<meFormatted.length();meresult++){
			String number=meFormatted.substring(meresult, meresult+1);
			TextView tv=new TextView(MeActivity.this);
			tv.setText(number);
			tv.setTextSize(32);
			tv.setTypeface(font);
			if(!number.equalsIgnoreCase(",")){
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.BLACK);
				tv.setPadding(2,0,2,0);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(1, 5, 0, 0);
				tv.setLayoutParams(lp);
			}else
			{
				tv.setTextColor(Color.BLACK);
				tv.setPadding(0,0,-2,0);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 8, 0, -8);
				tv.setLayoutParams(lp);		
			}
			mePhil.addView(tv);
			}
			}
			else{
				TextView tv=new TextView(MeActivity.this);
			   tv.setText("0");
			   tv.setTextSize(32);
			   tv.setTypeface(font);
			   tv.setTextColor(Color.WHITE);
			   tv.setBackgroundColor(Color.BLACK);
			   tv.setPadding(2,0,2,0);
			   LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			   lp.setMargins(1, 5, 0, 0);
			   tv.setLayoutParams(lp);
			   mePhil.addView(tv);
		}
		//	mePhil.setText(Html.fromHtml("<html> <body><table border='0' align='center'><tr><td bgcolor=\"#000000\"><h3 face='Helvetica' color='white'>"+response.get("me")+"</h3></td></tr></body></html>"));
			//tangoTabPhil.setText(Html.fromHtml("<html> <body><table border='0' align='center'><tr><td bgcolor=\"#000000\"><h3 face='Helvetica' color='white'>"+response.get("tangotab")+"</h3></td></tr></body></html>"));
			
		
			String tangotab = sharPreferences.getString("tangotabPhil", "0");
			if(!tangotab.equals("0")){
			String philonThrophy=createPhilontrophyFormatString(tangotab);
			for(int tangoTabResult=0;tangoTabResult<philonThrophy.length();tangoTabResult++){
				String number=philonThrophy.substring(tangoTabResult, tangoTabResult+1);
				TextView tv=new TextView(MeActivity.this);
				tv.setText(number);
				tv.setTextSize(32);  
				tv.setTypeface(font);  
				if(!number.equalsIgnoreCase(",")){
					tv.setTextColor(Color.WHITE);
					tv.setBackgroundColor(Color.BLACK);
					tv.setPadding(2,0,2,0);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(1, 5, 0, 0);
					tv.setLayoutParams(lp);
				}
				else{
					tv.setTextColor(Color.BLACK);
					tv.setPadding(0,0,-2,0);	
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(0, 8, 0, -8);
					tv.setLayoutParams(lp);
				}
				
				tangoTabPhil.addView(tv);
			}
			}else{
				 	TextView tv=new TextView(MeActivity.this);
				   tv.setText("0");
				   tv.setTextSize(32);
				   tv.setTypeface(font);
				   tv.setTextColor(Color.WHITE);
				   tv.setBackgroundColor(Color.BLACK);
				   tv.setPadding(2,0,2,0);
				   LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				   lp.setMargins(1, 5, 0, 0);
				   tv.setLayoutParams(lp);
				   tangoTabPhil.addView(tv);
			}
			String friends = sharPreferences.getString("friendsPhil", "0");
	         if(!friends.equals("0")){
			//String friends="345657";
	        	 String firendsFormated=createPhilontrophyFormatString(friends);
				Log.e("fd leng",""+friends.length());
				for(int friendsResult=0;friendsResult<firendsFormated.length();friendsResult++){
					String number=firendsFormated.substring(friendsResult, friendsResult+1);
					TextView tv=new TextView(MeActivity.this);
					tv.setText(number);
					tv.setTextSize(32);
					tv.setTypeface(font);
					if(!number.equalsIgnoreCase(",")){
						tv.setTextColor(Color.WHITE);
						tv.setBackgroundColor(Color.BLACK);
						tv.setPadding(2,0,2,0);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(1, 5, 0, 0);
						tv.setLayoutParams(lp);
					}
					else{
						tv.setTextColor(Color.BLACK);
						tv.setPadding(0,0,-2,0);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(0, 8, 0, -8);
						tv.setLayoutParams(lp);
					}
					friendPhil.addView(tv);
				
				}
				//friendPhil.setText(Html.fromHtml("<html> <body><table border='0' align='center'><tr><td bgcolor=\"#000000\"><h3 face='Helvetica' color='white'>"+response.get("friends")+"</h3></td></tr></body></html>"));
			}
	               else{
	            	   TextView tv=new TextView(MeActivity.this);
					   tv.setText("0");
					   tv.setTextSize(32);
					   tv.setTypeface(font);
					   tv.setTextColor(Color.WHITE);
					   tv.setBackgroundColor(Color.BLACK);
					   tv.setPadding(2,0,2,0);
					   LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					   lp.setMargins(1, 5, 0, 0);
					   tv.setLayoutParams(lp);
					   friendPhil.addView(tv);
	               }
	         String potential = sharPreferences.getString("networkPhil", "0");
	         String potentialFormatted=createPhilontrophyFormatString(potential);
		if(!potential.equals("0")){
	               for(int potentialResult=0;potentialResult<potentialFormatted.length();potentialResult++){
					String number=potentialFormatted.substring(potentialResult, potentialResult+1);
					TextView tv=new TextView(MeActivity.this);
					tv.setText(number);
					tv.setTextSize(32);
					tv.setTypeface(font);
					if(!number.equalsIgnoreCase(",")){
						tv.setTextColor(Color.WHITE);
						tv.setBackgroundColor(Color.BLACK);
						tv.setPadding(2,0,2,0);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(1, 5, 0, 0);
						tv.setLayoutParams(lp);
					}
					else{
						tv.setTextColor(Color.BLACK);
						tv.setPadding(0,0,-2,0);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(0, 8, 0, -8);
						tv.setLayoutParams(lp);
					}			
					NetwrkPhil.addView(tv);
				
				}
				
			//	NetwrkPhil.setText(Html.fromHtml("<html> <body><table border='0' align='center'><tr><td bgcolor=\"#000000\"><h3 face='Helvetica' color='white'>"+response.get("potential")+"</h3></td></tr></body></html>"));
			}
		else{
			TextView tv=new TextView(MeActivity.this);
			tv.setText("0");
			tv.setTextSize(32);
			tv.setTypeface(font);
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.BLACK);
			tv.setPadding(2,0,2,0);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(1, 5, 0, 0);
			tv.setLayoutParams(lp);
			NetwrkPhil.addView(tv);
		}
			
			description.append("You Have Fed:");
			description.append(me);
			description.append(", ");
			description.append("Your Friends Have Feed:");
			description.append(friends);
			description.append(", ");
			description.append("TangoTab Has Fed:");
			description.append(tangotab);
			description.append(", ");
			description.append("Your Network Could Feed:");
			description.append(potential);
			
	
	}
	/**
	 * Used to create a my philonthrophy format string
	 * @param data
	 *            String which is to be converted
	 * @return
	 *        Converted String
	 */
	private String createPhilontrophyFormatString(String data){
	/*StringBuilder builder=new StringBuilder(data);
	if(builder.length()>5){
  	  
 	   builder.insert(3,",");
 	   if(builder.length()>7){
 		   builder.insert(7,",");  
 	   }
 	   if(builder.length()>10){
 		 String format=builder.substring(0,10); 
 	     format=format+"..";
 	     return format;
 	   }
    }
	
	return builder.toString();*/
		/*DecimalFormat formatter = new DecimalFormat("#,###");
		String formattedString=formatter.format(Double.valueOf(data));
	     StringBuilder builder=new StringBuilder(formattedString);
	     if(builder.length()>10){
	    	 builder.insert(10,"..");
	    	 String result=builder.substring(0,11);
	    	 return result;
	     }
		 return formattedString;*/
		StringBuilder builder=new StringBuilder(data);
		if(builder.length()>3){
			 builder.insert(3,",");	
		}
		if(builder.length()>9){
			String subString=builder.substring(0,9);
			return subString;
		}
		return builder.toString();
	}
	/*
	 * Facebook sharing implementation
	 */
	private void postOfferToFacebook() {
		
		 Session session = Session.getActiveSession();

	    if (session != null){
		Bundle postParams = new Bundle();
		
        postParams.putString("message", description.toString());
        
        Request.Callback callback= new Request.Callback() {
        	@Override
            public void onCompleted(Response response) {
        		
                
                JSONObject graphResponse = null;
                if(!ValidationUtil.isNull(response) && !ValidationUtil.isNull(response.getGraphObject()) && !ValidationUtil.isNull(response.getGraphObject().getInnerJSONObject())){
                	graphResponse = response.getGraphObject().getInnerJSONObject();
                }
                String postId = null;
                try {
                	if(!ValidationUtil.isNull(graphResponse)){
                		postId = graphResponse.getString("id");
                		if(!ValidationUtil.isNull(postId)){
                			showDialog(11);
                		}
                	}
                } catch (JSONException e) {
                    Log.i("Error",
                        "JSON error "+ e.getMessage());
                }
                if (response.getError() != null) {
                    Toast.makeText(MeActivity.this,
                         response.getError().getMessage(),
                         Toast.LENGTH_SHORT).show();
                    }
            }
        };

        Request request = new Request(session, "me/feed", postParams, 
                              HttpMethod.POST,callback);

        RequestAsyncTask task = new RequestAsyncTask(request);
        task.execute();
        
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case AppConstant.FACEBOOK_POST_SUCCESSFUL_DIALOG:
			AlertDialog.Builder facebookShareDialog = new AlertDialog.Builder(
					MeActivity.this);
			facebookShareDialog.setTitle("TangoTab");
			facebookShareDialog
					.setMessage("Successfully posted on your facebook account wall");
			facebookShareDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Session session = Session.getActiveSession();
							if (!ValidationUtil.isNull(session)
									&& !session.isClosed()) {
								session.closeAndClearTokenInformation();
							}
						}
					});

			return facebookShareDialog.create();
		case AppConstant.ICON_TWITTER_SHARE:
			AlertDialog.Builder twitterdetailsicon = new AlertDialog.Builder(
					MeActivity.this);
			twitterdetailsicon.setTitle("TangoTab");
			twitterdetailsicon
					.setMessage("Thank you for your contribution to fight against hunger by sharing offer with your friends");
			twitterdetailsicon.setPositiveButton("Share",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							posttoTwitter();
						}
					});
			twitterdetailsicon.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return twitterdetailsicon.create();
		case AppConstant.ICON_FACEBOOK_SHARE:
			AlertDialog.Builder sharedetails1 = new AlertDialog.Builder(
					MeActivity.this);
			sharedetails1.setTitle("TangoTab");
			sharedetails1
					.setMessage("Thank you for your contribution to fight against hunger by sharing offer with your friends");
			sharedetails1.setPositiveButton("Share",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							posttoFacebook();
						}
					});
			sharedetails1.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return sharedetails1.create();
		case TWITTER_ID:
			AlertDialog.Builder twitterDialog = new AlertDialog.Builder(MeActivity.this);
			twitterDialog.setTitle("TangoTab");
			twitterDialog.setMessage(AppConstant.TWITTER_OFF_MESSGAGE);
			twitterDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			return twitterDialog.create();
		/*case FACEBOOK_ID:
			AlertDialog.Builder fbDialog = new AlertDialog.Builder(MyoffersDetailActivity.this);
			fbDialog.setTitle("TangoTab");
			fbDialog.setMessage(AppConstant.FACEBOOK_OFF_MESSAGE);
			fbDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return fbDialog.create();*/
		case TWEET_SENT:
			AlertDialog.Builder tweetSent = new AlertDialog.Builder(MeActivity.this);
			tweetSent.setTitle("TangoTab");
			tweetSent.setMessage("Tweet Sent ");
			tweetSent.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return tweetSent.create();
			
		case TWITTER_FAILED:
			AlertDialog.Builder tweetFailed = new AlertDialog.Builder(MeActivity.this);
			tweetFailed.setTitle("TangoTab");
			tweetFailed.setMessage("There was an error while connecting with twitter");
			tweetFailed.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return tweetFailed.create();	
		case 10:
			AlertDialog.Builder ab10 = new AlertDialog.Builder(MeActivity.this);
			ab10.setTitle("TangoTab");
			ab10.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");

			ab10.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});
			return ab10.create();
		case TWITTER_POST_FAILED:
			AlertDialog.Builder postFailed = new AlertDialog.Builder(MeActivity.this);
			postFailed.setTitle("TangoTab");
			postFailed.setMessage("Sorry this message was already tweeted");
			postFailed.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return postFailed.create();		
		}
		return null;
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
	class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void>
	{

		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private SharedPreferences prefs;
		
		public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) 
		{
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.prefs=prefs;
		}


		
		@Override
		protected Void doInBackground(Uri...params) {
			final Uri uri = params[0];
		
			final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			//Log.i("path is..",oauth_verifier);
			try {
				provider.retrieveAccessToken(consumer, oauth_verifier);

				final Editor edit = prefs.edit();
				edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
				edit.commit();
				
				String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
				String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
				
				consumer.setTokenWithSecret(token, secret);
				myhand.post(run);
			
				Log.v("Authenticatiuon:", "OAuth - Access Token Retrieved");
				
			} catch (Exception e) {
				Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
			twitterHandler.sendEmptyMessage(TWITTER_FAILED);	
			}

			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		}

		Runnable run=new Runnable()
		{
			public void run()
			{
			Log.e("twitter","checked");
	        twitter=true;
	        if(progressDialog!=null&&progressDialog.isShowing()){
				progressDialog.dismiss();
			}
	       /* setResult(RESULT_OK);
	        finish();*/
	        sendTweet();
			//twittercheck.setImageResource(R.drawable.checked);
			}
		};
	}	
class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void>
	{

		final String TAG = getClass().getName();
		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private Handler myhand;
		String url;
		
		/**
		 * 
		 * We pass the OAuth consumer and provider.
		 * 
		 * @param 	context
		 * 			Required to be able to start the intent to launch the browser.
		 * @param 	provider
		 * 			The OAuthProvider object
		 * @param 	consumer
		 * 			The OAuthConsumer object
		 */
		public OAuthRequestTokenTask(Context context,OAuthConsumer consumer,OAuthProvider provider,Handler myhand)
		{
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.myhand=myhand;
		}

		/**
		 * 
		 * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
		 * 
		 */
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				Log.i(TAG, "Retrieving request token from Google servers");
				url = provider.retrieveRequestToken(consumer,AppConstant.OAUTH_CALLBACK_URL);
				Log.i(TAG, "Popping a browser with the authorize URL : " + url);
				
				myhand.post(run);
			} catch (Exception e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
				/*setResult(RESULT_CANCELED);
				finish();*/
				//mydialog.dismiss();
				twitterHandler.sendEmptyMessage(TWITTER_FAILED);
				
			}

			return null;
		}
		
	    @Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		}

		Runnable run=new Runnable()
	    {
	    	public void run()
	    	{
	    		
	    		Log.e("url is",url);
	    		dialog = new Dialog(context);
	    		//dialog.setOnDismissListener(MeActivity.this);
	    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    		dialog.setCancelable(false);
			    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View vi = inflater.inflate(R.layout.twitterpopup, null);
			    dialog.setContentView(vi);
			    
			    ImageView close=(ImageView)vi.findViewById(R.id.close);
			    close.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						/*setResult(RESULT_CANCELED);
						finish();*/
						if(progressDialog!=null&&progressDialog.isShowing()){
							progressDialog.dismiss();
						}
						
						//mydialog.dismiss();
					}
				});
			    WebView wb = (WebView)vi.findViewById(R.id.twitterweb);
			    wb.setWebViewClient(new myWebClient());  
		        wb.getSettings().setJavaScriptEnabled(true);  
		        wb.loadUrl(url);  
		       
		
			   
	    	}
	    };
	 class myWebClient extends WebViewClient{  //HERE IS THE MAIN CHANGE. 
  	   String url;
  	@Override  
      public void onPageStarted(WebView view, String url, Bitmap favicon) {  
          // TODO Auto-generated method stub  
  		this.url=url;
          super.onPageStarted(view, url, favicon);  
      }  
        
      @Override  
      public boolean shouldOverrideUrlLoading(WebView view, String url) {  
          // TODO Auto-generated method stub  
      	
      	this.url=url;
      	if(url.startsWith("htt"))
      	{
      		
      		view.loadUrl(url); 
      	}
      	else
      	{
      	 	
      	 	dialog.dismiss();
      	 	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MeActivity.this);
      		final Uri uri = Uri.parse(url);
      		if (uri != null && uri.getScheme().equals(AppConstant.OAUTH_CALLBACK_SCHEME)) {
      			Log.e(TAG, "Callback received : " + uri);
      			Log.e(TAG, "Retrieving Access Token");
      			new RetrieveAccessTokenTask(MeActivity.this,consumer,provider,prefs).execute(uri);
      				
      		}
      	}
      	
          
          return true;  
            
      }

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			
			if(url.startsWith("http://"))
			{
				Log.e("http","yes");
				//mydialog.dismiss();
			try{
  			dialog.show();
			}catch(Exception e){
				
			}
			}
			else if(pageFinish)
  		{
				Log.e("https","finished");
  			//mydialog.dismiss();
  			dialog.show();
  		}
			else
			{
				Log.e("https","true");
				pageFinish=true;
			}
			
		}

	 

  }
	}

	private void startTwitterLogin()
	{
		
		
		try {
					
			this.consumer = new CommonsHttpOAuthConsumer(AppConstant.CONSUMER_KEY, AppConstant.CONSUMER_SECRET);
		    this.provider = new CommonsHttpOAuthProvider(AppConstant.REQUEST_URL,AppConstant.ACCESS_URL,AppConstant.AUTHORIZE_URL);
		} catch (Exception e) {
			Log.e(TAG, "Error creating consumer / provider",e);
			
		}
		
		myhand=new Handler();
		new OAuthRequestTokenTask(this,consumer,provider,myhand).execute();
	}

	@Override
	public boolean handleMessage(Message msg) {
	if(msg.what==TWITTER_FAILED){
		 if(progressDialog!=null&&progressDialog.isShowing()){
				progressDialog.dismiss();
			}
		showDialog(TWITTER_FAILED);
	}
		return false;
	}
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode) {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(MeActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(MeActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true;
		    }

		    return super.onKeyDown(keycode, e);
		}
	 
	 private void publishMessage() {
			facebookFacade.publishMessage(description.toString(),null,null,null);

		}

		PostListener postListener = new PostListener() {

			public void onPostPublishingFailed() {
				postHandler.sendEmptyMessage(AppConstant.UNSUCCESFULL_POST);
			}

			public void onPostPublished() {
				postHandler.sendEmptyMessage(AppConstant.SUCCESFULL_POST);

				// unregisterListeners();
			}
		};

		AuthListener authListener = new AuthListener() {

			public void onAuthSucceed() {
			}

			public void onAuthFail(String error) {
			}
		};
		LogoutListener logoutListener = new LogoutListener() {

			public void onLogoutComplete() {
			}
		};

		/** Should be call at {@link Activity#onStart()} */
		public void registerListeners() {

			FacebookEvents.addAuthListener(authListener);
			FacebookEvents.addPostListener(postListener);
			FacebookEvents.addLogoutListener(logoutListener);
		}

		/** Should be call at {@link Activity#onStop()} */
		public void unregisterListeners() {

			FacebookEvents.removeAuthListener(authListener);
			FacebookEvents.removePostListener(postListener);
			FacebookEvents.removeLogoutListener(logoutListener);
		}

		public void posttoFacebook() {

			registerListeners();
			if (facebookFacade.isAuthorized()) {
				publishMessage();

			} else {

				facebookFacade.authorize(new AuthListener() {

					public void onAuthSucceed() {

						publishMessage();

					}

					public void onAuthFail(String error) {
					}
				});
			}

		}
}
