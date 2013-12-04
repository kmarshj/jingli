package com.zdht.jingli.groups.event;

import org.json.JSONObject;

/**
 * 通过身份证号获取学号事件
 * @author HJ
 *
 */
public class GetStuNoEvent extends HttpGetEvent {

	private String mDescribe;
	private String name;
	private String stuNo;
	
	public GetStuNoEvent(int nEventCode) {
		super(nEventCode);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				name = jsonObject.getString("name");
				stuNo = jsonObject.getString("stuNo");
			}
		}
	}
	
	public String getDescribe() {
		return mDescribe;
	}
	
	public String getName() {
		return name;
	}
	
	public String getStuNo() {
		return stuNo;
	}
}
