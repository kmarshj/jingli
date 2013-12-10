package com.zdht.jingli.groups.ui;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.UploadImage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;



public class SendLiveImageActivity extends BaseSendLiveActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStrImage = getIntent().getStringExtra(EXTRA_IMAGE);
		Bitmap bmp = BitmapFactory.decodeFile(mStrImage);
		mImageViewLiveImage.setImageBitmap(bmp);
		mListActivityImage.add(new UploadImage(mStrImage, bmp));
	}
	
	public static void launch(android.app.Activity activity, int acitivityId, String image) {
		Intent intent = new Intent(activity, SendLiveImageActivity.class);
		intent.putExtra(EXTRA_ACTIVITYID, acitivityId);
		intent.putExtra(EXTRA_IMAGE, image);
		activity.startActivityForResult(intent, 100);
	}
	
	
	@Override
	protected void uploadImage() {
		if(TextUtils.isEmpty(mStrImage)){
			mToastManager.show(R.string.prompt_add_image);
			return;
		}
		super.uploadImage();
	}
}
