package com.tangotab.twitter.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.harrison.lee.twitpic4j.TwitPic;
import com.harrison.lee.twitpic4j.TwitPicResponse;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.ValidationUtil;

public class TwitterUtils 
{
/**
 * Check whether the user is already logged in or not
 * @param prefs
 * @return
 */
	public static boolean isAuthenticated(SharedPreferences prefs) {

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(AppConstant.CONSUMER_KEY, AppConstant.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		
		try {
			twitter.getAccountSettings();
			return true;
		} catch (TwitterException e)
		{
			return false;
		}
	}
	/**
	 * Used to send a tweet from the application
	 * @param prefs,SharedPrefrence where the credentials are stored
	 * @param msg,message to be sent 
	 * @throws Exception
	 */
	public static void sendTweet(SharedPreferences prefs,String msg,String imageUrl) throws Exception {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		Log.e("secret key..",secret);
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(AppConstant.CONSUMER_KEY, AppConstant.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		if(!ValidationUtil.isNull(imageUrl)){
        twitter.updateStatus(msg+imageUrl);
		}
		else{
			   twitter.updateStatus(msg);	
		}
     }	
	
}
