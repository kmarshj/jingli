package com.zdht.jingli.groups.provider;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zdht.core.Event;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;

public class LiveBmpProvider extends ImageBmpProvider {

	private static LiveBmpProvider sInstance;
	
	private List<String> mListLiveBmp = new ArrayList<String>();
	
	
	public static LiveBmpProvider getInstance(){
		if(sInstance == null){
			sInstance = new LiveBmpProvider();
		}
		return sInstance;
	}
	
	private LiveBmpProvider(){
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		/*mBmpDefault = BitmapFactory.decodeResource(SCApplication.getApplication().getResources(), 
				R.drawable.ic_launcher);*/
	}
	
	@Override
	protected void addAvatarToCache(String strImagerUrl, Bitmap bmp) {
		super.addAvatarToCache(strImagerUrl, bmp);
		mMapImageUrlToImageBmp.put(strImagerUrl, bmp);
		mListLiveBmp.add(strImagerUrl);
		if(mListLiveBmp.size() > 50){
			String strPop = mListLiveBmp.get(0);
			mListLiveBmp.remove(strPop);
			mMapImageUrlToImageBmp.remove(strPop);
		}
	}
	
	@Override
	protected void removeCache(String strImagerUrl) {
		super.removeCache(strImagerUrl);
		mListLiveBmp.remove(strImagerUrl);
		mMapImageUrlToImageBmp.remove(strImagerUrl);
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
	}
	
}
