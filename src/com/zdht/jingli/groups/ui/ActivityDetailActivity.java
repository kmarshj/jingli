package com.zdht.jingli.groups.ui;


import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.ActivityImagesPageAdapter;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.QuitAndAddActivityEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.utils.SystemUtils;

/**
 * 活动详情
 * @author think
 *
 */
public class ActivityDetailActivity extends SCBaseActivity implements ViewPager.OnPageChangeListener,
																	  View.OnTouchListener,
																	  OnClickListener{

	/** 图片展示墙 */
	private ViewPager mViewPagerActivityImages;
	/** 图片展示墙adapter */
	private ActivityImagesPageAdapter mActivityImagesPageAdapter;
	/** 手势监控 */
	private GestureDetector	mGestureDetector;
	/** 图片地址列表 */
	private List<String> mListActivituImageUrl;
	/**  */
	private RadioGroup mRadioGroup;
	private View mViewInputPhone;
	private View mViewClick;
	private EditText mEditTextPhone;
	private EditText mEditTextRemark;
	private Button mButtonAddActivity;
	private int addStatus;
	private int whichBuilderComfirmClick;
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (Activity)getIntent().getSerializableExtra(ActivityMainActivity.EXTRA_ACTIVITY);
		((TextView)findViewById(R.id.tvActivity_name)).setText(activity.getName());
		((TextView)findViewById(R.id.tvHost)).setText(activity.getGroupName());
		((TextView)findViewById(R.id.tvActivity_time)).setText(activity.getFromTime() + "至" + activity.getEndTime());
		((TextView)findViewById(R.id.tvActivity_site)).setText(activity.getSite());
		// 活动详情
		((TextView)findViewById(R.id.tvActivity_details)).setText(Html.fromHtml(activity.getDetails()));
		((TextView)findViewById(R.id.tvPrompt_activity_name)).setText(activity.getName());
		mListActivituImageUrl = activity.getListImage();
		mActivityImagesPageAdapter = new ActivityImagesPageAdapter(this);
		mActivityImagesPageAdapter.replaceAll(mListActivituImageUrl);
		mViewPagerActivityImages = (ViewPager)findViewById(R.id.vp);
		mRadioGroup = (RadioGroup)findViewById(R.id.ragActivity_image_mark);
		initAd(mRadioGroup);
		mViewPagerActivityImages.setOnPageChangeListener(this);
		mViewPagerActivityImages.setOnTouchListener(this);
		mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				final int nPos = mViewPagerActivityImages.getCurrentItem();
				if(mActivityImagesPageAdapter.isImageDownloadFail(nPos)){
					mActivityImagesPageAdapter.requestDownloadImage(nPos);
				}
				return super.onSingleTapUp(e);
			}
		});
		mViewPagerActivityImages.setAdapter(mActivityImagesPageAdapter);
		mButtonAddActivity = (Button)findViewById(R.id.btnAdd_activity);
		mButtonAddActivity.setOnClickListener(this);
		addStatus = activity.isIsAdd();
		setTheAddButton(addStatus);
		mViewInputPhone = findViewById(R.id.vRegistration_form);
		mViewClick = findViewById(R.id.viewClick);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		findViewById(R.id.btnConfirm).setOnClickListener(this);
		mEditTextPhone = (EditText)findViewById(R.id.etContact_phone);
		mEditTextRemark = (EditText)findViewById(R.id.etRemark);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mHasTitle = false;
	}

	@Override
	public void onPageScrollStateChanged(int nState) {
		if(nState == ViewPager.SCROLL_STATE_DRAGGING){
			mViewPagerActivityImages.requestDisallowInterceptTouchEvent(true);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		mRadioGroup.check(position % mListActivituImageUrl.size());
	}

	private Runnable mRunnableAutoSlide = new Runnable() {
		@Override
		public void run() {
			final int nCurrentItem = mViewPagerActivityImages.getCurrentItem();
			if(nCurrentItem >= mActivityImagesPageAdapter.getCount() - 1){
				mViewPagerActivityImages.setCurrentItem(0, true);
			}else{
				mViewPagerActivityImages.setCurrentItem(nCurrentItem + 1, true);
			}
			
			SCApplication.getMainThreadHandler().postDelayed(mRunnableAutoSlide, 5000);
		}
	};
	
	public void startAutoSlide(){
		SCApplication.getMainThreadHandler().removeCallbacks(mRunnableAutoSlide);
		SCApplication.getMainThreadHandler().postDelayed(mRunnableAutoSlide, 5000);
	}
	
	public void stopAutoSlide(){
		SCApplication.getMainThreadHandler().removeCallbacks(mRunnableAutoSlide);
	}
	
	
	private void initAd(RadioGroup radioGroup){
		radioGroup.removeAllViews();
		for(int i=0;i<mListActivituImageUrl.size();i++){
			RadioButton rb = new RadioButton(this);
			rb.setId(i);
			rb.setButtonDrawable(R.drawable.selector_activity_image_point);
			rb.setClickable(false);
			radioGroup.addView(rb, SystemUtils.dipToPixel(this, 15), SystemUtils.dipToPixel(this, 15));
		}
		if(mListActivituImageUrl.size() > 0){
			radioGroup.check(-1);
			radioGroup.check(0);
		}
	}
	
	private void closeTheInputPhoneWindow(){
		mViewInputPhone.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_middle_close));
		mViewInputPhone.setVisibility(View.GONE);
		mEditTextRemark.setText("");
		mViewClick.setVisibility(View.GONE);
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		final int nAction = event.getAction();
		if(nAction == MotionEvent.ACTION_DOWN){
			stopAutoSlide();
		}else if(nAction != MotionEvent.ACTION_MOVE){
			startAutoSlide();
		}
		return false;
	}
	
	@Override
	protected void onBuilderPositionBtnClick(DialogInterface dialog, int which) {
		super.onBuilderPositionBtnClick(dialog, which);
		if(whichBuilderComfirmClick == 1){
			showProgressDialog(null, R.string.submiting);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_QuitActivity, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_QuitActivity, 0,
					String.format(URLUtils.URL_QuitActivity, activity.getId()), activity);
		}else {
//			mToastManager.show("待补充删除活动功能");

			showProgressDialog(null, R.string.submiting);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_DelActivity, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_DelActivity, 0,
					String.format(URLUtils.URL_DelActivity, activity.getId()), activity);
		}
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		if(nId == R.id.btnAdd_activity){
			
			if(addStatus == 0){
				mViewInputPhone.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_middle_open));
				mViewInputPhone.setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.tvUser_name)).setText(getString(R.string.applicants) + LocalInfoManager.getInstance().getmLocalInfo().getRealName());
				mEditTextPhone.setText(LocalInfoManager.getInstance().getmPhone());
				mViewClick.setVisibility(View.VISIBLE);
			}else if(addStatus == 1) {
				whichBuilderComfirmClick = 1;
				showAlertDialog(R.string.builder_quit_activity, R.string.confirm, R.string.cancle);
			}else {
				whichBuilderComfirmClick = 2;
				showAlertDialog(R.string.builder_delet_activity, R.string.confirm, R.string.cancle);
			}
			
			return;
		}
		if(nId == R.id.btnCancel){
			closeTheInputPhoneWindow();
			return;
		}
		if(nId == R.id.btnConfirm){
			final String strPhone = mEditTextPhone.getEditableText().toString();
			if(!SystemUtils.isMobile(strPhone)){
				mToastManager.show(R.string.prompt_input_phone);
				return;
			}
			final String strRemark = mEditTextRemark.getEditableText().toString() == null ? "" : mEditTextRemark.getEditableText().toString();
			showProgressDialog(null, R.string.submiting);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_AddActivity, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_AddActivity, 0,
					String.format(URLUtils.URL_AddActivity, activity.getId(), strPhone, strRemark), activity);
			return;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mViewInputPhone.getVisibility() == View.VISIBLE){
			closeTheInputPhoneWindow();
			return;
		}
		super.onBackPressed();
	}
	
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPGET_QuitActivity){
			QuitAndAddActivityEvent baseInfoGetEvent = (QuitAndAddActivityEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				addStatus = 0;
				activity.setIsAdd(addStatus);
				setTheAddButton(addStatus);
			}
		}else if(nCode == EventCode.HTTPGET_AddActivity){
			QuitAndAddActivityEvent baseInfoGetEvent = (QuitAndAddActivityEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				addStatus = 1;
				activity.setIsAdd(addStatus);
				setTheAddButton(addStatus);
				closeTheInputPhoneWindow();
			}
		} else if(nCode == EventCode.HTTPGET_DelActivity){
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				getMyCreateActivity();
				android.app.Activity mActivity = getParent();
				if(mActivity instanceof ActivityMainActivity) {
					((ActivityMainActivity)mActivity).deleteActivity(activity);
				}
			}
		}
	}
	
	private void setTheAddButton(int status){
		if(status == 0){
			mButtonAddActivity.setText(R.string.add_activity);
		}else if(status == 1){
			mButtonAddActivity.setText(R.string.quit_activity);
		}else {
			mButtonAddActivity.setText(R.string.delete_activity);
		}
	}
	
	
}
