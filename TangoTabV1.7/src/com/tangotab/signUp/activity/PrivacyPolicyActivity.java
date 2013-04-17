package com.tangotab.signUp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.tangotab.R;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * Class for privacy policy activity
 * 
 * <br> class :PrivacyPolicyActivity
 * <br> layout :privacypolicy.xml
 * 
 * @author dillip.lenka
 *
 */
public class PrivacyPolicyActivity extends Activity
{
	private WebView moreWebView=null;	
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.privacypolicy);
				
		Button back=(Button)findViewById(R.id.Back);

		moreWebView = (WebView) findViewById(R.id.webView);
		/**
		 * Load the HTML file
		 */
		moreWebView.loadUrl("file:///android_asset/privacypolicy.html");
		/**
		 * Back button on click listner added.
		 */
		back.setOnClickListener(new OnClickListener()
		{		
			public void onClick(View v) {
				finish();
			}
		});
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		/**
		 * Near me menu button on click listener added.
		 */
		
	}
	
	/**
	 * Added the menu
	 */
	
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
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	        	Intent mainMenuIntent = new Intent(PrivacyPolicyActivity.this,MainMenuActivity.class);
				mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
	            return true;
	        case KeyEvent.KEYCODE_SEARCH:
	        	Intent searchIntent=new Intent(PrivacyPolicyActivity.this, SearchActivity.class);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(searchIntent);
	            return true;
	    }

	    return super.onKeyDown(keycode, e);
	}
}
