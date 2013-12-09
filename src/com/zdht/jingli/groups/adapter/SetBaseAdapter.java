package com.zdht.jingli.groups.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SetBaseAdapter<E extends Object> extends BaseAdapter {
	
	protected List<E> mListObject;
	public List<E> getmListObject() {
		return mListObject;
	}
	public SetBaseAdapter(){
		mListObject = new ArrayList<E>();
	}

	public int getCount() {
		return mListObject.size();
	}

	public Object getItem(int position) {
		return mListObject.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	public void replaceAll(Collection<E> collection){
		mListObject.clear();
		if(collection != null){
			mListObject.addAll(collection);
		}
		notifyDataSetChanged();
	}
	
	public void addItem(int position, E e) {
		mListObject.add(position, e);
		notifyDataSetChanged();
	}

	public void addItem(E e){
		mListObject.add(e);
		notifyDataSetChanged();
	}
	
	public void addAllItem(List<E> list){
		mListObject.addAll(list);
		notifyDataSetChanged();
	}
	
	public void removeItem(E e){
		mListObject.remove(e);
		notifyDataSetChanged();
	}
	
	public void removeAllItem(List<E> list){
		mListObject.removeAll(list);
		notifyDataSetChanged();
	}
	
	public void clear(){
		mListObject.clear();
		notifyDataSetChanged();
	}
	
	public static interface OnChildViewClickListener{
		public void onChildViewClickListener(View view);
	}

	protected OnChildViewClickListener mOnChildViewClickListener;
	
	public void setOnChildViewClickListener(OnChildViewClickListener listener){
		mOnChildViewClickListener = listener;
	}
}
