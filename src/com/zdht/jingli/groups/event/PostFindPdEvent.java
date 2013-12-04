package com.zdht.jingli.groups.event;

import org.json.JSONObject;

public class PostFindPdEvent extends HttpPostEvent{
	private String  mDescribe;
	
	private String password;
	
	private String nickname;
	
	public PostFindPdEvent(int nEventCode) {
		super(nEventCode);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if(isRequestSuccess()){
				password = jsonObject.getString("password");
				nickname = jsonObject.getString("nickname");
			}
		}
	}
	
	public String getPd() {
		return password;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getDescribe() {
		return mDescribe;
	}
}
