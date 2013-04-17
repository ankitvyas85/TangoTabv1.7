package com.tangotab.login.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.login.service.ForgetPasswordService;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.signUp.activity.SignUpActivity;
/**
 * This class will be used for  Forget password and retrieve the password from your own mail id.
 * 
 * <br> Class :ForgetPasswordActivity
 * <br> Layout: forgetpassword.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class ForgetPasswordActivity extends Activity
{
	private final int messageId = 0;
	private final int reserComp = 1;
	private final int emailval = 2;
	
	private EditText email=null;
	private ProgressDialog mDialog=null;
	
	private LoginVo loginVo;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpassword);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.FORGOT_PASSWORD_PAGE);
		tracker.trackEvent("ForgotPassword", "TrackEvent", "forgotpassword", 1);
		
		/**
		 * Button widgets		
		 */
		email = (EditText)findViewById(R.id.email);
		final Button send = (Button)findViewById(R.id.send);
		Button cancel =(Button)findViewById(R.id.cancel);
		/**
		 * Cancel button on click listener 
		 */
		cancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent homeIntent = new Intent(getApplicationContext(), FacebookLogin.class);
				startActivity(homeIntent);
				myVib.vibrate(50);
				finish();
				
			}
		});
		/**
		 * Send button on Click Listener
		 */
		send.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{	
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(send.getWindowToken(), 0);
				myVib.vibrate(50);
				if(!ValidationUtil.isNullOrEmpty(email.getText().toString().trim()))
				{
				if (checkInternetConnection())
				{
				 String emailId = email.getText().toString();
				 
				 loginVo = new LoginVo();
				 loginVo.setUserId(emailId);
				 
				/**
				  * AsyncTask call for forget password web services.
				  */
				 new ForgetPasswordAsyncTask().execute();
				}
				
			}else
				showDialog(emailval);
				}
		});
		
		/**
		 * OnEditorActionListener for Email TextBox.
		 */
		email.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{

			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_DONE)
				{
					/*InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(email.getWindowToken(), 0);*/
				return true;
				}
				return false;
			}
			
		});
	}
	
	/**
	 * ForgetPasswordAsyncTask for execute service in different thread.
	 * 
	 * @author dillip.lenka
	 *
	 */
	
	public class ForgetPasswordAsyncTask extends AsyncTask<Void, Void, String> 
	{
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Searching ", "Please wait...");
			mDialog.setCancelable(true);
		}
		@Override
		protected String doInBackground(Void... params)
		{
			String message =null;
			try
			{
				ForgetPasswordService service = new ForgetPasswordService();
				message =service.forgetPassword(loginVo);
			}catch(TangoTabException e)
			{
				message =null;
			}
			return message;
		}
		@Override
		protected void onPostExecute(String message) 
		{
			mDialog.dismiss();
			if(!ValidationUtil.isNullOrEmpty(message) && message.equals("true"))
			{
				showDialog(messageId);
			}
			else{					
					showDialog(reserComp);
				}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case messageId:
			AlertDialog.Builder ab = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage("Your password has been sent to your Email Address");
			ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					
					dialog.dismiss();
					email.setText("");
					Intent homeIntent = new Intent(getApplicationContext(), FacebookLogin.class);
					startActivity(homeIntent);
					finish();

				}
			});
			return ab.create();
		case reserComp:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage("Please Provide Valid Email Format");
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					email.setText("");
				}
			});
			return ab1.create();
			
		case emailval:
			AlertDialog.Builder ab5 = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab5.setTitle("TangoTab");
			ab5.setMessage("Please Enter Email Address");
			ab5.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			return ab5.create();
		case 15:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					email.setText("");
				}
			});
			return ab15.create();
		}
		return null;
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
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode)
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(ForgetPasswordActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(ForgetPasswordActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true; 
		    }

		    return super.onKeyDown(keycode, e);
		}
}
