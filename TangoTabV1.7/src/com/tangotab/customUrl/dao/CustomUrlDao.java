package com.tangotab.customUrl.dao;

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
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.customUrl.vo.CustomDealVo;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.nearMe.xmlHandler.DealDetailHandler;

/**
 * Class will be used to get deal matching to deal id and Deal date.
 * 
 * @author Dillip.Lenka
 *
 */
public class CustomUrlDao extends TangoTabBaseDao
{
	/**
	 * Get deal matching with the deal id and Deal Date.
	 * 
	 * @param customDealVo
	 * @return
	 * @throws TangoTabException
	 */
	public DealsDetailVo getDeal(CustomDealVo customDealVo)throws ConnectTimeoutException,TangoTabException
	{
		DealsDetailVo dealsDetailVo =null;
		Log.v("Invoking getDeal() method  with parameter CustomDealVo ", "customDealVo = "+customDealVo.toString());
		List<DealsDetailVo> dealsList =null;		
		TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
		ConnectionManager cManager = instance.getConManger();
		DealDetailHandler dealHandler = new DealDetailHandler();
		instance.getXmlReader().setContentHandler(dealHandler);
		String dealUrl = getDealUrl(customDealVo);
		Log.v("Custom deal URL: ", dealUrl);		
		cManager.setupHttpGet(dealUrl);	
		
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
				if(m_is!=null)
				{
					instance.getXmlReader().parse(m_is);
					dealsList = dealHandler.getDealsList();
					if(!ValidationUtil.isNullOrEmpty(dealsList))
						dealsDetailVo = dealsList.get(0);
				}
			}
			catch (ConnectTimeoutException exe)
			{
				Log.e("Exception occured ", "Connection time out exception occured in getDeal() method ", exe);
				throw new ConnectTimeoutException(exe.getLocalizedMessage());
			}catch (IOException e)
			{
				Log.e("Exception occured ", "IOException occured in getDeal() method ", e);
				throw new TangoTabException("CustomUrlDao", "getDeal", e);
			} catch (SAXException e)
			{
				Log.e("Exception occured ", "SAXException occured in getDeal() method.", e);
				throw new TangoTabException("CustomUrlDao", "getDeal", e);
			}
			return dealsDetailVo;
	}
	
	/**
	 * Get the Deal URL from customDealVo
	 * 
	 * @param customDealVo
	 * @return
	 */
	private String getDealUrl(CustomDealVo customDealVo)
	{
		Log.v("Invoking getDealUrl() method with parameter customDealVo= ", customDealVo.toString());
		String dealUrl =null;
		String dealId = customDealVo.getDealId();
		String dealDate = customDealVo.getDealDate();
		String locLat = customDealVo.getLocLat();
		String locLang = customDealVo.getLocLong();
		dealUrl =  AppConstant.baseUrl+"/deals/get?dealId="+ dealId+ "&date="+dealDate+ "&coordinate="+ locLat+ ","+ locLang;
		Log.v("Custom deal Url is", dealUrl);
		return dealUrl;
	}
}
