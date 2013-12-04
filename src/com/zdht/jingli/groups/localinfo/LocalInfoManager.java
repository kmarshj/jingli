package com.zdht.jingli.groups.localinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.SharedPreferenceManager;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.Sex;
import com.zdht.jingli.groups.model.User;



public class LocalInfoManager {

	private static LocalInfoManager sInstance;
	
	private String mAvatar;
	private Sex    mSex;
	private String mFaculty;
	private String mClass;
	private String mPhone;
	private String mSignature;
	private SharedPreferences sp;
	
	private List<Group> mListMyCreateGroup;
	
	public List<Group> getListMyCreateGroup() {
		return mListMyCreateGroup;
	}

	public void setListMyCreateGroup(List<Group> mListMyCreateGroup) {
		this.mListMyCreateGroup = mListMyCreateGroup;
	}

	private List<Activity> mListMyCreateActivity;
	
	public List<Activity> getListMyCreateActivity() {
		return mListMyCreateActivity;
	}

	public void setListMyCreateActivity(List<Activity> listMyCreateActivity) {
		this.mListMyCreateActivity = listMyCreateActivity;
	}

	public String getAvatar() {
		if(TextUtils.isEmpty(mAvatar)){
			mAvatar = sp.getString(SharedPreferenceManager.KEY_AVATAR, "");
		}
		return mAvatar;
	}
	

	public Sex getmSex() {
//		if(mSex == null){
//			mSex = Sex.valueOf(sp.getString(SharedPreferenceManager.KEY_SEX, ""));
//		}
		return mSex;
	}

	public String getmFaculty() {
		if(TextUtils.isEmpty(mFaculty)){
			mFaculty = sp.getString(SharedPreferenceManager.KEY_FACULTY, "");
		}
		return mFaculty;
	}

	public String getmClass() {
		if(TextUtils.isEmpty(mClass)){
			mClass = sp.getString(SharedPreferenceManager.KEY_ClASS, "");
		}
		return mClass;
	}

	public String getmPhone() {
		if(TextUtils.isEmpty(mPhone)){
			mPhone = sp.getString(SharedPreferenceManager.KEY_PHONE, "");
		}
		return mPhone;
	}
	
	public void setmAvatar(String mAvatar) {
		sp.edit().putString(SharedPreferenceManager.KEY_AVATAR, mAvatar).commit();
		this.mAvatar = mAvatar;
	}

	public void setmPhone(String mPhone) {
		sp.edit().putString(SharedPreferenceManager.KEY_PHONE, mPhone).commit();
		this.mPhone = mPhone;
	}

	public void setmSignature(String mSignature) {
		sp.edit().putString(SharedPreferenceManager.KEY_SIGNATURE, mSignature).commit();
		this.mSignature = mSignature;
	}

	public String getmSignature() {
		if(TextUtils.isEmpty(mSignature)){
			mSignature = sp.getString(SharedPreferenceManager.KEY_SIGNATURE, "");
		}
		return mSignature;
	}

	public static LocalInfoManager getInstance(){
		if(sInstance == null){
			sInstance = new LocalInfoManager();
		}
		return sInstance;
	}
	
	private LocalInfo mLocalInfo;
	
	private LocalInfoManager(){
		sp = SCApplication.getDefaultSharedPreferences();
		mLocalInfo = new LocalInfo();
		mLocalInfo.set(sp);
		mListMyCreateGroup    = new ArrayList<Group>();
		mListMyCreateActivity = new ArrayList<Activity>();
	}
	
	public void		saveDetailInfoJson(String strJson){
		try {
			final User mUser = new User(new JSONObject(strJson), false);
			sp.edit().putString(SharedPreferenceManager.KEY_AVATAR, mUser.getAvatarUrl()).commit();
			sp.edit().putString(SharedPreferenceManager.KEY_SEX, mUser.getSex().toString()).commit();
			sp.edit().putString(SharedPreferenceManager.KEY_FACULTY, mUser.getFaculties()).commit();
			sp.edit().putString(SharedPreferenceManager.KEY_ClASS, mUser.getmClass()).commit();
			sp.edit().putString(SharedPreferenceManager.KEY_PHONE, mUser.getPhone()).commit();
			sp.edit().putString(SharedPreferenceManager.KEY_SIGNATURE, mUser.getSignature()).commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void saveSchoolCode(String code) {
		sp.edit().putString(SharedPreferenceManager.KEY_SCHOOL_CODE, code).commit();
		mLocalInfo.set(sp);
	}

	public void saveSchoolName(String name) {
		sp.edit().putString(SharedPreferenceManager.KEY_SCHOOL_NAME, name).commit();
		mLocalInfo.set(sp);
	}

	public void saveLogoUrl(String url) {
		if(url == null) {
			url = "";
		}
		sp.edit().putString(SharedPreferenceManager.KEY_LOGO_URL, url).commit();
		mLocalInfo.set(sp);
	}
	
	public void editUserToken(String strUserToken){
		Editor et = sp.edit();
		et.putString(SharedPreferenceManager.KEY_USERTOKEN, strUserToken);
		et.commit();
		mLocalInfo.set(sp);
	}
	
	public void editPassWord(String strPassword){
		Editor et = sp.edit();
		et.putString(SharedPreferenceManager.KEY_USERPWD, strPassword);
		et.commit();
		mLocalInfo.set(sp);
	}
	
	/**
	 * 保存极光推送的tag， 以逗号分隔
	 * @param tags
	 */
	public void saveTags(String tags) {
		sp.edit().putString(SharedPreferenceManager.KEY_TAG, tags).commit();
		mLocalInfo.jTag = tags;
	}
	
	public void save(Map<String, String> map){
		Editor et = sp.edit();
		for (String str_key : map.keySet()) {
			et.putString(str_key, map.get(str_key));
		}
		et.commit();
		mLocalInfo.set(sp);
	}
	
	public void			onLoginOut(){
		mLocalInfo = new LocalInfo();
		mAvatar = null;
		mSex = null;
		mFaculty = null;
		mClass = null;
		mPhone = null;
		mSignature = null;
		SCApplication.getDefaultSharedPreferences().edit()
			.remove(SharedPreferenceManager.KEY_USERTOKEN)
			.remove(SharedPreferenceManager.KEY_USERID)
			.remove(SharedPreferenceManager.KEY_USERPWD)
			.remove(SharedPreferenceManager.KEY_REALNAME)
			.remove(SharedPreferenceManager.KEY_AVATAR)
			.remove(SharedPreferenceManager.KEY_SEX)
			.remove(SharedPreferenceManager.KEY_FACULTY)
			.remove(SharedPreferenceManager.KEY_ClASS)
			.remove(SharedPreferenceManager.KEY_PHONE)
			.remove(SharedPreferenceManager.KEY_SIGNATURE)
			.commit();
		clearMyCreate();
	}

	private void clearMyCreate(){
		if(mListMyCreateGroup != null){
//			mListMyCreateGroup.clear();
			mListMyCreateGroup = null;
		}
		if(mListMyCreateActivity != null){
//			mListMyCreateActivity.clear();
			mListMyCreateActivity = null;
		}
	}

	public String getSchoolCode() {
		return sp.getString(SharedPreferenceManager.KEY_SCHOOL_CODE, "");
	}
	
	public String getSchoolName() {
		return sp.getString(SharedPreferenceManager.KEY_SCHOOL_NAME, "");
	}
	
	public String getLogoUrl() {
		return sp.getString(SharedPreferenceManager.KEY_LOGO_URL, "");
	}

	public LocalInfo getmLocalInfo() {
		return mLocalInfo;
	}
	
	public boolean		isLocalUser(String userId){
		return mLocalInfo.getUserId().equals(userId);
	}
	
	
	public boolean		isLogined(){
		return mLocalInfo.hasLoginInfo();
	}
	
	
}
