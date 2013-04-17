package com.tangotab.myOffers.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.contactSupport.activity.ContactSupportActvity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.myOfferDetails.activity.MyoffersDetailActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.adapter.MyOffersAdapter;
import com.tangotab.myOffers.service.MyOffersService;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;

/**
 * Get all claimed offers in the my offers tab from web service.
 * 
 * <br>
 * Class :MyOffersActivity <br>
 * layout :myoffers.xml
 * 
 * @author Dillip.Lenka
 * 
 */
public class MyOffersActivity extends ListActivity {
	/*
	 * Meta Definitions
	 */
	private int pageCount = 1;
	private LinearLayout llShowMore = null;
	private ListView offerListView = null;
	private Button map = null;
	private EditText editSearch;
	private TextView emptyList;
	//private List<OffersDetailsVo> finalOfferList = new ArrayList<OffersDetailsVo>();
	private List<OffersDetailsVo> listForFiltering = new ArrayList<OffersDetailsVo>();
	private List<OffersDetailsVo> myOffersList = new ArrayList<OffersDetailsVo>();
	public TangoTabBaseApplication application;
	private Vibrator myVib;
	private int count = 11;
	private boolean isAutoScrolldown = false;
	private GoogleAnalyticsTracker tracker;
	private static int totalOffers;
	/**
	 * Execution will be start here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myoffers);

		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.MYOFFER_PAGE);
		tracker.trackEvent("MyOffers", "TrackEvent", "myoffers", 1);
		
		application = (TangoTabBaseApplication) getApplication();
		/**
		 * get UI Widgets
		 */
		emptyList = (TextView) findViewById(R.id.emptylist);
		offerListView = (ListView) findViewById(android.R.id.list);
		llShowMore = (LinearLayout) getLayoutInflater().inflate(
				R.layout.showmorecell, null);
		map = (Button) findViewById(R.id.map);
		editSearch = (EditText) findViewById(R.id.edittextsearch);
		// final Button restSearch =(Button)findViewById(R.id.restserch);
		Button topMenu = (Button) findViewById(R.id.topMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		Button search = (Button) findViewById(R.id.topSearchMenuButton);
		TextView statusFedLabel = (TextView) findViewById(R.id.statusbarFedLabel);
		ImageView symbol=(ImageView)findViewById(R.id.cup);
		symbol.setVisibility(View.GONE);
		SharedPreferences sharedUserDetails = getSharedPreferences("UserDetails", 0);
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(sharedUserDetails.getString("firstName", "").substring(0, 1).toUpperCase());
		strBuilder.append(sharedUserDetails.getString("firstName", "").substring(1)).append(", ");
		strBuilder.append("you have fed ");
		strBuilder.append(sharedUserDetails.getString("mePhil", "0"));
		strBuilder.append(" people.");
		statusFedLabel.setText(strBuilder.toString());

		topMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Starting main menu activity
				 */
				Intent mainMenuIntent = new Intent(MyOffersActivity.this,
						MainMenuActivity.class);
				mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
			}
		});

		/*
		 * me button onclick listener.
		 */
		meButton.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				Intent meIntent = new Intent(MyOffersActivity.this,
						MeActivity.class);
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
				Intent nearmeIntent = new Intent(MyOffersActivity.this,
						NearMeActivity.class);
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
				Intent searchIntent = new Intent(MyOffersActivity.this,
						SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});

		/**
		 * get Offers list from session and display
		 */
		/*List<OffersDetailsVo> offersList = application.getOffersList();
		SharedPreferences spc = getSharedPreferences("AppNotification", 0);
	
		if (!ValidationUtil.isNullOrEmpty(offersList) &&spc.getBoolean("isCheckInOccured", false)) 
		{
			pageCount = application.getMyOfferPageCount();
			emptyList.setVisibility(View.GONE);
			offerListView.setVisibility(View.VISIBLE);
			Log.v("offersList is", String.valueOf(offersList.size()));
			MyOffersAdapter myOffersAdapter = new MyOffersAdapter(
					MyOffersActivity.this, offersList, llShowMore);
			offerListView = getListView();
			offerListView.setCacheColorHint(Color.TRANSPARENT);
			offerListView.removeFooterView(llShowMore);
			offerListView.addFooterView(llShowMore);
			setListAdapter(myOffersAdapter);

		} else {*/
			/**
			 * Get first 10 offers.
			 */
			getOfferList(pageCount);
			SharedPreferences spc = getSharedPreferences("AppNotification", 0);
			SharedPreferences.Editor edit = spc.edit();
			edit.putBoolean("isCheckInOccured",false);
			edit.commit();
		/*}*/

		/**
		 * On click listener for show more button.
		 */
		/*
		 * llShowMore.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * myVib.vibrate(50); pageCount++; getOfferList(pageCount); } });
		 */

		/**
		 * Auto scroll down offers
		 */
		offerListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;

				// is the bottom item visible? Load more !
				if ((lastInScreen == totalItemCount) && totalItemCount != 1
						&&  totalItemCount != totalItemCount + 10
						&& totalItemCount == count && totalItemCount != totalOffers+1) {
					pageCount++;
					isAutoScrolldown = true;
					getOfferList(pageCount);
					count += 10;
				}
			}
		});

		/**
		 * Map button on click listener added.
		 */
		map.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				myVib.vibrate(50);
				new Handler().postDelayed(new Runnable() {
					public void run() {
						v.setClickable(true);
					}
				}, 600);

				if (checkInternetConnection()) {
					myOffersList = null;
					if (!ValidationUtil.isNullOrEmpty(listForFiltering)) {
						myOffersList = listForFiltering;
					} else {
						myOffersList = application.getOffersList();
					}

					if (ValidationUtil.isNullOrEmpty(myOffersList)) 
					{
						showDialog(0);
					} else {

						new Thread(new Runnable() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Bundle bundle = new Bundle();
										/**
										 * Start map activity in order to
										 * display offers into the map
										 */
										Intent intent = new Intent(getApplicationContext(),	MappingActivity.class);
										bundle.putParcelableArrayList(
												"offerList",
												(ArrayList<? extends Parcelable>) myOffersList);
										intent.putExtras(bundle);
										startActivity(intent);
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
		 * Search text change listener.
		 */
		editSearch.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				int textlength = editSearch.getText().length();
				if (!ValidationUtil.isNullOrEmpty(listForFiltering))
					listForFiltering.clear();
				for (int i = 0; i < application.getOffersList().size(); i++) {
					if (textlength <= application.getOffersList().get(i)
							.getBusinessName().length()) {
						String editSearchText = editSearch.getEditableText()
								.toString();
						String businessName = application.getOffersList()
								.get(i).getBusinessName();
						if (businessName.toLowerCase().contains(
								editSearchText.toLowerCase())) {
							listForFiltering.add(application.getOffersList()
									.get(i));
						}
					}
				}
				if (listForFiltering.isEmpty()) {
					emptyList.setVisibility(0);
					if (textlength <= 0) {

						emptyList.setText("You have not selected any offer. "
								+ "Please search for a offer and reserve it.");
					} else
						emptyList
								.setText("Sorry, no offers match the search criteria "
										+ "or you have no offers reserved yet.");
				} else
					emptyList.setVisibility(8);

				if (!ValidationUtil.isNullOrEmpty(listForFiltering)) {
					MyOffersAdapter myOffersAdapter = new MyOffersAdapter(
							MyOffersActivity.this, listForFiltering, llShowMore);
					offerListView.setAdapter(myOffersAdapter);
				}
			}
		});

		/**
		 * Edit listener for search offers
		 */
		editSearch
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									editSearch.getWindowToken(), 0);
						}
						return false;
					}
				});
	}

	/**
	 * Get list of offers for given page.
	 * 
	 * @param pageCount
	 */
	private void getOfferList(int pageCount) {
		SharedPreferences spc = getSharedPreferences("UserName", 0);
		String userId = spc.getString("username", "");
		String password = spc.getString("password", "");
		LoginVo loginvo = new LoginVo();
		loginvo.setUserId(userId);
		loginvo.setPassword(password);
		application.setMyOfferPageCount(pageCount);
		/**
		 * Aynctask call for get list of offers from web service.
		 */
		new MyOffersListAsyncTask().execute(loginvo);
	}

	/**
	 * AsyncTask call to retrieve all the offers from service using different
	 * thread
	 * 
	 * @author Dillip.Lenka
	 * 
	 */
	public class MyOffersListAsyncTask extends AsyncTask<LoginVo, Void, List<OffersDetailsVo>>
	{
		private ProgressDialog mDialog = null;

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MyOffersActivity.this, "Searching ", "Please wait...");
			mDialog.setCancelable(true);
		}

		@Override
		protected List<OffersDetailsVo> doInBackground(LoginVo... loginVo)
		{
			List<OffersDetailsVo> offersList = null;
			List<OffersDetailsVo> todayOffers = null;
			try {
				MyOffersService service = new MyOffersService();
				offersList = service.getOffers(pageCount, loginVo[0]);	
				/**
				 * Remove the old offers whose reserve time stamp before current time.
				 */
				try
				{
				if(!ValidationUtil.isNullOrEmpty(offersList))
				{
					todayOffers = new ArrayList<OffersDetailsVo>();
					for(OffersDetailsVo offersDetailsVo:offersList)
					{
						String reserveTime = offersDetailsVo.getReserveTimeStamp();
						int index = reserveTime.indexOf(" ");
						String reserve = reserveTime.substring(0, index).trim();
						String current = offersDetailsVo.getCurrentDate();
						index = current.indexOf(" ");
						String currentTime = current.substring(0, index).trim();
						if(reserve.compareToIgnoreCase(currentTime)==0)
							todayOffers.add(offersDetailsVo);
					}
				}
				}catch(ConcurrentModificationException e)
				{
					Log.e("Exception:", "Exception occured",e);
				}
			}
			catch (ConnectTimeoutException e)
			{
				Log.e("ConnectTimeoutException occured get list of offers", "", e);
				Intent contactIntent=new Intent(getApplicationContext(), ContactSupportActvity.class);
				startActivity(contactIntent);
			}
			catch (Exception e)
			{
				Log.e("Exception occured get list of offers", "", e);
			}
			return todayOffers;
		}

		@Override
		protected void onPostExecute(List<OffersDetailsVo> offersList) {
			if(mDialog !=null)
				mDialog.dismiss();
			if (!ValidationUtil.isNullOrEmpty(offersList))
			{
				// Set the alaram for local notifications for offers
				sendLocalNotification(offersList);
				/**
				 * get Previous list of Offers
				 */
				List<OffersDetailsVo> myOfferList = application.getOffersList();
				if (!ValidationUtil.isNullOrEmpty(offersList)&& isAutoScrolldown)
				{
					if (!ValidationUtil.isNullOrEmpty(offersList))
					{
						myOfferList.addAll(offersList);
						
					}
					isAutoScrolldown =false;
				}
				else
				{	if (!ValidationUtil.isNullOrEmpty(offersList))
					{
						if(!ValidationUtil.isNullOrEmpty(myOfferList))
							myOfferList.addAll(offersList);
						else
						{
							myOfferList = new ArrayList<OffersDetailsVo>();
							myOfferList.addAll(offersList);
						}
					}
					if(isAutoScrolldown && ValidationUtil.isNullOrEmpty(offersList))
					{
						myOfferList.addAll(myOfferList);
					}
						
				}
				application.setOffersList(myOfferList);
				MyOffersAdapter myOffersAdapter = new MyOffersAdapter(MyOffersActivity.this, myOfferList, llShowMore);
				/**
				 * cursor to be point into next 10 records.
				 */
				if (!ValidationUtil.isNullOrEmpty(myOfferList)&& myOfferList.size() > 10)
				{
					getListView().postDelayed(new Runnable() {
						@Override
						public void run() {
							offerListView.setSelection((pageCount - 1) * 10);
						}
					}, 100L);

				}
				emptyList.setVisibility(View.GONE);
				offerListView.setCacheColorHint(Color.TRANSPARENT);
				offerListView.removeFooterView(llShowMore);
				offerListView.addFooterView(llShowMore);
				setListAdapter(myOffersAdapter);

			} else {
				emptyList.setVisibility(View.VISIBLE);
			}
			
		}
	}


	/**
	 * OnPause functionality added .
	 * 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * Save scroll position into shared preferences.
		 */
		SharedPreferences preferences = getSharedPreferences("OFFERSCROLL", 0);
		SharedPreferences.Editor edit = preferences.edit();
		if (offerListView != null) {
			int scroll = offerListView.getFirstVisiblePosition();
			edit.putInt("scrollValue", scroll);
			View v = offerListView.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();
			edit.putInt("Top", top);
			edit.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getSharedPreferences("OFFERSCROLL", 0);
		int scroll = preferences.getInt("scrollValue", 0);
		int top = preferences.getInt("Top", 0);
		if (offerListView != null)
			offerListView.setSelectionFromTop(scroll, top);
		
		if(!MyoffersDetailActivity.isAutoCheckIn)
		{
			/**
			 * get Offers list from session and display
			 */
			List<OffersDetailsVo> offersList = application.getOffersList();
			if (!ValidationUtil.isNullOrEmpty(offersList)) 
			{
				pageCount = application.getMyOfferPageCount();
				emptyList.setVisibility(View.GONE);
				offerListView.setVisibility(View.VISIBLE);
				Log.v("offersList is", String.valueOf(offersList.size()));
				MyOffersAdapter myOffersAdapter = new MyOffersAdapter(MyOffersActivity.this, offersList, llShowMore);
				offerListView = getListView();
				offerListView.setCacheColorHint(Color.TRANSPARENT);
				offerListView.removeFooterView(llShowMore);
				offerListView.addFooterView(llShowMore);
				setListAdapter(myOffersAdapter);

			}
		}
	};

	/**
	 * Send local notification for List of Offers
	 * 
	 * @param offersDetailsList
	 */
	private void sendLocalNotification(List<OffersDetailsVo> offersDetailsList)
	{	
		if(ValidationUtil.isNullOrEmpty(offersDetailsList))
			return;
		Log.v("Invoking is ", "consumerdealsList = "+offersDetailsList.size());
		for(OffersDetailsVo offersDetailsVo:offersDetailsList)
		{
			 /**
		      * First remove the local notification for the offer
		      */
		     removeAlarmForNotification(offersDetailsVo);
		}
		for(OffersDetailsVo offersDetailsVo:offersDetailsList)
		{	
			int isCheckin =0;
			totalOffers = Integer.parseInt(offersDetailsVo.getNoOfDeals());
			if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getIsConsumerShownUp()))
			{
					isCheckin =Integer.parseInt(offersDetailsVo.getIsConsumerShownUp());
			}
			Log.v("isCheckin = ",String.valueOf(isCheckin));
			/**
			 * If offers neither manually or nor auto check in
			 */
			if(isCheckin ==0)
			{
				String reserveDate = offersDetailsVo.getReserveTimeStamp();
				StringBuilder finalClaimDate = new StringBuilder();
				if(!ValidationUtil.isNullOrEmpty(reserveDate))
				{
					int index = reserveDate.indexOf(" ");	
					String claimDate = offersDetailsVo.getEndTime();
					Log.v("ClaimDate is ", claimDate);
					finalClaimDate.append(reserveDate.substring(0,index).trim()).append(" ").append(claimDate);
					Log.v("finalClaimDate is ", finalClaimDate.toString());
				}
							
				Date finalEndTime = DateFormatUtil.parseIntoDifferentFormat(finalClaimDate.toString(),"yyyy-MM-dd hh:mm aa");
				Log.v("Reeserve time is ", finalEndTime.toString());
				
				String currentDate = offersDetailsVo.getCurrentDate();
				Log.v("server Date ", currentDate);
                Date currentTime = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
                Log.v("Server Time ", currentTime.toString());

				
				//boolean isExpiredOffer =(currentTime.after(finalEndTime))?true:false;		
			    
			     /**
			      * Set the Local notification for the offer if expired.
			      */
				
				/*
				 * Calendar alarmCal = Calendar.getInstance();
				 * alarmCal.setTime(finalEndTime); Long reserveTime = null; long
				 * diffInMilisecond
				 * =alarmCal.getTimeInMillis()-System.currentTimeMillis();
				 * reserveTime = diffInMilisecond+(60*60*1000);
				 * setAlaramForNotification(reserveTime, offersDetailsVo);
				 */

				Calendar alarmCal = Calendar.getInstance();
				alarmCal.setTime(finalEndTime);

				Long reserveTime = null;
				long diffInMilisecond = Math.abs(alarmCal.getTimeInMillis()
						- System.currentTimeMillis());
				if (alarmCal.getTimeInMillis() > System.currentTimeMillis()) {
					reserveTime = diffInMilisecond + (60 * 60 * 1000);
				} else {
					reserveTime = (60 * 60 * 1000) - diffInMilisecond;
				}

				setAlaramForNotification(reserveTime, offersDetailsVo);

           }
			else
			{
				Log.v("Don't send local notification already checked in",String.valueOf(isCheckin));
			}
		}
	}
	/**
	 * Set Alarm for local notification for individual offers.
	 * 
	 * @param alramTime
	 * @param offersDetailsVo
	 */
	private void setAlaramForNotification(long alramTime ,OffersDetailsVo offersDetailsVo)
	{
		/*Toast.makeText(getApplicationContext(), "Alarm set ", Toast.LENGTH_LONG).show();
		Long timesMilliseconds = System.currentTimeMillis()+alramTime;
		String alarmTime = getDate(timesMilliseconds,"dd/MM/yyyy hh:mm:ss.SSS aa");
		Toast.makeText(getApplicationContext(), "alarmTime "+alarmTime, Toast.LENGTH_LONG).show();*/
		Log.v("Invoking setAlaramForNotification() method ", "alramTime ="+alramTime+" businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getReserveTimeStamp());
		int dealId =0;
		if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
			Bundle b = new Bundle();
	        b.putParcelable("selectOffers", offersDetailsVo);
	        Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);		
	        alarmIntent.putExtras(b);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dealId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); 
	        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (alramTime), pendingIntent);  
		    
   	}

	/**
	 * Remove the alarm for the offers .
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void  removeAlarmForNotification(OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking removeAlarmForNotification() method ", " businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getReserveTimeStamp());
		int dealId =0;
		if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
		Log.v("dealId for remove alarm ", String.valueOf(dealId));
		Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dealId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);   
	    alarmManager.cancel(pendingIntent);
	    pendingIntent.cancel();
	}


	/**
	 * This method will check the Internet connection for the application.
	 * 
	 * @return
	 */
	private boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return (conMgr.getActiveNetworkInfo() != null&& conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) ? true : false;

	}

	/**
	 * Display the Dialog message
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(
					MyOffersActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage("No offers are found to display on map");
			ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab.create();

		case 10:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(
					MyOffersActivity.this);
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

	/**
	 * Back button functioanlity
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	/*public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}*/
	
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode) {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(MyOffersActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(MyOffersActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true;
		    }

		    return super.onKeyDown(keycode, e);
		}
}
