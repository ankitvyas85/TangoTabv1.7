package com.tangotab.signUp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * <br> Class : TermsOfUseActivity
 * <br> Layout :terms.xml
 * 
 * @author dillip.lenka
 *
 */
public class TermsOfUseActivity extends Activity
{
	private WebView moreWebView=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.terms);		
		
		Button back=(Button)findViewById(R.id.Back);	
	
		// Refer to Buttons from Layout
		moreWebView = (WebView) findViewById(R.id.webView);
		moreWebView.loadUrl("file:///android_asset/terms.html");
		back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{				
				finish();
			}
		});
	}
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e)
	 {
		    switch(keycode)
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(TermsOfUseActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(TermsOfUseActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true;    
		    }
		    return super.onKeyDown(keycode, e);
		}
}
