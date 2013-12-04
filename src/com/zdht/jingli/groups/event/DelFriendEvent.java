package com.zdht.jingli.groups.event;

import com.zdht.jingli.groups.model.User;


public class DelFriendEvent extends BaseInfoGetEvent {
	private User mUser;
	
	public DelFriendEvent(int nEventCode, boolean isNeedAddUser) {
		super(nEventCode, isNeedAddUser);
	}

	@Override
	public void run(Object... params) throws Exception {
		// TODO Auto-generated method stub
		mUser = (User)params[1];
		super.run(params[0]);
	}

	@Override
	public Object getReturnParam() {
		return mUser;
	}
	
}
