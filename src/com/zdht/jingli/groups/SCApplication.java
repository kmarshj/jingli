package com.zdht.jingli.groups;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.groups.event.AddGroupEvent;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.BaseInfoPostEvent;
import com.zdht.jingli.groups.event.CallbackEvent;
import com.zdht.jingli.groups.event.DBFacultyClassesStructureEvent;
import com.zdht.jingli.groups.event.DBRecentChatEvent;
import com.zdht.jingli.groups.event.DelFriendEvent;
import com.zdht.jingli.groups.event.EditUserInfoEvent;
import com.zdht.jingli.groups.event.GetActivitiesEvent;
import com.zdht.jingli.groups.event.GetActivityEvent;
import com.zdht.jingli.groups.event.GetAddressListEvent;
import com.zdht.jingli.groups.event.GetFacultyStructureEvent;
import com.zdht.jingli.groups.event.GetGroupEvent;
import com.zdht.jingli.groups.event.GetGroupInfoEvent;
import com.zdht.jingli.groups.event.GetLiveInfoEvent;
import com.zdht.jingli.groups.event.GetNewsEvent;
import com.zdht.jingli.groups.event.GetPhoneEvent;
import com.zdht.jingli.groups.event.GetStuNoEvent;
import com.zdht.jingli.groups.event.GetUserInfoEvent;
import com.zdht.jingli.groups.event.GetUsersEvent;
import com.zdht.jingli.groups.event.LoginEvent;
import com.zdht.jingli.groups.event.PostAvatarEvent;
import com.zdht.jingli.groups.event.PostFindPdEvent;
import com.zdht.jingli.groups.event.PostImageEvent;
import com.zdht.jingli.groups.event.QuitAndAddActivityEvent;
import com.zdht.jingli.groups.im.HPIMSystem;
import com.zdht.jingli.groups.im.IMStatus;
import com.zdht.jingli.groups.localinfo.LocalBaseInfoProtocol;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.utils.SchoolUtils;

public class SCApplication extends Application implements OnEventListener{
	public static final String TAG = "scapplication";
	
	public static boolean isDebug = true;
	
	private static SCApplication sInstance;
	
	private static Logger sLogger;
	
	public Handler	mHandler;
	
	public int appPause;
	
	public static void print(String str) {
		int l = 80;
		if(isDebug) {
			if(str.length() <= l) {
				Log.d(TAG, str);
				return;
			}
			int nums = str.length() / l;
			int aa = str.length() % l;
			for(int i = 0; i < nums;i++) {
				Log.d(TAG, str.substring(i * l, i * l + l));
			}
			Log.d(TAG, str.substring(nums * l, nums * l + aa));
		}
	}
	
	public static void print(String str, int eventCode) {
		if(eventCode == EventCode.HTTPGET_AddActivity){
			print(str);
		}
	}
	
	public int getAppPause() {
		return appPause;
	}

	public void setAppPause(int appPause) {
		this.appPause = appPause;
	}
	
	public static SCApplication getInstance() {
		return sInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;

        JPushInterface.setDebugMode(false); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        
		mHandler = new Handler();
		
		appPause = -1;
		
		AndroidEventManager eventManager = AndroidEventManager.getInstance();
		
		eventManager.addEvent(new LoginEvent(EventCode.HTTPPOST_Login));
		eventManager.addEvent(new GetPhoneEvent(EventCode.HTTPPOST_GetPhone));
		eventManager.addEvent(new PostImageEvent(EventCode.HTTPPOST_PostActivityImage));
		eventManager.addEvent(new PostImageEvent(EventCode.HTTPPOST_PostLiveImage));
		eventManager.addEvent(new PostImageEvent(EventCode.HTTPPOST_PostGroupAvatar));
		eventManager.addEvent(new PostAvatarEvent(EventCode.HTTPPOST_PostAvatar));
		eventManager.addEvent(new PostFindPdEvent(EventCode.HTTPPOST_FindPassword));
		eventManager.addEvent(new EditUserInfoEvent(EventCode.HTTPPOST_EditInfo));
		eventManager.addEvent(new BaseInfoPostEvent(EventCode.HTTPPOST_CreateGroup, true));
		eventManager.addEvent(new BaseInfoPostEvent(EventCode.HTTPPOST_EditGroup, true));
		eventManager.addEvent(new BaseInfoPostEvent(EventCode.HTTPPOST_CreateActivity, true));
		eventManager.addEvent(new BaseInfoPostEvent(EventCode.HTTPPOST_EditActivity, true));
		eventManager.addEvent(new BaseInfoPostEvent(EventCode.HTTPPOST_HandlerAddFriendMessage, true));
		
		
		eventManager.addEvent(new GetUserInfoEvent(EventCode.HTTPGET_GetUserInfo));
		eventManager.addEvent(new GetActivitiesEvent(EventCode.HTTPGET_GetCurrentActivities, false));
		eventManager.addEvent(new GetActivitiesEvent(EventCode.HTTPGET_GetMyCreateActivioes, true));
		eventManager.addEvent(new GetActivitiesEvent(EventCode.HTTPGET_GetMyAddActivioes, true));
		eventManager.addEvent(new GetActivitiesEvent(EventCode.HTTPGET_GetGroupOnGoingActivioes, true));
		eventManager.addEvent(new GetActivitiesEvent(EventCode.HTTPGET_GetGroupPastActivioes, true));
		eventManager.addEvent(new GetGroupEvent(EventCode.HTTPGET_GetMyCreateGroups, true));
		eventManager.addEvent(new GetGroupEvent(EventCode.HTTPGET_GetGroups, false));
		eventManager.addEvent(new GetGroupEvent(EventCode.HTTPGET_GetKeyWordGroups, false));
		eventManager.addEvent(new QuitAndAddActivityEvent(EventCode.HTTPGET_QuitActivity, true));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_DeleteActivityMember, false));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_DelActivity, true));
		eventManager.addEvent(new AddGroupEvent(EventCode.HTTPGET_AddGroup, true));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_AddFriend, true));
		eventManager.addEvent(new DelFriendEvent(EventCode.HTTPGET_DelRelationShip, true));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_QuitGroup, true));
		eventManager.addEvent(new QuitAndAddActivityEvent(EventCode.HTTPGET_AddActivity, true));
		eventManager.addEvent(new GetLiveInfoEvent(EventCode.HTTPGET_GetLiveInfo));
		eventManager.addEvent(new GetUsersEvent(EventCode.HTTPGET_GetActivityMember, true, false));
		eventManager.addEvent(new GetUsersEvent(EventCode.HTTPGET_GetClassUser, false, true));
		eventManager.addEvent(new GetUsersEvent(EventCode.HTTPGET_GetKeyWordUser, false, false));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_SendLive, true));
		eventManager.addEvent(new GetGroupInfoEvent(EventCode.HTTPGET_GetGroupInfo));
		eventManager.addEvent(new GetGroupEvent(EventCode.HTTPGET_GetUserAddedGroup, true));
		eventManager.addEvent(new GetFacultyStructureEvent(EventCode.HTTPGET_GetFacultiesClasses));
		eventManager.addEvent(new GetAddressListEvent(EventCode.HTTPGET_GetAddressList));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_Feedback, true));
		eventManager.addEvent(new GetStuNoEvent(EventCode.HTTPGET_GetStuNo));
		eventManager.addEvent(new BaseInfoGetEvent(EventCode.HTTPGET_DelComMember, true));
		eventManager.addEvent(new GetNewsEvent(EventCode.HTTPGET_GetNews, false));
		eventManager.addEvent(new GetActivityEvent(EventCode.HTTPGET_GetActivityById));
		
		
		eventManager.addEvent(new CallbackEvent(EventCode.HP_LoginGetOrChangeInfoSuccess));
		eventManager.addEvent(new CallbackEvent(EventCode.HP_UnreadMessageCountChanged));
		eventManager.addEvent(new CallbackEvent(EventCode.HP_RecentChatChanged));
		
		eventManager.addEvent(new CallbackEvent(EventCode.IM_ReceiveSystemMessage));
		eventManager.addEvent(new CallbackEvent(EventCode.IM_ReceiveMessage));
		eventManager.addEvent(new CallbackEvent(EventCode.IM_SendMessageStart));
		eventManager.addEvent(new CallbackEvent(EventCode.HP_PostPhotoPercentChanged));
		
		eventManager.addEvent(new DBFacultyClassesStructureEvent(EventCode.DB_FacultyClassesStructureHandle));
		eventManager.addEvent(new DBRecentChatEvent(EventCode.DB_RecentChat));
		
		eventManager.addEventListener(EventCode.IM_SystemStarted, this, false);
		eventManager.addEventListener(EventCode.HTTPPOST_Login, this, false);
		eventManager.addEventListener(EventCode.HP_LoginGetOrChangeInfoSuccess, this, false);
	}

	public void onUserLogined(){
		final String strUserId = LocalInfoManager.getInstance().getmLocalInfo().getStudentId();
		Intent intent = new Intent(this, HPIMSystem.class);
		startService(intent);
		
		HPUserDatabaseManager.getInstance().onInit(this, strUserId);
		HPUserFilePathManager.getInstance().onInit(this, strUserId);
		RecentChatManager.getInstance().onInit();
		HPDatabaseManager.getInstance().onInit(this);
		// 读取jpush标签信息并设置
		String tagsStr = LocalInfoManager.getInstance().getmLocalInfo().getJTag();
		print("tagsStr----------------------" + tagsStr);
		if(!TextUtils.isEmpty(tagsStr)) {
			String[] tags = tagsStr.split(",");
			Set<String> tagSet = new HashSet<String>();
			for(int i = 0; i < tags.length; i++) {
				tagSet.add(tags[i]);
			}
			
			setJPushInfo(null, tagSet);
		}
		// 设置别名
		String id = LocalInfoManager.getInstance().getmLocalInfo().getStudentId();
		print("别名----------------------" + id);
		setJPushInfo(id, null);
	}
	
	/** 设置极光推送别名和标签 */
	public void setJPushInfo(String alias, Set<String> tags) {
		// TODO调用JPush API设置Alias
		JPushInterface.setAliasAndTags(getApplicationContext(), alias, tags, null);
	}
	
	public static Logger getLogger(){
		if(sLogger == null){
			sLogger = Logger.getLogger(sInstance.getPackageName());
			sLogger.setLevel(Level.ALL);
			LoggerSystemOutHandler handler = new LoggerSystemOutHandler();
			handler.setLevel(Level.ALL);
			sLogger.addHandler(handler);
		}
		return sLogger;
	}
	
	public static SharedPreferences getDefaultSharedPreferences(){
		return SharedPreferenceManager.getSharedPreferences(sInstance);
	}

	public static Context getApplication(){
		return sInstance.getApplicationContext();
	}
	
	
	public static Handler	getMainThreadHandler(){
		return sInstance.mHandler;
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		final int nCode = event.getEventCode();
		if(nCode == EventCode.IM_SystemStarted){
			print("im开始");
			onIMServiceStarted();
		}else if(nCode == EventCode.HTTPPOST_Login){
			print("Login");
			onLoginEventRunEnd(event);
		}else if(nCode == EventCode.HP_LoginGetOrChangeInfoSuccess){
			//Log.d("mydebug", "登录获取资料成功");
			onUserLogined();
		}
	}
	
	protected void onIMServiceStarted(){
		final String strIMUser = LocalInfoManager.getInstance().getmLocalInfo().getStudentId();
		
		AndroidEventManager.getInstance().runEvent(EventCode.IM_SetLoginUser, 
				strIMUser,
				LocalInfoManager.getInstance().getmLocalInfo().getUserPwd());
		AndroidEventManager.getInstance().postEvent(EventCode.IM_Login, 0);
	}
	
	protected void onLoginEventRunEnd(Event event){
		if(event instanceof LocalBaseInfoProtocol){
			LoginEvent hEvent = (LoginEvent)event;
			if(hEvent.isRequestSuccess()){
				boolean status = (Boolean)event.getReturnParam();
				if(status){
					LocalBaseInfoProtocol localBaseInfo = (LocalBaseInfoProtocol)event;
					saveLocalBaseInfo(localBaseInfo);
				}
			}
		}else{
			throw new IllegalArgumentException("LoginEvent must implements LocalBaseInfoProtocol");
		}
	}
	
	private void saveLocalBaseInfo(LocalBaseInfoProtocol localBaseInfo){
		Map<String, String> map = new HashMap<String, String>();
		SchoolUtils.buildLocalInfoSaveMap(map, localBaseInfo);
		LocalInfoManager.getInstance().save(map);
	}
	
	public static void loginOut(){
		sInstance.userLoginOut();
	}
	
	protected void userLoginOut(){
		Intent intent = new Intent(this, HPIMSystem.class);
		stopService(intent);
		LocalInfoManager.getInstance().onLoginOut();
		HPUserDatabaseManager.getInstance().onDestory();
		RecentChatManager.getInstance().onDestroy();
		StatusBarManager.getInstance().clearStatusBar();
	}
	
	public static boolean isIMConnectionSuccess(){
		IMStatus status = new IMStatus();
		AndroidEventManager.getInstance().runEvent(EventCode.IM_StatusQuery,status);
		return status.mIsLogined;
	}
	
}
