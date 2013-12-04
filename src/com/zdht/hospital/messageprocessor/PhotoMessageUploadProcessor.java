package com.zdht.hospital.messageprocessor;

import java.util.HashMap;

import android.os.Handler;
import android.os.Looper;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.event.PostFileEvent;
import com.zdht.jingli.groups.event.PostPhotoEvent;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.utils.HttpUtils.ProgressRunnable;

public class PhotoMessageUploadProcessor implements OnEventListener{
	
	public static PhotoMessageUploadProcessor getInstance(){
		if(sInstance == null){
			sInstance = new PhotoMessageUploadProcessor();
		}
		return sInstance;
	}
	
	private static PhotoMessageUploadProcessor sInstance;
	
	private HashMap<String, UploadInfo> mMapIdToUploadInfo = new HashMap<String,UploadInfo>();
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	private PhotoMessageUploadProcessor(){
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_PostPhoto, this, false);
	}
	
	public void requestUpload(XMessage m){
		if(m.getType() == XMessage.TYPE_PHOTO){
			if(!mMapIdToUploadInfo.containsKey(m.getId())){
				UploadInfo ui = new UploadInfo(m);
				mMapIdToUploadInfo.put(m.getId(), ui);
				PostPhotoEvent event = new PostPhotoEvent(
						EventCode.HP_PostPhoto, 
						m.getPhotoFilePath(), ui.mRunnable, mHandler);
				event.setTag(m);
				AndroidEventManager.getInstance().postEvent(event, 0);
			}
		}
	}
	
	public boolean 	isUploading(XMessage m){
		return mMapIdToUploadInfo.containsKey(m.getId());
	}
	
	public int		getUploadPercentage(XMessage m){
		UploadInfo ui = mMapIdToUploadInfo.get(m.getId());
		if(ui != null){
			return ui.getUploadPercentage();
		}
		return -1;
	}

	@Override
	public void onEventRunEnd(Event event) {
		if(event.getEventCode() == EventCode.HP_PostPhoto){
			final XMessage m = (XMessage)event.getTag();
			PostFileEvent pEvent = (PostFileEvent)event;
			if(pEvent.isRequestSuccess()){
				m.setContent(pEvent.getmUrl());
				m.setUploadSuccess(true);
				m.updateDB();
				
				AndroidEventManager.getInstance().postEvent(EventCode.IM_SendMessage, 0,m);
			}
			mMapIdToUploadInfo.remove(m.getId());
		}
	}
	
	public static class UploadInfo{
		private XMessage			mMessage;
		private int 				mPercentage;
		private ProgressRunnable	mRunnable;
		
		public UploadInfo(XMessage m){
			mMessage = m;
			
			mRunnable = new ProgressRunnable() {
				@Override
				public void run() {
					setUploadPercentage(getPercentage());
					AndroidEventManager.getInstance().postEvent(
							EventCode.HP_PostPhotoPercentChanged, 
							0,
							mMessage);
				}
			};
		}
		
		private void setUploadPercentage(int nPer){
			mPercentage = nPer;
		}
		
		public int getUploadPercentage(){
			return mPercentage;
		}
	}
}
