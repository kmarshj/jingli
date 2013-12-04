package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zdht.jingli.groups.model.NetImage;


public class PostFileEvent extends HttpPostEvent {

	private String mDescribe;

	public String getDescribe() {
		return mDescribe;
	}
	
	private String mUrl;
	private String mRelrelativepath;
	
	
	public String getmUrl() {
		return mUrl;
	}

	public String getmRelrelativepath() {
		return mRelrelativepath;
	}

	List<NetImage> mListNetImage = new ArrayList<NetImage>();

	public PostFileEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
	}

	@Override
	public void onPreRun() {
		super.onPreRun();
		mListNetImage.clear();
	}

	@Override
	public Object getReturnParam() {
		return Collections.unmodifiableList(mListNetImage);
	}

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if (isNetSuccess()) {
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				final JSONArray jsonArray = jsonObject.getJSONArray("path");
				for (int i = 0; i < jsonArray.length(); i++) {
					final JSONObject jsonObjectNetImage = jsonArray.getJSONObject(i);
					mListNetImage.add(new NetImage(jsonObjectNetImage.getString("url"), jsonObjectNetImage.getString("relativepath")));
				}
				if(mListNetImage.size() == 1){
					mUrl = mListNetImage.get(0).getImageUrl();
					mRelrelativepath = mListNetImage.get(0).getRelativepath();
				}
			}
		}
	}

}
