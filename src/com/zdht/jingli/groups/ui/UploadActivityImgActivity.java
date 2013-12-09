package com.zdht.jingli.groups.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.adapter.UploadImageAdapter;
import com.zdht.jingli.groups.event.PostFileEvent;
import com.zdht.jingli.groups.model.NetImage;
import com.zdht.jingli.groups.model.UploadImage;
import com.zdht.jingli.groups.utils.ImageUtils;
import com.zdht.utils.FileHelper;
import com.zdht.utils.ToastManager;

public class UploadActivityImgActivity extends SCBaseActivity implements OnClickListener,
																		 OnItemClickListener,
																		 DialogInterface.OnClickListener,
																		 OnItemLongClickListener{

	public final static String EXTRA_LISTTEMP = "listtemp";
//	public final static String EXTRA_ISEDIT   = "isedit";
	public final static String EXTRA_IMAGE_LIST = "imagelist";
	
	private GridView mGridViewImages;
	private Button mButtonSubmit;
	private UploadImageAdapter mUploadImageAdapter;
	private UploadImage mBitmapAddImage;
	private int mDialogIdSetAvatar;
	private List<UploadImage> mListActivityImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mButtonSubmit = addButtonInTitleRight();
		mButtonSubmit.setText(R.string.submit);
		mButtonSubmit.setOnClickListener(this);
		mGridViewImages = (GridView)findViewById(R.id.gvImages);
		mGridViewImages.setOnItemClickListener(this);
		mGridViewImages.setOnItemLongClickListener(this);
		mUploadImageAdapter = new UploadImageAdapter(this);
		final BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.add_image);
		mBitmapAddImage = new UploadImage("add", bd.getBitmap());
		mGridViewImages.setAdapter(mUploadImageAdapter);
		mListActivityImage = new ArrayList<UploadImage>();
		mListActivityImage.add(mBitmapAddImage);
		mUploadImageAdapter.replaceAll(mListActivityImage);
		addAndManageEventListener(EventCode.HTTPPOST_PostActivityImage);
	}
	
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
		ba.mTitleTextStringId = R.string.upload_image;
	}
	
	public static void launchForResult(Activity activity, int nRequestCode) {
		Intent intent = new Intent(activity, UploadActivityImgActivity.class);
//		intent.putExtra(EXTRA_ISEDIT, isEditActivity);
		activity.startActivityForResult(intent, nRequestCode);
	}


	@Override
	public void onClick(View v) {
		if(mListActivityImage.size() <= 1){
			mToastManager.show(R.string.prompt_upload_poster);
			return;
		}
		
		showProgressDialog(null, R.string.uploading_image);
		
		final List<UploadImage> list = new ArrayList<UploadImage>();
		
		for (int i = 0; i < mListActivityImage.size() - 1; i++) {
			list.add(mListActivityImage.get(i));
		}
		
		
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPPOST_PostActivityImage,
				0, list, "7");
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPPOST_PostActivityImage){
			dismissProgressDialog();
			PostFileEvent pEvent = (PostFileEvent)event;
			if(pEvent.isRequestSuccess()){
				final List<NetImage> list = (List<NetImage>)pEvent.getReturnParam();
				String imagesString = "";
				for (int i = 0; i < list.size(); i++) {
					imagesString += list.get(i).getImageUrl() + ",";
				}
				if(imagesString.length() > 0){
					imagesString = imagesString.substring(0, imagesString.length() - 1);
				}
				Intent intent = new Intent();
				intent.putExtra(EXTRA_IMAGE_LIST, imagesString);
				setResult(RESULT_OK, intent);
				finish();
			}else{
				mToastManager.show(pEvent.getDescribe());
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == mDialogIdSetAvatar){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.upload_image)
			.setItems(R.array.set_avatar, this);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == 0){
			launchCamera(false);
		}else if(which == 1){
			launchPictureChoose(false);
		}
	}

	@Override
	protected void onPictureChooseResult(Intent data) {
		super.onPictureChooseResult(data);
		if(data != null){
			
			String path = data.getStringExtra("path");
			Bitmap bmp = ImageUtils.compressImage(path);

			File file = new File(path);
			if(file.exists()) {
				file.delete();
			}
			if(bmp == null) {
				ToastManager.getInstance(this).show("未找到文件");
				return;
			}
			
			Calendar c = Calendar.getInstance();
			final String strPosterFilePath = FilePaths.getPosterSavePath("" + (c.get(Calendar.YEAR)) + ((c.get(Calendar.MONTH) + 1)) + (c.get(Calendar.DAY_OF_MONTH)) + (c.get(Calendar.HOUR_OF_DAY)) + (c.get(Calendar.MINUTE)) + (c.get(Calendar.SECOND)));
			FileHelper.saveBitmapToFile(strPosterFilePath, bmp);
			mListActivityImage.add(mListActivityImage.size() - 1, new UploadImage(strPosterFilePath, bmp));
			//SCApplication.print( "mListActivityImage.size:" + mListActivityImage.size());
			mUploadImageAdapter.replaceAll(mListActivityImage);
		}
	}
	
	@Override
	protected void onSetCropExtra(Intent intent) {
		super.onSetCropExtra(intent);
        intent.putExtra("return-data", false);
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position == parent.getChildCount() - 1){
			if(mDialogIdSetAvatar == 0){
				mDialogIdSetAvatar = generateDialogId();
			}
			showDialog(mDialogIdSetAvatar);
			return;
		}
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}
	
}
