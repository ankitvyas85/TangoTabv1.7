package com.tangotab.nearMe.service;

import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.nearMe.dao.NearMeDao;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.nearMe.vo.NearMeVo;
/**
 * Get all Deal list from near me object.
 * 
 * @author dillip.lenka
 *
 */
public class NearMeService 
{
	/**
	 * Get list of Deals from near me Object.
	 * @param nearMeVo
	 * @return
	 * @throws TangoTabException
	 */
	public List<DealsDetailVo> getListOfDeals(NearMeVo nearMeVo) throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking getListOfDeals method with parameter as nearMeVo ", nearMeVo.toString());
		List<DealsDetailVo> dealsList =null;
		try
		{
			NearMeDao nearDao = new NearMeDao();
			dealsList = nearDao.getDealsList(nearMeVo);			
		}
		catch(ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException occured ", "ConnectTimeoutException occured in getDealsList() method ", e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch(Exception e)
		{
			Log.e("Exception occured ", "Exception occured in getDealsList() method ", e);
			throw new TangoTabException("NearMeService", "getListOfDeals", e);
		}
		return dealsList;
	}
}
