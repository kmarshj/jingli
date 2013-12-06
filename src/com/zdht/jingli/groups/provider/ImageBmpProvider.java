package com.zdht.jingli.groups.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.utils.SchoolUtils;

public abstract class ImageBmpProvider implements OnEventListener {
	

	protected HashMap<String, Bitmap> mMapImageUrlToImageBmp = new HashMap<String, Bitmap>();
	
	
	public HashMap<String, Bitmap> getmMapImageUrlToImageBmp() {
		return mMapImageUrlToImageBmp;
	}



	private boolean	mIsDownloading;
	private List<String> mListUrlWaitDownload = new ArrayList<String>();
	private List<String> mListUrlDownloading  = new ArrayList<String>();
	
	protected Bitmap	mBmpDefault;
	
	public Bitmap loadImage(String imagerUrl){
		if(TextUtils.isEmpty(imagerUrl)) {
			return null;
		}
		Bitmap bmp = mMapImageUrlToImageBmp.get(imagerUrl);
		if(bmp == null){
			String strFileName = SchoolUtils.getFileNameFronUrl(imagerUrl);
			if(strFileName == null) {
				return null;
			}
			bmp = BitmapFactory.decodeFile(FilePaths.getPosterSavePath(strFileName));
			if(bmp == null){
				bmp = mBmpDefault;
				requestDownloadImage(imagerUrl, strFileName);
			}
			if(bmp != null){
				addAvatarToCache(imagerUrl, bmp);
			}
		}
		return bmp;
	}

	protected void addAvatarToCache(String strImagerUrl,Bitmap bmp){
	}
	
	protected void removeCache(String strImagerUrl){
	}
	
	private void requestDownloadImage(String imageUrl, String fileName){
		if(!TextUtils.isEmpty(imageUrl)){
			if(mListUrlDownloading.contains(imageUrl) || 
					mListUrlWaitDownload.contains(imageUrl)){
				return;
			}
			if(!mIsDownloading){
				mIsDownloading = true;
				DownloadEvent de = new DownloadEvent(EventCode.SC_DownloadImage, null, null);
				de.setTag(imageUrl);
				AndroidEventManager.getInstance().postEvent(de, 0, imageUrl,
						FilePaths.getPosterSavePath(fileName));
				mListUrlDownloading.add(imageUrl);
			}else {
				mListUrlWaitDownload.add(imageUrl);
			}
		}
	}

	@Override
	public void onEventRunEnd(Event event) {
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				final String strImageUrl = (String)dEvent.getTag();
				removeCache(strImageUrl);
				mListUrlDownloading.remove(strImageUrl);
			}
			mIsDownloading = false;
			if(mListUrlWaitDownload.size() > 0){
				for(String strImageUrl : mListUrlWaitDownload){
					requestDownloadImage(strImageUrl, SchoolUtils.getFileNameFronUrl(strImageUrl));
					mListUrlWaitDownload.remove(strImageUrl);
					return;
				}
			}
		}
	}

	
}
