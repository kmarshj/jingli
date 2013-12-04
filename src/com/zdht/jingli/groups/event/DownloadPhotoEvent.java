package com.zdht.jingli.groups.event;

import android.os.Handler;

import com.zdht.jingli.groups.model.XMessage;
import com.zdht.utils.HttpUtils.ProgressRunnable;

public class DownloadPhotoEvent extends DownloadEvent {
	
	protected XMessage 		mMessage;
	protected boolean		mIsDownloadThumb = true;

	public DownloadPhotoEvent(int nEventCode,XMessage m,boolean bDownloadThumb,
			ProgressRunnable pr,Handler handler) {
		super(nEventCode,pr,handler);
		mMessage = m;
		mIsDownloadThumb = bDownloadThumb;
	}

	@Override
	public Object getReturnParam() {
		return mMessage;
	}

	@Override
	public void run(Object... params) throws Exception {
		String strUrl = null;
		String strSavePath = null;
		if(mIsDownloadThumb){
			strUrl = mMessage.getThumbPhotoDownloadUrl();
			strSavePath = mMessage.getThumbPhotoFilePath();
		}else{
			strUrl = mMessage.getPhotoDownloadUrl();
			strSavePath = mMessage.getPhotoFilePath();
		}
		super.run(strUrl,strSavePath);
		
		mMessage.setDownloaded();
		mMessage.updateDB();
	}

}
