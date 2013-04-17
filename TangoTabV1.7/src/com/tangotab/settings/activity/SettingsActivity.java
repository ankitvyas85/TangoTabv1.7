package com.tangotab.settings.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.contactSupport.activity.ContactSupportActvity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.service.FacebookLoginService;
import com.tangotab.login.dao.LoginDao;
import com.tangotab.login.service.LoginService;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.me.activity.MeActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.signUp.vo.UserVo;

/**
 * Setting tab for Sign out and set the Distance information.
 * 
 * <br>
 * Class:SettingsActivity <br>
 * layout :settings
 * 
 * @author dillip.lenka
 * 
 */
public class SettingsActivity extends Activity {

	String homeZipText;
	String workZipText;
	private GoogleAnalyticsTracker tracker;
	private int ZipUpdateFlag = 0;
	private ToggleButton twitterOnOff;
	private ToggleButton facebookOnOff;
	private UserVo userVo;
	private Button updateBtn;

	private SharedPreferences twitterPreferences;
	private SharedPreferences facebookPreferences;

	private SharedPreferences.Editor twitterEditor;
	private SharedPreferences.Editor facebookEditor;

	private SharedPreferences flowprferences;
	private SharedPreferences.Editor flowediEditor;

	ArrayList<Integer> shareflow;

	private boolean isfacebook_automated;
	private boolean istwitter_automated;

	private boolean isfbpopsup;
	private boolean istwitterpopsup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY, 10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SETTING_PAGE);
		tracker.trackEvent("Settings", "TrackEvent", "settings", 1);

		Button topMenu = (Button) findViewById(R.id.topMenuButton);
		Button meButton = (Button) findViewById(R.id.topMeMenuButton);
		Button nearMe = (Button) findViewById(R.id.topNearmeMenuMenuButton);
		updateBtn = (Button) findViewById(R.id.updateBtn);
		Button search = (Button) findViewById(R.id.topSearchMenuButton);
		Button map = (Button) findViewById(R.id.map);

		final EditText homeZip = (EditText) findViewById(R.id.homeZip);
		final EditText workZip = (EditText) findViewById(R.id.workZip);
		twitterOnOff = (ToggleButton) findViewById(R.id.onoffTwitter);
		facebookOnOff = (ToggleButton) findViewById(R.id.onoffFacebook);

		// Focus a particular field according to user action on menu page
		String focusField = getIntent().getStringExtra("FocusField");
		if (!ValidationUtil.isNullOrEmpty(focusField)) {
			if (focusField.equalsIgnoreCase("Home")) {
				homeZip.requestFocus();
			} else if (focusField.equalsIgnoreCase("Work")) {
				workZip.requestFocus();
			}
		}

		// Change update button to search offers if the page is navigated from
		// menu
		updateBtn.setEnabled(false);
		updateBtn.setBackgroundResource(R.drawable.btnblankgreenpressed);
		String fromPage = getIntent().getExtras().getString("frmPage");
		String fromButton = getIntent().getExtras().getString("frmButton");
		if (!ValidationUtil.isNullOrEmpty(fromPage)
				&& fromPage.equalsIgnoreCase("MainMenuActivity")) {
			updateBtn.setText("Search Offers");
		}
		if (!ValidationUtil.isNullOrEmpty(fromButton)) {
			if (fromButton.equalsIgnoreCase("Settings")) {
				updateBtn.setText("Update");
			}
		}

		/**
		 * Get homeZip and workZip from shared preferences.
		 */
		SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
		homeZipText = spc1.getString("postal", "");
		workZipText = spc1.getString("workZip", "");
		homeZip.setText(homeZipText);
		workZip.setText(workZipText);

		SharedPreferences spc = getSharedPreferences("UserName", 0);
		String userName = spc.getString("username", "");
		String password = spc.getString("password", "");
		LoginVo loginVo = new LoginVo();
		loginVo.setUserId(userName);
		loginVo.setPassword(password);
		new UserValidationAsyncTask().execute(loginVo);

		topMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Starting main menu activity
				 */
				Intent mainMenuIntent = new Intent(SettingsActivity.this,
						MainMenuActivity.class);
				mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
			}
		});

		// Enable update button only when text is changed
		homeZip.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!homeZipText.equals(homeZip.getText().toString())) {
					updateBtn.setEnabled(true);
					updateBtn.setBackgroundResource(R.drawable.btnblankgreen);
				} else {
					updateBtn.setEnabled(false);
					updateBtn
							.setBackgroundResource(R.drawable.btnblankgreenpressed);
				}
			}
		});

		workZip.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!workZipText.equals(workZip.getText().toString())) {
					updateBtn.setEnabled(true);
					updateBtn.setBackgroundResource(R.drawable.btnblankgreen);
				} else {
					updateBtn.setEnabled(false);
					updateBtn
							.setBackgroundResource(R.drawable.btnblankgreenpressed);
				}
			}
		});

		/**
		 * Map button on click listener
		 */
		map.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (checkInternetConnection()) {
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

					Geocoder geocoder = new Geocoder(getBaseContext(), Locale
							.getDefault());
					try {
						addressList = geocoder.getFromLocation(
								AppConstant.dev_lat, AppConstant.dev_lang, 1);
					} catch (IOException e) {
						Log.e("Exception:",
								"Exception occuerd at the time getting address list from Geo Coder.");
						e.printStackTrace();
					}

					if (!ValidationUtil.isNullOrEmpty(addressList)) {
						Intent myOfferMapIntent = new Intent(
								getApplicationContext(), MappingActivity.class);
						myOfferMapIntent.putExtra("businessname", addressList
								.get(0).getAddressLine(0));
						myOfferMapIntent.putExtra("dealname", addressList
								.get(0).getAddressLine(0));
						myOfferMapIntent.putExtra("IsFromPlaceOrSearch",
								"mySettings");
						myOfferMapIntent.putExtra("fromPage", "mySettings");
						startActivity(myOfferMapIntent);
					}

				} else
					showDialog(10);
			}
		});
		/*
		 * me button onclick listener.
		 */
		meButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent meIntent = new Intent(SettingsActivity.this,
						MeActivity.class);
				meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(meIntent);
			}
		});

		/*
		 * near me button onclick listener
		 */
		nearMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nearmeIntent = new Intent(SettingsActivity.this,
						NearMeActivity.class);
				nearmeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nearmeIntent);
			}
		});

		/**
		 * Search button onclick listener
		 */
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent searchIntent = new Intent(SettingsActivity.this,
						SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
			}
		});

		updateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if(!homeZipText.equals(homeZip.getText().toString())||!workZipText.equals(workZip.getText().toString())){
				updateBtn.setEnabled(false);
				twitterPreferences = getSharedPreferences(
						AppConstant.TWITTER_PREFERENCES, Context.MODE_PRIVATE);
				facebookPreferences = getSharedPreferences(
						AppConstant.FACEBOOK_PREFERENCES, Context.MODE_PRIVATE);

				flowprferences = getSharedPreferences(
						AppConstant.FLOW_PREFERENCES, Context.MODE_PRIVATE);
				flowediEditor = flowprferences.edit();
				shareflow = new ArrayList<Integer>();
				shareflow.add(AppConstant.CLAIM_OFFER);
				twitterEditor = twitterPreferences.edit();
				facebookEditor = facebookPreferences.edit();
				twitterEditor.clear();
				facebookEditor.clear();

				if ((!facebookOnOff.isChecked()) && (!twitterOnOff.isChecked())) {
					facebookEditor.putString("showicon", "true");
					facebookEditor.putString("showpopup", "true");
					facebookEditor.putString("iconpopup", "true");
					twitterEditor.putString("showicon", "true");
					twitterEditor.putString("showpopup", "true");
					twitterEditor.putString("iconpopup", "true");
					facebookEditor.commit();
					twitterEditor.commit();
					shareflow.add(AppConstant.FACEBOOK_SHARE);
					shareflow.add(AppConstant.TWITTER_SHARE);
				} else {

					if (!facebookOnOff.isChecked()) {
						facebookEditor.putString("showicon", "true");
						facebookEditor.putString("showpopup", "false");
						facebookEditor.putString("iconpopup", "true");
						facebookEditor.commit();
					} else {

						if (isfacebook_automated) {
							facebookEditor.putString("isfacebookon", "true");
							facebookEditor.putString("showicon", "false");
							facebookEditor.putString("showpopup", "false");
							facebookEditor.putString("iconpopup", "false");
							facebookEditor.putString("iconfacebookon", "true");
							isfbpopsup = false;
							shareflow.add(AppConstant.FACEBOOK_SHARE);
							facebookEditor.commit();
						} else {
							facebookEditor.putString("isfacebookon", "false");
							facebookEditor.putString("showicon", "true");
							facebookEditor.putString("showpopup", "false");
							facebookEditor.putString("iconpopup", "false");
							facebookEditor.putString("iconfacebookon", "true");
							isfbpopsup = false;
							facebookEditor.commit();
						}
					}

					if (!twitterOnOff.isChecked()) {
						twitterEditor.putString("showicon", "true");
						twitterEditor.putString("showpopup", "false");
						twitterEditor.putString("iconpopup", "true");
						twitterEditor.commit();
					} else {

						if (istwitter_automated) {
							twitterEditor.putString("istwitteron", "true");
							twitterEditor.putString("showicon", "false");
							twitterEditor.putString("showpopup", "false");
							twitterEditor.putString("iconpopup", "false");
							twitterEditor.putString("icontwitteron", "true");
							istwitterpopsup = false;
							shareflow.add(AppConstant.TWITTER_SHARE);
							twitterEditor.commit();
						} else {
							twitterEditor.putString("istwitteron", "false");
							twitterEditor.putString("showicon", "true");
							twitterEditor.putString("showpopup", "false");
							twitterEditor.putString("iconpopup", "false");
							twitterEditor.putString("icontwitteron", "true");
							istwitterpopsup = false;
							twitterEditor.commit();
						}

					}
				}
				TangoTabBaseApplication.getInstance().setShareflow(shareflow);

				if (isfbpopsup || istwitterpopsup)
					TangoTabBaseApplication.isdialogpopsup = true;
				else
					TangoTabBaseApplication.isdialogpopsup = false;

				SharedPreferences spc2 = getSharedPreferences("UserName", 0);
				String userName = spc2.getString("username", "");

				String homeZipCode = homeZip.getText().toString();
				String workZipCode = workZip.getText().toString();

				Pattern usPattern = Pattern.compile("(^\\d{5}(-\\d{4})?$)");
				Pattern caPattern = Pattern
						.compile("(^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]{1}\\d{1}[A-Za-z]{1} *\\d{1}[A-Za-z]{1}\\d{1}$)");

				Matcher usHomeZipMatcher = usPattern.matcher(homeZipCode);
				Matcher usWorkZipMatcher = usPattern.matcher(workZipCode);

				Matcher caHomeZipMatcher = caPattern.matcher(homeZipCode);
				Matcher caWorkZipMatcher = caPattern.matcher(workZipCode);

				if (ValidationUtil.isNullOrEmpty(homeZipCode)) {
					showDialog(4);
				} else if (ValidationUtil.isNullOrEmpty(workZipCode)) {
					showDialog(5);
				} else {
					if ((usHomeZipMatcher.matches() || caHomeZipMatcher
							.matches())
							&& (usWorkZipMatcher.matches() || caWorkZipMatcher
									.matches())) {
						if (checkInternetConnection()) {
							if (!isValidHomeZip(homeZipCode, usHomeZipMatcher,
									caHomeZipMatcher)) {
								showDialog(2);
							} else if (!isValidWorkZip(workZipCode,
									usWorkZipMatcher, caWorkZipMatcher)) {
								showDialog(3);
							} else {
								/**
								 * Get all the sign up informations from the
								 * user.
								 */

								if (!ValidationUtil.isNullOrEmpty(userName)) {
									homeZipText = homeZipCode;
									workZipText = workZipCode;
									new UpdateZipCodeAsyncTask().execute(
											userName, homeZipCode, workZipCode);
								}
							}
						} else {
							showDialog(15);
						}
					} else {
						if (!usHomeZipMatcher.matches()
								&& !caHomeZipMatcher.matches())
							showDialog(2);
						if (!usWorkZipMatcher.matches()
								&& !caWorkZipMatcher.matches())
							showDialog(3);

					}
				}
				// }
			}

			private boolean isValidWorkZip(String workZipCode,
					Matcher usWorkZipMatcher, Matcher caWorkZipMatcher) {
				if (usWorkZipMatcher.matches()) {
					if (ValidationUtil.isValidUSZip(workZipCode)) {
						return true;
					} else {
						return false;
					}
				}

				if (caWorkZipMatcher.matches()) {
					if (ValidationUtil.isValidZip(workZipCode)) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}

			private boolean isValidHomeZip(String homeZipCode,
					Matcher usHomeZipMatcher, Matcher caHomeZipMatcher) {
				if (usHomeZipMatcher.matches()) {
					if (ValidationUtil.isValidUSZip(homeZipCode)) {
						return true;
					} else {
						return false;
					}
				}

				if (caHomeZipMatcher.matches()) {
					if (ValidationUtil.isValidZip(homeZipCode)) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
		});

		/*
		 * Twitter on off listener
		 */
		twitterOnOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn.setEnabled(true);
				updateBtn.setBackgroundResource(R.drawable.btnblankgreen);
				if (twitterOnOff.isChecked())
					showDialog(60);
			}
		});
		/*
		 * Facebook on off listener
		 */
		facebookOnOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn.setEnabled(true);
				updateBtn.setBackgroundResource(R.drawable.btnblankgreen);
				if (facebookOnOff.isChecked())
					showDialog(50);
			}
		});

		// Home ZipCode focus change listener
		homeZip.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				ZipUpdateFlag = 1;
			}
		});

		// Work ZipCode focus change listener
		workZip.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				ZipUpdateFlag = 2;
			}
		});

	}

	/**
	 * Async task for updating zipcode.
	 * 
	 * @author lakshmipathi.p
	 * 
	 */
	public class UpdateZipCodeAsyncTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SettingsActivity.this,
					"Updating ", "Please wait...");
			progressDialog.setCancelable(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				FacebookLoginService facebookLoginService = new FacebookLoginService();
				facebookLoginService.updateZipForUser(params[0], params[1],
						params[2]);
				String facebook = "false";
				String twitter = "false";
				if (twitterOnOff.isChecked())
					twitter = "true";
				if (facebookOnOff.isChecked())
					facebook = "true";
				facebookLoginService.updateSocialPreferenceUser(params[0],
						facebook, twitter);
				SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("postal", params[1]);
				edits.putString("workZip", params[2]);
				edits.commit();
			} catch (Exception e) {
				Log.e("Exception occured",
						"Exception occured at the time of updating Zip", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateBtn.setEnabled(true);
			progressDialog.dismiss();

			Log.v("ZipFlag", "" + ZipUpdateFlag);
			String fromPage = getIntent().getExtras().getString("frmPage");
			if (!ValidationUtil.isNullOrEmpty(fromPage)
					&& fromPage.equalsIgnoreCase("MeActivity")) {
				Intent meIntent = new Intent(SettingsActivity.this,
						MeActivity.class);
				meIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(meIntent);

			} else if (!ValidationUtil.isNullOrEmpty(fromPage)
					&& fromPage.equalsIgnoreCase("MainMenuActivity")) {
				/*
				 * Intent mainMenuIntent=new
				 * Intent(SettingsActivity.this,MainMenuActivity.class);
				 * mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 * startActivity(mainMenuIntent); } else {
				 */
				Intent nearMeIntent = new Intent(SettingsActivity.this,
						NearMeActivity.class);
				nearMeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (ZipUpdateFlag == 1) {
					nearMeIntent.putExtra("SearchBy", "HomeZip");
				} else if (ZipUpdateFlag == 2) {
					nearMeIntent.putExtra("SearchBy", "WorkZip");
				}
				startActivity(nearMeIntent);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case 1:
			AlertDialog.Builder zipUpdateDialog = new AlertDialog.Builder(
					SettingsActivity.this);
			zipUpdateDialog.setTitle("TangoTab");
			zipUpdateDialog
					.setMessage("Home or Work Zip/Postal code updated successfully.");
			zipUpdateDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return zipUpdateDialog.create();
		case 2:
			AlertDialog.Builder homeZipErrDialog = new AlertDialog.Builder(
					SettingsActivity.this);
			homeZipErrDialog.setTitle("TangoTab");
			homeZipErrDialog.setMessage("Please provide proper home zipcode.");
			homeZipErrDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return homeZipErrDialog.create();

		case 3:
			AlertDialog.Builder workZipErrDialog = new AlertDialog.Builder(
					SettingsActivity.this);
			workZipErrDialog.setTitle("TangoTab");
			workZipErrDialog.setMessage("Please provide proper zipcode.");
			workZipErrDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return workZipErrDialog.create();
		case 4:
			AlertDialog.Builder homeZipEmptyDialog = new AlertDialog.Builder(
					SettingsActivity.this);
			homeZipEmptyDialog.setTitle("TangoTab");
			homeZipEmptyDialog.setMessage("Please provide Home zipcode.");
			homeZipEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return homeZipEmptyDialog.create();

		case 5:
			AlertDialog.Builder workZipEmptyDialog = new AlertDialog.Builder(
					SettingsActivity.this);
			workZipEmptyDialog.setTitle("TangoTab");
			workZipEmptyDialog.setMessage("Please provide Work zipcode.");
			workZipEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return workZipEmptyDialog.create();
		case 10:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(
					SettingsActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});
			return ab3.create();
		case 15:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(
					SettingsActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab15.create();

		case 50:
			AlertDialog.Builder facebook = null;
			AlertDialog dlg = null;
			facebook = new AlertDialog.Builder(this);
			facebook.setTitle("TangoTab");
			facebook.setMessage("Enable the automated facebook sharing?");
			facebook.setCancelable(false);
			/**
			 * on click listener added for confirmation Dialog button
			 */
			facebook.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							SharedPreferences automatedSharing = getSharedPreferences(
									"automatedSharing", 0);
							SharedPreferences.Editor edits = automatedSharing
									.edit();
							edits.putBoolean("facebookSharing", true);
							edits.commit();
							dialog.dismiss();
							isfacebook_automated = true;
						}
					});

			/**
			 * on click listener added for Cancel Dialog button
			 */
			facebook.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							SharedPreferences automatedSharing = getSharedPreferences(
									"automatedSharing", 0);
							SharedPreferences.Editor edits = automatedSharing
									.edit();
							edits.putBoolean("facebookSharing", false);
							edits.commit();
							dialog.dismiss();
						}
					});
			return dlg = facebook.create();

		case 60:
			AlertDialog.Builder twiter = null;
			AlertDialog dlg1 = null;
			twiter = new AlertDialog.Builder(this);
			twiter.setTitle("TangoTab");
			twiter.setMessage("Enable the automated twitter sharing?");
			twiter.setCancelable(false);
			/**
			 * on click listener added for confirmation Dialog button
			 */
			twiter.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							SharedPreferences automatedSharing = getSharedPreferences(
									"automatedSharing", 0);
							SharedPreferences.Editor edits = automatedSharing
									.edit();
							edits.putBoolean("twitterSharing", true);
							edits.commit();
							dialog.dismiss();
							istwitter_automated = true;
						}
					});

			/**
			 * on click listener added for Cancel Dialog button
			 */
			twiter.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							SharedPreferences automatedSharing = getSharedPreferences(
									"automatedSharing", 0);
							SharedPreferences.Editor edits = automatedSharing
									.edit();
							edits.putBoolean("twitterSharing", false);
							edits.commit();
							dialog.dismiss();
						}
					});
			return dlg1 = twiter.create();

		}

		return null;
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

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			Intent mainMenuIntent = new Intent(SettingsActivity.this,
					MainMenuActivity.class);
			mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainMenuIntent);
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			Intent searchIntent = new Intent(SettingsActivity.this,
					SearchActivity.class);
			searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(searchIntent);
			return true;
		}

		return super.onKeyDown(keycode, e);
	}

	/**
	 * User validation asynctask used for to get authnticate for new user..
	 * 
	 * @author dillip.lenka
	 * 
	 */
	public class UserValidationAsyncTask extends
			AsyncTask<LoginVo, Void, UserVo> {
		private ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(SettingsActivity.this, "Connecting",
					"Please Wait...");
			mDialog.setCancelable(true);
		}

		@Override
		protected UserVo doInBackground(LoginVo... params) {
			String message = null;
			try {
				LoginService loginService = new LoginService();
				message = loginService.doSignIn(params[0]);
				if (ValidationUtil.isNullOrEmpty(message)) {
					List<UserVo> userVoList = LoginDao.userVoList;
					userVo = userVoList.get(0);
				}
			} catch (ConnectTimeoutException e) {
				Log.e("ConnectTimeoutException occured",
						"ConnectTimeoutException occured at the time of login",
						e);
				message = null;
				Intent contactIntent = new Intent(getApplicationContext(),
						ContactSupportActvity.class);
				startActivity(contactIntent);
			} catch (Exception e) {
				Log.e("Exception occured",
						"Exception occured at the time of login", e);
				message = null;
			}

			return userVo;
		}

		@Override
		protected void onPostExecute(UserVo userVo) {
			mDialog.dismiss();
			if (userVo != null) {
				String faceBook = userVo.getFacebook_share();
				String twitterShare = userVo.getTwitter_share();
				if (!ValidationUtil.isNullOrEmpty(faceBook)
						&& faceBook.equalsIgnoreCase("true")) {
					facebookOnOff.setChecked(true);
				} else {
					facebookOnOff.setChecked(false);
				}
				if (!ValidationUtil.isNullOrEmpty(twitterShare)
						&& twitterShare.equalsIgnoreCase("true")) {
					twitterOnOff.setChecked(true);
				} else {
					twitterOnOff.setChecked(false);
				}
			}
			SharedPreferences automatedSharing = getSharedPreferences(
					"automatedSharing", 0);
			SharedPreferences.Editor edits = automatedSharing.edit();
			edits.putBoolean("facebookSharing", facebookOnOff.isChecked());
			edits.putBoolean("twitterSharing", twitterOnOff.isChecked());
			edits.commit();
		}

	}
}