package com.zdht.jingli.groups;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
	
	public static final String SP_NAME 		= "school";
	
	public static final String KEY_USERTOKEN    = "token";
	public static final String KEY_USERID	    = "id";
	public static final String KEY_STUDENTID	= "studentid";
	public static final String KEY_USERPWD		= "pwd";
	public static final String KEY_REALNAME		= "realname";
	public static final String KEY_SCHOOL_CODE	= "schoolCode";
	public static final String KEY_SCHOOL_NAME	= "schoolName";
	public static final String KEY_LOGO_URL		= "logoUrl";
	public static final String KEY_TAG = "jpushtag";
	
	public static final String KEY_AVATAR	    = "avatar";
	public static final String KEY_SEX	        = "sex";
	public static final String KEY_FACULTY	    = "faculties";
	public static final String KEY_ClASS	    = "class";
	public static final String KEY_PHONE	    = "phone";
	public static final String KEY_SIGNATURE	= "signature";
	
	
	
	public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

	public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

	public static final String API_KEY = "API_KEY";

	public static final String VERSION = "VERSION";

	public static final String XMPP_HOST = "XMPP_HOST";

	public static final String XMPP_PORT = "XMPP_PORT";
	
	public static final String XMPP_USERNAME = "XMPP_USERNAME";

	public static final String XMPP_PASSWORD = "XMPP_PASSWORD";


	//public static final String SHARED_PREFERENCE_NAME = "client_preferences";
	// public static final String USER_KEY = "USER_KEY";

	public static final String DEVICE_ID = "DEVICE_ID";

	public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";

	public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

	public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";

	public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

	public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

	public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

	// NOTIFICATION FIELDS

	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

	public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

	public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

	public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

	public static final String NOTIFICATION_URI = "NOTIFICATION_URI";

	// INTENT ACTIONS
	public static final String ACTION_SHOW_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";

	public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";

	public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";
	
	public static final String SP_CONFIG 	= "config";
	public static final String KEY_RECEIVENOTIFY		= "receivenotify";
	public static final String KEY_RECEIVESOUNDNOTIFY	= "receivesoundnotify";
	public static final String KEY_RECEIVEVIBRATENOTIFY	= "receivevibratenotify";
	
	/*public static final String KEY_IMUSER 		= "imuser";
	public static final String KEY_IMPWD 		= "impwd";
	public static final String KEY_IMNICK		= "nick";
	public static final String KEY_AVATARURL	= "avatarurl";
	public static final String KEY_QRCODEURL	= "qrcodeurl";
	public static final String KEY_USERTYPE		= "usertype";
	public static final String KEY_AUDITSTATUS	= "auditstatus";
	
	
	public static final String SP_CONFIG 	= "config";
	public static final String KEY_RECEIVENOTIFY		= "receivenotify";
	public static final String KEY_RECEIVESOUNDNOTIFY	= "receivesoundnotify";
	public static final String KEY_RECEIVEVIBRATENOTIFY	= "receivevibratenotify";*/
	
	public static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(SP_NAME, 0);
	}
	
	
	
}
