package com.tangotab.core.vo;

import java.io.Serializable;

import com.tangotab.login.vo.LoginVo;
/**
 * Deal information class
 * 
 * @author dillip.lenka
 *
 */
public class DealsVo extends LoginVo implements Serializable
{
	private static final long serialVersionUID = 1L;
		
	public String businessName;
	public String address;
	public String dealName;
	public String dealDescription;
	public String dealRestriction;
	public String imageUrl;
	public String startTime;
	public String endTime;
	
	
	/**
	 * @return the businessName
	 */
	public String getBusinessName() {
		return businessName;
	}
	/**
	 * @param businessName the businessName to set
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the dealName
	 */
	public String getDealName() {
		return dealName;
	}
	/**
	 * @param dealName the dealName to set
	 */
	public void setDealName(String dealName) {
		this.dealName = dealName;
	}
	/**
	 * @return the dealDescription
	 */
	public String getDealDescription() {
		return dealDescription;
	}
	/**
	 * @param dealDescription the dealDescription to set
	 */
	public void setDealDescription(String dealDescription) {
		this.dealDescription = dealDescription;
	}
	/**
	 * @return the dealRestriction
	 */
	public String getDealRestriction() {
		return dealRestriction;
	}
	/**
	 * @param dealRestriction the dealRestriction to set
	 */
	public void setDealRestriction(String dealRestriction) {
		this.dealRestriction = dealRestriction;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
	
	

}
