package com.zdht.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zdht.jingli.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PullToRefreshListView extends PulldownableListView implements OnClickListener{
	
	private static final SimpleDateFormat DATEFORMAT_YMDHM = new SimpleDateFormat("yyyy-M-d H:mm");
	
	private ProgressBar mProgressBarRefresh;
	private ImageView mImageViewArrow;
	private TextView mTextViewRefresh;
	private TextView mTextViewTime;
	
	private Date mDateRefreshLast;
	
	private RotateAnimation mRotateAnimationFlip;
	private RotateAnimation mRotateAnimationReverseFlip;
	private boolean mPullToRefresh;
	
	public View loadMoreView;
	
	private ActivityChatEditView mEditView;
	
	public PullToRefreshListView(Context context) {
		super(context);
		init();
		
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		mRotateAnimationFlip = new RotateAnimation(0, -180.0f, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnimationFlip.setDuration(250);
		mRotateAnimationFlip.setFillAfter(true);
		mRotateAnimationFlip.setFillEnabled(true);
		
		mRotateAnimationReverseFlip = new RotateAnimation(-180.0f, 0,
				Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		mRotateAnimationReverseFlip.setDuration(250);
		mRotateAnimationReverseFlip.setFillAfter(true);
		mRotateAnimationReverseFlip.setFillEnabled(true);
		
		setHorizontalScrollBarEnabled(false);
		setHeaderDividersEnabled(false);
		
		setCacheColorHint(0x00000000);
		setSelector(new ColorDrawable(0x00000000));
	}


	@Override
	protected View onCreatePullDownView() {
		Context context = getContext();
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View refreshView = layoutInflater.inflate(R.layout.refreshview, null);
		
		mProgressBarRefresh = (ProgressBar)refreshView.findViewById(R.id.pbRefresh);
		mImageViewArrow = (ImageView)refreshView.findViewById(R.id.ivPullArrow);
		mTextViewRefresh = (TextView)refreshView.findViewById(R.id.tvRefresh);
		mTextViewTime = (TextView)refreshView.findViewById(R.id.tvTime);
		
		mProgressBarRefresh.setVisibility(View.GONE);
		
		return refreshView;
	}
	
	
	

	@Override
	protected int getPullDownBeyondHeight() {
		return 60;
	}

	@Override
	protected void onStartRun() {
		mProgressBarRefresh.setVisibility(View.VISIBLE);
		mTextViewRefresh.setText("正在刷新...");
		mImageViewArrow.clearAnimation();
		mImageViewArrow.setVisibility(View.GONE);
	}
	
	@Override
	protected void onEndRun() {
		mProgressBarRefresh.setVisibility(View.GONE);
		mImageViewArrow.setVisibility(View.VISIBLE);
		mPullToRefresh = false;
		
		mDateRefreshLast = new Date();
		mTextViewTime.setText("最后更新:" + DATEFORMAT_YMDHM.format(mDateRefreshLast));
	}
	
	public void onPullDownHeightChanged(int nPaddingTop,int nPaddingTopOld){
		if(mIsRunning){
			return;
		}
		if(nPaddingTop < mPullDownViewPaddingTop){
			mTextViewRefresh.setText("下拉刷新");
			if(mPullToRefresh){
				mImageViewArrow.clearAnimation();
				mImageViewArrow.startAnimation(mRotateAnimationReverseFlip);
				mPullToRefresh = false;
			}
		}else{
			mTextViewRefresh.setText("松开即可刷新");
			if(!mPullToRefresh){
				mPullToRefresh = true;
				mImageViewArrow.clearAnimation();
				mImageViewArrow.startAnimation(mRotateAnimationFlip);
			}
		}
	}
    
    public static interface OnRefreshListener{
    	public void onRefresh(PullToRefreshListView listView);
    }

	@Override
	protected View onCreateFooterView() {
		Context context = getContext();
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		loadMoreView = layoutInflater.inflate(R.layout.load_more, null);
		loadMoreView.setOnClickListener(this);
		loadMoreView.setVisibility(View.GONE);
		return loadMoreView;
	}
	
	private OnLoadMoreClickListener mOnLoadMoreClickListener;
	
	public void setOnLoadMoreClickListener(OnLoadMoreClickListener listener){
		mOnLoadMoreClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if(mOnLoadMoreClickListener != null){
			mOnLoadMoreClickListener.onLoadMoreClicked(v.getId(), v);
		}
	}
	
	
	public static interface OnLoadMoreClickListener{
		public void onLoadMoreClicked(int nId, View view);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			if(mEditView != null){
				mEditView.hideAllPullUpView(true);
				mEditView.hideInputMethod();
			}
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	public void setEditView(ActivityChatEditView editView){
		mEditView = editView;
	}
}
