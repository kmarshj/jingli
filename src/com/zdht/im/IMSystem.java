package com.zdht.im;

import java.util.Date;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.utils.MessageGroupTimeGenerator;
import com.zdht.utils.SystemUtils;

public abstract class IMSystem extends Service implements ConnectionListener,
														  ChatManagerListener{

	protected AtomicReference<String> mAtomicLoginUsername = new AtomicReference<String>();
	protected AtomicReference<String> mAtomicLoginPassword = new AtomicReference<String>(); 
	
	protected XMPPConnection 					mConnection;
	
	private WeakHashMap<String, String> mMapUserIdToChatThreadId = new WeakHashMap<String, String>();
	
	protected boolean 	mIsReConnect 	   			= true;
	protected int		mReConnectIntervalMillis 	= 1000;
	
//	protected boolean	mIsNetworkMonitoring	= false;
	protected boolean	mIsInitiativeDisConnect = false;
	protected boolean	mIsConnectionAvailable	= false;
	protected boolean	mIsConnecting			= false;
	
	protected boolean   mIsRegister             = false;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mIsReConnect = true;
		startNetworkMonitor();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mIsReConnect = false;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	protected abstract void requestLogin();
	
	protected void doLogin(ConnectionConfiguration cc) throws XMPPException{
		if(mIsConnectionAvailable){
			return;
		}
		
		if(mIsConnecting){
			return;
		}
		
		mIsConnecting = true;
		try {
			mConnection = new XMPPConnection(cc);

			PrivacyListManager.getInstanceFor(mConnection);

			mConnection.connect();
			mConnection.getChatManager().addChatListener(this);
			mConnection.addConnectionListener(this);

			final String strUsername = mAtomicLoginUsername.get();
			final String strPassword = mAtomicLoginPassword.get();
			mConnection.login(strUsername, strPassword);

			/*mRoster = mConnection.getRoster();
			mRoster.addRosterListener(this);

			Presence presence = new Presence(Presence.Type.available);
			onInterceptLoginPresence(presence);

			mConnection.sendPacket(presence);

			updateContactByRoster();
			
			onLoginGet();*/
			
			if(mIsInitiativeDisConnect){
				mConnection.removeConnectionListener(this);
				mConnection.disconnect();
			}else{
				mIsConnectionAvailable = true;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			mConnection.removeConnectionListener(this);
			if(mConnection.isAuthenticated()){
				mConnection.disconnect();
			}
			throw e;
		} finally {
			mIsConnecting = false;
		}
	}

	protected void handleRegisterFinished(boolean bSuccess){
		if(bSuccess){
			requestReconnect();
		}else {
			requestRegister();
		}
	}


	protected void handleLoginFinished(boolean bSuccess){
		if(bSuccess){
			/*final IMChatRoom room = mAtomicDisconnectRoom.get();
			if(room != null){
				requestJoinRoom(room);
			}*/
		}else{
			if(!mIsRegister){
				mIsRegister = true;
				requestRegister();
				return;
			}
        	if(mIsReConnect){
				if(SystemUtils.isNetworkAvailable(IMSystem.this)){
					requestReconnect();
				}else{
//					startNetworkMonitor();
				}
			}
		}
	}

	protected abstract void requestReconnect();
	
	protected abstract void requestRegister();

	protected void startNetworkMonitor(){
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mBroadcastReceiverNetworkMonitor, intentFilter);
	}

	protected BroadcastReceiver mBroadcastReceiverNetworkMonitor = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(SystemUtils.isNetworkAvailable(intent)){
				if(!mIsConnectionAvailable) {
					SCApplication.print("网络好了--开始重连");
					requestLogin();
				}
			}
		}
	};

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		// TODO chatCreated
		mMapUserIdToChatThreadId.put(removeSuffix(chat.getParticipant()), chat.getThreadID());
		if(!createdLocally){// 本地没有建立消息监听器则建立
			chat.addMessageListener(mMessageListenerSingleChat);
		}
	}



	@Override
	public void connectionClosed() {
		SCApplication.print( "IMSystem---connectionClosed");
		if(!mIsInitiativeDisConnect){
			mIsConnectionAvailable = false;
//			onHandleConnectionClosedOnError();
		}
//		startNetworkMonitor();
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		SCApplication.print( "IMSystem---connectionClosedOnError:");
		if(!mIsInitiativeDisConnect){
			mIsConnectionAvailable = false;
		}
	}


	@Override
	public void reconnectingIn(int arg0) {
		SCApplication.print( "IMSystem----reconnectingIn:" + arg0);
	}


	@Override
	public void reconnectionFailed(Exception arg0) {
		SCApplication.print( "IMSystem----reconnectionFailed:" + arg0.getMessage());
	}

	@Override
	public void reconnectionSuccessful() {
		SCApplication.print( "IMSystem----reconnectionSuccessful:");
	}

	protected void doLoginOut(){
		mIsInitiativeDisConnect = true;
		
		mIsConnectionAvailable = false;
		
		mConnection.removeConnectionListener(this);
		mConnection.disconnect();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	protected void onHandleConnectionClosedOnError(){
//		requestLogin();
		/*synchronized (mMultiUserChat) {
			MultiUserChatEx multiUserChat = mMultiUserChat.get();
			if(multiUserChat != null){
				mAtomicDisconnectRoom.set(multiUserChat.getChatRoom());
				mMultiUserChat.set(null);
			}
		}*/
	}
	
	
	protected void doSend(XMessage xm) throws XMPPException{
		final int nFromType = xm.getFromType();
		if (nFromType == HPMessage.FROMTYPE_CHATROOM) {
			/*final MultiUserChat multiUserChat = mMultiUserChat.get();
			if (multiUserChat != null) {
				Message message = multiUserChat.createMessage();
				onSendInit(message, xm);
				multiUserChat.sendMessage(message);
			}*/
		} else if (nFromType == HPMessage.FROMTYPE_SINGLE) {
			final String strFromId = xm.getFromId().toUpperCase(Locale.CHINA);
			SCApplication.print( "doSend -- fromId:" + strFromId);
			Chat chat = getOrCreateChat(strFromId);
			chat.sendMessage(onSendInit(xm));
			SCApplication.print("发的是--->" + onSendInit(xm));
		}
	}
	
	protected String onSendInit(XMessage xm){
		//final XMessage xMessage = xm;
		/*final int nMessageType = xMessage.getType();
		if(nMessageType == HPMessage.TYPE_VOICE){
			
		}else if(nMessageType == HPMessage.TYPE_PHOTO){
			
		}else{*/
			return "{\"name\":\"" + LocalInfoManager.getInstance().getmLocalInfo().getRealName() 
					+ "\",\"useridforinfo\":\""+xm.getUserIdForInfo()+"\"," 
					+ "\"avatar\":\""+LocalInfoManager.getInstance().getAvatar()+"\",\"content\":\"" 
			+ xm.getContent() + "\",\"type\":" + xm.getType() + ",\"time\":\"" +  new Date().getTime() + "\"}";
		//}
		
	}
	
	protected abstract String 	addSuffixUserJid(String strUserId);
	
	protected Chat getOrCreateChat(String strUserId){
		String strChatThreadId = mMapUserIdToChatThreadId.get(strUserId);
		Chat chat = mConnection.getChatManager().getThreadChat(strChatThreadId);
		if(chat == null){
			chat = mConnection.getChatManager().createChat(addSuffixUserJid(strUserId), 
					mMessageListenerSingleChat);
		}
//		Log.d("chat", "getOrCreateChat");
		return chat;
	}
	
	private MessageListener mMessageListenerSingleChat = new MessageListener() {
		@Override
		public void processMessage(Chat chat, Message message) {
			onProcessSingleChatMessage(chat, message);
		}
	};
	
	
	protected void onProcessSingleChatMessage(Chat chat,Message message){
//		RecentChatManager.print( "----onProcessSingleChatMessage");
		if(message.getType().equals(Message.Type.error)){
//			RecentChatManager.print( "错误");
			return;
		}
		onProcessSingleChatBody(chat, message);
	}
	
	protected void onProcessSingleChatBody(Chat chat,Message message){
		// TODO 处理聊天信息
		try {
			final String strUserId = removeSuffix(chat.getParticipant()).toUpperCase(Locale.CHINA);
			SCApplication.print("收到：" + message.getBody());
			final JSONObject jsonObject = new JSONObject(message.getBody());
			final int type = jsonObject.getInt("type");
			
			XMessage xm = onCreateXMessage(strUserId, type);
			xm.setFromType(XMessage.FROMTYPE_SINGLE);
			xm.setUserIdForInfo(jsonObject.getString("useridforinfo"));
			xm.setUserId(strUserId);
			xm.setUserName(jsonObject.getString("name"));
			xm.setAvatar(jsonObject.getString("avatar"));
			xm.setContent(jsonObject.getString("content"));
			xm.setSendTime(jsonObject.getLong("time"));
			MessageGroupTimeGenerator.processGroupTime(xm);
			xm.setFromSelf(false);
			//onSetMessageCommonValue(xm, message);
			onReceiveMessage(xm);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected void	onReceiveMessage(XMessage xm){
		//TODO 收到消息
	}
	
	protected void onSetMessageCommonValue(XMessage hm,Message m){
		hm.setFromSelf(false);
		hm.setContent(m.getBody());
		
		/*final int nType = hm.getType();
		if(nType == XMessage.TYPE_VOICE){
			final String strSize = body.attributes.getAttributeValue("size");
			hm.setTag(strSize);
		}else if(nType == XMessage.TYPE_PHOTO){
			MessageDetailExtension de = (MessageDetailExtension)m.getExtension("detail", "jabber:client");
			hm.setTag(de.getContent());
		}*/
	}
	
	protected abstract XMessage onCreateXMessage(String strId,int nMessageType);
	
	protected String	removeSuffix(String strJid){
		SCApplication.print("用户id:" + strJid);
		int nIndex = strJid.lastIndexOf("@");
		if(nIndex != -1){
			return strJid.substring(0, nIndex);
		}
		return strJid;
	}
	
}
