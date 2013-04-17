package com.tangotab.signUp.activity;



import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.contactSupport.activity.ContactSupportActvity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.CreateSqliteHelper;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.customUrl.activity.CustomUrlActivity;
import com.tangotab.customUrl.activity.CustomUrlActivity.GetDealAsyncTask;
import com.tangotab.customUrl.service.CustonUrlService;
import com.tangotab.customUrl.vo.CustomDealVo;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.login.activity.LogInActivity;
import com.tangotab.login.dao.LoginDao;
import com.tangotab.login.service.LoginService;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.signUp.service.SignUpService;
import com.tangotab.signUp.vo.UserVo;
/**
 * Activity class for new user to sign up and get authenticate to login into the Tangotap Allication
 * 
 * <br> Class :SignUpActivity
 * <br> Layout :signup.xml
 * 
 * @author dillip.lenka
 *
 */
public class SignUpActivity extends Activity
{
	ProgressDialog mDialog = null;
	private EditText firstName;
	private EditText lastName;
	private EditText email ;
	private EditText password;
	private EditText confirmpassword;
	private EditText zipCode;
	private EditText promoCode;
	private EditText mobileNumber;	
	private EditText workZip;
	private UserVo userVo;
	private Vibrator myVib;
	private List<UserVo> userVoList =null;
	private String message;
	private LoginVo loginVo;
	private GoogleAnalyticsTracker tracker;
	private String dealId  =null;
	public TangoTabBaseApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		application = (TangoTabBaseApplication) getApplication(); 
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SIGN_UP_PAGE);
		tracker.trackEvent("SignUp", "TrackEvent", "signup", 1);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		/**
		 * All the Edit text id from XML
		 */
		firstName = (EditText) findViewById(R.id.fname);
		
		lastName = (EditText) findViewById(R.id.lname);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		confirmpassword = (EditText) findViewById(R.id.password);
		zipCode = (EditText) findViewById(R.id.zipcode);
		workZip = (EditText) findViewById(R.id.workzipcode);
		promoCode = (EditText) findViewById(R.id.promocode);
		mobileNumber = (EditText) findViewById(R.id.mnumber);
		Button signUp = (Button) findViewById(R.id.signup);
		
		/**
		 * OnClick Listener for signup Button
		 */
		
		signUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				myVib.vibrate(50);
				doSignUp();		

			}
		});
		
		/**
		 * OnEditorActionListener for PromoCode TextBox.
		 */
		promoCode.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{

			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_DONE)
				{
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(promoCode.getWindowToken(), 0);
					doSignUp();
				return true;
				}
				return false;
			}
			
		});
		
	}
	/**
	 * This method will do all Field validation and will call web service for Sign Up
	 * 
	 */
	private void doSignUp()
	{
		Log.v("Method Invokation ", "Invoking doSignUp() method for sign up");
		if (ValidationUtil.isNullOrEmpty(firstName.getEditableText().toString()) && ValidationUtil.isNullOrEmpty(lastName.getEditableText().toString()) 
				&& ValidationUtil.isNullOrEmpty(email.getEditableText().toString()) && ValidationUtil.isNullOrEmpty(password.getEditableText().toString())
				&& ValidationUtil.isNullOrEmpty(confirmpassword.getEditableText().toString()) && ValidationUtil.isNullOrEmpty(zipCode.getEditableText().toString()))
		{
			showDialog(0);
		}
		else if (ValidationUtil.isNullOrEmpty(firstName.getEditableText().toString()))
		{
			showDialog(1);
		}			
		else if (ValidationUtil.isNullOrEmpty(lastName.getEditableText().toString()))
		{
			showDialog(2);
		}			
		else if (ValidationUtil.isNullOrEmpty(email.getEditableText().toString()))
		{
			showDialog(3);
		}			
		else if (ValidationUtil.isNullOrEmpty(password.getEditableText().toString()))
		{
			showDialog(4);
		}
			
		else if (ValidationUtil.isNullOrEmpty(confirmpassword.getEditableText().toString()) || confirmpassword.getEditableText().toString().length()<6)
		{
			showDialog(5);
		}		

		else if (ValidationUtil.isNullOrEmpty(zipCode.getEditableText().toString()))
		{
			showDialog(7);
		}
		else if(ValidationUtil.isNullOrEmpty(workZip.getEditableText().toString()))
		{
			showDialog(21);
		}
			
		else if (!ValidationUtil.isNullOrEmpty(firstName.getEditableText().toString()) && !ValidationUtil.isNullOrEmpty(lastName.getEditableText().toString())
				&& !ValidationUtil.isNullOrEmpty(email.getEditableText().toString()) && !ValidationUtil.isNullOrEmpty(password.getEditableText().toString())
				&& !ValidationUtil.isNullOrEmpty(confirmpassword.getEditableText().toString()) && !ValidationUtil.isNullOrEmpty(zipCode.getEditableText().toString())
				&& !ValidationUtil.isNullOrEmpty(workZip.getEditableText().toString()) && zipCode.getEditableText().length() <= 7) {
			String emailstring = email.getEditableText().toString();
			if (emailstring.length() != 0) {
				boolean isValidEmail = ValidationUtil.eMailValidation(emailstring);
				if (isValidEmail)
				{
					if (password.getEditableText().length() >= 6) 
					{
						if (password.getEditableText().toString().equals(confirmpassword.getEditableText().toString())) 
						{
							String homeZipCode = zipCode.getEditableText().toString();
							String workZipCode = workZip.getEditableText().toString();
							
							Pattern usPattern = Pattern.compile("(^\\d{5}(-\\d{4})?$)");
							Pattern caPattern = Pattern.compile("(^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]{1}\\d{1}[A-Za-z]{1} *\\d{1}[A-Za-z]{1}\\d{1}$)");
							
							Matcher usHomeZipMatcher = usPattern.matcher(homeZipCode);
							Matcher usWorkZipMatcher = usPattern.matcher(workZipCode);
							
							Matcher caHomeZipMatcher = caPattern.matcher(homeZipCode);
							Matcher caWorkZipMatcher = caPattern.matcher(workZipCode);
							
							if((usHomeZipMatcher.matches() ||  caHomeZipMatcher.matches())&& (usWorkZipMatcher.matches() || caWorkZipMatcher.matches()))
							{
								if (checkInternetConnection())
								{
									if(!isValidHomeZip(homeZipCode,usHomeZipMatcher,caHomeZipMatcher)){
										showDialog(20);
									}else if(!isValidWorkZip(workZipCode,usWorkZipMatcher,caWorkZipMatcher)){
										showDialog(22);
									}else{
										/**
										 * Get all the sign up informations from the user.
										 */
										String fName = firstName.getEditableText().toString();
										String lName = lastName.getEditableText().toString();
										String newEmail = email.getEditableText().toString();
										String newPass = password.getEditableText().toString();
										String newNumber = mobileNumber.getEditableText().toString();
										String newZip = zipCode.getEditableText().toString();
										String promoText = promoCode.getEditableText().toString();
										String workZipText = workZip.getEditableText().toString();
										userVo = new UserVo(fName,lName,newZip,workZipText,null,null,newNumber,null,newEmail,newPass,promoText);
										new SignupAsyncTask().execute();
									}
								}
								else{
									showDialog(15);									
								}
							}else{
								if(!usHomeZipMatcher.matches() && !caHomeZipMatcher.matches())
									showDialog(20);
								if(!usWorkZipMatcher.matches() && !caWorkZipMatcher.matches())
									showDialog(22);
									
							}
						}
						else
						{
							showDialog(9);
						}
					} 
					else
						showDialog(5);
				} 
				else
					showDialog(12);			}
		} else if (mobileNumber.getEditableText().length() > 0) {
			if (mobileNumber.getEditableText().length() != 10)
				showDialog(10);
		} else if (zipCode.getEditableText().length() != 6)
			showDialog(11);
	}
	
	private boolean isValidWorkZip(String workZipCode,
			Matcher usWorkZipMatcher, Matcher caWorkZipMatcher) {
		if(usWorkZipMatcher.matches()){
			if(ValidationUtil.isValidUSZip(workZipCode)){
				return true;
			}else{
				return false;
			}
		}
		
		if(caWorkZipMatcher.matches()){
			if(ValidationUtil.isValidZip(workZipCode)){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	private boolean isValidHomeZip(String homeZipCode,
			Matcher usHomeZipMatcher, Matcher caHomeZipMatcher) {
		if(usHomeZipMatcher.matches()){
			if(ValidationUtil.isValidUSZip(homeZipCode)){
				return true;
			}else{
				return false;
			}
		}
		
		if(caHomeZipMatcher.matches()){
			if(ValidationUtil.isValidZip(homeZipCode)){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Signup service to be run in background thread.
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class SignupAsyncTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(SignUpActivity.this, "Connecting", "Please Wait...");
			mDialog.setCancelable(true);
		}
		
		@Override
		protected String doInBackground(String... params)
		{
			String message =null;
			try{
				SignUpService service = new SignUpService();
				message = service.doSignUp(userVo);
			}
			catch(ConnectTimeoutException e)
			{
				Log.e("ConnectTimeoutException occured in sign up service", e.getLocalizedMessage());
				message =null;
				Intent contactIntent=new Intent(getApplicationContext(), ContactSupportActvity.class);
				startActivity(contactIntent);
			}			
			catch(Exception e)
			{
				Log.e("Exception occured in sign up service", e.getLocalizedMessage());
				message =null;
			}
			
			return message;
		}
		
		@Override
		protected void onPostExecute(String message)
		{
			mDialog.dismiss();
			
			if(!ValidationUtil.isNullOrEmpty(message))
			{
				Log.v("Signup response message ", message);
				if(message.equals("You have Signed Up Successfully."))
					showDialog(13);
				if(message.equals("Email already exists."))
					showDialog(14);
			}
						
		}

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
			mDialog = ProgressDialog.show(SignUpActivity.this, "Searching ", "Please wait...");
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
			}catch(Exception e)
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
				/**
				 * put all the user information into the shared preferences		
				 */
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
					getIntoActivity(uriPath,parameter);
				}
				else
				{				
					Intent mainIntent = new Intent(getApplicationContext(),NearMeActivity.class);
					mainIntent.putExtra("frmPage", "splashScreen");
					mainIntent.putExtra("isGetStarted", "true");
					mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainIntent);
					finish();
				}
			} 
			else {
				if(!ValidationUtil.isNullOrEmpty(message) && message.equalsIgnoreCase("Your account is inactive, Please contact TangoTab Administrator."))
				{
					showDialog(16);
				} 
				else
				{
					showDialog(17);	
				}
			}
			
		}

		
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab0 = new AlertDialog.Builder(SignUpActivity.this);
			ab0.setTitle("TangoTab");
			ab0.setMessage("Please provide First Name");
			ab0.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab0.create();
		case 1:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(SignUpActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage("Please provide First Name");
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab1.create();
		case 2:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(SignUpActivity.this);
			ab2.setTitle("TangoTab");
			ab2.setMessage("Please provide Last Name");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab2.create();
		case 3:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(SignUpActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("Please provide Email address");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab3.create();
		case 4:
			AlertDialog.Builder ab4 = new AlertDialog.Builder(SignUpActivity.this);
			ab4.setTitle("TangoTab");
			ab4.setMessage("Please provide password");
			ab4.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab4.create();
		case 5:
			AlertDialog.Builder ab5 = new AlertDialog.Builder(SignUpActivity.this);
			ab5.setTitle("TangoTab");
			ab5.setMessage("Password should be more than 6 characters");
			ab5.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
					password.getEditableText().clear();
				}
			});
			return ab5.create();

		case 7:
			AlertDialog.Builder ab7 = new AlertDialog.Builder(SignUpActivity.this);
			ab7.setTitle("TangoTab");
			ab7.setMessage("Please provide Home zipcode.");
			ab7.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					zipCode.getEditableText().clear();
				}
			});
			return ab7.create();
		case 8:
			AlertDialog.Builder ab8 = new AlertDialog.Builder(SignUpActivity.this);
			ab8.setTitle("TangoTab");
			ab8.setMessage("Please Agree Privacy policy and terms of use");
			ab8.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab8.create();

		case 10:
			AlertDialog.Builder ab10 = new AlertDialog.Builder(SignUpActivity.this);
			ab10.setTitle("TangoTab");
			ab10.setMessage("Please provid valid Mobile Number");
			ab10.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab10.create();
		case 11:
			AlertDialog.Builder ab11 = new AlertDialog.Builder(SignUpActivity.this);
			ab11.setTitle("TangoTab");
			ab11.setMessage("Zip/Post Code Should be max of 7 characters");
			ab11.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					zipCode.getEditableText().clear();
				}
			});
			return ab11.create();
		case 12:
			AlertDialog.Builder ab12 = new AlertDialog.Builder(SignUpActivity.this);
			ab12.setTitle("TangoTab");
			ab12.setMessage("Please provide  Valid E-Mail format");
			ab12.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					email.getEditableText().clear();
				}
			});
			return ab12.create();
		
		case 13:
			AlertDialog.Builder ab13 = new AlertDialog.Builder(SignUpActivity.this);
			ab13.setTitle("TangoTab");
			ab13.setMessage("You have Signed Up Successfully.");
			ab13.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
					String pass =password.getEditableText().toString();
					loginVo = new LoginVo();
					loginVo.setUserId(email.getEditableText().toString());
					String encodedpass = Base64.encodeToString(pass.getBytes(),Base64.DEFAULT);
					encodedpass.trim();			
					String enpass = encodedpass.substring(0,encodedpass.length() - 1);
					loginVo.setPassword(enpass);
					
					new UserValidationAsyncTask().execute();

				}
			});
			return ab13.create();
		case 14:
			AlertDialog.Builder ab14 = new AlertDialog.Builder(SignUpActivity.this);
			ab14.setTitle("TangoTab");
			ab14.setMessage("Email already exists.");
			ab14.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					email.getEditableText().clear();
					password.getEditableText().clear();
					confirmpassword.getEditableText().clear();
					dialog.dismiss();
				}
			});
			return ab14.create();
		case 15:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(SignUpActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return ab15.create();
		case 16:
			AlertDialog.Builder ab16 = new AlertDialog.Builder(SignUpActivity.this);
			ab16.setTitle("TangoTab");
			ab16.setMessage(message);
			ab16.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent loginIntent = new Intent(SignUpActivity.this,LogInActivity.class);
					startActivity(loginIntent);
				}
			});
			return ab16.create();
		case 17:
			AlertDialog.Builder ab17 = new AlertDialog.Builder(SignUpActivity.this);
			ab17.setTitle("TangoTab");
			ab17.setMessage(message);
			ab17.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					password.getEditableText().clear();
					dialog.dismiss();
				}
			});
			return ab17.create();
		case 20:
			AlertDialog.Builder zipEmptyDialog = new AlertDialog.Builder(
					SignUpActivity.this);
			zipEmptyDialog.setTitle("TangoTab");
			zipEmptyDialog.setMessage("Please provide proper home zipcode.");
			zipEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return zipEmptyDialog.create();
		case 21:
			AlertDialog.Builder ab21 = new AlertDialog.Builder(SignUpActivity.this);
			ab21.setTitle("TangoTab");
			ab21.setMessage("Please provide work Zip Code");
			ab21.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab21.create();
		case 22:
			AlertDialog.Builder workEmptyDialog = new AlertDialog.Builder(
					SignUpActivity.this);
			workEmptyDialog.setTitle("TangoTab");
			workEmptyDialog.setMessage("Please provide proper work zipcode.");
			workEmptyDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			return workEmptyDialog.create();
		}
		return mDialog;
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
			Intent nearMeIntent = new Intent(SignUpActivity.this, NearMeActivity.class);
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
			Intent searchIntent = new Intent(SignUpActivity.this, SearchActivity.class);
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
			mDialog = ProgressDialog.show(SignUpActivity.this, "Searching ", "Please wait...");
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
			Intent searchIntent = new Intent(SignUpActivity.this, SearchActivity.class);
			searchIntent.putExtra("fromPage", "customURL");
			searchIntent.putExtra("dealId", dealId);
			searchIntent.putExtra("selectDeal",  dealsDetailVo);
			startActivity(searchIntent);

				
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
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent homeActivity = new Intent(SignUpActivity.this, FacebookLogin.class);
		homeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeActivity);
		finish();
	}
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode) 
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(SignUpActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(SignUpActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true; 
		    }

		    return super.onKeyDown(keycode, e);
		}
}
