package com.zdht.jingli.groups.event;

public class RecordStopEvent extends CallbackEvent {

	private boolean mIsBeyondMinTime;
	
	private String	mRecordFilePathName;
	
	public RecordStopEvent(int nEventCode) {
		super(nEventCode);
	}

	public boolean 	isBeyondMinTime(){
		return mIsBeyondMinTime;
	}
	
	public String	getRecordFilePathName(){
		return mRecordFilePathName;
	}

	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		mIsBeyondMinTime = (Boolean)params[0];
		mRecordFilePathName = (String)params[1];
	}
}
