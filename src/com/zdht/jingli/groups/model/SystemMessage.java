package com.zdht.jingli.groups.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.zdht.jingli.groups.DBColumns;

import android.content.ContentValues;
import android.database.Cursor;


public class SystemMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int mId;
	private final String mAvatarUrl;
	private final String mName;
	private final String mMessage;

	public SystemMessage(JSONObject jsonObject)
			throws JSONException {
		mId = jsonObject.getInt("uid");
		mAvatarUrl = jsonObject.getString("avatar");
		mName = jsonObject.getString("name");
		mMessage = "请求加为好友";
		setContentValues();
	}
	
	public SystemMessage(Cursor cursor){
		mId = cursor.getInt(cursor.getColumnIndex(DBColumns.SystemMessage.COLUMN_ID));
		mAvatarUrl = cursor.getString(cursor.getColumnIndex(DBColumns.SystemMessage.COLUMN_AVATAR));
		mName = cursor.getString(cursor.getColumnIndex(DBColumns.SystemMessage.COLUMN_NAME));
		mMessage = cursor.getString(cursor.getColumnIndex(DBColumns.SystemMessage.COLUMN_MESSAGE));
	}
	
	

	private void setContentValues() {
		mContentValues.put(DBColumns.SystemMessage.COLUMN_ID, mId);
		mContentValues.put(DBColumns.SystemMessage.COLUMN_AVATAR, mAvatarUrl);
		mContentValues.put(DBColumns.SystemMessage.COLUMN_NAME, mName);
		mContentValues.put(DBColumns.SystemMessage.COLUMN_MESSAGE, mMessage);
	}

	public ContentValues getSaveContentValues() {
		return mContentValues;
	}

	public int getId() {
		return mId;
	}

	public String getAvatarUrl() {
		return mAvatarUrl;
	}

	public String getName() {
		return mName;
	}

	public String getMessage() {
		return mMessage;
	}
	
	protected boolean 		mStoraged;
	protected ContentValues mContentValues = new ContentValues();
	
	public boolean	isStoraged(){
		return mStoraged;
	}
	
	public void 	setStoraged(){
		mContentValues.clear();
		mStoraged = true;
	}

}