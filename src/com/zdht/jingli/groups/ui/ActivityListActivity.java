package com.zdht.jingli.groups.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.ActivityAdapter;
import com.zdht.jingli.groups.event.GetActivitiesEvent;
import com.zdht.jingli.groups.model.Activity;

public class ActivityListActivity extends BaseManagerActivity {

	private final static String EXTRA_ISPAST = "ispast";
	private final static String EXTRA_GROUPID = "groupid";
	
	private ActivityAdapter mActivityAdapter;
	private boolean isPast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mButtonBuild.setVisibility(View.GONE);
		isPast = getIntent().getBooleanExtra(EXTRA_ISPAST, false);
		if(isPast){
			mTextViewTitle.setText(getString(R.string.past_activities).substring(0, 4));
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetGroupPastActivioes, this, true);
			showProgressDialog(null, R.string.geting_past_activities);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetGroupPastActivioes, 0,
					String.format(URLUtils.URL_GetActivities, 6, getIntent().getIntExtra(EXTRA_GROUPID, 0), 1));
		}else {
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetMyAddActivioes, this, true);
			showProgressDialog(null, R.string.geting_my_add_activities);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetMyAddActivioes, 0,
					String.format(URLUtils.URL_GetActivities, 5, "", 1));
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPGET_GetGroupPastActivioes){
			GetActivitiesEvent getActivitiesEvent = (GetActivitiesEvent)event;
			if(getActivitiesEvent.isRequestSuccess()){
				final List<com.zdht.jingli.groups.model.Activity> list = (List<com.zdht.jingli.groups.model.Activity>)getActivitiesEvent.getReturnParam();
				if(list.size() != 0){
					mActivityAdapter = new ActivityAdapter(this);
					mActivityAdapter.replaceAll(list);
					mListView.setAdapter(mActivityAdapter);
				}
			}else {
				mToastManager.show(getActivitiesEvent.getDescribe());
			}
		}else if(nCode == EventCode.HTTPGET_GetMyAddActivioes){
			GetActivitiesEvent getActivitiesEvent = (GetActivitiesEvent)event;
			if(getActivitiesEvent.isRequestSuccess()){
				final List<com.zdht.jingli.groups.model.Activity> list = (List<com.zdht.jingli.groups.model.Activity>)getActivitiesEvent.getReturnParam();
				if(list.size() != 0){
					mActivityAdapter = new ActivityAdapter(this);
					mActivityAdapter.replaceAll(list);
					mListView.setAdapter(mActivityAdapter);
				}
			}else {
				mToastManager.show(getActivitiesEvent.getDescribe());
			}
		}		
	}
	
	@Override
	protected void onDestroy() {
		if(mActivityAdapter != null){
			mActivityAdapter.clear();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.my_activities;
	}
	
	public static void launch(android.app.Activity activity, boolean isPast, int groupId) {
		Intent intent = new Intent(activity, ActivityListActivity.class);
		intent.putExtra(EXTRA_ISPAST, isPast);
		intent.putExtra(EXTRA_GROUPID, groupId);
		activity.startActivity(intent);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object != null){
			if (object instanceof Activity) {
				Activity activity = (Activity)object;
				ActivityMainActivity.launch(this, activity);
			}
		}
	}
	
	
}
