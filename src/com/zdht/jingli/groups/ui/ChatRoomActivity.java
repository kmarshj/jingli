package com.zdht.jingli.groups.ui;

import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.event.DBReadMessageCountEvent;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.parampool.DBReadMessageCountParam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



public class ChatRoomActivity extends ChatActivity{
	
	
	private static final String EXTRA_GROUPID 		= "groupid";
	private static final String EXTRA_GROUPNAME 	= "groupname";
	
	private String mGroupId;
	private String mGroupName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		mGroupId = getIntent().getStringExtra(EXTRA_GROUPID);
		mGroupName = getIntent().getStringExtra(EXTRA_GROUPNAME);
		super.onCreate(savedInstanceState);
//		if(!SCApplication.isIMConnectionSuccess()) {
//			ToastManager.getInstance(getApplicationContext()).show("聊天服务器未连接");
//			finish();
//			return;
//		}
	}
	
	@Override
	protected void onInitChatAttribute(ChatAttribute attr) {
		super.onInitChatAttribute(attr);
		attr.mFromId = mGroupId;
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleText = mGroupName == null ? "" : mGroupName;
	}
	
	@Override
	protected void onInit() {
		super.onInit();
		
		DBReadMessageCountParam param = new DBReadMessageCountParam(HPMessage.FROMTYPE_CHATROOM, mGroupId);
		AndroidEventManager.getInstance().runEvent(new DBReadMessageCountEvent(0), param);
		mLastReadPosition = param.mReturnCount - 1;
		
		loadOnePage();
		
		mListView.setSelection(mMessageAdapter.getCount() - 1);
		
		
	}
	
	public static void launch(Activity activity,String strId,String strName){
		Intent intent = new Intent(activity, ChatRoomActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(EXTRA_GROUPID, strId);
		intent.putExtra(EXTRA_GROUPNAME, strName);
		activity.startActivity(intent);
	}
	
	@Override
	protected void onInitMessage(HPMessage m) {
		super.onInitMessage(m);
		m.setFromType(HPMessage.FROMTYPE_CHATROOM);
		m.setGroupId(mGroupId);
	}
	
	
}
