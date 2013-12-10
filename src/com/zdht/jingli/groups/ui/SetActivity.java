package com.zdht.jingli.groups.ui;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.SCApplication;
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

	private TextView mTextViewNotificationCycle;
	private TextView tvMy_name;
	int acNum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnLogin_out).setOnClickListener(this);
		findViewById(R.id.vPersonal_information).setOnClickListener(this);
		findViewById(R.id.vMy_activities).setOnClickListener(this);
		findViewById(R.id.vFeedback).setOnClickListener(this);
		findViewById(R.id.vAbout_us).setOnClickListener(this);
		initView();

	}

	public void initView() {
		tvMy_name = (TextView) findViewById(R.id.tvMy_name);

		if (acNum == 0) {
			tvMy_name.setText("个人信息");
		} else {
			tvMy_name.setText("商家信息");
		}

	}

	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.set;
	}

	@Override
	public void onClick(View v) {
		int nId = v.getId();
		if (nId == R.id.vPersonal_information) {
			PersonalInfoActivity.launch(this);
			return;
		}

		if (nId == R.id.vMy_activities) {
			if (acNum == 0) {
				ManagerActivitiesActivity.launch(this);
			} else {
				ActivityListActivity.launch(this, false, 0);
			}

			return;
		}

		if (nId == R.id.vFeedback) {
			FeedbackActivity.launch(this);
			return;
		}
		if (nId == R.id.vAbout_us) {
			AboutUsActivity.launch(this);
			return;
		}
		if (nId == R.id.btnLogin_out) {
			DialogUtils.showAlertDialog(this, R.string.set_logout_alert_title,
					R.string.set_logout_alert_msg,
					new DialogInterface.OnClickListener() {
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

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// menu.clear();
	// int i = 0;
	// menu.add(0, i++, i++, "提前两小时");
	// menu.add(0, i++, i++, "提前一天");
	// menu.add(0, i++, i++, "提前两天");
	// }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		mTextViewNotificationCycle.setText(item.getTitle().toString());
		return true;
	}

}
