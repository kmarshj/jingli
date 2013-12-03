package com.zdht.jingli.groups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.zdht.core.Event;
import com.zdht.core.EventManager;

public class AndroidEventManager extends EventManager {
	
	private static AndroidEventManager sInstance;
	
	public static AndroidEventManager getInstance(){
		if(sInstance == null){
			sInstance = new AndroidEventManager();
		}
		return sInstance;
	}
	
	private static final int WHAT_EVENT_NOTIFY 	= 1;
	private static final int WHAT_EVENT_RUN		= 2;
	
	private SparseArray<Event> mMapCodeToEvent = new SparseArray<Event>();
	
	private List<EventWrapper> mListEventWrapper = new LinkedList<AndroidEventManager.EventWrapper>();
	
	/** 运行中的EVENT */
	private Map<Event,Event> mMapEventRunning = new ConcurrentHashMap<Event, Event>();
	
	private List<Event> mListEventNotify = new LinkedList<Event>();
	private boolean mIsEventNotifying;
	
	private SparseArray<List<OnEventListener>> mMapCodeToEventListener = new SparseArray<List<OnEventListener>>();
	private SparseArray<List<OnEventListener>> mMapCodeToEventListenerAddCache = new SparseArray<List<OnEventListener>>();
	private SparseArray<List<OnEventListener>> mMapCodeToEventListenerRemoveCache = new SparseArray<List<OnEventListener>>();
	private boolean mIsMapListenerLock = false;
	private SparseArray<OnEventListener> mMapListenerUserOnce = new SparseArray<OnEventListener>();
	
	private Map<Event,List<ParamWrapper>> mMapEventToWaitRunParam = new ConcurrentHashMap<Event, List<ParamWrapper>>();
	
	private static Handler mHandler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			//Log.d("chatbug", "AndroidEventManager.mHandler.handleMessage");
			final int nWhat = msg.what;
			if(nWhat == WHAT_EVENT_RUN){
				EventWrapper ew = (EventWrapper)msg.obj;
				sInstance.runEvent(ew.mEvent, ew.mParams);
			
				sInstance.recycleEventWrapper(ew);
			}else if(nWhat == WHAT_EVENT_NOTIFY){
				sInstance.doNotify((Event)msg.obj);
			}
		}
	};
	

	@Override
	public void addEvent(Event event) {
		mMapCodeToEvent.put(event.getEventCode(), event);
	}

	@Override
	public void removeEvent(int nEventCode) {
		//Log.d("eventobj", "mMapCodeToEvent--remove:" + nEventCode);
		
		mMapCodeToEvent.remove(nEventCode);
	}

	@Override
	public void removeAllEvent() {
		mMapCodeToEvent.clear();
	}

	@Override
	public void postEvent(int nEventCode, long delayMillis, Object... params) {
		// 注销后接收消息的event为空了。
		Event event = getEvent(nEventCode);
		postEvent(event, delayMillis, params);
	}

	@Override
	public void postEvent(Event event, long delayMillis, Object... params) {
		if(event != null){
			EventWrapper ew = obtainEventWrapper();
			ew.set(event, params);
			Message msg = mHandler.obtainMessage(WHAT_EVENT_RUN, ew);
			mHandler.sendMessageDelayed(msg , delayMillis);
		}
	}
	
	private synchronized EventWrapper obtainEventWrapper(){
		if(mListEventWrapper.size() > 0){
			EventWrapper ev = mListEventWrapper.get(0);
			mListEventWrapper.remove(0);
			return ev;
		}
		return new EventWrapper();
	}
	
	private synchronized void 	recycleEventWrapper(EventWrapper ew){
		ew.clear();
		mListEventWrapper.add(ew);
	}
	
	public	void 	addEventListener(int nEventCode,OnEventListener listener,boolean bOnce){
		if(mIsMapListenerLock){
			addToListenerMap(mMapCodeToEventListenerAddCache, nEventCode, listener);
		}else{
			addToListenerMap(mMapCodeToEventListener, nEventCode, listener);
		}
		if(bOnce){
			mMapListenerUserOnce.put(calculateHashCode(nEventCode, listener), listener);
		}
	}
	
	public  void	removeEventListener(int nEventCode,OnEventListener listener){
		if(mIsMapListenerLock){
			addToListenerMap(mMapCodeToEventListenerRemoveCache, nEventCode, listener);
		}else{
			List<OnEventListener> listeners = mMapCodeToEventListener.get(nEventCode);
			if(listeners != null){
				listeners.remove(listener);
			}
		}
	}
	
	private int		calculateHashCode(int nEventCode,OnEventListener listener){
		int nResult = nEventCode;
		nResult = nResult * 29 + listener.hashCode();
		return nResult;
	}
	
	
	private void doNotify(Event event){

		mIsEventNotifying = true;
		
		mIsMapListenerLock = true;
		List<OnEventListener> list = mMapCodeToEventListener.get(event.getEventCode());

//		if(event.getEventCode() == EventCode.IM_ReceiveMessage) {
//			RecentChatManager.print( "AndroidEventManager---doNotify>>>" + list.size());
//		}
		if(list != null){
			List<OnEventListener> listNeedRemove = null;
			for(OnEventListener listener : list){
//				if(event.getEventCode() == EventCode.IM_ReceiveMessage) {
//					RecentChatManager.print( "AndroidEventManager---doNotify>>>" + listener.getClass().getSimpleName());
//				}
				listener.onEventRunEnd(event);
				int nHashCode = calculateHashCode(event.getEventCode(), listener);
				if(mMapListenerUserOnce.get(nHashCode) != null){
					mMapListenerUserOnce.remove(nHashCode);
					if(listNeedRemove == null){
						listNeedRemove = new ArrayList<EventManager.OnEventListener>();
					}
					listNeedRemove.add(listener);
					
//					SCApplication.getLogger().info("removeOnce Code:" + event.getEventCode() + 
//							" listener:" + listener);
				}
			}
			if(listNeedRemove != null){
				list.removeAll(listNeedRemove);
			}
		}
		mIsMapListenerLock = false;
		
		mIsEventNotifying = false;
		
		if(mMapCodeToEventListenerAddCache.size() > 0){
			int nSize = mMapCodeToEventListenerAddCache.size();
			for(int nIndex = 0;nIndex < nSize;++nIndex){
				int nCode = mMapCodeToEventListenerAddCache.keyAt(nIndex);
				List<OnEventListener> listCache = mMapCodeToEventListenerAddCache.get(nCode);
				if(listCache.size() > 0){
					List<OnEventListener> listeners = mMapCodeToEventListener.get(nCode);
					if(listeners == null){
						listeners = new LinkedList<AndroidEventManager.OnEventListener>();
						mMapCodeToEventListener.put(nCode, listeners);
					}
					listeners.addAll(listCache);
				}
			}
			mMapCodeToEventListenerAddCache.clear();
		}
		if(mMapCodeToEventListenerRemoveCache.size() > 0){
			int nSize = mMapCodeToEventListenerRemoveCache.size();
			for(int nIndex = 0;nIndex < nSize;++nIndex){
				int nCode = mMapCodeToEventListenerRemoveCache.keyAt(nIndex);
				List<OnEventListener> listCache = mMapCodeToEventListenerRemoveCache.get(nCode);
				if(listCache.size() > 0){
					List<OnEventListener> listeners = mMapCodeToEventListener.get(nCode);
					if(listeners != null){
						listeners.removeAll(listCache);
					}
				}
			}
			mMapCodeToEventListenerRemoveCache.clear();
		}
		
		if(mListEventNotify.size() > 0){
			Event eventNotify = mListEventNotify.get(0);
			mListEventNotify.remove(0);
			mHandler.sendMessage(mHandler.obtainMessage(WHAT_EVENT_NOTIFY, eventNotify));
		}
		
	}

	@Override
	public Event runEvent(int nEventCode, Object... params) {
		Event event = getEvent(nEventCode);
		return runEvent(event, params);
	}

	@Override
	public Event runEvent(Event event, Object... params) {
		if(event == null){
			return null;
		}
		
		if(mMapEventRunning.containsKey(event)){// 此event是否在运行中
			if(event.isWaitRunWhenRunning()){
				List<ParamWrapper> listParamWrapper = mMapEventToWaitRunParam.get(event);
				if(listParamWrapper == null){
					listParamWrapper = Collections.synchronizedList(new LinkedList<ParamWrapper>());
					mMapEventToWaitRunParam.put(event, listParamWrapper);
				}
				listParamWrapper.add(new ParamWrapper(params));
			}
			return null;
		}
		
		mMapEventRunning.put(event, event);
		
		if(event.isAsyncRun()){
			final Event localEvent = event;
			new AsyncTask<Object, Void, Void>() {
				@Override
				protected void onPreExecute() {
					localEvent.onPreRun();
				}

				@Override
				protected Void doInBackground(Object... params) {
					try {
						localEvent.run(params);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					onEventRunEnd(localEvent);
				}
				
			}.execute(params);
		}else{
			try {
				event.run(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 
			onEventRunEnd(event);
		}
		return event;
	}

	
	
	private void addToListenerMap(SparseArray<List<OnEventListener>> map,
			int nEventCode,OnEventListener listener){
		List<OnEventListener> listeners = map.get(nEventCode);
		if(listeners == null){
			listeners = new LinkedList<AndroidEventManager.OnEventListener>();
			map.put(nEventCode, listeners);
		}
		listeners.add(listener);
	}
	
	protected void	onEventRunEnd(Event event){
		// 从运行中event列表移除该event
		mMapEventRunning.remove(event);
//		if(event.getEventCode() == EventCode.IM_ReceiveMessage) {
//			RecentChatManager.print( "AndroidEventManager.onEventRunEnd-start>>>" + event.getEventCode() + "/" + event.getReturnParam().toString() );
//	}
		event.onRunEnd();
		
		if(event.isNotifyAfterRun()){// 
			notifyEventRunEnd(event);
		}
		
		List<ParamWrapper> listParamWrapper = mMapEventToWaitRunParam.get(event);
		if(listParamWrapper != null){
			if(listParamWrapper.size() > 0){
				ParamWrapper param = listParamWrapper.get(0);
				listParamWrapper.remove(param);
				postEvent(event, 0,param.mParams);
				if(listParamWrapper.size() == 0){
					mMapEventToWaitRunParam.remove(event);
				}
			}
		}
	}
	
	private void	notifyEventRunEnd(Event event){
		if(mIsEventNotifying){
			mListEventNotify.add(event);
		}else{
			doNotify(event);
		}
	}
	
	
	public Event	getEvent(int nEventCode){
		//Log.d("eventobj", "mMapCodeToEvent-size:" + mMapCodeToEvent.size());
		
		return mMapCodeToEvent.get(nEventCode);
	}
	
	private static class EventWrapper{
		public Event mEvent;
		public Object[] mParams;
		
		public void set(Event event,Object ... params){
			mEvent = event;
			mParams = params;
		}
		
		public void clear(){
			mEvent = null;
			mParams = null;
		}
	}
	
	
	private static class ParamWrapper{
		public Object[] mParams;
		
		public ParamWrapper(Object[] params){
			mParams = params;
		}
	}

}
