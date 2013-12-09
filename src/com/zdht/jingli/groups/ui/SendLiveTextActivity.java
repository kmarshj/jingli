package com.zdht.jingli.groups.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class SendLiveTextActivity extends BaseSendLiveActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	public static void launch(android.app.Activity activity, int acitivityId) {
		Intent intent = new Intent(activity, SendLiveTextActivity.class);
		intent.putExtra(EXTRA_ACTIVITYID, acitivityId);
		activity.startActivity(intent);
	}
	
	@Override
	protected void uploadImage() {
		if(!TextUtils.isEmpty(mStrImage)){
			super.uploadImage();
		}else {
			sendLive();
		}
	}
	
}
