package com.zdht.jingli.groups.provider;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.SCApplication;

public class AvatarBmpProvider extends ImageBmpProvider {

	private static AvatarBmpProvider sInstance;
	
	private List<String> mListAvatarBmp = new ArrayList<String>();
	
	
	public static AvatarBmpProvider getInstance(){
		if(sInstance == null){
			sInstance = new AvatarBmpProvider();
		}
		return sInstance;
	}
	
	private AvatarBmpProvider(){
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
//		mBmpDefault = BitmapFactory.decodeResource(SCApplication.getApplication().getResources(), 
//				R.drawable.default_avatar);
	}
	
	@Override
	protected void addAvatarToCache(String strImagerUrl, Bitmap bmp) {
		super.addAvatarToCache(strImagerUrl, bmp);
		mMapImageUrlToImageBmp.put(strImagerUrl, bmp);
		mListAvatarBmp.add(strImagerUrl);
		if(mListAvatarBmp.size() > 50){
			String strPop = mListAvatarBmp.get(0);
			mListAvatarBmp.remove(strPop);
			mMapImageUrlToImageBmp.remove(strPop);
		}
	}
	
	@Override
	protected void removeCache(String strImagerUrl) {
		super.removeCache(strImagerUrl);
		mListAvatarBmp.remove(strImagerUrl);
		mMapImageUrlToImageBmp.remove(strImagerUrl);
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
	}
	
	@Override
	public Bitmap loadImage(String imagerUrl){
		Bitmap mBitmap = super.loadImage(imagerUrl);
		if(mBitmap == null) {
			mBitmap = BitmapFactory.decodeResource(SCApplication.getApplication().getResources(), R.drawable.avatar);
		}
		return mBitmap;
	}
	
	public void loadImage(View view, String imageUrl) {
		
	}
}
