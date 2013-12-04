package com.zdht.jingli.groups.event;

import com.zdht.jingli.groups.model.Activity;


public class QuitAndAddActivityEvent extends BaseInfoGetEvent {

	private Activity mActivity;
	
	
	@Override
	public Object getReturnParam() {
		return mActivity;
	}

	public QuitAndAddActivityEvent(int nEventCode, boolean isNeedAddUser) {
		super(nEventCode, isNeedAddUser);
	}

	@Override
	public void run(Object... params) throws Exception {
		mActivity = (Activity)params[1];
		super.run(params[0]);
	}
	

}