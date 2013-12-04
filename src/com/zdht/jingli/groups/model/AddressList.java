package com.zdht.jingli.groups.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<User> mListCLassFriend;
	private List<User> mListFriend;
	private List<Group> mListGroup;
	private String versionFriend;
	private String versionCLassFriend;
	private String versionGroup;
	
	public void clear(){
		if(mListFriend != null){
			mListFriend.clear();
		}
		if(mListCLassFriend != null){
			mListCLassFriend.clear();
		}
		if(mListGroup != null){
			mListGroup.clear();
		}
	}
	
	
	public void setParam(String jsonStr, int mType)throws JSONException{
		final JSONObject jsonObject = new JSONObject(jsonStr);
		try {
			if(mType == 1){
				versionFriend = jsonObject.getString("version");
				JSONArray jsonArray = jsonObject.getJSONArray("friends");
				if(jsonArray.length() != 0) {
					mListFriend.clear();
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					final JSONObject jsonFriend = jsonArray.getJSONObject(i);
					final User friend = new User(jsonFriend, false);
					mListFriend.add(friend);
				}
			}
			
			if(mType == 2){
				versionCLassFriend = jsonObject.getString("version");
				JSONArray jsonArray = jsonObject.getJSONArray("friends");
				if(jsonArray.length() != 0) {
					mListCLassFriend.clear();
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					final JSONObject jsonFriend = jsonArray.getJSONObject(i);
					final User friend = new User(jsonFriend, false);
					mListCLassFriend.add(friend);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		if(mType == 3){
			versionGroup = jsonObject.getString("version");
			JSONArray jsonArray = jsonObject.getJSONArray("communities");
			if(jsonArray.length() != 0) {
				mListGroup.clear();
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				final JSONObject jsonFriend = jsonArray.getJSONObject(i);
				final Group group = new Group(jsonFriend, true);
				mListGroup.add(group);
			}
		}
	}
	
	public String getVersionFriend() {
		return versionFriend;
	}


	public String getVersionCLassFriend() {
		return versionCLassFriend;
	}


	public String getVersionGroup() {
		return versionGroup;
	}


	public AddressList(){
		mListFriend = new ArrayList<User>();
		mListCLassFriend = new ArrayList<User>();
		mListGroup = new ArrayList<Group>();
	}

	public List<User> getListFriend() {
		return mListFriend;
	}
	
	public List<User> getmListCLassFriend() {
		return mListCLassFriend;
	}


	public List<Group> getListGroup() {
		return mListGroup;
	}
	
	
	public int getType(){
		return 1;
	}
	
	public String getName(){
		return "";
	}
}
