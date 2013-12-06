package com.zdht.jingli.groups.provider;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.zdht.core.Event;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;

public class PosterBmpProvider extends ImageBmpProvider {

	private static PosterBmpProvider sInstance;
	
	private List<String> mListPosterBmp = new ArrayList<String>();
	
	
	public static PosterBmpProvider getInstance(){
		if(sInstance == null){
			sInstance = new PosterBmpProvider();
		}
		return sInstance;
	}
	
	private PosterBmpProvider(){
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		/*mBmpDefault = BitmapFactory.decodeResource(SCApplication.getApplication().getResources(), 
				R.drawable.ic_launcher);*/
	}
	
	@Override
	protected void addAvatarToCache(String strImagerUrl, Bitmap bmp) {
		super.addAvatarToCache(strImagerUrl, bmp);
		mMapImageUrlToImageBmp.put(strImagerUrl, bmp);
		mListPosterBmp.add(strImagerUrl);
		if(mListPosterBmp.size() > 50){
			String strPop = mListPosterBmp.get(0);
			mListPosterBmp.remove(strPop);
			mMapImageUrlToImageBmp.remove(strPop);
		}
	}
	
	@Override
	protected void removeCache(String strImagerUrl) {
		super.removeCache(strImagerUrl);
		mListPosterBmp.remove(strImagerUrl);
		mMapImageUrlToImageBmp.remove(strImagerUrl);
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
	}
	
}
