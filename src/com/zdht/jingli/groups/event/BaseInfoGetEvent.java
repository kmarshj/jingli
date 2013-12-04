package com.zdht.jingli.groups.event;

import org.json.JSONObject;


public class BaseInfoGetEvent extends HttpGetEvent {

	private int    mStatus;
	private String mDescribe;
	
	
	public BaseInfoGetEvent(int nEventCode, boolean isNeedAddUser) {
		super(nEventCode);
		mIsNeedAddUser = isNeedAddUser;
	}

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if (isNetSuccess()) {
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mStatus   = jsonObject.getInt("status");
			mDescribe = jsonObject.getString("describe");
		}
	}

	public int getStatus() {
		return mStatus;
	}

	public String getDescribe() {
		return mDescribe;
	}
	
	public void init() {
		mStatus = -1;
		mDescribe = null;
	}
	
}
