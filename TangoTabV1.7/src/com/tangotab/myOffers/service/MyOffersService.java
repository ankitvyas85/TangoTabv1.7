package com.tangotab.myOffers.service;

import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.myOffers.Dao.MyOffersDao;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * Get all list of offers calimed by the user.
 * 
 * @author dillip.lenka
 *
 */
public class MyOffersService
{

	public List<OffersDetailsVo> getOffers(int count,LoginVo loginVo) throws ConnectTimeoutException, TangoTabException
	{
		Log.v("Invoking getOffers() method with parameter", "loginVo = "+loginVo.toString()+"count ="+count);
		List<OffersDetailsVo> offersList =null;
		try
		{
			MyOffersDao dao = new MyOffersDao();
			offersList = dao.getOfferList(count, loginVo);
			
		}
		catch(ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occures when invoking service to get all Offers ");	
			throw new TangoTabException("MyOffersService", "getOffers", e);
		}
		catch(Exception e)
		{
			Log.e("Error ", "Exception occures when invoking service to get all Offers ");	
			throw new TangoTabException("MyOffersService", "getOffers", e);
		}
		return offersList;
	}
	
	
}
