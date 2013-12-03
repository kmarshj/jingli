package com.zdht.jingli.groups;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zdht.core.DatabaseManager;

public class HPDatabaseManager extends DatabaseManager{
	
	public 	static HPDatabaseManager getInstance(){
		if(sInstance == null){
			sInstance = new HPDatabaseManager();
		}
		return sInstance;
	}
	
	private static HPDatabaseManager sInstance;
	
	public void 	onInit(Context context){
		mDBHelper = new DBHelper(context.getApplicationContext());
	}
	
	public void 	onEnd(){
		mDBHelper.close();
	}
	
	public String 	getDatabaseName(){
		return DBHelper.DB_NAME;
	}

	private static class DBHelper extends SQLiteOpenHelper{
		
		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "School";

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
