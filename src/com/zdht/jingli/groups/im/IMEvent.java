package com.zdht.jingli.groups.im;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XMPPError;


import com.zdht.core.Event;

public abstract class IMEvent extends Event {

	protected boolean  	mIsSuccess = false;
	
	protected int		mErrorCode = 0;
	
	protected Object	mParams[];
	
	protected boolean	mIsHandleDelayable = true;
	
	private	  List<PacketCollector> mListPacketCollector;
	
	public IMEvent(int nEventCode) {
		super(nEventCode);
		
		mIsAsyncRun = true;
		mIsNotifyAfterRun = true;
	}
	
	public boolean 	isSuccess(){
		return mIsSuccess;
	}

	public int	getErrorCode(){
		return mErrorCode;
	}
	
	protected void managePacketCollector(PacketCollector collector){
		if(mListPacketCollector == null){
			mListPacketCollector = new ArrayList<PacketCollector>();
		}
		mListPacketCollector.add(collector);
	}
	
	protected void checkResultIQ(IQ result) throws XMPPException{
		if (result == null) {
			throw new XMPPException("No response from the server.");
		} else if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
	}
	
	public void setIsHandleDelayable(boolean bHandleDelayable){
		mIsHandleDelayable = bHandleDelayable;
	}
	
	public boolean isHandleDelayable(){
		return mIsHandleDelayable;
	}
	
	@Override
	public void onPreRun() {
		super.onPreRun();
		mIsSuccess = false;
		mErrorCode = 0;
	}

	@Override
	public void run(Object... params) throws Exception {
		mParams = params;
		
		if (canExecute()) {
			execute(params);
		}else{
			if(this instanceof Delayable){
				if(isHandleDelayable()){
					setIsNotifyAfterRun(false);
					onHandleDelayable();
				}else{
					setIsHandleDelayable(true);
				}
			}
		}
	}
	
	protected void execute(Object... params) throws Exception{
		try {
			mIsSuccess = onExecute(params);
		} catch (XMPPException e) {
			mIsSuccess = false;
			XMPPError error = e.getXMPPError();
			if (error != null) {
				mErrorCode = error.getCode();
			}else if("No response from the server.".equals(e.getMessage())){
				onTimeout();
			}
			throw e;
		} finally {
//			SCApplication.getLogger().info(getClass().getName() + " execute:" + mIsSuccess);

			if (mListPacketCollector != null) {
				for (PacketCollector collector : mListPacketCollector) {
					collector.cancel();
				}
				mListPacketCollector.clear();
			}
		}
	}
	
	/** 执行 */
	protected abstract boolean 	onExecute(Object... params) throws XMPPException;
	/** 是否可以执行 */
	protected abstract boolean 	canExecute();
	
	protected abstract void		onHandleDelayable() throws Exception;

	/** 连接超时 */
	protected abstract void		onTimeout();
}
