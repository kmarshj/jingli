package com.zdht.jingli.groups.adapter;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class AddressUserAdapter extends SetBaseAdapter<User> implements OnClickListener{
	private Context mContext;
	private ViewHolder viewHolder;
	private boolean hasMore;
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	public boolean hasMore() {
		return hasMore;
	}

	public AddressUserAdapter(Context context) {
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
			viewHolder.mImageViewPhone.setOnClickListener(this);
			viewHolder.mImageViewChat.setOnClickListener(this);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final User user = (User)getItem(position);
		viewHolder.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(user.getAvatarUrl()));
		viewHolder.mTextViewName.setText(user.getName());
		viewHolder.mImageViewChat.setTag(user);
		viewHolder.mImageViewChat.setVisibility(View.VISIBLE);
		if(TextUtils.isEmpty(user.getPhone())){
			viewHolder.mImageViewPhone.setVisibility(View.GONE);
			viewHolder.mImageViewPhone.setTag(null);
		}else {
			viewHolder.mImageViewPhone.setVisibility(View.VISIBLE);
			viewHolder.mImageViewPhone.setTag(user.getPhone());
		}
		return convertView;
	}
	
	private static class ViewHolder {
		/** 头像 */
		ImageView mImageViewAvatar;
		/** 名称 */
		TextView  mTextViewName;
		/** 聊天图标 */
		public ImageView mImageViewChat;
		/** 电话图标 */
		ImageView mImageViewPhone;
	}

	@Override
	public void onClick(View v) {
		if(mOnChildViewClickListener != null) {
			mOnChildViewClickListener.onChildViewClickListener(v);
		}
	}

}
