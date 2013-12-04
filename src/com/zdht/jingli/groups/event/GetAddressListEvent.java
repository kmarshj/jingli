package com.zdht.jingli.groups.event;

import org.json.JSONObject;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.model.AddressList;

public class GetAddressListEvent extends HttpGetEvent implements OnEventListener {

	private String mDescribe;
	private String mSign;
	private AddressList mAddressList = new AddressList();

	/*private boolean mUpdatedFriend;
	private boolean mUpdatedClassFriend;
	private boolean mUpdatedGroup;*/
	private boolean isNotGroup;
	private boolean isNotFriend;
	
	public boolean isNotGroup() {
		return isNotGroup;
	}
	
	public boolean isNotFriend() {
		return isNotFriend;
	}

	public GetAddressListEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
		setIsWaitRunWhenRunning(true);
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_LoginActivityCreated, this, false);
	}

	public String getmDescribe() {
		return mDescribe;
	}

	public String getmSign() {
		return mSign;
	}

	@Override
	public Object getReturnParam() {
		return mAddressList;
	}

	@Override
	public void onRunEnd() {
		super.onRunEnd();
	}
	
	private int type;
	
	public int getType() {
		return type;
	}
	
	@Override
	public void run(Object... params) throws Exception {

		type = (Integer) params[0];
		String strUrl = String.format(URLUtils.URL_GetAddressList, 0, type);

		super.run(strUrl);
		if(isNetSuccess()){
			JSONObject jsonObject = new JSONObject(getHttpRetString());
			mDescribe = jsonObject.getString("describe");
			if (isRequestSuccess()) {
				mAddressList.setParam(getHttpRetString(), type);
			}
		}
		/*if (type == 1) {
			//SCApplication.print( "查看好友");
			if (mAddressList.getListFriend().size() == 0) {
				DBAddressListFriendEvent dbEvent = (DBAddressListFriendEvent) AndroidEventManager
						.getInstance().runEvent(EventCode.DB_FRIEND,
								DBHandleType.READ);
				String strSign = "0";
				if(dbEvent != null){
					final String strJson = dbEvent.getJsonData();
					strSign = dbEvent.getSign();

					if (strJson != null) {
						mAddressList.setParam(strJson, 1);
						isNotFriend = false;
					} 
				}else {
					mUpdatedFriend = false;
				}
				
				if (!mUpdatedFriend) {
			String strUrl = String.format(URLUtils.URL_GetAddressList, 0, type);

			super.run(strUrl);

			if (isRequestSuccess()) {
				mAddressList.setParam(getHttpRetString(), 1);

				AndroidEventManager.getInstance().runEvent(
						EventCode.DB_FRIEND, DBHandleType.WRITE,
						mAddressList.getVersionFriend(),
						getHttpRetString());
				isNotFriend = false;
				mUpdatedFriend = true;
			}else {
				JSONObject jsonObject = new JSONObject(getHttpRetString());
				if(jsonObject.getInt("status") == 2){
					isNotFriend = true;
				}
			}*/
	}

	@Override
	public void onEventRunEnd(Event event) {
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HP_LoginActivityCreated){
			mAddressList.clear();
			mAddressList = new AddressList();
		}
	}
}
