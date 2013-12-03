package com.zdht.jingli.groups;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zdht.core.DatabaseManager;

public class HPUserDatabaseManager extends DatabaseManager{
	
	public static HPUserDatabaseManager getInstance(){
		if(sInstance == null){
			sInstance = new HPUserDatabaseManager();
		}
		return sInstance;
	}
	
	private static HPUserDatabaseManager sInstance;
	
	private HPUserDatabaseManager(){	
	}
	
	public void onInit(Context context,String strIMUserId){
		mDBHelper = new DBUserHelper(context, strIMUserId);
	}
	
	public void onDestory(){
		if(mDBHelper != null){
			mDBHelper.close();
		}
	}
	
	private static class DBUserHelper extends SQLiteOpenHelper{

		private static final int DB_VERSION = 1;
		
		public DBUserHelper(Context context, String name) {
			super(context, name, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
