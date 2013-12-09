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


public class AddressGroupAdapter extends SetBaseAdapter<Group>{
	private Context mContext;
	private ViewHolder viewHolder;
	private boolean hasMore;
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	public boolean hasMore() {
		return hasMore;
	}

	public AddressGroupAdapter(Context context) {
		mContext = context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_address_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewAvatar = (ImageView)convertView.
					findViewById(R.id.ivAvatar);
			viewHolder.mTextViewName = (TextView) convertView
					.findViewById(R.id.tvName);
			viewHolder.mImageViewChat = (ImageView) convertView
					.findViewById(R.id.ivChat);
			viewHolder.mImageViewPhone = (ImageView)convertView.
					findViewById(R.id.ivPhone);
			viewHolder.mImageViewPhone.setVisibility(View.GONE);
			viewHolder.mImageViewChat.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final Group mGroup = (Group)getItem(position);
		viewHolder.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(mGroup.getAvatarUrl()));
		viewHolder.mTextViewName.setText(mGroup.getName());
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView mImageViewAvatar;
		TextView  mTextViewName;
		public ImageView mImageViewChat;
		ImageView mImageViewPhone;
	}
}
