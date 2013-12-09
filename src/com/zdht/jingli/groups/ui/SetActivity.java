package com.zdht.jingli.groups.ui;


import com.zdht.jingli.R;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.utils.DialogUtils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetActivity extends SCBaseActivity implements OnClickListener {

	private TextView mTextViewMy_name;
	private TextView mTextViewNotificationCycle;
	private View mViewNotificationCycle;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnLogin_out).setOnClickListener(this);
		findViewById(R.id.vPersonal_information).setOnClickListener(this);
		findViewById(R.id.vManage_group).setOnClickListener(this);
		
//		registerForContextMenu(mViewNotificationCycle);
		findViewById(R.id.vFeedback).setOnClickListener(this);
		findViewById(R.id.vAbout_us).setOnClickListener(this);
		mTextViewMy_name = (TextView)findViewById(R.id.tvMy_name);
//		mTextViewMy_name.setText(LocalInfoManager.getInstance().getmLocalInfo().getRealName());
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.set;
	}

	@Override
	public void onClick(View v) {
		int nId = v.getId();
		if(nId == R.id.vPersonal_information){
			PersonalInfoActivity.launch(this);
			return;
		}

//		if(nId == R.id.vMy_activities){
//			ActivityListActivity.launch(this, false, 0);
//			return;
//		}
//		if(nId == R.id.vManage_group){
//			ManagerGroupActivity.launch(this);
//			return;
//		}
//		if(nId == R.id.vManage_activities){
//			ManagerActivitiesActivity.launch(this);
//			return;
//		}
//		if(nId == R.id.vActivity_notification){
//			v.showContextMenu();
//			return;
//		}
//		if(nId == R.id.vSet_more){
//			AppSetActivity.launch(this);
//			return;
//		}
//		if(nId == R.id.vFeedback){
//			FeedbackActivity.launch(this);
//			return;
//		}
//		if(nId == R.id.vAbout_us){
//			AboutUsActivity.launch(this);
//			return;
//		}
		if(nId == R.id.btnLogin_out){
			DialogUtils.showAlertDialog(this, R.string.set_logout_alert_title,
					R.string.set_logout_alert_msg, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							SCApplication.loginOut();
							LoginActivity.launch(SetActivity.this);
						}
					});
			return;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.clear();
		int i = 0;
		menu.add(0, i++, i++, "提前两小时");
		menu.add(0, i++, i++, "提前一天");
		menu.add(0, i++, i++, "提前两天");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		mTextViewNotificationCycle.setText(item.getTitle().toString());
		return true;
	}
	
}
