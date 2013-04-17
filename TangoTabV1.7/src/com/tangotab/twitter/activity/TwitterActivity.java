package com.tangotab.twitter.activity;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
/**
 * Handles the login for Twitter from application
 * @author Ram.Donda
 *
 */
public class TwitterActivity extends Activity implements OnDismissListener
{
	 Handler myhand;
	  final String TAG = getClass().getName();
	 private OAuthConsumer consumer; 
    private OAuthProvider provider;
    boolean pageFinish=false;
    Dialog dialog;
   boolean twitter=false;
   private ProgressBar mProgressBar;
	private GoogleAnalyticsTracker tracker;
	@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.twitter_activity);
	/**
	 * Google analytics implementation added here.
	 */
	tracker = GoogleAnalyticsTracker.getInstance();
	tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
	tracker.setDebug(true);
	tracker.trackPageView(AppConstant.TWITTER_SCREEN);
	tracker.trackEvent("TwitterScreen", "TrackEvent", "twitterscreen", 1);
	
	mProgressBar=(ProgressBar)findViewById(R.id.xProgressBar);
	startTwitterLogin();
}
	class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void>
	{

		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private SharedPreferences prefs;
		
		public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) 
		{
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.prefs=prefs;
		}


		
		@Override
		protected Void doInBackground(Uri...params) {
			final Uri uri = params[0];
		
			final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			//Log.i("path is..",oauth_verifier);
			try {
				provider.retrieveAccessToken(consumer, oauth_verifier);

				final Editor edit = prefs.edit();
				edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
				edit.commit();
				
				String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
				String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
				
				consumer.setTokenWithSecret(token, secret);
				myhand.post(run);
			
				Log.v("Authenticatiuon:", "OAuth - Access Token Retrieved");
				
			} catch (Exception e) {
				Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
				setResult(RESULT_CANCELED);
				
				finish();
			}

			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		}

		Runnable run=new Runnable()
		{
			public void run()
			{
			Log.e("twitter","checked");
	        twitter=true;
	        setResult(RESULT_OK);
	        finish();
			//twittercheck.setImageResource(R.drawable.checked);
			}
		};
	}	
class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void>
	{

		final String TAG = getClass().getName();
		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private Handler myhand;
		String url;
		
		/**
		 * 
		 * We pass the OAuth consumer and provider.
		 * 
		 * @param 	context
		 * 			Required to be able to start the intent to launch the browser.
		 * @param 	provider
		 * 			The OAuthProvider object
		 * @param 	consumer
		 * 			The OAuthConsumer object
		 */
		public OAuthRequestTokenTask(Context context,OAuthConsumer consumer,OAuthProvider provider,Handler myhand)
		{
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.myhand=myhand;
		}

		/**
		 * 
		 * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
		 * 
		 */
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				Log.i(TAG, "Retrieving request token from Google servers");
				url = provider.retrieveRequestToken(consumer,AppConstant.OAUTH_CALLBACK_URL);
				Log.i(TAG, "Popping a browser with the authorize URL : " + url);
				
				myhand.post(run);
			} catch (Exception e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
				setResult(RESULT_CANCELED);
				finish();
				//mydialog.dismiss();
				
			}

			return null;
		}
		
	    @Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		}

		Runnable run=new Runnable()
	    {
	    	public void run()
	    	{
	    		
	    		Log.e("url is",url);
	    		dialog = new Dialog(context);
	    		dialog.setOnDismissListener(TwitterActivity.this);
	    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    		dialog.setCancelable(false);
			    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View vi = inflater.inflate(R.layout.twitterpopup, null);
			    dialog.setContentView(vi);
			    
			    ImageView close=(ImageView)vi.findViewById(R.id.close);
			    close.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						setResult(RESULT_CANCELED);
						finish();
						//mydialog.dismiss();
					}
				});
			    WebView wb = (WebView)vi.findViewById(R.id.twitterweb);
			    wb.setWebViewClient(new myWebClient());  
		        wb.getSettings().setJavaScriptEnabled(true);  
		        wb.loadUrl(url);  
		       
		
			   
	    	}
	    };
	 class myWebClient extends WebViewClient{  //HERE IS THE MAIN CHANGE. 
  	   String url;
  	@Override  
      public void onPageStarted(WebView view, String url, Bitmap favicon) {  
          // TODO Auto-generated method stub  
  		this.url=url;
          super.onPageStarted(view, url, favicon);  
      }  
        
      @Override  
      public boolean shouldOverrideUrlLoading(WebView view, String url) {  
          // TODO Auto-generated method stub  
      	
      	this.url=url;
      	if(url.startsWith("htt"))
      	{
      		
      		view.loadUrl(url); 
      	}
      	else
      	{
      	 	
      	 	dialog.dismiss();
      	 	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TwitterActivity.this);
      		final Uri uri = Uri.parse(url);
      		if (uri != null && uri.getScheme().equals(AppConstant.OAUTH_CALLBACK_SCHEME)) {
      			Log.e(TAG, "Callback received : " + uri);
      			Log.e(TAG, "Retrieving Access Token");
      			new RetrieveAccessTokenTask(TwitterActivity.this,consumer,provider,prefs).execute(uri);
      				
      		}
      	}
      	
          
          return true;  
            
      }

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			
			if(url.startsWith("http://"))
			{
				Log.e("http","yes");
				//mydialog.dismiss();
			try{
  			dialog.show();
			}catch(Exception e){
				
			}
			}
			else if(pageFinish)
  		{
				Log.e("https","finished");
  			//mydialog.dismiss();
  			dialog.show();
  		}
			else
			{
				Log.e("https","true");
				pageFinish=true;
			}
			
		}

	 

  }
	}

	private void startTwitterLogin()
	{
		
		
		try {
					
			this.consumer = new CommonsHttpOAuthConsumer(AppConstant.CONSUMER_KEY, AppConstant.CONSUMER_SECRET);
		    this.provider = new CommonsHttpOAuthProvider(AppConstant.REQUEST_URL,AppConstant.ACCESS_URL,AppConstant.AUTHORIZE_URL);
		} catch (Exception e) {
			Log.e(TAG, "Error creating consumer / provider",e);
			
		}
		
		myhand=new Handler();
		new OAuthRequestTokenTask(this,consumer,provider,myhand).execute();
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) { 
		if(myhand==null){
			myhand=new Handler();	
		}
		Log.e("dialog","dismissed");
		
		
	}
	public interface twitterLoginStatusListener{
		public void getTwitterStatus(boolean status);
	}
}
