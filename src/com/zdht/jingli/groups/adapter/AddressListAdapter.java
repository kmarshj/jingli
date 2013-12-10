package com.zdht.jingli.groups.adapter;

import java.util.List;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.AddressList;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 通讯录lieview适配器 
 *
 */
public class AddressListAdapter extends BaseExpandableListAdapter implements OnClickListener{

	
	private Context mContext;
	private ViewHolderParent viewHolderParent;
	private ViewHolderChild  viewHolderChild;
	
	
	private AddressList mAddressList;
	public AddressList getAddressList() {
		return mAddressList;
	}
	public AddressListAdapter(Context context, AddressList addressList){
		mContext = context;
		mAddressList = addressList;
	}

	public void setmAddressList(AddressList mAddressList) {
		this.mAddressList = mAddressList;
		notifyDataSetChanged();
	}
	@Override
	public int getGroupCount() {
		return 3;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildrenCount(int groupPosition) {
		if(groupPosition == 0 || groupPosition == 1){
			return ((List<User>)getGroup(groupPosition)).size();
		}else{
			return ((List<Group>)getGroup(groupPosition)).size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		if(groupPosition == 0){
			return mAddressList.getmListCLassFriend();
		}else if(groupPosition == 1){
			return mAddressList.getListFriend();
		}else{
			return mAddressList.getListGroup();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if(groupPosition == 0 || groupPosition == 1){
			return ((List<User>)getGroup(groupPosition)).get(childPosition);
		}else{
			return ((List<Group>)getGroup(groupPosition)).get(childPosition);
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		if(groupPosition == 0 || groupPosition == 1){
			return ((List<User>)getGroup(groupPosition)).get(childPosition).getUid();
		}else{
			return ((List<Group>)getGroup(groupPosition)).get(childPosition).getId();
		}
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		viewHolderParent = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_address_parent_item, null);
			viewHolderParent = new ViewHolderParent();
			viewHolderParent.mImageViewArrow = (ImageView)convertView.
					findViewById(R.id.ivArrow);
			viewHolderParent.mTextViewAddressListName = (TextView) convertView
					.findViewById(R.id.tvAddress_name);
			convertView.setTag(viewHolderParent);
		} else {
			viewHolderParent = (ViewHolderParent)convertView.getTag();
		}
		
		if(isExpanded){
			viewHolderParent.mImageViewArrow.setBackgroundResource(R.drawable.arrow_up);
		}else {
			viewHolderParent.mImageViewArrow.setBackgroundResource(R.drawable.arrow_down);
		}
		if(groupPosition == 0){
			viewHolderParent.mTextViewAddressListName.setText(R.string.class_address);
		}else if(groupPosition == 1){
			viewHolderParent.mTextViewAddressListName.setText(R.string.friend_address);
		}else {
			viewHolderParent.mTextViewAddressListName.setText(R.string.group_address);
		}
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		viewHolderChild = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_address_child_item, null);
			viewHolderChild = new ViewHolderChild();
			viewHolderChild.mImageViewAvatar = (ImageView)convertView.
					findViewById(R.id.ivAvatar);
			viewHolderChild.mTextViewName = (TextView) convertView
					.findViewById(R.id.tvName);
			viewHolderChild.mImageViewChat = (ImageView) convertView
					.findViewById(R.id.ivChat);
			viewHolderChild.mImageViewPhone = (ImageView)convertView.
					findViewById(R.id.ivPhone);
			viewHolderChild.mImageViewPhone.setOnClickListener(this);
			viewHolderChild.mImageViewChat.setOnClickListener(this);
			convertView.setTag(viewHolderChild);
		} else {
			viewHolderChild = (ViewHolderChild)convertView.getTag();
		}
		viewHolderChild.groupPosition = groupPosition;
		if(groupPosition == 0 || groupPosition == 1){
			//final List<User> list = (List<User>)getGroup(groupPosition);
			final User friend = (User)getChild(groupPosition, childPosition);
			viewHolderChild.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(friend.getAvatarUrl()));
			viewHolderChild.mTextViewName.setText(friend.getName());
			viewHolderChild.mImageViewChat.setTag(friend);
			viewHolderChild.mImageViewChat.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(friend.getPhone())){
				viewHolderChild.mImageViewPhone.setVisibility(View.GONE);
			}else {
				viewHolderChild.mImageViewPhone.setVisibility(View.VISIBLE);
				viewHolderChild.mImageViewPhone.setTag(friend.getPhone());
			}
			
		}else {
			final Group group = (Group)getChild(groupPosition, childPosition);
			viewHolderChild.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(group.getAvatarUrl()));
			viewHolderChild.mTextViewName.setText(group.getName());
			viewHolderChild.mImageViewChat.setTag(group);
			viewHolderChild.mImageViewChat.setVisibility(View.INVISIBLE);
			viewHolderChild.mImageViewPhone.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public static class ViewHolderParent {
		ImageView mImageViewArrow;
		TextView  mTextViewAddressListName;
	}
	
	public static class ViewHolderChild {
		ImageView mImageViewAvatar;
		TextView  mTextViewName;
		public ImageView mImageViewChat;
		ImageView mImageViewPhone;
		public int groupPosition;
	}
	
	
	private OnExpandableListChildViewClickListener mOnExpandableListChildViewClickListener;
	
	
	public void setOnExpandableListChildViewClickListener(OnExpandableListChildViewClickListener listener){
		mOnExpandableListChildViewClickListener = listener;
	}

	
	@Override
	public void onClick(View v) {
		mOnExpandableListChildViewClickListener.onExpandableListChildViewClicked(v.getId(), v.getTag());
	}

	public static interface OnExpandableListChildViewClickListener{
		public void onExpandableListChildViewClicked(int nId, Object object);
	}

}
