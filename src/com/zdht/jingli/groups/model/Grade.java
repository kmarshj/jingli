package com.zdht.jingli.groups.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Grade implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final String mName;
	private final List<Faculty> mListfFaculty;
	
	public Grade(JSONObject jsonObject)throws JSONException{
		mName        = jsonObject.getString("NJ");
		mListfFaculty = new ArrayList<Faculty>();
		JSONArray jsonArray = jsonObject.getJSONArray("faculties");
		final int nIndex = jsonArray.length();
		for (int i = 0; i < nIndex; i++) {
			mListfFaculty.add(new Faculty(jsonArray.getJSONObject(i)));
		}
	}

	public String getName() {
		return mName;
	}

	public List<Faculty> getmListfFaculty() {
		return mListfFaculty;
	}

	
	
}