package com.zdht.jingli.groups.ui;


import java.io.File;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.ListChildViewClickableAdapter.OnListChildViewClickListener;
import com.zdht.jingli.groups.adapter.LiveAdapter;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.event.GetLiveInfoEvent;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.jingli.groups.model.Live;
import com.zdht.jingli.groups.utils.ImageUtils;
import com.zdht.utils.DateUtils;
import com.zdht.utils.FileHelper;
import com.zdht.view.ActivityChatEditView;
import com.zdht.view.ActivityChatEditView.OnLiveEditListener;
import com.zdht.view.PullToRefreshListView.OnLoadMoreClickListener;
import com.zdht.view.PulldownableListView;

/**
 * 活动-现场发布
 * @author think
 *
 */
public class ActivityLiveActivity extends SCPullDownRefreshActivity implements OnClickListener,
																			   OnListChildViewClickListener,
																			   OnLiveEditListener,
																			   OnLoadMoreClickListener{
	
	private LiveAdapter mLiveAdapter;
	private Button mButtonSendLive;
	private Button mButtonPhotograph;
	private Button mButtonText;
	private Activity activity;
	private View mViewClick;

	private int currentPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取活动
		activity = (com.zdht.jingli.groups.model.Activity)getIntent().getSerializableExtra(ActivityMainActivity.EXTRA_ACTIVITY);
		// 互动适配器
		mLiveAdapter = new LiveAdapter(this);
		mLiveAdapter.setOnChildViewClickListener(this);
		mListView.setAdapter(mLiveAdapter);
		// 更多按钮
		mListView.setOnLoadMoreClickListener(this);
		// 
		mButtonSendLive = (Button)findViewById(R.id.btnSend_live);
		mButtonPhotograph = (Button)findViewById(R.id.btnPhotograph);
		mButtonText = (Button)findViewById(R.id.btnText);
		
		mViewClick = findViewById(R.id.viewClick);
		
		// 内容发送编辑视图
		ActivityChatEditView editview = (ActivityChatEditView)findViewById(R.id.activity_chat_editview);
		editview.setOnLiveEditListener(this);
		mListView.setEditView(editview);
		
		mViewClick.setOnClickListener(this);
		mButtonSendLive.setOnClickListener(this);
		mButtonPhotograph.setOnClickListener(this);
		mButtonText.setOnClickListener(this);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetLiveInfo, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_SendLive, this, false);
	}
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mHasTitle = false;
	}
	
	@Override
	public void onStartRun(PulldownableListView view) {
		super.onStartRun(view);
		getActivityLive(false);
	}
	
	
	private void getActivityLive(boolean isMore) {
		if(isMore) {
			currentPage++;
		} else {
			currentPage = 1;
		}
		//SCApplication.print( "获取活动直播情况");
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetLiveInfo, 0,
				String.format(URLUtils.URL_GetLiveInfo, activity.getId(), currentPage), isMore);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				mLiveAdapter.notifyDataSetChanged();
			}
		}else if(nCode == EventCode.HTTPGET_GetLiveInfo){
			mListView.endRun();
			GetLiveInfoEvent getLiveInfoEvent = (GetLiveInfoEvent)event;
			if(getLiveInfoEvent.isRequestSuccess()){
				boolean isMore = false;
				if(getLiveInfoEvent.getRequestParams().length > 1) {
					isMore = (Boolean)getLiveInfoEvent.getRequestParams()[1];
				}
				if(isMore) {
					mLiveAdapter.addAllItem((List<Live>)getLiveInfoEvent.getReturnParam());
				} else {
					mLiveAdapter.replaceAll((List<Live>)getLiveInfoEvent.getReturnParam());
				}
			}else {
				mToastManager.show(getLiveInfoEvent.getDescribe());
				if(currentPage > 1) {
					currentPage--;
				}
			}

			if(getLiveInfoEvent.hasMore()) {
				mListView.loadMoreView.setVisibility(View.VISIBLE);
			} else {
				mListView.loadMoreView.setVisibility(View.GONE);
			}
		} else if(nCode == EventCode.HTTPGET_SendLive) {// 发布互动
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			mToastManager.show(baseInfoGetEvent.getDescribe());
			if(baseInfoGetEvent.isRequestSuccess()){
				String text = (String)baseInfoGetEvent.getRequestParams()[1];
				String url = (String)baseInfoGetEvent.getRequestParams()[2];
				Live live = new Live(DateUtils.getDataStr(), text, url);
				System.out.println("HTTPGET_SendLive--mLiveAdapter.addItem(0, live);");
				mLiveAdapter.addItem(0, live);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndroidEventManager.getInstance().removeEventListener(EventCode.SC_DownloadImage, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_GetLiveInfo, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_SendLive, this);
	}
	
	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		if(nId == R.id.btnSend_live){
			if(mViewClick.getVisibility() == View.VISIBLE){
				closeMoreView();
			}else {
				showMoreView();
			}
			return;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == 100 && resultCode == RESULT_OK && data != null) {
//			System.out.println("mLiveAdapter.addItem");
//			String content = data.getStringExtra("content");
//			String url = data.getStringExtra("url");
//			Live live = new Live(DateUtils.getDataStr(), content, url);
//			mLiveAdapter.addItem(0, live);
//		}
	}

	private void closeMoreView(){
		mButtonPhotograph.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.anim_middle_close));
		mButtonText.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.anim_middle_close));
		mButtonPhotograph.setVisibility(View.GONE);
		mButtonText.setVisibility(View.GONE);
		mViewClick.setVisibility(View.GONE);
	}
	
	private void showMoreView(){
		mButtonPhotograph.setVisibility(View.VISIBLE);
		mButtonText.setVisibility(View.VISIBLE);
		mButtonPhotograph.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.anim_middle_open));
		mButtonText.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.anim_middle_open));
		mViewClick.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onPictureChooseResult(Intent data) {
		super.onPictureChooseResult(data);
		if(data != null){
			String path = data.getStringExtra("path");
			try {
				Bitmap bmp = ImageUtils.compressImage(path);
				if(bmp == null) {
					return;
				}
				File file = new File(path);
				file.delete();
				System.out.println("-----onPictureChooseResult>>>width:" + bmp.getWidth() + "--height:" + bmp.getHeight());
				final String strAvatarFilePath = FilePaths.getLiveImageSavePath();
				FileHelper.saveBitmapToFile(strAvatarFilePath, bmp);
				bmp = null;
				SendLiveImageActivity.launch(this, activity.getId(), strAvatarFilePath);
				System.gc();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				Toast.makeText(this, "加载图片失败,请重试!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	public void onListChildViewClicked(int nId, Object object) {
		if(nId == R.id.ivAvatar){
			if(object instanceof Integer){
				int userId = (Integer)object;
				requestGetUserInfoWithId(userId, false);
			}
		}
	}

	@Override
	public void onSendText(String text) {
		// TODO onSendText
		if(TextUtils.isEmpty(text)) {
			return;
		}
		SCApplication.print(text);
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_SendLive, 0,
				String.format(URLUtils.URL_SendLive, activity.getId(), text, ""), text, "");
	}

	@Override
	public void onSendPhoto() {
		// TODO Auto-generated method stub
		launchPictureChoose(false);
	}

	@Override
	public void onSendCamera() {
		// TODO Auto-generated method stub
		launchCamera(false);
	}

	@Override
	public void onLoadMoreClicked(int nId, View object) {
		// TODO onLoadMoreClicked
		getActivityLive(true);
	}
}
