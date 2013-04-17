package com.tangotab.login.vo;
/**
 * Login information class.
 * 
 * @author dillip.lenka
 *
 */
public class LoginVo 
{
	private String userId;
	
	private String password;
	private String phone_id,os_id,tt_app_id,network_id,phone_uid;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the phone_id
	 */
	public String getPhoneId() {
		return phone_id;
	}

	/**
	 * @param phone_id the phone_id to set
	 */
	public void setPhoneId(String phone_id) {
		this.phone_id = phone_id;
	}
	
	
	
	
	
	/**
	 * @return the os_id
	 */
	public String getOsId() {
		return os_id;
	}

	/**
	 * @param the phone_id to set
	 */
	public void setOsId(String os_id) {
		this.os_id = os_id;
	}
	
	/**
	 * @return the tt_app_id
	 */
	public String getTtAppId() {
		return tt_app_id;
	}

	/**
	 * @param the tt_app_id to set
	 */
	public void setTtAppId(String tt_app_id) {
		this.tt_app_id = tt_app_id;
	}
	
	/**
	 * @return the network_id
	 */
	public String getNetworkId() {
		return network_id;
	}

	/**
	 * @param the network_id to set
	 */
	public void setNetworkId(String network_id) {
		this.network_id = network_id;
	}
	
	/**
	 * @return the phone_uid
	 */
	public String getPhoneUId() {
		return phone_uid;
	}

	/**
	 * @param the phone_uid to set
	 */
	public void setPhoneUId(String phone_uid) {
		this.phone_uid = phone_uid;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LoginVo [userId=" + userId + ", password=" + password
				+ ", phone_id=" + phone_id + ", os_id=" + os_id 
				+ ", tt_app_id=" + tt_app_id + ", network_id=" + network_id 
				+ ", phone_uid=" + phone_uid + ", getUserId()=" + getUserId() + ", getPassword()="
				+ getPassword() + ", getPhoneId()=" + getPhoneId() 
				+ ", getOsId()=" + getOsId() + ", getTtAppId()="
				+ getTtAppId() + ",getNetworkId()=" + getNetworkId() 
				+ ", getPhoneUId()=" + getPhoneUId() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
