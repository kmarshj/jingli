package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zdht.jingli.groups.model.Live;

import android.R.integer;


public class GetLiveInfoEvent extends HttpGetEvent {

	public GetLiveInfoEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
		mIsNeedPaged   = true;
	}
	
	private List<Live> mListLive = new ArrayList<Live>();
	private boolean hasMore;
	private String mDescribe;
	private int totalPageCount;
	private int currentPage;
	private int totalCount;
	
	public boolean hasMore() {
		return hasMore;
	}
	
	@Override
	public Object getReturnParam() {
		return Collections.unmodifiableList(mListLive);
	}
	
	@Override
	public void onPreRun() {
		super.onPreRun();
		mListLive.clear();
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
				totalCount = jsonObject.getInt("totalCnt");
				currentPage = jsonObject.getInt("currentPage");
				JSONArray jsonArray = jsonObject.getJSONArray("broadcast");
				final int nGroupCount = jsonArray.length();
				for(int nIndex = 0; nIndex < nGroupCount; ++nIndex){
					mListLive.add(new Live(jsonArray.getJSONObject(nIndex)));
				}
			}
		}
	}

	public List<Live> getListLive() {
		return mListLive;
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

}
