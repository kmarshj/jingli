package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.model.Group;


public class GetGroupEvent extends HttpGetEvent {

	private List<Group> mListGroup = new ArrayList<Group>();
	private boolean hasMore;
	private String mDescribe;
	private int totalCount;
	private int totalPageCount;
	private int currentPage;
	
	public boolean hasMore() {
		return hasMore;
	}
	
	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public Object getReturnParam() {
		return Collections.unmodifiableList(mListGroup);
//		return mListGroup;
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


	public GetGroupEvent(int nEventCode, boolean isGetAll) {
		super(nEventCode);
		mIsNeedAddUser = true;
		mIsGetAll = isGetAll;
		mIsNeedPaged   = true;
	}

	@Override
	public void onPreRun() {
		super.onPreRun();
		mListGroup.clear();
	}
	
	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		SCApplication.print("-------------获取圈子列表--------------\r\n" + getHttpRetString());
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				hasMore = jsonObject.getBoolean("hasMore");
				totalCount = jsonObject.getInt("totalCnt");
				totalPageCount = jsonObject.getInt("totalPageCnt");
				currentPage = jsonObject.getInt("currentPage");
				JSONArray jsonArray = jsonObject.getJSONArray("communities");
				final int nGroupCount = jsonArray.length();
				for(int nIndex = 0; nIndex < nGroupCount; ++nIndex){
					mListGroup.add(new Group(jsonArray.getJSONObject(nIndex), true));
				}
			}
		}
	}
}
