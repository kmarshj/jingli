package com.zdht.jingli.groups.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.Live;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.jingli.groups.provider.LiveBmpProvider;
import com.zdht.jingli.groups.utils.ExpressionCoding;
import com.zdht.utils.SystemUtils;
/**
 * 直播列表adapter
 */
public class LiveAdapter extends ListChildViewClickableAdapter<Live>{
	
	private Context mContext;
	private ViewHolder viewHolder;

	public LiveAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_live_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewLiveImage = (ImageView)convertView.
					findViewById(R.id.ivLive_img);
			viewHolder.mTextViewUserName = (TextView) convertView
					.findViewById(R.id.tvName);
			viewHolder.mTextViewContent = (TextView) convertView
					.findViewById(R.id.tvContent);
			viewHolder.mTextViewTime = (TextView) convertView
					.findViewById(R.id.tvTime);
			viewHolder.mImageViewAvatar = (ImageView)convertView
					.findViewById(R.id.ivAvatar);
			viewHolder.mImageViewAvatar.setOnClickListener(this);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Live live = (Live)getItem(position);
		
		final String imgUrl = live.getImageUrl();
		if(TextUtils.isEmpty(imgUrl)){
			viewHolder.mImageViewLiveImage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, SystemUtils.dipToPixel(mContext, 25)));
			viewHolder.mImageViewLiveImage.setImageDrawable(null);
		}else {
			viewHolder.mImageViewLiveImage.setImageBitmap(null);
			Bitmap mBitmap = LiveBmpProvider.getInstance().loadImage(imgUrl);
			if(mBitmap != null) {
				int width = mBitmap.getWidth();
				int height =  mBitmap.getHeight();
				int imageWidth = parent.getWidth();
				
				double scale= imageWidth * 1.0f / width;
				double imageHeight = height * scale;
				viewHolder.mImageViewLiveImage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)imageHeight));
				viewHolder.mImageViewLiveImage.setImageBitmap(mBitmap);
			}
		}
		
//		viewHolder.mTextViewContent.setText(live.getContent());

		viewHolder.mTextViewContent.setText(
				ExpressionCoding.spanMessage(mContext,
			    live.getContent(), 
				0.6f,
				ImageSpan.ALIGN_BASELINE));
		
		viewHolder.mTextViewTime.setText(live.getTime());
		viewHolder.mTextViewUserName.setText(live.getUserName());
		viewHolder.mImageViewAvatar.setImageBitmap(SystemUtils.toRoundBitmap(AvatarBmpProvider.getInstance().loadImage(live.getUserAvatar())));
		viewHolder.mImageViewAvatar.setTag(live.getUserId());
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView mImageViewLiveImage;
		TextView  mTextViewUserName;
		TextView  mTextViewContent;
		TextView  mTextViewTime;
		ImageView mImageViewAvatar;
	}
	
	
}
