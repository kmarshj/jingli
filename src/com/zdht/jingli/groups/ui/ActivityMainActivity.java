package com.zdht.jingli.groups.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.zdht.jingli.R;
import com.zdht.utils.SystemUtils;

/**
 * 活动主页
 * @author think
 *
 */
public class ActivityMainActivity extends TabActivity implements OnClickListener, OnFocusChangeListener  {

	public final static String EXTRA_ACTIVITY = "activity";
	
	protected RelativeLayout 	mRelativeLayoutTitle;
	
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitymainactivity);
		mRelativeLayoutTitle = (RelativeLayout)findViewById(R.id.viewTitle);
		addTextInTitle();
		mRelativeLayoutTitle.addView(onCreateBackButton(),onCreateBackButtonLayoutParams());
		addTab(ActivityDetailActivity.class.getName(), ActivityDetailActivity.class, R.string.detail);
		addTab(ActivityLiveActivity.class.getName(), ActivityLiveActivity.class, R.string.live);
		addTab(ActivityMemberActivity.class.getName(), ActivityMemberActivity.class, R.string.member);
		addTab(GroupHomeActivity.class.getName(), GroupHomeActivity.class, R.string.organization);
	}
	
	private void 	addTextInTitle(){
//		String aa = getIntent().getStringExtra("aa");  
		title = onCreateTitleTextView(((com.zdht.jingli.groups.model.Activity)getIntent().getSerializableExtra(EXTRA_ACTIVITY)).getName());
		title.setOnFocusChangeListener(this);
		mRelativeLayoutTitle.addView(title, onCreateTitleTextViewLayoutParams());
	}
	
	private TextView	onCreateTitleTextView(String str){
		final TextView textView = (TextView)LayoutInflater.from(this)
				.inflate(R.layout.textview_title, null);
		textView.setText(str);
		return textView;
	}
	
	private RelativeLayout.LayoutParams onCreateTitleTextViewLayoutParams(){
		return new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
	}
	
	private Button onCreateBackButton(){
		final Button btnBack = (Button)(LayoutInflater.from(this)
				.inflate(R.layout.btn_title_left_back, null));
		btnBack.setOnClickListener(this);
		return btnBack;
	}
	
	protected RelativeLayout.LayoutParams onCreateBackButtonLayoutParams(){
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				SystemUtils.dipToPixel(this, 30));
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.leftMargin = SystemUtils.dipToPixel(this, 2);
		return lp;
	}
	
	public static void launch(Activity activity, com.zdht.jingli.groups.model.Activity mActivity){
		Intent intent = new Intent(activity, ActivityMainActivity.class);
		intent.putExtra(EXTRA_ACTIVITY, mActivity);
		activity.startActivity(intent);
	}
	public static void launchForResult(Activity activity, com.zdht.jingli.groups.model.Activity activity2) {
		Intent intent = new Intent(activity, ActivityMainActivity.class);
		intent.putExtra(EXTRA_ACTIVITY, activity2);
		activity.startActivityForResult(intent, CreateOrEditActivityActivity.REQUEST_CODE_FOR_EDIT);
	}

	@Override
	public void onClick(View v) {
		onBackPressed();
		finish();
	}
	
	private void addTab(String strTag,Class<?> cls,int nStringId){
		final TabHost tabHost = getTabHost();
		final TabSpec tabSpec = tabHost.newTabSpec(strTag);
		TextView textView = (TextView)LayoutInflater.from(this).inflate(R.layout.textview_top_tab_item, null);
		textView.setBackgroundResource(R.drawable.selector_btn_activity_main_tab_item);
		textView.setText(nStringId);
		Intent intent = new Intent(this,cls);
		intent.putExtra(EXTRA_ACTIVITY, getIntent().getSerializableExtra(EXTRA_ACTIVITY));
		tabSpec.setContent(intent).setIndicator(textView);
		tabHost.addTab(tabSpec);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		//SCApplication.print( "title hasFocus:" + hasFocus);
		if(!hasFocus) {
			title.requestFocus();
			//SCApplication.print( "title requestFocus:");
		}
	}
	
	public void deleteActivity(com.zdht.jingli.groups.model.Activity activity) {
		Intent intent = new Intent();
		intent.putExtra(CreateOrEditActivityActivity.EXTRA_ACTIVITY, activity);
		intent.putExtra(CreateOrEditActivityActivity.RESULT_IS_DELETE, true);
		setResult(RESULT_OK, intent);
		finish();
	}
}
