package com.zdht.jingli.groups.model;


public class NetImage {

	private final String mImageUrl;
	private final String mRelativepath;

	public NetImage(String imageUrl, String relativepath){
		mImageUrl = imageUrl;
		mRelativepath = relativepath;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public String getRelativepath() {
		return mRelativepath;
	}
	
	
	
	
}
