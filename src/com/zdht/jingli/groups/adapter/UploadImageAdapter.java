package com.zdht.jingli.groups.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.UploadImage;
import com.zdht.utils.SystemUtils;

public class UploadImageAdapter extends SetBaseAdapter<UploadImage> {

	private Context mContext;
	private ViewHolder viewHolder;

	public UploadImageAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_upload_image_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewImage = (ImageView)convertView.
					findViewById(R.id.ivImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final UploadImage ActivityImage = (UploadImage)getItem(position);
		viewHolder.mImageViewImage.setImageBitmap(ActivityImage.getmBitmap());
		final GridView.LayoutParams lP = new GridView.LayoutParams((SystemUtils.getScreenWidth() - 40) / 3, (SystemUtils.getScreenWidth() - 40) / 3);
		convertView.setLayoutParams(lP);
		return convertView;
	}

	private static class ViewHolder {
		ImageView mImageViewImage;
	}
	
}