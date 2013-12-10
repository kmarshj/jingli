package com.zdht.jingli.groups.ui;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.utils.SystemUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditPhoneActivity extends SCBaseActivity implements OnClickListener {

	private final static String EXTRA_PHONE = "phone";
	
	private EditText mEditTextPhone;
	private Button mButtonSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mButtonSave = addButtonInTitleRight();
		mButtonSave.setText(R.string.save);
		mButtonSave.setOnClickListener(this);
		mEditTextPhone = (EditText)findViewById(R.id.etPhone);
		mEditTextPhone.setText(getIntent().getStringExtra(EXTRA_PHONE));
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
		ba.mTitleTextStringId = R.string.phone;
	}
	
	
	public static void launchForResult(android.app.Activity activity, int nRequestCode, String phone) {
		Intent intent = new Intent(activity, EditPhoneActivity.class);
		intent.putExtra(EXTRA_PHONE, phone);
		activity.startActivityForResult(intent, nRequestCode);
	}

	@Override
	public void onClick(View v) {
		final String strPhone = mEditTextPhone.getEditableText().toString();
		if(!SystemUtils.isMobile(strPhone)){
			mToastManager.show(R.string.prompt_input_phone);
			return;
		}
		commitInfo(strPhone, "", "");
	}
	
	@Override
	protected void OnEditInfoSuccess() {
		LocalInfoManager.getInstance().setmPhone(mEditTextPhone.getEditableText().toString());
		setResult(RESULT_OK);
		finish();
	}
	
}
