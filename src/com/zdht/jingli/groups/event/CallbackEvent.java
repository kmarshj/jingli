package com.zdht.jingli.groups.event;

import com.zdht.core.Event;


public class CallbackEvent extends Event {

	private Object 	mReturnParam;
	
	private boolean mInitParam = false;
	
	public CallbackEvent(int nEventCode) {
		super(nEventCode);
		mIsAsyncRun = false;
		mIsNotifyAfterRun = true;
	}
	
	public CallbackEvent(int nEventCode,Object returnParam){
		this(nEventCode);
		mReturnParam = returnParam;
		mInitParam = true;
	}
	
	@Override
	public Object getReturnParam() {
		return mReturnParam;
	}

	@Override
	public void run(Object... params) throws Exception {
		if(params != null && params.length > 0){
			if(mInitParam){
				if(params[0] != null){
					mReturnParam = params[0];
				}
			}else{
				mReturnParam = params[0];
			}
		}else{
			if(!mInitParam){
				mReturnParam = null;
			}
		}
	}

}
