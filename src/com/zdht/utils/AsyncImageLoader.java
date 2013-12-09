package com.zdht.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zdht.jingli.groups.FilePaths;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;


public class AsyncImageLoader {
	public static AsyncImageLoader getInstance(){
		if(sInstance == null){
			sInstance = new AsyncImageLoader();
		}
		return sInstance;
	}
	
	private static AsyncImageLoader sInstance;
	
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();

	private ExecutorService executorService = Executors.newFixedThreadPool(3);
	private final Handler handler = new Handler();
	
	private AsyncImageLoader(){
	}

	public Drawable loadDrawable(final Context context, final String imageUrl, final ImageCallback1 imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				return softReference.get();
			}
		}

		executorService.submit(new Runnable() {
			public void run() {
				try {
					Drawable d = Drawable.createFromPath(FilePaths.getUrlFileCachePath(imageUrl));
					if (d == null) {
						Bitmap bmp = loadImageFromUrl(imageUrl);
						if (bmp != null) {
							FileHelper.saveBitmapToFile(FilePaths.getUrlFileCachePath(imageUrl), bmp);
							d = new BitmapDrawable(bmp);
						}
					}
					if (d != null) {
						imageCache.put(imageUrl, new SoftReference<Drawable>(d));
					}
					final Drawable drawable = d;
					handler.post(new Runnable() {
						public void run() {
							imageCallback.imageLoaded(drawable, imageUrl);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(new Runnable() {
						public void run() {
							imageCallback.imageLoaded(null, imageUrl);
						}
					});
				}
			}
		});
		return null;
	}

	private Bitmap loadImageFromUrl(String url) {
        InputStream i = null;
        try {
        	HttpGet httpRequest = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
			i = bufferedHttpEntity.getContent();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(i);
        
        return bmp;
    }
	
	public interface ImageCallback1 {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
}
