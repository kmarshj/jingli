package com.zdht.jingli.groups.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.BaseInfoPostEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Group;

/**
 * 新建或编辑活动
 * @author think
 *
 */
@SuppressLint("SimpleDateFormat")
public class CreateOrEditActivityActivity extends SCBaseActivity implements OnClickListener{

	public final static String EXTRA_ACTIVITY = "avtivity";
	
	public final static int REQUEST_CODE_FOR_EDIT = 11;
	public final static int REQUEST_ACTIVITY_MEMBER = 12;
	
	public final static String RESULT_IS_DELETE = "isdelete";

	private EditText mEditTextName;
	private EditText mEditTextSite;
	private EditText mEditTextActivityDetails;
	private EditText mEditTextLimitNum;
	private TextView mTextViewFromTime;
	private TextView mTextViewEndTime;
	private Spinner mSpinnerHostGroup;
	private int mDialogIdSetFromTime;
	private int mDialogIdSetEndTime;
	private int mDialogIdSetTimePoint;
	private Calendar cal;
	private String mStrFT_Y_M_D;
	private String mStrFT_H_M_S;
	private String mStrFromTime;
	private String mStrET_Y_M_D;
	private String mStrET_H_M_S;
	private String mStrEndTime;
	private boolean isSeletFromTime;
	
	private ArrayAdapter<String> adapter;
	private List<Group> mListMyCreateGroup;
	
	private boolean isEditActivity;
	
	private com.zdht.jingli.groups.model.Activity activity;
	private int mRequestCodeUploadActivityImg;
	
	private int whichBuilderComfirmClick;
	
	/** 图片地址， 多个图片以逗号隔开 */
	private String images;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEditTextName = (EditText) findViewById(R.id.etActivity_name);
		mEditTextSite = (EditText) findViewById(R.id.etSite);
		mEditTextActivityDetails = (EditText) findViewById(R.id.etActivity_details);
		mEditTextLimitNum = (EditText) findViewById(R.id.etMember_num);
		mTextViewFromTime = (TextView) findViewById(R.id.tvFrom_time);
		mTextViewEndTime = (TextView) findViewById(R.id.tvEnd_time);
		mSpinnerHostGroup = (Spinner)findViewById(R.id.spHost_group);
		// 顶部右边提交按钮
		Button mButtonSubmit = addButtonInTitleRight();
		mButtonSubmit.setText(R.string.submit);
		mButtonSubmit.setOnClickListener(this);
		
		findViewById(R.id.btnUpload_image).setOnClickListener(this);
		mTextViewFromTime.setOnClickListener(this);
		mTextViewEndTime.setOnClickListener(this);
		activity = (com.zdht.jingli.groups.model.Activity) getIntent()
				.getSerializableExtra(EXTRA_ACTIVITY);
		if (activity != null) {
			List<String> imageList = activity.getListImage();
			for (int i = 0; i < imageList.size(); i++) {
				images += imageList.get(i) + ",";
			}
			if(images.length() > 0){
				images = images.substring(0, images.length() - 1);
			}
			
			isEditActivity = true;
			mEditTextName.setFocusable(false);
			Button delete = (Button)findViewById(R.id.manager_activity_delete);
			delete.setVisibility(View.VISIBLE);
			delete.setOnClickListener(this);
			
			TextView members = (TextView)findViewById(R.id.manage_activity_member);
			members.setText("成员:" + activity.getNowNumber() + "人" );
			members.setVisibility(View.VISIBLE);
			members.setOnClickListener(this);
			
			mTextViewTitle.setText(R.string.edit_activity);
			mEditTextName.setText(activity.getName());
			
			mEditTextSite.setText(activity.getSite());
			mEditTextActivityDetails.setText(activity.getDetails());
			mEditTextLimitNum.setText(activity.getTotalNumber());
			// 开始时间
			final String startTime = activity.getFromTime(); 
			if(!TextUtils.isEmpty(startTime) && startTime.length() > 12) {
				mTextViewFromTime.setText(startTime);
				// 开始时间年月日
				mStrFT_Y_M_D = startTime.substring(0, 10);
				// 开始时间时分秒
				mStrFT_H_M_S = " " + startTime.substring(11);
			}
			// 结束时间
			final String endTime = activity.getEndTime();
			if(!TextUtils.isEmpty(endTime) && endTime.length() > 12) {
				mTextViewEndTime.setText(endTime);
				// 结束时间年月日
				mStrET_Y_M_D = endTime.substring(0, 10);
				// 结束时间时分秒
				mStrET_H_M_S = " " + endTime.substring(11);
			}
		} else {
			isEditActivity = false;
		}
		cal = Calendar.getInstance();
		final List<Group> groups = LocalInfoManager.getInstance().getListMyCreateGroup();
		if(groups == null || groups.size() == 0){
			getMyCreateGroup();
		}else {
			onMyCreateGroupGetSuccess(LocalInfoManager.getInstance().getListMyCreateGroup());
		}
	}
	
	
	@Override
	protected void onMyCreateGroupGetSuccess(List<Group> list) {
		super.onMyCreateGroupGetSuccess(list);
		mListMyCreateGroup = list;
		final int index = mListMyCreateGroup.size();
		final String[] groupnames = new String[index];
		for (int i = 0; i < index; i++) {
			groupnames[i] = mListMyCreateGroup.get(i).getName();
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupnames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);       
		mSpinnerHostGroup.setAdapter(adapter);
	}
	
	@Override
	protected void onMyCreateGroupGetFail() {
		super.onMyCreateGroupGetFail();
		mToastManager.show(R.string.no_create_group_can_not_create_activity);
		finish();
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.create_activity;
		ba.mAddBackButton = true;
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		switch(nId) {
		case  R.id.btn_title_right: // 标题栏右侧按钮， 提交
			createActivity(images);
			break;
		case R.id.tvFrom_time:// 开始时间
			if (mDialogIdSetFromTime == 0) {
				mDialogIdSetFromTime = generateDialogId();
			}
			mStrFromTime = "";
			isSeletFromTime = true;
			showDialog(mDialogIdSetFromTime);
			break;
		case R.id.tvEnd_time: // 结束时间
			if (mDialogIdSetEndTime == 0) {
				mDialogIdSetEndTime = generateDialogId();
			}
			mStrEndTime = "";
			isSeletFromTime = false;
			showDialog(mDialogIdSetEndTime);
			break;
		case R.id.btnUpload_image: // 上传图片
			
			if(mRequestCodeUploadActivityImg == 0){
				mRequestCodeUploadActivityImg = generateRequestCode();
			}
			UploadActivityImgActivity.launchForResult(this, mRequestCodeUploadActivityImg);
			break;
		case R.id.manager_activity_delete: // 删除活动
			whichBuilderComfirmClick = 1;
			showAlertDialog(R.string.builder_delet_activity, R.string.confirm, R.string.cancle);
			break;
		case R.id.manage_activity_member: // 管理活动成员
			Intent intent = new Intent(this,ActivityMemberActivity.class);
			intent.putExtra(ActivityMainActivity.EXTRA_ACTIVITY, activity);
			intent.putExtra(ActivityMemberActivity.EXTRA_IS_EDIT, true);
			startActivityForResult(intent, REQUEST_ACTIVITY_MEMBER);
			break;
		}
	}

	private void createActivity(String images){
		// 获取并检测活动名称
		final String strActivityName = mEditTextName.getEditableText().toString();
		if(TextUtils.isEmpty(strActivityName)){
			mToastManager.show(R.string.prompt_input_activity_name);
			return;
		}
		// 获取并检测开始时间
		if(TextUtils.isEmpty(mStrFT_Y_M_D)){
			mToastManager.show(R.string.prompt_input_activity_time);
			return;
		}
		
		final String strFromTime;
		final String strEndTime;
		
		if(TextUtils.isEmpty(mStrET_Y_M_D)){
			if(TextUtils.isEmpty(mStrFT_H_M_S)){
				strFromTime = mStrFT_Y_M_D + " 00:00:00";
				strEndTime  = mStrFT_Y_M_D + " 23:59:59";
			}else {
				mToastManager.show(R.string.prompt_input_activity_e_time_point);
				return;
			}
		}else {
			if(TextUtils.isEmpty(mStrFT_H_M_S)){
				strFromTime = mStrFT_Y_M_D + " 00:00:00";
			}else {
				strFromTime = mStrFT_Y_M_D + mStrFT_H_M_S;
			}
			
			if(TextUtils.isEmpty(mStrET_H_M_S)){
				strEndTime = mStrET_Y_M_D + " 00:00:00";
			}else {
				strEndTime = mStrET_Y_M_D + mStrET_H_M_S;
			}
		}
		
		int timeResult = compareTiem(strFromTime, strEndTime);
		if(timeResult == 0){
			mToastManager.show(R.string.prompt_input_activity_f_time_small_e_time);
			return;
		} else if(timeResult == 1){
			mToastManager.show(R.string.prompt_input_activity_f_time_equale_e_time);
			return;
		}
		// 活动地点
		final String strSite = mEditTextSite.getEditableText().toString();
		if(TextUtils.isEmpty(strSite)){
			mToastManager.show(R.string.prompt_input_activity_site);
			return;
		}
		// 活动详情
		final String strDetails = mEditTextActivityDetails.getEditableText().toString();
		if(TextUtils.isEmpty(strDetails)){
			mToastManager.show(R.string.prompt_input_activity_details);
			return;
		}
		
		if(TextUtils.isEmpty(images)) {
			mToastManager.show(R.string.prompt_input_activity_images);
			return;
		}
		// 限制人数
		final String strNum =  chechLimitNum()? "" : mEditTextLimitNum.getEditableText().toString();
		// 圈子id
		final String strGroupId = String.valueOf(mListMyCreateGroup.get(mSpinnerHostGroup.getSelectedItemPosition()).getId());
		
		final List<NameValuePair> listTemp = new ArrayList<NameValuePair>();
		listTemp.add(new BasicNameValuePair("appId", URLUtils.KEY));
		listTemp.add(new BasicNameValuePair("title", strActivityName));
		listTemp.add(new BasicNameValuePair("starttime", strFromTime));
		listTemp.add(new BasicNameValuePair("endtime", strEndTime));
		listTemp.add(new BasicNameValuePair("limit", strNum));
		listTemp.add(new BasicNameValuePair("summary", strDetails));
		listTemp.add(new BasicNameValuePair("location", strSite));
		listTemp.add(new BasicNameValuePair("communityId", strGroupId));
		listTemp.add(new BasicNameValuePair("poster", images.split(",")[0]));
		listTemp.add(new BasicNameValuePair("images", images));
		
		if(isEditActivity){
			listTemp.add(new BasicNameValuePair("activityId", String.valueOf(activity.getId())));
			
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_EditActivity, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_EditActivity,
					0, URLUtils.URL_EditActivity, listTemp);
		}else {
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_CreateActivity, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_CreateActivity, 
					0,URLUtils.URL_CreateActivity, listTemp);
		}
		showProgressDialog(null, R.string.submiting);
	}
	
	/**
	 * 人数是否没有限制
	 * @return
	 */
	private boolean chechLimitNum() {
		String limit = mEditTextLimitNum.getEditableText().toString();
		return TextUtils.isEmpty(limit) || limit.equals("不限");
	}
	
	@Override
	protected void onBuilderPositionBtnClick(DialogInterface dialog, int which) {
		super.onBuilderPositionBtnClick(dialog, which);
		if(whichBuilderComfirmClick == 1){
			showProgressDialog(null, R.string.submiting);
			AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_DelActivity, this, true);
			AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_DelActivity, 0,
					String.format(URLUtils.URL_DelActivity, activity.getId()), activity);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case REQUEST_ACTIVITY_MEMBER:
			if(data != null) {
				com.zdht.jingli.groups.model.Activity mActivity = (com.zdht.jingli.groups.model.Activity) data
						.getSerializableExtra(ActivityMainActivity.EXTRA_ACTIVITY);
				if(mActivity != null) {
					activity = mActivity;
					TextView members = (TextView)findViewById(R.id.manage_activity_member);
					members.setText("成员:" + activity.getNowNumber() + "人" );
				}
			}
			break;
		default:
			if(resultCode == RESULT_OK){
				if(requestCode == mRequestCodeUploadActivityImg && resultCode == RESULT_OK && data != null){
					String imageString = data.getStringExtra(UploadActivityImgActivity.EXTRA_IMAGE_LIST);
					if(!TextUtils.isEmpty(imageString)) {
						images = imageString;
						SCApplication.print("" + images);
					}
				}
			}
		}
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPGET_DelActivity){
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				getMyCreateActivity();
				Intent intent = new Intent();
				intent.putExtra(EXTRA_ACTIVITY, activity);
				intent.putExtra(RESULT_IS_DELETE, true);
				setResult(RESULT_OK, intent);
				finish();
			}
		} else if(nCode == EventCode.HTTPPOST_CreateActivity || 
				nCode == EventCode.HTTPPOST_EditActivity){
			BaseInfoPostEvent baseInfoPostEvent = (BaseInfoPostEvent)event;
			mToastManager.show(baseInfoPostEvent.getDescribe());
			if(baseInfoPostEvent.isRequestSuccess()){
				getMyCreateActivity();
				finish();
			}
		}
	}

	/**
	 * 比较开始时间是否在结束时间之前
	 * @param fromTime
	 * @param endTime
	 * @return 1: 两个时间一样； 
	 */
	private int compareTiem(String fromTime, String endTime){
		if(endTime.equals(fromTime)){
			return 1;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date from = format.parse(fromTime);
			Date end = format.parse(endTime);
			
			if(from.after(end)) {
				return 0;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 2;
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == mDialogIdSetFromTime) {
			return new DatePickerDialog(this, onDateSetListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
		}
		if (id == mDialogIdSetEndTime) {
			return new DatePickerDialog(this, onDateSetListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
		}
		if (id == mDialogIdSetTimePoint) {
			return new TimePickerDialog(this, mOnTimeSetListener, 6, 0, true);
		}
		return super.onCreateDialog(id);
	}

	public static void launch(Activity activity,
			com.zdht.jingli.groups.model.Activity activity2) {
		Intent intent = new Intent(activity, CreateOrEditActivityActivity.class);
		intent.putExtra(EXTRA_ACTIVITY, activity2);
		activity.startActivity(intent);
	}
	
	public static void launchForResult(Activity activity,
			com.zdht.jingli.groups.model.Activity activity2) {
		Intent intent = new Intent(activity, CreateOrEditActivityActivity.class);
		intent.putExtra(EXTRA_ACTIVITY, activity2);
		activity.startActivityForResult(intent, REQUEST_CODE_FOR_EDIT);
	}

	DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			monthOfYear++;
			setShowTime(isSeletFromTime, year + "-" + format(monthOfYear) + "-" + format(dayOfMonth));
			setPostTimeYMD(isSeletFromTime, year + "-" + format(monthOfYear) + "-" + format(dayOfMonth));
			
			if (mDialogIdSetTimePoint == 0) {
				mDialogIdSetTimePoint = generateDialogId();
			}
			showDialog(mDialogIdSetTimePoint);
		}
	};

	OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setShowTime(isSeletFromTime, " " + format(hourOfDay) + ":" + format(minute));
			setPostTimeHMS(isSeletFromTime, " " + format(hourOfDay) + ":" + format(minute));
		}
	};
	
	private void setPostTimeYMD(boolean isSetFromTime, String str){
		if(isSetFromTime){
			mStrFT_H_M_S = "";
			mStrFT_Y_M_D = str;
		}else {
			mStrET_H_M_S = "";
			mStrET_Y_M_D = str;
		}
	}
	
	private void setPostTimeHMS(boolean isSetFromTime, String str){
		if(isSetFromTime){
			mStrFT_H_M_S = str + ":00";
		}else {
			mStrET_H_M_S = str + ":00";
		}
	}
	
	private void setShowTime(boolean isSetFromTime, String str){
		if(isSetFromTime){
			mStrFromTime += str;
			mTextViewFromTime.setText(mStrFromTime);
		}else {
			mStrEndTime += str;
			mTextViewEndTime.setText(mStrEndTime);
		}
	}
	private String format(int x){
	    String s = String.valueOf(x);
	    if(s.length() == 1){ 
	    	s = "0" + s;
	    }
	    return s; 
	}
	
}
