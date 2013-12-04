package com.zdht.jingli.groups.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.TextUtils;

public class ImageUtils {
	
	public static final int EXPECT_WIDTH_640 = 640;
	public static final int EXPECT_WIDTH_480 = 480;
	public static final int EXPECT_WIDTH_320 = 320;
	public static final int EXPECT_WIDTH_720 = 720;
	
	/**
	 * 使用640作为期望宽度进行图片压缩
	 * 
	 * @see com.zdht.jingli.groups.utils.ImageUtils#compressImage(String, int)
	 */
	public static Bitmap compressImage(String path) throws OutOfMemoryError {
		return compressImage(path, EXPECT_WIDTH_480);
	}
	
	/**
	 * 按宽高等比例压缩图片.<p>
	 * 
	 * 根据期望宽度计算出压缩比例(整除), 然后根据此比例压缩, 所以输出bitmap的宽度可能高于期望值. 如果本身图片小于期望值, 则返回本身大小.
	 * 
	 * @param path 图片文件路劲
	 * @param expectWidth 期望的宽度, 用于计算压缩比. 
	 * @return 压缩后的Bitmap, 此Bitmap的宽度并不一定等于期望的宽度, 如果原图本身的宽度小于期望值, 则返回原图,不进行压缩.
	 */
	public static Bitmap compressImage(String path, int expectWidth) throws OutOfMemoryError {
		if(TextUtils.isEmpty(path) || expectWidth < 1) {
			return null;
		}
		
		try {
			Options o = new Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			
			int width = o.outWidth;
			int height = o.outHeight;
			System.out.println("-----compressImage>>>width:" + width + "--height:" + height);
			if(width > expectWidth) {
				height = o.outHeight * expectWidth / width;
				width = expectWidth;
				o.inSampleSize = o.outWidth / expectWidth;
				o.outWidth = width;
				o.outHeight = height;
			}
	
			o.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(path, o);
		} catch (OutOfMemoryError e) {
			throw e;
		}
	}
	
	public static void loadImage(File file, String imgUrl) {
		
		HttpURLConnection connection = null;
		try {
			URL url = new URL(imgUrl);
			// 设置http连接
			connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			
			// 获取文件大小
			int fileSize = connection.getContentLength();
			if(fileSize == -1) {
				return;
			}
			// 打开http连接, 获取输入流
			InputStream inputStream = connection.getInputStream();
			// 设置缓冲区
			byte[] buf = new byte[1024];
			byte[] total = new byte[fileSize];
			int readNum = 0;
			int loadSize = 0;
			if(connection.getResponseCode() == 200){
				// 读取数据
				while((readNum = inputStream.read(buf)) != -1){
					// 拷贝单次读取的数据到总数据中
					System.arraycopy(buf, 0, total, loadSize, readNum);
					loadSize += readNum;
				}
				if(loadSize == fileSize) {// 如果下载完成, 保存文件到本地
					// 文件保存到本地
					if(file != null) {
						FileOutputStream fos = new FileOutputStream(file);
						// 写入到文件
						fos.write(total, 0, loadSize);
						fos.close();
						//SCApplication.print( "loadImage end:success");
					}
				}
			}
		} catch (MalformedURLException e) {
			//SCApplication.print( "loadImage end:" + e.getMessage());
		} catch (IOException e) {
			//SCApplication.print( "loadImage end:" + e.getMessage());
		} finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
	}
}
