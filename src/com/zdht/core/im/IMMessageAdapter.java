package com.zdht.core.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IMMessageAdapter extends BaseAdapter {

	protected Context mContext;
	
	protected List<IMMessageProtocol> mListMessage = new ArrayList<IMMessageProtocol>();
	
	protected boolean mCanAddProvider;
	protected List<IMMessageViewProvider> 	mListViewProvider = new ArrayList<IMMessageViewProvider>();
	protected IMMessageViewProvider			mDefaultProvider;
	
	protected HashMap<View, IMMessageProtocol> mMapConvertViewToMessage = new HashMap<View, IMMessageProtocol>();
	
	public IMMessageAdapter(Context context){
		mContext = context;
		mCanAddProvider = true;
	}
	
	@Override
	public int getCount() {
		return mListMessage.size();
	}
	
	@Override
	public int getItemViewType(int position) {
		IMMessageProtocol m = mListMessage.get(position);
		int nIndex = 0;
		for(IMMessageViewProvider provider : mListViewProvider){
			if(provider.acceptHandle(m)){
				return nIndex;
			}
			++nIndex;
		}
		if(mDefaultProvider != null){
			return nIndex;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		int nCount = mListViewProvider.size();
		if(mDefaultProvider != null){
			++nCount;
		}
		return nCount > 0 ? nCount : 1;
	}

	@Override
	public Object getItem(int position) {
		return mListMessage.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IMMessageProtocol m = mListMessage.get(position);
		
		for(IMMessageViewProvider provider : mListViewProvider){
			if(provider.acceptHandle(m)){
				convertView =  provider.getView(m, convertView, parent);
				break;
			}
		}
		if(convertView == null && mDefaultProvider != null){
			convertView = mDefaultProvider.getView(m, convertView, parent);
		}
		
		if(convertView != null){
			mMapConvertViewToMessage.put(convertView, m);
		}
		
		return convertView;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
		mCanAddProvider = false;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		super.unregisterDataSetObserver(observer);
		mCanAddProvider = true;
	}
	
	public boolean isIMMessageViewVisible(IMMessageProtocol m){
		if(m == null){
			return true;
		}
		for(IMMessageProtocol message : mMapConvertViewToMessage.values()){
			if(message.equals(m)){
				return true;
			}
		}
		return false;
	}
	
	public void setDefaultIMMessageViewProvider(IMMessageViewProvider provider){
		mDefaultProvider = provider;
	}

	public void addIMMessageViewProvider(IMMessageViewProvider provider){
		if(checkFactorySetCanChange()){
			mListViewProvider.add(provider);
		}
	}
	
	public void removeIMMessageViewProvider(IMMessageViewProvider provider){
		if(checkFactorySetCanChange()){
			mListViewProvider.remove(provider);
		}
	}
	
	public void clearIMMessageViewProvider(){
		if(checkFactorySetCanChange()){
			mListViewProvider.clear();
		}
	}
	
	protected boolean checkFactorySetCanChange(){
		if(mCanAddProvider){
			return true;
		}else{
			Assert.assertTrue("addIMMessageViewProvider must be call before registerDataSetObserver", false);
		}
		return false;
	}
	
	public void addItem(IMMessageProtocol m){
		mListMessage.add(m);
		notifyDataSetChanged();
	}
	
	public void addItem(int nPos,IMMessageProtocol m){
		mListMessage.add(nPos, m);
		notifyDataSetChanged();
	}
	
	public void addAllItem(int nPos,List<IMMessageProtocol> list){
		mListMessage.addAll(nPos, list);
		notifyDataSetChanged();
	}
	
	public void addAllItem(List<IMMessageProtocol> list){
		mListMessage.addAll(list);
		notifyDataSetChanged();
	}
	
	public void removeItem(IMMessageProtocol m){
		mListMessage.remove(m);
		notifyDataSetChanged();
	}
	
	public void removeAllItem(List<IMMessageProtocol> list){
		mListMessage.removeAll(list);
		notifyDataSetChanged();
	}
	
	public void clear(){
		mListMessage.clear();
		notifyDataSetChanged();
	}
}
