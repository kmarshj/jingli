package com.zdht.jingli.groups.event;

import com.zdht.jingli.groups.model.Group;


public class AddGroupEvent extends BaseInfoGetEvent {

	private Group mGroup;
	
	
	@Override
	public Object getReturnParam() {
		return mGroup;
	}

	public AddGroupEvent(int nEventCode, boolean isNeedAddUser) {
		super(nEventCode, isNeedAddUser);
	}

	@Override
	public void run(Object... params) throws Exception {
		mGroup = (Group)params[1];
		super.run(params);
	}
	

}