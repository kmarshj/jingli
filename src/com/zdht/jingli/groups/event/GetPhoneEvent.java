package com.zdht.jingli.groups.event;

import org.json.JSONObject;

public class GetPhoneEvent extends HttpPostEvent {

	private String mDescribe;

	public String getDescribe() {
		return mDescribe;
	}

	public GetPhoneEvent(int nEventCode) {
		super(nEventCode);
	}

	public String getPhone() {
		return mPhone;
	}

	private String mPhone;

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if (isNetSuccess()) {
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				mPhone = jsonObject.getString("mobile");
			}
		}
	}

}
