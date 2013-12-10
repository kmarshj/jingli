package com.zdht.jingli.groups.ui;


import com.zdht.jingli.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutUsActivity extends SCBaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			((TextView)findViewById(R.id.tvVersion)).setText(getString(R.string.about_version) + 
					getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.about_us;
		ba.mAddBackButton = true;
	}
	
	public static void launch(Activity activity){
		Intent intent = new Intent(activity, AboutUsActivity.class);
		activity.startActivity(intent);
	}
	
}
