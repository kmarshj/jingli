package com.zdht.jingli.groups.adapter;

import java.util.ArrayList;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.GroupMember;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.jingli.groups.utils.DialogUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class GroupMemberAdapter extends BaseAdapter implements OnClickListener{
	private Context context;
	private ArrayList<GroupMember> members;
	/** 是否编辑模式 */
	private boolean isEdit = false;
	
	private OnViewClickListener listener;
	
	private String localUserId;
	
	private String role;
	
	public GroupMemberAdapter(Context context, ArrayList<GroupMember> members, String role) {
		this.context = context;
		this.members = members;
		this.role = role;
		localUserId = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
	}
	
	public void setEdit(boolean isEdit) {
		if(this.isEdit != isEdit) {
			this.isEdit = isEdit;
			notifyDataSetChanged();
		}
	}
	
	public boolean isEdit(){
		return isEdit;
	}
	
	@Override
	public int getCount() {
		return members.size();
	}

	@Override
	public Object getItem(int arg0) {
		return members.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position , View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_group_member_item, null);
			viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.ivAvatar);
			viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
			viewHolder.friendLayout = (LinearLayout)convertView.findViewById(R.id.group_member_item_friend);
			viewHolder.ivChat = (ImageView)convertView.findViewById(R.id.ivChat);
			viewHolder.ivPhone = (ImageView)convertView.findViewById(R.id.ivPhone);
			viewHolder.addFriend = (TextView)convertView.findViewById(R.id.group_member_item_add);
			viewHolder.delete = (Button)convertView.findViewById(R.id.group_member_item_delete);
			viewHolder.ivChat.setOnClickListener(this);
			viewHolder.ivPhone.setOnClickListener(this);
			viewHolder.addFriend.setOnClickListener(this);
			viewHolder.delete.setOnClickListener(this);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		GroupMember member = members.get(position);
		viewHolder.ivAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(member.mAvatar));
		viewHolder.tvName.setText(member.mName);
		if(member.role.equals("1") || member.role.equals("0")) {
			Drawable right = context.getResources().getDrawable(R.drawable.admin);
			right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
			viewHolder.tvName.setCompoundDrawables(right, null, null, null);
		} else {
			viewHolder.tvName.setCompoundDrawables(null, null, null, null);
		}
		viewHolder.ivChat.setTag(member);
		viewHolder.delete.setTag(member);
		viewHolder.addFriend.setTag(member);
		viewHolder.ivPhone.setTag(position);
		// 如果是自己， 则什么操作也显示
		if(localUserId.equals(String.valueOf(member.mUserId))) {
			viewHolder.friendLayout.setVisibility(View.GONE);
			viewHolder.addFriend.setVisibility(View.GONE);
			viewHolder.delete.setVisibility(View.GONE);
		} else {
			if(isEdit) {
				viewHolder.friendLayout.setVisibility(View.GONE);
				viewHolder.addFriend.setVisibility(View.GONE);
				viewHolder.delete.setVisibility(View.VISIBLE);
				if(role.equals("1") && !member.role.equals("2")){
					viewHolder.delete.setVisibility(View.GONE);
				}
			} else {
				viewHolder.delete.setVisibility(View.GONE);
				if(member.isFriend) {
					viewHolder.friendLayout.setVisibility(View.VISIBLE);
					viewHolder.addFriend.setVisibility(View.GONE);
					if(TextUtils.isEmpty(member.mPhone)) {
						viewHolder.ivPhone.setVisibility(View.GONE);
					} else {
						viewHolder.ivPhone.setVisibility(View.VISIBLE);
					}
				} else {
					viewHolder.friendLayout.setVisibility(View.GONE);
					viewHolder.addFriend.setVisibility(View.VISIBLE);
				}
			}
		}
		return convertView;
	}

	
	class ViewHolder {
		public ImageView ivAvatar;
		public TextView tvName;
		/** 好友操作 */
		public LinearLayout friendLayout;
		public ImageView ivChat;
		public ImageView ivPhone;
		
		public TextView addFriend;
		public Button delete;
	}

	@Override
	public void onClick(View v) {
		// TODO 
		if(v.getId() == R.id.ivPhone) {
			int position = (Integer)v.getTag();
			GroupMember member = members.get(position);
			final String phone = member.mPhone;
			final String name = member.mName;
			if(TextUtils.isEmpty(phone)) {
				Toast.makeText(context, "该用户未绑定电话号码", Toast.LENGTH_SHORT).show();
			} else {
				//用intent启动拨打电话  
                DialogUtils.showAlertDialog(context, "温馨提示", 
                		"确定拨打电话给" + name + "(" +  phone + ")吗?", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					try {
    						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone)); 
    						context.startActivity(intent);
    					} catch (Exception e) {
    					}
    				}
    			});
			}
			return;
		}
		if(listener != null) {
			listener.onViewClick(v);
		}
	}
	
	public void setOnViewClickListener(OnViewClickListener listener) {
		this.listener = listener;
	}
	
	public static interface OnViewClickListener {
		public abstract void onViewClick(View v);
	}
	
}
