package com.zdht.jingli.groups.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;

public class FeedbackActivity extends SCBaseActivity implements OnClickListener {

	private EditText mEditTextContent;
	private Button mButtonSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEditTextContent = (EditText)findViewById(R.id.etFeedbackContent);
		mButtonSubmit = addButtonInTitleRight();
		mButtonSubmit.setText(R.string.submit);
		mButtonSubmit.setOnClickListener(this);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.feedback;
		ba.mAddBackButton = true;
	}

	@Override
	public void onClick(View v) {
		final String strContent = mEditTextContent.getEditableText().toString();
		if(TextUtils.isEmpty(strContent)){
			mToastManager.show(R.string.prompt_input_feedback);
			return;
		}
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_Feedback, this, true);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_Feedback, 0, String.format(URLUtils.URL_Feedback, strContent));
		
		
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HTTPGET_Feedback){
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				finish();
			}
		}
	}
	
	
	public static void launch(android.app.Activity activity) {
		Intent intent = new Intent(activity, FeedbackActivity.class);
		activity.startActivity(intent);
	}
}
