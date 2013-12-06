package com.zdht.jingli.groups;

import java.util.Collection;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.RecentChat;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.ui.MainActivity;

public class StatusBarManager implements OnEventListener{
	
	public static StatusBarManager getInstance(){
		if(sInstance == null){
			sInstance = new StatusBarManager();
		}
		return sInstance;
	}
	
	private StatusBarManager(){	}
	
	private static StatusBarManager sInstance;
	
	private static final int NOTIFY_ID_SINGLECHAT = 1;
	
	private NotificationManager mNotificationManager;
	
	private String mTickerLast;
	
	public void onStart(){
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_UnreadMessageCountChanged,
				this, false);
		
		mNotificationManager = (NotificationManager)SCApplication.getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onEventRunEnd(Event event) {
		Collection<RecentChat> collection = RecentChatManager.getInstance()
				.getAllHasUnreadSingleRecentChat();
		final int nSingleRecentChatSize = collection.size();
		if(nSingleRecentChatSize > 0){
			if(!ConfigManager.getInstance().isReceiveNewMessageNotify()){
				clearStatusBar();
				return;
			}
			
			final XMessage xmRet = (XMessage)event.getReturnParam();
			
			String strTicker = null;
			if(xmRet != null){
				final String strNickname = xmRet.getUserName();
				strTicker = SCApplication.getApplication().getString(
					R.string.statusbar_singleusertextnotify, strNickname == null ? "" : strNickname);
				if(strTicker.equals(mTickerLast)){
					strTicker = strTicker + " ";
				}
				mTickerLast = strTicker;
			}
			
			final int nUnreadMessageTotalCount = RecentChatManager.getInstance().getSingleUnreadMessageTotalCount();
			
			Notification notification = new Notification(R.drawable.ic_launcher, strTicker,
					System.currentTimeMillis());
			notification.number = nUnreadMessageTotalCount;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			if(ConfigManager.getInstance().isReceiveNewMessageSoundNotify() && xmRet != null){
				notification.defaults |= Notification.DEFAULT_SOUND;
			}
			if(ConfigManager.getInstance().isReceiveNewMessageVibrateNotify() && xmRet != null){
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			}
			if(nSingleRecentChatSize > 1){
				final Context app = SCApplication.getApplication();
//				notification.setLatestEventInfo(app, 
//						app.getString(R.string.app_name), 
//						app.getString(R.string.statusbar_multiusernotify, nSingleRecentChatSize,nUnreadMessageTotalCount),
//						MainActivity.getPendingIntent(app,null,null,null));
			}else{
				final Context app = SCApplication.getApplication();
				final RecentChat recentChat = collection.iterator().next();
//				if(nUnreadMessageTotalCount > 1){
//					notification.setLatestEventInfo(app, 
//							recentChat.getName(), 
//							app.getString(R.string.statusbar_singleusermultimessagenotify, nUnreadMessageTotalCount),
//							MainActivity.getPendingIntent(app, 
//									recentChat.getId(), 
//									recentChat.getName(),
//									recentChat.getLastMessage().getAvatar()));
//				}else{
//					XMessage m = recentChat.getLastMessage();
//					final int nMessageType = m == null ? XMessage.TYPE_TEXT : m.getType();
//					notification.setLatestEventInfo(app,
//							app.getString(nMessageType == XMessage.TYPE_VOICE ? 
//									R.string.statusbar_singleuservoicenotify : R.string.statusbar_singleusertextnotify, 
//									recentChat.getName()), 
//							nMessageType == XMessage.TYPE_VOICE ?
//									app.getString(R.string.inquiry_voice) :
//										nMessageType == XMessage.TYPE_PHOTO ?
//												app.getString(R.string.inquiry_photo) :
//													m == null ? "" : m.getContent(),
//							MainActivity.getPendingIntent(app, 
//									recentChat.getId(), 
//									recentChat.getName(),
//									recentChat.getLastMessage().getAvatar()));
//				}
			}
			
			mNotificationManager.notify(NOTIFY_ID_SINGLECHAT, notification);
		}else{
			clearStatusBar();
		}
	}
	
	public void clearStatusBar(){
		mNotificationManager.cancel(NOTIFY_ID_SINGLECHAT);
	}
}
