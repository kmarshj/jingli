package com.zdht.jingli.groups.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.utils.ToastManager;

public class EditPasswordActivity extends SCBaseActivity implements OnClickListener {

	
	
	private EditText mEditPWD;
	private EditText mEditTextRePWD;
	private Button mButtonSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mButtonSave = addButtonInTitleRight();
		mButtonSave.setText(R.string.save);
		mButtonSave.setOnClickListener(this);
		mEditPWD = (EditText)findViewById(R.id.etNew_password);
		mEditTextRePWD = (EditText)findViewById(R.id.etConfirm_password);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
		ba.mTitleTextStringId = R.string.password;
	}
	
	
	public static void launch(android.app.Activity activity) {
		Intent intent = new Intent(activity, EditPasswordActivity.class);
		activity.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		final String strNewPassword = mEditPWD.getEditableText().toString();
		
		final String strConfirmPassword = mEditTextRePWD.getEditableText().toString();
		
		if(!TextUtils.isEmpty(strNewPassword)){
			if(strNewPassword.length() < 6){
				mToastManager.show(R.string.prompt_password_to_short);
				return;
			}
			if(!strConfirmPassword.equals(strNewPassword)){
				mToastManager.show(R.string.prompt_passwords_not_match);
				return;
			}
		}else {
			mToastManager.show(R.string.prompt_input_password);
			return;
		}

		if(SCApplication.isIMConnectionSuccess()) { 
			// IM服务连接上才允许修改密码.避免业务系统的密码修改了, 而IM系统的密码没有修改.
			// 修改密码
			commitInfo(null, strNewPassword, null);
		} else {
			ToastManager.getInstance(this).show("XMPP server is disconnected!");
		}
	}
	
	@Override
	protected void OnEditInfoSuccess() {
		LocalInfoManager.getInstance().editPassWord(mEditPWD.getEditableText().toString());
		finish();
	}
	
	
}
