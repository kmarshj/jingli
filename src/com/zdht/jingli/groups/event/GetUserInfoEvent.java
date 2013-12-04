package com.zdht.jingli.groups.event;

import org.json.JSONObject;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.User;

public class GetUserInfoEvent extends HttpGetEvent {
	
	private String mDescribe;
	
	private User mUser; 

	public GetUserInfoEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
	}
	
	public String getDescribe() {
		return mDescribe;
	}

	public User getUser() {
		return mUser;
	}
	
	public boolean isLocalUser(){
		if(getUser() != null){
			return LocalInfoManager.getInstance().isLocalUser(String.valueOf(getUser().getUid()));
		}
		return false;
	}
	
	
	
	@Override
	public void run(Object... params) throws Exception {
		final String strUserId = (String)params[0];
		final String strUrl = String.format(URLUtils.URL_GetUserinfo, strUserId);
		super.run(strUrl);
		if(isNetSuccess()){
			SCApplication.print(mHttpRetString + "\r\n-----------------");
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if(isRequestSuccess()){
				mUser = new User(jsonObject, false);
			}
		}
	}
	

}
