package com.zdht.jingli.groups.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Class implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final String mId;
	private final String mName;
	
	
	public Class(JSONObject jsonObject)throws JSONException{
		mId          = jsonObject.getString("id");
		mName        = jsonObject.getString("BJMC");
	}


	public String getId() {
		return mId;
	}


	public String getName() {
		return mName;
	}
	
	
}
