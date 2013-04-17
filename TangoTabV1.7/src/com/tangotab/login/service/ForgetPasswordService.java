package com.tangotab.login.service;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.services.TangoTabBaseService;
import com.tangotab.login.dao.LoginDao;
import com.tangotab.login.vo.LoginVo;
/**
 * This Class will check the authentication for login
 * 
 * @author dillip.lenka
 *
 */
public class ForgetPasswordService extends TangoTabBaseService
{
	/**
	 * This method will check the authentication for login
	 * @param loginUrl
	 * @return
	 */
	public String forgetPassword(LoginVo loginVo) throws TangoTabException
	{
		Log.v("Invoking forgetPassword method with loginVo as parameter ", loginVo.toString());
		String message = null;
		try
		{
			LoginDao loginDao = new LoginDao();
			message = loginDao.forgetPassword(loginVo);
		}catch(TangoTabException e)
		{
			Log.e("Exception :", "Exception occured in forgetPasswordService respone ", e);
			throw new TangoTabException("ForgetPasswordService", "forgetPasswordService", e);
		}
		return message;
	}
}
