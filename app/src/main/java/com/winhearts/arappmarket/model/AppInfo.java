package com.winhearts.arappmarket.model;

import android.graphics.drawable.Drawable;

/**
 * app相关信息
 * 
 * @author huyf
 * 
 */
public class AppInfo {
	private int id;
	private String appIconUrl;
	private String packageName;
	private int appIconId;
	private String name;
	private String downloadUrl;
	private Drawable appIcon;
	private String activityName;
	private int  status;
	private String json;
	private AppDetail appDetail;

	public int getAppIconId() {
		return appIconId;
	}

	public void setAppIconId(int appIconId) {
		this.appIconId = appIconId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppIconUrl() {
		return appIconUrl;
	}

	public void setAppIconUrl(String appIconUrl) {
		this.appIconUrl = appIconUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public AppDetail getAppDetail() {
		return appDetail;
	}

	public void setAppDetail(AppDetail appDetail) {
		this.appDetail = appDetail;
	}

}
