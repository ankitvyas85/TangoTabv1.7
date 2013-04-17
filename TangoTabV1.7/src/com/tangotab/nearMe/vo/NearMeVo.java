package com.tangotab.nearMe.vo;
/**
 * Class for Deal information.
 * 
 * @author dillip.lenka
 *
 */
public class NearMeVo
{
	private String cityName;
	private String zipCode;
	private double lattitude;
	private double longittude;
	private String setDistance;
	private String userId;
	private int pageIndex;
	private String appVersion;
	private String diningId;
	private int maxRange;
	private int minRange;
	private String type;
	private String date;
	
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return the lattitude
	 */
	public double getLattitude() {
		return lattitude;
	}
	/**
	 * @param lattitude the lattitude to set
	 */
	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}
	/**
	 * @return the longittude
	 */
	public double getLongittude() {
		return longittude;
	}
	/**
	 * @param longittude the longittude to set
	 */
	public void setLongittude(double longittude) {
		this.longittude = longittude;
	}
	/**
	 * @return the setDistance
	 */
	public String getSetDistance() {
		return setDistance;
	}
	/**
	 * @param setDistance the setDistance to set
	 */
	public void setSetDistance(String setDistance) {
		this.setDistance = setDistance;
	}
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
	
	
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	
	
	/**
	 * @return the appVersion
	 */
	public String getAppVersion() {
		return appVersion;
	}
	/**
	 * @param appVersion the appVersion to set
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	/**
	 * @return the diningId
	 */
	public String getDiningId() {
		return diningId;
	}
	/**
	 * @param diningId the diningId to set
	 */
	public void setDiningId(String diningId) {
		this.diningId = diningId;
	}
	/**
	 * @return the maxRange
	 */
	public int getMaxRange() {
		return maxRange;
	}
	/**
	 * @param maxRange the maxRange to set
	 */
	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}
	/**
	 * @return the minRange
	 */
	public int getMinRange() {
		return minRange;
	}
	/**
	 * @param minRange the minRange to set
	 */
	public void setMinRange(int minRange) {
		this.minRange = minRange;
	}
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NearMeVo [cityName=" + cityName + ", zipCode=" + zipCode
				+ ", lattitude=" + lattitude + ", longittude=" + longittude
				+ ", setDistance=" + setDistance + ", userId=" + userId
				+ ", pageIndex=" + pageIndex + ", appVersion=" + appVersion
				+ ", diningId=" + diningId + ", maxRange=" + maxRange
				+ ", minRange=" + minRange + ", type=" + type + ", date="
				+ date + ", getCityName()=" + getCityName() + ", getZipCode()="
				+ getZipCode() + ", getLattitude()=" + getLattitude()
				+ ", getLongittude()=" + getLongittude()
				+ ", getSetDistance()=" + getSetDistance() + ", getUserId()="
				+ getUserId() + ", getPageIndex()=" + getPageIndex()
				+ ", getAppVersion()=" + getAppVersion() + ", getDiningId()="
				+ getDiningId() + ", getMaxRange()=" + getMaxRange()
				+ ", getMinRange()=" + getMinRange() + ", getType()="
				+ getType() + ", getDate()=" + getDate() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
		
		
}
