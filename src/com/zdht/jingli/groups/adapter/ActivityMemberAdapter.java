package com.zdht.jingli.groups.adapter;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ActivityMemberAdapter extends SetBaseAdapter<User> {

	private Context mContext;
	private ViewHolder viewHolder;

	public ActivityMemberAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_activity_member_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewUserAvatar = (ImageView)convertView.
					findViewById(R.id.ivAvatar);
			viewHolder.mTextViewUserName = (TextView) convertView
					.findViewById(R.id.tvName);
			viewHolder.mImageViewSex = (ImageView)convertView.
					findViewById(R.id.ivSex);
			viewHolder.mTextViewIsGroupMember = (TextView) convertView
					.findViewById(R.id.tvIs_group_member);
			viewHolder.mTextViewTime = (TextView) convertView
					.findViewById(R.id.tvAdd_time);
			viewHolder.admin = (ImageView)convertView.findViewById(R.id.activity_member_admin);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final User user = (User)getItem(position);
		
		viewHolder.mImageViewUserAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(user.getAvatarUrl()));
		viewHolder.mTextViewUserName.setText(user.getName());
		if(user.getSex().getIntValue() == 1){
			viewHolder.mImageViewSex.setImageResource(R.drawable.female);
		}else {
			viewHolder.mImageViewSex.setImageResource(R.drawable.male);
		}
		if(user.isGroupMember()){
			viewHolder.mTextViewIsGroupMember.setText(R.string.is_group_host_member);
		}else {
			viewHolder.mTextViewIsGroupMember.setText(R.string.is_not_group_host_member);
		}
		String date = user.getAddActivityTime();
		if(date.length() > 10) {
			date = date.substring(0, 10);
		}
		viewHolder.mTextViewTime.setText(date);
		String role = user.getRole();
		if(role.equals("0") || role.equals("1")) {
			viewHolder.admin.setVisibility(View.VISIBLE);
		} else {
			viewHolder.admin.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView mImageViewUserAvatar;
		ImageView mImageViewSex;
		TextView  mTextViewUserName;
		TextView  mTextViewIsGroupMember;
		TextView  mTextViewTime;
		ImageView admin;
	}
	
}
