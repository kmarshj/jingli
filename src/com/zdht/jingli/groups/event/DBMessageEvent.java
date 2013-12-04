package com.zdht.jingli.groups.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.parampool.DBHandleType;
import com.zdht.jingli.groups.parampool.DBMessageParam;

public class DBMessageEvent extends DBEvent {
	
	private DBMessageParam mParam;
	
	public DBMessageEvent(int nEventCode) {
		super(nEventCode);
	}

	@Override
	public String createTableSql() {
		return null;
	}

	@Override
	protected boolean useUserDatabase() {
		return true;
	}

	@Override
	public void run(Object... params) throws Exception {
		mParam = (DBMessageParam)params[0];
		final int nType = mParam.mHandleType;
		if(nType == DBHandleType.WRITE){
			requestExecute(false);
		}else if(nType == DBHandleType.DELETE){
			requestExecute(false);
		}else{
			requestExecute(true);
		}
	}
	
	@Override
	protected void onExecute(SQLiteDatabase db) {
		if(mIsRead){// 读操作
			int nReadPosition = mParam.mReadPosition;
			int nReadCount = mParam.mReadCount;
			int nStartPosition = nReadPosition - nReadCount + 1;
			if(nStartPosition < 0){
				nReadCount = nReadPosition + 1;
				nStartPosition = 0;
			}
			
			Cursor cursor = null;
			if(mParam.mFromType == HPMessage.FROMTYPE_SINGLE){// 单聊
				cursor = db.query(getSingleChatTableName(mParam.mId), null, null, null, null, null,
						DBColumns.Message.COLUMN_AUTOID + " ASC", 
						nStartPosition + "," + nReadCount);
				managerCursor(cursor);
				if (cursor != null && cursor.moveToFirst()) {
					do{
						HPMessage m = new HPMessage(cursor);
						m.setFromType(HPMessage.FROMTYPE_SINGLE);
						mParam.mListReadMessage.add(m);
					}while(cursor.moveToNext());
				}
			}else if(mParam.mFromType == HPMessage.FROMTYPE_CHATROOM){// 群聊
				cursor = db.query(getRoomTableName(mParam.mId), null, null, null, null, null,
						DBColumns.Message.COLUMN_AUTOID + " ASC", 
						nStartPosition + "," + nReadCount);
				managerCursor(cursor);
				if (cursor != null && cursor.moveToFirst()) {
					do{
						HPMessage m = new HPMessage(cursor);
						m.setFromType(HPMessage.FROMTYPE_CHATROOM);
						m.setGroupId(mParam.mId);
						mParam.mListReadMessage.add(m);
					}while(cursor.moveToNext());
				}
			}
		}else{// 写操作
			if(mParam.mHandleType == DBHandleType.DELETE){// 删除
				if(mParam.mFromType == XMessage.FROMTYPE_SINGLE){
					db.delete(getSingleChatTableName(mParam.mId),null,null);
				}else if(mParam.mFromType == XMessage.FROMTYPE_CHATROOM){
					db.delete(getRoomTableName(mParam.mId), null, null);
				}
			} else {// 保存
				final HPMessage m = mParam.mSaveMessage;
				final String strTableName = getTableName(m);
				if (!TextUtils.isEmpty(strTableName)) {
					if (m.isStoraged()) {// 更新
						ContentValues cv = m.getSaveContentValues();
						if (cv.size() > 0) {
							int nRet = db.update(strTableName, 
									cv,
									DBColumns.Message.COLUMN_ID + "='" + m.getId() + "'", null);
							if (nRet <= 0) {
//								SCApplication.getLogger().warning("update fail");
							}
						}
					} else {// 插入
						final ContentValues cv = m.getSaveContentValues();
						try {
							db.execSQL(createTableSql(strTableName));
						} catch (SQLiteException e) {
							
						}

						long lRet = db.insert(strTableName, null, cv);
						if (lRet == -1) {
							db.insert(strTableName, null, cv);
						}
					}
					m.setStoraged();
				}
			}
		}
	}
	
	private String createTableSql(String strTableName){
		return "CREATE TABLE " + strTableName + " (" + 
				DBColumns.Message.COLUMN_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				DBColumns.Message.COLUMN_ID + " TEXT, " +
				DBColumns.Message.COLUMN_TYPE + " INTEGER, " + 
				DBColumns.Message.COLUMN_USERID + " TEXT, " +
				DBColumns.Message.COLUMN_USERIDFORINFO + " TEXT, " +
				DBColumns.Message.COLUMN_USERNAME + " TEXT, " +
				DBColumns.Message.COLUMN_AVATAR + " TEXT, " +
				DBColumns.Message.COLUMN_CONTENT + " TEXT, " +
				DBColumns.Message.COLUMN_FROMSELF + " INTEGER, " +
				DBColumns.Message.COLUMN_SENDTIME + " INTEGER, " +
				DBColumns.Message.COLUMN_GROUPTIME + " INTEGER, " +
				DBColumns.Message.COLUMN_EXTENSION + " INTEGER, " +
				DBColumns.Message.COLUMN_TAG + " TEXT);";
	}
	
	protected String getTableName(HPMessage m){
		final int nFromType = m.getFromType();
		if(nFromType == HPMessage.FROMTYPE_CHATROOM){
			return getRoomTableName(m.getGroupId());
		}else if(nFromType == HPMessage.FROMTYPE_SINGLE){
			return getSingleChatTableName(m.getUserId());
		}
		return null;
	}
	
	protected String getSingleChatTableName(String strUserId){
		return "single" + strUserId;
	}
	
	protected String getRoomTableName(String strRoomId){
		return "room" + strRoomId;
	}
}
