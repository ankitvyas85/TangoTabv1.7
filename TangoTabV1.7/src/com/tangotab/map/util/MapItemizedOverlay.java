package com.tangotab.map.util;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOfferDetails.activity.MyoffersDetailActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.nearMe.vo.DealsDetailVo;
/**
 * 
 * @author Dillip
 *
 */
public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem>
{

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private String fromPage;
	DealsDetailVo  dealsDetailVo=null;
	int position=0;
	private List<DealsDetailVo> dealsList;
	OffersDetailsVo offersDetailsVo;
	private List<OffersDetailsVo> offersList;
	
	public MapItemizedOverlay(Drawable defaultMarker)
	{
		super(defaultMarker);
	}

	public MapItemizedOverlay(Drawable defaultMarker, Context context)
	 {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		fromPage = null;
	 }
	
	public MapItemizedOverlay(Drawable defaultMarker, Context context, String fromPage)
	 {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		this.fromPage = fromPage;
	 }
	
	 public void addOverlay(OverlayItem overlay)
	 {
		 mOverlays.add(overlay);
		 populate();
	 }
	 
	@Override
	protected OverlayItem createItem(int i) {
	
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	 protected boolean onTap(int index)
	 {
		 dealsList = AppConstant.dealsList;
		 offersList = AppConstant.offersList;
		 final OverlayItem item = mOverlays.get(index);
		 position = index;
		 Vibrator myVib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		 myVib.vibrate(50);
		 if (!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("nearmeMap"))
		 {
			 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			 dialog.setTitle(item.getTitle());
			 dialog.setMessage(item.getSnippet());
			 dialog.setPositiveButton("View Offer",
		              new DialogInterface.OnClickListener() {
		                  public void onClick(DialogInterface dialog, int id) {
		                      dialog.cancel();
		      				Intent intent=null;
		    				dealsDetailVo = dealsList.get(position);
		    				intent =new Intent(mContext,ClaimOfferActivity.class);
		    				intent.putExtra("from", "nearme");
		    				intent.putExtra("selectDeal", dealsDetailVo);
		    				mContext.startActivity(intent);
		    				
		    				}
		              });
			 dialog.show();
		 }
		 else if(!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("myoffersMap"))
		 {
			 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			 dialog.setTitle(item.getTitle());
			 dialog.setMessage(item.getSnippet());
			 dialog.setPositiveButton("View Offer",
		              new DialogInterface.OnClickListener() {
		                  public void onClick(DialogInterface dialog, int id) {
		                      dialog.cancel();
		                    int pos = 0,i = 0;
							for (OffersDetailsVo offersDetailsVo : offersList) {
								if (offersDetailsVo.getAddress().equals(item.getSnippet())) {
									pos = i;
								}
								i++;
							}
		      				Intent intent=null;
		      				Bundle bundle = new Bundle();
		      				offersDetailsVo = offersList.get(pos);
		      				Log.v("selected offer", offersDetailsVo.toString());
		    				intent = new Intent(mContext,MyoffersDetailActivity.class);
		    				bundle.putParcelable("selectOffers", offersDetailsVo);
		    				intent.putExtras(bundle);
		    				mContext.startActivity(intent);
		    				
		    				}
		              });
			 dialog.show();
		 }else{
			 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			 dialog.setTitle(item.getTitle());
			 dialog.setMessage(item.getSnippet());
			 dialog.show();
		 }
		 return true;
	 }

}
