package com.zdht.jingli.groups;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zdht.jingli.groups.model.XMessage;
import com.zdht.utils.SystemUtils;

public class MessagePhotoProvider {
	
	private static HashMap<String, Bitmap> 	mMapIdToThumbPhoto = new HashMap<String, Bitmap>();
	private static List<String>					mListId = new LinkedList<String>();
	
	public static Bitmap loadThumbPhoto(XMessage m){
		Bitmap bmp = mMapIdToThumbPhoto.get(m.getId());
		if(bmp == null){
			if (m.isThumbPhotoFileExists()) {
				bmp = BitmapFactory.decodeFile(m.getThumbPhotoFilePath());
			} else {
				if (m.isFromSelf()) {
					BitmapFactory.Options op = new BitmapFactory.Options();
					op.inJustDecodeBounds = true;
					final String strPhotoFilePath = m.getPhotoFilePath();
					BitmapFactory.decodeFile(strPhotoFilePath, op);
					final int nMaxWidth = SystemUtils.dipToPixel(SCApplication.getApplication(), 100);
					if (op.outWidth > nMaxWidth) {
						op.inJustDecodeBounds = false;
						op.inSampleSize = op.outWidth / nMaxWidth;
						if (op.inSampleSize == 1) {
							op.inSampleSize = 2;
						}
						try {
							bmp = BitmapFactory.decodeFile(strPhotoFilePath, op);
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
							op.inSampleSize = op.inSampleSize * 2;
							bmp = BitmapFactory.decodeFile(strPhotoFilePath, op);
						}
					} else {
						bmp = BitmapFactory.decodeFile(strPhotoFilePath);
					}
				} else {
					bmp = BitmapFactory.decodeFile(m.getThumbPhotoFilePath());
				}
			}
			if(bmp != null){
				addToCache(m, bmp);
			}
		}
		
		return bmp;
	}
	
	private static void addToCache(XMessage m,Bitmap bmp){
		mListId.add(m.getId());
		mMapIdToThumbPhoto.put(m.getId(), bmp);
		if(mListId.size() > 10){
			final String strPopId = mListId.get(0);
			mListId.remove(0);
			mMapIdToThumbPhoto.remove(strPopId);
		}
	}
}
