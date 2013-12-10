package com.zdht.jingli.groups.adapter;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class BelongGroupAdapter extends SetBaseAdapter<Group> {

	private Context mContext;
	private ViewHolder viewHolder;

	public BelongGroupAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_belong_group_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewGroupAvatar = (ImageView)convertView.
					findViewById(R.id.ivAvatar);
			viewHolder.mTextViewGroupName = (TextView) convertView
					.findViewById(R.id.tvName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Group group = (Group)getItem(position);
		viewHolder.mImageViewGroupAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(group.getAvatarUrl()));
		viewHolder.mTextViewGroupName.setText(group.getName());
		return convertView;
	}

	private static class ViewHolder {
		ImageView mImageViewGroupAvatar;
		TextView  mTextViewGroupName;
	}
	
}
