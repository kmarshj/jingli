package com.zdht.jingli.groups;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {

	public static ConfigManager getInstance(){
		if(sInstance == null){
			sInstance = new ConfigManager();
		}
		return sInstance;
	}
	
	private static ConfigManager sInstance;
	
	private boolean mIsReceiveNewMessageNotify;
	
	private boolean mIsReceiveNewMessageSoundNotify;
	private boolean mIsReceiveNewMessageVibrateNotify;
	
	private ConfigManager(){
		SharedPreferences sp = getConfigSharedPreferences();
		mIsReceiveNewMessageNotify = sp.getBoolean(SharedPreferenceManager.KEY_RECEIVENOTIFY, true);
		mIsReceiveNewMessageSoundNotify = sp.getBoolean(SharedPreferenceManager.KEY_RECEIVESOUNDNOTIFY, true);
		mIsReceiveNewMessageVibrateNotify = sp.getBoolean(SharedPreferenceManager.KEY_RECEIVEVIBRATENOTIFY, true);
	}
	
	public boolean isReceiveNewMessageNotify(){
		return mIsReceiveNewMessageNotify;
	}
	
	public boolean isReceiveNewMessageSoundNotify(){
		return mIsReceiveNewMessageSoundNotify;
	}
	
	public boolean isReceiveNewMessageVibrateNotify(){
		return mIsReceiveNewMessageVibrateNotify;
	}
	
	public void	setReceiveNewMessageNotify(boolean bNotify){
		mIsReceiveNewMessageNotify = bNotify;
		getConfigSharedPreferences().edit()
		.putBoolean(SharedPreferenceManager.KEY_RECEIVENOTIFY, bNotify)
		.commit();
	}
	
	public void setReceiveNewMessageSoundNotify(boolean bNotify){
		mIsReceiveNewMessageSoundNotify = bNotify;
		getConfigSharedPreferences().edit()
		.putBoolean(SharedPreferenceManager.KEY_RECEIVESOUNDNOTIFY, bNotify)
		.commit();
	}
	
	public void setReceiveNewMessageVibrateNotify(boolean bNotify){
		mIsReceiveNewMessageVibrateNotify = bNotify;
		getConfigSharedPreferences().edit()
		.putBoolean(SharedPreferenceManager.KEY_RECEIVEVIBRATENOTIFY, bNotify)
		.commit();
	}
	
	private SharedPreferences getConfigSharedPreferences(){
		return SCApplication.getApplication().getSharedPreferences(
				SharedPreferenceManager.SP_CONFIG, Context.MODE_PRIVATE);
	}
}

