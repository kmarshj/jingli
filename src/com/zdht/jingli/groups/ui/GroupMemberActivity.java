package com.zdht.jingli.groups.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.adapter.GroupMemberAdapter;
import com.zdht.jingli.groups.adapter.GroupMemberAdapter.OnViewClickListener;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.GroupMember;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.utils.DialogUtils;

public class GroupMemberActivity extends SCBaseActivity implements View.OnClickListener, 
																   OnViewClickListener, 
																   OnItemClickListener{
	
	public static final String EXTRA_GROUP_MEMBER = "groupmember";
	public static final String EXTRA_GROUP_ID = "groupid";
	
	public static final int REQUEST_CODE_GROUP_MEMBER = 101;
	
	private Button mButtonManage;
	private static final String MANAGEMENT = "管理";
	private static final String COMMIT = "完成";
	private ListView groupMemberList;
	private GroupMemberAdapter adapter;
	
	private ArrayList<GroupMember> memberList;
	private String groupId;
	
	// 当前用户权限 0：创建者；1：管理员；2：普通成员 
	private String role = "2";
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		memberList = getIntent().getParcelableArrayListExtra(EXTRA_GROUP_MEMBER);
		groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
		for(int i = 0; i < memberList.size(); i++) {
			String userID = String.valueOf(memberList.get(i).mUserId);
			// 如果与本地帐号相同
			if(userID.equals(LocalInfoManager.getInstance().getmLocalInfo().getUserId())){
				role = memberList.get(i).role;
			}
		}
		if(!role.equals("2")){// 非普通成员时显示管理按钮
			mButtonManage = addButtonInTitleRight();
			mButtonManage.setText(MANAGEMENT);
			mButtonManage.setOnClickListener(this);
		}
		
		groupMemberList = (ListView)findViewById(R.id.group_member_list);
		adapter = new GroupMemberAdapter(this, memberList, role);
		adapter.setOnViewClickListener(this);
		groupMemberList.setAdapter(adapter);
		groupMemberList.setOnItemClickListener(this);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.group_member;
		ba.mAddBackButton = true;
	}

	/**
	 * 
	 * @param activity
	 * @param list 圈子成员列表
	 * @param groupId 圈子id
	 */
	public static void launchForResult(Activity activity, ArrayList<GroupMember> list, String groupId) {
//		SCApplication.print("launch--group-member:" + list.size());
		Intent intent = new Intent(activity, GroupMemberActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_GROUP_MEMBER, list);
		intent.putExtra(EXTRA_GROUP_ID, groupId);
		activity.startActivityForResult(intent, REQUEST_CODE_GROUP_MEMBER);
	}

	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		if(event.getEventCode() == EventCode.HTTPGET_AddFriend){
			// TODO 添加好友 事件处理
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			if(baseInfoGetEvent.isRequestSuccess()){// 添加好友请求成功
				Object[] params = baseInfoGetEvent.getRequestParams();
				if(params.length == 2){
					User user = (User)params[1];
					for(GroupMember member: memberList) {
						// 匹配被添加的好友
						if(member.mUserId == user.getUid()) {
							member.isFriend = true;
							adapter.notifyDataSetChanged();
						}
					}
				}
			}
		} else if(event.getEventCode() == EventCode.HTTPGET_DelComMember) {
			// TODO 删除群组成员 事件处理
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			if(baseInfoGetEvent.isRequestSuccess()) {
				Object[] params = baseInfoGetEvent.getRequestParams();
				if(params.length == 2) {
					GroupMember mMember = (GroupMember)params[1];
					if(mMember != null){
						memberList.remove(mMember);
						adapter.notifyDataSetChanged();
					}
				}
			} else {
				mToastManager.show(baseInfoGetEvent.getDescribe());
			}
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_title_right:// 标题栏右边按钮
			if(adapter.isEdit()) {
				mButtonManage.setText(MANAGEMENT);
				adapter.setEdit(false);
			} else {
				mButtonManage.setText(COMMIT);
				adapter.setEdit(true);
			}
			break;
		}
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		Object obj = v.getTag();
		if(!(obj instanceof GroupMember)){
			return;
		}
		
		final GroupMember member = (GroupMember)obj;
		switch(v.getId()){
		case R.id.ivChat:// 聊天
			final String userIdForInfo = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
			SingleChatActivity.launch(this, member.mUserName, member.mName, member.mAvatar, userIdForInfo);
			break;
		case R.id.group_member_item_add:// 添加好友
			DialogUtils.showAlertDialog(this, "确定添加" + member.mName + "为好友吗？", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					User user = new User(member);
					addFriend(user);
				}
			});
			break;
		case R.id.group_member_item_delete:// 删除好友
			DialogUtils.showAlertDialog(this, "确定删除圈子成员 " + member.mName + " 吗？", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 删除好友
					delComMember(groupId, member);
				}
			});
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO onBackPressed
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(EXTRA_GROUP_MEMBER, memberList);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object != null && object instanceof GroupMember) {
			GroupMember mGroupMember = (GroupMember)object;
			if(!isLocalUser(mGroupMember.mUserId)){
				UserHomeActivity.launch(this, new User(mGroupMember));
			}
		}
	}
	
}
