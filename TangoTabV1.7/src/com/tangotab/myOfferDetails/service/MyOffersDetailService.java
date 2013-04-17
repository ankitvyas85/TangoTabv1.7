package com.tangotab.myOfferDetails.service;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.myOfferDetails.dao.MyOffersDetailDao;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * Do check in for selected offer.
 * @author dillip.lenka
 *
 */
public class MyOffersDetailService
{
	/**
	 * do check in for selected offer.
	 * @param offersDetailsVo
	 * @return
	 * @throws TangoTabException
	 */
	public String checkIn(OffersDetailsVo offersDetailsVo)throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking checkIn method", offersDetailsVo.toString());
		String checkinMessage =null;
		try
		{
			MyOffersDetailDao dao = new MyOffersDetailDao();
			checkinMessage= dao.doCheckIn(offersDetailsVo);
		}
		catch(ConnectTimeoutException exe)
		{
			Log.e("ConnectTimeoutException:", "ConnectTimeoutException occured in chackin the offers ",exe);
			throw new ConnectTimeoutException(exe.getLocalizedMessage());
		}
		catch(Exception exe)
		{
			Log.e("Exception:", "Exception occured in chackin the offers ",exe);
			throw new TangoTabException("MyOffersDetailService", "checkIn", exe);
		}
		return checkinMessage;
	}
	/**
	 * Cancel the offer from user request.
	 * 
	 * @param conResId
	 * @return
	 */
	public String cancelOffer(String conResId)throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking cancelOffer method", conResId);
		String message =null;
		try
		{
			MyOffersDetailDao dao = new MyOffersDetailDao();
			message= dao.cancelOffer(conResId);
		}
		catch(ConnectTimeoutException exe)
		{
			Log.e("ConnectTimeoutException:", "ConnectTimeoutException occured in cancelOffer the offers ",exe);
			throw new ConnectTimeoutException(exe.getLocalizedMessage());
		}
		catch(Exception exe)
		{
			Log.e("Exception:", "Exception occured in cancelOffer the offers ",exe);
			throw new TangoTabException("MyOffersDetailService", "cancelOffer", exe);
		}
		return message;
	}
	
}
