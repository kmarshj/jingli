package com.zdht.jingli.groups.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zdht.jingli.groups.localinfo.LocalInfo;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;


/**
 * 互动
 * @author think
 *
 */
public class Live {

	//private final int mId;
	private final int mUserId;
	private final String mUserName;
	private final String mUserAvatar;
	private final String mTime;
	private final String mContent;
	private final String mImageUrl;

	public Live(JSONObject jsonObject) throws JSONException {
		//mId = jsonObject.getInt("id");
		mUserId = jsonObject.getInt("userId");
		mUserName = jsonObject.getString("name");
		mUserAvatar = jsonObject.getString("avatar");
		mTime = jsonObject.getString("sendTime");
		mContent = jsonObject.getString("text");
		mImageUrl = jsonObject.getString("image").equals("null") ? "" : jsonObject.getString("image");
	}
	
	/** 构建自己发的互动信息 */
	public Live(String time, String content, String imageUrl) {
		LocalInfo mLocalInfo = LocalInfoManager.getInstance().getmLocalInfo();
		mUserId = Integer.valueOf(mLocalInfo.getUserId());
		mUserName = mLocalInfo.getRealName();
		mUserAvatar = LocalInfoManager.getInstance().getAvatar();
		mTime = time;
		mContent = content;
		mImageUrl = imageUrl;
	}

	/*public int getId() {
		return mId;
	}*/

	public int getUserId() {
		return mUserId;
	}

	public String getUserName() {
		return mUserName;
	}

	public String getUserAvatar() {
		return mUserAvatar;
	}

	public String getTime() {
		return mTime;
	}

	public String getContent() {
		return mContent;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
	
	
	
	
}
