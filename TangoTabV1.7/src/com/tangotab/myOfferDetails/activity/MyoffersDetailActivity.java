package com.tangotab.myOfferDetails.activity;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import com.nostra13.socialsharing.facebook.FacebookFacade;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nostra13.socialsharing.common.AuthListener;
import com.nostra13.socialsharing.common.LogoutListener;
import com.nostra13.socialsharing.common.PostListener;
import com.nostra13.socialsharing.facebook.FacebookEvents;
import com.tangotab.R;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.contactSupport.activity.ContactSupportActvity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.constant.Constants;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.activity.MapPointActivity;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.myOfferDetails.service.MyOffersDetailService;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.myOffers.service.MyOffersService;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
import com.tangotab.twitter.util.TwitterUtils;

/**
 * Retrieve deal detail information from the selected offers and do auto or Manual checkin to that offer
 * 
 * <br> Class :MyoffersDetailActivity
 * <br> layout :myofferdetails.xml
 * 
 * @author dillip.lenka
 *
 */
public class MyoffersDetailActivity extends FacebookActivity implements OnClickListener,Handler.Callback
{
	private static final int Message_Id = 0;
	private static final int Reser_comp = 1;
	private static final int TWITTER_ID=40;
	private static final int TWITTER_FAILED=77;
	private static final int TWITTER_POST_FAILED=78;
	private static final int FACEBOOK_ID=50;
	private static final int TWEET_SENT=35;
	private static final int exp_Date = 2;
	private static final int not_valid = 111;
	FacebookFacade facebookFacade;
	private OffersDetailsVo offersDetailsVo;
	private ProgressDialog mDialog;
	private ProgressDialog mlocDialog;
	public static boolean isAutoCheckIn =false;
	private boolean isLocationAvailable=false;
	private float distanceInmiles;
	private String locationProvider=null;
	private String message;
	public Handler postHandler;
	LocationManager locationManager;
	private Vibrator myVib;
	private boolean isTimeToAutoCheck;
	private boolean isValidOffer;
	private boolean isExpiredOffer;
	private boolean isTodaysOffer;
	private Vibrator vibrator;
	private int autoCheckInCount;
	private TextView restPhone;
	private LinearLayout restPhoneLayout;
	private LinearLayout getDirection;
	private TextView step,dealRestriction,dealDate,confirmationCode,dealTime;
	private TextView confrmBoxMessage,businessName,dealName,dealNameBold;
	private LinearLayout addToCalender;
	private Button mBtnShareTwitter,facebookShare;
	private SharedPreferences prefs;
	public TangoTabBaseApplication application;
	ArrayList<OffersDetailsVo>  newOffersList;
	//private Button mBtnNext,mBtnBack;
	private int mSelectedIndex;
	private ImageView mydealImage ;
	private String reserveStartTime;
	private String reserveEndTime;
	private int position;
	boolean twitter=false;
	Handler myhand,twitterHandler;
	final String TAG = getClass().getName();
	private OAuthConsumer consumer; 
	private OAuthProvider provider;
	boolean pageFinish=false;
	Dialog dialog;
	ProgressDialog progressDialog;
	private GoogleAnalyticsTracker tracker;
	
	private SharedPreferences twitterPreferences;
	private SharedPreferences facebookPreferences;
	
	private SharedPreferences.Editor twitterEditor;
	private SharedPreferences.Editor facebookEditor;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myofferdetails);
		overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
		Bundle bundle = getIntent().getExtras();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		facebookShare = (Button) findViewById(R.id.btnFacebookShare);
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
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.DEALS_DETAIL_PAGE);
		tracker.trackEvent("CheckIn", "TrackEvent", "checkin", 1);
		
		application = (TangoTabBaseApplication) getApplication();
		twitterHandler=new Handler(this);
		if(bundle!=null)
		{
			offersDetailsVo =(OffersDetailsVo)bundle.get("selectOffers");	
			newOffersList=bundle.getParcelableArrayList("OffersList");
		}
		Button map = (Button) findViewById(R.id.topMapMenuButton);
		mBtnShareTwitter=(Button)findViewById(R.id.btnTweeterShare);
		mBtnShareTwitter.setOnClickListener(this);
		/*mBtnNext=(Button)findViewById(R.id.btnNext);
		mBtnNext.setOnClickListener(this);
		mBtnBack=(Button)findViewById(R.id.btnBack);
		mBtnBack.setOnClickListener(this);*/
		// Refer to TextView from Layout
		 businessName = (TextView) findViewById(R.id.mybusinessName);
		 dealName = (TextView) findViewById(R.id.mydealname);
		 dealNameBold = (TextView) findViewById(R.id.dealnamebold);
		getDirection = (LinearLayout) findViewById(R.id.getDirectionsLayout);
		restPhone = (TextView) findViewById(R.id.restPhone);
		restPhoneLayout =(LinearLayout) findViewById(R.id.restPhoneLayout);
		//TextView cusineType = (TextView) findViewById(R.id.mydealcusinetype);
		//TextView dealDescription = (TextView) findViewById(R.id.mydealdescription);
		 dealRestriction = (TextView) findViewById(R.id.mydealrestrictions);
		 dealDate = (TextView) findViewById(R.id.mydealdate);
		 dealTime = (TextView) findViewById(R.id.mydealtime);
		 addToCalender =(LinearLayout)findViewById(R.id.addToCalendarLayout);
		 confirmationCode = (TextView) findViewById(R.id.confirmationCode);
		step = (TextView) findViewById(R.id.step);
		confrmBoxMessage = (TextView) findViewById(R.id.message);
		Button cancelOfferBtn = (Button)findViewById(R.id.btncanceloffer);
		step.setShadowLayer(1, -1, 1, Color.BLACK);
		confrmBoxMessage.setShadowLayer(1, -1, 1, Color.BLACK);
		confirmationCode.setShadowLayer(1, -1, 1, Color.BLACK);
		
		//Phone number onclick listener
		restPhoneLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent call = new Intent(Intent.ACTION_DIAL);
				call.setData(Uri.parse("tel:" + restPhone.getText()));
				startActivity(call);
			}
		});
		
		/**
		 * Add event to Calendar functionality
		 */
		
		//Deal date onclick listener
		addToCalender.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				   Intent calIntent = new Intent(Intent.ACTION_EDIT);
				   calIntent.setType("vnd.android.cursor.item/event");
				   
				   //calIntent.putExtra("calendar_id", m_selectedCalendarId);  //this doesn't work
				   calIntent.putExtra("title", "TangoTab at "+offersDetailsVo.getBusinessName());
				   calIntent.putExtra("description", offersDetailsVo.getDealDescription());
				   if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getAddress()))
				   {
					   try
					   {
						   String[] splitAdd = offersDetailsVo.getAddress().split(","); 
						   if(splitAdd!=null)
						   {
							   StringBuilder address = new StringBuilder();
							   for(int count=0;count<splitAdd.length-1;count++)
							   {
								   address.append(splitAdd[count]).append(", ");
							   }
							   address.append(splitAdd[splitAdd.length-1]);
							   calIntent.putExtra("eventLocation", address.toString());
						   }
						   else
						   {
							   calIntent.putExtra("eventLocation", offersDetailsVo.getAddress()); 
						   }
					   }catch(StringIndexOutOfBoundsException e)
					   {
						   Log.e("Exception:", "StringIndexOutOfBoundsException occured at the time of splitting the address",e);
					   }
				   }
				   Date beginTime=DateFormatUtil.parseIntoDifferentFormat(reserveStartTime, "yyyy-MM-dd hh:mm aa");
				   Date endTime=DateFormatUtil.parseIntoDifferentFormat(reserveEndTime, "yyyy-MM-dd hh:mm aa");
				   Calendar cal =Calendar.getInstance();
				   cal.setTime(beginTime);
				   long beginTimeInMillis =cal.getTimeInMillis();
				   calIntent.putExtra("beginTime", beginTimeInMillis);
				   cal.setTime(endTime);
				   long endTimeInMillis =cal.getTimeInMillis();
				   calIntent.putExtra("endTime", endTimeInMillis);
				   //calIntent.putExtra("allDay", 0);
				   //status: 0~ tentative; 1~ confirmed; 2~ canceled
				   calIntent.putExtra("eventStatus", 1);
				   //0~ default; 1~ confidential; 2~ private; 3~ public
				   calIntent.putExtra("visibility", 0);
				   //0~ opaque, no timing conflict is allowed; 1~ transparency, allow overlap of scheduling
				   calIntent.putExtra("transparency", 0);
				   //0~ false; 1~ true
				   calIntent.putExtra("hasAlarm", 1);

				   try {
				       		startActivity(calIntent);
				   		} catch (Exception e) {
				   			Toast.makeText(getApplicationContext(), "Sorry, no compatible calendar is found!", Toast.LENGTH_LONG).show();
				   	}
				
			}
		});
		
		//Get Direction onclick listener
				getDirection.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						if(checkInternetConnection())
						{
							Bundle bundle = new Bundle();
							Intent myOfferMapIntent = new Intent(getApplicationContext(), MapPointActivity.class);
							bundle.putParcelable("selectOffers", offersDetailsVo);
							myOfferMapIntent.putExtra("businessname", offersDetailsVo.getBusinessName());
							myOfferMapIntent.putExtra("dealname", offersDetailsVo.getDealName());
							myOfferMapIntent.putExtra("address", offersDetailsVo.getAddress());
							myOfferMapIntent.putExtra("IsFromPlaceOrSearch", "FromPlace");
							myOfferMapIntent.putExtra("from", "myoffer");
							myOfferMapIntent.putExtra("fromPage", "myOfferDetail");
							myOfferMapIntent.putExtras(bundle);
							startActivity(myOfferMapIntent);
						}else
							 showDialog(10);	
					}
				});
				
				/**		
				 * Cancel offer on click listener.
				 */
				cancelOfferBtn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						if(checkInternetConnection())
						{
							String isCheckin = offersDetailsVo.getIsConsumerShownUp();
							if(!ValidationUtil.isNullOrEmpty(isCheckin))
							{
								int checkIn = Integer.parseInt(isCheckin);
								if(checkIn==0)
								{
									 showDialog(50);										
								}
								else
								{
									showDialog(21);
								}
							}
						}else
							 showDialog(10);	
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
		/**
		 * Top Bar functionality 	
		 */
		Button topMenuBtn = (Button) findViewById(R.id.topMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		Button topSearchBtn = (Button) findViewById(R.id.topSearchMenuButton);
		
		
		//menu button onclick listener.
				topMenuBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent meIntent=new Intent(MyoffersDetailActivity.this, MainMenuActivity.class);
						meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(meIntent);
					}
				});
					
		//me button onclick listener.
				meButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent meIntent=new Intent(MyoffersDetailActivity.this, MeActivity.class);
						meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(meIntent);
					}
				});		
		
		// near me button onclick listener		 
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent=new Intent(MyoffersDetailActivity.this, NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});
		
		//Top Search button onclick listener.
		topSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(MyoffersDetailActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});
				
		// Refer to ImageView from Layout
		 mydealImage = (ImageView) findViewById(R.id.mydealimage);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		


		/**
		 * Map button on click listener
		 */
		map.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				myVib.vibrate(50);
				if(checkInternetConnection()){
				Intent myOfferMapIntent = new Intent(getApplicationContext(), MapPointActivity.class);
				myOfferMapIntent.putExtra("businessname", offersDetailsVo.getBusinessName());
				myOfferMapIntent.putExtra("dealname", offersDetailsVo.getDealName());
				myOfferMapIntent.putExtra("address", offersDetailsVo.getAddress());
				myOfferMapIntent.putExtra("IsFromPlaceOrSearch", "FromPlace");
				myOfferMapIntent.putExtra("from", "myoffer");
				startActivity(myOfferMapIntent);
				}else
					 showDialog(10);				
			}
		});
		
		/*
		 *  Code for facebook sharing
		 */
		

		final Session session = Session.getActiveSession();
        if (!ValidationUtil.isNull(session) && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        
		
		/*SharedPreferences sharePref = getSharedPreferences("UserDetails", 0);
		boolean isFaceBookOn=sharePref.getBoolean("isFacebookOn", false);
		facebookShare.setTag(isFaceBookOn);
		facebookShare.setBackgroundResource(R.drawable.facebookselector);
		facebookShare.setApplicationId(AppConstant.APP_ID);
        List<String> permissions = new ArrayList<String>();
        			 permissions.add("publish_stream");
        			 permissions.add("publish_checkins");
        			 facebookShare.setPublishPermissions(permissions);
		
		*//**
         * Face book user info changed call back added             
         *//*
		 facebookShare.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback()
       	{
			
			@Override
			public void onUserInfoFetched(GraphUser user) {
				
				if(user != null){
					postOfferToFacebook();
				}
			}
		});*/
		 
		
			if(!ValidationUtil.isNull(offersDetailsVo))
			{
				
				/*if(newOffersList.size()==1)
					mBtnNext.setVisibility(View.GONE);
				else
				{
					for(int count=0;count<newOffersList.size();count++)
					{
						if(offersDetailsVo.getDealId().equals(newOffersList.get(count).getDealId()))
						{
							position = count;
							mSelectedIndex=position;
							
						}
					}
				}
				if(mSelectedIndex == newOffersList.size()-1)
					mBtnNext.setVisibility(View.GONE);*/
				/**
				 * Set all the field informations
				 */
				
				 //Set all offers related data
				setData(offersDetailsVo);
			}	
		
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
		
	}
	
	private void setData(OffersDetailsVo offerDetails){
		Date startDate =null;
		String timestamp = offerDetails.getReserveTimeStamp();
		String sdate = timestamp.substring(0, 11);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = formatter.parse(sdate);
		} catch (ParseException e)
		{
			Log.e("Error ", e.getLocalizedMessage());
			e.printStackTrace();
		}
		SimpleDateFormat formatter2 = new SimpleDateFormat("EEEE, MMMM dd");
		String mystartDate = formatter2.format(startDate);
		// imageDownloader.download(ImageUrl,mydealImage);
		ImageLoader imageLoader = new ImageLoader(getApplicationContext());
		imageLoader.DisplayImage(offerDetails.getImageUrl(), mydealImage);
		businessName.setText(offerDetails.getBusinessName());
		dealName.setText(offerDetails.getDealName());
		dealNameBold.setText(offerDetails.getDealName());
		//cusineType.setText(offersDetailsVo.getConResId());
		dealRestriction.setText(offerDetails.getDealRestriction());
		confirmationCode.setText(offerDetails.getConResId());
		dealDate.setText(mystartDate);
		dealTime.setText(offerDetails.getStartTime() + " - "+ offerDetails.getEndTime());
		
		String reserveTime = offerDetails.getReserveTimeStamp();
		StringBuilder stDate = new StringBuilder();
		StringBuilder endDate = new StringBuilder();
		if(!ValidationUtil.isNullOrEmpty(reserveTime))
		{
			int index = reserveTime.indexOf(" ");	
			String endTime = offerDetails.getEndTime();
			if (endTime.equals("12:00 AM"))
			{
				endTime ="11:59 PM";
			}			
			String startTime = offerDetails.getStartTime();
			if(index!=-1)
			{
				String reserveDate = reserveTime.substring(0,index).trim();
				reserveStartTime = stDate.append(reserveDate).append(" ").append(startTime).toString();
				reserveEndTime  = endDate.append(reserveDate).append(" ").append(endTime).toString();
				Date finalReservStartTime = DateFormatUtil.dateAfterSomeTimePeriod(reserveStartTime,"mins",15,"yyyy-MM-dd hh:mm aa");
				Date finalReservEndTime = DateFormatUtil.dateAfterSomeTimePeriod(reserveEndTime,"hour",2,"yyyy-MM-dd hh:mm aa");
				Log.v(" finalReservStartTime is ", finalReservStartTime.toString());
				Log.v("finalReservEndTime is ", finalReservEndTime.toString());
				String currentDate = offersDetailsVo.getCurrentDate();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
				String localTime = sdf.format(new Date());
				
                if(!ValidationUtil.isNullOrEmpty(localTime))
                {
                	//Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
					Date currentLocalTime = DateFormatUtil.parseIntoDifferentFormat(localTime,"yyyy-MM-dd HH:mm:ss.SSSSSS");
                	isTimeToAutoCheck = ((finalReservStartTime.before(currentLocalTime)) && (finalReservEndTime.after(currentLocalTime)))?true:false;
					isValidOffer = ((finalReservStartTime.before(currentLocalTime)))?true:false;
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal = Calendar.getInstance();
					String currentTime= df.format(cal.getTime()).toString();
					isTodaysOffer = (reserveDate.equalsIgnoreCase(currentTime));
					isExpiredOffer = ((finalReservEndTime.before(currentLocalTime)))?true:false;
                }
               				
                /*if(!ValidationUtil.isNullOrEmpty(currentDate))
                {
                      Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
                      isTimeToAutoCheck = ((finalReservStartTime.before(current)) && (finalReservEndTime.after(current)))?true:false;
                      isValidOffer = ((finalReservStartTime.before(current)))?true:false;
                      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                      Calendar cal = Calendar.getInstance();
                      String currentTime= df.format(cal.getTime()).toString();
                      isTodaysOffer = (reserveDate.equalsIgnoreCase(currentTime));
                }*/
		

				/* getting location of device */
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			    
				new FetchCordinates().execute();
				
				//Set confirmation Box messages.
				 if(isTimeToAutoCheck){
						step.setText("THE NEXT STEP");
						confrmBoxMessage.setText("If you’re at the restaurant, please show this confirmation code to your waiter to redeem the offer.  We’ll follow up later to find out if you could attend.  By confirming your attendance, TangoTab will feed a person in need.");
					}
				 
					if(!ValidationUtil.isNullOrEmpty(offerDetails.getIsConsumerShownUp()))
					{
						if (offerDetails.getIsConsumerShownUp().contentEquals("-1") && isExpiredOffer) {
							step.setText("THANK YOU");
							confrmBoxMessage.setText("We're sorry you could not attend. Redeem an offer soon, and TangoTab will feed a person in need.");
						}
						else if(!offerDetails.getIsConsumerShownUp().contentEquals("0") && isExpiredOffer){
								step.setText("THANK YOU");
								confrmBoxMessage.setText("We hope you enjoyed your offer.  Thank you for feeding a person in need.");
							}
					}
			}
		}	
	}
	private final LocationListener locationListener = new LocationListener()
	{ 
		public void onLocationChanged(Location location)
		{ 
			isLocationAvailable=true;

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
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu); 
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.menubar, menu);
		return true;
	}*/
	
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.myoffers:
				Intent homeIntent=new Intent(this, MyOffersActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);
				break;
			
			case R.id.nearme:
				Intent businessearchIntent=new Intent(this, NearMeActivity.class);
				businessearchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(businessearchIntent);
				break;
		
			case R.id.search:
				Intent contactmanagerIntent=new Intent(this, SearchActivity.class);
				contactmanagerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(contactmanagerIntent);
				break;				
			case R.id.settings:
				Intent followupIntent=new Intent(this, SettingsActivity.class);
				followupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(followupIntent);
				break;		
			default:
				return super.onOptionsItemSelected(item);
		}		
		finish();
		return true;
	}*/
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Message_Id:
			AlertDialog.Builder ab = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage(message.replace("%20", " "));
			ab.setPositiveButton("Dismiss", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab.create();
		case Reser_comp:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage("You have successfully checked in this offer");
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab1.create();
		case AppConstant.FACEBOOK_POST_SUCCESSFUL_DIALOG:
			AlertDialog.Builder facebookShareDialog = new AlertDialog.Builder(
					MyoffersDetailActivity.this);
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
		case exp_Date:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab2.setTitle("TangoTab");
			ab2.setMessage("You cannot check into this offer. It has already expired or is not valid yet.");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab2.create();		
		case 10:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});
			return ab3.create();
		case not_valid:
			AlertDialog.Builder ab6 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab6.setTitle("Alert:");
			ab6.setMessage("The offer is not valid yet");
			ab6.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab6.create();	
		case 20:
			AlertDialog.Builder ab7 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab7.setTitle("Alert:");
			ab7.setMessage("Offer Cancelled.");
			ab7.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					Intent myOffersIntent=new Intent(MyoffersDetailActivity.this, MyOffersActivity.class);
					myOffersIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myOffersIntent);
				}
			});
			return ab7.create();	
		case 21:
			AlertDialog.Builder ab8 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab8.setTitle("Alert:");
			ab8.setMessage("Offer is already Checked In");
			ab8.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					/*Intent myOffersIntent=new Intent(MyoffersDetailActivity.this, MyOffersActivity.class);
					myOffersIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myOffersIntent);*/
				}
			});
			return ab8.create();
		case TWITTER_ID:
			AlertDialog.Builder twitterDialog = new AlertDialog.Builder(MyoffersDetailActivity.this);
			twitterDialog.setTitle("TangoTab");
			twitterDialog.setMessage(AppConstant.TWITTER_OFF_MESSGAGE);
			twitterDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			return twitterDialog.create();
		case 30:
			AlertDialog.Builder noOffers = new AlertDialog.Builder(MyoffersDetailActivity.this);
			noOffers.setTitle("TangoTab");
			noOffers.setMessage("No more offers");
			noOffers.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return noOffers.create();
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
			AlertDialog.Builder tweetSent = new AlertDialog.Builder(MyoffersDetailActivity.this);
			tweetSent.setTitle("TangoTab");
			tweetSent.setMessage("Tweet Sent ");
			tweetSent.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return tweetSent.create();
			
		case TWITTER_FAILED:
			AlertDialog.Builder tweetFailed = new AlertDialog.Builder(MyoffersDetailActivity.this);
			tweetFailed.setTitle("TangoTab");
			tweetFailed.setMessage("There was an error while connecting with twitter");
			tweetFailed.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return tweetFailed.create();	
		case TWITTER_POST_FAILED:
			AlertDialog.Builder postFailed = new AlertDialog.Builder(MyoffersDetailActivity.this);
			postFailed.setTitle("TangoTab");
			postFailed.setMessage("Sorry this offer was already tweeted");
			postFailed.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});return postFailed.create();	
			
		case 50:
			AlertDialog.Builder alert = null;
			AlertDialog dlg =null;
			alert = new AlertDialog.Builder(this);
			alert.setTitle("TangoTab");
			alert.setMessage("Do you really want to cancel claimed offer?");
			alert.setCancelable(false);
				/**
				 * on click listener added for confirmation Dialog button
				 */
				alert.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton)
							{
								/**
								 * Remove offers from the my offers list.
								 */
								new RemoveOfferAsyncTask().execute(offersDetailsVo.getConResId());
							}
						});
		
				/**
				 * on click listener added for Cancel Dialog button
				 */
				alert.setNegativeButton("NO",new DialogInterface.OnClickListener()
				{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								dialog.dismiss();						
							}
				});return dlg = alert.create();		
		case AppConstant.ICON_TWITTER_SHARE:
			AlertDialog.Builder twitterdetailsicon = new AlertDialog.Builder(
					MyoffersDetailActivity.this);
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
					MyoffersDetailActivity.this);
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
			
		}
		return null;
	}
	/**
	 * This method will be update with the new location.
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location)
	{
		if (location != null)
		{
			double dealLat, dealLong = 0.0;
			double locationLat,locationLong=0.0;
			locationLat = location.getLatitude();
			locationLong = location.getLongitude();
			AppConstant.locationLat =locationLat;
			AppConstant.locationLong = locationLong;
			Log.v("Device Latttitude is ", String.valueOf(locationLat));
			Log.v("Device Longitude is ", String.valueOf(locationLong));
			
			Location locationA = new Location("DeviceLocation");

			locationA.setLatitude(locationLat);
			locationA.setLongitude(locationLong);

			Location locationB = new Location("UserLocation");
			if(!ValidationUtil.isNull(offersDetailsVo))
			{
				dealLat = Double.parseDouble(offersDetailsVo.getLatitude());
				dealLong = Double.parseDouble(offersDetailsVo.getLongitude());
				
				Log.v("Restrurant Latttitude is ", String.valueOf(dealLat));
				Log.v("Restrurant Longitude is ", String.valueOf(dealLong));
				
				locationB.setLatitude(dealLat);
				locationB.setLongitude(dealLong);
			}		
			
			//distance between Device and restaurant.
			
			float distance = locationA.distanceTo(locationB);

			distanceInmiles = (float) (distance * 0.000621371);
			
			Log.v("distance in miles is ", String.valueOf(distanceInmiles));
			/**
			 * If distance between restaurant and device is less than 1/4th of
			 * miles.
			 */
			//Toast.makeText(getApplicationContext(), "distance ="+distanceInmiles, 2000).show();
			
			if (isTimeToAutoCheck && distance <= Float.valueOf(AppConstant.NEAR_TO_RESTRURANT))
			{	
				//if(offersDetailsVo.getIsConsumerShownUp().contentEquals("0")){
					step.setText("THE LAST STEP");
					confrmBoxMessage.setText("Welcome to the restaurant. Please show this confirmation code to your waiter to redeem the offer. Thank you for helping to feed a person in need.");
				//}
				
				if(isAutoCheckIn && autoCheckInCount==0)
				{
					isAutoCheckIn=false;
					doCheckIn("Y");
				}
			}else if(isTimeToAutoCheck && distance > Float.valueOf(AppConstant.NEAR_TO_RESTRURANT)){
				step.setText("THE NEXT STEP");
				confrmBoxMessage.setText("If you’re at the restaurant, please show this confirmation code to your waiter to redeem the offer.  We’ll follow up later to find out if you could attend.  By confirming your attendance, TangoTab will feed a person in need.");
			}
			else if (isTodaysOffer && !(isValidOffer)&& distance <= Float.valueOf(AppConstant.NEAR_TO_RESTRURANT)) 
				{
				//showDialog(not_valid);
			}else 
			{
				Log.v("Auto check in status", "Auto Checkin is not possible");
			}	
		}
	}
	
	/**
	 * This method will take care all the check in functionality.
	 * 
	 * @param autoCheckIn
	 */
	private void doCheckIn(String autoCheckIn)
	{
		if (checkInternetConnection())
		{
				//Get all user details from sharedPreferences.				
				SharedPreferences spc = getSharedPreferences("UserDetails", 0);
				String firstName = spc.getString("firstName", "");
				String lastName = spc.getString("lastName", "");
				offersDetailsVo.setFirstName(firstName);
				offersDetailsVo.setLastName(lastName);
				offersDetailsVo.setAutoCheckIn(autoCheckIn);
				/** 
				 * Call the asyntask to execute the service
				 */
				new InsertDealAsyncTask().execute();
		}
		else{
				showDialog(10);
			}
		}
	
	/**
	 * This call will be used to remove the offers from my offers.
	 * 
	 * @author dillip.lenka
	 *
	 */
		public class RemoveOfferAsyncTask extends AsyncTask<String, Void, String>
		{		
			@Override
			protected void onPreExecute()
			{
				mDialog = ProgressDialog.show(MyoffersDetailActivity.this, "Searching ", "Please wait...");
				mDialog.setCancelable(true);
			}
			@Override
			protected String doInBackground(String...conResId)
			{
				message =null;
				try {
					MyOffersDetailService service = new MyOffersDetailService();
					message = service.cancelOffer(conResId[0]);
				}
				catch (ConnectTimeoutException e)
				{
					Log.e("ConnectTimeoutException", "ConnectTimeoutException ocuuered in invoking cancel the offer =",e);
					message =null;
					Intent contactIntent=new Intent(getApplicationContext(), ContactSupportActvity.class);
					startActivity(contactIntent);
				}
				catch (Exception e)
				{
					Log.e("Error", "Error ocuuered in invoking cancel the offer =",e);
					message =null;
				}
				return message;
			}
			
			@Override
			protected void onPostExecute(String message) 
			{
				try{
				 mDialog.dismiss();
				}catch(Exception e)
				{
					Log.e("Exception:", "Exceptions occured at the time of dismiss the dilog", e);
				}
				if (message != null && message.equalsIgnoreCase("Return Successful")) 
				{
					/**
					 * Remove the offers from session.
					 */
					Log.v("Size of the list Before remove the offers", String.valueOf(application.getOffersList().size()));
					if(application.getOffersList().size()>0)
					{
						
						application.getOffersList().clear();
						/*for(int count =0;count<offerList.size();count++)
						{
							OffersDetailsVo  offersVo =offerList.get(count);
							if(offersVo.getDealId().equals(offersDetailsVo.getDealId()))
								offerList.remove(count);
							Log.v("Size of the list after remove the offers", String.valueOf(application.getOffersList().size()));
						}*/
					}
					showDialog(20);	
				}			
			
			}	

		}
	
	/**
	 * This call will be used to run the check in service in different thread.
	 * 
	 * @author dillip.lenka
	 *
	 */
		public class InsertDealAsyncTask extends AsyncTask<Void, Void, String>
		{		
			@Override
			protected void onPreExecute()
			{
				mDialog = ProgressDialog.show(MyoffersDetailActivity.this, "Searching ", "Please wait...");
				mDialog.setCancelable(true);
			}
			@Override
			protected String doInBackground(Void... params)
			{
				message =null;
				try {
					MyOffersDetailService service = new MyOffersDetailService();
					message = service.checkIn(offersDetailsVo);
				}
				catch (Exception e)
				{
					Log.e("Error", "Error ocuuered in invoking check in service url and detailurl =",e);
					message =null;
				}
				return message;
			}
			
			@Override
			protected void onPostExecute(String message) 
			{
				SharedPreferences spc = getSharedPreferences("AppNotification", 0);
				SharedPreferences.Editor edit = spc.edit();
				edit.putBoolean("isCheckInOccured",true);
				edit.commit();
				 mDialog.dismiss();
				if (message != null) 
				{
					Log.v("message is ", message);
					/*
					 * Remove the alarm if alreday set.
					 */
					if(!ValidationUtil.isNull(offersDetailsVo))
						removeAlarmForNotification(offersDetailsVo);
				 	if (message.equals("Successfully CheckIn."))
					{
						
				 		Log.v("result ", "Successfully CheckIn");
						autoCheckInCount++;		
						showDialog(Reser_comp);
					}
					else
					{
						//showDialog(Message_Id);	
					}         	
		           
				}			
			
			}	

		}
		
		
/**
 * AsyncTask to get user location update
 * @author Mahantesh.Tavag
 *
 */
	
    public class FetchCordinates extends AsyncTask<Void, Void, String> 
    {

        @Override
        protected void onPreExecute() {

        	Criteria criteria = new Criteria();
    		locationProvider = locationManager.getBestProvider(criteria, true);
    
    		if (locationProvider != null) {
    			Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        	    autoCheckInCount=0;
        	    updateWithNewLocation(lastKnownLocation);
    			locationManager.requestLocationUpdates(locationProvider, 0, 0,locationListener);    			
    			new CountDownTimer(10000, 1000) {
   			     public void onTick(long millisUntilFinished)
   			     {
   			    	 
   			     }
   			     public void onFinish() 
   			     {
   			    	 if(!isLocationAvailable)
   			    	 {
   			    		 new FetchCordinates().cancel(true);
   			    		 locationManager.removeUpdates(locationListener);
   			    	 }
   			     }
   			  }.start();
    			
    		}
        }

        @Override
        protected void onCancelled()
        {
            Log.v("Cancel","Cancelled by user!");
            locationManager.removeUpdates(locationListener);
        }

        @Override
        protected void onPostExecute(String result)
        {
        	isAutoCheckIn=true;
        }

        @Override
        protected String doInBackground(Void... params)
        {
          return null;  
        }
    }
		
		/**
		 * Asynctask class added for Spinning wheel.
		 * 
		 * @author Dillip.Lenka
		 *
		 */
		public class SpinningTask extends AsyncTask<Void,Void,Void>
		{
			
			@Override
			protected void onPreExecute()
			{
	        	mlocDialog = ProgressDialog.show(MyoffersDetailActivity.this, "Searching ", "Please wait...");
				mlocDialog.setCancelable(true);
	        }
			
			 @Override
		        protected void onCancelled(){
				 mlocDialog.dismiss();
		        }
			 
	        @Override
	        protected Void doInBackground(Void... params)
	        {
	        	try {
	                    Thread.sleep(5000);
	                } catch (InterruptedException e)
	                {
	                    
	                    e.printStackTrace();
	                }
	        	mlocDialog.dismiss();

	            return null;
	        }
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
	 * Remove the alarm for the offers .
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void  removeAlarmForNotification(OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking removeAlarmForNotification() method ", " businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getCurrentDate());
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
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		locationManager.removeUpdates(locationListener);
		isAutoCheckIn=false;
		finish();
		overridePendingTransition(R.anim.push_left_out,R.anim.push_left_out);
	}
	

	public void onMenuSelected(int item) 
{
	switch (item) 
	{
		case 0:
			Intent homeIntent=new Intent(this, MyOffersActivity.class);
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
			break;
		
		case 1:
			Intent businessearchIntent=new Intent(this, NearMeActivity.class);
			businessearchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(businessearchIntent);
			break;
	
		case 2:
			Intent contactmanagerIntent=new Intent(this, SearchActivity.class);
			contactmanagerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(contactmanagerIntent);
			break;				
		case 3:
			Intent followupIntent=new Intent(this, SettingsActivity.class);
			followupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(followupIntent);
			break;		
	}		
}
	@Override
	public void onClick(View v) {
		
		if(v==mBtnShareTwitter){

			if (twitterPreferences.getString("iconpopup", "false")
					.equalsIgnoreCase("true")) {
				showDialog(AppConstant.ICON_TWITTER_SHARE);
			} else if (twitterPreferences.getString("icontwitteron", "false")
					.equalsIgnoreCase("true")) {		
			posttoTwitter();
			
			//startTwitterDialog();
		}
		}
		/*else if(v==mBtnNext)
		{
			Log.e("search list size",""+newOffersList.size());
			try{
			   
				*//**
				 * Check the current index
				 *//*
				 mSelectedIndex = mSelectedIndex+1;
				if((mSelectedIndex<newOffersList.size()))
				{
					setData(newOffersList.get(mSelectedIndex));
					offersDetailsVo = newOffersList.get(mSelectedIndex);
					if(mSelectedIndex+1 == newOffersList.size())
						mBtnNext.setVisibility(View.GONE);
				 }
								
			}catch(ArrayIndexOutOfBoundsException e)
			{
				Log.e("Exception:", "ArrayIndexOutOfExceptions occure", e);
			}
			
		}
		else if(v==mBtnBack){
			finish();
		}*/
	}	/**
	 * Displays a toast notification with No more offers
	 */
	private void showToastMessage(String message){
		Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
	}
	public static void setStatus(int status){
		if(status==0){
			
		}
		else if(status==1){
		
		}
	}
	/**
	 * Start a asynctask and send a tweet
	 */
	private  void sendTweet(){
		new SendTweetAsyncTask().execute();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		try{
			super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=RESULT_CANCELED){
	     if(requestCode==AppConstant.TWITTER_REQUEST_CODE){
	    	 Log.e("twiteer ","req code");
	    	 if(resultCode==RESULT_OK){
	    		 Log.e("result","OK");
	    		 sendTweet();
	    	 }
	    	 else{
	    		 showDialog(TWITTER_FAILED);
	    	 }
	     }
		}
		}catch(Exception e){
			
		}
	}
	/**
	 * AsyncTask for sending a tweet
	 *
	 *
	 */
	public class SendTweetAsyncTask extends AsyncTask<Void,Void,Integer>{
      private final int SUCCESS=1,FAILED=0;
      ProgressDialog mTweetProgressDialog;
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				TwitterUtils.sendTweet(prefs,offersDetailsVo.getBusinessName()+" "+offersDetailsVo.getDealName()+" ",offersDetailsVo.getImageUrl());
				return SUCCESS;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return FAILED;
			}
			
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mTweetProgressDialog==null){
				mTweetProgressDialog=new ProgressDialog(MyoffersDetailActivity.this);
				mTweetProgressDialog.setMessage("Sending tweet..");
			}
			mTweetProgressDialog.show();
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mTweetProgressDialog.dismiss();
			if(result==SUCCESS){
				showDialog(TWEET_SENT);
			}
			else{
				showDialog(TWITTER_FAILED);
			}
		}
	}
	
	/*
	 * Facebook sharing implementation
	 */
	private void postOfferToFacebook() {
		
		 Session session = Session.getActiveSession();

	    if (session != null){
		Bundle postParams = new Bundle();
		postParams.putString("message", offersDetailsVo.getBusinessName());
        postParams.putString("name", offersDetailsVo.getBusinessName());
        postParams.putString("caption", "images.tangotab.com");
        postParams.putString("description", offersDetailsVo.getAddress());
        postParams.putString("link", offersDetailsVo.getImageUrl());
        postParams.putString("picture", offersDetailsVo.getImageUrl());
        
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
                    Toast.makeText(MyoffersDetailActivity.this,
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
					public void onClick(View v)
					{
						dialog.dismiss();
						/*setResult(RESULT_CANCELED);
						finish();*/
						if(progressDialog!=null&&progressDialog.isShowing()){
							progressDialog.dismiss();
						}
						
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
      	 	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyoffersDetailActivity.this);
      		final Uri uri = Uri.parse(url);
      		if (uri != null && uri.getScheme().equals(AppConstant.OAUTH_CALLBACK_SCHEME)) {
      			Log.e(TAG, "Callback received : " + uri);
      			Log.e(TAG, "Retrieving Access Token");
      			new RetrieveAccessTokenTask(MyoffersDetailActivity.this,consumer,provider,prefs).execute(uri);
      				
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
		    switch(keycode)
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(MyoffersDetailActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(MyoffersDetailActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true;
		    }

		    return super.onKeyDown(keycode, e);
		}
	 
	 public void posttoTwitter() {
		 SharedPreferences userPreferences = getSharedPreferences("UserDetails", 0);
			boolean isTwitterShareOn=userPreferences.getBoolean("isTwitterOn", false);
			
			//Check if the user is already logged in twitter
			if (TwitterUtils.isAuthenticated(prefs)) 
			{
	    		sendTweet();
	    	
	    	}
			//Start twitter activity if user is not logged in
			else
	    	{
	    		/*Intent twitterActivity=new Intent(MyoffersDetailActivity.this,TwitterActivity.class);
	    		//twitterActivity.putExtra("calling","MyoffersDetailActivity");
	    	//	startActivity(twitterActivity);
	    		startActivityForResult(twitterActivity,AppConstant.TWITTER_REQUEST_CODE);*/
				/*mDialog = ProgressDialog.show(MyoffersDetailActivity.this, "Please Wait", "Loading...");
				mDialog.setCancelable(false);
				startTwitterLogin();*/
				if(progressDialog==null){
					progressDialog=new ProgressDialog(MyoffersDetailActivity.this);
					progressDialog.setMessage("Please wait..");
					progressDialog.setCancelable(false);
				}
				progressDialog.show();
				startTwitterLogin();
	    	}
	 }
	 private void publishMessage() {
			facebookFacade.publishMessage(offersDetailsVo.getBusinessName(),
					"images.tangotab.com", offersDetailsVo.getBusinessName(), offersDetailsVo.getAddress(),
					offersDetailsVo.getImageUrl());

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
