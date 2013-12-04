package com.zdht.jingli.groups;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.groups.event.DBRecentChatEvent;
import com.zdht.jingli.groups.model.RecentChat;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.parampool.DBHandleType;

public class RecentChatManager implements OnEventListener{
	
//	public static final void print(String str) {
//		Log.i("chat", "" + str);
//	}

	public static RecentChatManager getInstance(){
		if(sInstance == null){
			sInstance = new RecentChatManager();
		}
		return sInstance;
	}
	
	private static RecentChatManager sInstance;
	
	private InternalHandler mHandler;
	private ParamRunnable	mRunnable;
	
	private List<RecentChat> 	mListRecentChat 		= Collections.synchronizedList(new LinkedList<RecentChat>());
	private Map<String, RecentChat> mMapIdToRecentChat 	= new ConcurrentHashMap<String, RecentChat>();
	
	private Map<String, RecentChat> mMapIdToHasUnreadSingleRecentChat = new ConcurrentHashMap<String, RecentChat>();
	private int		mSingleUnreadMessageTotalCount = 0;
	
	private RecentChatManager(){
		Log.i("chat", "new RecentChatManager()");
		AndroidEventManager.getInstance().addEventListener(EventCode.IM_ReceiveMessage, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.IM_SendMessageStart, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_LoginActivityCreated, this, true);
	}
	
	public void onInit(){
		HandlerThread handleThread = new HandlerThread("processRecentChat");
		handleThread.start();
		mHandler = new InternalHandler(handleThread.getLooper());
		
		mRunnable = new ParamRunnable();
		mListRecentChat.clear();
		mMapIdToRecentChat.clear();
		mMapIdToHasUnreadSingleRecentChat.clear();
		mSingleUnreadMessageTotalCount=0;
//		print("onInit--recentChat:" + mListRecentChat.size());
		AndroidEventManager.getInstance().runEvent(EventCode.DB_RecentChat, 
				DBHandleType.READ,mListRecentChat);
		for(RecentChat recentChat : mListRecentChat){
			mMapIdToRecentChat.put(recentChat.getId(), recentChat);
			if(recentChat.getFromType() == XMessage.FROMTYPE_SINGLE
					&& recentChat.getUnreadMessageCount() > 0){
				mMapIdToHasUnreadSingleRecentChat.put(recentChat.getId(), recentChat);
				mSingleUnreadMessageTotalCount += recentChat.getUnreadMessageCount();
			}
		}
	}
	
	public void onDestroy(){
		try {
			mHandler.getLooper().quit();
			mListRecentChat.clear();
			mMapIdToHasUnreadSingleRecentChat.clear();
			mMapIdToRecentChat.clear();
			mSingleUnreadMessageTotalCount = 0;
			AndroidEventManager.getInstance().removeEventListener(EventCode.IM_ReceiveMessage, this);
			sInstance = null;
		} catch (NullPointerException e) {
			Log.i(SCApplication.TAG, "" + e.getMessage());
		}
	}
	
	/***
	 * 新消息
	 * @param message
	 */
	protected void onNewMessage(XMessage message){
//		RecentChatManager.print( "onNewMessage");
		final int nFromType = message.getFromType();
		// 聊天对象id
		final String strId = nFromType == XMessage.FROMTYPE_CHATROOM ? 
				message.getGroupId() : message.getFromId();
		
		RecentChat recentChat = getRecentChat(strId);
		if(recentChat == null){
			recentChat = new RecentChat(strId, nFromType, message.getUserIdForInfo());
			/*if(nFromType == XMessage.FROMTYPE_CHATROOM){
				Event event = AndroidEventManager.getInstance().runEvent(
						EventCode.IM_JoinedRoomNameQuery);
				recentChat.setName((String)event.getReturnParam());
			}*/
			
			mListRecentChat.add(0,recentChat);
			mMapIdToRecentChat.put(recentChat.getId(), recentChat);
		}else{
//			RecentChatManager.print( "最近联系人存在--------未读消息有："+ recentChat.getUnreadMessageCount() + "条");
			mListRecentChat.remove(recentChat);
			mListRecentChat.add(0, recentChat);
		}
		
		if(nFromType == XMessage.FROMTYPE_SINGLE){
			recentChat.setName(message.getUserName());
			if(!message.isFromSelf()){// 来自别人
				if(!message.isReaded()){// 如果未读
//					RecentChatManager.print( "RecentChatManager-onNewMessage--本条消息未读");
					recentChat.addUnreadMessageCount();// 未读数加1
				
					mMapIdToHasUnreadSingleRecentChat.put(recentChat.getId(), recentChat);
					mSingleUnreadMessageTotalCount += 1;

//					RecentChatManager.print( "RecentChatManager-onNewMessage--发出未读event");
					AndroidEventManager.getInstance().postEvent(
							EventCode.HP_UnreadMessageCountChanged,
							0,message);
				}
			}
		}
		
		recentChat.setLastMessage(message);
		
		AndroidEventManager.getInstance().runEvent(EventCode.DB_RecentChat, 
				DBHandleType.WRITE,recentChat);
		
		//
//		RecentChatManager.print( "RecentChatManager----onNewMessage---发出最近联系人改变");
		AndroidEventManager.getInstance().postEvent(EventCode.HP_RecentChatChanged, 0,
				Collections.unmodifiableList(mListRecentChat));
	}
	
	public void deleteRecentChat(String strId){
		RecentChat recentChatRemove = mMapIdToRecentChat.remove(strId);
		if(recentChatRemove != null){
			mListRecentChat.remove(recentChatRemove);
			if(recentChatRemove.getFromType() == XMessage.FROMTYPE_SINGLE &&
					recentChatRemove.getUnreadMessageCount() > 0){
				mMapIdToHasUnreadSingleRecentChat.remove(strId);
				mSingleUnreadMessageTotalCount -= recentChatRemove.getUnreadMessageCount();
			}
			AndroidEventManager.getInstance().runEvent(
					new DBRecentChatEvent(EventCode.DB_RecentChat), 
					DBHandleType.DELETE,strId);
			
			AndroidEventManager.getInstance().postEvent(EventCode.HP_RecentChatChanged, 0,
					Collections.unmodifiableList(mListRecentChat));
		}
	}
	
	public List<RecentChat> getAllRecentChat(){
		return Collections.unmodifiableList(mListRecentChat);
	}
	
	public Collection<RecentChat> getAllHasUnreadRecentChat(){
		//return Collections.unmodifiableCollection(mMapIdToRecentChat.values());
		return getAllHasUnreadSingleRecentChat();
	}
	
	public Collection<RecentChat> getAllHasUnreadSingleRecentChat(){
		return Collections.unmodifiableCollection(mMapIdToHasUnreadSingleRecentChat.values());
	}
	
	public int	getSingleUnreadMessageTotalCount(){
		return mSingleUnreadMessageTotalCount;
	}
	
	public int  getUnreadMessageCount(String strId){
		RecentChat recentChat = mMapIdToRecentChat.get(strId);
		if(recentChat != null){
			return recentChat.getUnreadMessageCount();
		}
		return 0;
	}
	
	public RecentChat getHasUnreadRecentChat(XMessage m){
		if(m.getFromType() == XMessage.FROMTYPE_SINGLE){
			return mMapIdToHasUnreadSingleRecentChat.get(m.getUserId());
		}
		return null;
	}
	
	public void	clearUnreadMessageCount(RecentChat recentChat){
		if(recentChat != null && recentChat.getUnreadMessageCount() > 0){

//			RecentChatManager.print( "RecentChatManager--clearUnreadMessageCount:" + recentChat.getUnreadMessageCount());
			mSingleUnreadMessageTotalCount -= recentChat.getUnreadMessageCount();
			recentChat.setUnreadMessageCount(0);
			mMapIdToHasUnreadSingleRecentChat.remove(recentChat.getId());
			
			AndroidEventManager.getInstance().runEvent(EventCode.DB_RecentChat, 
					DBHandleType.WRITE,recentChat);
		
			AndroidEventManager.getInstance().postEvent(
					EventCode.HP_UnreadMessageCountChanged,
					0);
		}
	}
	
	public RecentChat getRecentChat(String strId){
		return mMapIdToRecentChat.get(strId);
	}

	@Override
	public void onEventRunEnd(Event event) {
		final int nCode = event.getEventCode();
		if(nCode == EventCode.IM_ReceiveMessage ||
				nCode == EventCode.IM_SendMessageStart){
//			RecentChatManager.print( "RecentChatManager---onEventRunEnd");
			mRunnable.mMessage = (XMessage)event.getReturnParam();
			SCApplication.getMainThreadHandler().post(mRunnable);
		}else if(nCode == EventCode.HP_LoginActivityCreated){
			sInstance = null;
		}
	}
	
	private static class ParamRunnable implements Runnable{

		private XMessage mMessage;
		
		@Override
		public void run() {
			final Handler handler = sInstance.mHandler;
			handler.sendMessage(handler.obtainMessage(1, mMessage));
		}
	}
	
	private static class InternalHandler extends Handler{
		public InternalHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			getInstance().onNewMessage((XMessage)msg.obj);
		}
	}
}
