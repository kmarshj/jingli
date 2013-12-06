package com.zdht.jingli.groups.im;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zdht.core.Event;
import com.zdht.im.IMSystem;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.event.CallbackEvent;
import com.zdht.jingli.groups.event.DBMessageEvent;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.parampool.DBHandleType;
import com.zdht.jingli.groups.parampool.DBMessageParam;

public class HPIMSystem extends IMSystem{

	private ConcurrentLinkedQueue<IMEvent> mQueueDelayEvent = new ConcurrentLinkedQueue<IMEvent>();
	private HashMap<String, Boolean> registerMap = new HashMap<String, Boolean>();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		AndroidEventManager em = AndroidEventManager.getInstance();
		
		em.addEvent(new SetLoginUserEvent());
		em.addEvent(new StatusQueryEvent());
		em.addEvent(new LoginEvent());
		em.addEvent(new RigesterEvent());
		em.addEvent(new SendMessageEvent());
		em.addEvent(new ChangePdEvent());
		
		em.postEvent(new CallbackEvent(EventCode.IM_SystemStarted),0);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		AndroidEventManager em = AndroidEventManager.getInstance();
		em.removeEvent(EventCode.IM_SetLoginUser);
		em.removeEvent(EventCode.IM_StatusQuery);
		em.removeEvent(EventCode.IM_Login);
		em.removeEvent(EventCode.IM_Register);
		em.removeEvent(EventCode.IM_SendMessage);
		em.removeEvent(EventCode.IM_ChangePassword);
		
		AndroidEventManager.getInstance().postEvent(new LoginOutEvent(), 0);
	}
	
	private class SetLoginUserEvent extends Event{

		public SetLoginUserEvent() {
			super(EventCode.IM_SetLoginUser);
		}

		@Override
		public void run(Object... params) throws Exception {
			mAtomicLoginUsername.set((String)params[0]);
			mAtomicLoginPassword.set((String)params[1]);
			
		}
		
	}
	
	private abstract class IMBaseEvent extends IMEvent{
		public IMBaseEvent(int nEventCode) {
			super(nEventCode);
		}

		@Override
		protected boolean canExecute() {
			if(mIsConnectionAvailable){
				return true;
			}
			return false;
		}

		@Override
		protected void onTimeout() {
			mConnection.disconnect();
		}

		@Override
		protected void onHandleDelayable() throws Exception{
			SCApplication.getLogger().info("HPIMSystem--onHandleDelayable:" + getClass().getName());
			synchronized (mQueueDelayEvent) {
				if(mIsConnectionAvailable){
					setIsNotifyAfterRun(true);
					execute(mParams);
				}else{
					mQueueDelayEvent.add(this);
					//SCApplication.print( "HPIMSystem----onHandleDelayable---connection error");
				}
			}
		}
		
	}
	
	
	private class LoginEvent extends IMBaseEvent{

		public LoginEvent() {
			super(EventCode.IM_Login);
			this.mErrorCode = 0;
		}

		@Override
		protected boolean canExecute() {
			return true;
		}

		@Override
		protected void onTimeout() {
		}

		@Override
		protected boolean onExecute(Object... params) throws XMPPException{
			
			ConnectionConfiguration connectionConfiguration = createBaseConnConfig();
			
			AndroidEventManager.getInstance().postEvent(new CallbackEvent(EventCode.IM_LoginStart), 0);

			try{
				doLogin(connectionConfiguration);
			}catch(XMPPException e) {
				if(e.getMessage().contains("not-authorized(401)")){
					// 检测是否注册过， 注册过则不再注册
						final String name = mAtomicLoginUsername.get(); 
						Boolean obj = registerMap.get(name);
						System.out.println("是否注册过：" + obj);
						if(obj == null || !obj) {
							requestRegister();
						}
				}
				return false;
			}finally{
				synchronized (mQueueDelayEvent) {
					IMEvent event = null;
					while ((event = mQueueDelayEvent.poll()) != null) {
						if(event.mErrorCode == 401) {
							mQueueDelayEvent.remove(event);
						} else {
							event.setIsNotifyAfterRun(true);
							event.setIsHandleDelayable(false);
							AndroidEventManager.getInstance().postEvent(event, 0, event.mParams);
						}
					}
				}
			}
			
			return true;
		}
		
		@Override
		public void onRunEnd() {
			super.onRunEnd();
			//Log.d("mydebug", "---->" + isSuccess());
//			handleLoginFinished(isSuccess());
		}
	}

	private class StatusQueryEvent extends Event{

		public StatusQueryEvent() {
			super(EventCode.IM_StatusQuery);
			
			mIsAsyncRun = false;
		}

		@Override
		public void run(Object... params) throws Exception {
			IMStatus status = (IMStatus)params[0];
			status.mIsLogined = mIsConnectionAvailable;
		}
	}
	
	private ConnectionConfiguration createBaseConnConfig(){
		ConnectionConfiguration connectionConfiguration = new
		ConnectionConfiguration("180.96.23.114", 5222);
		
		connectionConfiguration.setSecurityMode(SecurityMode.disabled);
		connectionConfiguration.setSASLAuthenticationEnabled(false);
		connectionConfiguration.setCompressionEnabled(false);
		return connectionConfiguration;
	}
	
	private class RigesterEvent extends IMBaseEvent{

		public RigesterEvent() {
			super(EventCode.IM_Register);
		}

		@Override
		protected boolean canExecute() {
			return true;
		}

		@Override
		protected void onTimeout() {
		}
		
		@Override
		protected boolean onExecute(Object... params) throws XMPPException {
			ConnectionConfiguration connectionConfiguration = createBaseConnConfig();
			XMPPConnection connection = new XMPPConnection(connectionConfiguration);
			Registration registration = new Registration();

            PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                    registration.getPacketID()), new PacketTypeFilter(
                    IQ.class));

            PacketListener packetListener = new PacketListener() {
                public void processPacket(Packet packet) {
                    if (packet instanceof IQ) {
                        IQ response = (IQ) packet;
                      	SCApplication.print( "RigesterEvent --- response:" + response.toXML());
                        if (response.getType() == IQ.Type.ERROR) {
                            if (!response.getError().toString().contains("409")) {
                           	 Log.i("mydebug", "Unknown error while registering XMPP account!------------->" + response.getError().getCondition());
                            }
                        } else if (response.getType() == IQ.Type.RESULT) {
                            Log.i("mydebug", "Account registered successfully");
                        }
                    }
                }
            };
            
            try {
        		connection.connect();
        		connection.addPacketListener(packetListener, packetFilter);
                registration.setType(IQ.Type.SET);
//                Log.d("mydebug", "注册注册注册");
                registration.addAttribute("username", mAtomicLoginUsername.get());
                registration.addAttribute("password", mAtomicLoginPassword.get());
                connection.sendPacket(registration);
                registerMap.put(mAtomicLoginUsername.get(), true);
                requestLogin();
//                Log.d("mydebug", "注册结束");
                
			} catch (Exception e) {
//				Log.d("mydebug", "注册异常");
				e.printStackTrace();
				return false;
			}finally{
				connection.disconnect();
			}
			return true;
		}
		
		@Override
		public void onRunEnd() {
			super.onRunEnd();
			handleRegisterFinished(isSuccess());
		}
		
	}
	
	
	
	private class ChangePdEvent extends IMBaseEvent {

		public ChangePdEvent() {
			super(EventCode.IM_ChangePassword);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean onExecute(Object... params) throws XMPPException {
			// TODO Auto-generated method stub
			String pd = (String)params[0];
			try {
				mConnection.getAccountManager().changePassword(pd);
				//SCApplication.print( "修改密码成功");
			} catch(XMPPException e) {
				//SCApplication.print( "修改密码失败");
				e.printStackTrace();
				return false;
			}
			return true;
		}

	}
	

	@Override
	protected void requestLogin() {
		AndroidEventManager.getInstance().postEvent(EventCode.IM_Login, 0, (Void)null);
		//SCApplication.print( "HPIMSystem---requestLogin");
	}

	@Override
	protected void requestReconnect() {
		AndroidEventManager.getInstance().postEvent(EventCode.IM_Login,
				mReConnectIntervalMillis);
	}

	
	private class LoginOutEvent extends IMBaseEvent{

		public LoginOutEvent() {
			super(EventCode.ZERO_CODE);
		}

		@Override
		protected boolean onExecute(Object... params) throws XMPPException {
			doLoginOut();
			
			return true;
		}

		@Override
		protected void onTimeout() {
		}

		@Override
		protected boolean canExecute() {
			return true;
		}
		
		@Override
		public void onRunEnd() {
			super.onRunEnd();
			
			AndroidEventManager.getInstance().postEvent(new CallbackEvent(EventCode.IM_LoginOuted), 0);
		}
	}
	

	
	private class SendMessageEvent extends IMBaseEvent{
		public SendMessageEvent() {
			super(EventCode.IM_SendMessage);
		}

		@Override
		protected boolean onExecute(Object... params) throws XMPPException {
			HPMessage hMessage = (HPMessage)params[0];
			
			AndroidEventManager.getInstance().postEvent(
					new CallbackEvent(EventCode.IM_SendMessageStart, hMessage), 0);
			
			boolean bSuccess = false;
			try{
				doSend(hMessage);
				bSuccess = true;
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				hMessage.setSended();
				hMessage.setSendSuccess(bSuccess);
				hMessage.updateDB();
			}
			
			return bSuccess;
		}
	}

	private void saveMessage(HPMessage m){
		DBMessageParam param = new DBMessageParam(DBHandleType.WRITE);
		param.mSaveMessage = m;
		AndroidEventManager.getInstance().runEvent(new DBMessageEvent(0), param);
	}
	
	@Override
	protected void onReceiveMessage(XMessage xm) {
		super.onReceiveMessage(xm);
		final HPMessage hm = (HPMessage)xm;
		saveMessage(hm);
//		RecentChatManager.print( "HPIMSystem.onReceiveMessage");
		notifyReceiveMessage(hm);
	}

	private void notifyReceiveMessage(HPMessage m){
		// 执行IM_ReceiveMessage，让所有注册了IM_ReceiveMessage的listener都接收数据
//		RecentChatManager.print( "code:" + EventCode.IM_ReceiveMessage);
		AndroidEventManager.getInstance().postEvent(EventCode.IM_ReceiveMessage, 0,m);
	}
	
	@Override
	protected String addSuffixUserJid(String strUserId) {
		return strUserId + "@bogon";
	}



	@Override
	protected XMessage onCreateXMessage(String strId, int nMessageType) {
		return new HPMessage(strId, nMessageType);
	}



	@Override
	protected void requestRegister() {
		AndroidEventManager.getInstance().postEvent(EventCode.IM_Register,
				0);
		
	}
	
	@Override
	protected void onHandleConnectionClosedOnError() {
		super.onHandleConnectionClosedOnError();
		AndroidEventManager.getInstance().postEvent(
				new CallbackEvent(EventCode.IM_ConnectionInterrupt), 1000);
	}
	
	

}

