package com.tangotab.map.activity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.GeoCoderUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.overlay.OnSingleTapListener;
import com.tangotab.map.overlay.TapControlledMapView;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;

/**
 * Map offers in google map
 * 
 * <br>
 * Class :MyOffersMapingActivity <br>
 * layout:myoffersmap.xml
 * 
 * @author Dillip.Lenka
 * 
 */
public class MyOffersMapingActivity extends MapActivity 
{
	/* FIELDS */
	private MapController mapController = null;
	private TapControlledMapView MView = null;
	GeoPoint point = null;
	Button back = null;
	ArrayList<GeoPoint> itemAddress = null;
	GeoPoint point1 = null;
	private Vibrator vibrator;
	SimpleItemizedOverlay itemizedOverlay = null;
	public TangoTabBaseApplication application;
	private ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle bundle) 
	{

		super.onCreate(bundle);
		
		mDialog = ProgressDialog.show(MyOffersMapingActivity.this, "Searching ", "Please wait...");
		mDialog.setCancelable(true);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.myoffersmap);

		application = (TangoTabBaseApplication) getApplication();

		itemAddress = new ArrayList<GeoPoint>();
		AppConstant.flagFormMaping = true;

		MView = (TapControlledMapView) findViewById(R.id.mapviewdeals);
		MView.invalidate();

		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		// dismiss balloon upon single tap of MapView
		MView.setOnSingleTapListener(new OnSingleTapListener() {
			@Override
			public boolean onSingleTap(MotionEvent e) {
				if(!ValidationUtil.isNull(itemizedOverlay))
				{
					itemizedOverlay.hideAllBalloons();
				}
				return true;
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						myMapView();
					}
				});
			}
		}).start();
		
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
						Intent meIntent=new Intent(MyOffersMapingActivity.this, MainMenuActivity.class);
						meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(meIntent);
					}
				});
					
		//me button onclick listener.
				meButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent meIntent=new Intent(MyOffersMapingActivity.this, MeActivity.class);
						meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(meIntent);
					}
				});		
		
		// near me button onclick listener		 
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent=new Intent(MyOffersMapingActivity.this, NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});
		
		//Top Search button onclick listener.
		topSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(MyOffersMapingActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});


	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AppConstant.flagFormMaping = false;

	}

	public void zoomOption() {
		int minLat = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLon = Integer.MIN_VALUE;

		if (itemAddress != null)
			for (GeoPoint item : itemAddress) {
				int lat = item.getLatitudeE6();
				int lon = item.getLongitudeE6();
				maxLat = Math.max(lat, maxLat);
				minLat = Math.min(lat, minLat);
				maxLon = Math.max(lon, maxLon);
				minLon = Math.min(lon, minLon);
			}
		GeoPoint myLocation = new GeoPoint((int) (AppConstant.dev_lat * 1E6),
				(int) (AppConstant.dev_lang * 1E6));
		maxLat = Math.max(myLocation.getLatitudeE6(), maxLat);
		minLat = Math.min(myLocation.getLatitudeE6(), minLat);
		maxLon = Math.max(myLocation.getLongitudeE6(), maxLon);
		minLon = Math.min(myLocation.getLongitudeE6(), minLon);
		mapController = MView.getController();
		if (mapController != null) {
			double borderFactor = 2.5;
			mapController.zoomToSpan(
					(int) (Math.abs(maxLat - minLat) * borderFactor),
					(int) (Math.abs(maxLon - minLon) * borderFactor));
			mapController.animateTo(myLocation);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@SuppressWarnings("unchecked")
	public void myMapView() {

		SimpleItemizedOverlay itemizedOverlay, myLocationItemizedOverlay;

		mapController = MView.getController();

		MView.setBuiltInZoomControls(true);
		MView.displayZoomControls(true);

		List<OffersDetailsVo> offersList = new ArrayList<OffersDetailsVo>();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			offersList = (List<OffersDetailsVo>) bundle.get("offerList");

		}

		AppConstant.offersList = offersList;
		if (!ValidationUtil.isNullOrEmpty(offersList)) {
			itemizedOverlay = new SimpleItemizedOverlay(getResources()
					.getDrawable(R.drawable.green_map_pin), MView, offersList,
					0);
			Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
			int offerCount = 1;
			for (OffersDetailsVo offersDetailsVo : offersList) {
				if (!ValidationUtil.isNullOrEmpty(offersDetailsVo.getAddress())) {
					try {
						List<Address> addresses = geoCoder.getFromLocationName(
								offersDetailsVo.getAddress(), 1);

						if (addresses != null && addresses.size() > 0) {
							point = new GeoPoint((int) (addresses.get(0)
									.getLatitude() * 1E6), (int) (addresses
									.get(0).getLongitude() * 1E6));
							itemAddress.add(point);
							// StringBuilder businessName = new StringBuilder();
							// businessName.append(offerCount).append(")").append(" ").append(offersDetailsVo.getBusinessName());
							OverlayItem overlayItem = new OverlayItem(point,
									offersDetailsVo.getBusinessName(),
									offersDetailsVo.getDealName());
							itemizedOverlay.addOverlay(overlayItem);
							MView.getOverlays().add(itemizedOverlay);
							offerCount++;

						} else {

							GeoCoderUtil.getLatLong(URLEncoder.encode(
									offersDetailsVo.getAddress(), "UTF-8"));
							point = new GeoPoint(
									(int) (AppConstant.locationLat * 1E6),
									(int) (AppConstant.locationLong * 1E6));
							itemAddress.add(point);
							/*StringBuilder businessName = new StringBuilder();
							businessName.append(offerCount).append(")")
									.append(" ")
									.append(offersDetailsVo.getBusinessName());*/
							OverlayItem overlayItem = new OverlayItem(point,offersDetailsVo.getBusinessName(),offersDetailsVo.getDealName());
							itemizedOverlay.addOverlay(overlayItem);
							MView.getOverlays().add(itemizedOverlay);
							offerCount++;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
		// MapItemizedOverlay itemizedOverlay1 = new MapItemizedOverlay(
		// getResources().getDrawable(R.drawable.location__icon),MyOffersMapingActivity.this);
		/*
		 * OverlayItem overlayItem = new OverlayItem(point,
		 * offersDetailsVo.getBusinessName(), offersDetailsVo.getDealName());
		 * itemizedOverlay.addOverlay(overlayItem);
		 */
		// MView.getOverlays().add(itemizedOverlay1);
		myLocationItemizedOverlay = new SimpleItemizedOverlay(getResources()
				.getDrawable(R.drawable.location__icon), MView);
		point1 = new GeoPoint((int) (AppConstant.dev_lat * 1E6),
				(int) (AppConstant.dev_lang * 1E6));
		// OverlayItem overlayItem = new OverlayItem(point,
		// offersDetailsVo.getBusinessName(), offersDetailsVo.getDealName());

		OverlayItem overlayItem = new OverlayItem(point1, "Current Location", "");

		myLocationItemizedOverlay.addOverlay(overlayItem);
		MView.getOverlays().add(myLocationItemizedOverlay);
		zoomOption();
		
		if(mDialog!=null)
			mDialog.dismiss();

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); MenuInflater
	 * menuInflater=getMenuInflater(); menuInflater.inflate(R.menu.menubar,
	 * menu); return true; }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.myoffers: Intent homeIntent=new
	 * Intent(this, MyOffersActivity.class);
	 * homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * startActivity(homeIntent); break;
	 * 
	 * case R.id.nearme: Intent businessearchIntent=new Intent(this,
	 * NearMeActivity.class);
	 * businessearchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * startActivity(businessearchIntent); break;
	 * 
	 * case R.id.search: Intent contactmanagerIntent=new Intent(this,
	 * SearchActivity.class);
	 * contactmanagerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * startActivity(contactmanagerIntent); break; case R.id.settings: Intent
	 * followupIntent=new Intent(this, SettingsActivity.class);
	 * followupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * startActivity(followupIntent); break; default: return
	 * super.onOptionsItemSelected(item); } finish(); return true; }
	 */

	public void onMenuSelected(int item) {
		switch (item) {
		case 0:
			Intent homeIntent = new Intent(this, MyOffersActivity.class);
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
			break;

		case 1:
			Intent businessearchIntent = new Intent(this, NearMeActivity.class);
			businessearchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(businessearchIntent);
			break;

		case 2:
			Intent contactmanagerIntent = new Intent(this, SearchActivity.class);
			contactmanagerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(contactmanagerIntent);
			break;
		case 3:
			Intent followupIntent = new Intent(this, SettingsActivity.class);
			followupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(followupIntent);
			break;
		}
	}
	
	  @Override
			public boolean onKeyDown(int keycode, KeyEvent e) {
			    switch(keycode) {
			        case KeyEvent.KEYCODE_MENU:
			        	Intent mainMenuIntent = new Intent(MyOffersMapingActivity.this,MainMenuActivity.class);
						mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mainMenuIntent);
			            return true;
			        case KeyEvent.KEYCODE_SEARCH:
			        	Intent searchIntent=new Intent(MyOffersMapingActivity.this, SearchActivity.class);
						searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(searchIntent);
			            return true;
			    }

			    return super.onKeyDown(keycode, e);
			}
}