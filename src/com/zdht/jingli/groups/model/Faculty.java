package com.zdht.jingli.groups.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Faculty implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final int    mId;
	private final String mName;
	private final List<Class> mListClass;
	
	public Faculty(JSONObject jsonObject)throws JSONException{
		mId          = jsonObject.getInt("id");
		mName        = jsonObject.getString("XYMC");
		mListClass   = new ArrayList<Class>();
		JSONArray jsonArray = jsonObject.getJSONArray("classes");
		final int nIndex = jsonArray.length();
		for (int i = 0; i < nIndex; i++) {
			mListClass.add(new Class(jsonArray.getJSONObject(i)));
		}
	}

	public int getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public List<Class> getmListClass() {
		return mListClass;
	}
	
}
