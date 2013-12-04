package com.zdht.jingli.groups.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;


public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int mUid;
	private String mStudentId;
	private final String mName;
	private final String mAvatarUrl;
	private final Sex mSex;
	private final String mFaculties;
	private final String mClass;
	private String mSignature;
	private final String mPhone;
	private boolean mIsFriend;
	//private String mAddGroupTime;
	private String mAddActivityTime;
	private boolean mIsGroupMember;
	/** 0：创建者；1：管理员；2：普通成员 */
	public String role;

	public User(JSONObject jsonObject, boolean isGetActivityMember) throws JSONException {
		mUid = jsonObject.getInt("userId");
		if(jsonObject.toString().contains("userName")){
			mStudentId = jsonObject.getString("userName");
		}
		mAvatarUrl = jsonObject.getString("avatar");
		if(!jsonObject.isNull("name")){
			mName = jsonObject.getString("name");
		} else {
			mName = "";
		}
		mSex = Sex.valueOf(jsonObject.getString("sex").equals("M") ? 0 : 1);
		mFaculties = jsonObject.getString("faculty");
		mClass = jsonObject.getString("class");
		mPhone = jsonObject.getString("phone");
		if(jsonObject.toString().contains("signature")){
			mSignature = jsonObject.getString("signature");
		}
		if(jsonObject.toString().contains("isFriend")){
			mIsFriend = jsonObject.getBoolean("isFriend");
		}else {
			mIsFriend = true;
		}	
		if(isGetActivityMember){
			//mAddGroupTime = jsonObject.getString("addtime");
			mAddActivityTime = jsonObject.getString("entryTime");
			mIsGroupMember = jsonObject.getBoolean("isCommunityMember");
		}
		if(jsonObject.has("role")) {
			role = jsonObject.getString("role");
		}
	}
	
	public User(GroupMember member) {
		mUid = member.mUserId;
		mStudentId = member.mUserName;
		mName = member.mName;
		mAvatarUrl = member.mAvatar;
		mSex = Sex.valueOf(member.mSex.equals("M") ? 0 : 1);
		mFaculties = member.mFaculty;
		mClass = member.mClass;
		mSignature = member.mSignature;
		mPhone = member.mPhone;
		mIsFriend = member.isFriend;
		role = member.role;
	}
	
//	public User(XMessage m) {
//		mUid = 11;
//		mSex = Sex.valueOf(m.get.equals("M") ? 0 : 1);
//	}
	
	public String getRole() {
		return role == null? "":role;
	}

	public int getUid() {
		return mUid;
	}

	public String getStudentId() {
		return mStudentId;
	}

	public String getName() {
		return mName;
	}

	public String getAvatarUrl() {
		return mAvatarUrl;
	}

	public Sex getSex() {
		return mSex;
	}

	public String getFaculties() {
		return mFaculties;
	}

	public String getmClass() {
		return mClass;
	}

	public String getSignature() {
		return mSignature;
	}

	public String getPhone() {
		return mPhone;
	}

	public boolean isIsFriend() {
		return mIsFriend;
	}
	
	public void isFriend(boolean isFriend) {
		this.mIsFriend = isFriend;
	}

	/*public String getAddGroupTime() {
		return mAddGroupTime;
	}*/

	public String getAddActivityTime() {
		return mAddActivityTime;
	}

	public boolean isGroupMember() {
		return mIsGroupMember;
	}

	

	

	

	
	
}
