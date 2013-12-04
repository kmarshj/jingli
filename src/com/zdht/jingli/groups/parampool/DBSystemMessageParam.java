package com.zdht.jingli.groups.parampool;

import java.util.List;

import com.zdht.jingli.groups.model.SystemMessage;



public class DBSystemMessageParam {
	
	public int						mHandleType;
	
	public SystemMessage 			mSaveMessage;
	
	public String					mId;
	
	public List<SystemMessage> 	   mListReadMessage;
	
	
	public DBSystemMessageParam(int nHandleType){
		mHandleType = nHandleType;
	}
}
