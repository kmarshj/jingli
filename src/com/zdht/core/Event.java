package com.zdht.core;

public abstract class Event {

protected final int mEventCode;
	
	protected boolean mIsNotifyAfterRun 	= false;
	/** 是否异步 */
	protected boolean mIsAsyncRun			= false;
	
	private   boolean mIsWaitRunWhenRunning	= false;
	
	private Object	mTag;
	
	public Event(int nEventCode){
		mEventCode = nEventCode;
	}

	public int 		getEventCode(){
		return mEventCode;
	}
	
	public void		setIsNotifyAfterRun(boolean bNotify){
		mIsNotifyAfterRun = bNotify;
	}
	
	public boolean 	isNotifyAfterRun(){
		return mIsNotifyAfterRun;
	}
	
	public void		setIsAsyncRun(boolean bAsync){
		mIsAsyncRun = bAsync;
	}
	
	public boolean  isAsyncRun(){
		return mIsAsyncRun;
	}
	
	public void 	setIsWaitRunWhenRunning(boolean bWait){
		mIsWaitRunWhenRunning = bWait;
	}
	
	public boolean	isWaitRunWhenRunning(){
		return mIsWaitRunWhenRunning;
	}
	
	public void 	setTag(Object tag){
		mTag = tag;
	}
	
	public Object 	getTag(){
		return mTag;
	}
	
	public Object 	getReturnParam(){
		return null;
	}
	
	public void 	onPreRun(){
	}
	
	public void 	onRunEnd(){
	}
	
	public abstract void run(Object ... params) throws Exception;
	
	
}
