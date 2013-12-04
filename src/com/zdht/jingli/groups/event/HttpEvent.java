package com.zdht.jingli.groups.event;



import org.json.JSONObject;


import com.zdht.core.Event;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.utils.SystemUtils;


public abstract class HttpEvent extends Event {

	protected boolean   mIsNeedAddUser;
	
	protected boolean   mIsNeedPaged;
	
	protected boolean 	mIsNetSuccess; 
	protected boolean 	mIsRequestSuccess;
	
	protected boolean   mIsGetAll;
	
	protected String	mHttpRetString;
	
	protected String	mUrl;

	public HttpEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = false;
		mIsNeedPaged = false;
		mIsGetAll = false;
		mIsAsyncRun = true;
		mIsNotifyAfterRun = true;
	}

	@Override
	public void onPreRun() {
		super.onPreRun();
		mIsNetSuccess = false;
		mIsRequestSuccess = false;
		mHttpRetString = null;
	}

	@Override
	public Object getReturnParam() {
		return Boolean.valueOf(mIsRequestSuccess);
	}

	public boolean 	isNetSuccess(){
		return mIsNetSuccess;
	}
	
	public boolean 	isRequestSuccess(){
		return mIsRequestSuccess;
	}
	
	public String	getHttpRetString(){
		return mHttpRetString;
	}
	
	public final String	getUrl(){
		return mUrl;
	}
	
	protected void	findUrl(Object ...params){
		mUrl = (String)params[0];
	}
	
	/*protected String getUrlParam(String strUrl,String strParamName){
		int nIndexStart = strUrl.indexOf("&" + strParamName);
		if(nIndexStart >= 0){
			nIndexStart = strUrl.indexOf("=", nIndexStart) + 1;
			int nEnd = strUrl.indexOf("&",nIndexStart);
			if(nEnd == -1){
				nEnd = strUrl.length();
			}
			return strUrl.substring(nIndexStart, nEnd);
		}
		return "";
	}
	
	protected String setUrlParam(String strUrl,String strParamName,String strParamValue){
		final StringBuffer sb = new StringBuffer(strUrl);
		int nIndexStart = sb.indexOf("&" + strParamName);
		if(nIndexStart >= 0){
			nIndexStart = sb.indexOf("=", nIndexStart);
			if(nIndexStart >= 0){
				sb.insert(nIndexStart + 1, strParamValue);
			}
		}
		return sb.toString();
	}
	
	protected boolean checkRequestSuccess(JSONObject jsonObject){
		try{
			mIsRequestSuccess = "true".equals(jsonObject.getString("ok"));
		}catch(Exception e){
			e.printStackTrace();
			mIsRequestSuccess = false;
		}
		return mIsRequestSuccess;
	}*/
	
	protected String addUrlCommonParams(String strUrl){
		StringBuilder buf = new StringBuilder(strUrl);
		final String sttUserId    = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
		buf.append("&userId=").append(sttUserId);
		return buf.toString();
	}
	
	protected String setPageCount(String strUrl){
		StringBuilder buf = new StringBuilder(strUrl);
		if(mIsGetAll){
			buf.append("&pageSize=").append("100");
			return buf.toString();
		}
		if(SystemUtils.getScreenWidth() > 720){
			buf.append("&pageSize=").append("20");
		}else {
			buf.append("&pageSize=").append("10");
		}
		return buf.toString();
	}
	
	protected int getPageSize() {
		if(mIsGetAll){
			return 100;
		}
		if(SystemUtils.getScreenWidth() > 720){
			return 20;
		}
		return 10;
	}
	
	
	protected boolean checkRequestSuccess(String strJson){
		try{
			JSONObject jsonObject = new JSONObject(strJson);
			mIsRequestSuccess = jsonObject.getString("status").equals("1");
		}catch(Exception e){
			e.printStackTrace();
			mIsRequestSuccess = false;
		}
		return mIsRequestSuccess;
	}
	
	protected boolean checkRequestSuccess(JSONObject jsonObject){
		try{
			mIsRequestSuccess = jsonObject.getString("status").equals("1");
		}catch(Exception e){
			e.printStackTrace();
			mIsRequestSuccess = false;
		}
		return mIsRequestSuccess;
	}
	
}
