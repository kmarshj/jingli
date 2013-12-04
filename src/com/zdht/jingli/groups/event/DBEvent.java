package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zdht.core.DatabaseManager;
import com.zdht.core.Event;
import com.zdht.jingli.groups.DBTableCreateable;
import com.zdht.jingli.groups.HPDatabaseManager;
import com.zdht.jingli.groups.HPUserDatabaseManager;

public abstract class DBEvent extends Event implements DBTableCreateable{

	protected List<Cursor> mListCursor;
	
	protected boolean mIsRead = true;
	
	public DBEvent(int nEventCode) {
		super(nEventCode);
	}
	
	protected void	managerCursor(Cursor cursor){
		if(cursor == null){
			return;
		}
		
		if(mListCursor == null){
			mListCursor = new ArrayList<Cursor>();
		}
		mListCursor.add(cursor);
	}
	
	/**
	 * 
	 * @param bRead
	 */
	protected void	requestExecute(boolean bRead){
		mIsRead = bRead;
		DatabaseManager dm = useUserDatabase() ? HPUserDatabaseManager.getInstance() :
								HPDatabaseManager.getInstance();
		if(bRead){
			SQLiteDatabase db = dm.lockReadableDatabase();
			try{
				onExecute(db);
			}finally{
				dm.unlockReadableDatabase(db);
				closeCursor();
			}
		}else{
			SQLiteDatabase db = dm.lockWritableDatabase();
			try{
				onExecute(db);
			}finally{
				dm.unlockWritableDatabase(db);
				closeCursor();
			}
		}
	}
	
	protected void	safeInsert(SQLiteDatabase db,String strTableName,ContentValues cv){
		long lRet = db.insert(strTableName, null, cv);
		if(lRet == -1){
			if(!tabbleIsExist(strTableName, db)){
				db.execSQL(createTableSql());
				db.insert(strTableName, null, cv);
			}
		}
	}
	
	protected boolean tabbleIsExist(String tableName,SQLiteDatabase db) {
		boolean result = false;
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return result;
	}
	
	protected void	closeCursor(){
		if(mListCursor != null){
			for(Cursor cursor : mListCursor){
				cursor.close();
			}
			mListCursor.clear();
		}
	}
	
	
	protected boolean useUserDatabase(){
		return false;
	}
	
	protected abstract void 	onExecute(SQLiteDatabase db);
}
