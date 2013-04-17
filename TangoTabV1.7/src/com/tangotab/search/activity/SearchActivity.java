package com.tangotab.search.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.calendar.activity.CalendarActivity;
import com.tangotab.calendar.utils.CalendarView;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.core.view.RangeSeekBar;
import com.tangotab.core.view.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.tangotab.core.view.RangeSeekBar.RangeBarValues;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.adapter.CustomWheelAdapter;
import com.tangotab.search.vo.SearchVo;

/**
 * Search activity class will be used for search the restaurant.
 * 
 * <br>
 * Class :SearchActivity <br>
 * Layout :search.xml
 * 
 * @author Dillip.Lenka
 * 
 */
public class SearchActivity extends ListActivity {
	/**
	 * Meta definitions
	 */
	private EditText editAddress;
	private int pageCount = 1;
	ListView itemsList = null;
	private LinearLayout llShowMore;
	private List<DealsDetailVo> finalDealList = new ArrayList<DealsDetailVo>();
	private String disvalues[] = { "1 Mile", "3 Miles", "5 Miles", "10 Miles","20 Miles", "50 Miles", "50+ Miles" };
	private String disvaluesKm[] = { "1 Km", "3 Km", "5 Km", "10 Km", "20 Km","50 Km", "50+ Km" };

	public TangoTabBaseApplication application;
	private Vibrator myVib;
	private int mNewIndex;
	// Scrolling flag
	private boolean scrolling = false;
	private String sel_distance = null;
	private Button distance, location, calendar;
	private int valueToSet;
	public String userName, postalZip;
	private String workZip;
	public static final int LOCATION_REQUEST_CODE = 19;
	public static final int CALENDAR_REQUEST_CODE = 1;
	private int maxTime = 24;
	private int minTime = 0;
	private static int count;
	private TextView emptyText;
	private String country;
	private GoogleAnalyticsTracker tracker;

	/**
	 * Execution will start here.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search);

		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY, 10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SEARCH_PAGE);
		tracker.trackEvent("Search", "TrackEvent", "search", 1);

		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		itemsList = (ListView) findViewById(android.R.id.list);
		editAddress = (EditText) findViewById(R.id.searchaddress);
		calendar = (Button) findViewById(R.id.date);

		application = (TangoTabBaseApplication) getApplication();

		if (!ValidationUtil.isNullOrEmpty(application.getDealsList()))
			application.getDealsList().clear();

		llShowMore = (LinearLayout) getLayoutInflater().inflate(
				R.layout.showmorecell, null);

		// Button topSearchBtn = (Button)
		// findViewById(R.id.topSearchMenuButton);
		Button topMenu = (Button) findViewById(R.id.topMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		Button searchOffers = (Button) findViewById(R.id.searchUpdateButton);
		distance = (Button) findViewById(R.id.distance);
		location = (Button) findViewById(R.id.Downtown);
		// Button updateButton = (Button) findViewById(R.id.searchUpdateButton);
		Button mapButton = (Button) findViewById(R.id.map);
		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
		emptyText = (TextView) findViewById(R.id.emptylist);
		// emptyText.setVisibility(View.GONE);

		SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", 0);
		country = sharedPreferences.getString("country", "");
		if (!ValidationUtil.isNullOrEmpty(country)
				&& country.equalsIgnoreCase("canada")) {
			disvalues = disvaluesKm;
		}

		SharedPreferences date = getSharedPreferences("SearchDate", 0);
		String selectedDate = date.getString("dateSelected", "");
		if (!ValidationUtil.isNullOrEmpty(selectedDate)
				&& !selectedDate.equals("Select Date"))
			calendar.setText(selectedDate);
		else {
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
			selectedDate = sdfDate.format(today.getTime());
			calendar.setText(selectedDate);
		}

		SharedPreferences spc1 = getSharedPreferences("locationDetail", 0);
		String locationText = spc1.getString("currentLocation",	"Current Location");
		location.setText(locationText);
		/**
		 * Get customURlHandler information.
		 * 
		 */
		Bundle bundel = getIntent().getExtras();
		if (bundel != null) {
			String fromPage = (String) bundel.getString("fromPage");
			if (!ValidationUtil.isNullOrEmpty(fromPage)
					&& fromPage.equals("customURL")) {
				String address = (String) bundel.getString("address");
				if (!ValidationUtil.isNullOrEmpty(address)) {
					editAddress.setText(address);
					SearchVo searchVo = new SearchVo();
					getSearch(searchVo);
					SharedPreferences range = getSharedPreferences("RangeBar", 0);
					int max = range.getInt("maxTime", 0);
					int min = range.getInt("minTime", 0);
					searchVo.setMinTime(min);
					if(max!=0)
						searchVo.setMaxTime(max);
					else
						searchVo.setMaxTime(24);
					/*
					 * Starting main menu activity
					 */
					
					
					Intent nearmeIntent = new Intent(SearchActivity.this,NearMeActivity.class);
					nearmeIntent.putExtra("searchVo", searchVo);
					nearmeIntent.putExtra("fromPage", "searchPage");				
					nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(nearmeIntent);
				} else {
					DealsDetailVo dealsDetailVo = (DealsDetailVo) bundel.getSerializable("selectDeal");
					String dealId = (String) bundel.getString("dealId");
					if (!ValidationUtil.isNull(dealsDetailVo)&& !ValidationUtil.isNullOrEmpty(dealId))
					{
						/**
						 * Start the claim offer activity with deal id and deal
						 * date.
						 */
						Intent claimIntent = new Intent(getApplicationContext(),ClaimOfferActivity.class);
						claimIntent.putExtra("from", "search");
						claimIntent.putExtra("fromPage", "customURL");
						claimIntent.putExtra("dealId", dealId);
						claimIntent.putExtra("selectDeal", dealsDetailVo);

						claimIntent.putExtra("fromsearch", "fromsearch");
						startActivity(claimIntent);
					} /*else {
						SearchVo searchVo = new SearchVo();
						*//**
						 * Get the current location details
						 *//*
						initalizeLocationDetails(searchVo);
						*//**
						 * Call the asynctask to get the deals list.
						 *//*
						new DealsListAsyncTask().execute(searchVo);
					}*/
				}

			}
		}

		/*
		 * Search offers button onclick listner.
		 */
		searchOffers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
				SharedPreferences.Editor edits11 = spc11.edit();
				edits11.putString("SelectedLocation", (String) location.getText());
				edits11.commit();
				
				SearchVo searchVo = new SearchVo();
				getSearch(searchVo);
				SharedPreferences range = getSharedPreferences("RangeBar", 0);
				int max = range.getInt("maxTime", 0);
				int min = range.getInt("minTime", 0);
				searchVo.setMinTime(min);
				if(max!=0)
					searchVo.setMaxTime(max);
				else
					searchVo.setMaxTime(24);
				/*
				 * Starting main menu activity
				 */
				
				
				Intent nearmeIntent = new Intent(SearchActivity.this,NearMeActivity.class);
				nearmeIntent.putExtra("searchVo", searchVo);
				nearmeIntent.putExtra("fromPage", "searchPage");				
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);				
				

			}
		});
		/*
		 * custom range seekbar
		 */
		
		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(minTime,maxTime, this);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				// getSearch(1);

			}
		});
		SharedPreferences range = getSharedPreferences("RangeBar", 0);
		int max = range.getInt("maxTime", 0);
		int min = range.getInt("minTime", 0);
		seekBar.setSelectedMinValue(min);
		if(max!=0)
			seekBar.setSelectedMaxValue(max);
		else
			seekBar.setSelectedMaxValue(24);
		/*
		 * custom range seekbar
		 */
		seekBar.setRangeBarValuesListener(new RangeBarValues() {

			@Override
			public void getMinVal(long minVal, long maxVal) {
				Log.i("HOUR RANGE", "User selected new range values: MIN="
						+ minVal + ", MAX=" + maxVal);
				maxTime = (int) maxVal;
				minTime = (int) minVal;
				if (maxTime == 0)
					maxTime = 24;
				
				SharedPreferences range = getSharedPreferences("RangeBar", 0);
				SharedPreferences.Editor scrollEdit = range.edit();
				scrollEdit.putInt("minTime", minTime);
				scrollEdit.putInt("maxTime", maxTime);
				scrollEdit.commit();
			}
		});
		/**
		 * Map button on click listener
		 */

		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {

				Vibrator myVib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				myVib.vibrate(50);

				new Handler().postDelayed(new Runnable() {
					public void run() {
						v.setClickable(true);
					}
				}, 600);

				if (checkInternetConnection()) {
					/**
					 * get list of deals from application.
					 */
					List<DealsDetailVo> mapDealsList = application.getSearchList();
					if (ValidationUtil.isNullOrEmpty(mapDealsList))
					{						
						SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
						String lat = currentLocation.getString("locLat", "");
						String lang = currentLocation.getString("locLong", "");
						if (!ValidationUtil.isNullOrEmpty(lat)&& !ValidationUtil.isNullOrEmpty(lang)) {
							try {
								AppConstant.dev_lat = Double.valueOf(lat);
								AppConstant.dev_lang = Double.valueOf(lang);

							} catch (NumberFormatException e) {

								Log.e("Exception",
										"Exception occuerd when converting String int doubble",
										e);
							}
						}

						List<Address> addressList = null;

						Geocoder geocoder = new Geocoder(getBaseContext(),
								Locale.getDefault());
						try {
							addressList = geocoder.getFromLocation(AppConstant.dev_lat, AppConstant.dev_lang,1);
						} catch (IOException e) {
							Log.e("Exception:",
									"Exception occuerd at the time getting address list from Geo Coder.");
							e.printStackTrace();
						}

						if (!ValidationUtil.isNullOrEmpty(addressList))
						{
							Intent myOfferMapIntent = new Intent(getApplicationContext(),MappingActivity.class);
							myOfferMapIntent.putExtra("businessname",addressList.get(0).getAddressLine(0));
							myOfferMapIntent.putExtra("dealname", addressList.get(0).getAddressLine(0));
							myOfferMapIntent.putExtra("IsFromPlaceOrSearch","mySettings");
							myOfferMapIntent.putExtra("fromPage", "mySettings");
							startActivity(myOfferMapIntent);
						}

					} else {

						new Thread(new Runnable() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										final Context appContext = getApplicationContext();
										/**
										 * Start map activity
										 */
										Intent mapIntent = new Intent(appContext,MappingActivity.class);
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

		// add RangeSeekBar to pre-defined layout
		ViewGroup layout = (ViewGroup) findViewById(R.id.rangeSeekBarLayout);
		layout.addView(seekBar);

		/*
		 * top menu onclick listener
		 */
		topMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Starting main menu activity
				 */
				Intent mainMenuIntent = new Intent(SearchActivity.this,MainMenuActivity.class);
				mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
			}
		});

		/*
		 * me button onClick listener.
		 */
		meButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent meIntent = new Intent(SearchActivity.this,MeActivity.class);
				meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(meIntent);
			}
		});
		/*
		 * calendar button onClick listener
		 */
		calendar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent calendarIntent = new Intent(SearchActivity.this,
						CalendarActivity.class);
				String date = calendar.getText().toString();
				String split[] = date.split("/");
				int day = Integer.parseInt(split[1]);
				int month = Integer.parseInt(split[0]);
				int year = Integer.parseInt(split[2]);
				Log.e("year..", "" + year);
				CalendarView.prevDay = day;
				CalendarView.prevMonth = month - 1;
				CalendarView.prevYear = year;
				/*
				 * calendarIntent.putExtra("prevDay",day);
				 * calendarIntent.putExtra("prevMonth",month);
				 * calendarIntent.putExtra("prevYear",year);
				 */
				startActivityForResult(calendarIntent, CALENDAR_REQUEST_CODE);

			}
		});

		/*
		 * near me button onClick listener
		 */
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent = new Intent(SearchActivity.this,
						NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});

		final LinearLayout wheelLayout = (LinearLayout) findViewById(R.id.wheelLayout);
		Button doneBtn = (Button) findViewById(R.id.doneBtn);

		final WheelView wheelView = (WheelView) findViewById(R.id.radiusWheelView);
		wheelView.setViewAdapter(new RadiusAdapter(this));
		wheelView.setVisibleItems(3);

		/**
		 * scroll change listener
		 */

		wheelView.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					populateRadius(wheel, disvalues, newValue);
				}
			}
		});

		/**
		 * scroll listener
		 */
		wheelView.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				populateRadius(wheel, disvalues, wheel.getCurrentItem());
				mNewIndex = wheel.getCurrentItem();
			}
		});
		wheelView.setCurrentItem(1);
		wheelView.setCurrentItem(0);
		wheelView.setCurrentItem(mNewIndex);
		/**
		 * distance onclick listener
		 */
		distance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences spc1 = getSharedPreferences("Distance", 0);
				String dist = spc1.getString("distancevalue", "20");

				if (dist == null)
					dist = disvalues[4];
				if (dist.equalsIgnoreCase("1000"))
					dist = disvalues[6];
				if (dist.equalsIgnoreCase("5"))
					dist = disvalues[2];
				if (dist.equalsIgnoreCase("10"))
					dist = disvalues[3];
				if (dist.equalsIgnoreCase("20"))
					dist = disvalues[4];
				if (dist.equalsIgnoreCase("50"))
					dist = disvalues[5];
				if (dist.equalsIgnoreCase("3"))
					dist = disvalues[1];
				if (dist.equalsIgnoreCase("1"))
					dist = disvalues[0];

				for (int i = 0; i < disvalues.length; i++) {
					if (disvalues[i].equalsIgnoreCase(dist.trim())) {
						valueToSet = i;
						break;
					}
				}
				wheelView.setCurrentItem(valueToSet);
				wheelLayout.setVisibility(View.VISIBLE);
				// popupWindow.showAsDropDown(v);

			}

		});
		/*
		 * near me button onClick listener
		 */
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wheelLayout.setVisibility(View.GONE);
			}
		});

		/**
		 * Edit listener for edit Address name edit.
		 */

		editAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) 
						{
							/*if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
								application.getSearchList().clear();*/
							SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
							SharedPreferences.Editor edits11 = spc11.edit();
							edits11.putString("SelectedLocation", (String) location.getText());
							edits11.commit();
							
							SearchVo searchVo = new SearchVo();
							getSearch(searchVo);
							SharedPreferences range = getSharedPreferences("RangeBar", 0);
							int max = range.getInt("maxTime", 0);
							int min = range.getInt("minTime", 0);
							searchVo.setMinTime(min);
							if(max!=0)
								searchVo.setMaxTime(max);
							else
								searchVo.setMaxTime(24);
							/*
							 * Starting main menu activity
							 */
							Intent nearmeIntent = new Intent(SearchActivity.this,NearMeActivity.class);
							nearmeIntent.putExtra("searchVo", searchVo);
							nearmeIntent.putExtra("fromPage", "searchPage");				
							nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(nearmeIntent);
							return true;
						}
						return false;
					}
				});

		/**
		 * On click listener for show more deals
		 *//*
		*//**
		 * Auto scroll down offers
		 * 
		 *//*
		itemsList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;

				// is the bottom item visible & not loading more already ?
				// Load more !
				if ((lastInScreen == totalItemCount) && totalItemCount != 1
						&& totalItemCount != totalItemCount + 10
						&& totalItemCount == count) {
					pageCount++;
					count += 10;
				}
			}
		});*/

		/**
		 * scroll done button listener
		 */
		doneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sel_distance = disvalues[mNewIndex];
				distance.setText("View " + sel_distance);

				if (sel_distance.equalsIgnoreCase("50+ Miles")
						|| sel_distance.equalsIgnoreCase("50+ Km")) {
					sel_distance = "1000";
				}
				if (sel_distance.equalsIgnoreCase("50 Miles")
						|| sel_distance.equalsIgnoreCase("50 Km")) {
					sel_distance = "50";
				}
				if (sel_distance.equalsIgnoreCase("20 Miles")
						|| sel_distance.equalsIgnoreCase("20 Km")) {
					sel_distance = "20";
				}
				if (sel_distance.equalsIgnoreCase("10 Miles")
						|| sel_distance.equalsIgnoreCase("10 Km")) {
					sel_distance = "10";
				}
				if (sel_distance.equalsIgnoreCase("5 Miles")
						|| sel_distance.equalsIgnoreCase("5 Km")) {
					sel_distance = "5";
				}
				if (sel_distance.equalsIgnoreCase("3 Miles")
						|| sel_distance.equalsIgnoreCase("3 Km")) {
					sel_distance = "3";
				}
				if (sel_distance.equalsIgnoreCase("1 Mile")
						|| sel_distance.equalsIgnoreCase("1 Km")) {
					sel_distance = "1";
				}
				SharedPreferences spc = getSharedPreferences("Distance", 0);
				String distance = spc.getString("distancevalue", "20");
				/**
				 * put the distance information in shared preferences.
				 */
				if (!distance.equals(sel_distance)) {
					SharedPreferences.Editor edit = spc.edit();
					edit.putString("distancevalue", sel_distance);
					edit.commit();
					pageCount = 1;
				}
				AppConstant.IS_SETTINGSCHANGED = "99999";
				/**
				 * Clear the dfeals from the application.
				 */
				clearFromApplication();
				wheelLayout.setVisibility(View.GONE);
				
			}
		});

		MySettings();

		/**
		 * location button onclick listener.
		 */
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent locationIntent = new Intent(SearchActivity.this,
						LocationActivity.class);
				locationIntent.putExtra("selectedLoc", location.getText());
				locationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(locationIntent, LOCATION_REQUEST_CODE);
			}
		});

		
	}

	/**
	 * Populate the radius wheel
	 */
	private void populateRadius(WheelView radiusWheel, String[] disvalues,
			int index) {
		CustomWheelAdapter adapter = new CustomWheelAdapter(this, disvalues);
		adapter.setTextSize(17);
		radiusWheel.setViewAdapter(adapter);
		radiusWheel.setCurrentItem(index);
	}

	/**
	 * search the deals from page count
	 * 
	 * @param pageCount
	 */
	private void getSearch(SearchVo searchVo) {
		Map<String,String> locationMap = null;
		searchVo.setPageIndex(String.valueOf(1));
		String addressText = editAddress.getText().toString().trim();
		boolean isZipCode = ValidationUtil.validateCanadZip(addressText)|| ValidationUtil.validateUSAZip(addressText);
		if (!isZipCode) 
		{
			searchVo.setRestName(addressText);
		} 
		else
		{
			locationMap = ValidationUtil.getLatLonFromAddress(addressText);
			if(!ValidationUtil.isNullOrEmpty(locationMap))
			{
				Log.v("lat and lng", "lat= "+locationMap.get("lat")+" lang "+locationMap.get("lng"));
				searchVo.setLocLaong(locationMap.get("lng"));
				searchVo.setLocLat(locationMap.get("lat"));
				searchVo.setAddress(addressText);
			}
		}

		SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
		String homeZipCode = spc1.getString("postal", "");
		String workZipCode = spc1.getString("workZip", "");
		String alternateZipCode = spc1.getString("alternateZip", "");

		if (location.getText().toString().equalsIgnoreCase("Current Location") && !isZipCode) 
		{
			SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
			String lat = currentLocation.getString("locLat", "");
			String lang = currentLocation.getString("locLong", "");
			searchVo.setLocLat(lat);
			searchVo.setLocLaong(lang);
			
			
			Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
			{
				try {
					double currentLat = Double.valueOf(lat);
					double currentLang = Double.valueOf(lang);
					List<Address> addresses = geocoder.getFromLocation(currentLat, currentLang, 5);
					if (addresses != null) 
					{
						Address returnedZip = addresses.get(0);
						String zipCode = returnedZip.getPostalCode();
						String address = returnedZip.getAddressLine(0);
						searchVo.setAddress(address);
						// searchVo.setZipCode(zipCode);
					}
				} catch (Exception e) {
					Log.e("Exception:",
							"Exception occured to find the currentLocation");
				}
			}
		}
		else if (location.getText().toString().equalsIgnoreCase("Home") && !isZipCode)
		{
			locationMap = ValidationUtil.getLatLonFromAddress(homeZipCode);
			if(!ValidationUtil.isNullOrEmpty(locationMap))
			{
				Log.v("lat and lng", "lat= "+locationMap.get("lat")+" lang "+locationMap.get("lng"));
				searchVo.setLocLaong(locationMap.get("lng"));
				searchVo.setLocLat(locationMap.get("lat"));
				searchVo.setAddress(homeZipCode);
			}

		}
		else if (location.getText().toString().equalsIgnoreCase("Work") && !isZipCode) 
		{
			locationMap = ValidationUtil.getLatLonFromAddress(workZipCode);
			if(!ValidationUtil.isNullOrEmpty(locationMap))
			{
				Log.v("lat and lng", "lat= "+locationMap.get("lat")+" lang "+locationMap.get("lng"));
				searchVo.setLocLaong(locationMap.get("lng"));
				searchVo.setLocLat(locationMap.get("lat"));
				searchVo.setAddress(workZipCode);
			}

		}
		else if (location.getText().toString().equalsIgnoreCase("Alternate") && !isZipCode) 
		{
			locationMap = ValidationUtil.getLatLonFromAddress(alternateZipCode);
			if(!ValidationUtil.isNullOrEmpty(locationMap))
			{
				Log.v("lat and lng", "lat= "+locationMap.get("lat")+" lang "+locationMap.get("lng"));
				searchVo.setLocLaong(locationMap.get("lng"));
				searchVo.setLocLat(locationMap.get("lat"));
				searchVo.setAddress(workZipCode);
			}

		}
		SharedPreferences spc = getSharedPreferences("Distance", 0);
		String distance_set = spc.getString("distancevalue", "20");
		if (ValidationUtil.isNullOrEmpty(distance_set)) {
			distance_set = "20";
		}

		searchVo.setDistance(distance_set);
		SharedPreferences spc3 = getSharedPreferences("UserDetails", 0);
		String userId = spc3.getString("UserId", "");
		Log.v("userId in search page", userId);
		searchVo.setUserId(userId);
		searchVo.setMaxTime(maxTime);
		searchVo.setMinTime(minTime);
		if (calendar.getText() != null)
		{
			String selectedDate = calendar.getText().toString();
			try {
				String[] spit = selectedDate.split("/");
				if (spit != null && spit[0] != null) {
					StringBuilder selectdate = new StringBuilder();
					selectdate.append(spit[2]).append("-").append(spit[0]).append("-").append(spit[1]);
					searchVo.setDate(selectdate.toString());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e("Exception:",
						"Exceptions occured as seleceted date is null", e);
			}
		}
		application.setSearchVo(searchVo);
		

	}

	/*
	 * Radius Adapter for distance dropdown in search page.
	 * 
	 * @author Lakshmipathi.P
	 */
	private class RadiusAdapter extends AbstractWheelTextAdapter {

		/**
		 * Constructor
		 */
		protected RadiusAdapter(Context context) {
			super(context, R.layout.distancetext, NO_RESOURCE);

			setItemTextResource(R.id.radiusText);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);

			return view;
		}

		@Override
		public int getItemsCount() {
			return disvalues.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return disvalues[index];
		}
	}
	
	/**
	 * On Pause implementation added.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// Save scroll position
		SharedPreferences preferences = getSharedPreferences("SCROLL", 0);
		SharedPreferences.Editor edit = preferences.edit();
		if (itemsList != null) {
			int scroll = itemsList.getFirstVisiblePosition();
			edit.putInt("ScrollValue", scroll);
			View v = itemsList.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();
			edit.putInt("Top", top);
			edit.commit();
		}
	}

	/**
	 * On resume implementation added.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getSharedPreferences("SCROLL", 0);
		int scroll = preferences.getInt("ScrollValue", 0);
		int top = preferences.getInt("Top", 0);
		if (itemsList != null)
			itemsList.setSelectionFromTop(scroll, top);
	};

	/**
	 * Back button functionality added.
	 */

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * clear the information from the application object.
	 * 
	 */
	private void clearFromApplication() {
		if (!ValidationUtil.isNullOrEmpty(application.getSearchList()))
			application.getSearchList().clear();
		if (!ValidationUtil.isNullOrEmpty(application.getOffersList()))
			application.getOffersList().clear();
		if (!ValidationUtil.isNullOrEmpty(application.getDealsList()))
			application.getDealsList().clear();

		/**
		 * Put the scroll to top of the list.
		 */

		SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
		SharedPreferences.Editor ScrollEdit = preferences.edit();
		ScrollEdit.putInt("scrollValue", 0);
		ScrollEdit.putInt("Top", 0);
		ScrollEdit.commit();

		/**
		 * Put the scroll to top of the list.
		 */
		SharedPreferences searchPreferences = getSharedPreferences("SCROLL", 0);
		SharedPreferences.Editor searchEdit = searchPreferences.edit();
		searchEdit.putInt("ScrollValue", 0);
		searchEdit.putInt("Top", 0);
		searchEdit.commit();
	}

	/**
	 * Function to Fetch existing user settings
	 */

	public void MySettings() {

		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		String dist = spc1.getString("distancevalue", "20");
		if (dist == null)
			dist = disvalues[4];
		if (dist.equalsIgnoreCase("1000"))
			dist = disvalues[6];
		if (dist.equalsIgnoreCase("5"))
			dist = disvalues[2];
		if (dist.equalsIgnoreCase("10"))
			dist = disvalues[3];
		if (dist.equalsIgnoreCase("20"))
			dist = disvalues[4];
		if (dist.equalsIgnoreCase("50"))
			dist = disvalues[5];
		if (dist.equalsIgnoreCase("3"))
			dist = disvalues[1];
		if (dist.equalsIgnoreCase("1"))
			dist = disvalues[0];
		distance.setText("View " + dist);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOCATION_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				location.setText(data.getStringExtra("locationSel"));
				postalZip = data.getStringExtra("Zip_Code");
				workZip = data.getStringExtra("Work_Zip");

			}
		}
		if (requestCode == CALENDAR_REQUEST_CODE) {
			if (data != null) {
				String day = data.getStringExtra("day");
				String month = data.getStringExtra("month");
				String year = data.getStringExtra("year");
				StringBuilder dateSelected = new StringBuilder();
				dateSelected.append("").append(month).append("/").append(day)
						.append("/").append(year);
				Log.e("date selected...", dateSelected.toString());
				calendar.setText(dateSelected.toString());
				/**
				 * Put selected date into shared preferences.
				 */
				SharedPreferences date = getSharedPreferences("SearchDate", 0);
				SharedPreferences.Editor ScrollEdit = date.edit();
				ScrollEdit.putString("dateSelected", dateSelected.toString());
				ScrollEdit.commit();

			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 10:
			AlertDialog.Builder ab10 = new AlertDialog.Builder(
					SearchActivity.this);
			ab10.setTitle("TangoTab");
			ab10.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");

			ab10.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});
			return ab10.create();
		}
		return null;

	}

	/**
	 * Initializes the Location Manager and attaches the LocationUpdates
	 * Listener for the device current Lat Lng values
	 */
	private void initalizeLocationDetails(SearchVo searchVo) {
		/*
		 * GPS Configuration
		 */

		searchVo.setType("A");
		searchVo.setNoOfDeals("0");
		searchVo.setPageIndex("0");
		SharedPreferences location = getSharedPreferences("LocationDetails", 0);
		String locLat = location.getString("locLat", "");
		String locLong = location.getString("locLong", "");
		searchVo.setLocLat(String.valueOf(locLat));
		searchVo.setLocLaong(String.valueOf(locLong));
		double lat = Double.valueOf(locLat);

		double lang = Double.valueOf(locLong);
		/**
		 * Get address from Geocoder
		 */
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
		{
			try {
				List<Address> addresses = geocoder
						.getFromLocation(lat, lang, 1);
				if (addresses != null) {
					Address returnedZip = addresses.get(0);
					String zipCode = returnedZip.getPostalCode();
					String address = returnedZip.getAddressLine(0);
					searchVo.setAddress(address);
					searchVo.setZipCode(zipCode);
				}
			} catch (Exception e) {
				Log.e("Exception:",
						"Exception occured to find the currentLocation");
			}
		}
		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		String distance = spc1.getString("distancevalue", "20");
		/**
		 * Take the default distance if distance is null or empty
		 */
		if (ValidationUtil.isNullOrEmpty(distance)) {
			distance = "20";
		}
		searchVo.setDistance(distance);

		/**
		 * Get user details from shared preferences.
		 */
		SharedPreferences spc3 = getSharedPreferences("UserDetails", 0);
		String userId = spc3.getString("UserId", "");
		searchVo.setUserId(userId);
		searchVo.setVersionName(application.getAppVerison());
		application.setSearchVo(searchVo);
	}

	/**
	 * This method will check the Internet connection for the application.
	 * 
	 * @return
	 */
	private boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable() && conMgr
				.getActiveNetworkInfo().isConnected()) ? true : false;

	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			Intent mainMenuIntent = new Intent(SearchActivity.this,
					MainMenuActivity.class);
			mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainMenuIntent);
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			Intent searchIntent = new Intent(SearchActivity.this,
					SearchActivity.class);
			searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(searchIntent);
			return true;
		}

		return super.onKeyDown(keycode, e);
	}
}
