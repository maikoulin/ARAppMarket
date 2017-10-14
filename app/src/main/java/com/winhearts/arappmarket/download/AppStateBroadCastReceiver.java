package com.winhearts.arappmarket.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.winhearts.arappmarket.model.DownRecordInfo;

public abstract class AppStateBroadCastReceiver extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent intent) {
		
		DownRecordInfo info = new DownRecordInfo();
		//info.setId(intent.getIntExtra("appId", 0));
		info.setFileSize(intent.getIntExtra("fileSize", 0));
		info.setDownlength(intent.getIntExtra("downloadSize", 0));
		info.setState(intent.getIntExtra("state", -1));
		info.setPackageName(intent.getStringExtra("packageName"));
		onReceiveAppState(info);
	}
	
	public abstract void onReceiveAppState(DownRecordInfo StateInfo);

}
