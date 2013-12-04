package com.zdht.jingli.groups;

import android.content.Context;

import com.zdht.jingli.groups.event.CallbackEvent;
import com.zdht.jingli.groups.event.RecordStopEvent;
import com.zdht.mediarecord.AsyncMediaRecorder;

public class MediaRecordManager implements AsyncMediaRecorder.OnMediaRecordListener{
	
	public static MediaRecordManager getInstance(Context context){
		if(sInstance == null){
			sInstance = new MediaRecordManager(context.getApplicationContext());
		}
		return sInstance;
	}
	
	private static MediaRecordManager sInstance;
	
	private Context mContext;
	
	private int 	mOpenTimes;
	
	private AsyncMediaRecorder mMediaRecorder;
	
	private MediaRecordManager(Context context){
		mContext = context;
		
		mOpenTimes = 0;
	}
	
	
	public void open(){
		++mOpenTimes;
		if(mOpenTimes == 1){
			mMediaRecorder = new AsyncMediaRecorder(mContext);
			mMediaRecorder.setOnMediaRecordListener(this);
		}
	}
	
	public void close(){
		--mOpenTimes;
		if(mOpenTimes == 0){
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}
	
	public void startRecord(){
		if(mOpenTimes > 0){
			mMediaRecorder.startRecord();
		}
	}
	
	public void stopRecord(){
		if(mOpenTimes > 0){
			mMediaRecorder.stopRecord();
		}
	}

	@Override
	public void onStarted(boolean bSuccess) {
		if(bSuccess){
			AndroidEventManager.getInstance().runEvent(new CallbackEvent(EventCode.E_RecordStart));
		}else{
			AndroidEventManager.getInstance().runEvent(new CallbackEvent(EventCode.E_RecordFail));
		}
	}

	@Override
	public void onStoped(boolean bBeyondMinTime) {
		AndroidEventManager.getInstance().runEvent(new RecordStopEvent(EventCode.E_RecordStop),
				Boolean.valueOf(bBeyondMinTime),
				mMediaRecorder.getFilePathOutput());
	}

	@Override
	public void onExceedMaxTime() {
		AndroidEventManager.getInstance().runEvent(new CallbackEvent(EventCode.E_RecordExceedMaxTime));
	}

	@Override
	public void onInterrupted() {
		AndroidEventManager.getInstance().runEvent(new CallbackEvent(EventCode.E_RecordInterrupt));
	}
}
