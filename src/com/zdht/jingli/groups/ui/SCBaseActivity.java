package com.zdht.jingli.groups.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.zdht.core.BaseActivity;
import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.event.AddGroupEvent;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.DelFriendEvent;
import com.zdht.jingli.groups.event.EditUserInfoEvent;
import com.zdht.jingli.groups.event.GetActivitiesEvent;
import com.zdht.jingli.groups.event.GetGroupEvent;
import com.zdht.jingli.groups.event.GetUserInfoEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.GroupMember;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.jingli.groups.utils.SchoolUtils;
import com.zdht.utils.SystemUtils;
import com.zdht.utils.ToastManager;

public class SCBaseActivity extends BaseActivity implements OnEventListener{
	
	protected ToastManager 		mToastManager;
	
	protected ProgressDialog 	mProgressDialog;
	
	protected AlertDialog.Builder builder;
	protected AlertDialog dialog;
	
	private SparseArray<OnEventListener> 		mMapCodeToListener;
	
	private View		mViewPromptConnection;
	private View		mViewConnecting;
	private View		mViewNormal;
	private ImageView	mImageViewPromptConnection;
	
	
	protected boolean mNotifyConnection 				= false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mToastManager = ToastManager.getInstance(getApplicationContext());
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(!SCApplication.isIMConnectionSuccess()){// 如果未连接则连接
			addAndManageEventListener(EventCode.IM_ConnectionInterrupt);
			addAndManageEventListener(EventCode.IM_Login);
			addAndManageEventListener(EventCode.IM_LoginStart);
			if(!SCApplication.isIMConnectionSuccess()){
				String className = getClass().getSimpleName();
//				if(className.equals(SingleChatActivity.class.getSimpleName())) {
//					addConnectionPromptView();
//				}
				AndroidEventManager.getInstance().postEvent(EventCode.IM_Login, 0);
			}
		}
	}
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		ba.mTitleLayoutId = R.id.viewTitle;
		ba.mTitleTextLayoutId = R.layout.textview_title;
		ba.mBackButtonLayoutId = R.layout.btn_title_left_back;
		ba.mRightButtonLayoutId = R.layout.btn_title_right;
		ba.mRightButtonRightMargin = SystemUtils.dipToPixel(this, 2);
		ba.mBackButtonLeftMargin = SystemUtils.dipToPixel(this, 2);
	}
	
	protected void addAndManageEventListener(int nEventCode){
		if(mMapCodeToListener == null){
			mMapCodeToListener = new SparseArray<OnEventListener>();
		}
		mMapCodeToListener.put(nEventCode, this);
		
		AndroidEventManager.getInstance().addEventListener(nEventCode, this, false);
	}
	
	protected void removeEventListener(int nEventCode){
		if(mMapCodeToListener == null){
			return;
		}
		mMapCodeToListener.remove(nEventCode);
		
		AndroidEventManager.getInstance().removeEventListener(nEventCode, this);
	}
	
	protected void showAlertDialog(int messageResId, int PositiveBtnResDd, int NegativeBtnResId){
		showAlertDialog(getResources().getString(messageResId), PositiveBtnResDd, NegativeBtnResId);
	}
	protected void showAlertDialog(String message, int PositiveBtnResDd, int NegativeBtnResId){
		if(builder == null){
			builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle(R.string.app_name);
		}
		builder.setMessage(message);
		builder.setPositiveButton(PositiveBtnResDd, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onBuilderPositionBtnClick(dialog, which);
			}
		});
		builder.setNegativeButton(NegativeBtnResId, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onBuilderNegativeBtnClick(dialog, which);
			}
		});
		builder.show();
	}
	
	
	protected void onBuilderPositionBtnClick(DialogInterface dialog, int which){
	}
	
	protected void onBuilderNegativeBtnClick(DialogInterface dialog, int which){
		dialog.cancel();
	}
	
	protected void commitInfo(String phone, String password, String title){
		
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_EditInfo, this, true);
		showProgressDialog(null, R.string.submiting);
		final List<NameValuePair> mListNameValuePair = new ArrayList<NameValuePair>();
		mListNameValuePair.add(new BasicNameValuePair("appId", URLUtils.KEY));
		if(!TextUtils.isEmpty(phone)){
			mListNameValuePair.add(new BasicNameValuePair("phone", phone));
		}
		if(!TextUtils.isEmpty(password)){
			mListNameValuePair.add(new BasicNameValuePair("password", password));
		}
		if(title != null){
			mListNameValuePair.add(new BasicNameValuePair("signature", title));
		}
		
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_EditInfo,
				0, URLUtils.URL_EDITINFO, mListNameValuePair);
	}
	
	protected View addViewInTitleRight(int nResId){
		ImageButton btn = new ImageButton(this);
		btn.setBackgroundResource(nResId);
		return addViewInTitleRight(btn);
	}
	
	protected View addViewInTitleRight(View v){
		return addViewInTitleRight(v, SystemUtils.dipToPixel(this, 2), 0);
	}
	
	protected void showProgressDialog(String strTitle,int nStringId){
		showProgressDialog(strTitle, getString(nStringId));
	}
	
	protected void showProgressDialog(String strTitle,String strMessage){
		if(mProgressDialog == null){
			mProgressDialog = ProgressDialog.show(this, strTitle, strMessage, true, false);
		}
	}
	
	protected void dismissProgressDialog(){
		SystemUtils.dismissProgressDialog(mProgressDialog);
		mProgressDialog = null;
	}
	
	
	
	protected static class TabIndicatorMoveRunnable implements Runnable{
		
		protected final View 		mView;
		protected final int 		mTabWidth;
		protected final Scroller 	mScroller;
		
		public TabIndicatorMoveRunnable(View view, int tabWidth){
			mView = view;
			mTabWidth = tabWidth;
			mScroller = new Scroller(mView.getContext());
		}
		
		public void onTabChanged(int nTabIndex){
			final int nTargetX = mTabWidth * nTabIndex;
			final int nCurPadding = mView.getLeft();
			mScroller.startScroll(nCurPadding, 0, nTargetX - nCurPadding, 0, 100);
			mView.post(this);
		}

		@Override
		public void run() {
			if(mScroller.computeScrollOffset()){
				final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						mTabWidth, 
						RelativeLayout.LayoutParams.MATCH_PARENT);
				lp.leftMargin = mScroller.getCurrX();
				mView.setLayoutParams(lp);
				mView.post(this);
			}
		}
	}

	protected boolean isLocalUser(String uId){
		if(LocalInfoManager.getInstance().isLocalUser(uId)){
			//PersonalInfoActivity.launch(this);
			return true;
		}
		return false;
	}
	
	protected boolean isLocalUser(int uId){
		return isLocalUser(String.valueOf(uId));
	}

	protected void		requestGetUserInfoWithId(String uId, boolean isLogin){
		if(!isLogin){
			if(isLocalUser(uId)){
				return;
			}
		}
		AndroidEventManager.getInstance().addEventListener(
				EventCode.HTTPGET_GetUserInfo, 
				this, true);
		showProgressDialog(null, R.string.geting_user_info);
		AndroidEventManager.getInstance().postEvent(
				EventCode.HTTPGET_GetUserInfo, 0, uId);
	}
	
	
	protected void		requestGetUserInfoWithId(int uId, boolean isLogin){
		if(!isLogin){
			if(isLocalUser(String.valueOf(uId))){
				return;
			}
		}
		AndroidEventManager.getInstance().addEventListener(
				EventCode.HTTPGET_GetUserInfo, 
				this, true);
		showProgressDialog(null, R.string.geting_user_info);
		AndroidEventManager.getInstance().postEvent(
				EventCode.HTTPGET_GetUserInfo, 0, String.valueOf(uId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		if(event.isAsyncRun()){
			dismissProgressDialog();
		}
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPGET_GetUserInfo) {
			GetUserInfoEvent mGetUserInfoEvent = (GetUserInfoEvent)event;
			if(mGetUserInfoEvent.isRequestSuccess()) {
				onHandleGetUserEventRunEnd(mGetUserInfoEvent);
			} else {
				mToastManager.show(mGetUserInfoEvent.getDescribe());
			}
		}else if(nCode == EventCode.HTTPPOST_EditInfo) {
			final EditUserInfoEvent editUserInfoEvent = (EditUserInfoEvent)event;
			mToastManager.show(editUserInfoEvent.getDescribe());
			if(editUserInfoEvent.isRequestSuccess()){// 如果修改成功
				LocalInfoManager.getInstance().editUserToken(editUserInfoEvent.getUserToken());
				OnEditInfoSuccess();
				// 获取修改参数
				List<NameValuePair> values = editUserInfoEvent.getListNameValuePair();
				for(NameValuePair value : values) {
					if(value.getName().equals("password")) {// 如果参数包含密码
						AndroidEventManager.getInstance().postEvent(EventCode.IM_ChangePassword, 0, value.getValue());
					}
				}
			}
		}else if(nCode == EventCode.HTTPGET_GetMyCreateGroups){
			GetGroupEvent getGroupEvent = (GetGroupEvent)event;
			if(getGroupEvent.isRequestSuccess()){
				LocalInfoManager.getInstance().setListMyCreateGroup((List<Group>)getGroupEvent.getReturnParam());
				onMyCreateGroupGetSuccess((List<Group>)getGroupEvent.getReturnParam());
			}else {
				onMyCreateGroupGetFail();
			}
		}else if(nCode == EventCode.HTTPGET_GetMyCreateActivioes){
			GetActivitiesEvent getActivitiesEvent = (GetActivitiesEvent)event;
			if(getActivitiesEvent.isRequestSuccess()){
				LocalInfoManager.getInstance().setListMyCreateActivity((List<Activity>)getActivitiesEvent.getReturnParam());
				onMyCreateActivityGetSuccess((List<Activity>)getActivitiesEvent.getReturnParam());
			}else {
//				mToastManager.show(R.string.no_create_activity);
				onMyCreateActivityGetFail();
			}
		}else if(nCode == EventCode.HTTPGET_AddFriend){
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				Object[] params = baseInfoGetEvent.getRequestParams();
				if(params.length == 2){
					User user = (User)params[1];
					user.isFriend(true);
					//AddressActivity.addOKFriend(user);
				}
			}
		}else if(nCode == EventCode.HTTPGET_AddGroup){
			//TODO 添加圈子反馈
			AddGroupEvent addGroupEvent = (AddGroupEvent)event;
			if(addGroupEvent.isRequestSuccess()){
				mToastManager.show(addGroupEvent.getDescribe());
				if(!addGroupEvent.getDescribe().equals("请求已提交,等待验证中")) {
					Group group = (Group)addGroupEvent.getReturnParam();
					group.setIsAdd(true);
					//AddressActivity.addOKGroup(group);
				}
			} else {
				mToastManager.show(addGroupEvent.getDescribe());
			}
		}else if(nCode == EventCode.HTTPGET_DelRelationShip){
			DelFriendEvent delFriendEvent = (DelFriendEvent)event;
			if(TextUtils.isEmpty(delFriendEvent.getDescribe())){
				mToastManager.show(R.string.submit_failed);
			} else {
				mToastManager.show("删除好友, " + delFriendEvent.getDescribe());
			}
		}else if(mNotifyConnection){
			if(nCode == EventCode.IM_Login){
				if(SCApplication.isIMConnectionSuccess()){
					if(mViewPromptConnection != null){
						removeConnectionPromptView();
					}
				}else{
					if(mViewPromptConnection != null){
						mViewConnecting.setVisibility(View.GONE);
						mViewNormal.setVisibility(View.VISIBLE);
					}
				}
			}else if(nCode == EventCode.IM_ConnectionInterrupt){
				if(mViewPromptConnection == null){
					addConnectionPromptView();
				}else{
					mViewConnecting.setVisibility(View.GONE);
					mViewNormal.setVisibility(View.VISIBLE);
				}
			}else if(nCode == EventCode.IM_LoginStart){
				if(mViewPromptConnection == null){
					addConnectionPromptView();
				}
				mViewConnecting.setVisibility(View.VISIBLE);
				mViewNormal.setVisibility(View.GONE);
			}
		}
		
	}
	
	protected void OnEditInfoSuccess(){
		requestGetUserInfoWithId(LocalInfoManager.getInstance().getmLocalInfo().getUserId(), false);
	}
	
	protected void onMyCreateGroupGetSuccess(List<Group> list){
	}
	
	protected void onMyCreateGroupGetFail(){
	}
	
	protected void onMyCreateActivityGetSuccess(List<Activity> list){
	}
	
	protected void onMyCreateActivityGetFail(){
	}
	
	protected void onHandleGetUserEventRunEnd(GetUserInfoEvent event){
		if(event.isLocalUser()){
			//PersonalInfoActivity.launch(this);
		}else{
			if(event.isRequestSuccess()){
				final User user = event.getUser();
				if(user != null){
					//UserHomeActivity.launch(this, user);
				}
			}else{
				mToastManager.show(R.string.get_user_info_failed);
			}
		}
	}
	
	
	protected void onLoginSuccessGetUserInfo(GetUserInfoEvent getUserInfoEvent){
		LocalInfoManager.getInstance().saveDetailInfoJson(getUserInfoEvent.getHttpRetString());
		final String strAvatarUrl = getUserInfoEvent.getUser().getAvatarUrl();
		final String strAvatarFilePath = FilePaths.getAvatarSavePath(SchoolUtils.getFileNameFronUrl(strAvatarUrl));
		Bitmap bmp = BitmapFactory.decodeFile(strAvatarFilePath);
		if(bmp == null){
			AvatarBmpProvider.getInstance().loadImage(strAvatarUrl);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mViewPromptConnection != null){
			removeConnectionPromptView();
		}
		
		if(mMapCodeToListener != null){
			final int nCount = mMapCodeToListener.size();
			for(int nIndex = 0;nIndex < nCount;++nIndex){
				int nCode = mMapCodeToListener.keyAt(nIndex);
				AndroidEventManager.getInstance().removeEventListener(nCode, 
						mMapCodeToListener.get(nCode));
			}
			mMapCodeToListener.clear();
		}
	}
	
	
	
	protected void getMyCreateGroup(){
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetMyCreateGroups, this, false);
		showProgressDialog(null, R.string.geting_my_groups);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetMyCreateGroups, 0,
				String.format(URLUtils.URL_GetGroups, 6, "", "", 1));
	}
	
	protected void getMyCreateActivity(){
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetMyCreateActivioes, this, false);
		showProgressDialog(null, R.string.geting_my_activities);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetMyCreateActivioes, 0,
				String.format(URLUtils.URL_GetActivities, 8, "", 1));
	}
	
	@Override
	protected String getCameraSaveFilePath() {
		return FilePaths.getCameraSaveFilePath();
	}
	
	protected void startUnConnectionAnimation(){
		if(mViewPromptConnection != null && mViewNormal.getVisibility() == View.VISIBLE){
			mImageViewPromptConnection = (ImageView)mViewPromptConnection.findViewById(R.id.imageView);
			mImageViewPromptConnection.setBackgroundDrawable(null);
			mImageViewPromptConnection.setBackgroundResource(R.anim.animlist_prompt_connection);
			AnimationDrawable d = (AnimationDrawable)mImageViewPromptConnection.getBackground();
			d.start();
		}
	}
	/** 添加重连提示视图 */
	private void addConnectionPromptView(){
		WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		mViewPromptConnection = LayoutInflater.from(this).inflate(R.layout.prompt_connection, null);
		mViewConnecting = mViewPromptConnection.findViewById(R.id.viewConnecting);
		mViewNormal = mViewPromptConnection.findViewById(R.id.viewNormal);
		mViewConnecting.setVisibility(View.VISIBLE);
		mViewNormal.setVisibility(View.GONE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT, 
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		lp.y = SystemUtils.dipToPixel(this,50);
		lp.gravity = Gravity.TOP;
		
		windowManager.addView(mViewPromptConnection, lp);
	}
	
	private void removeConnectionPromptView(){
		WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(mViewPromptConnection);
		mViewPromptConnection = null;
	}
	
	
	protected void addFriend(User user){
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_AddFriend, this, true);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_AddFriend, 0,
				String.format(URLUtils.URL_AddFriend, user.getUid()), user);
	}
	
	protected void delFriend(User user) {
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_DelRelationShip, this, true);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_DelRelationShip, 0,
				String.format(URLUtils.URL_DelRelationShip, user.getUid()), user);
	}
	
	protected void addGroup(Group group){
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_AddGroup, this, true);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_AddGroup, 0,
				String.format(URLUtils.URL_AddGroup, group.getId()), group);
	}
	
	protected void delComMember(String groupId, GroupMember member){
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_DelComMember, this, true);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_DelComMember, 0,
				String.format(URLUtils.URL_DelComMember, groupId, member.mUserId), member);
	}
}
