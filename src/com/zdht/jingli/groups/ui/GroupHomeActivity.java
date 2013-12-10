package com.zdht.jingli.groups.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.ActivityAdapter;
import com.zdht.jingli.groups.adapter.GroupHomeAdapter;
import com.zdht.jingli.groups.adapter.GroupHomeAdapter.OnChildViewClickListener;
import com.zdht.jingli.groups.adapter.SectionAdapter;
import com.zdht.jingli.groups.event.AddGroupEvent;
import com.zdht.jingli.groups.event.GetActivitiesEvent;
import com.zdht.jingli.groups.event.GetGroupInfoEvent;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.GroupMember;

/**
 * 圈子主页
 * @author think
 *
 */
public class GroupHomeActivity extends SCBaseActivity implements OnChildViewClickListener,
																 OnItemClickListener{

	private final static String EXTRA_ISTABACITIVITYCONTENT = "isTabActivityContent";
	private final static String EXTTR_GROUPID = "groupid";
	
	private SectionAdapter mSectionAdapter;
	private GroupHomeAdapter mGroupHomeAdapter;
	private ListView mListViewOngoingActivities;
	private ActivityAdapter mActivityAdapter;
	private Group mGroup;
	private com.zdht.jingli.groups.model.Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getBooleanExtra(EXTRA_ISTABACITIVITYCONTENT, true)){
			mRelativeLayoutTitle.setVisibility(View.GONE);
		}
		mListViewOngoingActivities = (ListView)findViewById(R.id.lvOngoing_activities);
		mListViewOngoingActivities.setOnItemClickListener(this);
		mSectionAdapter = new SectionAdapter();
		
		activity = (com.zdht.jingli.groups.model.Activity)getIntent().getSerializableExtra(ActivityMainActivity.EXTRA_ACTIVITY);
		showProgressDialog(null, R.string.geting_group_info);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetGroupInfo, this, true);
		if(activity == null){
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetGroupInfo, 0,
					String.format(URLUtils.URL_GetGroupInfo, getIntent().getIntExtra(EXTTR_GROUPID, 0)));
		}else {
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetGroupInfo, 0,
					String.format(URLUtils.URL_GetGroupInfo, activity.getGroupId()));
		}
		
	}
	
	private void showInfo(){
		if(mGroupHomeAdapter == null) {
			mGroupHomeAdapter = new GroupHomeAdapter(this, mGroup);
			mGroupHomeAdapter.setOnChildViewClickListener(this);
			mSectionAdapter.addSection(mGroupHomeAdapter);
			mListViewOngoingActivities.setAdapter(mSectionAdapter);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetGroupOnGoingActivioes, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetGroupOnGoingActivioes, 0,
					String.format(URLUtils.URL_GetActivities, 7, mGroup.getId(), 1));
		} else {
			mGroupHomeAdapter.changeGroup(mGroup);
			mSectionAdapter.notifyDataSetChanged();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPGET_GetGroupInfo){
			GetGroupInfoEvent getGroupInfoEvent = (GetGroupInfoEvent)event;
			if(getGroupInfoEvent.isRequestSuccess()){
				mGroup = (Group)getGroupInfoEvent.getReturnParam();
				showInfo();
			}else {
				mToastManager.show(getGroupInfoEvent.getDescribe());
			}
		}else if(nCode == EventCode.HTTPGET_GetGroupOnGoingActivioes){
			GetActivitiesEvent getActivitiesEvent = (GetActivitiesEvent)event;
			if(getActivitiesEvent.isRequestSuccess()){
				final List<com.zdht.jingli.groups.model.Activity> list = (List<com.zdht.jingli.groups.model.Activity>)getActivitiesEvent.getReturnParam();
				if(list.size() != 0){
					mActivityAdapter = new ActivityAdapter(this);
					mActivityAdapter.replaceAll(list);
					if(mActivityAdapter.getCount() > 0){
						mSectionAdapter.addSection(mActivityAdapter);
						mSectionAdapter.notifyDataSetChanged();
					}
				}
			}/*else {
				mToastManager.show(getActivitiesEvent.getDescribe());
			}*/
		}/*else if(nCode == EventCode.HTTPGET_QuitGroup){
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				
			}
		}*/
		else if(nCode == EventCode.HTTPGET_AddGroup){
			AddGroupEvent addGroupEvent = (AddGroupEvent)event;
			mToastManager.show(addGroupEvent.getDescribe());
			if(addGroupEvent.isRequestSuccess()){
				mToastManager.show(addGroupEvent.getDescribe());
				if(!addGroupEvent.getDescribe().equals("请求已提交,等待验证中")) {
					Group group = (Group)addGroupEvent.getReturnParam();
					group.setIsAdd(true);
					mGroupHomeAdapter.changeGroup(mGroup);
					mSectionAdapter.notifyDataSetChanged();
				}
//				showProgressDialog(null, R.string.geting_group_info);
//				AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetGroupInfo, this, true);
//				AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetGroupInfo, 0,
//						String.format(URLUtils.URL_GetGroupInfo, mGroup.getId()));
			}
		}
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.home;
		ba.mAddBackButton = true;
	}
	
	public static void launch(Activity activity, int groupId ){
		Intent intent = new Intent(activity, GroupHomeActivity.class);
		intent.putExtra(EXTTR_GROUPID, groupId);
		intent.putExtra(EXTRA_ISTABACITIVITYCONTENT, false);
		activity.startActivity(intent);
	}

	@Override
	public void onChildViewClick(View v) {
		final int nId = v.getId();
		switch(nId) {
		case R.id.btnAdd_group:
			if(mGroup.isIsAdd()){
				ChatRoomActivity.launch(this, String.valueOf(mGroup.getId()), mGroup.getName());
			}else {
				addGroup(mGroup);
			}
			break;
		case R.id.tvPast_activities:
			ActivityListActivity.launch(this, true, mGroup.getId());
			break;
		case R.id.group_member:// 打开成员列表
			GroupMemberActivity.launchForResult(this, mGroup.getMembers(), String.valueOf(mGroup.getId()));
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == GroupMemberActivity.REQUEST_CODE_GROUP_MEMBER && resultCode == RESULT_OK && data != null) {
			ArrayList<GroupMember> memberList = data.getParcelableArrayListExtra(GroupMemberActivity.EXTRA_GROUP_MEMBER);
			mGroup.setMemberNum(memberList.size());
			mGroup.setMembers(memberList);
			mGroupHomeAdapter.changeGroup(mGroup);
			mSectionAdapter.notifyDataSetChanged();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object != null){
			if (object instanceof Activity) {
				com.zdht.jingli.groups.model.Activity activity = (com.zdht.jingli.groups.model.Activity)object;
				ActivityMainActivity.launch(this, activity);
			}
		}
	}
}
