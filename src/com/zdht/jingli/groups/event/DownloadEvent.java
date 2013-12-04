package com.zdht.jingli.groups.event;

import android.os.Handler;
import android.util.Log;

import com.zdht.core.Event;
import com.zdht.utils.HttpUtils;
import com.zdht.utils.HttpUtils.ProgressRunnable;


public class DownloadEvent extends Event {

	protected ProgressRunnable 	mRunnable;
	protected Handler 			mHandler;
	
	protected boolean			mIsSuccess;
	protected String			mFileSavePath;
	
	public DownloadEvent(int nEventCode,ProgressRunnable pr,Handler handler) {
		super(nEventCode);
		setIsAsyncRun(true);
		setIsNotifyAfterRun(true);
		
		mRunnable = pr;
		mHandler = handler;
	}
	
	public boolean 	isSuccess(){
		return mIsSuccess;
	}
	
	public String	getFileSavePath(){
		return mFileSavePath;
	}

	@Override
	public void run(Object... params) throws Exception {
		final String strUrl = (String)params[0];
		final String strSavePath = (String)params[1];
		mFileSavePath = strSavePath;
		
		mIsSuccess = HttpUtils.doDownload(strUrl, strSavePath, mRunnable, mHandler, null);
		
	}

}
