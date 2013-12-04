package com.zdht.jingli.groups.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.model.SystemMessage;
import com.zdht.jingli.groups.parampool.DBHandleType;
import com.zdht.jingli.groups.parampool.DBSystemMessageParam;

public class DBSystemMessageEvent extends DBEvent {
	
	private DBSystemMessageParam mParam;
	
	public DBSystemMessageEvent(int nEventCode) {
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
		mParam = (DBSystemMessageParam)params[0];
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
		if(mIsRead){
			
			Cursor cursor = null;
			cursor = db.query(getTableName(), null, null, null, null, null,
					DBColumns.Message.COLUMN_AUTOID + " ASC", 
					null);
			managerCursor(cursor);
			if (cursor != null && cursor.moveToFirst()) {
				do{
					SystemMessage m = new SystemMessage(cursor);
					mParam.mListReadMessage.add(m);
				}while(cursor.moveToNext());
			}
		}else{
			if(mParam.mHandleType == DBHandleType.DELETE){
				db.delete(getTableName(),null,null);
			} else {
				final SystemMessage m = mParam.mSaveMessage;
				final String strTableName = getTableName();
				if (!TextUtils.isEmpty(strTableName)) {
					if (m.isStoraged()) {
						ContentValues cv = m.getSaveContentValues();
						if (cv.size() > 0) {
							int nRet = db.update(strTableName, 
									cv,
									DBColumns.Message.COLUMN_ID + "='" + m.getId() + "'", null);
							if (nRet <= 0) {
//								SCApplication.getLogger().warning("update fail");
							}
						}
					} else {
						final ContentValues cv = m.getSaveContentValues();
						long lRet = db.insert(strTableName, null, cv);
						if (lRet == -1) {
							if (!tabbleIsExist(strTableName, db)) {
								db.execSQL(createTableSql(strTableName));
								db.insert(strTableName, null, cv);
							}
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
				DBColumns.SystemMessage.COLUMN_ID + " INTEGER, " +
				DBColumns.SystemMessage.COLUMN_NAME + " TEXT, " + 
				DBColumns.SystemMessage.COLUMN_AVATAR + " TEXT, " +
				DBColumns.SystemMessage.COLUMN_MESSAGE + " TEXT);";
	}
	
	protected String getTableName(){
		return "system_message";
	}
	
}
