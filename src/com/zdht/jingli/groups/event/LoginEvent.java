package com.zdht.jingli.groups.event;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.localinfo.LocalBaseInfoProtocol;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.utils.ImageUtils;
import com.zdht.jingli.groups.utils.SchoolUtils;


public class LoginEvent extends HttpPostEvent implements LocalBaseInfoProtocol {
	
	private String  mDescribe;

	public String getDescribe() {
		return mDescribe;
	}

	private String  mUserId;
	private String  mStudentId;
	private String  mUserPwd;
	private boolean mIsFirst;
	private String  mRealName;
	private String  mSchoolCode;
	private String 	mSchoolName;
	private String 	mLogoUrl;
	

	public boolean ismIsFirst() {
		return mIsFirst;
	}

	public LoginEvent(int nEventCode) {
		super(nEventCode);
	}

	@Override
	public void onPreRun() {
		super.onPreRun();
		mDescribe = "";
	}
	
	
	@Override
	public void run(Object... params) throws Exception {
		super.run(params);
		if(isNetSuccess()){
				JSONObject jsonObject = new JSONObject(getHttpRetString());
				mDescribe = jsonObject.getString("describe");
			if(isRequestSuccess()){
				mUserId     = jsonObject.getString("uid");
				// 设置别名
				SCApplication.getInstance().setJPushInfo(mUserId, null);
				
				mIsFirst    = jsonObject.getInt("loginTimes") > 0 ? false : true;
				mRealName   = jsonObject.getString("name");
				if(!jsonObject.isNull("schoolCode")) {
					mSchoolCode = jsonObject.getString("schoolCode");
					LocalInfoManager.getInstance().saveSchoolCode(mSchoolCode);
				}

				if(!jsonObject.isNull("schoolName")) {
					mSchoolName = jsonObject.getString("schoolName");
					LocalInfoManager.getInstance().saveSchoolName(mSchoolName);
				}

				if(!jsonObject.isNull("logoUrl")) {
					mLogoUrl = jsonObject.getString("logoUrl");
					mLogoUrl = "http://d.hiphotos.baidu.com/album/w%3D2048/sign=2a2861ff9d82d158bb825eb1b43219d8/c2fdfc039245d6882f1e55b5a5c27d1ed21b2487.jpg";
					
					LocalInfoManager.getInstance().saveLogoUrl(mLogoUrl);
					if(!TextUtils.isEmpty(mLogoUrl)) {
						final File logoFile = SchoolUtils.getLogoUrlFile(mLogoUrl);
						if(!logoFile.exists()) {// 对应的logo文件不存在则下载.
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									ImageUtils.loadImage(logoFile, mLogoUrl);
								}
								
							}).start();
						}
					}
				}
				// 用户标签
				String tags = "";
				if(jsonObject.has("Jtag")) {
					JSONArray array = jsonObject.getJSONArray("Jtag");
					for(int i = 0; i < array.length(); i++) {
						String obj = array.getString(i);
						tags += obj + ",";
					}
				}
				tags += mSchoolCode;
				
				if(!TextUtils.isEmpty(tags)) {
					LocalInfoManager.getInstance().saveTags(tags);
				}
			}
		}
	}
	
	@Override
	protected void onAddNameValuePair(List<NameValuePair> listNameValuePair) {
		super.onAddNameValuePair(listNameValuePair);
		for (NameValuePair pair : listNameValuePair) {
			if("password".equals(pair.getName())){
				mUserPwd = pair.getValue();
			}else if("userName".equals(pair.getName())) {
				mStudentId = pair.getValue();
			}
		}
	}


	public String getmSchoolCode() {
		return mSchoolCode;
	}

	public String getmSchoolName() {
		return mSchoolName;
	}

	public String getmLogoUrl() {
		return mLogoUrl;
	}

	@Override
	public String getUserPwd() {
		return mUserPwd;
	}

	@Override
	public String getUserId() {
		return mUserId;
	}

	@Override
	public String getStudentId() {
		return mStudentId;
	}

	@Override
	public String getRealName() {
		return mRealName;
	}
	
}
