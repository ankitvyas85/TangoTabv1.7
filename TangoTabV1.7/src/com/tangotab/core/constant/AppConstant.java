package com.tangotab.core.constant;

import java.util.List;

import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.nearMe.vo.DealsDetailVo;

/**
 * This class will contain all the constant informations which will be used
 * through out the application.
 * 
 * @author dillip.lenka
 * 
 */
public class AppConstant {

	public static String IS_SETTINGSCHANGED = "10";
	public static final int GET_DATE_REQUEST_CODE = 345;
	public static String productionServer = "http://services.tangotab.com/services";

	public static String qaServer = "http://qa.tangotab.com/tangotabservices/services";
	public static String develServer = "http://ec2-54-245-22-11.us-west-2.compute.amazonaws.com/tangotabservices/services";
	public static String stageServer = "http://Stage.tangotab.com/tangotabservices/services";
	public static final String GOOGLE_SERVICE_URL = "http://maps.google.com/maps/geo?q=";

	public static final String TWITTER_OFF_MESSGAGE = "Twitter sharing is currently OFF.Please turn it ON in settings page to tweet.";
	public static String debugFlurryAgentID = "FSSR4W2S8FGCY52YMX98";
	public static String productionFlurryAgentID = "W47ZC2NDG4KQM56287CS";

	public static String debugGoogleAnalyticsID = "UA-32403979-2";
	public static String productionGoogleAnalyticsID = "GA-3461-2529-1";

	public static String baseUrl = stageServer;
	public static String FlurryAgentID = productionFlurryAgentID;
	public static String GoogleAnalyticsID = productionGoogleAnalyticsID;

	public static double locationLat = 0.0;
	public static double locationLong = 0.0;

	public static double dev_lat = 0.0;
	public static double dev_lang = 0.0;

	public static final String NEAR_TO_RESTRURANT = "403.0";
	public static final String PACKAGE_NAME = "com.tangotab";
	public static final String WELCOME_MESSAGE = "TangoTab welcomes you";
	public static long ALARM_HOUR = 3600 * 1000;
	public static final String ALARM_ACTION_NAME = "com.bytefoundry.broadcast.ALARM";
	public static final String INSTLL_VERSION = "1.6";
	public static boolean map_flag = false;
	public static boolean flagFormMaping = false;
	public static List<DealsDetailVo> dealsList;
	public static List<OffersDetailsVo> offersList;
	public static final String GCM_APP_ID = "1086439766692";

	/**
	 * added for twitter implementation
	 */

	public static final String CONSUMER_KEY =  	"WyKzwA772JmPp7pRD3vQ";// 	KPTRiYasd11oO3d30rPDoQ// 4HQ1Xr01ZNYICPTpoBZCzA
	public static final String CONSUMER_SECRET = "KR3fRz4twqhyY0EtzFyy4oVlxNsmeM5N6jV5VSfAJI";//cx5A3Z59GezUAGFnXLrTLvzJnATsqOQ2w5zJ9Y2H8Jk// IWIXQIHdlS2OwTUHrM4DjQN2nbqU72kaOH4wROJkhd0

	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";

	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
			+ "://" + OAUTH_CALLBACK_HOST;
	public static final String TWITTER_PIC_API_KEY = "4b0b6eb0d8d2911ed9a5e3936481959e";
	public static final int TWITTER_REQUEST_CODE = 47;

	public static String MYPHILANTHROPY_METRICS_URL = "http://sm-dev.tangotab.com/app_dev.php/json/metrics/";

	/**
	 * Constant added for Contact Support.
	 */

	public static final String SUPPORT_EMAIL_ID = "support@tangotab.com";
	public static final String EMAIL_SUBJECT = "Android App Support -Version 1.7";
	public static final int TIME_PEROID = 10000;

	/**
	 * constant added for Push notification
	 */

	public static final String NOTIFICATION_URL = "http://172.16.76.48:8080/gcm-demo/register?regId=";// 172.16.76.48
																										// will
																										// point
																										// to
																										// your
																										// local
																										// IP
																										// address.

	/**
	 * Google analytics constants.
	 */
	public static final String APP_ID = "262980363802171";

	public final static String GA_REG_KEY = "UA-37866823-1";// UA-4500699-13
	public static final String LOGIN_PAGE = "/SignIn";
	public static final String SIGN_UP_PAGE = "/SignUp";
	public static final String FORGOT_PASSWORD_PAGE = "/Forgetpassword";
	public static final String SEARCH_PAGE = "/Search";
	public static final String SETTING_PAGE = "/Settings";
	public static final String NEARME_PAGE = "/NearMe";
	public static final String MYOFFER_PAGE = "/MyOffers";
	public static final String CLAIM_OFFER_PAGE = "/ClaimNow";
	public static final String DEALS_DETAIL_PAGE = "/CheckIn";
	public static final String FACEBOOK_LOGIN = "/FaceBookLogin";
	public static final String ZIP_ACTIVITY = "/ZipActivity";
	public static final String SPLASH_SCREEN = "/SplashScreen";
	public static final String MAIN_MENU_SCREEN = "/MainMenuScreen";
	public static final String ME_SCREEN = "/MeScreen";
	public static final String TWITTER_SCREEN = "/TwitterScreen";
	public static final int SUCCESFULL_POST = 101;
	public static final int UNSUCCESFULL_POST = 102;
	public static final int VALIDATE_ZIPCODE = 23;
	public static final int SHARE_DISMISS_DIALOG = 29;
	public static final int SHARE_TWITTER_DISMISS_DIALOG = 30;
	public static final String SELECTED_PREFS = "selectedpreferences";
	public static final String KEY_SELECTED_ITEM = "selecteditem";
	public static final String SHARE = "sahre";
	public static final int FACEBOOK_SHARE = 111;
	public static final int TWITTER_SHARE = 222;
	public static final int CLAIM_OFFER = 333;
	public static final int FACEBOOK_POST_SUCCESSFUL_DIALOG = 444;
	public static final String TWITTER_PREFERENCES = "twitterpreferences";
	public static final String FACEBOOK_PREFERENCES = "facebookpreferences";
	public static final String FLOW_PREFERENCES = "flowpreferences";
	public static final int ICON_FACEBOOK_SHARE = 555;
	public static final int ICON_TWITTER_SHARE = 666;
	

}
