package com.tangotab.localNotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tangotab.core.constant.AppConstant;
import com.tangotab.localNotification.activity.LocalNotificationActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * This class will receive the alarm intent from broad cast and send it to Local notification.
 * 
 * @author Dillip.Lenka
 *
 */
public class LocalNotificationReceiver extends BroadcastReceiver
{
	private OffersDetailsVo  offersDetailsVo;
	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		 Bundle bundle = intent.getExtras();
		 offersDetailsVo= (OffersDetailsVo)bundle.getParcelable("selectOffers");
		 
		// Handle the alarm broadcast
		if (AppConstant.ALARM_ACTION_NAME.equals(action))
		{
			/**
			 * Put the consumer deals object into alarm intent.
			 */
			Bundle b = new Bundle();
	        b.putParcelable("selectOffers", offersDetailsVo);			
			Intent alarmIntent = new Intent("android.intent.action.MAIN");
			alarmIntent.putExtras(b);
			alarmIntent.setClass(context, LocalNotificationActivity.class);
			alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			
			context.startActivity(alarmIntent);
		}
	}
}