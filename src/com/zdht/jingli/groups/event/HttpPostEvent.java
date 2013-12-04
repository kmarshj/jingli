package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.utils.HttpUtils;


public class HttpPostEvent extends HttpEvent {

	private List<NameValuePair> mListNameValuePair;
	
	public List<NameValuePair> getAllPostNameValuePair(){
		return mListNameValuePair;
	}
	
	public HttpPostEvent(int nEventCode) {
		super(nEventCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Object... params) throws Exception {
		findUrl(params);
		
		String strUrl = getUrl();
		SCApplication.print( "http post:" + strUrl);
		List<NameValuePair> listNameValuePair = (List<NameValuePair>)params[1];
		if(listNameValuePair == null){
			listNameValuePair = new ArrayList<NameValuePair>();
		}
		
		if(mIsNeedAddUser){
			listNameValuePair.add(new BasicNameValuePair("userId", LocalInfoManager.getInstance().getmLocalInfo().getUserId()));
			//Log.d("mydebug", LocalInfoManager.getInstance().getmLocalInfo().getUserId() + "----------");
		}
		
		mListNameValuePair = listNameValuePair;
		onAddNameValuePair(listNameValuePair);
		List<String> mListImagePath = params.length >= 3 ? 
				(List<String>)params[2] : null;
		
		mHttpRetString = onPost(strUrl, listNameValuePair, mListImagePath);
		Log.d("mydebug", mHttpRetString + "-------------返回值");
		if(TextUtils.isEmpty(mHttpRetString)){
			mIsNetSuccess = false;
			mIsRequestSuccess = false;
		}else{
			mIsNetSuccess = true;
			checkRequestSuccess(getHttpRetString());
		}
	}

	protected void onAddNameValuePair(List<NameValuePair> listNameValuePair){
	}
	
	
	protected String onPost(String strUrl,
			List<NameValuePair> listNameValuePair,List<String> mListImagePath){
		return HttpUtils.doPost(strUrl, listNameValuePair, mListImagePath, null, null);
	}
	
	public List<NameValuePair> getListNameValuePair() {
		return mListNameValuePair;
	}
}
