package com.tangotab.signUp.vo;

import com.tangotab.core.vo.TangoTabBaseVo;
/**
 * User detail information class.
 * @author dillip.lenka
 *
 */
public class UserVo extends TangoTabBaseVo
{
	/**
	 * @return the facebook_share
	 */
	public String getFacebook_share() {
		return facebook_share;
	}

	/**
	 * @param facebook_share the facebook_share to set
	 */
	public void setFacebook_share(String facebook_share) {
		this.facebook_share = facebook_share;
	}

	/**
	 * @return the twitter_share
	 */
	public String getTwitter_share() {
		return twitter_share;
	}

	/**
	 * @param twitter_share the twitter_share to set
	 */
	public void setTwitter_share(String twitter_share) {
		this.twitter_share = twitter_share;
	}

	private String first_name;
	private String last_name;
	private String zip_code;
	private String work_zip;
	private String facebook_share;
	private String twitter_share;
	private String mobile_phone;
	private String user_Id;
	private String email;
	private String password;
	private String promoText;
	/**
	 * Overloaded constructor
	 * @param first_name
	 * @param last_name
	 * @param zip_code
	 * @param mobile_phone
	 * @param user_Id
	 * @param email
	 * @param password
	 * @param promoText
	 */
	public UserVo(String first_name, String last_name, String zip_code,String work_zip,String facebook_share,
				String twitter_share,String mobile_phone,String user_Id,String email,String password,String promoText)
	{
		this.first_name = first_name;
		this.last_name = last_name;
		this.zip_code = zip_code;
		this.work_zip = work_zip;
		this.facebook_share = facebook_share;
		this.twitter_share =twitter_share;
		this.mobile_phone = mobile_phone;
		this.user_Id = user_Id;
		this.email = email;
		this.password = password;
		this.promoText = promoText;
	}

	/**
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}

	/**
	 * @param first_name the first_name to set
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	/**
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}

	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	/**
	 * @return the zip_code
	 */
	public String getZip_code() {
		return zip_code;
	}

	/**
	 * @param zip_code the zip_code to set
	 */
	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	/**
	 * @return the mobile_phone
	 */
	public String getMobile_phone() {
		return mobile_phone;
	}

	/**
	 * @param mobile_phone the mobile_phone to set
	 */
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	/**
	 * @return the user_Id
	 */
	public String getUser_Id() {
		return user_Id;
	}

	/**
	 * @param user_Id the user_Id to set
	 */
	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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
	 * @return the promoText
	 */
	public String getPromoText() {
		return promoText;
	}

	/**
	 * @param promoText the promoText to set
	 */
	public void setPromoText(String promoText) {
		this.promoText = promoText;
	}

	/**
	 * @return the work_zip
	 */
	public String getWork_zip() {
		return work_zip;
	}

	/**
	 * @param work_zip the work_zip to set
	 */
	public void setWork_zip(String work_zip) {
		this.work_zip = work_zip;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserVo [first_name=" + first_name + ", last_name=" + last_name
				+ ", zip_code=" + zip_code + ", work_zip=" + work_zip
				+ ", facebook_share=" + facebook_share + ", twitter_share="
				+ twitter_share + ", mobile_phone=" + mobile_phone
				+ ", user_Id=" + user_Id + ", email=" + email + ", password="
				+ password + ", promoText=" + promoText
				+ ", getFacebook_share()=" + getFacebook_share()
				+ ", getTwitter_share()=" + getTwitter_share()
				+ ", getFirst_name()=" + getFirst_name() + ", getLast_name()="
				+ getLast_name() + ", getZip_code()=" + getZip_code()
				+ ", getMobile_phone()=" + getMobile_phone()
				+ ", getUser_Id()=" + getUser_Id() + ", getEmail()="
				+ getEmail() + ", getPassword()=" + getPassword()
				+ ", getPromoText()=" + getPromoText() + ", getWork_zip()="
				+ getWork_zip() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
}
