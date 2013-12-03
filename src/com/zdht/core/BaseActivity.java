package com.zdht.core;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.zdht.utils.SystemUtils;

public abstract class BaseActivity extends Activity{

	protected BaseAttribute     mBaseAttribute;
	
	protected RelativeLayout 	mRelativeLayoutTitle;
	protected View 				mButtonBack;
	protected TextView 			mTextViewTitle;
	
	protected int		mRequestCodeLaunchCamera;
	protected int 		mLaunchCameraNotCrop = 111111;
	
	protected int 		mRequestCodeLaunchChoosePicture;
	
	
	private Object				mTag;
	
	private	int					mRequestCodeInc = 0;
	private int					mDialogIdInc 	= 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 初始化标题栏相关控件属性资源
		onInitAttribute(mBaseAttribute = new BaseAttribute());
		if(mBaseAttribute.mSetContentView){
			String strClassName = this.getClass().getName();
			int nIndex = strClassName.lastIndexOf(".");
			if(nIndex != -1){
				final String strResourceName = strClassName.substring(nIndex + 1).toLowerCase();
//				//SCApplication.print( "strResourceName:" + strResourceName);
				final int nLayoutId = getResources().getIdentifier(strResourceName, 
						"layout", getPackageName());
//				//SCApplication.print( "nLayoutId:" + nLayoutId);
				if(nLayoutId != 0){
					setContentView(nLayoutId);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mTag = null;
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
//		if(this instanceof SingleChatActivity) {
//			LinearLayout root = (LinearLayout)root1;
//			for(int i = 0; i < root.getChildCount(); i++) {
//				View view = root.getChildAt(i);
//				
//				int id = view.getId();
//				String name = getResources().getResourceName(id);
//				View child = root1.findViewById(id);
//				//SCApplication.print( "name:" + name + "/"+ id);
//			}
//			
//		}
		if(mBaseAttribute.mHasTitle){
			mRelativeLayoutTitle = (RelativeLayout)findViewById(mBaseAttribute.mTitleLayoutId);
			
			if(mBaseAttribute.mAddTitleText){
				if(mBaseAttribute.mTitleText == null){
					addTextInTitle(mBaseAttribute.mTitleTextStringId);
				}else{
					addTextInTitle(mBaseAttribute.mTitleText);
				}
			}
			
			if(mBaseAttribute.mAddBackButton){
				mButtonBack = onCreateBackButton();
				mRelativeLayoutTitle.addView(mButtonBack, onCreateBackButtonLayoutParams());
			}
			
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == mRequestCodeLaunchCamera){
				try {
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setDataAndType(Uri.fromFile(new File(getCameraSaveFilePath())), 
							"image/*");
					onSetCropExtra(intent);
					if (mRequestCodeLaunchChoosePicture == 0) {
						mRequestCodeLaunchChoosePicture = generateRequestCode();
					}
					startActivityForResult(intent, mRequestCodeLaunchChoosePicture);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(requestCode == mLaunchCameraNotCrop) {
				if(data == null) {
					data = new Intent();
				}
				data.putExtra("path", getCameraSaveFilePath());
				onPictureChooseResult(data);
			} else if(requestCode == mRequestCodeLaunchChoosePicture){
				onPictureChooseResult(data);
			}
		}
	}
	
	protected void onPictureChooseResult(Intent data){
	}
	
	/**
	 * 调用拍照功能
	 * @param crop 拍完后是否需要剪切
	 */
	protected void launchCamera(boolean crop){
		try{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(getCameraSaveFilePath());
			if(!file.exists()){
				File vDirPath = file.getParentFile();
				vDirPath.mkdirs();
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			if(crop) {
				if(mRequestCodeLaunchCamera == 0){
					mRequestCodeLaunchCamera = generateRequestCode();
				}
				startActivityForResult(intent, mRequestCodeLaunchCamera);
			} else {
				startActivityForResult(intent, mLaunchCameraNotCrop);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 调用拍照功能- 用于聊天
	 * @param uri 文件存放路径
	 */
	protected void launchCameraForChat(Uri uri){
		try{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, mLaunchCameraNotCrop);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 从相册选择图片
	 * @param crop 选择后是否需要剪切
	 */
	protected void launchPictureChoose(boolean crop){
		try{
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			File file = new File(getCameraSaveFilePath());
			if(!file.exists()){
				File vDirPath = file.getParentFile();
				vDirPath.mkdirs();
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			
			intent.setType("image/*");
			onSetCropExtra(intent);
			if(crop) {
				if(mRequestCodeLaunchChoosePicture == 0){
					mRequestCodeLaunchChoosePicture = generateRequestCode();
				}
				startActivityForResult(intent,mRequestCodeLaunchChoosePicture);
			} else {
				startActivityForResult(intent, mLaunchCameraNotCrop);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	protected void onSetCropExtra(Intent intent){
		intent.putExtra("crop", "true");
	}
	
	protected String 	getCameraSaveFilePath(){
		return "";
	}
	
	protected void 		setTag(Object object){
		mTag = object;
	}
	
	protected Object 	getTag(){
		return mTag;
	}
	
	protected int		generateRequestCode(){
		return ++mRequestCodeInc;
	}
	
	protected int		generateDialogId(){
		return ++mDialogIdInc;
	}
	
	protected Button addButtonInTitleRight(){
		final Button btnRight = (Button)(LayoutInflater.from(this)
				.inflate(mBaseAttribute.mRightButtonLayoutId, null));
		mRelativeLayoutTitle.addView(btnRight, onCreateRightButtonLayoutParams());
		return btnRight;
	}
	
	protected View addViewInTitleRight(View v,int nTopMargin,int nRightMargin){
		final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.topMargin = nTopMargin;
		lp.rightMargin = nRightMargin;
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mRelativeLayoutTitle.addView(v, lp);
		return v;
	}
	
	protected void 	addTextInTitle(int nResId){
		mTextViewTitle = onCreateTitleTextView(nResId);
		mRelativeLayoutTitle.addView(mTextViewTitle,onCreateTitleTextViewLayoutParams());
	}
	
	protected void 	addTextInTitle(String strText){
		mTextViewTitle = onCreateTitleTextView(strText);
		mRelativeLayoutTitle.addView(mTextViewTitle,onCreateTitleTextViewLayoutParams());
	}
	
	/** 初始化标题栏相关控件属性资源 */
	protected void onInitAttribute(BaseAttribute ba){
	}
	
	/** 创建返回按钮 */
	protected Button onCreateBackButton(){
		final Button btnBack = (Button)(LayoutInflater.from(this)
				.inflate(mBaseAttribute.mBackButtonLayoutId, null));;
		btnBack.setOnClickListener(mOnClickListener);
		return btnBack;
	}
	
	protected TextView	onCreateTitleTextView(int nResId){
		final TextView textView = (TextView)LayoutInflater.from(this)
				.inflate(mBaseAttribute.mTitleTextLayoutId, null);
		textView.setText(nResId);
		return textView;
	}
	
	protected TextView	onCreateTitleTextView(String strText){
		final TextView textView = (TextView)LayoutInflater.from(this)
				.inflate(mBaseAttribute.mTitleTextLayoutId, null);
		textView.setText(strText);
		return textView;
	}
	
	protected RelativeLayout.LayoutParams onCreateTitleTextViewLayoutParams(){
		return new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
	}
	
	protected RelativeLayout.LayoutParams onCreateBackButtonLayoutParams(){
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				SystemUtils.dipToPixel(this, 30));
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.leftMargin = mBaseAttribute.mBackButtonLeftMargin;
		return lp;
	}
	
	protected RelativeLayout.LayoutParams onCreateRightButtonLayoutParams(){
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				SystemUtils.dipToPixel(this, 30));
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.rightMargin = mBaseAttribute.mRightButtonRightMargin;
		return lp;
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};

	/**
	 * activity基本属性类， 主要是定义标题栏内的控件属性
	 * @author think
	 *
	 */
	protected static class BaseAttribute{
		public boolean  mSetContentView = true;
		/** 是否有标题 */
		public boolean  mHasTitle = true;
		/** 标题资源id */
		public int		mTitleLayoutId;
		/** 是否添加标题文字 */
		public boolean 	mAddTitleText = true;
		/** 标题layout资源id */
		public int		mTitleTextLayoutId;
		/** 遍体内容资源id */
		public int		mTitleTextStringId;
		/** 标题内容 */
		public String	mTitleText;
		/** 是否添加了返回按钮 */
		public boolean 	mAddBackButton = false;
		/** 返回按钮layout资源id */
		public int		mBackButtonLayoutId;
		/** 返回按钮左边距 */
		public int		mBackButtonLeftMargin;
		/** 标题栏右边按钮layout资源id */
		public int      mRightButtonLayoutId;
		/** 标题栏右边按钮右边距 */
		public int      mRightButtonRightMargin;
	}
}
