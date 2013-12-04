package com.zdht.jingli.groups;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.zdht.jingli.groups.model.XMessage;
import com.zdht.utils.SystemUtils;

public class HPUserFilePathManager {
	
	public static HPUserFilePathManager getInstance(){
		if(sInstance == null){
			sInstance = new HPUserFilePathManager();
		}
		return sInstance;
	}
	
	private static HPUserFilePathManager sInstance;
	
	private String mFilePathPrefix;
	
	private HPUserFilePathManager(){
	}
	
	public void onInit(Context context,String strIMUserId){
		mFilePathPrefix = SystemUtils.getExternalCachePath(context) + File.separator + strIMUserId;
	}
	
	public String getMessagePhotoFilePath(XMessage m){
		if(m.getType() == XMessage.TYPE_PHOTO){
			return getMessageFilePath(m.getSendToId(), m.getFromType()) + File.separator +
					"photo" + File.separator + m.getId();
		}else{
			throw new IllegalArgumentException("photoPath juse use type = photo");
		}
	}
	
	public String getMessagePhotoThumbFilePath(XMessage m){
		String strPath = getMessagePhotoFilePath(m);
		return strPath + "thumb";
	}
	
	public String getMessageVoiceFilePath(XMessage m){
		if(m.getType() == XMessage.TYPE_VOICE){
			return getMessageFilePath(m.getSendToId(), m.getFromType()) + File.separator +
					"voice" + File.separator + m.getId();
		}else{
			throw new IllegalArgumentException("voiceFilePath just use type = Voice");
		}
	}
	
	public String getMessageFilePath(String strId,int nFromType){
		StringBuffer buf = new StringBuffer(mFilePathPrefix);
		buf.append(File.separator);
		if(nFromType == XMessage.FROMTYPE_CHATROOM){
			buf.append("room").append(File.separator);
			if(!TextUtils.isEmpty(strId)){
				buf.append(strId);
			}else{
				throw new IllegalArgumentException("roomId is Empty");
			}
		}else if(nFromType == XMessage.FROMTYPE_SINGLE){
			buf.append("single").append(File.separator);
			if(!TextUtils.isEmpty(strId)){
				buf.append(strId);
			}else{
				throw new IllegalArgumentException("userId is Empty");
			}
		}else{
			throw new IllegalArgumentException("unknow fromtype");
		}
		
		return buf.toString();
	}
}
