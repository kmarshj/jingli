package com.zdht.jingli.groups.model;

import android.graphics.Bitmap;

public class UploadImage {

	private final String mImagePath;
	private final Bitmap mBitmap;

	public UploadImage(String ImagePath, Bitmap bitmap){
		mImagePath = ImagePath;
		mBitmap = bitmap;
	}

	public String getmImagePath() {
		return mImagePath;
	}

	public Bitmap getmBitmap() {
		return mBitmap;
	}
	
	
	
	
}
