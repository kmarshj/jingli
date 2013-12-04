package com.zdht.hospital.messageprocessor;

import java.util.HashMap;

import android.os.Handler;
import android.os.Looper;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.event.DownloadPhotoEvent;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.utils.HttpUtils.ProgressRunnable;

public class PhotoMessageDownloadProcessor implements OnEventListener{

	public static PhotoMessageDownloadProcessor getInstance(){
		if(sInstance == null){
			sInstance = new PhotoMessageDownloadProcessor();
		}
		return sInstance;
	}
	
	private static PhotoMessageDownloadProcessor sInstance;
	
	private HashMap<String, DownloadInfo> mMapIdToDownloadInfo = new HashMap<String,DownloadInfo>();
	private HashMap<String, DownloadInfo> mMapIdToThumbDownloadInfo = new HashMap<String,DownloadInfo>();
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	private PhotoMessageDownloadProcessor(){
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_DownloadPhoto, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_DownloadThumbPhoto, this, false);
	}
	
	public void requestDownload(XMessage m,boolean bDownloadThumb){
		if(m.getType() == XMessage.TYPE_PHOTO){
			if(bDownloadThumb){
				if(!mMapIdToThumbDownloadInfo.containsKey(m.getId())){
					DownloadInfo di = new DownloadInfo(m,true);
					mMapIdToThumbDownloadInfo.put(m.getId(), di);
					DownloadPhotoEvent dEvent = new DownloadPhotoEvent(
							EventCode.HP_DownloadThumbPhoto, 
							m, true, di.mRunnable, mHandler);
					AndroidEventManager.getInstance().postEvent(dEvent, 0);
				}
			}else{
				if(!mMapIdToDownloadInfo.containsKey(m.getId())){
					DownloadInfo di = new DownloadInfo(m,false);
					mMapIdToDownloadInfo.put(m.getId(), di);
					DownloadPhotoEvent dEvent = new DownloadPhotoEvent(
						EventCode.HP_DownloadPhoto, 
						m, false, di.mRunnable, mHandler);
					AndroidEventManager.getInstance().postEvent(dEvent, 0);
				}
			}
		}
	}
	
	public boolean isThumbPhotoDownloading(XMessage m){
		return mMapIdToThumbDownloadInfo.containsKey(m.getId());
	}
	
	public boolean isPhotoDownloading(XMessage m){
		return mMapIdToDownloadInfo.containsKey(m.getId());
	}
	
	public int	getThumbPhotoDownloadPercentage(XMessage m){
		DownloadInfo di = mMapIdToThumbDownloadInfo.get(m.getId());
		if(di != null){
			return di.getDownloadPercentage();
		}
		return -1;
	}
	
	public int getPhotoDownloadPercentage(XMessage m){
		DownloadInfo di = mMapIdToDownloadInfo.get(m.getId());
		if(di != null){
			return di.getDownloadPercentage();
		}
		return -1;
	}

	@Override
	public void onEventRunEnd(Event event) {
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HP_DownloadPhoto){
			final XMessage m = (XMessage)event.getReturnParam();
			mMapIdToDownloadInfo.remove(m.getId());
		}else if(nCode == EventCode.HP_DownloadThumbPhoto){
			final XMessage m = (XMessage)event.getReturnParam();
			mMapIdToThumbDownloadInfo.remove(m.getId());
		}
	}
	
	public static class DownloadInfo{
		
		private XMessage 		mMessage;
		private boolean			mIsDownloadThumb;
		private int				mPercentage;
		private ProgressRunnable mRunnable;
		
		private DownloadInfo(XMessage m,boolean bDownloadThumb){
			mMessage = m;
			mIsDownloadThumb = bDownloadThumb;
			
			mRunnable = new ProgressRunnable() {
				@Override
				public void run() {
					setDownloadPercentage(getPercentage());
					AndroidEventManager.getInstance().postEvent(
							mIsDownloadThumb ? 
									EventCode.HP_DownloadThumbPhotoPercentChanged : 
										EventCode.HP_DownloadPhotoPercentChanged,
							0,
							mMessage);
				}
			};
		}
		
		private void setDownloadPercentage(int nPer){
			mPercentage = nPer;
		}
		
		public int getDownloadPercentage(){
			return mPercentage;
		}
	}
}
