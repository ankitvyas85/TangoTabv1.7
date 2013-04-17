package com.tangotab.nearMe.activity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

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
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.tangotab.R;
import com.tangotab.appNotification.activity.AppNotificationActivity;
import com.tangotab.calendar.activity.CalendarActivity;
import com.tangotab.calendar.utils.CalendarView;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.contactSupport.activity.ContactSupportActvity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ApplicationDetails;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.me.service.MyPhilanthropyService;
import com.tangotab.myOfferDetails.activity.MyoffersDetailActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.myOffers.service.MyOffersService;
import com.tangotab.nearMe.adapter.NearMeGalleryAdapter;
import com.tangotab.nearMe.adapter.NearMeListAdapter;
import com.tangotab.nearMe.adapter.NearMeeUpScaleAdapter;
import com.tangotab.nearMe.service.NearMeService;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.nearMe.vo.NearMeVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.search.adapter.CustomWheelAdapter;
import com.tangotab.search.adapter.SearchListAdapter;
import com.tangotab.search.service.SearchService;
import com.tangotab.search.vo.SearchVo;

/**
 * This class will be display list of deals from near to your selected distance
 * location.
 * 
 * <bR>
 * Class : NearMeActivity <br>
 * Layout : nearme.xml
 * 
 * @author Dillip.Lenka
 * 
 */
public class NearMeActivity extends ListActivity implements LocationListener {
	/*
	 * Meta Definitions
	 */
	private String userId;
	private String zipCode;
	private String cityName;
	private String provider;
	private double dev_lat;
	private double dev_lang;
	private LocationManager locationManager;
	private LinearLayout llShowMore;
	private ListView itemsList;
	private List<DealsDetailVo> finalDealList = new ArrayList<DealsDetailVo>();
	private int pageCount = 1;
	private LoginVo loginvo;
	private TextView emptyList, currentDate, locationInfo;
	private TextView dinningType;
	public TangoTabBaseApplication application;
	private List<OffersDetailsVo> offersList, uncheckedOffers;
	// private String frmPage;
	private Vibrator vibrator;
	private static int count;
	private RelativeLayout statusFed;
	private TextView statusFedLabel;
	private RelativeLayout statusPendingOffers;
	private TextView statusPendingOffersLabel;
	private List<Address> addressList;
	private String country;
	private RelativeLayout relativeLayoutTopBar;
	// SwipeDetector swipeDetector;
	private boolean islistRefreshed = false;
	private String[] dinningStyles = new String[] { "ALL", "Breakfast",
			"Lunch", "Happy Hour", "Dinner", "Late" };
	int[] pics = { R.drawable.casual_selector, R.drawable.upscale_selector,
			R.drawable.fine_selector };

	private int[] topBarImgs = new int[] { R.drawable.white_one,
			R.drawable.white_two, R.drawable.white_three };
	GestureDetector gestureDetector;
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;
	private int diningStyle = 0;
	private String dinningStyleType;

	
	private String diningId;
	private String selectedDate;
	private GoogleAnalyticsTracker tracker;
	private String serverDate;
	private boolean scrolling = false;
	private String fromPage;
	private int mNewIndex;
	private int valueToSet = 0;
	private int pendingOffersStVisible;
	private Button getStarted;
	private RelativeLayout Started;
	private String getStartedPage;
	private String selectedLocation;
	private SharedPreferences gallerySelectedPreferences;
	private SharedPreferences.Editor galleryEditor;
	private NearMeGallery ga;
	private int selectedposition;

	/**
	 * Execution will start here
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearme);
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY, 10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.NEARME_PAGE);
		tracker.trackEvent("NearMe", "TrackEvent", "nearme", 1);
		// String title= getIntent().getExtras().getString("frmPagelogin");

		DisplayMetrics dm = getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

		// Display location info selected by user.
		SharedPreferences spc11 = getSharedPreferences("locationDetail", 0);
		selectedLocation = spc11.getString("SelectedLocation",
				"Current Location");
		locationInfo = (TextView) findViewById(R.id.locationLabel);
		if (selectedLocation.equalsIgnoreCase("Current Location")) {
			locationInfo.setText(selectedLocation);
		} else {
			locationInfo.setText(selectedLocation + " Location");
		}
		gallerySelectedPreferences = getSharedPreferences(
				AppConstant.SELECTED_PREFS, Context.MODE_PRIVATE);
		galleryEditor = gallerySelectedPreferences.edit();

		//
		Started = (RelativeLayout) findViewById(R.id.getStartedRl);
		getStarted = (Button) findViewById(R.id.getStarted);

		Bundle getStartedBun = getIntent().getExtras();
		if (getStartedBun != null) {
			getStartedPage = (String) getStartedBun.getString("isGetStarted");
			if (!ValidationUtil.isNullOrEmpty(getStartedPage)
					&& getStartedPage.equalsIgnoreCase("true")) {
				Started.setVisibility(View.VISIBLE);
				getStarted.setVisibility(View.VISIBLE);
			} else {
				Started.setVisibility(View.GONE);
				getStarted.setVisibility(View.GONE);
			}
		}

		getStarted.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Started.setVisibility(View.GONE);
				getStarted.setVisibility(View.GONE);

			}
		});

		// gestureDetector = new GestureDetector(new SwipeListener());

		String searchBy = getIntent().getStringExtra("SearchBy");

		// Initialize the GPS service for obtaining the Device Latitude and
		// Longitude Values
		if (selectedLocation.equalsIgnoreCase("Current Location")) {
			initalizeLocationManagerService();
		}
		relativeLayoutTopBar = (RelativeLayout) findViewById(R.id.relativeLayoutBackground);
		emptyList = (TextView) findViewById(R.id.emptylist);
		final SharedPreferences datePreferences = getSharedPreferences(
				"NearMeDate", 0);
		itemsList = (ListView) findViewById(android.R.id.list);
		final GestureDetector gestureDetector = new GestureDetector(
				new MyGestureDetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.e("on touch", "called");
				gestureDetector.onTouchEvent(event);
				return false;
			}
		};
		itemsList.setOnTouchListener(gestureListener);
		View.OnTouchListener textListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.e("on touch", "called");

				return gestureDetector.onTouchEvent(event);
			}
		};
		emptyList.setOnTouchListener(textListener);
		relativeLayoutTopBar.setOnTouchListener(textListener);
		// itemsList.setOnItemClickListener(this);
		// swipeDetector=new SwipeDetector();

		llShowMore = (LinearLayout) getLayoutInflater().inflate(
				R.layout.showmorecell, null);
		Button map = (Button) findViewById(R.id.map);
		Button topMenu = (Button) findViewById(R.id.topMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button search = (Button) findViewById(R.id.topSearchMenuButton);
		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
		statusFed = (RelativeLayout) findViewById(R.id.statusbarFed);
		statusFedLabel = (TextView) findViewById(R.id.statusbarFedLabel);
		statusPendingOffers = (RelativeLayout) findViewById(R.id.statusPendingOffers);
		statusPendingOffersLabel = (TextView) findViewById(R.id.statusbarPendingOffersLabel);
		// statusFed.setVisibility(View.GONE);
		statusPendingOffers.setVisibility(View.GONE);
		dinningType = (TextView) findViewById(R.id.tvDinningType);
		currentDate = (TextView) findViewById(R.id.tvCurrentDate);

		String calenderText = datePreferences.getString("selectText", "");
		selectedDate = datePreferences.getString("selectedDate", "");
		if (!ValidationUtil.isNullOrEmpty(calenderText)
				&& !ValidationUtil.isNullOrEmpty(selectedDate)) {
			currentDate.setText(calenderText);
		} else {
			Calendar today = Calendar.getInstance();
			// StringBuilder currentDate = new StringBuilder();
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
			selectedDate = sdfDate.format(today.getTime());
			currentDate.setText("Today");
		}

		currentDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent getDate = new Intent(NearMeActivity.this,
						CalendarActivity.class);
				int year = datePreferences.getInt("year", 0);
				int month = datePreferences.getInt("month", 0);
				int day = datePreferences.getInt("day", 0);
				CalendarView.prevDay = day;
				CalendarView.prevMonth = month;
				CalendarView.prevYear = year;
				startActivityForResult(getDate,
						AppConstant.GET_DATE_REQUEST_CODE);
				application.getDealsList().clear();

			}
		});

		SharedPreferences currentLocation = getSharedPreferences(
				"LocationDetails", 0);
		String lat = currentLocation.getString("locLat", "");
		String lang = currentLocation.getString("locLong", "");
		if (!ValidationUtil.isNullOrEmpty(lat)
				&& !ValidationUtil.isNullOrEmpty(lang)) {
			try {
				AppConstant.dev_lat = Double.valueOf(lat);
				AppConstant.dev_lang = Double.valueOf(lang);

			} catch (NumberFormatException e) {

				Log.e("Exception",
						"Exception occuerd when converting String int doubble",
						e);
			}
		}

		Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			addressList = geocoder.getFromLocation(AppConstant.dev_lat,
					AppConstant.dev_lang, 1);
		} catch (IOException e) {
			Log.e("Exception:",
					"Exception occuerd at the time getting address list from Geo Coder.");
			e.printStackTrace();
		}

		if (!ValidationUtil.isNullOrEmpty(addressList)) {
			/*
			 * SharedPreferences sharedPreferences =
			 * getSharedPreferences("UserDetails", 0); String country =
			 * sharedPreferences.getString("country", "");
			 * if(ValidationUtil.isNullOrEmpty(country)) {
			 * SharedPreferences.Editor edit1 = sharedPreferences.edit();
			 * edit1.putString("country",
			 * addressList.get(0).getCountryName().trim()); edit1.commit(); }
			 */
			

			SharedPreferences citySpc = getSharedPreferences("cityandzip", 0);
			SharedPreferences.Editor edit = citySpc.edit();
			edit.putString("cityname", addressList.get(0).getAddressLine(0));
			edit.putString("zipCode", addressList.get(0).getPostalCode());
			edit.putString("devLat", String.valueOf(AppConstant.dev_lat));
			edit.putString("devLang", String.valueOf(AppConstant.dev_lang));
			edit.commit();
		}
		SharedPreferences sharedPreferences = getSharedPreferences(
				"UserDetails", 0);
		country = sharedPreferences.getString("country", "");

		/**
		 * Slider onItem selected implementation added.
		 */
		ga = (NearMeGallery) findViewById(R.id.nearMeGalleryView);
		ga.setAdapter(new NearMeGalleryAdapter(getApplicationContext(), pics));
		ga.setSelection(selectedposition,true);
		/*
		 * int previousIndex=getIntent().getIntExtra("galleryPos",2);
		 * ga.setSelection(previousIndex);
		 * diningStyle=getIntent().getIntExtra("diningStyle",1);
		 */
		int prevPos = datePreferences.getInt("slidingPos", 0);
		Bundle bun = getIntent().getExtras();
		if (bun != null) {
			fromPage = bun.getString("fromPage");

			if (fromPage != null && fromPage.equalsIgnoreCase("searchPage")) {
				ga.setSelection(0);
				dinningType.setText(dinningStyles[0]);
			} else {
				ga.setSelection(prevPos);
			}
		}

		diningStyle = datePreferences.getInt("diningStyle", 0);
		ga.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Log.v("Changed----->", "" + pos);
				relativeLayoutTopBar.setBackgroundResource(topBarImgs[pos]);
				StringBuilder dinning = new StringBuilder();
				if (pos == 0) {

					dinning.append("1").append(",").append("2").append(",")
							.append("3");
					diningId = dinning.toString();

				} else {
					diningId = String.valueOf(pos + 3);
				}

				selectedposition = pos + 1;
				if (fromPage != null && fromPage.equalsIgnoreCase("searchPage")) {
					Bundle bun = getIntent().getExtras();
					SearchVo searchVo = (SearchVo) bun
							.getSerializable("searchVo");
					String searchDate = searchVo.getDate();
					try {
						String split[] = searchDate.split("-");
						int year = Integer.parseInt(split[0]);
						int month = Integer.parseInt(split[1]);
						int day = Integer.parseInt(split[2]);
						String searchDateText = getTextFromDate(year, month,
								day);
						currentDate.setText(searchDateText);
					} catch (Exception exe) {
						Log.e("Exception :",
								"Exception occures search date parsing", exe);
					}

					searchVo.setDiningId(diningId);
					String dateText = currentDate.getText().toString();
					if (dateText.equalsIgnoreCase("Today")) {
						Calendar cal = Calendar.getInstance();
						int hour = cal.getTime().getHours();
						searchVo.setMinTime(hour);
					}
					//statusPendingOffers.setVisibility(View.GONE);
					//statusFed.setVisibility(View.GONE);
					/**
					 * Call the asynctask to get the deals list.
					 */
					new DealsListAsyncTask().execute(searchVo);

				} else {

					application.getDealsList().clear();
					finalDealList.clear();

					/**
					 * Retrieve all the deals
					 */
					getDealList(1, diningId, diningStyle);
					fromPage = null;
					pageCount = 1;
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
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
					populateRadius(wheel, dinningStyles, newValue);
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
				populateRadius(wheel, dinningStyles, wheel.getCurrentItem());
				mNewIndex = wheel.getCurrentItem();
			}
		});
		wheelView.setCurrentItem(1);
		wheelView.setCurrentItem(0);
		wheelView.setCurrentItem(mNewIndex);
		/**
		 * distance onclick listener
		 */
		dinningType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				diningStyle = datePreferences.getInt("diningStyle", 0);

				if (diningStyle == 0)
					dinningStyleType = dinningStyles[0];
				if (diningStyle == 1)
					dinningStyleType = dinningStyles[1];
				if (diningStyle == 2)
					dinningStyleType = dinningStyles[2];
				if (diningStyle == 3)
					dinningStyleType = dinningStyles[3];
				if (diningStyle == 4)
					dinningStyleType = dinningStyles[4];

				for (int i = 0; i < dinningStyles.length; i++) {
					if (dinningStyles[i].equalsIgnoreCase(dinningStyleType)) {
						valueToSet = i;
						break;
					}
				}
				wheelView.setCurrentItem(valueToSet);
				wheelLayout.setVisibility(View.VISIBLE);
				itemsList.setEnabled(false);
				pendingOffersStVisible = statusPendingOffers.getVisibility();
				statusPendingOffers.setVisibility(View.GONE);
				statusFed.setVisibility(View.GONE);

			}

		});
		/*
		 * near me button onClick listener
		 */
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wheelLayout.setVisibility(View.GONE);
				itemsList.setEnabled(true);
				if (!ValidationUtil.isNullOrEmpty(uncheckedOffers)
						&& uncheckedOffers.size() > 0
						&& pendingOffersStVisible == View.VISIBLE) {
					statusFed.setVisibility(View.GONE);
					statusPendingOffers.setVisibility(View.VISIBLE);
				} else {
					statusFed.setVisibility(View.VISIBLE);
					statusPendingOffers.setVisibility(View.GONE);
				}
			}
		});

		/**
		 * scroll done button listener
		 */
		doneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dinningStyleType = dinningStyles[mNewIndex];

				SharedPreferences datePreferences = getSharedPreferences(
						"NearMeDate", 0);
				SharedPreferences.Editor dateEdit = datePreferences.edit();

				dateEdit.putInt("diningStyle", mNewIndex);
				dateEdit.commit();
				itemsList.setEnabled(true);
				if (fromPage != null && fromPage.equalsIgnoreCase("searchPage")) {

					Bundle bun = getIntent().getExtras();
					SearchVo searchVo = (SearchVo) bun
							.getSerializable("searchVo");
					searchVo.setDiningId(diningId);
					dinningType.setText(dinningStyles[mNewIndex]);
					String dateText = currentDate.getText().toString();
					if (dateText.equalsIgnoreCase("Today")) {
						Calendar cal = Calendar.getInstance();
						int hour = cal.getTime().getHours();
						searchVo.setMinTime(hour);
					}
					/**
					 * Call the asynctask to get the deals list.
					 */
					new DealsListAsyncTask().execute(searchVo);

				} else {
					getDealList(1, diningId, mNewIndex);
					pageCount = 1;
				}
				wheelLayout.setVisibility(View.GONE);
				if (!ValidationUtil.isNullOrEmpty(uncheckedOffers)
						&& uncheckedOffers.size() > 0
						&& pendingOffersStVisible == View.VISIBLE) {
					statusFed.setVisibility(View.GONE);
					statusPendingOffers.setVisibility(View.VISIBLE);
				} else {
					statusFed.setVisibility(View.VISIBLE);
					statusPendingOffers.setVisibility(View.GONE);
				}

			}

		});

		/*
		 * Status bar implementation for pending offers
		 */
		statusPendingOffers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// statusPendingOffers.setVisibility(vie)
				statusFed.setVisibility(View.VISIBLE);
				statusPendingOffers.setVisibility(View.GONE);
				if (uncheckedOffers.size() > 1) {
					Intent myoffersIntent = new Intent(NearMeActivity.this,
							MyOffersActivity.class);
					myoffersIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					storeSliderPosition();
					startActivity(myoffersIntent);
				} else if (uncheckedOffers.size() == 1) {
					OffersDetailsVo offersDetailsVo = uncheckedOffers.get(0);
					ArrayList<OffersDetailsVo> newOffersList = new ArrayList<OffersDetailsVo>();
					for (OffersDetailsVo OffersVo : uncheckedOffers) {
						if (!offersDetailsVo.getDealId().equals(
								OffersVo.getDealId())
								&& OffersVo.getBusinessName().equalsIgnoreCase(
										offersDetailsVo.getBusinessName()))
							newOffersList.add(OffersVo);
					}
					Bundle bundle = new Bundle();
					Intent calimedIntent = new Intent(NearMeActivity.this,
							MyoffersDetailActivity.class);
					calimedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					bundle.putParcelable("selectOffers", offersDetailsVo);
					bundle.putParcelableArrayList("OffersList", newOffersList);
					calimedIntent.putExtras(bundle);
					storeSliderPosition();
					startActivity(calimedIntent);
				}
			}
		});

		topMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Starting main menu activity
				 */
				Intent mainMenuIntent = new Intent(NearMeActivity.this,
						MainMenuActivity.class);
				mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				storeSliderPosition();
				startActivity(mainMenuIntent);
			}
		});

		/*
		 * me button onclick listener.
		 */
		meButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent meIntent = new Intent(NearMeActivity.this,
						MeActivity.class);
				meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				storeSliderPosition();
				startActivity(meIntent);
			}
		});

		/**
		 * Search button onclick listener
		 */
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent searchIntent = new Intent(NearMeActivity.this,
						SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				storeSliderPosition();
				startActivity(searchIntent);
			}
		});

		// If this page is loaded from menu page check search by values
		SharedPreferences spc = getSharedPreferences("cityandzip", 0);
		SharedPreferences.Editor editor = spc.edit();

		SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
		String homeZipCode = spc1.getString("postal", "");
		String workZipCode = spc1.getString("workZip", "");
		String alternateZipCode = spc1.getString("alternateZip", "");

		if (selectedLocation.equalsIgnoreCase("Home")) {
			// Search offers near HomeZip
			Map<String, String> locationMap = null;
			editor.putString("zipcode", homeZipCode);
			editor.commit();

			locationMap = ValidationUtil.getLatLonFromAddress(homeZipCode);
			if (!ValidationUtil.isNullOrEmpty(locationMap)) {
				Log.v("lat and lng", "lat= " + locationMap.get("lat")
						+ " lang " + locationMap.get("lng"));
				AppConstant.dev_lat = Double.valueOf(locationMap.get("lat"));
				AppConstant.dev_lang = Double.valueOf(locationMap.get("lng"));
			}else{
				AppConstant.dev_lat = 0;
				AppConstant.dev_lang =0;
			}

			List<Address> addressFromZip = null;

			Geocoder geocoder1 = new Geocoder(getBaseContext(),
					Locale.getDefault());
			try {
				if (!ValidationUtil.isNullOrEmpty(locationMap)) {
				addressFromZip = geocoder1.getFromLocation(AppConstant.dev_lat,
						AppConstant.dev_lang, 1);
				Address address1 = addressFromZip.get(0);
				String addressLine1 = address1.getAddressLine(0);
				String addressLine2 = address1.getAddressLine(1);
				String address = addressLine1 + " " + addressLine2;
				Log.v("Addressfromzip", "" + address);
				SharedPreferences citySpc1 = getSharedPreferences("cityandzip",
						0);
				SharedPreferences.Editor edit = citySpc1.edit();
				edit.putString("cityname", address);
				edit.commit();
				}
			} catch (IOException e) {
				Log.e("Exception:",
						"Exception occuerd at the time getting address list from Geo Coder.");
				e.printStackTrace();
			}
		} else if (selectedLocation.equalsIgnoreCase("Work")) {
			// Search offers near WorkZip
			Map<String, String> locationMap = null;
			editor.putString("zipcode", workZipCode);
			editor.commit();

			locationMap = ValidationUtil.getLatLonFromAddress(workZipCode);
			if (!ValidationUtil.isNullOrEmpty(locationMap)) {
				Log.v("lat and lng", "lat= " + locationMap.get("lat")
						+ " lang " + locationMap.get("lng"));
				AppConstant.dev_lat = Double.valueOf(locationMap.get("lat"));
				AppConstant.dev_lang = Double.valueOf(locationMap.get("lng"));
			}else{
				AppConstant.dev_lat = 0;
				AppConstant.dev_lang = 0;
			}

			List<Address> addressFromZip = null;
			Geocoder geocoder2 = new Geocoder(getBaseContext(),
					Locale.getDefault());
			try {
				if (!ValidationUtil.isNullOrEmpty(locationMap)) {
				addressFromZip = geocoder2.getFromLocation(AppConstant.dev_lat,
						AppConstant.dev_lang, 1);
				Address address1 = addressFromZip.get(0);
				String addressLine1 = address1.getAddressLine(0);
				String addressLine2 = address1.getAddressLine(1);
				String address = addressLine1 + " " + addressLine2;
				Log.v("Addressfromzip", "" + address);
				SharedPreferences citySpc1 = getSharedPreferences("cityandzip",
						0);
				SharedPreferences.Editor edit = citySpc1.edit();
				edit.putString("cityname", address);
				edit.commit();
				}
			} catch (IOException e) {
				Log.e("Exception:",
						"Exception occuerd at the time getting address list from Geo Coder.");
				e.printStackTrace();
			}
		} else if (selectedLocation.equalsIgnoreCase("Alternate")) {
			Map<String, String> locationMap = null;
			editor.putString("alternateZipCode", alternateZipCode);
			editor.commit();

			locationMap = ValidationUtil.getLatLonFromAddress(alternateZipCode);
			if (!ValidationUtil.isNullOrEmpty(locationMap)) {
				Log.v("lat and lng", "lat= " + locationMap.get("lat")
						+ " lang " + locationMap.get("lng"));
				AppConstant.dev_lat = Double.valueOf(locationMap.get("lat"));
				AppConstant.dev_lang = Double.valueOf(locationMap.get("lng"));
			}else{
				AppConstant.dev_lat = 0;
				AppConstant.dev_lang =0;
			}

			List<Address> addressFromZip = null;
			Geocoder geocoder2 = new Geocoder(getBaseContext(),
					Locale.getDefault());
			try {
				if (!ValidationUtil.isNullOrEmpty(locationMap)) {
				addressFromZip = geocoder2.getFromLocation(AppConstant.dev_lat,
						AppConstant.dev_lang, 1);
				Address address1 = addressFromZip.get(0);
				String addressLine1 = address1.getAddressLine(0);
				String addressLine2 = address1.getAddressLine(1);
				String address = addressLine1 + " " + addressLine2;
				Log.v("Addressfromzip", "" + address);
				SharedPreferences citySpc1 = getSharedPreferences("cityandzip",
						0);
				SharedPreferences.Editor edit = citySpc1.edit();
				edit.putString("cityname", address);
				edit.commit();
				}
			} catch (IOException e) {
				Log.e("Exception:",
						"Exception occuerd at the time getting address list from Geo Coder.");
				e.printStackTrace();
			}
		}

		application = (TangoTabBaseApplication) getApplication();
		/**
		 * get deals list from session and display
		 */

		/**
		 * Get all offers form web service and set the app notification
		 */
		if (ValidationUtil.isNullOrEmpty(fromPage))
			getOfferList();

		if (AppConstant.dev_lat == 0.0 && AppConstant.dev_lang == 0.0) {
			emptyList.setVisibility(View.GONE);
		}

		/*
		 * Bundle bundel = getIntent().getExtras(); if(bundel!=null) { frmPage =
		 * (String)bundel.getString("frmPage"); }
		 */

		/**
		 * Auto scroll down offers
		 */
		itemsList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;

				// is the bottom item visible? Load more !
				if ((lastInScreen == totalItemCount) && totalItemCount != 1
						&& totalItemCount != totalItemCount + 10
						&& totalItemCount == count) {
					pageCount++;
					getDealList(pageCount, diningId, diningStyle);
					count += 10;
				}
			}
		});

		/**
		 * set onClickListeners for Buttons(Nearby, Expiring, Recent and Map).
		 */

		map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {

				Vibrator myVib = (Vibrator) getApplicationContext()
						.getSystemService(Context.VIBRATOR_SERVICE);
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
					List<DealsDetailVo> mapDealsList = application
							.getDealsList();
					if (ValidationUtil.isNullOrEmpty(mapDealsList)) {
						SharedPreferences currentLocation = getSharedPreferences(
								"LocationDetails", 0);
						String lat = currentLocation.getString("locLat", "");
						String lang = currentLocation.getString("locLong", "");
						if (!ValidationUtil.isNullOrEmpty(lat)
								&& !ValidationUtil.isNullOrEmpty(lang)) {
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
							addressList = geocoder.getFromLocation(
									AppConstant.dev_lat, AppConstant.dev_lang,
									1);
						} catch (IOException e) {
							Log.e("Exception:",
									"Exception occuerd at the time getting address list from Geo Coder.");
							e.printStackTrace();
						}

						if (!ValidationUtil.isNullOrEmpty(addressList)) {
							Intent myOfferMapIntent = new Intent(
									getApplicationContext(),
									MappingActivity.class);
							myOfferMapIntent.putExtra("businessname",
									addressList.get(0).getAddressLine(0));
							myOfferMapIntent.putExtra("dealname", addressList
									.get(0).getAddressLine(0));
							myOfferMapIntent.putExtra("IsFromPlaceOrSearch",
									"mySettings");
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
										Intent mapIntent = new Intent(
												appContext,
												MappingActivity.class);
										/*
										 * mapIntent.putExtra("galleryPos",
										 * selectedPosition-1);
										 * mapIntent.putExtra
										 * ("diningStyle",diningStyle);
										 */
										storeSliderPosition();
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

		new MyPhilanthropyInformation().execute();

	}

	/**
	 * Get list of offers for notifications.
	 * 
	 */
	private void getOfferList() {
		SharedPreferences spc = getSharedPreferences("UserName", 0);
		String userId = spc.getString("username", "");
		String password = spc.getString("password", "");
		loginvo = new LoginVo();
		loginvo.setUserId(userId);
		loginvo.setPassword(password);
		/**
		 * Get all the offers from asynctask call
		 */
		new MyOffersListAsyncTask().execute(loginvo);

	}

	/**
	 * Retrieve all the deals from number of page count
	 * 
	 * @param pageCount
	 */
	private void getDealList(int pageCount, String diningId, int diningStyle) {
		if (pageCount == 1) {
			count = 11;
		}
		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		String distance_set = spc1.getString("distancevalue", "");
		if (ValidationUtil.isNullOrEmpty(distance_set)) {
			distance_set = "20";
		}

		SharedPreferences spc3 = getSharedPreferences("UserDetails", 0);
		userId = spc3.getString("UserId", "");
		/**
		 * get city and zip code from shared preferences.
		 */
		SharedPreferences spc = getSharedPreferences("cityandzip", 0);
		cityName = spc.getString("cityname", "");
		zipCode = spc.getString("zipcode", "");
		String lat = spc.getString("devLat", "");
		String lang = spc.getString("devLang", "");
		String s = lat + " " + lang;

		if (s.contains("E")) {
			dev_lat = Double.valueOf(lat) / 1E6;
			dev_lang = Double.valueOf(lang) / 1E6;
		}
		/**
		 * Set all the data in near me vo object
		 */
		dinningType.setText(dinningStyles[diningStyle]);
		NearMeVo nearMeVo = new NearMeVo();
		nearMeVo.setType("A");
		nearMeVo.setCityName(cityName);
		nearMeVo.setZipCode(zipCode);
		nearMeVo.setLattitude(dev_lat);
		nearMeVo.setLongittude(dev_lang);
		nearMeVo.setSetDistance(distance_set);
		nearMeVo.setUserId(userId);
		nearMeVo.setPageIndex(pageCount);
		nearMeVo.setDiningId(diningId);
		nearMeVo.setDate(selectedDate);
		nearMeVo.setAppVersion(application.getAppVerison());
		application.setNearMePageCount(pageCount);
		setTimeRange(nearMeVo, diningStyle);
		Log.v("nearMeVo is ", nearMeVo.toString());

		if (!ValidationUtil.isNull(nearMeVo)) {
			new NearListAsyncTask().execute(nearMeVo);
		}
		// dinningType.setText(dinningStyles[diningStyle-1]);
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
			return dinningStyles.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return dinningStyles[index];
		}
	}

	/**
	 * Asynctask call for get list of deals in near me activity
	 * 
	 * @author dillip.lenka
	 * 
	 */
	public class NearListAsyncTask extends
			AsyncTask<NearMeVo, Void, List<DealsDetailVo>> {
		private ProgressDialog mDialog = null;

		@Override
		protected void onPreExecute() {
			if (ValidationUtil.isNullOrEmpty(getStartedPage)) {
				mDialog = ProgressDialog.show(NearMeActivity.this,
						"Searching ", "Please wait...");
				mDialog.setCancelable(true);
			}
		}

		@Override
		protected List<DealsDetailVo> doInBackground(NearMeVo... nearMeVo) {
			List<DealsDetailVo> dealsList = null;
			try {
				NearMeService nearService = new NearMeService();
				dealsList = nearService.getListOfDeals(nearMeVo[0]);
			} catch (ConnectTimeoutException e) {
				Log.e("Exception occured",
						"Exception occured at the time of login", e);
				/**
				 * Contact to TangoTab contact support team
				 */
				Intent contactIntent = new Intent(getApplicationContext(),
						ContactSupportActvity.class);
				startActivity(contactIntent);
			} catch (Exception e) {
				Log.e("Exception occured",
						"Exception occured at the time of login", e);
			}
			return dealsList;
		}

		@Override
		protected void onPostExecute(List<DealsDetailVo> dealsList) {
			if (mDialog != null)
				mDialog.dismiss();
			if (!ValidationUtil.isNullOrEmpty(dealsList)) {
				if (islistRefreshed) {
					finalDealList.clear();
					finalDealList.addAll(dealsList);
				} else {
					List<DealsDetailVo> nearMeList = application.getDealsList();
					if (!ValidationUtil.isNullOrEmpty(nearMeList)
							&& finalDealList.size() == 0) {
						if (!ValidationUtil.isNullOrEmpty(dealsList))
							nearMeList.addAll(dealsList);
						finalDealList.addAll(nearMeList);
					} else {
						finalDealList.addAll(dealsList);
					}
					emptyList.setVisibility(View.GONE);
					itemsList.setVisibility(View.VISIBLE);
					application.setOffersList(offersList);
					Log.v("NearListAsyncTask size of the finalDealList is",
							String.valueOf(finalDealList.size()));
					application.setDealsList(finalDealList);
					AppConstant.dealsList = finalDealList;

				}
				/**
				 * Display list of deals by using adapter.
				 */
				if (selectedposition != 2 && selectedposition != 3) {
					NearMeListAdapter nearMeListAdapter = new NearMeListAdapter(
							NearMeActivity.this, finalDealList, llShowMore,
							null);
					setListAdapter(nearMeListAdapter);
				} else {
					setUpScaleOrFineAdapter();

				}
				/**
				 * Curson to be point in next 10 records.
				 */
				if (!ValidationUtil.isNullOrEmpty(finalDealList)
						&& finalDealList.size() > 10) {
					getListView().postDelayed(new Runnable() {

						@Override
						public void run() {
							itemsList.setSelection((pageCount - 1) * 10);
						}
					}, 100L);

				}
				itemsList = (ListView) getListView();
				itemsList.setCacheColorHint(Color.TRANSPARENT);
				//itemsList.removeFooterView(llShowMore);
				//itemsList.addFooterView(llShowMore);

				/*
				 * SharedPreferences preferences =
				 * getSharedPreferences("NEARSCROLL", 0); int scroll =
				 * preferences.getInt("scrollValue", 0); int top =
				 * preferences.getInt("Top", 0); if(itemsList!=null)
				 * itemsList.setSelectionFromTop(scroll, top);
				 */

				// ((PullToRefreshListView) getListView()).onRefreshComplete();
				if (islistRefreshed || pageCount == 1) {
					// setSelection(1);
					islistRefreshed = false;
				}
			} else {
				//llShowMore.setVisibility(View.GONE);
				List<DealsDetailVo> olddealsList = application.getDealsList();
				if (ValidationUtil.isNullOrEmpty(olddealsList)) {
					emptyList.setVisibility(View.VISIBLE);
					itemsList.setVisibility(View.GONE);
				} else {
					NearMeListAdapter nearMeListAdapter = new NearMeListAdapter(
							NearMeActivity.this, olddealsList, llShowMore, null);
					setListAdapter(nearMeListAdapter);
				}
				if (AppConstant.dev_lat == 0.0 && AppConstant.dev_lang == 0.0) {
					emptyList.setVisibility(View.GONE);
				}
			}

		}
	}

	/**
	 * AsyncTask call to retrieve all the offers from service using different
	 * thread
	 * 
	 * @author Dillip.Lenka
	 * 
	 */
	public class MyOffersListAsyncTask extends
			AsyncTask<LoginVo, Void, List<OffersDetailsVo>> {
		@Override
		protected void onPreExecute() {
			dinningType.setEnabled(false);
		}

		@Override
		protected List<OffersDetailsVo> doInBackground(LoginVo... loginVo) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			List<OffersDetailsVo> offersList = null;
			try {
				MyOffersService service = new MyOffersService();
				offersList = service.getOffers(pageCount, loginVo[0]);

			} catch (ConnectTimeoutException e) {
				Log.e("ConnectTimeoutException occured get list of offers", "",
						e);
				Intent contactIntent = new Intent(getApplicationContext(),
						ContactSupportActvity.class);
				startActivity(contactIntent);
			} catch (Exception e) {
				Log.e("Exception occured get list of offers", "", e);
			}
			return offersList;
		}

		@Override
		protected void onPostExecute(List<OffersDetailsVo> offersList) {
			dinningType.setEnabled(true);
			if (!ValidationUtil.isNullOrEmpty(offersList)) {
				/**
				 * Send app notifications for Expired offers
				 */
				sendAppNotification(offersList);
				
			}
			/**
			 * Validate the offers for list of offers
			 */
			if (!ValidationUtil.isNullOrEmpty(offersList))
				validateTheOffers();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * Save scroll position in shared preferences.
		 */

		SharedPreferences spreferences = getSharedPreferences(
				AppConstant.SELECTED_PREFS, 0);
		SharedPreferences.Editor editor = spreferences.edit();
		editor.putInt(AppConstant.KEY_SELECTED_ITEM,
				ga.getSelectedItemPosition());
		editor.commit();

		SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
		SharedPreferences.Editor edit = preferences.edit();
		if (itemsList != null) {
			int scroll = itemsList.getFirstVisiblePosition();
			edit.putInt("scrollValue", scroll);
			View v = itemsList.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();
			edit.putInt("Top", top);
			edit.commit();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();

		SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);

		SharedPreferences spreferences = getSharedPreferences(
				AppConstant.SELECTED_PREFS, 0);
		int scroll = preferences.getInt("scrollValue", 0);
		selectedposition = spreferences
				.getInt(AppConstant.KEY_SELECTED_ITEM, 0);
		ga.setAdapter(new NearMeGalleryAdapter(NearMeActivity.this, pics));
		ga.setSelection(selectedposition,true);
		View v=(View) ga.getItemAtPosition(ga.getSelectedItemPosition());
		ga.setSelected(true);
		ga.invalidate();
		int top = preferences.getInt("Top", 0);
		if (itemsList != null)
			itemsList.setSelectionFromTop(scroll, top);
	};

	/**
	 * Convert point to geo point and get the address details.
	 * 
	 * @param point
	 */
	public void ConvertToPoint(GeoPoint point) {
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresseses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6,
					1);
			dev_lat = addresseses.get(0).getLatitude();
			dev_lang = addresseses.get(0).getLongitude();
			AppConstant.dev_lat = dev_lat;
			AppConstant.dev_lang = dev_lang;

			Log.v("dev_lat and dev_lang ", "dev_lang = " + dev_lang
					+ "dev_lang = " + dev_lang);

			if (addresseses.size() > 0) {
				zipCode = addresseses.get(0).getPostalCode();
				cityName = addresseses.get(0).getSubAdminArea();
			}

		} catch (IOException e) {
			Log.e("Exception:",
					"Exception occuerd at the time of getting address from point",
					e);
			e.printStackTrace();
		}
	}

	/**
	 * Method will validate the offers and set the local and app notifications
	 * for expired offers.
	 * 
	 */
	private void validateTheOffers() {
		boolean isFirstTimeRun = false;
		final String PREFS_NAME = "MyPrefsFile";

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		if (settings.getBoolean("my_first_time", true)) {
			isFirstTimeRun = true;
			settings.edit().putBoolean("my_first_time", false).commit();
		}
		boolean isPackageExists = isPackageExists(AppConstant.PACKAGE_NAME);

		if (isPackageExists) {
			ApplicationDetails appDetails = null;
			try {
				appDetails = getApplicationInfo(false, AppConstant.PACKAGE_NAME);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			/**
			 * Check if application version is 1.6 or not and then implement
			 */
			if (!ValidationUtil.isNull(appDetails)) {
				String appVersion = appDetails.getVersionName();
				application.setAppVerison(appVersion);
				if (appVersion.equals(AppConstant.INSTLL_VERSION)
						&& isFirstTimeRun) {
					String installDate = appDetails.getAppInstallDate();
					// Save the date of v 1.6 installation in the app Settings
					application.setInstallDate(installDate);
					Log.v("Application install Date is ", installDate);
					if (!ValidationUtil.isNullOrEmpty(offersList)) {
						validateTheOffersAndNotify(offersList);
					}
				}
			}

		}
	}

	/**
	 * Check whether the package exists in the device or not.
	 * 
	 * @param targetPackage
	 * @return
	 */
	private boolean isPackageExists(String targetPackage) {
		Log.v("Invoking isPackageExists() method ", "targetPackage ="
				+ targetPackage);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(targetPackage,
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			Log.e("Error",
					"Error ocuured in checking whther the package exists into the device or not.");
			return false;
		}
		return true;
	}

	/**
	 * Get all the application information from package.
	 * 
	 * @param getSysPackages
	 * @return
	 * @throws NameNotFoundException
	 */
	private ApplicationDetails getApplicationInfo(boolean getSysPackages,
			String packageName) throws NameNotFoundException {
		Log.v("Invoking getApplicationInfo() method ", "getSysPackages ="
				+ getSysPackages + " packageName = " + packageName);
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		ApplicationDetails appDetails = null;
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo packInfo = packs.get(i);
			if ((!getSysPackages) && (packInfo.versionName == null)) {
				continue;
			}
			/**
			 * Check if package name is matching with application package name
			 */
			if (packInfo.packageName.equals(packageName)) {
				appDetails = new ApplicationDetails();
				appDetails.setAppName(packInfo.applicationInfo.loadLabel(
						getPackageManager()).toString());
				appDetails.setAppPackage(packInfo.packageName);
				appDetails.setVersionName(packInfo.versionName);
				appDetails.setVersionCode(String.valueOf(packInfo.versionCode));
				PackageManager pm = getApplicationContext().getPackageManager();
				try {
					// Find the installation time of the given application.
					ApplicationInfo appInfo = pm.getApplicationInfo(
							packageName, 0);
					String appFile = appInfo.sourceDir;
					long installed = new File(appFile).lastModified();
					appDetails
							.setAppInstallDate(new Date(installed).toString());
				} catch (IllegalArgumentException e) {
					Log.e("Error",
							"Error ocuured in getting application installation time.");
					e.printStackTrace();
				}
			}
		}
		Log.v("AppDetails object is ", appDetails.toString());
		return appDetails;
	}

	/**
	 * This method will validate all the offers and add notify to the offers.
	 * 
	 * @param consumerDealList
	 */
	private void validateTheOffersAndNotify(List<OffersDetailsVo> offerList) {
		Log.v("Invoking validateTheOffersAndNotify( ) method",
				"consumerDealList =" + offerList.size());
		for (OffersDetailsVo offersDetailsVo : offerList) {
			String installDate = application.getInstallDate();
			Calendar cal = Calendar.getInstance();
			Log.v("installDate is ", installDate);
			Date finalInstallDate = DateFormatUtil
					.parseGMTFormatDate(installDate);
			Log.v("finalInstallDate after parsing the install date",
					finalInstallDate.toString());

			String reserveDate = offersDetailsVo.getReserveTimeStamp();
			StringBuilder finalReserveDate = new StringBuilder();
			if (!ValidationUtil.isNullOrEmpty(reserveDate)) {
				int index = reserveDate.indexOf(" ");
				String endDate = offersDetailsVo.getEndTime();
				Log.v("ClaimDate is ", endDate);
				finalReserveDate.append(reserveDate.substring(0, index).trim())
						.append(" ").append(endDate);
				Log.v("finalClaimDate is ", finalReserveDate.toString());
			}

			// Check whether the claim date after install application date
			boolean isClaimDateAfterInstallDate = DateFormatUtil
					.isClaimDateAfterInstallDate(installDate,
							finalReserveDate.toString());
			Log.v("isClaimDateAfterInstallDate = ",
					String.valueOf(isClaimDateAfterInstallDate));

			// Get the claim date after two weeks
			Date claimDateAfterTwoWeeks = DateFormatUtil
					.dateAfterSomeTimePeriod(finalReserveDate.toString(),
							"Week", 2, "yyyy-MM-dd hh:mm aa");
			Log.v("claim Date after two weeks is ",
					claimDateAfterTwoWeeks.toString());

			// Check whether claims older than two weeks or not.
			boolean isTwoWeeksOlderClaim = (claimDateAfterTwoWeeks.after(cal
					.getTime())) ? false : true;

			Log.v("isTwoWeeksOlderClaim = ",
					String.valueOf(isTwoWeeksOlderClaim));
			Date finalEndTime = DateFormatUtil.parseIntoDifferentFormat(
					finalReserveDate.toString(), "yyyy-MM-dd hh:mm aa");

			if (isClaimDateAfterInstallDate && !isTwoWeeksOlderClaim) {
				if (finalEndTime != null) {
					String currentDate = offersDetailsVo.getCurrentDate();
					Date current = DateFormatUtil.parseIntoDifferentFormat(
							currentDate, "yyyy-MM-dd HH:mm:ss.SSSSSS");
					boolean isExpiredOffer = (finalEndTime.after(current)) ? true
							: false;
					if (isExpiredOffer) {
						Bundle bundle = new Bundle();
						bundle.putParcelable("selectOffers", offersDetailsVo);
						/**
						 * Start new activity
						 */
						Intent appNotification = new Intent(
								getApplicationContext(),
								AppNotificationActivity.class);
						appNotification.putExtras(bundle);
						startActivity(appNotification);
					}
				}
			}
			String currentDate = offersDetailsVo.getCurrentDate();
			Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,
					"yyyy-MM-dd HH:mm:ss.SSSSSS");
			Date expireDate = DateFormatUtil.dateAfterSomeTimePeriod(
					finalReserveDate.toString(), "hour", 1,
					"yyyy-MM-dd hh:mm aa");
			boolean notYetExpired = (expireDate.before(current)) ? true : false;
			if (notYetExpired && !isTwoWeeksOlderClaim) {
				Log.v("Offer to be notify ", "Please notify the given offer");
				sendLocalNotification(offersDetailsVo);
			}

		}
	}

	/**
	 * Send app notifications for list of Expired offers.
	 * 
	 * @param offersList
	 */
	private void sendAppNotification(List<OffersDetailsVo> offersList) {
		if (ValidationUtil.isNullOrEmpty(offersList))
			return;
		uncheckedOffers = new ArrayList<OffersDetailsVo>();
		Log.v("Invoking is ", "consumerdealsList = " + offersList.size());
		for (OffersDetailsVo offersDetailsVo : offersList) {
			int isCheckin = 0;
			if (!ValidationUtil.isNullOrEmpty(offersDetailsVo
					.getIsConsumerShownUp())) {
				isCheckin = Integer.parseInt(offersDetailsVo
						.getIsConsumerShownUp());
			}
			Log.v("isCheckin = ", String.valueOf(isCheckin));
			/**
			 * If the offer neither manual or auto check in
			 */
			if (isCheckin == 0) {
				String reserveDate = offersDetailsVo.getReserveTimeStamp();
				StringBuilder finalClaimDate = new StringBuilder();
				if (!ValidationUtil.isNullOrEmpty(reserveDate)) {
					int index = reserveDate.indexOf(" ");
					String claimDate = offersDetailsVo.getEndTime();
					Log.v("ClaimDate is ", claimDate);
					finalClaimDate
							.append(reserveDate.substring(0, index).trim())
							.append(" ").append(claimDate);
					Log.v("finalClaimDate is ", finalClaimDate.toString());
				}

				Date finalEndTime = DateFormatUtil.parseIntoDifferentFormat(
						finalClaimDate.toString(), "yyyy-MM-dd hh:mm aa");
				if (finalEndTime != null) {
					// Check whether the offer expired or not.
					String currentDate = offersDetailsVo.getCurrentDate();
					Date current = DateFormatUtil.parseIntoDifferentFormat(
							currentDate, "yyyy-MM-dd HH:mm:ss.SSSSSS");
					boolean isExpiredOffer = (current.after(finalEndTime)) ? true
							: false;

					Log.v("isExpiredOffer = ", String.valueOf(isExpiredOffer));

					if (isExpiredOffer) {
						// Toast.makeText(getApplicationContext(),
						// "Offers to validate APP Notification for "+offersDetailsVo.getBusinessName(),
						// Toast.LENGTH_LONG).show();
						Bundle bundle = new Bundle();
						bundle.putParcelable("selectOffers", offersDetailsVo);
						Intent appNotification = new Intent(
								getApplicationContext(),
								AppNotificationActivity.class);
						appNotification.putExtras(bundle);
						startActivity(appNotification);
						/**
						 * Remove the local notification
						 */
						int dealId = 0;
						if (!ValidationUtil.isNullOrEmpty(offersDetailsVo
								.getDealId()))
							dealId = Integer.parseInt(offersDetailsVo
									.getDealId());

						Log.v("dealId for remove local Notification ",
								String.valueOf(dealId));

						Intent alarmIntent = new Intent(
								AppConstant.ALARM_ACTION_NAME);
						PendingIntent pendingIntent = PendingIntent
								.getBroadcast(getApplicationContext(), dealId,
										alarmIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
						alarmManager.cancel(pendingIntent);
						pendingIntent.cancel();

					}
				}
			} else {
				uncheckedOffers.add(offersDetailsVo);
				Log.v("Don't send local notification already checked in",
						String.valueOf(isCheckin));
			}
		}

		if (!ValidationUtil
				.isNullOrEmpty(getIntent().getStringExtra("frmPage"))
				&& getIntent().getStringExtra("frmPage").equals("splashScreen")) {
			if (!ValidationUtil.isNullOrEmpty(uncheckedOffers)
					&& uncheckedOffers.size() > 0) {
				statusFed.setVisibility(View.GONE);
				statusPendingOffers.setVisibility(View.VISIBLE);
				SharedPreferences sharedUserDetails = getSharedPreferences(
						"UserDetails", 0);

				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(sharedUserDetails.getString("firstName", "")
						.substring(0, 1).toUpperCase());
				strBuilder.append(
						sharedUserDetails.getString("firstName", "").substring(
								1)).append(", ");
				strBuilder.append("open your pending offer here");

				Animation pushleft = AnimationUtils.loadAnimation(this,
						R.anim.push_left_in);
				pushleft.setDuration(1000);
				statusPendingOffersLabel.startAnimation(pushleft);
				statusPendingOffersLabel.setText(strBuilder.toString());
			}
		} else {
			statusFed.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Send the local notification to the given offers.
	 * 
	 * @param offersDetailsVo
	 */
	private void sendLocalNotification(OffersDetailsVo offersDetailsVo) {
		Log.v("Invoking sendLocalNotification() method ", "claimDate ="
				+ offersDetailsVo.getReserveTimeStamp() + "businessName ="
				+ offersDetailsVo.getBusinessName());
		int isCheckin = 0;
		if (!ValidationUtil.isNullOrEmpty(offersDetailsVo
				.getIsConsumerShownUp())) {
			isCheckin = Integer
					.parseInt(offersDetailsVo.getIsConsumerShownUp());
		}
		Log.v("isCheckin = ", String.valueOf(isCheckin));

		if (isCheckin == 0) {
			/**
			 * First remove the local notification for the offer
			 */
			removeAlarmForNotification(offersDetailsVo);

			String reserve = offersDetailsVo.getReserveTimeStamp();
			int index = reserve.indexOf(" ");
			String claimTime = offersDetailsVo.getEndTime();
			Log.v("claimTime is ", claimTime);

			StringBuilder finalClaimDate = new StringBuilder();
			finalClaimDate.append(reserve.substring(0, index).trim())
					.append(" ").append(claimTime);
			Log.v("finalClaimDate is ", finalClaimDate.toString());
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm aa");
			Date reserveDate = null;
			try {
				reserveDate = format.parse(finalClaimDate.toString());
			} catch (ParseException e) {
				Log.e("Error", "Error occured in parsing the calim date.");
				e.printStackTrace();
			}
			if (reserveDate != null) {
				Calendar alarmCal = Calendar.getInstance();
				alarmCal.setTime(reserveDate);
				Long reserveTime = null;
				long diffInMilisecond = Math.abs(alarmCal.getTimeInMillis()
						- System.currentTimeMillis());
				if (alarmCal.getTimeInMillis() > System.currentTimeMillis()) {
					reserveTime = diffInMilisecond;

				} else {
					reserveTime = -diffInMilisecond;
				}

				setAlaramForNotification(reserveTime, offersDetailsVo);
				/*
				 * Calendar alarmCal = Calendar.getInstance();
				 * alarmCal.setTime(reserveDate);
				 * 
				 * Long reserveTime = null; long diffInMilisecond
				 * =Math.abs(alarmCal
				 * .getTimeInMillis()-System.currentTimeMillis()); reserveTime =
				 * (alarmCal.getTimeInMillis()>System.currentTimeMillis())?
				 * diffInMilisecond:-diffInMilisecond;
				 * setAlaramForNotification(reserveTime, offersDetailsVo);
				 */
			}
		}

	}

	/**
	 * Set alarm for individual offers.
	 * 
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void setAlaramForNotification(long alramTime,
			OffersDetailsVo offersDetailsVo) {
		Log.v("Invoking setAlaramForNotification() method ",
				"alramTime =" + alramTime + " businessName ="
						+ offersDetailsVo.getBusinessName() + " claimDate = "
						+ offersDetailsVo.getReserveTimeStamp());
		/*
		 * Create an Alaram intent
		 */
		int dealId = 0;
		if (!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId())) {
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
		}

		Bundle bundle = new Bundle();
		bundle.putParcelable("selectOffers", offersDetailsVo);
		Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);
		alarmIntent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), dealId, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (alramTime), pendingIntent);

	}

	/**
	 * Remove the alarm for the offers .
	 * 
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void removeAlarmForNotification(OffersDetailsVo offersDetailsVo) {
		Log.v("Invoking removeAlarmForNotification() method ",
				" businessName =" + offersDetailsVo.getBusinessName()
						+ " claimDate = "
						+ offersDetailsVo.getReserveTimeStamp());
		int dealId = 0;
		if (!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
		Log.v("dealId for remove alarm ", String.valueOf(dealId));
		Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), dealId, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
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

		return (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable() && conMgr
				.getActiveNetworkInfo().isConnected()) ? true : false;

	}

	/**
	 * Dialog message display.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(
					NearMeActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage("No offers are found to display on map");
			ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab.create();
		case 16:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(
					NearMeActivity.this);
			ab2.setTitle("Location Services Denied");
			ab2.setMessage("Your device is configured to deny Location Services to TangoTab. Some features of TangoTab require these services to work. Please enable Location Services in the Settings App to continue.");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab2.create();

		case 17:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(
					NearMeActivity.this);
			ab3.setTitle("Location Services Denied");
			ab3.setMessage("Your account is inactive, Please contact TangoTab Administrator.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab3.create();
		case 10:
			AlertDialog.Builder ab10 = new AlertDialog.Builder(
					NearMeActivity.this);
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

	/*
	 * ==========================================================================
	 * ======================================== Geo Informaation of the Device
	 * ==
	 * ========================================================================
	 * ========================================
	 */
	/**
	 * Initalizes the Location Manager and attaches the LocationUpdates Listener
	 * for the device current Lat Lng values
	 */
	private void initalizeLocationManagerService() {
		/*
		 * GPS Configuration
		 */
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 10000L, 500.0f, this);

		// Initialize the location fields
		if (!ValidationUtil.isNull(location)) {
			Log.i("GEO LOCATION", "Provider " + provider
					+ " has been selected.");
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			Log.i("GEO LOCATION DETAILS", "LAT and Long " + lat + "  " + lng);
			// Push the Lat Lng values into Global Execution Context
			AppConstant.dev_lat = lat;
			AppConstant.dev_lang = lng;
			SharedPreferences currentLocation = getSharedPreferences(
					"LocationDetails", 0);
			SharedPreferences.Editor edit = currentLocation.edit();
			edit.putString("locLat", String.valueOf(lat));
			edit.putString("locLong", String.valueOf(lng));
			edit.commit();
		} else {
			Log.i("GEO LOCATION", "Provider is not available");
			SharedPreferences currentLocation = getSharedPreferences(
					"LocationDetails", 0);
			SharedPreferences.Editor edit = currentLocation.edit();
			edit.putString("locLat", "0.0");
			edit.putString("locLong", "0.0");
			edit.commit();
			AppConstant.dev_lat = 0.0;
			AppConstant.dev_lang = 0.0;
		}
	}

	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		Log.i("LocationChanged", "Lat = " + lat + "   : Lng = " + lng);

		// Push the Lat Lng values into Global Execution Context
		AppConstant.dev_lat = lat;
		AppConstant.dev_lang = lng;

	}

	public void onProviderDisabled(String arg0) {
		Log.i("LocationProvider", "[" + arg0 + "] has been Disabled!");
	}

	public void onProviderEnabled(String arg0) {
		Log.i("LocationProvider", "[" + arg0 + "] has been Enabled!");
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	class MyPhilanthropyInformation extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			MyPhilanthropyService myPhilanthropyService = new MyPhilanthropyService();
			Map<String, String> response = new HashMap<String, String>();
			try {
				SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
				String userId = spc1.getString("UserId", "");
				response = myPhilanthropyService.getMyPhilanthropy(userId);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("mePhil", response.get("me"));
				edits.putString("friendsPhil", response.get("friends"));
				edits.putString("tangotabPhil", response.get("tangotab"));
				edits.putString("networkPhil", response.get("potential"));
				edits.commit();

			} catch (TangoTabException e) {
				Log.e("Exception ",
						"Exception occured at SahreMyPhilanthropy async task",
						e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			SharedPreferences sharedUserDetails = getSharedPreferences(
					"UserDetails", 0);

			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(sharedUserDetails.getString("firstName", "")
					.substring(0, 1).toUpperCase());
			strBuilder.append(
					sharedUserDetails.getString("firstName", "").substring(1))
					.append(", ");
			strBuilder.append("you've fed ");
			strBuilder.append(sharedUserDetails.getString("mePhil", "0"));
			strBuilder.append(" people in need");
			statusFedLabel.setText(strBuilder.toString());
		}

	}

	/**
	 * Get list of Deals from the search criteria.
	 * 
	 * @author dillip.lenka
	 * 
	 */
	public class DealsListAsyncTask extends
			AsyncTask<SearchVo, Void, List<DealsDetailVo>> {
		private ProgressDialog mDialog = null;

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(NearMeActivity.this, "Searching ",
					"Please wait...");
			mDialog.setCancelable(true);
		}

		@Override
		protected List<DealsDetailVo> doInBackground(SearchVo... searchVo) {
			List<DealsDetailVo> dealsList = null;
			try {
				SearchService service = new SearchService();
				dealsList = service.getSearchList(searchVo[0]);

			} catch (ConnectTimeoutException e) {
				dealsList = null;
				Log.e("", "", e);
				Intent contactIntent = new Intent(getApplicationContext(),
						ContactSupportActvity.class);
				startActivity(contactIntent);
			} catch (Exception e) {
				dealsList = null;
				Log.e("", "", e);
			}
			return dealsList;
		}

		@Override
		protected void onPostExecute(List<DealsDetailVo> dealsList) {
			try {
				mDialog.dismiss();
			} catch (Exception e) {
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}

			if (!ValidationUtil.isNullOrEmpty(dealsList))
			{
				itemsList = getListView();
				itemsList.setCacheColorHint(Color.TRANSPARENT);
				emptyList.setVisibility(View.GONE);
				/*
				 * List<DealsDetailVo> oldDealList =
				 * application.getSearchList(); if
				 * (!ValidationUtil.isNullOrEmpty(oldDealList) &&
				 * finalDealList.size() == 0) { oldDealList.addAll(dealsList);
				 * finalDealList.addAll(oldDealList); } else {
				 * finalDealList.addAll(dealsList); }
				 * application.setSearchList(finalDealList);
				 * Log.v("DealsListAsyncTask size of the finalDealList is",
				 * String.valueOf(finalDealList.size()));
				 */
				if (selectedposition != 2 && selectedposition != 3) 
				{
					/**
					 * Display the list of deal using adapter
					 */
					application.setDealsList(dealsList);
					SearchListAdapter searchListAdapter = new SearchListAdapter(NearMeActivity.this, dealsList, true, selectedposition,country);
					setListAdapter(searchListAdapter);
				}
				else 
				{
					application.getDealsList().clear();
					if(!ValidationUtil.isNullOrEmpty(finalDealList))
						{
						finalDealList.clear();
						finalDealList.addAll(dealsList);
						}
					else
					{
						finalDealList.addAll(dealsList);	
					}
					
					application.setDealsList(finalDealList);					
					setUpScaleOrFineAdapter();

				}
				

				/*
				 * if (!ValidationUtil.isNullOrEmpty(finalDealList) &&
				 * finalDealList.size() > 10) { getListView().postDelayed(new
				 * Runnable() {
				 * 
				 * @Override public void run() {
				 * itemsList.setSelection((pageCount - 1) * 10); } }, 100L);
				 * 
				 * }
				 */
				
			} else {
				// if (finalDealList.size() == 0)
				emptyList.setVisibility(View.VISIBLE);
				/*
				 * else {
				 *//**
				 * Display the list of deal using adapter
				 */
				/*
				 * SearchListAdapter searchListAdapter = new
				 * SearchListAdapter(NearMeActivity.this, finalDealList,
				 * true,llShowMore, country);
				 * 
				 * if (!ValidationUtil.isNullOrEmpty(finalDealList) &&
				 * finalDealList.size() > 10) { getListView().postDelayed(new
				 * Runnable() {
				 * 
				 * @Override public void run() {
				 * itemsList.setSelection((pageCount - 1) * 10); } }, 100L);
				 * 
				 * } itemsList = getListView();
				 * itemsList.setCacheColorHint(Color.TRANSPARENT);
				 * //itemsList.removeFooterView(llShowMore);
				 * //itemsList.addFooterView(llShowMore);
				 * emptyList.setVisibility(View.GONE);
				 * setListAdapter(searchListAdapter); }
				 */
			}

		}
	}

	/**
	 * Set time range according to dining Id
	 * 
	 * @param nearMeVo
	 * @param position
	 */
	private void setTimeRange(NearMeVo nearMeVo, int position) {
		switch (position) {
		case 0:
			nearMeVo.setMinRange(0);
			nearMeVo.setMaxRange(24);
			break;
		case 1:
			nearMeVo.setMinRange(04);
			nearMeVo.setMaxRange(11);
			break;
		case 2:
			nearMeVo.setMinRange(11);
			nearMeVo.setMaxRange(13);
			break;
		case 3:
			nearMeVo.setMinRange(13);
			nearMeVo.setMaxRange(17);
			break;
		case 4:
			nearMeVo.setMinRange(17);
			nearMeVo.setMaxRange(21);
			break;
		case 5:
			nearMeVo.setMinRange(21);
			nearMeVo.setMaxRange(04);
			break;
		default:
			break;
		}
	}

	public class MyGestureDetector extends SimpleOnGestureListener {

		// Detect a single-click and call my own handler.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			ListView lv = getListView();
			int pos = lv.pointToPosition((int) e.getX(), (int) e.getY());
			myOnItemClick(pos);
			return false;
		}

		/*@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// Log.e("on fling","called");
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				onRTLFling();
			} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				onLTRFling();
			}
			return false;
		}
*/
		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return true;
		}
	}

	private void myOnItemClick(int position) {

		try {
			Log.e("", "" + position + "," + application.getDealsList().size());
			ArrayList<DealsDetailVo> businessList = new ArrayList<DealsDetailVo>();
			DealsDetailVo dealsDetailVo = application.getDealsList().get(
					position);
			// businessList.addAll(application.getDealsList());
			for (DealsDetailVo dealsVo : application.getDealsList()) {
				if (dealsDetailVo.businessName.equals(dealsVo.businessName)) {
					businessList.add(dealsVo);
				}

			}

			Intent calimedIntent = new Intent(NearMeActivity.this,ClaimOfferActivity.class);
			calimedIntent.putExtra("from", "nearme");
			calimedIntent.putExtra("selectDeal", dealsDetailVo);
			calimedIntent.putExtra("businessList", businessList);
			calimedIntent.putExtra("country", "");
			calimedIntent.putExtra("selectedPosition", selectedposition);
			startActivity(calimedIntent);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onLTRFling() {
		if (diningStyle != 1) {
			diningStyle--;
			application.getDealsList().clear();
			finalDealList.clear();
			getDealList(1, diningId, diningStyle);
			pageCount = 1;
		}
		Log.e("swipe", "ltf");
	}

	private void onRTLFling() {
		if (diningStyle < 5) {
			diningStyle++;
			application.getDealsList().clear();
			finalDealList.clear();
			getDealList(1, diningId, diningStyle);
			pageCount = 1;
		}
		Log.e("swipe", "rtl");

	}

	/**
	 * Different list adapter for UpScale
	 */
	private void setUpScaleOrFineAdapter() {
		/**
		 * NearMeListAdapter nearMeListAdapter= new
		 * NearMeListAdapter(NearMeActivity
		 * .this,finalDealList,llShowMore,country);
		 * setListAdapter(nearMeListAdapter);
		 */
		NearMeeUpScaleAdapter nearMeListAdapter = new NearMeeUpScaleAdapter(NearMeActivity.this, finalDealList, llShowMore);

		setListAdapter(nearMeListAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			if (requestCode == AppConstant.GET_DATE_REQUEST_CODE) {
				StringBuilder date = new StringBuilder();
				int year = Integer.parseInt(data.getStringExtra("year"));
				int month = Integer.parseInt(data.getStringExtra("month"));
				int day = Integer.parseInt(data.getStringExtra("day"));
				date.append(year).append("-").append(month).append("-")
						.append(day);
				selectedDate = date.toString();

				String selectText = getTextFromDate(year, month, day);

				SharedPreferences datePreferences = getSharedPreferences(
						"NearMeDate", 0);
				SharedPreferences.Editor dateEdit = datePreferences.edit();
				dateEdit.putString("selectText", selectText);
				dateEdit.putInt("year", year);
				dateEdit.putInt("month", month - 1);
				dateEdit.putInt("day", day);
				dateEdit.putString("selectedDate", selectedDate);
				dateEdit.commit();

				currentDate.setText(selectText);

				getDealList(1, diningId, diningStyle);
				pageCount = 1;
			}
		}
	}

	private String getTextFromDate(int year, int month, int day) {
		String result = null;
		Log.v("selectedDate is", selectedDate);

		Calendar selectCal = Calendar.getInstance();
		selectCal.set(Calendar.YEAR, year);
		selectCal.set(Calendar.MONTH, month - 1);
		selectCal.set(Calendar.DAY_OF_MONTH, day);
		selectCal.set(Calendar.HOUR_OF_DAY, 0);
		selectCal.set(Calendar.MINUTE, 0);
		selectCal.set(Calendar.SECOND, 0);
		selectCal.set(Calendar.MILLISECOND, 0);
		Date selectDate = selectCal.getTime();

		Calendar today = Calendar.getInstance();
		today.setTime(today.getTime());
		Date current = today.getTime();

		today.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrowDate = today.getTime();

		String dateSelect = new SimpleDateFormat("MMM d yyyy ")
				.format(selectDate);
		String todaysDate = new SimpleDateFormat("MMM d yyyy ").format(current);
		String tommorwsDate = new SimpleDateFormat("MMM d yyyy ")
				.format(tomorrowDate);

		if (dateSelect.equalsIgnoreCase(todaysDate))
			result = "Today";
		else if (dateSelect.equalsIgnoreCase(tommorwsDate))
			result = "Tomorrow";
		else {
			result = dateSelect;
		}
		return result;
	}

	private void storeSliderPosition() {

		SharedPreferences datePreferences = getSharedPreferences("NearMeDate",
				0);
		Editor edit = datePreferences.edit();
		edit.putInt("slidingPos", selectedposition - 1);
		edit.putInt("diningStyle", mNewIndex);
		edit.commit();

	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			Intent mainMenuIntent = new Intent(NearMeActivity.this,
					MainMenuActivity.class);
			mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainMenuIntent);
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			Intent searchIntent = new Intent(NearMeActivity.this,
					SearchActivity.class);
			searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(searchIntent);
			return true;
		}

		return super.onKeyDown(keycode, e);
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

}