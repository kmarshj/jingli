package com.zdht.jingli.groups.adapter;


import android.view.View;
import android.view.ViewGroup;

public abstract class ListChildViewClickableAdapter<E extends Object> extends SetBaseAdapter<E> implements
																			View.OnClickListener
																			{

	private OnListChildViewClickListener mOnListChildViewClickListener;
	
	
	public void setOnChildViewClickListener(OnListChildViewClickListener listener){
		mOnListChildViewClickListener = listener;
	}

	
	@Override
	public void onClick(View v) {
		if(mOnListChildViewClickListener != null) {
			mOnListChildViewClickListener.onListChildViewClicked(v.getId(), v.getTag());
		}
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public static interface OnListChildViewClickListener{
		public void onListChildViewClicked(int nId, Object object);
	}
	
	
	
	
}
