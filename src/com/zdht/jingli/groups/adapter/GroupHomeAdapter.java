package com.zdht.jingli.groups.adapter;


import java.util.List;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class GroupHomeAdapter extends BaseAdapter implements OnClickListener {

    private OnChildViewClickListener mOnChildViewClickListener;
	
    public void setOnChildViewClickListener(OnChildViewClickListener listener){
    	mOnChildViewClickListener = listener;
    }
    
	private Context mContext;
	private Group mGroup; 
	
	
	public GroupHomeAdapter(Context context, Group group) {
		mContext = context;
		mGroup = group;
	}
	
	public void changeGroup(Group group){
		this.mGroup = group;
		notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_grouphome_headview, null);
		((ImageView)convertView.findViewById(R.id.ivGroup_avatar)).setImageBitmap(AvatarBmpProvider.getInstance().loadImage(mGroup.getAvatarUrl()));
		((TextView)convertView.findViewById(R.id.tvGroup_name)).setText(mGroup.getName());
		
		final List<String> adminList = mGroup.getmListAdmin();
		String admins = "";
		for (int i = 0; i < adminList.size(); i++) {
			admins = adminList.get(i) + " ";
		}
		
		((TextView)convertView.findViewById(R.id.tvGroup_admin)).setText(mContext.getString(R.string.admin) +"   "+ admins);
		// 成员人数
		TextView groupMember = (TextView)convertView.findViewById(R.id.group_member);
		groupMember.setText(mContext.getString(R.string.group_member) + ":" + 
				mGroup.getMemberNum() + mContext.getString(R.string.people));
		groupMember.setOnClickListener(this);
		String group_explanation = mContext.getResources().getString(R.string.group_explanation);
		String group_remark = mContext.getResources().getString(R.string.group_home_remark);
		
		((TextView)convertView.findViewById(R.id.tvGroup_explanation)).setText(group_explanation + ":" + mGroup.getExplanation());
		if(mGroup.isIsAdd()) {
			((TextView)convertView.findViewById(R.id.group_remark)).setText(group_remark  + ":" + mGroup.getRemark());
		}
		convertView.findViewById(R.id.btnAdd_group).setOnClickListener(this);
		convertView.findViewById(R.id.tvPast_activities).setOnClickListener(this);
		final Button btnAddGroup = (Button)convertView.findViewById(R.id.btnAdd_group);
		if(mGroup.isIsAdd()){
			btnAddGroup.setText(R.string.into_chat);
			btnAddGroup.setVisibility(View.GONE);
			groupMember.setClickable(true);
		}else {
			btnAddGroup.setText(R.string.add_group);
			btnAddGroup.setVisibility(View.VISIBLE);
			groupMember.setClickable(false);
		}
		return convertView;
	}
	
	public static interface OnChildViewClickListener{
		public void onChildViewClick(View v);
	}
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


	@Override
	public void onClick(View v) {
		
		if(mOnChildViewClickListener != null){
			mOnChildViewClickListener.onChildViewClick(v);
		}
	}
	
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		super.unregisterDataSetObserver(observer);
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
	}
}
