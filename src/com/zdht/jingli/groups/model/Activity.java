package com.zdht.jingli.groups.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Activity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final int     mId;
	private final String  mName;
	private int     mGroupId;
	private String  mGroupName;
	private final String  mFromTime;
	private final String  mEndTime;
	private final String  mSite;
	private final String  mTotalNumber;
	private String  mNowNumber;
	private final String  mDetails;
	/** 是否参加 0-未参加 1-已参加 2-已参加，且是我创建的活动 */
	private int mIsAdd;
	private String  mPosterUrl;
	private final String  mAtlasImg;
	private final List<String> mListImage;
	
	
	public Activity(JSONObject jsonObject)throws JSONException{
		mId          = jsonObject.getInt("activityId");
		mName        = jsonObject.getString("activityName");
		if(jsonObject.has("communityId")){
			mGroupId     = jsonObject.getInt("communityId");
		}else {
			mGroupId     = 0;
		}
		if(jsonObject.has("communityName")){
			mGroupName   = jsonObject.getString("communityName");
		} else {
			mGroupName   = "";
		}
		
		mFromTime    = jsonObject.getString("beginTime");
		mEndTime     = jsonObject.getString("endTime");
		mSite        = jsonObject.getString("location");
		mTotalNumber = jsonObject.getString("limit");
		mNowNumber   = jsonObject.getString("joinNum");
		mDetails     = jsonObject.getString("summary");
		mIsAdd       = jsonObject.getInt("isAdd");
		mPosterUrl   = jsonObject.getString("poster");
		
		if(mPosterUrl.contains(",")){
			mPosterUrl = mPosterUrl.split(",")[0];
		}
		
		mAtlasImg    = jsonObject.getString("atlas");
		mListImage = new ArrayList<String>();
		JSONArray jsonArray = jsonObject.getJSONArray("images");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject imageJsonObject = (JSONObject) jsonArray.getJSONObject(i);
			mListImage.add(imageJsonObject.getString("url"));
		}
	}
	

	public int getId() {
		return mId;
	}


	public String getName() {
		return mName;
	}


	public int getGroupId() {
		return mGroupId;
	}


	public String getGroupName() {
		return mGroupName;
	}


	public String getFromTime() {
		return mFromTime;
	}


	public String getEndTime() {
		return mEndTime;
	}


	public String getSite() {
		return mSite;
	}


	public String getTotalNumber() {
		
		if(mTotalNumber.equals("-1")){
			return "不限";
		}
		
		return mTotalNumber;
	}


	public String getNowNumber() {
		return mNowNumber;
	}

	public void setNowNumber(String number) {
		mNowNumber = number;
	}

	public String getDetails() {
		return mDetails;
	}


	/** 是否参加 0-未参加 1-已参加 2-已参加，且是我创建的活动 */
	public int isIsAdd() {
		return mIsAdd;
	}

	public void setIsAdd(int isAdd){
		mIsAdd = isAdd;
	}

	public String getPosterUrl() {
		return mPosterUrl;
	}


	public String getAtlasImg() {
		return mAtlasImg;
	}


	public List<String> getListImage() {
		return mListImage;
	}


	
	
	
	
}
