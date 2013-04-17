package com.tangotab.localNotification.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tangotab.appNotification.activity.AppNotificationActivity;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * This class will be used to populate local notification message for Expired offers.
 * 
 * @author Dillip.Lenka
 *
 */
public class LocalNotificationActivity extends Activity
{
	/**
	 * Meta Definitions
	 */
	private static final int DIALOG_ALARM = 0;
	private OffersDetailsVo offersDetailsVo;
	private String offerDate;
	/**
	 * Execution will start here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);			
		/**
		 * Get the offers details from intent  
		 */
	  Bundle bundle = getIntent().getExtras();
	  if (bundle != null)
		{
		  offersDetailsVo= (OffersDetailsVo)bundle.getParcelable("selectOffers");
		}
		showDialog(DIALOG_ALARM);
	}

	// For Showing the Dialog
	protected Dialog onCreateDialog(int id)
	{
		String dealId = offersDetailsVo.getDealId();
		SharedPreferences spc2 = getSharedPreferences("LocalNotification", 0);
		String localDealId = spc2.getString("dealId", "");
		//super.onCreateDialog(id);
		AlertDialog.Builder alert = null;
		AlertDialog dlg =null;
		if(ValidationUtil.isNullOrEmpty(localDealId) || !(localDealId.equals(dealId)))
		{
			//super.onCreateDialog(id);
			alert = new AlertDialog.Builder(this);
			alert.setTitle("TangoTab");
			offerDate = offersDetailsVo.getReserveTimeStamp();
			String formattedOfferDate = DateFormatUtil.getconvertdate(offerDate);
			StringBuilder notifyMessage = new StringBuilder();
			if(!ValidationUtil.isNull(offersDetailsVo))
			{			
				notifyMessage.append("We hope you enjoyed your visit to ").append(offersDetailsVo.getBusinessName()).append(" on ").append(formattedOfferDate).append(". Please let us know if you were able to attend");
			}		
			alert.setMessage(notifyMessage.toString());
			alert.setCancelable(false);
			
			
			// Remove title bar
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	
			// Remove notification bar
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			SharedPreferences spc = getSharedPreferences("LocalNotification", 0);
			SharedPreferences.Editor edit = spc.edit();
			edit.putString("dealId", offersDetailsVo.getDealId());
			edit.commit();
			/**
			 * on click listener added for confirmation Dialog button
			 */
			alert.setPositiveButton("Confirm",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton)
						{
							 /**
					         * Start app notification Activity
					         */
							Bundle bundle = new Bundle();
							bundle.putParcelable("selectOffers", offersDetailsVo);	
					        Intent appNotificationIntent = new Intent(getApplicationContext(),AppNotificationActivity.class);
							appNotificationIntent.putExtras(bundle);
							startActivity(appNotificationIntent);
							LocalNotificationActivity.this.finish();	
						}
					});
	
			/**
			 * on click listener added for Cancel Dialog button
			 */
			alert.setNegativeButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton)
						{
							LocalNotificationActivity.this.finish();						
						}
					});
	
			// Create and return the dialog
			dlg = alert.create();
		}
		
		return dlg;
	}
	
}
