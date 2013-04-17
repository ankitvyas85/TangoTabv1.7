package com.tangotab.login.dao;

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
import com.tangotab.login.vo.LoginVo;
import com.tangotab.login.xmlHandler.ForgetPasswordHandler;
import com.tangotab.login.xmlHandler.UserValidationXmlHandler;
import com.tangotab.signUp.vo.UserVo;
/**
 * This Class will check the authentication for user
 * 
 * @author dillip.lenka
 *
 */
public class LoginDao extends TangoTabBaseDao
{
	public static List<UserVo> userVoList = null;

	/**
	 * This method will check the authentication for login
	 * 
	 * @param loginUrl
	 * @return
	 */
	public String doLogin(LoginVo loginVo) throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking the method doSignIn() with url is ", loginVo.toString());
		String message = null;		
		String loginUrl = getLoginUrl(loginVo);
		Log.v("loginUrl is ", loginUrl);
		if(!ValidationUtil.isNullOrEmpty(loginUrl))
		{
			TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
			ConnectionManager cManager = instance.getConManger();
			UserValidationXmlHandler userHandler = new UserValidationXmlHandler();
			instance.getXmlReader().setContentHandler(userHandler);
			cManager.setupHttpGet(loginUrl);
			Log.v("Login url is ", loginUrl);			
			try {
				InputSource m_is = cManager.makeGetRequestGetResponse();
				if (m_is != null) {
					Log.v("response", "" + m_is);
					instance.getXmlReader().parse(m_is);
					message = userHandler.getMessage();
					Log.v("message is ", message);
					if (ValidationUtil.isNullOrEmpty(message)){
						userVoList = userHandler.getUserDetailsList();
					}
				}
			}
			catch (ConnectTimeoutException e)
			{
				Log.e("ConnectTimeoutException :", "ConnectTimeoutException occured in login service respone ", e);
				throw new ConnectTimeoutException(e.getLocalizedMessage());
			}
			catch (IOException e)
			{
				Log.e("Exception :", "IOException occured in login service respone ", e);
				throw new TangoTabException("LoginDao", "doSignIn", e);
			}
			catch (SAXException e)
			{
				Log.e("Exception :", "IOException occured in login service respone ", e);
				throw new TangoTabException("LoginDao", "doSignIn", e);
			}
		}
		return message;
	}
	
	/**
	 * This method will check the authentication for login
	 * 
	 * @param loginUrl
	 * @return
	 */
	public String forgetPassword(LoginVo loginVo) throws TangoTabException
	{
		Log.v("Invoking forgetPassword() method :", "forgetPassUrl =" + loginVo.toString());
		String message =null;		
		try {
			String forgetPassUrl = getForgetPasswordUrl(loginVo);
			Log.v("forgetPassUrl is ", forgetPassUrl);
			TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
			ConnectionManager cManager = instance.getConManger();	
			ForgetPasswordHandler xHandler = new ForgetPasswordHandler();
			instance.getXmlReader().setContentHandler(xHandler);
			cManager.setupHttpGet(forgetPassUrl);
			InputSource m_is = cManager.makeGetRequestGetResponse();
			
			if(m_is!=null)
			{
				instance.getXmlReader().parse(m_is);
				message = xHandler.getForgetMessage();	
				Log.v("Response message is ", message);
			}
		}catch (IOException e)
		{	
			Log.e("Exception :", "IOException occured in forget service respone ", e);
			throw new TangoTabException("LoginDao", "forgetPassword", e);
		}
		catch (SAXException e)
		{
			Log.e("Exception :", "SAXException occured in forget service respone ", e);
			throw new TangoTabException("LoginDao", "forgetPassword", e);
		}		
		return message;
		
	}
	/**
	 * This method will generate the url for login
	 * 
	 * @param loginVo
	 * @return
	 */
	private String getLoginUrl(LoginVo loginVo)
	{
		String loginUrl =null;
		if(!ValidationUtil.isNull(loginVo))
		{
			if(!ValidationUtil.isNullOrEmpty(loginVo.getUserId()) && !ValidationUtil.isNullOrEmpty(loginVo.getPassword()))
					loginUrl =AppConstant.baseUrl + '/' + "uservalidation?emailId=" + loginVo.getUserId() + "&password=" + loginVo.getPassword()+ "&phone_uid=" + loginVo.getPhoneId()+ "&os_id=" + loginVo.getOsId()+ "&tt_app_id=" + loginVo.getTtAppId()+ "&network_id=" + loginVo.getNetworkId(); //+ "&phone_uid=" + loginVo.getPhoneUId();
					//&phone_id=12345&os_id=12345&tt_app_id=1.7&network_id=1&phone_uid=1
		}
		return loginUrl;
	}
	/**
	 * This method will generate the url for login
	 * 
	 * @param loginVo
	 * @return
	 */
	private String getForgetPasswordUrl(LoginVo loginVo)
	{
		String forgetPassUrl =null;
		if(!ValidationUtil.isNull(loginVo))
		{
			if(!ValidationUtil.isNullOrEmpty(loginVo.getUserId()))
				forgetPassUrl = AppConstant.baseUrl+"/forgotpassword/checkuser?emailId="+loginVo.getUserId();
		}
		return forgetPassUrl;
	}
	
	 
}
