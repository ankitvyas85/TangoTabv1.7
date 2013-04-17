package com.tangotab.map.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.google.directions.service.RouteDirectionService;
import com.tangotab.core.google.directions.vo.DirectionsVo;
import com.tangotab.core.google.directions.vo.DirectionsVo.Location;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.GeoCoderUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.login.activity.ForgetPasswordActivity;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.overlay.OnSingleTapListener;
import com.tangotab.map.overlay.TapControlledMapView;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;

/**
 * Display a point in the google map
 * 
 * <br> Class: MapPointActivity
 * <br> layout:mappoint.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class MapPointActivity extends MapActivity
{
	/* FIELDS */
	private MapController mapController = null;
	private TapControlledMapView MView = null;
	public static int index, position;
	TangoTabBaseApplication application;
	Geocoder gc=null;
	GeoPoint point=null;
	List<Address> foundGeocode=null;
	SimpleItemizedOverlay itemizedOverlay=null;
	public String isFrom=null;
	private Vibrator vibrator;
	private MapView mapView =null;
	private String fromPage;
	private String businessName;
	private String address;
	private OffersDetailsVo offersDetailsVo;
		
	
	 
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		
			
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mappoint);
		
		
		application = (TangoTabBaseApplication) getApplication();
	/*	if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}*/
		
		mapView = (MapView) findViewById(R.id.mapview);
		
		fromPage = getIntent().getStringExtra("fromPage");
		if(!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("claimOffer"))
		{
			DealsDetailVo dealsDetailVo = (DealsDetailVo) getIntent().getSerializableExtra("selectDeal");
			businessName = dealsDetailVo.getBusinessName();
			address = dealsDetailVo.getAddress();
			
		}
		else if (!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("myOfferDetail"))
		{
			Bundle bun = getIntent().getExtras();
			if(bun!=null)
			{
				offersDetailsVo =(OffersDetailsVo) bun.getParcelable("selectOffers");
				businessName = offersDetailsVo.getBusinessName();
				address = offersDetailsVo.getAddress();
			}
		}
		
		MView = (TapControlledMapView) findViewById(R.id.mapview);
		itemizedOverlay =  new SimpleItemizedOverlay( getResources().getDrawable(R.drawable.green_map_pin),MView);
		
		//Invoke method to plot map direction
		if(!ValidationUtil.isNullOrEmpty(fromPage) && !fromPage.equals("mySettings"))
		{
			Log.v("Get direction for ", " businessName = "+businessName+" address= "+address);
			getDirections(businessName,address);
		}
		
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
		
		
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		final String get = getIntent().getStringExtra("from");
		
	
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
						Intent meIntent=new Intent(MapPointActivity.this, MainMenuActivity.class);
						meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(meIntent);
					}
				});
					
		//me button onclick listener.
				meButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent meIntent=new Intent(MapPointActivity.this, MeActivity.class);
						meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(meIntent);
					}
				});		
		
		// near me button onclick listener		 
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent=new Intent(MapPointActivity.this, NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});
		
		//Top Search button onclick listener.
		topSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent searchIntent=new Intent(MapPointActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});
		
		
	}
	/*
	 * ==================================================================================================================================
	 *  Inner Classes
	 * ==================================================================================================================================
	 */
	
	/**
	 * Asynchronous Task For Loading the Direction Data using Google Map API
	 * 
	 * @author Dillip.Lenka
	 *
	 */
	private class GetDirectionDetailsAsyncTask extends AsyncTask<Location, Void, DirectionsVo> 
	{
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() 
		{
			progressDialog = new ProgressDialog(MapPointActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Plotting Route...");
			progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
			progressDialog.show();
		}	
		
		@Override
		protected DirectionsVo doInBackground(Location... paramArrayOfParams) 
		{
	        try 
	        {
	        	RouteDirectionService routeService = new RouteDirectionService();
	        	DirectionsVo directionsVo = routeService.getDirections(paramArrayOfParams[0], paramArrayOfParams[1]);
				
				return directionsVo;
	        }
	        catch(Exception e)
	        {
	        	Log.e("GoogleDirection",e.getLocalizedMessage());
	        	return new DirectionsVo();
	        }
		}
		
		/**
		 * This method runs on the UI Thread. As a result we can easily achieve an handle onto any of the UI Widget component
		 */
		@Override
		protected void onPostExecute(DirectionsVo directionsVo) 
		{
			progressDialog.dismiss();		
			//Display Informative message to the User
			if(!ValidationUtil.isNull(directionsVo))
			{
				/*
				 * Draw the Route
				 */
				drawRoutePath(directionsVo.getMapRouteOverlayDetails(), Color.BLUE, mapView);
			}
									
			
			
		}
	}
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	 @Override
	  public boolean onKeyUp(int keyCode, KeyEvent event)
	 {
	      if (keyCode == KeyEvent.KEYCODE_BACK) {
	          onBackPressed();
	          return true;
	      }
	      return super.onKeyUp(keyCode, event);
	  }	
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();	
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		MView.invalidate();
		myMapView();
	}
	public void myMapView()
	{
		
		mapController = MView.getController();
		MView.setBuiltInZoomControls(true);
		MView.displayZoomControls(true);
		Intent in = getIntent();
		String address = in.getStringExtra("address");
		String businessName = in.getStringExtra("businessname");
		gc = new Geocoder(this);
		try {
				Log.v("address","address"+address);
				foundGeocode = new Geocoder(getApplicationContext()).getFromLocationName(address, 1);
				Log.v("size",""+foundGeocode.size());
				if(foundGeocode.size()>0){
				point = new GeoPoint((int) (foundGeocode.get(0).getLatitude() * 1E6),
						(int) (foundGeocode.get(0).getLongitude() * 1E6));
				mapController.setCenter(point);
				mapController.setZoom(17);
				OverlayItem overlayItem = new OverlayItem(point,businessName,address);
				itemizedOverlay.addOverlay(overlayItem);
				MView.getOverlays().add(itemizedOverlay);
				mapController.animateTo(point);
				}
				if(foundGeocode.size()<=0)
				{
					point = new GeoPoint((int) (0.0 * 1E6),
							(int) (0.0 * 1E6));
					mapController.setCenter(point);
					OverlayItem overlayItem = new OverlayItem(point,businessName,address);
					itemizedOverlay.addOverlay(overlayItem);
					MView.getOverlays().add(itemizedOverlay);
					mapController.animateTo(point);
				}
			} catch (IOException e)
			{
				Log.e("Exception occured in displaying deal in Map", e.getLocalizedMessage());
				e.printStackTrace();
			}	
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
	/**
	 * Draw the Routes Between the EndPoints
	 * @param {@link GeoPoint} geoPoints
	 * @param color that to be filled in directions display
	 * @param mapView To display Map
	 */
	private void drawRoutePath(List<GeoPoint> geoPoints, int color , MapView mapView) 
	{
	   List<Overlay> overlays = mapView.getOverlays();
	 
	   for (int i = 1; i < geoPoints.size(); i++) 
		   overlays.add(new RouteOverlay(geoPoints.get(i - 1), geoPoints.get(i), color));

	   //Instruct MapView to Flush the Details and refresh
	   mapView.invalidate();
	}
	
	/**
	 * Get Corresponding GeoPoint
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private GeoPoint getGeoPoint(String sLatitude , String sLongitude)
	{
		double latitude = 0;
		double longitude = 0;
		try{
			latitude =  Double.valueOf(sLatitude);
			longitude = Double.valueOf(sLongitude);
		}catch(NumberFormatException e)
		{
			Log.e("Exception:", "Exception occeured at the time of convert to double.", e);
		}
		
		return new GeoPoint((int) (latitude * 1E6),(int)(longitude * 1E6));
	}
	
	
	/**
	 * Route OverLay details. This extends Overlay for allowing use to draw the map Route
	 * 
	 * @author Dillip.Lenka
	 *
	 */
	private class RouteOverlay extends Overlay 
	{
	    private GeoPoint gp1;
	    private GeoPoint gp2;
	    private int color;
	    
	    /**
	     * Constructor
	     */
	    public RouteOverlay(GeoPoint gp1, GeoPoint gp2, int color) 
	    {
	        this.gp1 = gp1;
	        this.gp2 = gp2;
	        this.color = color;
	    }
	    
	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) 
	    {
	    	
	    	//Projects map with the given Geo points
	        Projection projection = mapView.getProjection();
	        Paint paint = new Paint();
	        paint.setAntiAlias(true);
	        
	        //Create points to plot on map
	        Point point = new Point();
	        projection.toPixels(gp1, point);
	        paint.setColor(color);
	        
	        Point point2 = new Point();
	        projection.toPixels(gp2, point2);
	        
	        paint.setStyle(Style.STROKE);
	        paint.setStrokeWidth(2.5f);
	        paint.setAlpha(120);
	        
	        canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
	        super.draw(canvas, mapView, shadow);
	    }
	}
	
	/**
	 * Resource OverLay for plotting additional Marker Details on the MapView
	 * 
	 * @author Dillip.Lenka
	 *
	 */
	private class ResoucesOverlay extends ItemizedOverlay<OverlayItem>
	{
		private Context mContext;
	    private int markerHeight;
	    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	    private static final int FONT_SIZE = 12;
	    private static final int TITLE_MARGIN = 3;
		
		/**
		 * Constructor
		 * @param defaultMarker
		 * @param context
		 */
	    public ResoucesOverlay(Drawable defaultMarker, Context context)
	    {
	        super(boundCenterBottom(defaultMarker));

	        markerHeight = ((BitmapDrawable) defaultMarker).getBitmap().getHeight();
	        mContext = context;

	        populate();
	    }
	    
	    @Override
	    protected OverlayItem createItem(int i)
	    {
	        return mOverlays.get(i);
	    }

	    @Override
	    public int size()
	    {
	        return mOverlays.size();
	    }

	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow)
	    {
	        super.draw(canvas, mapView, shadow);

	        // go through all OverlayItems and draw title for each of them
	        for (OverlayItem item:mOverlays)
	        {
	            /* Converts latitude & longitude of this overlay item to coordinates on screen.
	             * As we have called boundCenterBottom() in constructor, so these coordinates
	             * will be of the bottom center position of the displayed marker.
	             */
	            GeoPoint point = item.getPoint();
	            Point markerBottomCenterCoords = new Point();
	            mapView.getProjection().toPixels(point, markerBottomCenterCoords);

	            /* Find the width and height of the title*/
	            TextPaint paintText = new TextPaint();
	            Paint paintRect = new Paint();

	            Rect rect = new Rect();
	            paintText.setTextSize(FONT_SIZE);
	            paintText.getTextBounds(item.getTitle(), 0, item.getTitle().length(), rect);

	            rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
	            rect.offsetTo(markerBottomCenterCoords.x - rect.width()/2, markerBottomCenterCoords.y - markerHeight - rect.height());

	            paintText.setTextAlign(Paint.Align.CENTER);
	            paintText.setTextSize(FONT_SIZE);
	            paintText.setARGB(255, 255, 255, 255);
	            paintRect.setARGB(130, 0, 0, 0);

	            canvas.drawRoundRect( new RectF(rect), 2, 2, paintRect);
	            canvas.drawText(item.getTitle(), rect.left + rect.width() / 2,  rect.bottom - TITLE_MARGIN, paintText);
	        }
	    }

	    /**
	     * Add the OverLay Pointer details
	     * @param overlayItem
	     */
	    public void addOverlay(OverlayItem overlayItem)
	    {
	        mOverlays.add(overlayItem);
	        populate();
	    }
	    
	  
	}
	
	/**
	 * Calculates the Google Map Zoom Level Based on the Source and Destination Lat Long Values
	 * @param source
	 * @param destination
	 * @return int
	 */
	private void calculateAppropriateZoomLevel(MapView mapView , GeoPoint source , GeoPoint destination)
	{
		try
		{
			List<GeoPoint> items = new ArrayList<GeoPoint>();
			items.add(source);
			items.add(destination);
			
			int minLat = Integer.MAX_VALUE;
			int maxLat = Integer.MIN_VALUE;
			int minLon = Integer.MAX_VALUE;
			int maxLon = Integer.MIN_VALUE;
			double fitFactor = 1.5;
			
			for (GeoPoint item : items) 
			{ 
			      int lat = item.getLatitudeE6();
			      int lon = item.getLongitudeE6();

			      maxLat = Math.max(lat, maxLat);
			      minLat = Math.min(lat, minLat);
			      maxLon = Math.max(lon, maxLon);
			      minLon = Math.min(lon, minLon);
			 }

			//Add controls to map and draw to the point
			MapController mapController = mapView.getController();
			
			//mapController.zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon - minLon));
			mapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor), (int)(Math.abs(maxLon - minLon) * fitFactor));
			mapController.animateTo(new GeoPoint( (maxLat + minLat)/2, (maxLon + minLon)/2 )); 
		}
		catch(Exception e)
		{
			//Add controls to map and draw to the point
			MapController mapController = mapView.getController();
			mapController.setZoom(4);
			mapController.animateTo(destination);
		}
	}
	/**
	 * Get direction from source to destination.
	 * 
	 * @param dealsDetailVo
	 */
	private void getDirections(String businessName, String address)
	{
		//Location Object
		String presentLocation =null;
        Location source = new Location();
        Location dest = new Location();
		/**
		 * Find the current location lattitude and Longitude.
		 */
		SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
		String locLat = Double.toString(AppConstant.dev_lat);//currentLocation.getString("locLat", "");
		String locLong = Double.toString(AppConstant.dev_lang);//currentLocation.getString("locLong", "");
		Log.v("Device latitude and longitude for Direction ", "locLat= "+locLat+" locLong=  "+locLong);				
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
			presentLocation = addressList.get(0).getAddressLine(0);
		}
		
        RouteDirectionService routeDirectionService= new RouteDirectionService();
        Location resturantLoc= null;
        String restLat = null;
		String restLong =null;
		if (!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("myOfferDetail"))
		{
			restLat = offersDetailsVo.getLatitude();
			restLong =offersDetailsVo.getLongitude();
		}
		else{
			try {
				
				
				Map<String, String> locationMap = ValidationUtil.getLatLonFromAddress(address);
				if(!ValidationUtil.isNullOrEmpty(locationMap))
				{
					restLat = locationMap.get("lat");
					restLong = locationMap.get("lng");
				}	
			}
			catch (Exception e)
			{
				Log.e("Exception:", "Exception occure at the time of find lat long from address.", e);
				try {
					GeoCoderUtil.getLatLong(URLEncoder.encode(address, "UTF-8"));
				} catch (UnsupportedEncodingException e1)
				{
					Log.e("Exception:", "Exception occure at the time of encode the address.", e);
				}
				//GeoCoderUtil.getLatLong(address);
				restLat = String.valueOf(AppConstant.locationLat);
				restLong = String.valueOf(AppConstant.locationLong);			
			}
		}
		 /**
         * Set the location latitude and longitude.
         */
        source.setLatitude(locLat);
		source.setLongitude(locLong);
		if(!ValidationUtil.isNullOrEmpty(presentLocation))
			source.setName(presentLocation);
		else
			source.setName("Your Current Location");
		
		Log.v("Restaurant latitude and longitude ", "Latitude= "+restLat+" Langitude=  "+restLong);
		dest.setLatitude(restLat);
		dest.setLongitude(restLong);
		//dest.setName(businessName);
      
		  /*
         * Invoke the Asynchronous Task for Getting the Direction Routes
         */

		new GetDirectionDetailsAsyncTask().execute(new Location[]{source , dest});
		
         /*
		 * Draw the end Points Pin
		 */
		Drawable drawable = MapPointActivity.this.getResources().getDrawable(R.drawable.location__icon);
		ResoucesOverlay sourceItemizedOverLay = new ResoucesOverlay(drawable, MapPointActivity.this);

		/*Drawable drawableDestination = MapPointActivity.this.getResources()
				.getDrawable(R.drawable.pin);
		ResoucesOverlay destinationItemizedOverLay = new ResoucesOverlay(
				drawableDestination, MapPointActivity.this);*/
		
		//Get source and destination geo points
		GeoPoint sourceGeoPoint = getGeoPoint(source.getLatitude(), source.getLongitude());
		GeoPoint destGeoPoint = getGeoPoint(dest.getLatitude(), dest.getLongitude());
		
		
		//Create overlays based on Geo points
		OverlayItem overlayItemSource = new OverlayItem(sourceGeoPoint, source.getName(),  source.getName());
		//OverlayItem overlayItemDest = new OverlayItem(destGeoPoint, businessName, businessName);
		
		//add overlay to ResoucesOverlay
		sourceItemizedOverLay.addOverlay(overlayItemSource);
		//destinationItemizedOverLay.addOverlay(overlayItemDest);

		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(sourceItemizedOverLay);
		//mapOverlays.add(destinationItemizedOverLay);
		
		//AutoCalculate Zoom Level
		calculateAppropriateZoomLevel(mapView , sourceGeoPoint , destGeoPoint);	
				
	}	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode) 
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(MapPointActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(MapPointActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true;
		    }

		    return super.onKeyDown(keycode, e);
		}
}