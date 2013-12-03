package com.zdht.mediarecord;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.zdht.parse.AmrCoding;
import com.zdht.utils.SystemUtils;

@SuppressLint("HandlerLeak")
public class AsyncMediaRecorder extends AbsMediaRecorder {
	
	private static final String FILENAME_OUTPUT = "recordcache";
	protected static final int TIME_RECORD_MIN = 500;
	protected static final int TIME_RECORD_MAX = 60000;
	
	private static final int nChannelFormat = AudioFormat.CHANNEL_IN_MONO;
	private static final int nAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
	private static final int nSampleSize = 8000;
	
	private static final int WHAT_RECORD = 1;
	private static final int WHAT_STOP = 2;
	
	private AtomicLong mAtomicMediaRecordTimeStart;
	
	private AtomicBoolean mAtomicRecording = new AtomicBoolean(false);
	private boolean mNotifyStoped;
	
	private boolean mStarted;
	private boolean mStoped;
	private boolean mCheckAudioFocus;
	
	private MediaRecordDoingHandler mMediaRecordDoingHandler;
	private boolean mDoing;
	
	private OnMediaRecordListener mOnMediaRecordListener;
	
	private AudioManager mAudioManager;
	
	private Runnable mRunnableExceedMaxTime = new Runnable() {
		public void run() {
			
			mStoped = true;
			mStarted = false;
			if(mOnMediaRecordListener != null){
				mOnMediaRecordListener.onExceedMaxTime();
			}
			
			doStopAndRelease(true);
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == WHAT_STOP){
				mCheckAudioFocus = false;
				if(mNotifyStoped){
					if(mOnMediaRecordListener != null){
						long lElapsedTime = System.currentTimeMillis() - mAtomicMediaRecordTimeStart.get();
						mOnMediaRecordListener.onStoped(lElapsedTime > TIME_RECORD_MIN);
					}
					mNotifyStoped = false;
				}
				mDoing = false;
			} else {
				boolean bSuccess = msg.arg1 == 1 ? true : false;
				if(mStoped){
					doStopAndRelease(true);
				}else{
					if(bSuccess){
						mStarted = true;
						mCheckAudioFocus = true;
						mHandler.postDelayed(mRunnableExceedMaxTime, TIME_RECORD_MAX + 200);
					}else{
						mDoing = false;
					}
					
					if(mOnMediaRecordListener != null){
						mOnMediaRecordListener.onStarted(bSuccess);
					}
				}
			}
		}
	};
	
	public AsyncMediaRecorder(Context context){
		mFilePathOutput = SystemUtils.getExternalCachePath(context) + 
				File.separator + FILENAME_OUTPUT;
		
		/*fix recordthread buffered overflow*/
		final File file = new File(mFilePathOutput);
		if(!file.exists()){
			final File fileParent = file.getParentFile();
			if(!fileParent.exists()){
				fileParent.mkdirs();
			}
		}
		
		mAtomicMediaRecordTimeStart = new AtomicLong();
		
		mDoing = false;
		mCheckAudioFocus = false;
		
		HandlerThread handlerThread = new HandlerThread("MediaRecordTask");
		handlerThread.start();
		mMediaRecordDoingHandler = new MediaRecordDoingHandler(handlerThread.getLooper());
		
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//		mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, 
//				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}
	
	public void setOnMediaRecordListener(OnMediaRecordListener listener){
		mOnMediaRecordListener = listener;
	}
	
	@Override
	public void startRecord(){
		mStarted = false;
		mStoped = false;
		if(!mDoing){
			mDoing = true;
			mAtomicRecording.set(true);
			mMediaRecordDoingHandler.sendEmptyMessage(WHAT_RECORD);
		}
	}
	
	private void doStopAndRelease(boolean bNotifyStoped){
		mCheckAudioFocus = false;
		mNotifyStoped = bNotifyStoped;
		mAtomicRecording.set(false);
		
		mHandler.removeCallbacks(mRunnableExceedMaxTime);
	}
	
	@Override
	public void stopRecord(){
		mStoped = true;
		if(mStarted){
			doStopAndRelease(true);
			mStarted = false;
		}else{
			mAtomicRecording.set(false);
		}
	}
	
	@Override
	public void release(){
		mOnMediaRecordListener = null;
		mMediaRecordDoingHandler.getLooper().quit();
		mMediaRecordDoingHandler = null;
		//mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
		
		mHandler.removeCallbacks(mRunnableExceedMaxTime);
		mHandler = null;
	}

	@Override
	public void pauseRecord() {
	}

	@Override
	public void resumeRecord() {
	}

	@Override
	public boolean pauseSupport() {
		return false;
	}
	
	private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = 
		new AudioManager.OnAudioFocusChangeListener() {
			public void onAudioFocusChange(int focusChange) {
				if(mCheckAudioFocus){
					if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || 
							focusChange == AudioManager.AUDIOFOCUS_LOSS){
						mStoped = true;
						mStarted = false;
						if(mOnMediaRecordListener != null){
							mOnMediaRecordListener.onInterrupted();
						}
						
						doStopAndRelease(true);
					}
				}
			}
		};
	
	@SuppressLint("HandlerLeak")
	private class MediaRecordDoingHandler extends Handler{
		
		private static final int ONE_FRAME_SHORT_SIZE = 160;
		
		public MediaRecordDoingHandler(Looper looper){
			super(looper);
			
		}
		
		private class EncodeThread extends Thread{
			
			private AtomicBoolean mAtomicAlive = new AtomicBoolean();
			
			private final ConcurrentShortQueue mShortQueue;
			private boolean mEncode;
			
			private short mSpeechBuffer[];
			
			public EncodeThread(int nCapacity){
				mShortQueue = new ConcurrentShortQueue(nCapacity);
				mEncode = true;
				mSpeechBuffer = new short[ONE_FRAME_SHORT_SIZE];
			}
			
			@Override
			public void run() {
				mAtomicAlive.set(true);
				try{
					while(mEncode){
						if(mShortQueue.length() >= ONE_FRAME_SHORT_SIZE){
							mShortQueue.get(mSpeechBuffer);
							AmrCoding.EncodeDo(mSpeechBuffer);
						}else{
							synchronized (mShortQueue) {
								mShortQueue.wait(100);
							}
						}
					}
				}catch(Exception e){
				}finally{
					synchronized (mAtomicAlive) {
						mAtomicAlive.set(false);
						mAtomicAlive.notify();
					}
				}
			}
			
			public void putByteBuffer(short buffer[],int nOffset,int nLength){
				mShortQueue.put(buffer, nOffset, nLength);
				synchronized (mShortQueue) {
					mShortQueue.notify();
				}
			}
			
			public void waitToStop(){
				mEncode = false;
				synchronized (mShortQueue) {
					mShortQueue.notify();
				}
				
				try{
					synchronized (mAtomicAlive) {
						if(mAtomicAlive.get()){
							mAtomicAlive.wait(500);
						}
					}
					
					while(mShortQueue.length() >= ONE_FRAME_SHORT_SIZE){
						mShortQueue.get(mSpeechBuffer);
						AmrCoding.EncodeDo(mSpeechBuffer);
					}
				}catch(Exception e){
				}
			}
		}

		@Override
		public void handleMessage(Message msg) {
			final int nWhat = msg.what;
			if (nWhat == WHAT_RECORD) {
				try {
					int nBufferSize = AudioRecord.getMinBufferSize(nSampleSize,
							nChannelFormat,
							nAudioFormat);
					
					if(nBufferSize < ONE_FRAME_SHORT_SIZE * 2){
						nBufferSize = ONE_FRAME_SHORT_SIZE * 2;
					}
					
					mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, 
							AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
					
					AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
							nSampleSize, nChannelFormat, nAudioFormat, nBufferSize * 3);			
					
					AmrCoding.EncodeInit(mFilePathOutput);

					audioRecord.startRecording();

					mAtomicMediaRecordTimeStart.set(System.currentTimeMillis());
					Message message = mHandler.obtainMessage();
					message.arg1 = 1;
					message.sendToTarget();
					
					short buffer[] = new short[nBufferSize];
					
					final EncodeThread thread = new EncodeThread(nBufferSize);
					thread.start();
					try{
						while (mAtomicRecording.get()) {
							final int nReadLength = audioRecord.read(buffer, 0, nBufferSize);
							if(nReadLength > 0){
								thread.putByteBuffer(buffer, 0, nReadLength);
							}
						}
					}finally{
						thread.waitToStop();
					}
					
					audioRecord.stop();
					audioRecord.release();
					audioRecord = null;

					AmrCoding.EncodeExit();
					
					mHandler.obtainMessage(WHAT_STOP).sendToTarget();
				} catch (Throwable e) {
					e.printStackTrace();
					Message message = mHandler.obtainMessage();
					message.arg1 = 0;
					message.sendToTarget();
				} finally{
					mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
				}
			}
		}
	}
	
	public static interface OnMediaRecordListener{
		public void onStarted(boolean bSuccess);
		
		public void onStoped(boolean bBeyondMinTime);
		
		public void onExceedMaxTime();
		
		public void onInterrupted();
	}
}
