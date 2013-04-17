package com.tangotab.login.activity;

import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.calendar.activity.CalendarActivity;
import com.tangotab.contactSupport.activity.ContactSupportActvity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.CreateSqliteHelper;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.customUrl.activity.CustomUrlActivity;
import com.tangotab.customUrl.service.CustonUrlService;
import com.tangotab.customUrl.vo.CustomDealVo;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.login.dao.LoginDao;
import com.tangotab.login.service.LoginService;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.signUp.activity.SignUpActivity;
import com.tangotab.signUp.activity.SignUpActivity.GetDealAsyncTask;
import com.tangotab.signUp.vo.UserVo;

/**
 * Class will be used for Authenticate the user in order to login into TangoTab application..
 * 
 * <br> class :LogInActivity
 * <br> Layout: login.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class LogInActivity extends Activity 
{
	private final int Invalid_User = 0;
	private final int I_User = 1;
	private final int Dialog_id = 2;
	private final int Inactive_User = 3;
	private ProgressDialog mDialog=null;	
	private EditText username;
	private EditText password;
	
	private LoginVo loginVo;	
	private String message;
	private List<UserVo> userVoList =null;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	private String dealId;
	public TangoTabBaseApplication application;
	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);		
		application = (TangoTabBaseApplication) getApplication();
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.LOGIN_PAGE);
		tracker.trackEvent("LoginPage", "TrackEvent", "loginpage", 1);
		
		/**
		 * Get login informations from the data base.
		 */
		doLoginFromDataBase();
		
		setContentView(R.layout.login);
		
		/**
		 * Collect all the Ui Widgets
		 */
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		
		Button signUp = (Button)findViewById(R.id.signup);
		 TextView forgetpasswrd = (TextView) findViewById(R.id.forgetpassword);
		
			
		/**
		 * Sign up button on click handler./
		 */
		signUp.setOnClickListener(new OnClickListener()
		{
				public void onClick(View v)
				{
					myVib.vibrate(50);
					Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
					startActivity(signUpIntent);
					finish();
				}
			});		
		
		/**
		 * forget password button on click handler.
		 */
		forgetpasswrd.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View arg0)
			{
				myVib.vibrate(50);
				Intent forgetPassIntent = new Intent(LogInActivity.this,ForgetPasswordActivity.class);
				startActivity(forgetPassIntent);
				finish();
			}
		});	
		
		
		/**
		 * password button on Edit listner added.
		 */
		password.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{

			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_DONE)
				{
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
					doSignIn();
				return true;
				}
				return false;
			}
			
		});
	}
	
	/**
	 * User validation asynctask used for to get authnticate for new user..
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class UserValidationAsyncTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(LogInActivity.this, "Connecting", "Please Wait...");
			mDialog.setCancelable(true);
		}
		@Override
		protected String doInBackground(Void... params)
		{
			try{
				LoginService loginService = new LoginService();
				message = loginService.doSignIn(loginVo);
				if(ValidationUtil.isNullOrEmpty(message))
				{				
					userVoList = LoginDao.userVoList;				
				}
			}catch(ConnectTimeoutException e)
			{
				Log.e("ConnectTimeoutException occured", "ConnectTimeoutException occured at the time of login",e);
				message =null;
				Intent contactIntent=new Intent(getApplicationContext(), ContactSupportActvity.class);
				startActivity(contactIntent);
			}
			catch(Exception e)
			{
				Log.e("Exception occured", "Exception occured at the time of login",e);
				message =null;
			}
			
			return message;
		}
		@Override
		protected void onPostExecute(String message)
		{
			mDialog.dismiss();			
			if (!ValidationUtil.isNullOrEmpty(userVoList) && ValidationUtil.isNullOrEmpty(message)) 
			{
				SharedPreferences spfc = getSharedPreferences("sharelogout", 0);
				SharedPreferences.Editor sEditor = spfc.edit();
				sEditor.putBoolean("sharelogout_value", true);
				sEditor.commit();

				SharedPreferences spc = getSharedPreferences("UserName", 0);
				SharedPreferences.Editor edit = spc.edit();
				edit.putString("username", loginVo.getUserId());
				edit.putString("password", loginVo.getPassword());
				edit.commit();
				
				UserVo userVo = userVoList.get(0);
				
				String country=null;
				boolean isCanadaZip =ValidationUtil.validateCanadZip(userVo.getZip_code());
				if(isCanadaZip)
				{
					country ="canada";					
					Log.v("country IN LGIN PAGE", country);
				}
				
				SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("firstName", userVo.getFirst_name());
				edits.putString("lastName", userVo.getLast_name());
				edits.putString("fbID", "");
				edits.putString("postal", userVo.getZip_code());
				edits.putString("UserId", userVo.getUser_Id());
				edits.putString("workZip", userVo.getWork_zip());	
				edits.putString("country", country);
				/*edits.putString("isTwitterOn", userVo.getTwitter_share());
				edits.putString("isFacebookOn", userVo.getFacebook_share());*/
				edits.commit();	
				
							
				CreateSqliteHelper csh = new CreateSqliteHelper(getApplicationContext());
				SQLiteDatabase db = csh.getWritableDatabase();	
				ContentValues cv = new ContentValues();
				cv.put("ID", 1);
				cv.put("OPEN", "ON");
				db.insert("LOGIN", null, cv);
				db.close();
				
				SharedPreferences customURL = getSharedPreferences("CustomURL", 0);
				String fromPage = customURL.getString("fromPage", "");
				if(!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("customURL"))
				{
					String uriPath = customURL.getString("uriPath", "");
					String parameter = customURL.getString("parameter", "");
					//CustomUrlActivity activity = new CustomUrlActivity();
					getIntoActivity(uriPath,parameter);
				}
				else
				{
					Intent mainIntent = new Intent(getApplicationContext(),NearMeActivity.class);
					mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mainIntent.putExtra("frmPage", "splashScreen");
					mainIntent.putExtra("isGetStarted", "true");
					startActivity(mainIntent);
					finish();
				}
			} 
			else {
				if(!ValidationUtil.isNullOrEmpty(message) && message.equalsIgnoreCase("Your account is inactive, Please contact TangoTab Administrator."))
				{
					showDialog(Inactive_User);
				} 
				else
				{
					showDialog(Invalid_User);	
				}
			}
			
		}

		
	}
	
	private void doSignIn()
	{
		if(!ValidationUtil.isNullOrEmpty(username.getText().toString()) && !ValidationUtil.isNullOrEmpty(password.getText().toString()))
		{
			if(checkInternetConnection())
			{
				/*if(on_off.isChecked())
				{
					on_off.setBackgroundResource(R.drawable.on);
					executeLogin();
				}
				else*/
				{
					executeLogin();			
				}
			}
			else
			showDialog(Dialog_id);
		}
		else
		{
			showDialog(I_User);
		}
		
	}
	
	
	public void getIntoActivity(String uriPath,String parameter)
	{
		Log.v(" uriPath  and  parameter", "uriPath = "+uriPath +" parameter = "+parameter);
		/**
		 * Start near me activity with no parameter.
		 */
		/* getting location of device */
		
		
		if(uriPath.equalsIgnoreCase("nearMe"))
		{
			Intent nearMeIntent = new Intent(LogInActivity.this, NearMeActivity.class);
			startActivity(nearMeIntent);
		}
		if(uriPath.equalsIgnoreCase("search"))
		{
			String city =null;
			//application.getSearchList().clear();
			int index = parameter.indexOf("/");
			if(index!=-1)
			{
				String[] param = parameter.split("/");
				city =param[0];
				/**
				 * Set the spMailingid,spUserId and spJobId into session.
				 */
				try{
					application.setSpMailingID(param[2]);
					application.setSpUserId(param[3]);
					application.setSpJobId(param[4]);
					Log.v("spMailingId ,spUserdId ,spJobId from dealSilver pop link", "spMailingId ="+param[2]+" spUserdId="+param[3]+" spJobId="+param[4]);
					}catch(ArrayIndexOutOfBoundsException e)
					{
						Log.v("Exception:", "Exception occured without passing parameter spMailingId.");
					}
			}
			else
			{
				city =parameter;
			}
			Log.v("parameter for search from custom url ", "City name  ="+city);
			/**
			 * Start the search activity with city as parameter.
			 */
			Intent searchIntent = new Intent(LogInActivity.this, SearchActivity.class);
			searchIntent.putExtra("fromPage", "customURL");
			searchIntent.putExtra("address", city);
			startActivity(searchIntent);
		}
		/**
		 * Start claim offer activity from here.
		 */
		if(uriPath.equalsIgnoreCase("deal"))
		{
			
			String date =null;
					
			if(!ValidationUtil.isNullOrEmpty(parameter))
			{
				String param[] =parameter.split("/");
				dealId =param[0];
				date = param[1];
				Log.v("Deal id and Deal deal date from URL is ", "dealId ="+dealId+" Deal Date "+date);
				/**
				 * Set the spMailingid,spUserId and spJobId into session.
				 */
				try{
				application.setSpMailingID(param[2]);
				application.setSpUserId(param[3]);
				application.setSpJobId(param[4]);
				Log.v("spMailingId ,spUserdId ,spJobId from dealSilver pop link", "spMailingId ="+param[2]+" spUserdId="+param[3]+" spJobId="+param[4]);
				}catch(ArrayIndexOutOfBoundsException e)
				{
					Log.v("Exception:", "Exception occured without passing parameter spMailingId.");
				}

			}
						
			/**
			 * Get location lat and long from the shared preferences.
			 */
			 SharedPreferences location = getSharedPreferences("LocationDetails", 0);
			 String locLat = location.getString("locLat", "");
			 String locLong = location.getString("locLong", "");
			 /**
			  * Create new CustomDealVo object
			  */
			 CustomDealVo customDealVo = new CustomDealVo();
			 customDealVo.setDealId(dealId);
			 customDealVo.setDealDate(date);
			 customDealVo.setLocLat(locLat);
			 customDealVo.setLocLong(locLong);	
			 /**
			  * Get deal from deal id and Date.
			  */
			 
			 new GetDealAsyncTask().execute(customDealVo);
		}
	}
	/**
	 * Inner class for executing service in back ground thread in order to get deal from deal id and Deal date.
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class GetDealAsyncTask extends AsyncTask<CustomDealVo, Void, DealsDetailVo> 
	{
		private ProgressDialog mDialog =null;
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(LogInActivity.this, "Searching ", "Please wait...");
			mDialog.setCancelable(true);
		}
		@Override
		protected DealsDetailVo doInBackground(CustomDealVo... customDealVo)
		{
			DealsDetailVo dealsDetailVo =null;
			try{
				CustonUrlService customService  = new CustonUrlService();
				dealsDetailVo = customService.getCustomDeal(customDealVo[0]);	
			}catch(ConnectTimeoutException exe)
			{
				Log.e("ConnectTimeoutException occured", "ConnectTimeoutException occured at the time get mating deal with deal id",exe);
				Intent contactIntent=new Intent(getApplicationContext(), ContactSupportActvity.class);
				startActivity(contactIntent);
			}
			catch(Exception e)
			{
				Log.e("Exception occured", "Exception occured at the time of login",e);
			}			
			return dealsDetailVo;
		}
		@Override
		protected void onPostExecute(DealsDetailVo dealsDetailVo)
		{
			try{
				mDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			/**
			 * Start search activity
			 */
			Intent searchIntent = new Intent(LogInActivity.this, SearchActivity.class);
			searchIntent.putExtra("fromPage", "customURL");
			searchIntent.putExtra("dealId", dealId);
			searchIntent.putExtra("selectDeal",  dealsDetailVo);
			startActivity(searchIntent);

				
		}	
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Invalid_User:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(LogInActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage(message);
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					password.setText("");
					dialog.dismiss();
				}
			});
			return ab1.create();
		case I_User:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(LogInActivity.this);
			ab2.setTitle("TangoTab");
			ab2.setMessage("Please provide login credentials");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					}
			});
			return ab2.create();
		case Dialog_id:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(LogInActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					}
			});
			return ab3.create();
		case Inactive_User:
			AlertDialog.Builder ab5 = new AlertDialog.Builder(LogInActivity.this);
			ab5.setTitle("TangoTab");
			ab5.setMessage(message);
			ab5.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent loginIntent = new Intent(LogInActivity.this,LogInActivity.class);
					startActivity(loginIntent);
				}
			});
			return ab5.create();
		}
		return mDialog;
		
	}
	
	/**
	 * Get the user and password from the user and call the service to execute.
	 * 
	 */
	private void executeLogin()
	{
		//InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//im.hideSoftInputFromWindow(signIn.getWindowToken(), 0);
		String newUser = username.getText().toString();
		String newPass = password.getText().toString().trim();
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		String PhoneId=tm.getDeviceId();
		System.getProperty("os.version");
		String OSId=android.os.Build.VERSION.RELEASE;
		String NetworkId = tm.getNetworkOperator();      
		
		String AppId = null;
		try 
		{
			AppId = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} 
		catch (NameNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		//String AndroidId=android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    
		//password encoding
		String encodedpass = Base64.encodeToString(newPass.getBytes(),Base64.DEFAULT);
		encodedpass.trim();
		String enpass = encodedpass.substring(0,encodedpass.length() - 1);
		
		loginVo = new LoginVo();
		loginVo.setUserId(newUser);
		loginVo.setPassword(enpass);
		loginVo.setPhoneId(PhoneId);
		loginVo.setOsId(OSId);
		loginVo.setTtAppId(AppId);
		loginVo.setNetworkId(NetworkId);
		new UserValidationAsyncTask().execute();
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
	/**
	 * Connect to sqllite database and retrieve the data from login table.
	 * 
	 */
	private void doLoginFromDataBase()
	{
		CreateSqliteHelper sqliteHelper = new CreateSqliteHelper(getApplicationContext());
		SQLiteDatabase dataBase = sqliteHelper.getReadableDatabase();
		Cursor cursor = dataBase.rawQuery("Select * from LOGIN", null);
		int rowCount = cursor.getCount();
		cursor.close();
		dataBase.close();		
		if(rowCount>0)
		{
			if (checkInternetConnection())
			{	
				String paswd1=null;
				String usrnm1 = null;
				SharedPreferences valid_spc = getSharedPreferences("UserName", 0);
				String usrnm = valid_spc.getString("username", usrnm1);
				String paswd = valid_spc.getString("password", paswd1);
				/**
				 * Create new login vo object and pass the user name and password
				 */
				loginVo = new LoginVo();
				loginVo.setUserId(usrnm);
				loginVo.setPassword(paswd);
				/**
				 * Service call for user validation		
				 */
				new UserValidationAsyncTask().execute();
			}
		}
	}
	/**
	 * Back button functionality
	 */
	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(LogInActivity.this, FacebookLogin.class);
		homeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeActivity);
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(mDialog != null)
		{
			mDialog.dismiss();
		}
	}
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode) 
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(LogInActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(LogInActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true; 
		    }

		    return super.onKeyDown(keycode, e);
		}
}