package com.zdht.jingli.groups.ui;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class EditSignatureActivity extends SCBaseActivity implements OnClickListener {

	private final static String EXTRA_SIGNATURE = "Signature";
	
	private EditText mEditSignature;
	private Button mButtonSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mButtonSave = addButtonInTitleRight();
		mButtonSave.setText(R.string.save);
		mButtonSave.setOnClickListener(this);
		mEditSignature = (EditText)findViewById(R.id.etSignature);
		mEditSignature.setText(getIntent().getStringExtra(EXTRA_SIGNATURE));
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
		ba.mTitleTextStringId = R.string.my_signature;
	}
	
	
	public static void launchForResult(android.app.Activity activity, int nRequestCode, String signature) {
		Intent intent = new Intent(activity, EditSignatureActivity.class);
		intent.putExtra(EXTRA_SIGNATURE, signature);
		activity.startActivityForResult(intent, nRequestCode);
	}

	@Override
	public void onClick(View v) {
		commitInfo(null, null, mEditSignature.getEditableText().toString() == null ? "" : mEditSignature.getEditableText().toString());
	}
	
	@Override
	protected void OnEditInfoSuccess() {
		LocalInfoManager.getInstance().setmSignature(mEditSignature.getEditableText().toString());
		setResult(RESULT_OK);
		finish();
	}
	
}
