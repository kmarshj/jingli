package com.zdht.jingli.groups.ui;


import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.adapter.ActivityAdapter;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Activity;

public class ManagerActivitiesActivity extends BaseManagerActivity{

	private ActivityAdapter mActivityAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityAdapter = new ActivityAdapter(this);
		mListView.setAdapter(mActivityAdapter);
		final List<Activity> activitys = LocalInfoManager.getInstance().getListMyCreateActivity();
		if(activitys == null || activitys.size() == 0){
			getMyCreateActivity();
		}else {
			mActivityAdapter.replaceAll(LocalInfoManager.getInstance().getListMyCreateActivity());
		}
	}
	
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				mActivityAdapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	protected void onMyCreateActivityGetSuccess(List<Activity> list) {
		super.onMyCreateActivityGetSuccess(list);
		mActivityAdapter.replaceAll(list);
	}
	
	@Override
	protected void onMyCreateActivityGetFail() {
		super.onMyCreateActivityGetFail();
		
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.manage_activities;
	}

	public static void launch(android.app.Activity activity) {
		Intent intent = new Intent(activity, ManagerActivitiesActivity.class);
		activity.startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		CreateOrEditActivityActivity.launch(this, null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object != null){
			if (object instanceof Activity) {
				Activity activity = (Activity)object;
				CreateOrEditActivityActivity.launchForResult(this, activity);
			}
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CreateOrEditActivityActivity.REQUEST_CODE_FOR_EDIT &&
				resultCode == RESULT_OK && data != null) {
			// 有活动被删除
			boolean isDeleted = data.getBooleanExtra(CreateOrEditActivityActivity.RESULT_IS_DELETE, false);
			if(isDeleted) {
				Activity activity = (Activity)data.getSerializableExtra(CreateOrEditActivityActivity.EXTRA_ACTIVITY);
				List<Activity> activityList = mActivityAdapter.getmListObject();
				for(Activity mActivity : activityList) {
					if(mActivity.getId() == activity.getId()) {
						mActivityAdapter.removeItem(mActivity);
						break;
					}
				}
			}
		}
	}
}
