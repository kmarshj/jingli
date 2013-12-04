package com.zdht.jingli.groups.utils;

import java.io.File;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.SharedPreferenceManager;
import com.zdht.jingli.groups.localinfo.LocalBaseInfoProtocol;


public class SchoolUtils {
	
	public static void	buildLocalInfoSaveMap(Map<String, String> map,LocalBaseInfoProtocol info){
		map.put(SharedPreferenceManager.KEY_USERID, info.getUserId());
		map.put(SharedPreferenceManager.KEY_STUDENTID, info.getStudentId());
		map.put(SharedPreferenceManager.KEY_USERPWD, info.getUserPwd());
		map.put(SharedPreferenceManager.KEY_REALNAME, info.getRealName());
	}
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(byte[] b) {
		// String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	/** MD5加密 */
	private static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 根据logourl生成对应的文件 */
	public static File getLogoUrlFile(String logoUrl) {
		if(TextUtils.isEmpty(logoUrl)) {
			return null;
		}
		String filePath = FilePaths.getSchoolLogoFilePath() + "/" + getNameFromLogoUrl(logoUrl);
		
		return new File(filePath);
	}
	
	/** 根据logourl生成对应的文件名称 */
	public static String getNameFromLogoUrl(String logoUrl) {
		if(TextUtils.isEmpty(logoUrl)) {
			return null;
		}
		
		return md5(URLEncoder.encode(logoUrl));
	}
	
	public static String getFileNameFronUrl(String url) {
		if(TextUtils.isEmpty(url)) {
			return null;
		}
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	public static boolean checkExternalStorageAvailable(){
		boolean bAvailable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		if(bAvailable){
			StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			if((long)statfs.getAvailableBlocks() * (long)statfs.getBlockSize() < 1048576){
				//ToastManager.show(sApplication, R.string.prompt_sdcard_full);
				bAvailable = false;
			}
		}else{
			//ToastManager.show(sApplication, R.string.prompt_sdcard_unavailable);
		}
		return bAvailable;
	}
	
	public static void mSendBrodCast(Context mContext, String page) {
		Intent intent = new Intent();
		intent.putExtra("page", page);

		intent.setAction("android.intent.action.page");// action与接收器相同

		mContext.sendBroadcast(intent);
	}
	
	public static boolean getCursorBoolean(Cursor cursor,int nColumnIndex){
		return cursor.getInt(nColumnIndex) == 1 ? true : false;
	}
}
