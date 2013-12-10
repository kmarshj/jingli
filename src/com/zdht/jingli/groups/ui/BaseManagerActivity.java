package com.zdht.jingli.groups.ui;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BaseManagerActivity extends SCBaseActivity implements OnClickListener,
																   OnItemClickListener{

	
	protected Button mButtonBuild;
	protected ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_activity);
		mButtonBuild = addButtonInTitleRight();
		mButtonBuild.setText(R.string.build);
		mButtonBuild.setOnClickListener(this);
		mListView = (ListView)findViewById(R.id.lv);
		mListView.setOnItemClickListener(this);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mSetContentView = false;
		ba.mAddBackButton = true;
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndroidEventManager.getInstance().removeEventListener(EventCode.SC_DownloadImage, this);
	}
	
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onClick(View v) {
	}

}
