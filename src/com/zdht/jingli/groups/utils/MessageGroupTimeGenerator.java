package com.zdht.jingli.groups.utils;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.event.DBReadLastMessageEvent;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.parampool.DBReadLastMessageParam;

public class MessageGroupTimeGenerator {
	
	private static final int GROUPTIME_INTERVAL = 120000;
	
	private static Map<String, Long> mMapIdToLastGroupTime = new HashMap<String, Long>();
	
	public static synchronized void processGroupTime(XMessage m){
		String strEntityId = m.getFromId();
		if(TextUtils.isEmpty(strEntityId)){
			m.setGroupTime(m.getSendTime());
		}else{
			Long lastGroupTime = null;
			lastGroupTime = getGroupTimeLast(strEntityId, m.getFromType(), m.getSendTime());
			long lGroupTimeNext = getGroupTimeNext(lastGroupTime, m.getSendTime());
			if(lGroupTimeNext != lastGroupTime){
				mMapIdToLastGroupTime.put(strEntityId, Long.valueOf(lGroupTimeNext));
			}
			
			m.setGroupTime(lGroupTimeNext);
		}
	}
	
	public static long getGroupTimeNext(long lGroupTimeLast,long lSendTime){
		long lGroupTimeCur = lSendTime;
		if(lGroupTimeCur - lGroupTimeLast > GROUPTIME_INTERVAL){
			return lGroupTimeCur;
		}
		return lGroupTimeLast;
	}
	
	private static long getGroupTimeLast(String strId,int nFromType,long lSendTime){
		Long lastGroupTime = mMapIdToLastGroupTime.get(strId);
		if(lastGroupTime == null){
			DBReadLastMessageParam param = new DBReadLastMessageParam();
			param.mFromType = nFromType;
			param.mId = strId;
			param.mColumnNames = new String[]{DBColumns.Message.COLUMN_SENDTIME};
			AndroidEventManager.getInstance().runEvent(new DBReadLastMessageEvent(), param);
			String strTime = param.mMapColumnNameToValue.get(DBColumns.Message.COLUMN_SENDTIME);
			long lTime = strTime == null ? 0 : Long.parseLong(strTime);
			if(lTime == 0){
				lastGroupTime = lSendTime;
			}else{
				lastGroupTime = lTime;
			}
			mMapIdToLastGroupTime.put(strId, lastGroupTime);
		}
		return lastGroupTime.longValue();
	}
}
