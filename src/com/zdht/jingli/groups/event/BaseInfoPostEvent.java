package com.zdht.jingli.groups.event;

import org.json.JSONObject;

public class BaseInfoPostEvent extends HttpPostEvent {

	private String mDescribe;
	
	public BaseInfoPostEvent(int nEventCode, boolean isNeedAddUser) {
		super(nEventCode);
		mIsNeedAddUser = isNeedAddUser;
	}

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if (isNetSuccess()) {
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
		}
	}
	
	public String getDescribe() {
		return mDescribe;
	}
	
}
