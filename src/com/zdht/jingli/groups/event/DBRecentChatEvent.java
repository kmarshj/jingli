package com.zdht.jingli.groups.event;

import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.model.RecentChat;
import com.zdht.jingli.groups.parampool.DBHandleType;

public class DBRecentChatEvent extends DBEvent {

	private RecentChat 			mSave;
	
	private List<RecentChat> 	mListRead;
	
	private boolean				mIsDelete;
	private String				mDeleteId;
	
	public DBRecentChatEvent(int nCode) {
		super(nCode);
	}

	@Override
	protected boolean useUserDatabase() {
		return true;
	}

	@Override
	public String createTableSql() {
		return "CREATE TABLE " + DBColumns.RecentChatDB.TABLENAME + " (" +
				DBColumns.RecentChatDB.COLUMN_ID + " TEXT PRIMARY KEY, " +
				DBColumns.RecentChatDB.COLUMN_USERIDFORINFO + " TEXT, " +
				DBColumns.RecentChatDB.COLUMN_TYPE + " INTEGER, " +
				DBColumns.RecentChatDB.COLUMN_NAME + " TEXT, " +
				DBColumns.RecentChatDB.COLUMN_UNREADCOUNT + " INTEGER, " +
				DBColumns.RecentChatDB.COLUMN_UPDATETIME + " INTEGER);";
	}

	@Override
	protected void onExecute(SQLiteDatabase db) {
		if(mIsRead){
			Cursor cursor = db.query(DBColumns.RecentChatDB.TABLENAME,
					null, null, null, null, null, 
					DBColumns.RecentChatDB.COLUMN_UPDATETIME + " DESC");
			managerCursor(cursor);
			if(cursor != null && cursor.moveToFirst()){
				do{
					mListRead.add(new RecentChat(cursor));
				}while(cursor.moveToNext());
			}
		}else{
			if(mIsDelete){
				db.delete(DBColumns.RecentChatDB.TABLENAME,
						DBColumns.RecentChatDB.COLUMN_ID + "='" + mDeleteId + "'", null);
			} else {
				ContentValues cv = new ContentValues();
				cv.put(DBColumns.RecentChatDB.COLUMN_NAME, mSave.getName());
				cv.put(DBColumns.RecentChatDB.COLUMN_UNREADCOUNT, mSave.getUnreadMessageCount());
				cv.put(DBColumns.RecentChatDB.COLUMN_UPDATETIME, System.currentTimeMillis());
				try {
					int nRet = db.update(DBColumns.RecentChatDB.TABLENAME,
							cv, 
							DBColumns.RecentChatDB.COLUMN_ID + "='" + mSave.getId() + "'", null);
					if (nRet <= 0) {
						cv.put(DBColumns.RecentChatDB.COLUMN_ID, mSave.getId());
						cv.put(DBColumns.RecentChatDB.COLUMN_TYPE, mSave.getFromType());
						cv.put(DBColumns.RecentChatDB.COLUMN_USERIDFORINFO, mSave.getUserIdForInfo());
						safeInsert(db, DBColumns.RecentChatDB.TABLENAME, cv);
					}
				} catch (Exception e) {
//					e.printStackTrace();
					if (!tabbleIsExist(DBColumns.RecentChatDB.TABLENAME, db)) {
						cv.put(DBColumns.RecentChatDB.COLUMN_ID, mSave.getId());
						cv.put(DBColumns.RecentChatDB.COLUMN_TYPE, mSave.getFromType());
						cv.put(DBColumns.RecentChatDB.COLUMN_USERIDFORINFO, mSave.getUserIdForInfo());
						db.execSQL(createTableSql());
						db.insert(DBColumns.RecentChatDB.TABLENAME, null, cv);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Object... params) throws Exception {
		Integer handleType = (Integer)params[0];
		mIsDelete = false;
		if(handleType.intValue() == DBHandleType.WRITE){
			mSave = (RecentChat)params[1];
			requestExecute(false);
		}else if(handleType.intValue() == DBHandleType.DELETE){
			mDeleteId = (String)params[1];
			mIsDelete = true;
			requestExecute(false);
		}else{
			mListRead = (List<RecentChat>)params[1];
			requestExecute(true);
		}
	}

}
