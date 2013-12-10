package com.zdht.jingli.groups.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.event.PostFileEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Sex;
import com.zdht.jingli.groups.model.UploadImage;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.jingli.groups.utils.DialogUtils;
import com.zdht.utils.FileHelper;
import com.zdht.utils.SystemUtils;

/**
 * 个人信息页面
 * @author think
 *
 */
public class PersonalInfoActivity extends SCBaseActivity implements OnClickListener,
																	DialogInterface.OnClickListener{

	private ImageView mImageViewAvatar;
	private int mDialogIdSetAvatar;
	
	private int mRequestCodeEditPhone;
	private int mRequestCodeEditSignature;
	private Bitmap mBitmapAvatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mImageViewAvatar = (ImageView)findViewById(R.id.touxiang);
		mImageViewAvatar.setOnClickListener(this);
		addAndManageEventListener(EventCode.HTTPPOST_PostAvatar);
		findViewById(R.id.btnLogin_out).setOnClickListener(this);
		
		findViewById(R.id.vPhone).setOnClickListener(this);
		findViewById(R.id.vSignature).setOnClickListener(this);
		findViewById(R.id.vPassword).setOnClickListener(this);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		initUI();
	}
	
	private void initUI(){
		mImageViewAvatar.setImageBitmap(SystemUtils.toRoundBitmap(AvatarBmpProvider.getInstance().loadImage(LocalInfoManager.getInstance().getAvatar())));
		((TextView)findViewById(R.id.tvMy_name)).setText(LocalInfoManager.getInstance().getmLocalInfo().getRealName());
		if(LocalInfoManager.getInstance().getmSex().equals(Sex.MALE)){
			((ImageView)findViewById(R.id.tvMy_sex)).setBackgroundResource(R.drawable.min);
		}else {
			((ImageView)findViewById(R.id.tvMy_sex)).setBackgroundResource(R.drawable.min);
		}
//		((TextView)findViewById(R.id.tvMy_faculties)).setText(LocalInfoManager.getInstance().getmFaculty());
//		((TextView)findViewById(R.id.tvMy_class)).setText(LocalInfoManager.getInstance().getmClass());
		((TextView)findViewById(R.id.tvMy_phone)).setText(LocalInfoManager.getInstance().getmPhone());
		((TextView)findViewById(R.id.tvMy_signature)).setText(LocalInfoManager.getInstance().getmSignature());
	}
	
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
		ba.mTitleTextStringId = R.string.personal_information;
	}
	
	public static void launch(android.app.Activity activity) {
		Intent intent = new Intent(activity, PersonalInfoActivity.class);
		activity.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		
		if(nId == R.id.touxiang){
			if(mDialogIdSetAvatar == 0){
				mDialogIdSetAvatar = generateDialogId();
			}
			showDialog(mDialogIdSetAvatar);
			return;
		}
		
		if(nId == R.id.vPhone){
			if(mRequestCodeEditPhone == 0){
				mRequestCodeEditPhone = generateRequestCode();
			}
			EditPhoneActivity.launchForResult(this, mRequestCodeEditPhone, LocalInfoManager.getInstance().getmPhone());
			return;
		}
		
		if(nId == R.id.vSignature){
			if(mRequestCodeEditSignature == 0){
				mRequestCodeEditSignature = generateRequestCode();
			}
			EditSignatureActivity.launchForResult(this, mRequestCodeEditSignature, LocalInfoManager.getInstance().getmSignature());
			return;
		}
		
		if(nId == R.id.vPassword){
			EditPasswordActivity.launch(this);
			return;
		}
		
		if(nId == R.id.btnLogin_out){
			DialogUtils.showAlertDialog(this, R.string.set_logout_alert_title,
					R.string.set_logout_alert_msg, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							SCApplication.loginOut();
							LoginActivity.launch(PersonalInfoActivity.this);
						}
					});
			return;
		}
		
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == mRequestCodeEditPhone || requestCode == mRequestCodeEditSignature){
				initUI();
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == mDialogIdSetAvatar){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.set_avatar)
			.setItems(R.array.set_avatar, this);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == 0){
			launchCamera(true);
		}else if(which == 1){
			launchPictureChoose(true);
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
	
	@Override
	protected void onPictureChooseResult(Intent data) {
		super.onPictureChooseResult(data);
		if(data != null){
			Parcelable p = data.getParcelableExtra("data");
			if(p != null){
				Bitmap bmp = (Bitmap)p;
				
				mBitmapAvatar = bmp;
				
				mImageViewAvatar.setImageBitmap(SystemUtils.toRoundBitmap(bmp));
				showProgressDialog(null, R.string.uploading);
				final String strAvatarFilePath = FilePaths.getAvatarTempFilePath();
				FileHelper.saveBitmapToFile(strAvatarFilePath, bmp);
				
				final List<UploadImage> list = new ArrayList<UploadImage>();
				list.add(new UploadImage(strAvatarFilePath, bmp));
				
				AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_PostAvatar,
						0, list, 4);
			}
		}
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPPOST_PostAvatar){
			PostFileEvent pEvent = (PostFileEvent)event;
			if(pEvent.isRequestSuccess()){
				LocalInfoManager.getInstance().setmAvatar(pEvent.getmUrl());
				/*FileHelper.deleteFile(FilePaths.getPosterSavePath(SchoolUtils.getFileNameFronUrl(pEvent.getmUrl())));
				AvatarBmpProvider.getInstance().getmMapImageUrlToImageBmp().remove(pEvent.getmUrl());*/
				mImageViewAvatar.setImageBitmap(SystemUtils.toRoundBitmap(mBitmapAvatar));
			}else{
				mToastManager.show(pEvent.getDescribe());
			}
		}else if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				initUI();
			}
		}
	}
	
}
