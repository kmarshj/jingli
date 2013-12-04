package com.zdht.jingli.groups.event;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.parampool.DBReadLastMessageParam;

public class DBReadLastMessageEvent extends DBMessageEvent {

	private DBReadLastMessageParam mParam;
	
	public DBReadLastMessageEvent() {
		super(EventCode.ZERO_CODE);
		mIsAsyncRun = false;
	}

	@Override
	public String createTableSql() {
		return null;
	}

	@Override
	protected void onExecute(SQLiteDatabase db) {
		if(mParam.mFromType == HPMessage.FROMTYPE_CHATROOM){
			doQuery(db, getRoomTableName(mParam.mId));
		}else if(mParam.mFromType == HPMessage.FROMTYPE_SINGLE){
			doQuery(db, getSingleChatTableName(mParam.mId));
		}else{
//			SCApplication.getLogger().warning("unknown FromType");
		}
	}
	
	protected void doQuery(SQLiteDatabase db,String strTableName){
		mParam.mHasValue = false;
		Cursor cursor = db.query(strTableName,
				mParam.mColumnNames,
				null, null, null, null, DBColumns.Message.COLUMN_AUTOID + " DESC",
				"0,1");
		managerCursor(cursor);
		if(cursor != null && cursor.moveToFirst()){
			mParam.mHasValue = true;
			if(mParam.mSetMessage && mParam.mColumnNames == null){
			//	mParam.mMessageOut = new HPMessage(cursor);
			}else{
				final int nCount = cursor.getColumnCount();
				for(int nIndex = 0;nIndex < nCount;++nIndex){
					mParam.mMapColumnNameToValue.put(cursor.getColumnName(nIndex),
							cursor.getString(nIndex));
				}
			}
		}
	}

	@Override
	public void run(Object... params) throws Exception {
		mParam = (DBReadLastMessageParam)params[0];
		requestExecute(true);
	}

}
