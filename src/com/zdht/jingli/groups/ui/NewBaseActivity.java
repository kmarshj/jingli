package com.zdht.jingli.groups.ui;

import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.utils.SystemUtils;
import com.zdht.utils.ToastManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class NewBaseActivity extends Activity {
	
	protected ProgressDialog mProgressDialog;

	protected ToastManager 		mToastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mToastManager = ToastManager.getInstance(getApplicationContext());
	}
	
	protected void showProgressDialog(int strTitleId,int nStringId){
		showProgressDialog(getString(strTitleId), getString(nStringId));
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
	
	protected boolean isLocalUser(String uId){
		if(LocalInfoManager.getInstance().isLocalUser(uId)){
			PersonalInfoActivity.launch(this);
			return true;
		}
		return false;
	}
	
	protected boolean isLocalUser(int uId){
		return isLocalUser(String.valueOf(uId));
	}
}
