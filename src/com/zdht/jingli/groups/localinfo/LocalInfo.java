package com.zdht.jingli.groups.localinfo;

import com.zdht.jingli.groups.SharedPreferenceManager;

import android.content.SharedPreferences;
import android.text.TextUtils;

public class LocalInfo implements LocalBaseInfoProtocol{
	
	private final static String DEF_VALUE = "";
	
	private final static String DEF_USER_ID = "28924";
	private final static String DEF_SCHOOL_CODE = "jinli";
	
	
	protected String mUserId       = "";
	
	protected String mStudentId    = "";
	
	protected String mUserPwd      = "";
	
	protected String mRealName     = "";
	
	protected String schoolCode	="";
	
	protected String schoolName	="";
	
	protected String logoUrl	="";
	
	/** 用户标签 */
	protected String jTag = "";
	
	
	public boolean hasLoginInfo(){
		return !TextUtils.isEmpty(mUserId) && 
				!TextUtils.isEmpty(mUserPwd) && 
				!TextUtils.isEmpty(mStudentId);
	}
	
	
	public void set(SharedPreferences sp){
		
		mUserId = sp.getString(SharedPreferenceManager.KEY_USERID, DEF_USER_ID);
		
		mStudentId = sp.getString(SharedPreferenceManager.KEY_STUDENTID, DEF_VALUE);
		
		mUserPwd = sp.getString(SharedPreferenceManager.KEY_USERPWD, DEF_VALUE);
		
		mRealName = sp.getString(SharedPreferenceManager.KEY_REALNAME, DEF_VALUE);

		schoolCode = sp.getString(SharedPreferenceManager.KEY_SCHOOL_CODE, DEF_SCHOOL_CODE);

		schoolName = sp.getString(SharedPreferenceManager.KEY_SCHOOL_NAME, DEF_VALUE);

		logoUrl = sp.getString(SharedPreferenceManager.KEY_LOGO_URL, DEF_VALUE);
		
		jTag = sp.getString(SharedPreferenceManager.KEY_TAG, DEF_VALUE);
	}

	public String getSchoolCode() {
		return schoolCode;
	}
	
	public String getSchoolName() {
		return schoolName;
	}
	
	public String getLogoUrl() {
		return logoUrl;
	}
	
	public String getJTag() {
		return jTag;
	}

	@Override
	public String getUserPwd() {
		return mUserPwd;
	}


	@Override
	public String getUserId() {
		return mUserId;
	}


	@Override
	public String getStudentId() {
		return mStudentId;
	}


	@Override
	public String getRealName() {
		return mRealName;
	}

}
