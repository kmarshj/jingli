package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;

import com.zdht.jingli.groups.URLUtils;
import com.zdht.utils.HttpUtils;
import com.zdht.utils.HttpUtils.ProgressRunnable;

public class PostPhotoEvent extends PostFileEvent {

	private ProgressRunnable 	mRunnable;
	private Handler 			mHandler;
	private String				mFilePath;
	
	public PostPhotoEvent(int nEventCode,
			String strFilePath,ProgressRunnable pr,Handler handler) {
		super(nEventCode);
		mFilePath = strFilePath;
		mRunnable = pr;
		mHandler = handler;
	}
	
	@Override
	public void run(Object... params) throws Exception {
		String strUrl = URLUtils.URL_PostFile;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("key", URLUtils.KEY));
		list.add(new BasicNameValuePair("type", "5"));
		final List<String> listImagePath = new ArrayList<String>();
		listImagePath.add(mFilePath);
		super.run(strUrl,list,listImagePath);
	}

	@Override
	protected String onPost(String strUrl,
			List<NameValuePair> listNameValuePair, List<String> mListImagePath) {
		return HttpUtils.doPost(strUrl, listNameValuePair,mListImagePath,mRunnable,mHandler);
	}
}
