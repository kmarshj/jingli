package com.zdht.jingli.groups.event;

import org.json.JSONObject;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.model.Group;

public class GetGroupInfoEvent extends HttpGetEvent {
	
	private String mDescribe;
	private Group mGroup;

	public GetGroupInfoEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
	}

	@Override
	public Object getReturnParam() {
		return mGroup;
	}

	public String getDescribe() {
		return mDescribe;
	}

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		SCApplication.print("-------------" + getHttpRetString());
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if(isRequestSuccess()){
				mGroup = new Group(jsonObject, true);
			}
		}
	}
	
}
