package com.zdht.jingli.groups.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.zdht.jingli.R;
import com.zdht.utils.AsyncImageLoader;
import com.zdht.utils.AsyncImageLoader.ImageCallback1;

public class ActivityImagesPageAdapter extends CommonPagerAdapter implements 
															ImageCallback1{
	
	private Context mContext;
	private List<String> mListActivityImageUrl = new ArrayList<String>();
	
	private HashMap<ViewHolder, String> mMapViewHolderToUrl = new HashMap<ViewHolder, String>();
	
	private AsyncImageLoader asyncImageLoader;
	
	public ActivityImagesPageAdapter(Context context){
		mContext = context;
		asyncImageLoader = AsyncImageLoader.getInstance();
	}
	
	public void replaceAll(List<String> list){
		mListActivityImageUrl.clear();
		mListActivityImageUrl.addAll(list);
		setPageCount(mListActivityImageUrl.size());
		mMapViewHolderToUrl.clear();
		notifyDataSetChanged();
	}
	
	public boolean isImageDownloadFail(int nPos){
		if(nPos >= mListActivityImageUrl.size()){
			return false;
		}
		final String imageUrl = mListActivityImageUrl.get(nPos);
		for(ViewHolder viewHolder : mMapViewHolderToUrl.keySet()){
			if(mMapViewHolderToUrl.get(viewHolder).equals(imageUrl)){
				return viewHolder.mViewPromptFail.getVisibility() == View.VISIBLE;
			}
		}
		return false;
	}
	
	public void	requestDownloadImage(int nPos){
		if(nPos >= mListActivityImageUrl.size()){
			return;
		}
		final String imageUrl = mListActivityImageUrl.get(nPos);
		asyncImageLoader.loadDrawable(mContext, imageUrl, this);
		for(ViewHolder viewHolder : mMapViewHolderToUrl.keySet()){
			if(mMapViewHolderToUrl.get(viewHolder).equals(imageUrl)){
				viewHolder.mViewLoad.setVisibility(View.VISIBLE);
				viewHolder.mViewPromptFail.setVisibility(View.GONE);
				break;
			}
		}
	}
	
	@Override
	protected View getView(View v, int nPos) {
		ViewHolder viewHolder = null;
		if(v == null){
			v = LayoutInflater.from(mContext).inflate(R.layout.adapter_advertising, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView)v.findViewById(R.id.iv);
			viewHolder.mViewLoad = v.findViewById(R.id.viewLoad);
			viewHolder.mViewPromptFail = v.findViewById(R.id.tvPrompt);
			viewHolder.mImageView.setClickable(false);
			v.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)v.getTag();
		}
		
		String imageUrl = mListActivityImageUrl.get(nPos);
		
		mMapViewHolderToUrl.put(viewHolder, imageUrl);
		
		Drawable drawable = asyncImageLoader.loadDrawable(mContext, imageUrl, this);
		if(drawable == null){
			viewHolder.mViewLoad.setVisibility(View.VISIBLE);
		}else{
			viewHolder.mViewLoad.setVisibility(View.GONE);
		}
		viewHolder.mViewPromptFail.setVisibility(View.GONE);
		
		viewHolder.mImageView.setImageDrawable(drawable);
		
		return v;
	}
	
	@Override
	public void imageLoaded(Drawable imageDrawable, String imageUrl) {
		for(ViewHolder viewHolder : mMapViewHolderToUrl.keySet()){
			if(mMapViewHolderToUrl.get(viewHolder).equals(imageUrl)){
				viewHolder.mViewLoad.setVisibility(View.GONE);
				if(imageDrawable == null){
					viewHolder.mViewPromptFail.setVisibility(View.VISIBLE);
				}else{
					viewHolder.mViewPromptFail.setVisibility(View.GONE);
					viewHolder.mImageView.setImageDrawable(imageDrawable);
				}
				break;
			}
		}
	}

	private static class ViewHolder{
		public ImageView 	mImageView;
		public View			mViewLoad;
		public View			mViewPromptFail;
	}
}
