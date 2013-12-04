package com.zdht.jingli.groups.event;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.parampool.DBReadMessageCountParam;

public class DBReadMessageCountEvent extends DBMessageEvent {

	private DBReadMessageCountParam mCountParam;
	
	public DBReadMessageCountEvent(int nEventCode) {
		super(nEventCode);
	}

	@Override
	public void run(Object... params) throws Exception {
		mCountParam = (DBReadMessageCountParam)params[0];
		requestExecute(true);
	}

	@Override
	protected void onExecute(SQLiteDatabase db) {
		String strTableName = null;
		if(mCountParam.mFromType == HPMessage.FROMTYPE_CHATROOM){
			strTableName = getRoomTableName(mCountParam.mId);
		}else if(mCountParam.mFromType == HPMessage.FROMTYPE_SINGLE){
			strTableName = getSingleChatTableName(mCountParam.mId);
		}
		if(!TextUtils.isEmpty(strTableName)){
			Cursor cursor = db.query(strTableName, new String[]{"count(*)"}, 
					null, null, null, null, null);
			managerCursor(cursor);
			if(cursor != null && cursor.moveToFirst()){
				mCountParam.mReturnCount = cursor.getInt(0);
			}
		}else{
			throw new IllegalArgumentException("unknow tablename");
		}
	}

}
