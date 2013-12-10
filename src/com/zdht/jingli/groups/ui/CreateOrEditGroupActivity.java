package com.zdht.jingli.groups.ui;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.event.BaseInfoPostEvent;
import com.zdht.jingli.groups.event.GetGroupInfoEvent;
import com.zdht.jingli.groups.event.PostImageEvent;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.GroupMember;
import com.zdht.jingli.groups.model.UploadImage;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.utils.FileHelper;

/**
 * 新建或修改圈子
 * @author think
 *
 */
public class CreateOrEditGroupActivity extends SCBaseActivity implements OnClickListener,
															DialogInterface.OnClickListener{

	public final static String EXTRA_GROUP = "group";
	
	private EditText mEditTextName;
	private EditText mEditTextRemark;
	private EditText mGroupExplanation;
	private ImageView mImageViewAvatar;
	private Button mButtonSubmit;
	private boolean isEditGroup;
	private Group group;
	private String mStringAvatarUrl = "";
	private TextView memberText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEditTextName = (EditText)findViewById(R.id.etGroup_name);
		mGroupExplanation = (EditText)findViewById(R.id.etGroup_remark);
		mEditTextRemark = (EditText)findViewById(R.id.etGroup_explanation);
		mImageViewAvatar = (ImageView)findViewById(R.id.ivGroup_avatar);
		mImageViewAvatar.setOnClickListener(this);
		mButtonSubmit = addButtonInTitleRight();
		mButtonSubmit.setText(R.string.submit);
		mButtonSubmit.setOnClickListener(this);
		group = (Group)getIntent().getSerializableExtra(EXTRA_GROUP);
		if(group != null){// create_group_member
			isEditGroup = true;
			mTextViewTitle.setText(R.string.edit_group);
			// 名称不可编辑
			mEditTextName.setText(group.getName());
			mEditTextName.setFocusable(false);
			
			mEditTextRemark.setText(group.getRemark());
			mGroupExplanation.setText(group.getExplanation());
			// 圈子成员
			memberText = (TextView)findViewById(R.id.create_group_member);
			memberText.setText("成员：" + group.getMemberNum() + "人");
			memberText.setVisibility(View.VISIBLE);
			memberText.setOnClickListener(this);
			if(group.isNeedVerification()){
			}
			mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(group.getAvatarUrl()));
		} else {
			isEditGroup = false;
		}
		addAndManageEventListener(EventCode.HTTPPOST_PostGroupAvatar);
		addAndManageEventListener(EventCode.HTTPGET_GetGroupInfo);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.create_group;
		ba.mAddBackButton = true;
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		switch(nId) {
		case  R.id.btn_title_right:
			final String strGroupName = mEditTextName.getEditableText().toString();
			if(TextUtils.isEmpty(strGroupName)){
				mToastManager.show(R.string.prompt_input_group_name);
				return;
			}
			
			final String strRemark = mEditTextRemark.getEditableText().toString();
			if(TextUtils.isEmpty(strRemark)){
				mToastManager.show(R.string.prompt_input_group_explanation);
				return;
			}
			
			final String explanation = mGroupExplanation.getText().toString();
			
			final List<NameValuePair> mListNameValuePair = new ArrayList<NameValuePair>();
			mListNameValuePair.add(new BasicNameValuePair("appId", URLUtils.KEY));
			mListNameValuePair.add(new BasicNameValuePair("comName", strGroupName));
			mListNameValuePair.add(new BasicNameValuePair("announcement", explanation));
			mListNameValuePair.add(new BasicNameValuePair("avatar", mStringAvatarUrl));
			mListNameValuePair.add(new BasicNameValuePair("permission", "0"));
			mListNameValuePair.add(new BasicNameValuePair("remark", strRemark));
			if(isEditGroup){
				AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_EditGroup, this, true);
				mListNameValuePair.add(new BasicNameValuePair("communityId", String.valueOf(group.getId())));
				AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_EditGroup,
						0, URLUtils.URL_EditGroup, mListNameValuePair);
			}else {
				AndroidEventManager.getInstance().addEventListener(EventCode.HTTPPOST_CreateGroup, this, true);
				AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_CreateGroup,
						0, URLUtils.URL_CreateGroup, mListNameValuePair);
			}
			showProgressDialog(null, R.string.submiting);
			break;
		case R.id.create_group_member: // 点击成员
			ArrayList<GroupMember> members = group.getMembers();
			if(group.getMembers() == null || group.getMembers().size() == 0){
				AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetGroupInfo, 0,
					String.format(URLUtils.URL_GetGroupInfo, group.getId()));
			} else {
				GroupMemberActivity.launchForResult(this, members, group.getId() + "");
			}
			break;
		case R.id.ivGroup_avatar:// 点击头像
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.upload_image).setItems(R.array.set_avatar, this);
			builder.create().show();
			break;
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == GroupMemberActivity.REQUEST_CODE_GROUP_MEMBER && resultCode == RESULT_OK && data != null) {
			ArrayList<GroupMember> memberList = data.getParcelableArrayListExtra(GroupMemberActivity.EXTRA_GROUP_MEMBER);
			group.setMemberNum(memberList.size());
			group.setMembers(memberList);
			memberText.setText("成员：" + group.getMemberNum() + "人");
		}
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPPOST_PostGroupAvatar){
			PostImageEvent pEvent = (PostImageEvent)event;
			dismissProgressDialog();
			if(pEvent.isRequestSuccess()){
				mStringAvatarUrl = pEvent.getmUrl();
			}else{
				mStringAvatarUrl = "";
				mImageViewAvatar.setImageBitmap(null);
				mToastManager.show(pEvent.getDescribe());
			}
		}else if(nCode == EventCode.HTTPPOST_CreateGroup || 
				nCode == EventCode.HTTPPOST_EditGroup){
			BaseInfoPostEvent baseInfoPostEvent = (BaseInfoPostEvent)event;
			mToastManager.show(baseInfoPostEvent.getDescribe());
			if(baseInfoPostEvent.isRequestSuccess()){
				getMyCreateGroup();
				finish();
			}
		} else if(nCode == EventCode.HTTPGET_GetGroupInfo){
			GetGroupInfoEvent getGroupInfoEvent = (GetGroupInfoEvent)event;
			if(getGroupInfoEvent.isRequestSuccess()){
				Group mGroup = (Group)getGroupInfoEvent.getReturnParam();
				ArrayList<GroupMember> members = mGroup.getMembers();
				if(members == null || members.size() == 0){
					mToastManager.show("获取圈子成员信息错误");
				} else {
					group = mGroup;
					GroupMemberActivity.launchForResult(this, members, group.getId() + "");
				}
			}else {
				mToastManager.show(getGroupInfoEvent.getDescribe());
			}
		}
	}
	
	@Override
	protected void onPictureChooseResult(Intent data) {
		super.onPictureChooseResult(data);
		if(data != null){
			Parcelable p = data.getParcelableExtra("data");
			if(p != null){
				Bitmap bmp = (Bitmap)p;
				mImageViewAvatar.setImageBitmap(bmp);
				showProgressDialog(null, R.string.uploading);
				final String strAvatarFilePath = FilePaths.getAvatarTempFilePath();
				FileHelper.saveBitmapToFile(strAvatarFilePath, bmp);
				final List<UploadImage> list = new ArrayList<UploadImage>();
				list.add(new UploadImage(strAvatarFilePath, bmp));
				
				AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_PostGroupAvatar,
						0, list, "5");
			}
		}
	}
	
	@Override
	protected void onSetCropExtra(Intent intent) {
		super.onSetCropExtra(intent);
		intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1); 
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100); 
		intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
	}
	
	
	public static void launch(Activity activity, Group group) {
		Intent intent = new Intent(activity, CreateOrEditGroupActivity.class);
		intent.putExtra(EXTRA_GROUP, group);
		activity.startActivity(intent);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO DialogInterface.onClick
		switch(which) {
		case 0:launchCamera(true);
			break;
		case 1:
			launchPictureChoose(true);
			break;
		case 2:
			break;
		}
	}
}
