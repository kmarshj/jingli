package com.zdht.jingli.groups.event;

import org.json.JSONObject;

public class EditUserInfoEvent extends HttpPostEvent {

	private String mDescribe;
	private String mUserToken;

	public String getDescribe() {
		return mDescribe;
	}
	
	public String getUserToken() {
		return mUserToken;
	}

	public EditUserInfoEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
	}
	
	
	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if(isRequestSuccess()){
				mUserToken  = jsonObject.getString("userToken");
			}
		}
	}
	

}
