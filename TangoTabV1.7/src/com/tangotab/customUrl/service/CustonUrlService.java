package com.tangotab.customUrl.service;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.customUrl.dao.CustomUrlDao;
import com.tangotab.customUrl.vo.CustomDealVo;
import com.tangotab.nearMe.vo.DealsDetailVo;
/**
 * Class will be used to get deal matching to deal id and Deal date.
 * 
 * @author Dillip.Lenka
 *
 */
public class CustonUrlService 
{
	/**
	 * Get deal matching with the deal id and Deal Date.
	 * 
	 * @param customDealVo
	 * @return
	 * @throws TangoTabException
	 */
	public DealsDetailVo getCustomDeal(CustomDealVo customDealVo) throws ConnectTimeoutException,TangoTabException
	{
		DealsDetailVo dealsDetailVo =null;
		try
		{
			CustomUrlDao customDao = new CustomUrlDao();
			dealsDetailVo = customDao.getDeal(customDealVo);			
		}
		catch (ConnectTimeoutException exe)
		{
			Log.e("Exception ", "ConnectTimeoutException occured at getCustomDeal() method",exe);
			throw new ConnectTimeoutException(exe.getLocalizedMessage());
		}
		catch (TangoTabException e)
		{
			Log.e("Exception ", "Exception occured at getCustomDeal() method",e);
			throw new TangoTabException("CustonUrlService", "getCustomDeal", e);
		}
		return dealsDetailVo;
	}
}
