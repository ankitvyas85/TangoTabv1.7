package com.tangotab.core.utils;
/**
 * Class will be used for get all application information.
 * 
 * @author dillip.lenka
 *
 */
public class ApplicationDetails
{
	private String appName;
	private String appPackage;
	private String versionName;
	private String versionCode;
	private String appInstallDate;
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	/**
	 * @return the appPackage
	 */
	public String getAppPackage() {
		return appPackage;
	}
	/**
	 * @param appPackage the appPackage to set
	 */
	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	/**
	 * @return the versionName
	 */
	public String getVersionName() {
		return versionName;
	}
	/**
	 * @param versionName the versionName to set
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	/**
	 * @return the versionCode
	 */
	public String getVersionCode() {
		return versionCode;
	}
	/**
	 * @param versionCode the versionCode to set
	 */
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	/**
	 * @return the appInstallDate
	 */
	public String getAppInstallDate() {
		return appInstallDate;
	}
	/**
	 * @param appInstallDate the appInstallDate to set
	 */
	public void setAppInstallDate(String appInstallDate) {
		this.appInstallDate = appInstallDate;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ApplicationInfo [appName=" + appName + ", appPackage="
				+ appPackage + ", versionName=" + versionName
				+ ", versionCode=" + versionCode + ", appInstallDate="
				+ appInstallDate + ", getAppName()=" + getAppName()
				+ ", getAppPackage()=" + getAppPackage()
				+ ", getVersionName()=" + getVersionName()
				+ ", getVersionCode()=" + getVersionCode()
				+ ", getAppInstallDate()=" + getAppInstallDate()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
	
	

}
