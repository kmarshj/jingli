package com.zdht.jingli.groups;

import java.io.File;

import com.zdht.utils.Encrypter;
import com.zdht.utils.SystemUtils;


public class FilePaths {
	public static String getAvatarTempFilePath(){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "tempavatar";
	}
	
	public static String getPosterSavePath(String strName){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "poster" + File.separator + strName;
	}
	
	public static String getAvatarSavePath(String strAvatarUrl){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "avatar" + File.separator + strAvatarUrl;
	}
	
	public static String getLiveImageSavePath(){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "live";
	}
	
	public static String getUrlFileCachePath(String strUrl){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "urlfile" + File.separator + Encrypter.encryptBySHA1(strUrl);
	}
	
	public static String getCameraSaveFilePath(){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "camera";
	}
	
	public static String getChatPictureChooseFilePath(){
		return SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "choose";
	}
	
	public static String getSchoolLogoFilePath(){
		String path = SystemUtils.getExternalCachePath(SCApplication.getApplication()) + 
				File.separator + "logo";
		File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
		file = null;
		return path;
	}
}
