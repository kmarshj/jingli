package com.zdht.utils;

import android.content.res.AssetManager;

public class TaskParam {
	private String filename;
	private AssetManager assetManager;
	private int ItemWidth;

	public TaskParam(String name, AssetManager assetManager, int width) {
		filename = name;
		this.assetManager = assetManager;
		ItemWidth = width;
	}

	public String getFilename() {
		return filename;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public int getItemWidth() {
		return ItemWidth;
	}
	
	
}
