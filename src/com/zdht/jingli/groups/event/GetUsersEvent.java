package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zdht.jingli.groups.model.User;


public class GetUsersEvent extends HttpGetEvent {

	public GetUsersEvent(int nEventCode, boolean isGetActivityMember, boolean isGetAll) {
		super(nEventCode);
		mIsNeedAddUser = true;
		mIsNeedPaged   = true;
		mIsGetAll = isGetAll;
		this.isGetActivityMember = isGetActivityMember;
	}

	private List<User> mListUser = new ArrayList<User>();
	private boolean hasMore;
	private String mDescribe;
	private int totalPageCount;
	private int currentPage;
	private int totalCount;
	private boolean isGetActivityMember;
	
	@Override
	public void onPreRun() {
		super.onPreRun();
		mListUser.clear();
	}
	
	public boolean hasMore() {
		return hasMore;
	}
	
	@Override
	public Object getReturnParam() {
		return Collections.unmodifiableList(mListUser);
	}
	
	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				hasMore = jsonObject.getBoolean("hasMore");
				totalPageCount = jsonObject.getInt("totalPageCnt");
				currentPage = jsonObject.getInt("currentPage");
				totalCount = jsonObject.getInt("totalCnt");
				JSONArray jsonArray = jsonObject.getJSONArray("users");
				final int nGroupCount = jsonArray.length();
				for(int nIndex = 0; nIndex < nGroupCount; ++nIndex){
					mListUser.add(new User(jsonArray.getJSONObject(nIndex), isGetActivityMember));
				}
			}
		}
	}

	public List<User> getListUser() {
		return mListUser;
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
	
	
	
}
