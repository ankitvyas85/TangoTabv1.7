package com.tangotab.myOfferDetails.dao;

import java.io.IOException;

import org.apache.http.conn.ConnectTimeoutException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.signUp.xmlHandler.MessageHandler;
import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOffers.Vo.OffersDetailsVo;

/**
 * Dao class for doing auto and manual check in.
 * 
 * @author Dillip.Lenka
 *
 */

public class MyOffersDetailDao extends TangoTabBaseDao
{
	/**
	 * do auto check in for the select offers .
	 * @param offersDetailsVo
	 * @return
	 * @throws TangoTabException
	 */
	public String doCheckIn(OffersDetailsVo offersDetailsVo)throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking doCheckIn method with parameter offersDetailsVo", offersDetailsVo.toString());
		String message=null;
		TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
		ConnectionManager cManager = instance.getConManger();
		String checkinUrl =getCheckinUrl(offersDetailsVo);		
		MessageHandler messageHandler = new MessageHandler();
		instance.getXmlReader().setContentHandler(messageHandler);
		Log.v("checkinUrl is ", checkinUrl);
		cManager.setupHttpGet(checkinUrl);
		
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = messageHandler.getMessage();
				Log.v("Check in Message is", message);
			}
		}catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException", "ConnectTimeoutException occured in invoking check in service url and checkinUrl ="+checkinUrl);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			Log.e("Error", "IOException occured in invoking check in service url and checkinUrl ="+checkinUrl);
			throw new TangoTabException("MyOffersDetailDao", "doCheckIn", e);
		} catch (SAXException e)
		{
			Log.e("Error", "SAXException ocuuered in invoking check in service url and checkinUrl ="+checkinUrl);
			throw new TangoTabException("MyOffersDetailDao", "doCheckIn", e);
		}
		return message;
	}
	/**
	 * Cancel the offer from the user request.
	 * 
	 * @param conResId
	 * @return
	 * @throws TangoTabException
	 */
	public String cancelOffer(String conResId)throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking cancelOffer method with parameter conResId", conResId);
		String message=null;
		TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
		ConnectionManager cManager = instance.getConManger();
		String cancelUrl = getCancelUrl(conResId);		
		MessageHandler messageHandler = new MessageHandler();
		instance.getXmlReader().setContentHandler(messageHandler);
		Log.v("cancelUrl is ", cancelUrl);
		cManager.setupHttpGet(cancelUrl);	
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = messageHandler.getMessage();
				Log.v("Cancel offer Message is", message);
			}
		}catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException", "ConnectTimeoutException occured in invoking cancelOffer method ="+cancelUrl);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			Log.e("Error", "IOException occured in invoking cancelOffer method ="+cancelUrl);
			throw new TangoTabException("MyOffersDetailDao", "cancelOffer", e);
		} catch (SAXException e)
		{
			Log.e("Error", "SAXException ocuuered in invoking cancel the offer ="+cancelUrl);
			throw new TangoTabException("MyOffersDetailDao", "cancelOffer", e);
		}
		return message;
	}
	
	
	/**
	 * Get the check in url for auto check in and mannual check in.
	 * @param offersDetailsVo
	 * @return
	 */
	private String getCheckinUrl(OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking getCheckinUrl() method with parameter offersDetailsVo =", offersDetailsVo.toString());
		String checkinUrl =null;
		String mCurrentDate = offersDetailsVo.getCurrentDate();
		Log.v("presentdate", "presentdate" + mCurrentDate);
		if(!ValidationUtil.isNullOrEmpty(mCurrentDate))
			{
				
				String consumerresId = offersDetailsVo.getConResId();
				String restname = offersDetailsVo.getBusinessName();
				String dealname = offersDetailsVo.getDealName();
				String dealdetails = offersDetailsVo.getDealDescription();
				String deal_manager_emailid = offersDetailsVo.getDealManagerEmailId();
				if (dealname.contains(" ")) {
					dealname = dealname.replace(" ", "%20");
				}
				if (dealdetails.contains(" ")) {
					dealdetails = dealdetails.replace(" ", "%20");
				}
				if (restname.contains(" ")) {
					restname = restname.replace(" ", "%20");
				}
				if (deal_manager_emailid.contains(" ")) {
					deal_manager_emailid = deal_manager_emailid.replace(" ","%20");
				}
				if (dealdetails.contains("%"))
				{
					dealdetails = dealdetails.replace("%", "%25");
					if (dealdetails.contains(" "))
					{
						dealdetails = dealdetails.replace(" ", "%20");
					}
				}
					/**
					 * Generate the Checkin service url
					 */
				checkinUrl = AppConstant.baseUrl+ "/mydeals/checkin?consumerresId="+ TangoTabBaseDao.encodeURI(consumerresId)
							+ "&name="+ TangoTabBaseDao.encodeURI(offersDetailsVo.getFirstName())+ TangoTabBaseDao.encodeURI(offersDetailsVo.getLastName())+ "&restname="
							+ TangoTabBaseDao.encodeURI(offersDetailsVo	.getBusinessName())+ "&dealname="
							+ TangoTabBaseDao.encodeURI(offersDetailsVo.getDealName())+ "&dealdetails="
							+ TangoTabBaseDao.encodeURI(dealdetails)+ "&coordinate="+ AppConstant.locationLat
							+ ","+ AppConstant.locationLong+ "&restEmailId="+ TangoTabBaseDao.encodeURI(deal_manager_emailid)
							+ "&autocheckin="+ TangoTabBaseDao.encodeURI(offersDetailsVo.getAutoCheckIn());	
				Log.v("final checkinUrl is", checkinUrl);					
					

			}
		return checkinUrl;
	}
	/**
	 * Generate cancel URL from conResId.
	 * @param conResId
	 * @return
	 */
	public String getCancelUrl(String conResId)
	{
		Log.v("Invoking getCancelUrl() method with parameter conResId =", conResId);
		String cancelUrl = null;
		if(ValidationUtil.isNullOrEmpty(conResId))
			return cancelUrl;
		cancelUrl =AppConstant.baseUrl+ "/deals/removedeal?conResId="+ conResId;
		Log.v("cancelUrl is", cancelUrl);
		return cancelUrl;
	}
}
