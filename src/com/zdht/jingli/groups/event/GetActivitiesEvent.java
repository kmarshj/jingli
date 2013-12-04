package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zdht.jingli.groups.model.Activity;


public class GetActivitiesEvent extends HttpGetEvent {

	
	private List<Activity> mListActivity = new ArrayList<Activity>();
	private boolean hasMore;
	private String mDescribe;
	private int totalCount;
	private int totalPageCount;
	private int currentPage;
	
	public boolean hasMore() {
		return hasMore;
	}
	
	@Override
	public void onPreRun() {
		super.onPreRun();
		mListActivity.clear();
	}
	
	@Override
	public Object getReturnParam() {
		return Collections.unmodifiableList(mListActivity);
	}
	
	
	public GetActivitiesEvent(int nEventCode, boolean isGetAll) {
		super(nEventCode);
		mIsGetAll = isGetAll;
		mIsNeedAddUser = true;
		mIsNeedPaged   = true;
	}

	public String getDescribe() {
		return mDescribe;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		
//		SCApplication.print(mHttpRetString + "\r\n-------------GetActivitiesEvent");
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				hasMore = jsonObject.getBoolean("hasMore");
				totalCount = jsonObject.getInt("totalCnt");
				totalPageCount = jsonObject.getInt("totalPageCnt");
				currentPage = jsonObject.getInt("currentPage");
				if(totalCount != 0){
					JSONArray jsonArray = jsonObject.getJSONArray("activities");
					final int nGroupCount = jsonArray.length();
					for(int nIndex = 0; nIndex < nGroupCount; ++nIndex){
						mListActivity.add(new Activity(jsonArray.getJSONObject(nIndex)));
					}
				}
			}
		}
	}
}
