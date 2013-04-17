package com.tangotab.search.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tangotab.R;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.search.adapter.CustomWheelAdapter;

/**
 * 
 * @author Lakshmipathi.P
 * 
 */
public class LocationActivity extends Activity {

	// Scrolling flag
	private boolean scrolling = false;
	private int mNewIndex;
	final String locations[] = new String[] { "Current Location", "Home",
			"Work", "Alternate" };
	private String alternateZipfromSearch;
	private EditText alternateZip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
		Button doneButton = (Button) findViewById(R.id.doneButton);

		final WheelView locWheelView = (WheelView) findViewById(R.id.locWheelView);
		final TextView homeZipText = (TextView) findViewById(R.id.homeZipTextView);
		final EditText homeZip = (EditText) findViewById(R.id.homeZip);
		final EditText workZip = (EditText) findViewById(R.id.workZip);
		alternateZip = (EditText) findViewById(R.id.alternateZip);
		// String alternateZipSearch=alternateZip.getText().toString();

		SharedPreferences spf = getSharedPreferences("alternateZipCode", 0);
		String alternateZipCode = spf.getString("alternateZipCode", "");

		SharedPreferences aspf = getSharedPreferences("Alternate", 0);
		alternateZipfromSearch = aspf.getString("alternateZipfromSearch", "");
		
		homeZipText.setVisibility(View.GONE);
		homeZip.setVisibility(View.GONE);

		locWheelView.setViewAdapter(new LocationAdapter(this));
		locWheelView.setVisibleItems(3);
		homeZip.setText(spc1.getString("postal", ""));
		workZip.setText(spc1.getString("workZip", ""));
		/*
		 * Visibility of location Field.
		 */

		SharedPreferences spc2 = getSharedPreferences("locationDetail", 0);
		int RollValue =0;
		if(!ValidationUtil.isNullOrEmpty(spc2.getString("wheelIndex", "")))
			RollValue = Integer.parseInt(spc2.getString("wheelIndex", ""));

		if (RollValue == 3) {
			homeZipText.setVisibility(View.VISIBLE);
			homeZipText.setText("Alterate Zip/Postal");
			alternateZip.setVisibility(View.VISIBLE);
			workZip.setVisibility(View.GONE);
			homeZip.setVisibility(View.GONE);
		} else if (RollValue == 0) {
			homeZipText.setVisibility(View.GONE);
			homeZipText.setText("Alterate Location");
			alternateZip.setVisibility(View.GONE);
			workZip.setVisibility(View.GONE);
			homeZip.setVisibility(View.GONE);
		} else if (RollValue == 1) {
			homeZipText.setVisibility(View.GONE);
			homeZipText.setText("Alterate Location");
			alternateZip.setVisibility(View.GONE);
			workZip.setVisibility(View.GONE);
			homeZip.setVisibility(View.GONE);
		} else if (RollValue == 2) {
			homeZipText.setVisibility(View.GONE);
			homeZipText.setText("Alterate Location");
			alternateZip.setVisibility(View.GONE);
			workZip.setVisibility(View.GONE);
			homeZip.setVisibility(View.GONE);
		}

		if (!ValidationUtil.isNullOrEmpty(alternateZipCode)
				&& ValidationUtil.isNullOrEmpty(alternateZipfromSearch)) {
			alternateZip.setText(alternateZipCode);
		} else {
			alternateZip.setText(alternateZipfromSearch);
		}
		String wheelIndex = spc1.getString("wheelIndex", "");
		if (!ValidationUtil.isNullOrEmpty(wheelIndex))
			locWheelView.setCurrentItem(Integer.parseInt(wheelIndex));
		else
			locWheelView.setCurrentItem(0);

		String selLoc = getIntent().getStringExtra("selectedLoc");
		for (int count = 0; count < locations.length; count++) {
			if (selLoc.equals(locations[count])) {
				mNewIndex = count;
				break;
			}
		}

		/**
		 * wheel view onchange listener.
		 */
		locWheelView.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					populateRadius(wheel, locations, newValue);
				}
			}
		});

		/**
		 * wheel view scroll listener
		 */
		locWheelView.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				populateRadius(wheel, locations, wheel.getCurrentItem());
				mNewIndex = wheel.getCurrentItem();
				if (mNewIndex == 3) {

					homeZipText.setVisibility(View.VISIBLE);
					homeZipText.setText("Alterate Zip/Postal");
					alternateZip.setVisibility(View.VISIBLE);
					workZip.setVisibility(View.GONE);
					homeZip.setVisibility(View.GONE);
				} else if (mNewIndex == 2) {
					homeZipText.setVisibility(View.GONE);
					homeZipText.setText("Work Zip/Postal");
					workZip.setVisibility(View.GONE);
					alternateZip.setVisibility(View.GONE);
					homeZip.setVisibility(View.GONE);
				} else if (mNewIndex == 1) {
					homeZipText.setVisibility(View.GONE);
					homeZipText.setText("Home Zip/Postal");
					homeZip.setVisibility(View.GONE);
					alternateZip.setVisibility(View.GONE);
					workZip.setVisibility(View.GONE);
				} else if (mNewIndex == 0) {
					homeZipText.setVisibility(View.GONE);
					alternateZip.setVisibility(View.GONE);
					workZip.setVisibility(View.GONE);
					homeZip.setVisibility(View.GONE);
				}
			}
		});

		locWheelView.setCurrentItem(1);
		locWheelView.setCurrentItem(0);
		locWheelView.setCurrentItem(mNewIndex);

		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String homeZipCode = homeZip.getText().toString();
				String workZipCode = workZip.getText().toString();
				alternateZipfromSearch = alternateZip.getText().toString();
				SharedPreferences spc1 = getSharedPreferences("locationDetail",
						0);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("workZip", workZipCode);
				edits.putString("homeZip", homeZipCode);
				edits.putString("currentLocation", locations[mNewIndex]);
				edits.putString("wheelIndex", String.valueOf(mNewIndex));
				edits.commit();

				/**
				 * put all the user information into the shared preferences
				 */
				SharedPreferences spc2 = getSharedPreferences("UserDetails", 0);
				SharedPreferences.Editor edits2 = spc2.edit();
				edits2.putString("postal", homeZipCode);
				edits2.putString("workZip", workZipCode);
				edits2.putString("alternateZip", alternateZipfromSearch);
				edits2.commit();
				
				/*
				 * edits2.putString("alternateZipfromSearch",
				 * alternateZipfromSearch); edits2.commit();
				 */

				/*
				 * SharedPreferences aspf=getSharedPreferences("Alternate", 0);
				 * SharedPreferences.Editor aspfeditor = aspf.edit();
				 * aspfeditor.putString("alternateZipfromSearch",
				 * alternateZipfromSearch); aspfeditor.commit();
				 */

				if (mNewIndex == 3) {
					Pattern pattern = Pattern
							.compile("(^\\d{5}(-\\d{4})?$)|(^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]{1}\\d{1}[A-Za-z]{1} *\\d{1}[A-Za-z]{1}\\d{1}$)");
					/*
					 * Matcher homeZipMatcher = pattern.matcher(homeZipCode);
					 * Matcher workZipMatcher = pattern.matcher(workZipCode);
					 */
					Matcher alternateMatcher = pattern
							.matcher(alternateZipfromSearch);
					if (ValidationUtil.isNullOrEmpty(alternateZipfromSearch)) {
						showDialog(1);
					} else if (!ValidationUtil
							.isNullOrEmpty(alternateZipfromSearch)) {
						if (!alternateMatcher.matches()) {
							showDialog(1);

						} else {
							SharedPreferences aspf = getSharedPreferences(
									"Alternate", 0);
							SharedPreferences.Editor aspfeditor = aspf.edit();
							aspfeditor.putString("alternateZipfromSearch",
									alternateZipfromSearch);
							aspfeditor.commit();
							Intent searchIntent = new Intent(
									LocationActivity.this, SearchActivity.class);
							startActivity(searchIntent);
						}

						/*
						 * if(!ValidationUtil.isNullOrEmpty(homeZipCode) &&
						 * !ValidationUtil.isNullOrEmpty(workZipCode)&&
						 * (!ValidationUtil
						 * .isNullOrEmpty(alternateZipfromSearch))){
						 * if(!homeZipMatcher.matches()){ showDialog(1); }else
						 * if(!workZipMatcher.matches()){ showDialog(2); }else
						 * if(!alternateMatcher.matches()){ showDialog(5); }else
						 * { Intent resultIntent = new Intent();
						 * resultIntent.putExtra("locationSel",
						 * locations[mNewIndex]);
						 * resultIntent.putExtra("Zip_Code", homeZipCode);
						 * resultIntent.putExtra("Work_Zip", workZipCode);
						 * setResult(RESULT_OK, resultIntent); finish(); Intent
						 * searchIntent=new Intent(LocationActivity.this,
						 * SearchActivity.class);
						 * searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						 * ); searchIntent.putExtra("fromLoaction",
						 * "LocationChange"); startActivity(searchIntent); }
						 * }else if(ValidationUtil.isNullOrEmpty(homeZipCode)){
						 * showDialog(3); }else
						 * if(ValidationUtil.isNullOrEmpty(workZipCode)){
						 * showDialog(4); }
						 */
					} /*
					 * else { Intent searchIntent = new
					 * Intent(LocationActivity.this, SearchActivity.class);
					 * startActivity(searchIntent); }
					 */
				} else {
					Intent searchIntent = new Intent(LocationActivity.this,
							SearchActivity.class);
					startActivity(searchIntent);
				}
			}
		});

	}

	/**
	 * Populate the radius wheel
	 */
	private void populateRadius(WheelView radiusWheel, String[] locations,
			int index) {
		CustomWheelAdapter adapter = new CustomWheelAdapter(this, locations);
		adapter.setTextSize(17);
		radiusWheel.setViewAdapter(adapter);
		radiusWheel.setCurrentItem(index);
	}

	/*
	 * location Adapter for distance location in search page.
	 * 
	 * @author Lakshmipathi.P
	 */
	private class LocationAdapter extends AbstractWheelTextAdapter {
		/**
		 * Constructor
		 */
		protected LocationAdapter(Context context) {
			super(context, R.layout.distancetext, NO_RESOURCE);

			setItemTextResource(R.id.radiusText);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);

			return view;
		}

		@Override
		public int getItemsCount() {
			return locations.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return locations[index];
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			AlertDialog.Builder homeZipErrDialog = new AlertDialog.Builder(
					LocationActivity.this);
			homeZipErrDialog.setTitle("TangoTab");
			homeZipErrDialog.setMessage("Invalid Alternate Zip code.");
			homeZipErrDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return homeZipErrDialog.create();

		case 2:
			AlertDialog.Builder workZipErrDialog = new AlertDialog.Builder(
					LocationActivity.this);
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
					LocationActivity.this);
			homeZipEmptyDialog.setTitle("TangoTab");
			homeZipEmptyDialog.setMessage("Please Specify Home Zip code.");
			homeZipEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return homeZipEmptyDialog.create();
		case 5:
			AlertDialog.Builder alternateZipDialog = new AlertDialog.Builder(
					LocationActivity.this);
			alternateZipDialog.setTitle("TangoTab");
			alternateZipDialog.setMessage("Please Enter a Valid Zip");
			alternateZipDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return alternateZipDialog.create();

		case 4:
			AlertDialog.Builder workZipEmptyDialog = new AlertDialog.Builder(
					LocationActivity.this);
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

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			Intent mainMenuIntent = new Intent(LocationActivity.this,
					MainMenuActivity.class);
			mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainMenuIntent);
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			Intent searchIntent = new Intent(LocationActivity.this,
					SearchActivity.class);
			searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(searchIntent);
			return true;
		}

		return super.onKeyDown(keycode, e);
	}

}
