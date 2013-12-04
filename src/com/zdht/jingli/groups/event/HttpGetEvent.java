package com.zdht.jingli.groups.event;

import android.text.TextUtils;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.utils.HttpUtils;


public class HttpGetEvent extends HttpEvent {

	private Object[] params;

	
	public Object[] getRequestParams() {
		return params;
	}
	
	public HttpGetEvent(int nEventCode) {
		super(nEventCode);
	}

	@Override
	public void run(Object... params) throws Exception{
		this.params = params;
		findUrl(params);
		String strUrl = getUrl();
		if(mIsNeedAddUser){
			strUrl = addUrlCommonParams(strUrl);
		}
		if(mIsNeedPaged){
			strUrl = setPageCount(strUrl);
		}
		SCApplication.print("-------------" + strUrl);
		mHttpRetString = HttpUtils.doGetString(strUrl);
//		SCApplication.print("-------------" + mHttpRetString);
		if(TextUtils.isEmpty(mHttpRetString)){
			mIsNetSuccess = false;
			mIsRequestSuccess = false;
		}else{
			mIsNetSuccess = true;
			checkRequestSuccess(mHttpRetString);
		}
	}
}
