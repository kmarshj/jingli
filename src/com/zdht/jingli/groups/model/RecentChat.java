package com.zdht.jingli.groups.model;

import java.util.Locale;

import android.database.Cursor;

import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.event.DBReadLastMessageEvent;
import com.zdht.jingli.groups.parampool.DBReadLastMessageParam;

public class RecentChat {
	
	private final String 	mId;
	
	private final String mUserIdForInfo;
	
	private final int		mFromType;
	
	private String 		mName;
	
	
	private int			mUnreadMessageCount;
	
	private XMessage 	mMessage;
	
	public RecentChat(String strId,int nFromType, String userIdForInfo){
		mId = strId.toUpperCase(Locale.CHINA);
		mFromType = nFromType;
		mUserIdForInfo = userIdForInfo;
	}
	
	public RecentChat(Cursor cursor){
		mId = cursor.getString(cursor.getColumnIndex(DBColumns.RecentChatDB.COLUMN_ID));
		mFromType = cursor.getInt(cursor.getColumnIndex(DBColumns.RecentChatDB.COLUMN_TYPE));
		mName = cursor.getString(cursor.getColumnIndex(DBColumns.RecentChatDB.COLUMN_NAME));
		mUserIdForInfo = cursor.getString(cursor.getColumnIndex(DBColumns.RecentChatDB.COLUMN_USERIDFORINFO));  
		mUnreadMessageCount = cursor.getInt(cursor.getColumnIndex(DBColumns.RecentChatDB.COLUMN_UNREADCOUNT));
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o){
			return true;
		}
		
		if(o != null && o instanceof RecentChat){
			return getId().equals(((RecentChat)o).getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mId.hashCode();
	}

	public String 	getId(){
		return mId;
	}
	
	public void idToUpperCase() {
		mId.toUpperCase(Locale.CHINA);
	}

	public String 	getUserIdForInfo(){
		return mUserIdForInfo;
	}
	
	public int		getFromType(){
		return mFromType;
	}
	
	public String 	getName(){
		/*if(ConstantID.ID_FriendVerify.equals(mId)){
			return HPApplication.getApplication().getString(R.string.inquiry_friend_verify_notice);
		}*/
		return mName;
	}
	

	public int		getUnreadMessageCount(){
		return mUnreadMessageCount;
	}
	
	public XMessage getLastMessage(){
		if(mMessage == null){
			DBReadLastMessageParam param = new DBReadLastMessageParam();
			param.mFromType = getFromType();
			param.mId = getId();
			param.mSetMessage = true;
			
			AndroidEventManager.getInstance().runEvent(new DBReadLastMessageEvent(),
					param);
			
			//mMessage = param.mMessageOut;
		}
		return mMessage;
	}
	
	public void setName(String strName){
		mName = strName;
	}
	
	public void setUnreadMessageCount(int nCount){
		mUnreadMessageCount = nCount;
	}
	
	public void addUnreadMessageCount(){
		++mUnreadMessageCount;
	}
	
	public void setLastMessage(XMessage message){
		mMessage = message;
	}
}
