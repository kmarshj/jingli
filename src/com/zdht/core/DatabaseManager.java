package com.zdht.core;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DatabaseManager {
	
	protected SQLiteOpenHelper 			mDBHelper;
	
	protected ReentrantReadWriteLock 	mReadWriteLock;
	
	public DatabaseManager() {
		mReadWriteLock = new ReentrantReadWriteLock();
	}
	
	public SQLiteDatabase 	lockWritableDatabase(){
		mReadWriteLock.writeLock().lock();
		return mDBHelper.getWritableDatabase();
	}
	
	public void	unlockWritableDatabase(SQLiteDatabase db){
		mReadWriteLock.writeLock().unlock();
	}
	
	public SQLiteDatabase 	lockReadableDatabase(){
		mReadWriteLock.readLock().lock();
		return mDBHelper.getReadableDatabase();
	}
	
	public void	unlockReadableDatabase(SQLiteDatabase db){
		mReadWriteLock.readLock().unlock();
	}
}
