package com.zdht.jingli.groups.parampool;

import java.util.List;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.groups.model.HPMessage;

public class DBMessageParam {
	
	public int						mHandleType;
	
	public HPMessage 				mSaveMessage;
	
	public String					mId;
	public int						mFromType;
	
	public List<IMMessageProtocol> 	mListReadMessage;
	public int						mReadPosition;
	public int						mReadCount;
	
	public DBMessageParam(int nHandleType){
		mHandleType = nHandleType;
	}
}
