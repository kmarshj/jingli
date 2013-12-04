package com.zdht.jingli.groups.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FacultyClassesStructure {

	
	
	private final List<Grade> mListGrade;
	
	private final String	mSign;
	
	public FacultyClassesStructure(String json) throws JSONException{
		List<Grade> listgGrade = null;
		
		final JSONObject jsonObject = new JSONObject(json);
		
		JSONArray jsonArray = jsonObject.getJSONArray("structure");
		
		final int count = jsonArray.length();
		if(count > 0){
			listgGrade = new ArrayList<Grade>();
			for(int nIndex = 0;nIndex < count;++nIndex){
				listgGrade.add(new Grade(jsonArray.getJSONObject(nIndex)));
			}
		}
		mSign = jsonObject.getString("version");
		mListGrade = listgGrade;
	}

	public List<Grade> getmListGrade() {
		return mListGrade;
	}

	public String getmSign() {
		return mSign;
	}
	
	
	
	
	
}
