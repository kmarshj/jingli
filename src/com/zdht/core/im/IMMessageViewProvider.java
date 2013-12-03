package com.zdht.core.im;

import android.view.View;
import android.view.ViewGroup;

public abstract class IMMessageViewProvider {
	
	protected OnViewClickListener mOnViewClickListener;
	
	public abstract boolean acceptHandle(IMMessageProtocol message);
	
	public abstract View 	getView(IMMessageProtocol message,View convertView,ViewGroup parent);
	
	public void setOnViewClickListener(OnViewClickListener listener){
		mOnViewClickListener = listener;
	}

	public static interface OnViewClickListener{
		
		public void 	onViewClicked(IMMessageProtocol message,int nViewId);
		
		public boolean 	onViewLongClicked(IMMessageProtocol message,int nViewId);
	}
}
