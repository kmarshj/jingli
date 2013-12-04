package com.zdht.jingli.groups.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.MediaRecordManager;
import com.zdht.jingli.groups.event.RecordStopEvent;
import com.zdht.utils.SystemUtils;

public class RecordViewHelper implements View.OnTouchListener,
											OnEventListener{
	
	private ImageButton mImageButtonPressTalk;
	private PopupWindow mPopupWindowRecordPrompt;
	private ImageView mImageViewRecordPromt;
	private ProgressBar mProgressBarRecordPrepare;
	//private View mViewPopupWindowAchor;
	
	private boolean mRecordSuccess;
	private boolean mNeedStop;
	private boolean mCancel;
	
	private final int mLocation[] = new int[2];
	private final Rect mRect = new Rect();
	
	private MediaRecordManager 	mMediaRecordManager;
	private boolean				mIsAddEventListener;
	
	private OnRecordListener mOnRecordListener;
	
	private Handler mHandler;
	
	private Runnable mRunnableDelayDismissRecordPrompt = new Runnable() {
		public void run() {
			mPopupWindowRecordPrompt.dismiss();
		}
	};
	
	public void onCreate(Context context,ImageButton imageButton,Handler handler){
		View contentView = LayoutInflater.from(context).inflate(R.layout.recordprompt, null);
		mImageViewRecordPromt = (ImageView)contentView.findViewById(R.id.imageView);
		mProgressBarRecordPrepare = (ProgressBar)contentView.findViewById(R.id.progressBar);
		
		mImageButtonPressTalk = imageButton;
		mImageButtonPressTalk.setOnTouchListener(this);
		
		int nSize = SystemUtils.dipToPixel(context, 150);
		mPopupWindowRecordPrompt = new PopupWindow(contentView,nSize,nSize,false);
		
		mMediaRecordManager = MediaRecordManager.getInstance(context);
		mMediaRecordManager.open();
		
		mIsAddEventListener = false;
		
		mHandler = handler;
	}
	
	public void onDestroy(){
		//mViewPopupWindowAchor = null;
		mMediaRecordManager.close();
		mHandler.removeCallbacks(mRunnableDelayDismissRecordPrompt);
		mOnRecordListener = null;
	}
	
	public void onPause(){
		processStopRecord();
		if(mIsAddEventListener){
			AndroidEventManager.getInstance().removeEventListener(EventCode.E_RecordExceedMaxTime, this);
			AndroidEventManager.getInstance().removeEventListener(EventCode.E_RecordFail, this);
			AndroidEventManager.getInstance().removeEventListener(EventCode.E_RecordInterrupt, this);
			AndroidEventManager.getInstance().removeEventListener(EventCode.E_RecordStart, this);
			AndroidEventManager.getInstance().removeEventListener(EventCode.E_RecordStop, this);
			mIsAddEventListener = false;
		}
	}
	
	public void onResume(){
		if(!mIsAddEventListener){
			AndroidEventManager.getInstance().addEventListener(EventCode.E_RecordExceedMaxTime, this, false);
			AndroidEventManager.getInstance().addEventListener(EventCode.E_RecordFail, this, false);
			AndroidEventManager.getInstance().addEventListener(EventCode.E_RecordInterrupt, this, false);
			AndroidEventManager.getInstance().addEventListener(EventCode.E_RecordStart, this, false);
			AndroidEventManager.getInstance().addEventListener(EventCode.E_RecordStop, this, false);
		
			mIsAddEventListener = true;
		}
	}
	
	public void setOnRecordListener(OnRecordListener listener){
		mOnRecordListener = listener;
	}
	
//	public void setRecordPromptAnchor(View anchor){
//		mViewPopupWindowAchor = anchor;
//	}
	
	@Override
	public void onEventRunEnd(Event event) {
		final int nEventCode = event.getEventCode();
		if(nEventCode == EventCode.E_RecordExceedMaxTime){
			mImageViewRecordPromt.setImageResource(R.drawable.image_talklong);
			mHandler.postDelayed(mRunnableDelayDismissRecordPrompt, 500);
			mNeedStop = false;
		}else if(nEventCode == EventCode.E_RecordFail){
			mRecordSuccess = false;
			
			if(mOnRecordListener != null){
				mOnRecordListener.onRecordFailed(false);
			}
		}else if(nEventCode == EventCode.E_RecordInterrupt){
			processStopRecord();
		}else if(nEventCode == EventCode.E_RecordStart){
			setRecordPromptDisplayChild(1);
			if(!mPopupWindowRecordPrompt.isShowing()){
				showPopupWindow();
			}
			mImageViewRecordPromt.setImageResource(R.drawable.image_talkstart);
			
			if(mOnRecordListener != null){
				mOnRecordListener.onRecordStarted();
			}
		}else if(nEventCode == EventCode.E_RecordStop){
			RecordStopEvent stopEvent =(RecordStopEvent)event;
			final boolean bBeyondMinTime = stopEvent.isBeyondMinTime();
			mNeedStop = false;
			if (mRecordSuccess) {
				if (bBeyondMinTime) {
					mPopupWindowRecordPrompt.dismiss();

					if (mCancel) {
						mCancel = false;
					} else {
						if (mOnRecordListener != null) {
							mOnRecordListener.onRecordEnded(stopEvent.getRecordFilePathName());
						}
					}
				}else{
					mImageViewRecordPromt.setImageResource(R.drawable.image_talkshort);
					mHandler.postDelayed(mRunnableDelayDismissRecordPrompt, 500);
				}
			}
		}
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		/*int nAction = event.getAction();
		if (nAction == MotionEvent.ACTION_MOVE){
			if (mRecordSuccess) {
				final int location[] = mLocation;
				v.getLocationOnScreen(location);
				final float fx = location[0] + event.getX();
				final float fy = location[1] + event.getY();

				mImageViewRecordPromt.getLocationOnScreen(location);
				final Rect rect = mRect;
				mImageViewRecordPromt.getGlobalVisibleRect(rect);
				rect.offsetTo(location[0], location[1]);
				if (rect.contains((int) fx, (int) fy)) {
					mImageViewRecordPromt.setImageResource(R.drawable.image_talkcancel);
					mCancel = true;
				} else {
					mImageViewRecordPromt.setImageResource(R.drawable.image_talkstart);
					mCancel = false;
				}
				return true;
			}
		}else if (nAction == MotionEvent.ACTION_DOWN) {
			if (!mPopupWindowRecordPrompt.isShowing()) {
				//if (HPApplication.isIMConnectionSuccess()) {
					if (SchoolUtils.checkExternalStorageAvailable()) {
						setRecordPromptDisplayChild(0);
						showPopupWindow();

						mMediaRecordManager.startRecord();
						mNeedStop = true;
						mRecordSuccess = true;
					}
				} else {
					mRecordSuccess = false;

					if (mOnRecordListener != null) {
						mOnRecordListener.onRecordFailed(true);
					}
				}
				return true;
			}
		} else if(nAction == MotionEvent.ACTION_UP || nAction == MotionEvent.ACTION_CANCEL){
			processStopRecord();
		}*/
		return false;
	}
	
	private void setRecordPromptDisplayChild(int nWhich){
		if(nWhich == 0){
			mProgressBarRecordPrepare.setVisibility(View.VISIBLE);
			mImageViewRecordPromt.setVisibility(View.GONE);
		}else{
			mProgressBarRecordPrepare.setVisibility(View.GONE);
			mImageViewRecordPromt.setVisibility(View.VISIBLE);
		}
	}
	
	private void showPopupWindow(){
		//View anchor = mViewPopupWindowAchor == null ? (ViewGroup)(mImageButtonPressTalk.getParent()) : mViewPopupWindowAchor;
		mImageViewRecordPromt.setImageBitmap(null);
		//mPopupWindowRecordPrompt.showAtLocation(anchor, Gravity.CENTER, 0, 0);
		mPopupWindowRecordPrompt.showAtLocation(mImageButtonPressTalk, Gravity.CENTER, 0, 0);
	}
	
	private void processStopRecord(){
		if (mNeedStop) {
			setRecordPromptDisplayChild(1);
			if (mRecordSuccess) {
				mMediaRecordManager.stopRecord();
			} else {
				mPopupWindowRecordPrompt.dismiss();
			}
			mNeedStop = false;
		}
	}
	
	public static interface OnRecordListener{
		public void onRecordStarted();
		
		public void onRecordEnded(String strRecordPath);
		
		public void onRecordFailed(boolean bFailByNet);
	}
}
