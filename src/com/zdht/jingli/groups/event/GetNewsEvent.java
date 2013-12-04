package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.model.News;


public class GetNewsEvent extends HttpGetEvent {

	private List<News> mListnews = new ArrayList<News>();
	private boolean hasMore;
	private String mDescribe;

	public boolean hasMore() {
		return hasMore;
	}

	@Override
	public void onPreRun() {
		super.onPreRun();
		mListnews.clear();
	}

	@Override
	public Object getReturnParam() {
		return Collections.unmodifiableList(mListnews);
	}

	public GetNewsEvent(int nEventCode, boolean isGetAll) {
		super(nEventCode);
		mIsGetAll = isGetAll;
	}

	public String getDescribe() {
		return mDescribe;
	}

	@Override
	public void run(Object... params) throws Exception {
		String strUrl = (String)params[0];
		int size = getPageSize();
		params[0] = strUrl + "&size=" + size;
		super.run(params);

		SCApplication.print(mHttpRetString + "\r\n-------------GetNewsEvent");
		if (!isNetSuccess()) {
			return;
		}
		JSONObject jsonObject = new JSONObject(getHttpRetString());
		mDescribe = jsonObject.getString("describe");
		if (isRequestSuccess()) {
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			final int nGroupCount = jsonArray.length();
			
			for (int nIndex = 0; nIndex < nGroupCount; ++nIndex) {
				JSONObject json = jsonArray.getJSONObject(nIndex);
				if(json.keys().hasNext()) {
					try {
						News news = new News(json);
						mListnews.add(news);
					}catch (JSONException e) {
					}
				}
			}
			if(mListnews.size() == size) {
				hasMore = true;
			} else {
				hasMore = false;
			}
		}
	}
}
