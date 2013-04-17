package com.tangotab;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.login.activity.SplashScreenActivity;

/**
 * Handles the push notification for the device,this is called by the GCM(Google
 * Cloud Messaging) BroadcastReceiver when the device registers with GCM for
 * getting registration id,when a message was received from the server,when an
 * error occurred while registering the device and when the device was
 * unregistered. This class should always be in application root package.
 * 
 * @author Ram.Donda
 * 
 */
public class GCMIntentService extends GCMBaseIntentService {
	public GCMIntentService() {
		// email id which is used to create the api key for the application
		super("shaktimaan84@gmail.com");
		/**
		 * you can use any google mail id and create the new api key and replace
		 * the new api key at AppConstants.GCM_APP_ID
		 */

		Log.e("called", "service..");

	}

	/**
	 * Called when there was an error while registering the device
	 */
	@Override
	protected void onError(Context arg0, String arg1) {
		Log.e("error", arg1);

	}

	/**
	 * Called when a message was received from server
	 */
	@Override
	protected void onMessage(Context arg0, Intent arg1)
	{
		
		Log.e("message","received");
		generateNotification(arg0,"Sample message received from TangoTab");
	
	}

	/**
	 * Called when the device registered successfully.
	 * 
	 */
	@Override
	protected void onRegistered(Context arg0, String arg1) {

		Log.e("reg id is..", arg1);

		TangoTabBaseDao baseDao = TangoTabBaseDao.getInstance();
		ConnectionManager manager = baseDao.getConManger();

		try {

			String url = AppConstant.NOTIFICATION_URL+ arg1;
			manager.intializePostURL(url);
			manager.goPostIt(url, null);
		//	Log.e("", "" + manager.getPostResponse());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Called when the device unregistered successfully with server for not
	 * receiving any further messages
	 */
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
	}
	/**
     * Issues a notification to inform the user that server has sent a message.
     */
    private  void generateNotification(Context context, String message) {
        int icon = R.drawable.tangotab_icon;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, SplashScreenActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
