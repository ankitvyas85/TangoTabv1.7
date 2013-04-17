package com.tangotab.appNotification.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOfferDetails.service.MyOffersDetailService;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * class will be used for get App notifications for expired offers and for all local notifications.
 * 
 * @author Dillip.Lenka
 *
 */
public class AppNotificationActivity extends Activity
{
	private static final int DIALOG_ALARM = 0;

		
	private OffersDetailsVo offersDetailsVo;	
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
				
		 Bundle bundle = getIntent().getExtras();
		
		if (bundle != null)
		{
			/**
			 * Retrieve the Offers Details Vo  object from the intent.
			 */
			offersDetailsVo= (OffersDetailsVo)bundle.getParcelable("selectOffers");
		}
		
		// Get the alarm ID from the intent extra data
		showDialog(DIALOG_ALARM);
	}

	/**
	 * Method will be used for display dialog boxes.
	 */
	protected Dialog onCreateDialog(int id)
	{
		//super.onCreateDialog(id);
		// Build the dialog
		String dealId = offersDetailsVo.getDealId();
		SharedPreferences spc2 = getSharedPreferences("AppNotification", 0);
		String localDealId = spc2.getString("dealId", "");
		//super.onCreateDialog(id);
		AlertDialog.Builder alert = null;
		AlertDialog dlg =null;
		if(ValidationUtil.isNullOrEmpty(localDealId) || !(localDealId.equals(dealId)))
		{
		alert = new AlertDialog.Builder(this);
		alert.setTitle("App Notification");
		String offerDate=offersDetailsVo.getReserveTimeStamp();
		String formattedOfferDate=DateFormatUtil.getconvertdate(offerDate);
		if(!ValidationUtil.isNull(offersDetailsVo))
		{
			StringBuilder notifyMessage = new StringBuilder();
			notifyMessage.append("We hope you enjoyed your visit to ").append(offersDetailsVo.getBusinessName()).append(" on ").append(formattedOfferDate).append(".\r\n").append("Were you able to attend?");
			alert.setMessage(notifyMessage.toString());
		}
		alert.setCancelable(false);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/**
		 * put the dela id into SharedPreferences
		 */
		SharedPreferences spc = getSharedPreferences("AppNotification", 0);
		SharedPreferences.Editor edit = spc.edit();
		edit.putString("dealId", offersDetailsVo.getDealId());
		edit.commit();
		
		// Dialog Yes Button confirmation
		alert.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						/**
						 * Check in the offer after the app notification
						 */
						if(!ValidationUtil.isNull(offersDetailsVo))
						{
							
							doCheckIn("A");
						}
						AppNotificationActivity.this.finish();
					}
				});

		/**
		 * Dialog No Button confirmation
		 */
		alert.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						/**
						 * Check in the offer after the appNotification
						 */				
						if(!ValidationUtil.isNull(offersDetailsVo))
						{
							doCheckIn("NA");
						}
						AppNotificationActivity.this.finish();
					}
				});

		// Create and return the dialog
			dlg = alert.create();
		}
		return dlg;
	}
	
	
	
	/**
	 * This method will take care all the check in functionality.
	 * 
	 * @param autoCheckIn
	 */
	private void doCheckIn(String status)
	{

		if (checkInternetConnection())
		{
			//Get all user details from sharedPreferences.				
				SharedPreferences spc = getSharedPreferences("UserDetails", 0);
				String firstName = spc.getString("firstName", "");
				String lastName = spc.getString("lastName", "");
				offersDetailsVo.setFirstName(firstName);
				offersDetailsVo.setLastName(lastName);
				offersDetailsVo.setAutoCheckIn(status);
				/** 
				 * Call the asyntask to execute the service
				 */
				new InsertDealAsyncTask().execute(offersDetailsVo);
		
		}	
			
	}
/**
 * This call will be used to run the check in service in different thread.
 * 
 * @author dillip.lenka
 *
 */
	class InsertDealAsyncTask extends AsyncTask<OffersDetailsVo, Void, Void>
	{		
		
		@Override
		protected Void doInBackground(OffersDetailsVo... offersDetailsVo)
		{
			try{
				MyOffersDetailService service = new MyOffersDetailService();
				service.checkIn(offersDetailsVo[0]);
			}
			catch (Exception e)
			{
				Log.e("Error", "Error ocuuered in invoking check in service url and detailurl =",e);
			}
			return null;
		}	
       @Override
    protected void onPostExecute(Void result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    SharedPreferences spc = getSharedPreferences("AppNotification", 0);
	SharedPreferences.Editor edit = spc.edit();
	edit.putBoolean("isCheckInOccured",true);
	edit.commit();
	
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
}
