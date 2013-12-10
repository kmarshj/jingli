package com.zdht.jingli.groups.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.adapter.SectionAdapter;
import com.zdht.view.PullToRefreshListView;
import com.zdht.view.PulldownableListView;

public class SCPullDownRefreshActivity extends SCBaseActivity implements 
						PulldownableListView.OnPullDownListener,
						AdapterView.OnItemClickListener{
	
	protected boolean mCreateRefresh = true;
	
	protected PullToRefreshListView mListView;
	
	protected SectionAdapter mSectionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mListView.endRun();
		mListView.setAdapter(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		mListView = (PullToRefreshListView)findViewById(R.id.lv);
//		mListView.loadMoreView.setVisibility(View.GONE);
		mListView.setDivider(null);
		mListView.setDividerHeight(10);
		mListView.setFadingEdgeLength(0);
		mListView.setOnPullDownListener(this);
		mListView.setOnItemClickListener(this);
		if(mCreateRefresh){
			mListView.post(new Runnable() {
				@Override
				public void run() {
					mListView.startRun();
				}
			});
		}
	}

	@Override
	public void onStartRun(PulldownableListView view) {
	}

	@Override
	public void onEndRun(PulldownableListView view) {
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}
	
	protected class endPullDownRunnable implements Runnable{
		@Override
		public void run() {
			mListView.endRun();
		}
	}
}
