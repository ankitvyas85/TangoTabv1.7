package com.tangotab.signUp.service;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.services.TangoTabBaseService;
import com.tangotab.signUp.dao.SignUpDao;
import com.tangotab.signUp.vo.UserVo;
/**
 * New user to do sign up for authentication.
 * 
 * @author dillip.lenka
 *
 */
public class SignUpService extends TangoTabBaseService
{
	/**
	 * Do sign up for the new user.
	 * @param userVo
	 * @return
	 * @throws TangoTabException
	 */
	public String doSignUp(UserVo userVo) throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking doSignUp() method with parameter userVo is", userVo.toString());
		String msg =null;
		try
		{	
			SignUpDao dao = new SignUpDao();
			msg = dao.signUp(userVo);
			Log.v("Response sign up message is ", msg);
		}
		catch(ConnectTimeoutException e) 
		{
			Log.e("ConnectTimeoutException occured in doSignUp method ", e.getLocalizedMessage(), e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		} 
		catch(TangoTabException e) 
		{
			Log.e("Exception occured in doSignUp method ", e.getLocalizedMessage(), e);
			throw new TangoTabException("SignUpService", "doSignUp", e);
		} 
		return msg;
	}
	

}
