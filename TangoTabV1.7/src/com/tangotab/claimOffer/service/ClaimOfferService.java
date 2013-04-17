package com.tangotab.claimOffer.service;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.tangotab.claimOffer.dao.ClaimOfferDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.nearMe.vo.DealsDetailVo;
/**
 * Service class for claim an offer.
 * 
 * @author dillip.lenka
 *
 */
public class ClaimOfferService 
{
	/**
	 * Claim the selected offer.
	 * @param dealsDetailVo
	 * @return
	 * @throws TangoTabException
	 */
	public String claimTheOffer(DealsDetailVo dealsDetailVo) throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking the Method claimTheOffer() ", dealsDetailVo.toString());
		String message = null;
		try
		{
			ClaimOfferDao calimDao = new ClaimOfferDao();
			message = calimDao.claimOffer(dealsDetailVo);
			Log.v("message is ", message);
		}catch(ConnectTimeoutException exe)
		{
			Log.e("Exception ", "ConnectTimeoutException occured at claimOffer() method",exe);
			throw new ConnectTimeoutException(exe.getLocalizedMessage());
		}
		catch(Exception e)
		{
			Log.e("Exception ", "Exception occured at claimOffer() method",e);
			throw new TangoTabException("ClaimOfferService", "claimOffer", e);
		}
		return message;		
	}
}
