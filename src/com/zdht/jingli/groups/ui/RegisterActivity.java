package com.zdht.jingli.groups.ui;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.SharedPreferenceManager;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.event.CallbackEvent;
import com.zdht.jingli.groups.event.GetStuNoEvent;
import com.zdht.jingli.groups.event.GetUserInfoEvent;
import com.zdht.jingli.groups.event.LoginEvent;
import com.zdht.jingli.groups.event.PostFindPdEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;

import com.zdht.utils.ToastManager;

/**
 * 注册页面
 * @author luchengsong
 *
 */
public class RegisterActivity extends SCBaseActivity implements OnClickListener,
                                                             OnEventListener{
	private EditText et_phone;
	private EditText et_nicheng;
	private EditText et_registerpassword;
	private EditText et_registerpassword_second;
	private RelativeLayout rl_complete;
	private String mphone;
	private String mnicheng;
	private String mregisterpsd;
	private String mregisterpsd_two;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et_phone = (EditText)findViewById(R.id.et_phone);
		et_nicheng= (EditText)findViewById(R.id.et_nicheng);
		et_registerpassword = (EditText)findViewById(R.id.et_registerpassword);
		et_registerpassword_second = (EditText)findViewById(R.id.et_registerpassword_second);
		
		rl_complete = (RelativeLayout) findViewById(R.id.rl_complete);

		
		AndroidEventManager.getInstance().postEvent(
				new CallbackEvent(EventCode.HP_RegisterActivityCreated), 0);
		

	}
	
	public void hideSystemKeyBoard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		//得到InputMethodManager的实例 
		if (imm.isActive()) { 
			//如果开启 
			imm.hideSoftInputFromWindow(et_phone.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(et_nicheng.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(et_registerpassword.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(et_registerpassword_second.getWindowToken(), 0);
		}
	 }
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.register;
	}
	

	

	@Override
	public void onClick(View v) {
		int nId = v.getId();
		
		
		switch(nId) {
		case R.id.rl_login:
	
			
			 mphone = et_phone.getEditableText().toString();
			 mnicheng = et_nicheng.getEditableText().toString();
			 mregisterpsd = et_registerpassword.getEditableText().toString();
			 mregisterpsd_two = et_registerpassword_second.getEditableText().toString();
			
			if(!checkUserName(mphone)){
				return;
			}
			
			if (TextUtils.isEmpty(mregisterpsd)) {
				mToastManager.show(R.string.register_password);
				return;
			}
			
			final List<NameValuePair> mListNameValuePair = new ArrayList<NameValuePair>();
//			mListNameValuePair.add(new BasicNameValuePair("appId", URLUtils.KEY));
//			mListNameValuePair.add(new BasicNameValuePair("mode", "0"));
//			mListNameValuePair.add(new BasicNameValuePair("userName", strPassword));
//			mListNameValuePair.add(new BasicNameValuePair("password", strPassword));
//			mListNameValuePair.add(new BasicNameValuePair("audit", "1"));
//			mListNameValuePair.add(new BasicNameValuePair("count", "1"));
			showProgressDialog("", R.string.logining);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_Login, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_Login,0,
							URLUtils.URL_Login, 
							mListNameValuePair);
			break;
		}
	}
	
	
	private boolean checkUserName(String strusername) {
		if (TextUtils.isEmpty(strusername)) {
			mToastManager.show(R.string.register_phone);
			return false;
		}

		return true;
	}

	public static void launch(Activity activity) {
		Intent intent = new Intent(activity, RegisterActivity.class);
		activity.startActivity(intent);
	}
	

	@Override
	public void onEventRunEnd(Event event) {
		// TODO onEventRunEnd
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPPOST_Login){
//			if(loading.getVisibility() == View.VISIBLE) {
//				loading.setVisibility(View.GONE);
//			}
			final LoginEvent loginEvent = (LoginEvent)event;
			if(loginEvent.isRequestSuccess()){
				if(loginEvent.ismIsFirst()){
//					if(mRequestCodeAccountManagement == 0){
//						mRequestCodeAccountManagement = generateRequestCode();
//					}
				//	AccountManagementActivity.launchForResult(this, mRequestCodeAccountManagement);
				}else {
					onLoginSuccess();
				}
			}else {
				mToastManager.show(loginEvent.getDescribe());
			}
		} else if(nCode == EventCode.HTTPPOST_FindPassword) {
		 	final PostFindPdEvent baseInfoEvent = (PostFindPdEvent)event;
		 	mToastManager.show(baseInfoEvent.getDescribe());
//		 	if(baseInfoEvent.isRequestSuccess()){
//		 		closeGetverificationWindow();
//		 		stuNoContent.setText("    " + baseInfoEvent.getNickname() + 
//		 				",您好\r\n    您的密码是:" + baseInfoEvent.getPd() + 
//		 				"\r\n\r\n\r\n小贴士:为确保账户安全,请定期修改您的密码.");
//		 		
//		 		findStuNoContentLayout.setVisibility(View.VISIBLE);
//		 	}
		} else if(nCode == EventCode.HTTPGET_GetStuNo) {
			GetStuNoEvent mGetStuNoEvent = (GetStuNoEvent)event;
			mToastManager.show(mGetStuNoEvent.getDescribe());
			if(mGetStuNoEvent.isRequestSuccess()) {
				 String name = mGetStuNoEvent.getName();
				 String stuNo = mGetStuNoEvent.getStuNo();
//				 findStuNoLayout.setVisibility(View.GONE);
//				 findStuNoContentLayout.setVisibility(View.VISIBLE);
//				 stuNoContent.setText(name + ",您好!\r\n您的学号是:" + stuNo);
			}
		}
	}
	
	private void onLoginSuccess(){
		requestGetUserInfoWithId(LocalInfoManager.getInstance().getmLocalInfo().getUserId(), true);
	}
	
	
	@Override
	protected void onHandleGetUserEventRunEnd(GetUserInfoEvent getUserInfoEvent) {
		if(getUserInfoEvent.isRequestSuccess()){
			onLoginSuccessGetUserInfo(getUserInfoEvent);
			AndroidEventManager.getInstance().postEvent(EventCode.HP_LoginGetOrChangeInfoSuccess, 0);
			//MainActivity.launch(this);
			finish();
		}else {
			mToastManager.show(R.string.get_user_info_failed);
		}
	}
	
	

	
}
