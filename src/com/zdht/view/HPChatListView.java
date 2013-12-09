package com.zdht.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.zdht.jingli.R;
import com.zdht.utils.SystemUtils;

public class HPChatListView extends PulldownableListView {

	private HPChatEditView mEditView;
	
	public HPChatListView(Context context) {
		super(context);
		init();
	}

	public HPChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		setCacheColorHint(0x00000000);
		setSelector(new ColorDrawable(0x00000000));
		setDivider(null);
		
		addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.message_divider, null));
	}
	
	public void setEditView(HPChatEditView editView){
		mEditView = editView;
	}

	@Override
	protected View onCreatePullDownView() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.refreshview_loadmoremessage, null);
		return v;
	}

	@Override
	protected int getPullDownBeyondHeight() {
		return SystemUtils.dipToPixel(getContext(), 30);
	}

	@Override
	public void setCanRun(boolean bCan) {
		super.setCanRun(bCan);
		if(bCan){
			mPullDownView.setVisibility(View.VISIBLE);
		}else{
			mPullDownView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStartRun() {
	}

	@Override
	protected void onEndRun() {
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

	@Override
	protected View onCreateFooterView() {
		return null;
	}
}
