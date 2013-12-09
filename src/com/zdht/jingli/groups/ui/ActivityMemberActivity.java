package com.zdht.jingli.groups.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.ActivityMemberAdapter;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.event.GetUsersEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.jingli.groups.model.User;
import com.zdht.view.PullToRefreshListView.OnLoadMoreClickListener;
import com.zdht.view.PulldownableListView;

/**
 * 活动成员
 * @author think
 *
 */
public class ActivityMemberActivity extends SCPullDownRefreshActivity implements OnItemLongClickListener,
														OnLoadMoreClickListener {

	private ActivityMemberAdapter mActivityMemberAdapter;
	private Activity activity;
	
	public static final String EXTRA_IS_EDIT = "edit";
	
	/** 是否编辑模式 */
	private boolean isEdit;
	
	/** 获取活动成员：当前页码 */
	private int currenPage = 0;
	/** 获取活动成员：是否加载更多 */
	private boolean mIsLoadMore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isEdit = getIntent().getBooleanExtra(EXTRA_IS_EDIT, false);
		super.onCreate(savedInstanceState);
		activity = (com.zdht.jingli.groups.model.Activity)getIntent().getSerializableExtra(ActivityMainActivity.EXTRA_ACTIVITY);
		if(isEdit) {
			RelativeLayout title = (RelativeLayout)findViewById(R.id.viewTitle);
			title.setVisibility(View.VISIBLE);
		}
		mActivityMemberAdapter = new ActivityMemberAdapter(this);
		mListView.setAdapter(mActivityMemberAdapter);
		mListView.setOnItemLongClickListener(this);
		mListView.setOnLoadMoreClickListener(this);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetActivityMember, this, false);
	}
	
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		if(isEdit) {
			ba.mHasTitle = true;
			ba.mTitleTextStringId = R.string.activity_member;
			ba.mAddBackButton = true;
		} else {
			ba.mHasTitle = false;
		}
	}
	
	@Override
	public void onStartRun(PulldownableListView view) {
		super.onStartRun(view);
		getActivityMember(1, false);
	}
	
	private void getActivityMember(int page, boolean isLoadMore) {
//		show
		mListView.loadMoreView.setVisibility(View.GONE);
		mIsLoadMore = isLoadMore;
		if(isLoadMore) {
			currenPage++;
		} else {
			currenPage = page;
		}
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetActivityMember, 0,
				String.format(URLUtils.URL_GetUser, 1, activity.getId() ,currenPage));
	}
	
	@Override
	public void onBackPressed() {
		// TODO onBackPressed
		Intent intent = new Intent();
		intent.putExtra(ActivityMainActivity.EXTRA_ACTIVITY, activity);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		super.onItemClick(parent, view, position, id);
		Object object = parent.getItemAtPosition(position);
		if(object != null){
			if (object instanceof User) {
				User user = (User)object;
				if(!isLocalUser(user.getUid())){
					UserHomeActivity.launch(this, user);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				mActivityMemberAdapter.notifyDataSetChanged();
			}
		}else if(nCode == EventCode.HTTPGET_GetActivityMember){// 获取活动成员
			mListView.endRun();
			GetUsersEvent getUsersEvent = (GetUsersEvent)event;
			if(getUsersEvent.isRequestSuccess()){
				if(mIsLoadMore){
					mActivityMemberAdapter.addAllItem((List<User>)getUsersEvent.getReturnParam());
				}else {
					mActivityMemberAdapter.replaceAll((List<User>)getUsersEvent.getReturnParam());
				}
				if(getUsersEvent.hasMore()){
					mListView.loadMoreView.setVisibility(View.VISIBLE);
				}else {
					mListView.loadMoreView.setVisibility(View.GONE);
				}
			}else {
				mToastManager.show(getUsersEvent.getDescribe());
			}

		} else if(nCode == EventCode.HTTPGET_DeleteActivityMember) {// 删除活动成员
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			if(baseInfoGetEvent.isRequestSuccess()) {
				Object[] params = baseInfoGetEvent.getRequestParams();
				User user = (User)params[1];
				mActivityMemberAdapter.removeItem(user);
				try {
					int nowNumber = Integer.valueOf(activity.getNowNumber());
					nowNumber--;
					activity.setNowNumber(String.valueOf(nowNumber));
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
			} else {
				mToastManager.show("删除活动成员失败");
				SCApplication.print("" + baseInfoGetEvent.getDescribe());
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndroidEventManager.getInstance().removeEventListener(EventCode.SC_DownloadImage, this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterview, View view,
			int i, long l) {
		// TODO Auto-generated method stub
		if(!isEdit) {
			return false;
		}
		List<User> list = mActivityMemberAdapter.getmListObject();
		if(list == null || list.size() == 0) {
			return false;
		}
		final User user = list.get(i - 1);
		final String uId = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
		if(String.valueOf(user.getUid()).equals(uId)) {
			return false;
		}
		// 删除活动成员
		if(builder == null){
			builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle(R.string.app_name);
		}
		builder.setMessage("是否将成员(" + user.getName() + ")从该活动中删除?");
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_DeleteActivityMember, this, true);
		builder.setPositiveButton(R.string.confirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showProgressDialog(null, R.string.submiting);
				AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_DeleteActivityMember, 0,
						String.format(URLUtils.URL_QuitActivity, activity.getId()) + "&userId=" + user.getUid(), user);
			}
		});
		builder.setNegativeButton(R.string.cancle, null);
		builder.show();
		return true;
	}
	
	@Override
	public void onLoadMoreClicked(int nId, View object) {
		setLoadMoreText();
	}
	
	private void setLoadMoreText() {
//		TextView loadMore = (TextView)mListView.loadMoreView.findViewById(R.id.load_more_text);
//		String loadText = getResources().getString(R.string.click_load_more);
//		if(loadMore.getText().toString().equals(loadText)) {
//			loadMore.setText(R.string.click_loading_more);
			getActivityMember(currenPage, true);
//		} else {
//			loadMore.setText(loadText);
//		}
	}
}
