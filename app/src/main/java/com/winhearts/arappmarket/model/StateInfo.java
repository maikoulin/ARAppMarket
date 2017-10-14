package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 *  下载状态信息，包括了下载大小、文件大下等
 */
public class StateInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private int  downloadSize;
	private int  fileSize;
	private int	 id;
	private int  state;
	private String speed;
	private String packageName;

	public int getDownloadSize() {
		return downloadSize;
	}
	public void setDownloadSize(int downloadSize) {
		this.downloadSize = downloadSize;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	@Override
	public String toString() {
		return "StateInfo [downloadSize=" + downloadSize + ", fileSize=" + fileSize + ", id=" + id + ", state=" + state
				+ ", speed=" + speed + ", packageName=" + packageName + "]";
	}
	
}
