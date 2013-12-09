package com.zdht.jingli.groups.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zdht.core.Event;
import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.RecentChatManager;
import com.zdht.jingli.groups.event.DBMessageEvent;
import com.zdht.jingli.groups.event.DBReadMessageCountEvent;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.parampool.DBHandleType;
import com.zdht.jingli.groups.parampool.DBMessageParam;
import com.zdht.jingli.groups.parampool.DBReadMessageCountParam;


public class SingleChatActivity extends ChatActivity {
	private static final String EXTRA_USERIDFORINFO = "useridforinfo";
	private static final String EXTRA_USERID 		= "userid";
	private static final String EXTRA_USERNAME 	    = "username";
	private static final String EXTRA_AVATAR 	    = "avatar";
	
	private String mUserId;
	
	private String mUserIdForInfo;
	
	private String mUserName;
	
	private String mAvatar;
	
	private int	mInitReadCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mUserIdForInfo = getIntent().getStringExtra(EXTRA_USERIDFORINFO);
		mUserId = getIntent().getStringExtra(EXTRA_USERID);
		mUserName = getIntent().getStringExtra(EXTRA_USERNAME);
		mAvatar = getIntent().getStringExtra(EXTRA_AVATAR);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RecentChatManager.getInstance().clearUnreadMessageCount(
				RecentChatManager.getInstance().getRecentChat(String.valueOf(mUserId)));
	}
	
	@Override
	protected void onInit() {
		super.onInit();
		initLoad();
	}

	protected void initLoad(){
		final int nUnreadMessageCount = RecentChatManager.getInstance()
				.getUnreadMessageCount(String.valueOf(mUserId));
		
		DBReadMessageCountParam param = new DBReadMessageCountParam(HPMessage.FROMTYPE_SINGLE, String.valueOf(mUserId));
		AndroidEventManager.getInstance().runEvent(new DBReadMessageCountEvent(0), param);
		mLastReadPosition = param.mReturnCount - 1;

		mInitReadCount = nUnreadMessageCount;
		if(mInitReadCount == 0){
			mInitReadCount = 15;
		}
		
		loadOnePage();
		
		mListView.setSelection(mMessageAdapter.getCount() - 1);
	}
	
	@Override
	protected void onLoadOnePageMessage(List<IMMessageProtocol> listMessage, int nPosition) {
		super.onLoadOnePageMessage(listMessage, nPosition);
		
		DBMessageParam param = new DBMessageParam(DBHandleType.READ);
		param.mListReadMessage = listMessage;
		if(mInitReadCount == 0){
			param.mReadCount = 15;
		}else{
			param.mReadCount = mInitReadCount > 15 ? mInitReadCount : 15;
		}
		param.mFromType = HPMessage.FROMTYPE_SINGLE;
		param.mId = String.valueOf(mUserId);
		param.mReadPosition = nPosition;
		AndroidEventManager.getInstance().runEvent(
				new DBMessageEvent(EventCode.DB_MessageHandle),
				param);
		
		if(mInitReadCount != 0){
			for(IMMessageProtocol m : listMessage){
				if(m.getType() == HPMessage.TYPE_VOICE){
					//mPlayProcessor.addMessage((HPMessage)m);
				}
			}
			mInitReadCount = 0;
		}
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleText = mUserName;
	}
	
	@Override
	protected void onInitChatAttribute(ChatAttribute attr) {
		super.onInitChatAttribute(attr);
		attr.mFromId = String.valueOf(mUserId);
	}

	/**
	 * 跳转到单聊页面
	 * @param activity 跳转源
	 * @param id	用户id	
	 * @param name	用户名称
	 * @param avatar	用户头像
	 * @param userIdForInfo 用于查询用户资料的id
	 */
	public static void launch(Activity activity, String userId, String name, String avatar, String userIdForInfo){
		Intent intent = new Intent(activity, SingleChatActivity.class);
		intent.putExtra(EXTRA_USERIDFORINFO, userIdForInfo);
		intent.putExtra(EXTRA_USERID, userId);
		intent.putExtra(EXTRA_USERNAME, name);
		intent.putExtra(EXTRA_AVATAR, avatar);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}
	
	/*private void sendMessage(int subject, String message){
		final List<NameValuePair> mListNameValuePair = new ArrayList<NameValuePair>();
		mListNameValuePair.add(new BasicNameValuePair("key", URLUtils.KEY));
		mListNameValuePair.add(new BasicNameValuePair("tuid", String.valueOf(mUserId)));
		mListNameValuePair.add(new BasicNameValuePair("subject", String.valueOf(subject)));
		mListNameValuePair.add(new BasicNameValuePair("message", message));
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_SendMessage,
				0, URLUtils.URL_SendMessage, mListNameValuePair);
	}*/
	
	@Override
	protected void onInitMessage(HPMessage m) {
		super.onInitMessage(m);
		m.setFromType(HPMessage.FROMTYPE_SINGLE);
		m.setUserIdForInfo(mUserIdForInfo);
		m.setUserId(String.valueOf(mUserId));
		m.setUserName(mUserName);
		m.setAvatar(mAvatar);
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		//final int nCode = event.getEventCode();
		//SCApplication.print( "singleChat-onEventRunEnd:" + event.getEventCode());
	}
	
}
