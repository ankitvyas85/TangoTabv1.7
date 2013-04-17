package com.tangotab.facebook.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Session;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.ValidationUtil;

/**
 * Get Zip code from face book login.
 * 
 * Class Name :ZipActivity
 * XML Use :show_offers_near.xml
 * @author dillip.lenka
 *
 */
public class ZipActivity extends Activity 
{
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showoffersnear);

		Button createNewAccount = (Button) findViewById(R.id.New_Acc);
		final EditText homeZip = (EditText) findViewById(R.id.home_zip);
		final EditText workZip = (EditText) findViewById(R.id.work_zip);
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.ZIP_ACTIVITY);
		tracker.trackEvent("ZipActivity", "TrackEvent", "zipactivity", 1);
		
		/**
		 * On click listener for create new Account
		 */
		createNewAccount.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				myVib.vibrate(50);
				
				String homeZipCode = homeZip.getText().toString();
				String workZipCode = workZip.getText().toString();
				Pattern pattern = Pattern.compile("(^\\d{5}(-\\d{4})?$)|(^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]{1}\\d{1}[A-Za-z]{1} *\\d{1}[A-Za-z]{1}\\d{1}$)");
				Matcher homeZipMatcher = pattern.matcher(homeZipCode);
				Matcher workZipMatcher = pattern.matcher(workZipCode);
				if(!ValidationUtil.isNullOrEmpty(homeZipCode) && !ValidationUtil.isNullOrEmpty(workZipCode)){
					if(!homeZipMatcher.matches()){
						showDialog(1);
					}else if(!workZipMatcher.matches()){
						showDialog(2);
					}else{
						Intent resultIntent = new Intent();
						resultIntent.putExtra("Zip_Code", homeZipCode);
						setResult(RESULT_OK, resultIntent);
						finish();
					}
				}else if(ValidationUtil.isNullOrEmpty(homeZipCode)){
					showDialog(3);
				}else if(ValidationUtil.isNullOrEmpty(workZipCode)){
					showDialog(4);
				}
			}
		});

	}
	@Override
	public void onBackPressed() {
		Session session = Session.getActiveSession();
        if (!ValidationUtil.isNull(session) && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
		super.onBackPressed();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			AlertDialog.Builder homeZipErrDialog = new AlertDialog.Builder(
					ZipActivity.this);
			homeZipErrDialog.setTitle("TangoTab");
			homeZipErrDialog.setMessage("Invalid Home Zip code.");
			homeZipErrDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return homeZipErrDialog.create();

		case 2:
			AlertDialog.Builder workZipErrDialog = new AlertDialog.Builder(
					ZipActivity.this);
			workZipErrDialog.setTitle("TangoTab");
			workZipErrDialog.setMessage("Invalid Work Zip code.");
			workZipErrDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return workZipErrDialog.create();
		case 3:
			AlertDialog.Builder homeZipEmptyDialog = new AlertDialog.Builder(
					ZipActivity.this);
			homeZipEmptyDialog.setTitle("TangoTab");
			homeZipEmptyDialog.setMessage("Please Specify Home Zip code.");
			homeZipEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return homeZipEmptyDialog.create();

		case 4:
			AlertDialog.Builder workZipEmptyDialog = new AlertDialog.Builder(
					ZipActivity.this);
			workZipEmptyDialog.setTitle("TangoTab");
			workZipEmptyDialog.setMessage("Please Specify Work Zip code.");
			workZipEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return workZipEmptyDialog.create();
		}
		return null;
	}
}
