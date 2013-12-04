package com.zdht.jingli.groups.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.parampool.DBHandleType;

public class DBAddressListFriendEvent extends DBEvent {
	
	private String mSign;
	private String mJson;

	public DBAddressListFriendEvent(int nEventCode) {
		super(nEventCode);
	}
	
	public String getSign(){
		return mSign;
	}
	
	public String getJsonData(){
		return mJson;
	}
	
	@Override
	public String createTableSql() {
		return "CREATE TABLE " + DBColumns.AddressListFriend.TABLENAME + " (" +
				DBColumns.AddressListFriend.COLUMN_SIGN + " TEXT, " +
				DBColumns.AddressListFriend.COLUMN_JSON + " TEXT);";
	}

	@Override
	public void run(Object... params) throws Exception {
		final Integer type = (Integer)params[0];
		if(DBHandleType.WRITE == type.intValue()){
			mSign = (String)params[1];
			mJson = (String)params[2];
			requestExecute(false);
		}else{
			requestExecute(true);
		}
	}

	@Override
	public void onExecute(SQLiteDatabase db) {
		if(mIsRead){
			Cursor cursor = db.query(DBColumns.AddressListFriend.TABLENAME, null, null, null, null, null, null);
			managerCursor(cursor);
			if(cursor != null && cursor.moveToFirst()){
				do{
					mSign = cursor.getString(0);
					mJson = cursor.getString(1);
				}while(cursor.moveToNext());
			}
		}else{
			try{
				db.delete(DBColumns.AddressListFriend.TABLENAME, null, null);
			}catch(Exception e){
				e.printStackTrace();
				if(!tabbleIsExist(DBColumns.AddressListFriend.TABLENAME, db)){
					db.execSQL(createTableSql());
				}
			}
			ContentValues cv = new ContentValues();
			cv.put(DBColumns.AddressListFriend.COLUMN_SIGN, mSign);
			cv.put(DBColumns.AddressListFriend.COLUMN_JSON, mJson);
			safeInsert(db, DBColumns.AddressListFriend.TABLENAME, cv);
		}
	}
	
	@Override
	protected boolean useUserDatabase() {
		return true;
	}
}
