package com.winhearts.arappmarket.download;

public interface SmartDownloadProgressListener {
	void onDownloadSize(int size, int fileSize, int state, String speed, String packageName);
}
