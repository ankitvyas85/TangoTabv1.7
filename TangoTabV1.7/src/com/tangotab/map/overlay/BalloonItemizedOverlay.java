
package com.tangotab.map.overlay;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.tangotab.R;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.nearMe.vo.DealsDetailVo;

/**
 * An abstract extension of ItemizedOverlay for displaying an information balloon
 * upon screen-tap of each marker overlay.
 * 
 *
 */
public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item>{

	private static final long BALLOON_INFLATION_TIME = 300;
	private static Handler handler = new Handler();
	
	private MapView mapView;
	private BalloonOverlayView<Item> balloonView;
	private View clickRegion;
	private View closeRegion;
	private int viewOffset;
	final MapController mc;
	private Item currentFocusedItem;
	private int currentFocusedIndex;
	private ImageButton next;
	ImageButton back;
	private boolean showClose = true;
	private boolean showDisclosure = false;
	private boolean snapToCenter = true;
	
	private static boolean isInflating = false;
	private List<DealsDetailVo> dealsDetailList,dealsFilteredList;
	private List<OffersDetailsVo> offerDetailList,offersFilteredList;
	int startingIndex=0;
	
	/**
	 * Create a new BalloonItemizedOverlay
	 * 
	 * @param defaultMarker - A bounded Drawable to be drawn on the map for each item in the overlay.
	 * @param mapView - The view upon which the overlay items are to be drawn.
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
	}
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView,List<DealsDetailVo> dealsDetailList){
		super(defaultMarker);	
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
		this.dealsDetailList=dealsDetailList;
		Log.e("dealsDetailList",""+dealsDetailList.size());
	}
	
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView,List<OffersDetailsVo> offerDetailList,int check)
	{
		super(defaultMarker);	
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
		this.offerDetailList=offerDetailList;
	}
	/**
	 * Set the horizontal distance between the marker and the bottom of the information
	 * balloon. The default is 0 which works well for center bounded markers. If your
	 * marker is center-bottom bounded, call this before adding overlay items to ensure
	 * the balloon hovers exactly above the marker. 
	 * 
	 * @param pixels - The padding between the center point and the bottom of the
	 * information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	public int getBalloonBottomOffset() {
		return viewOffset;
	}
	
	/**
	 * Override this method to handle a "tap" on a balloon. By default, does nothing 
	 * and returns false.
	 * 
	 * @param index - The index of the item whose balloon is tapped.
	 * @param item - The item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index, Item item) {
		return false;
	}
	
	/**
	 * Override this method to perform actions upon an item being tapped before 
	 * its balloon is displayed.
	 * 
	 * @param index - The index of the item tapped.
	 */
	protected void onBalloonOpen(int index) {}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	//protected final boolean onTap(int index) {
	public final boolean onTap(int index) {
		startingIndex=0;
		if(dealsFilteredList!=null)
		dealsFilteredList.clear();
		dealsFilteredList=null;
		handler.removeCallbacks(finishBalloonInflation);
		isInflating = true;
		handler.postDelayed(finishBalloonInflation, BALLOON_INFLATION_TIME);
		
		currentFocusedIndex = index;
		currentFocusedItem = createItem(index);
		setLastFocusedIndex(index);
		
		onBalloonOpen(index);
		createAndDisplayBalloonOverlay();
		
		if (snapToCenter) {
			animateTo(index, currentFocusedItem.getPoint());
		}
		
		return true;
	}

	/**
	 * Animates to the given center point. Override to customize how the
	 * MapView is animated to the given center point
	 *
	 * @param index The index of the item to center
	 * @param center The center point of the item
	 */
	protected void animateTo(int index, GeoPoint center) {
		mc.animateTo(center);
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that
	 * can populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView() {
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}
	
	/**
	 * Expose map view to subclasses.
	 * Helps with creation of balloon views. 
	 */
	protected MapView getMapView() {
		return mapView;
	}

	/**
	 * Makes the balloon the topmost item by calling View.bringToFront().
	 */
	public void bringBalloonToFront() {
		if (balloonView != null) {
			balloonView.bringToFront();
		}
	}

	/**
	 * Sets the visibility of this overlay's balloon view to GONE and unfocus the item. 
	 */
	public void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
		if(back!=null){
			back.setVisibility(View.GONE);
			next.setVisibility(View.GONE);
		}
		currentFocusedItem = null;
		startingIndex=0;
		if(!ValidationUtil.isNullOrEmpty(dealsFilteredList))
			dealsFilteredList.clear();
		dealsFilteredList=null;
		if(!ValidationUtil.isNullOrEmpty(offerDetailList)){
			offerDetailList.clear();
			offerDetailList=null;
		}
	}
	
	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {
		
		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
				if(back!=null){
					back.setVisibility(View.GONE);
					next.setVisibility(View.GONE);
				}
			}
		}
		
	}
	
	public void hideAllBalloons() {
		if (!isInflating) {
			List<Overlay> mapOverlays = mapView.getOverlays();
			if (mapOverlays.size() > 1) {
				hideOtherBalloons(mapOverlays);
			}
			hideBalloon();
		}
	}
	
	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden {@link #onBalloonTap} method.
	 */
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			
			float startX;
			float startY;
			
			public boolean onTouch(View v, MotionEvent event) {
				
				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (d != null) {
						int[] states = {android.R.attr.state_pressed};
						if (d.setState(states)) {
							d.invalidateSelf();
						}
					}
					startX = event.getX();
					startY = event.getY();
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if (d != null) {
						int newStates[] = {};
						if (d.setState(newStates)) {
							d.invalidateSelf();
						}
					}
					if (Math.abs(startX - event.getX()) < 40 && 
							Math.abs(startY - event.getY()) < 40 ) {
						// call overridden method
						onBalloonTap(currentFocusedIndex, currentFocusedItem);
					}
					return true;
				} else {
					return false;
				}
				
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#getFocus()
	 */
	@Override
	public Item getFocus() {
		return currentFocusedItem;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#setFocus(Item)
	 */
	@Override
	public void setFocus(Item item) {
		super.setFocus(item);	
		currentFocusedIndex = getLastFocusedIndex();
		currentFocusedItem = item;
		if (currentFocusedItem == null) {
			hideBalloon();
		} else {
			createAndDisplayBalloonOverlay();
		}	
	}
	
	/**
	 * Creates and displays the balloon overlay by recycling the current 
	 * balloon or by inflating it from xml. 
	 * @return true if the balloon was recycled false otherwise 
	 */
	private boolean createAndDisplayBalloonOverlay(){
		boolean isRecycled;
		if (balloonView == null) {
			balloonView = createBalloonOverlayView();
			
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());

			closeRegion = (View) balloonView.findViewById(R.id.balloon_close);
			if (closeRegion != null) {
				if (!showClose) {
					closeRegion.setVisibility(View.GONE);
				} else {
					closeRegion.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(back!=null){
								back.setVisibility(View.GONE);
							    next.setVisibility(View.GONE);	
							}
							
							hideBalloon();	
						}
					});
				}
			}
			if (showDisclosure && !showClose) {
				/*View v = balloonView.findViewById(R.id.balloon_disclosure);
				if (v != null) {
					v.setVisibility(View.VISIBLE);
				}*/
			}
			isRecycled = false;
		} else {
			isRecycled = true;
		}
		if(!ValidationUtil.isNullOrEmpty(dealsDetailList)){
			
			for(DealsDetailVo dealsDetailVo:dealsDetailList)
			{	
				//businessName.append(offerCount).append(")").append(" ").append(dealsList.get(i).getBusinessName()
			
				if(dealsDetailVo.getBusinessName().equals(currentFocusedItem.getTitle()))
			{
				if(ValidationUtil.isNullOrEmpty(dealsFilteredList))
				{
						dealsFilteredList=new ArrayList<DealsDetailVo>();
				}
				/**
				 * if select offer matches with the list of offers
				 */
				if(dealsDetailVo.getDealName().equals(currentFocusedItem.getSnippet())){
					dealsFilteredList.add(0, dealsDetailVo);
				}
				else{
					dealsFilteredList.add(dealsDetailVo);
				}
			}
		}
			if(dealsFilteredList!=null){
		if(dealsFilteredList.size()>1){
			
	    back=(ImageButton)balloonView.findViewById(R.id.btnBack);
	    //back.setVisibility(View.VISIBLE);
	    //back.setClickable(false);
	    next=(ImageButton)balloonView.findViewById(R.id.btnNext);
	    next.setVisibility(View.VISIBLE);
	    
	    /**
	     * back button on click listener.
	     */
	    back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Log.e("start index",""+startingIndex);
				if(startingIndex>0){
					startingIndex--;
					//Log.e("start index",""+startingIndex);
					   balloonView.setBallonData(dealsFilteredList.get(startingIndex));
					   next.setVisibility(View.VISIBLE);
				} 
				if(startingIndex == 0){
					back.setVisibility(View.GONE);
				}
				
			}
		});
	    /**
	     * next button on click listener.
	     */
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Log.e("start index",""+startingIndex);
				if(startingIndex<dealsFilteredList.size()-1){
					startingIndex++;
		          balloonView.setBallonData(dealsFilteredList.get(startingIndex));
		          back.setVisibility(View.VISIBLE); 
		           
				}
				if(startingIndex == dealsFilteredList.size()-1){
					next.setVisibility(View.GONE);
				}
				/*if(!(back.isClickable())){
					back.setClickable(true);
				}*/
			}
		});
	    }
		else{
			if(back!=null){
				back.setVisibility(View.GONE);
				next.setVisibility(View.GONE);
			}
		}
		}
		}
		if(!ValidationUtil.isNullOrEmpty(offerDetailList)){

			
			for(OffersDetailsVo dealsDetailVo:offerDetailList)
			{	
				//businessName.append(offerCount).append(")").append(" ").append(dealsList.get(i).getBusinessName()
			
				if(dealsDetailVo.getBusinessName().equals(currentFocusedItem.getTitle()))
			{
				if(ValidationUtil.isNullOrEmpty(dealsFilteredList))
				{
						offersFilteredList=new ArrayList<OffersDetailsVo>();
				}
				/**
				 * if select offer matches with the list of offers
				 */
				if(dealsDetailVo.getAddress().equals(currentFocusedItem.getSnippet())){
					offersFilteredList.add(0, dealsDetailVo);
				}
				else{
					offersFilteredList.add(dealsDetailVo);
				}
			}
		}
			if(offersFilteredList!=null){
		if(offersFilteredList.size()>1){
			
	    back=(ImageButton)balloonView.findViewById(R.id.btnBack);
	    back.setVisibility(View.VISIBLE);
	    //back.setClickable(false);
	    next=(ImageButton)balloonView.findViewById(R.id.btnNext);
	    next.setVisibility(View.VISIBLE);
	    
	    /**
	     * back button on click listener.
	     */
	    back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Log.e("start index",""+startingIndex);
				if(startingIndex>0){
					startingIndex--;
					//Log.e("start index",""+startingIndex);
					   balloonView.setBallonData(offersFilteredList.get(startingIndex));
				}
				
			}
		});
	    /**
	     * next button on click listener.
	     */
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(startingIndex<dealsFilteredList.size()-1){
					startingIndex++;
		          balloonView.setBallonData(offersFilteredList.get(startingIndex));
		             
		           
				}	
				/*if(!(back.isClickable())){
					back.setClickable(true);
				}*/
			}
		});
	    }
		}
			
		}
		balloonView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}
		
		if (currentFocusedItem != null)
			
			balloonView.setData(currentFocusedItem);
			
		GeoPoint point = currentFocusedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		balloonView.setVisibility(View.VISIBLE);
		
		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}
		
		return isRecycled;
	}
	
	public void setShowClose(boolean showClose) {
		this.showClose = showClose;
	}

	public void setShowDisclosure(boolean showDisclosure) {
		this.showDisclosure = showDisclosure;
	}

	public void setSnapToCenter(boolean snapToCenter) {
		this.snapToCenter = snapToCenter;
	}

	public static boolean isInflating() {
		return isInflating;
	}
	
	private static Runnable finishBalloonInflation = new Runnable() {
		public void run() {
			isInflating = false;
		}
	};
	
}
