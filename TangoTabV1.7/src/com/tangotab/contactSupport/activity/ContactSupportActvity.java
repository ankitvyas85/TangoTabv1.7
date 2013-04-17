package com.tangotab.contactSupport.activity;

import com.tangotab.core.constant.AppConstant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
/**
 * Class will be used for contact support if any web service call taking more than 10 seconds.
 * 
 * @author Dillip.Lenka
 *
 */
public class ContactSupportActvity extends Activity
{
	private static final int SUPPORT = 0;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		showDialog(SUPPORT);
	}

	/**
	 * Method will be used for display dialog boxes.
	 */
	protected Dialog onCreateDialog(int id)
	{
		super.onCreateDialog(id);
		// Build the dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Contact Support");
		StringBuilder notifyMessage = new StringBuilder();
		notifyMessage.append("We apologize for the inconvenience, but we are having technical difficulties in reaching our servers.  If you receive this frequently, please contact support. ");
		alert.setMessage(notifyMessage.toString());
		alert.setCancelable(false);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// Dialog Yes Button confirmation
		alert.setPositiveButton("Contact Support",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
						String locLat = currentLocation.getString("locLat", "");
						String locLong = currentLocation.getString("locLong", "");
						
						SharedPreferences spc1 = getSharedPreferences("Distance", 0);
						String dist = spc1.getString("distancevalue", "");
						
						SharedPreferences spc = getSharedPreferences("UserName", 0);
						String userId = spc.getString("username", "");
						/**
						 * Generate Email to send to the contact Team.
						 */
						String emailId =AppConstant.SUPPORT_EMAIL_ID;
						String subject =AppConstant.EMAIL_SUBJECT;
						StringBuilder body = new StringBuilder();
						body.append("Information for Support:").append("\r\n").append("Location: <").append(locLat).append(",").append(locLong).append(" >")
						.append("\r\n").append("Search Radius Settings: <").append(dist).append(" >").append("\r\n").append("User ID: <").append(userId).append(" >");
						/**
						 * Send email to contact support team
						 */
						sendMailAppNotification(emailId,subject,body.toString());
						ContactSupportActvity.this.finish();
					}
				});

		/**
		 * Dialog Dismiss Button confirmation
		 */
		alert.setNegativeButton("Dismiss",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						ContactSupportActvity.this.finish();
					}
				});

		// Create and return the dialog
		AlertDialog dlg = alert.create();
		return dlg;
	}
	/**
	 * Send email to contact support.
	 * @param mailId
	 * @param subject
	 * @param body
	 */
	private void sendMailAppNotification(String mailId,String subject,String body)
	{
		
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[] { mailId });
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, body);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));	            
        Log.v("Sending Mail", "Completed");
	}
	
}
