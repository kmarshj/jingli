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
 * 登录页面
 * @author think
 *
 */
public class LoginActivity extends SCBaseActivity implements OnClickListener,
                                                             OnEventListener{
	private EditText et_username;
	private EditText et_userpassword;
	private TextView tv_forgetpsd;
	private CheckBox cb_savepsd;
	private String mstrusername;
	private int mRequestCodeAccountManagement;
	
	/** 查询学号 / 找回密码 */
	private EditText findStuNoEdit;
	private LinearLayout findStuNoLayout;
	/** 显示学号 */
	private LinearLayout findStuNoContentLayout;
	private TextView stuNoContent;
	
	private TextView login_find_pd_edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		et_username = (EditText)findViewById(R.id.et_username);
		et_username.setText(SharedPreferenceManager
        		.getSharedPreferences(this)
        		.getString(SharedPreferenceManager.KEY_STUDENTID, null));
		et_userpassword = (EditText)findViewById(R.id.et_userpassword);
		
		cb_savepsd = (CheckBox) findViewById(R.id.cb_savepsd);
		tv_forgetpsd = (TextView)findViewById(R.id.tv_forgetpsd);
		tv_forgetpsd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_forgetpsd.setOnClickListener(this);

		findViewById(R.id.rl_register).setOnClickListener(this);
		findViewById(R.id.rl_login).setOnClickListener(this);
		
		AndroidEventManager.getInstance().postEvent(
				new CallbackEvent(EventCode.HP_LoginActivityCreated), 0);
		

	}
	
	public void hideSystemKeyBoard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		//得到InputMethodManager的实例 
		if (imm.isActive()) { 
			//如果开启 
			imm.hideSoftInputFromWindow(et_username.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(et_userpassword.getWindowToken(), 0);
		}
	 }
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.login;
	}
	

	private void closeGetverificationWindow(){
//		mViewGetVerication.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_middle_close));
//		mViewGetVerication.setVisibility(View.GONE);
	}
	

	@Override
	public void onClick(View v) {
		int nId = v.getId();
//		if(nId == R.id.ibClose_get_verification_window){
//			closeGetverificationWindow();
//			return;
//		}
		
		final String strusername = et_username.getEditableText().toString();
		
		switch(nId) {
		case R.id.rl_login:
			if(!checkUserName(strusername)){
				return;
			}
			
			final String strPassword  = et_userpassword.getEditableText().toString();
			if (TextUtils.isEmpty(strPassword)) {
				mToastManager.show(R.string.login_userpassword);
				return;
			}
			
			final List<NameValuePair> mListNameValuePair = new ArrayList<NameValuePair>();
			mListNameValuePair.add(new BasicNameValuePair("appId", URLUtils.KEY));
			mListNameValuePair.add(new BasicNameValuePair("mode", "0"));
			mListNameValuePair.add(new BasicNameValuePair("userName", strPassword));
			mListNameValuePair.add(new BasicNameValuePair("password", strPassword));
			mListNameValuePair.add(new BasicNameValuePair("audit", "1"));
			mListNameValuePair.add(new BasicNameValuePair("count", "1"));
			showProgressDialog("", R.string.logining);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_Login, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_Login,0,
							URLUtils.URL_Login, 
							mListNameValuePair);
			break;
		case R.id.tv_forgetpsd:// 忘记密码按钮
			if(!checkUserName(strusername)){
				return;
			}
			
//			mViewGetVerication.setAnimation(AnimationUtils.loadAnimation(this,
//					R.anim.anim_middle_open));
//			mViewGetVerication.setVisibility(View.VISIBLE);
//			login_find_pd_edit.setText("");
			break;
//		case R.id.login_find_pd_commit: // 查询密码
//			String idCardForPd = login_find_pd_edit.getText().toString();
//            //判断用户输入是否为身份证号  
//			
//            if(checkIdCard(idCardForPd)){
//    			showProgressDialog(null, R.string.loading);
//            	AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_FindPassword, this, true);
//    			final List<NameValuePair> mPdListNameValuePair = new ArrayList<NameValuePair>();
//    			mPdListNameValuePair.add(new BasicNameValuePair("appId", URLUtils.KEY));
//    			mPdListNameValuePair.add(new BasicNameValuePair("userName", mStrStudentId));
//    			mPdListNameValuePair.add(new BasicNameValuePair("identity", idCardForPd.toUpperCase(Locale.CHINA)));
//    			AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_FindPassword, 0,
//    							URLUtils.URL_FindPassword, 
//    							mPdListNameValuePair);
//            } else {
//            	ToastManager.getInstance(this).show("请输入正确的身份证号码");
//            }
//			break;
		}
	}
	
	/**
	 * 验证身份证是否符合规范
	 * @param idCard
	 * @return
	 */
	private boolean checkIdCard(String idCard) {
		//定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）  
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");  
        //通过Pattern获得Matcher  
        Matcher idNumMatcher = idNumPattern.matcher(idCard);
        return idNumMatcher.matches();
	}
	
	private boolean checkUserName(String strusername) {
		if (TextUtils.isEmpty(strusername)) {
			mToastManager.show(R.string.login_username);
			return false;
		}
//		if(strStudentId.length() != 13){
//			mToastManager.show(R.string.prompt_student_id_error);
//			return false;
//		}
		mstrusername = strusername;
		return true;
	}

	public static void launch(Activity activity) {
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
	}
	
//	@Override
//	public void onBackPressed() {
//		if(mViewGetVerication.getVisibility() == View.VISIBLE){
//			closeGetverificationWindow();
//			return;
//		}
//		super.onBackPressed();
//	}

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
					if(mRequestCodeAccountManagement == 0){
						mRequestCodeAccountManagement = generateRequestCode();
					}
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
		 	if(baseInfoEvent.isRequestSuccess()){
		 		closeGetverificationWindow();
		 		stuNoContent.setText("    " + baseInfoEvent.getNickname() + 
		 				",您好\r\n    您的密码是:" + baseInfoEvent.getPd() + 
		 				"\r\n\r\n\r\n小贴士:为确保账户安全,请定期修改您的密码.");
		 		
		 		findStuNoContentLayout.setVisibility(View.VISIBLE);
		 	}
		} else if(nCode == EventCode.HTTPGET_GetStuNo) {
			GetStuNoEvent mGetStuNoEvent = (GetStuNoEvent)event;
			mToastManager.show(mGetStuNoEvent.getDescribe());
			if(mGetStuNoEvent.isRequestSuccess()) {
				 String name = mGetStuNoEvent.getName();
				 String stuNo = mGetStuNoEvent.getStuNo();
				 findStuNoLayout.setVisibility(View.GONE);
				 findStuNoContentLayout.setVisibility(View.VISIBLE);
				 stuNoContent.setText(name + ",您好!\r\n您的学号是:" + stuNo);
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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == mRequestCodeAccountManagement){
				finish();
			}
		}
	}
	
}
