package com.zdht.jingli.groups.event;

import org.json.JSONObject;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.model.Activity;


public class GetActivityEvent extends HttpGetEvent {

	private String mDescribe;
	private Activity activity;
	
	public String getDescribe() {
		return mDescribe;
	}
	
	public GetActivityEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
		setIsWaitRunWhenRunning(true);
	}
	

	@Override
	public void run(Object... params) throws Exception {
		activity = null;
		super.run(params);
		
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			SCApplication.print("GetActivityEvent-------------" + mHttpRetString);
			if (isRequestSuccess()) {
				activity = new Activity(jsonObject.getJSONObject("activity"));
			}
		}
	}

	public Activity getActivity() {
		return activity;
	}
}
