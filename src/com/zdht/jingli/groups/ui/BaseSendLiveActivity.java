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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.PostFileEvent;
import com.zdht.jingli.groups.model.UploadImage;
import com.zdht.utils.FileHelper;

public class BaseSendLiveActivity extends SCBaseActivity implements OnClickListener,
																	DialogInterface.OnClickListener{

	protected static final String EXTRA_ACTIVITYID = "activityid";
	protected static final String EXTRA_IMAGE = "image";
	protected static final String EXTRA_BITMAP = "bitmap";
	protected static final String EXTRA_FILE_PATH = "filepath";
	
	protected Button mButtonSubmit;
	protected ImageView mImageViewLiveImage;
	protected EditText  mEditTextLiveContent;
	protected String mStrImage;
	protected String mStrImageUrl;
	protected String mStrLiveContent;
	protected Button mButtonAddImage;
	private int mDialogIdAddImage;
	protected List<UploadImage> mListActivityImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mButtonSubmit = addButtonInTitleRight();
		mButtonSubmit.setText(R.string.submit);
		mButtonSubmit.setOnClickListener(this);
		mImageViewLiveImage  = (ImageView)findViewById(R.id.ivLive_image);
		mImageViewLiveImage.setOnClickListener(this);
		mEditTextLiveContent = (EditText)findViewById(R.id.etLive_content);
		mButtonAddImage = (Button)findViewById(R.id.btnAdd_image);
		mButtonAddImage.setOnClickListener(this);
		mListActivityImage = new ArrayList<UploadImage>();
		addAndManageEventListener(EventCode.HTTPPOST_PostLiveImage);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
		ba.mTitleTextStringId = R.string.send_live;
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		if(nId == R.id.ivLive_image || nId == R.id.btnAdd_image){
			if(mDialogIdAddImage == 0){
				mDialogIdAddImage = generateDialogId();
			}
			showDialog(mDialogIdAddImage);
			return;
		}
		if(nId == R.id.btn_title_right){
			final String strLiveContent = mEditTextLiveContent.getEditableText().toString();
			if(TextUtils.isEmpty(strLiveContent)){
				mToastManager.show(R.string.prompt_input_content);
				return;
			}
			mStrLiveContent = strLiveContent;
			uploadImage();
			return;
		}
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == mDialogIdAddImage){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.change_image)
			.setItems(R.array.set_avatar, this);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	
	protected void uploadImage(){
		showProgressDialog(null, R.string.uploading_image);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_PostLiveImage,
				0, mListActivityImage, "6");
		
	}
	
	protected void sendLive(){
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_SendLive, this, true);
		if(TextUtils.isEmpty(mStrImageUrl)) {
			mStrImageUrl = "";
		}
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_SendLive, 0,
				String.format(URLUtils.URL_SendLive, getIntent().getIntExtra(EXTRA_ACTIVITYID, 0), 
						mStrLiveContent, mStrImageUrl), mStrLiveContent, mStrImageUrl);
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPPOST_PostLiveImage){
			PostFileEvent pEvent = (PostFileEvent)event;
			if(pEvent.isRequestSuccess()){
				mStrImageUrl = pEvent.getmUrl();
				sendLive();
			}else{
				mStrImage = "";
				mImageViewLiveImage.setImageBitmap(null);
				mToastManager.show(pEvent.getDescribe());
			}
		}else if(nCode == EventCode.HTTPGET_SendLive){
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				String content = (String)baseInfoGetEvent.getRequestParams()[1];
				String url = (String)baseInfoGetEvent.getRequestParams()[2];
				Intent intent = new Intent();
				intent.putExtra("content", content);
				intent.putExtra("url", url);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
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
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600); 
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
				mImageViewLiveImage.setImageBitmap(bmp);
				final String strAvatarFilePath = FilePaths.getLiveImageSavePath();
				FileHelper.saveBitmapToFile(strAvatarFilePath, bmp);
				mStrImage = strAvatarFilePath;
				mListActivityImage.clear();
				mListActivityImage.add(new UploadImage(mStrImage, bmp));
			}
		}
	}
}