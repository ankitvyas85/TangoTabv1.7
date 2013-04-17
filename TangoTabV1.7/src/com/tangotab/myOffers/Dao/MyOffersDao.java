package com.tangotab.myOffers.Dao;

import java.io.IOException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.xmlHandler.OfferDetailHandler;
import com.tangotab.signUp.xmlHandler.MessageHandler;
/**
 * Dao class to retrieve all the offers claimed by the end user.
 * .
 * @author Dillip.Lenka
 *
 */
public class MyOffersDao extends TangoTabBaseDao
{
	/**
	 * Get list of offers for my offers tab
	 * @param count
	 * @param loginVo
	 * @return
	 * @throws TangoTabException
	 */
	public List<OffersDetailsVo> getOfferList(int count ,LoginVo loginVo) throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking getOfferList() method  with parameter", "loginVo = "+loginVo.toString()+"count ="+count);
		TangoTabBaseDao instance = TangoTabBaseDao.getInstance();	
		ConnectionManager cManager = instance.getConManger();
		List<OffersDetailsVo> offersDetailsList= null;
		String myOffersUrl = getMyOffersUrl(count, loginVo);
		Log.v("myOffersUrl  is", myOffersUrl);
			
		OfferDetailHandler offerHandler = new OfferDetailHandler();			
		instance.getXmlReader().setContentHandler(offerHandler);
		cManager.setupHttpGet(myOffersUrl);		
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null)
			{
				instance.getXmlReader().parse(m_is);
				offersDetailsList = offerHandler.getOffersDetailList();
				Log.v("DoInBackGround List is", String.valueOf(offersDetailsList.size()));
			}				
		}catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occures when invoking service to get all Offers ");			
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			Log.e("Error ", "IOException occures when invoking service to get all Offers ");			
			throw new TangoTabException("MyOffersDao", "getOfferList", e);
		} catch (SAXException e)
		{
			Log.e("Error ", "SAXException occures when invoking service to get all Offers ");	
			throw new TangoTabException("MyOffersDao", "getOfferList", e);
		}
		return offersDetailsList;	
	}
	
	/**
	 * Generate the my offers 
	 * @param count
	 * @param loginVo
	 * @return
	 */
	private String getMyOffersUrl(int count,LoginVo loginVo)
	{
		Log.v("Invoking getMyOffersUrl method  is", "loginVo = "+loginVo.toString()+"count ="+count);
		String myOffersUrl = null;		
		myOffersUrl = AppConstant.baseUrl + "/mydeals/alldeals?emailId=" + loginVo.getUserId()
				+ "&password=" + loginVo.getPassword() + "&noOfdeals=0&pageIndex=" + count;
		Log.v("myOffersUrl is ", myOffersUrl);
		return myOffersUrl;
	}
}
