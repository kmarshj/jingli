package com.zdht.jingli.groups.adapter;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.jingli.groups.provider.PosterBmpProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 活动列表适配器
 * @author think
 *
 */
public class ActivityAdapter extends SetBaseAdapter<Activity> {

	private Context mContext;
	private ViewHolder viewHolder;
	private boolean hasMore;
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	public boolean hasMore() {
		return hasMore;
	}

	public ActivityAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		viewHolder = null;
		if(convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (viewHolder == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_activity_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewActivityPoster = (ImageView)convertView.
					findViewById(R.id.ivActivity_poster);
			/*viewHolder.mProgressBarLoading = (ProgressBar)convertView.
					findViewById(R.id.pbLoading);*/ 
			viewHolder.mTextViewActivityName = (TextView) convertView
					.findViewById(R.id.tvActivity_name);
			viewHolder.mTextViewActivityHost = (TextView) convertView
					.findViewById(R.id.tvActivity_host);
			viewHolder.mTextViewActivityTime = (TextView) convertView
					.findViewById(R.id.activity_item_time);
			viewHolder.mTextViewActivityPeople = (TextView) convertView
					.findViewById(R.id.activity_item_people);
			convertView.setTag(viewHolder);
		}
		
		final Activity activity = (Activity)getItem(position);
		
		if(!TextUtils.isEmpty(activity.getPosterUrl())){
			Bitmap bitmap = PosterBmpProvider.getInstance().loadImage(activity.getPosterUrl());
			if(bitmap == null) {
				viewHolder.mImageViewActivityPoster.setImageDrawable(new BitmapDrawable());
			} else {
				viewHolder.mImageViewActivityPoster.setImageBitmap(bitmap);
			}
		}
		// 设置活动名称
		viewHolder.mTextViewActivityName.setText(activity.getName());
		// 设置活动主办方
		viewHolder.mTextViewActivityHost.setText(mContext.getString(R.string.host_first) + mContext.getString(R.string.host_second) + ":  "
				+ activity.getGroupName());
		// 时间
		String date = activity.getFromTime();
		if(date.length() > 10) {
			date = date.substring(0, 10);
		}
		viewHolder.mTextViewActivityTime.setText(date + "起");
		// 已参加人数
		viewHolder.mTextViewActivityPeople.setText(activity.getNowNumber());
		return convertView;
	}

	
	private static class ViewHolder {
		ImageView mImageViewActivityPoster;
		//ProgressBar mProgressBarLoading;
		TextView  mTextViewActivityName;
		TextView  mTextViewActivityHost;
		TextView  mTextViewActivityTime;
		TextView  mTextViewActivityPeople;
	}
	
}
