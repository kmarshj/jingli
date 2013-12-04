package com.zdht.jingli.groups.ui;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import cn.jpush.android.api.InstrumentedActivity;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
/*import com.zdht.school.SCApplication;
import com.zdht.school.localinfo.LocalInfoManager;
import com.zdht.school.model.Version;*/
import com.zdht.jingli.groups.model.Version;

public class StartActivity extends InstrumentedActivity {

	private ImageView startImage;
	private long time = 0;
	/** 是否西南航空 */
	private boolean isXNHKZXXY = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		startImage = (ImageView)findViewById(R.id.start_image);
		if(isXNHKZXXY) {
			//startImage.setBackgroundResource(R.anim.image_show);
			final AnimationDrawable mAnimationDrawable = (AnimationDrawable)startImage.getBackground();
			startImage.post(new Runnable() {
				@Override
				public void run() {
					mAnimationDrawable.start();
				}
			});
		}
		/*
		String logoUrl = LocalInfoManager.getInstance().getmLocalInfo().getLogoUrl();
		//SCApplication.print( "logoUrl :" + logoUrl);
		if(!TextUtils.isEmpty(logoUrl)) {
			File file = SchoolUtils.getLogoUrlFile(logoUrl);
			if(file.exists()) {
				//SCApplication.print( "logo file大小:" + file.length());
				startImage.setBackgroundDrawable(BitmapDrawable.createFromPath(file.getAbsolutePath()));
			} else {
				//SCApplication.print( "logo file 不存在");
			}
		}*/
		new UpdateTask().execute();
	}

	class UpdateTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			long start = System.currentTimeMillis();
			HttpURLConnection connection = null;
			String result = null;
			try {
				URL url = new URL("http://118.114.52.30/school/update.json");
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				InputStream inputStream = connection.getInputStream();
				byte[] buf = new byte[128];
				if (connection.getResponseCode() == 200) {
					String content = "";
					int readNum = 0;
					// 读取数据
					while ((readNum = inputStream.read(buf)) != -1) {
						content += new String(buf, 0, readNum);
					}

					result = content;
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				long end = System.currentTimeMillis();
				time = end - start;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (StartActivity.this.isFinishing() || TextUtils.isEmpty(result)) {
				go();
				return;
			}
			try {
				JSONObject json = new JSONObject(result);
				Version mVersion = new Version();
				mVersion.setVersion(json.getInt("version"));
				mVersion.setUrl(json.getString("url"));
				mVersion.setContent(json.getString("content"));

				int versionCode = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionCode;
				if (versionCode < mVersion.getVersion()) {
					showUpdataDialog(mVersion);
				} else {
					go();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				go();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				go();
			}
		}

	}

	protected void showUpdataDialog(final Version version) {
		AlertDialog.Builder builer = new AlertDialog.Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage(version.getContent());
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(version.getUrl()));
				startActivity(intent);
				finish();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(isXNHKZXXY){
					time = 3000;
				} else {
					time = 1000;
				}
				go();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.setCancelable(false);
		dialog.show();
	}

	Timer timer = new Timer();
	
	private void go() {
		
		if(isXNHKZXXY) {
			if(time < 3000) {
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						time = 3000;
						go();
					}
				}, 3000 - time);
				return;
			}
		} else {
			if(time < 1000) {
				try {
					Thread.sleep(1000 - time);
				} catch (InterruptedException e) {
				}
			}
		}
		if (LocalInfoManager.getInstance().isLogined()) {
			((SCApplication)getApplication()).onUserLogined();
			//MainActivity.launch(StartActivity.this);
		} else {
			//LoginActivity.launch(StartActivity.this);
		}
		finish();
	}
}
