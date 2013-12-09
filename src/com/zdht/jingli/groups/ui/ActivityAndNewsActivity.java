package com.zdht.jingli.groups.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.ActivityAdapter;
import com.zdht.jingli.groups.adapter.NewsAdapter;
import com.zdht.jingli.groups.adapter.ViewPagerAdapter;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.event.GetActivitiesEvent;
import com.zdht.jingli.groups.event.GetNewsEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Activity;
import com.zdht.jingli.groups.model.News;
import com.zdht.view.PullToRefreshListView;
import com.zdht.view.PullToRefreshListView.OnLoadMoreClickListener;
import com.zdht.view.PulldownableListView;
import com.zdht.view.PulldownableListView.OnPullDownListener;

/**
 * 活动与资讯页面
 * @author luchengsong
 *
 */
public class ActivityAndNewsActivity extends NewBaseActivity implements OnCheckedChangeListener,
																OnClickListener,
																OnEventListener,
																OnPageChangeListener,
																OnPullDownListener,
																OnLoadMoreClickListener,
																OnItemClickListener{

	/** 标题栏中导航 */
	private RadioGroup navigation;
	
	/** 标题栏右侧菜单栏 */
	private ImageView menu;
	
	/** 资讯与活动容器 */
	private ViewPager viewPager;
	
	/** PagerAdapter */
	private ViewPagerAdapter adapter;
	
	/** 活动列表, tag设置为类别, 1为本月活动的标签 */
	private PullToRefreshListView activity;
	
	/** 活动列表当前page */
	private int activityPage = 0;
	
	/** 是否加载更多活动 */
	private boolean mIsLoadMoreActivity;
	
	/** 活动列表适配器 */
	private ActivityAdapter activityAdapter;
	
	/** 活动菜单 */
	private PopupWindow activityPopup;
	
	/** 新闻列表 */
	private PullToRefreshListView news;
	
	/** 新闻列表当前page */
	private int newsPage = 0;
	
	/** 是否加载更多新闻 */
	private boolean mIsLoadMoreNews;
	
	/** 活动列表适配器 */
	private NewsAdapter newsAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO onCreate
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_activity);
		initView();

		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetCurrentActivities, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_AddActivity, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_QuitActivity, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetNews, this, false);
	}
	
	/** 初始化视图控件 */
	private void initView() {
		// 标题栏中部选择标签
		navigation = (RadioGroup)findViewById(R.id.activity_news_radiogroup);
		navigation.setOnCheckedChangeListener(this);
		
		// 标题栏右侧菜单按钮
		menu = (ImageView)findViewById(R.id.activity_news_title_menu);
		menu.setOnClickListener(this);
		
		viewPager = (ViewPager)findViewById(R.id.activity_news_viewpager);

		List<View> viewList = new ArrayList<View>();
		
		// 活动
		LayoutInflater inflater = LayoutInflater.from(this);
		activity = (PullToRefreshListView)inflater.inflate(R.layout.refresh_listview, null);
		activity.setOnPullDownListener(this);
		// 
		activity.setTag(1);
		activityAdapter = new ActivityAdapter(this);
		activity.setAdapter(activityAdapter);
		activity.setOnLoadMoreClickListener(this);
		activity.setOnItemClickListener(this);
		viewList.add(activity);
		
		// 新闻
		news = (PullToRefreshListView)inflater.inflate(R.layout.refresh_listview, null);
		news.setOnPullDownListener(this);
		newsAdapter = new NewsAdapter(this);
		news.setAdapter(newsAdapter);
		news.setOnLoadMoreClickListener(this);
		news.setOnItemClickListener(this);
		viewList.add(news);
		// 设置viewpager
		adapter = new ViewPagerAdapter(viewList, null);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		// 请求活动数据
		activity.startRun();
	}
	
	@Override
	protected void onDestroy() {
		// TODO onDestroy
		super.onDestroy();
		AndroidEventManager.getInstance().removeEventListener(EventCode.SC_DownloadImage, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_GetCurrentActivities, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_AddActivity, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_QuitActivity, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_GetNews, this);
	}

	/**
	 * 打开菜单popupwindow
	 */
	private void openMenuPopup() {
		if(navigation.getCheckedRadioButtonId() == R.id.activity_news_radiobutton_activity) {
			// 当前选中的是活动信息
			if(activityPopup == null) {
				activityPopup = new PopupWindow(this);
				activityPopup.setOutsideTouchable(true);
				View activityView = getLayoutInflater().inflate(R.layout.activity_news_menu_popup_activity, null);
				RadioGroup activityRadios = (RadioGroup)activityView.findViewById(R.id.activity_news_popup_activity_radios);
				activityRadios.setOnCheckedChangeListener(this);
				int x = getWindowManager().getDefaultDisplay().getWidth() / 2;
				activityPopup.setWidth(x);
				activityPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//				activityPopup.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
				activityPopup.setFocusable(true);
				activityPopup.setContentView(activityView);
				activityPopup.setBackgroundDrawable(new BitmapDrawable());
			}
			activityPopup.showAsDropDown(menu);
		} else {
			// 当前选中的是资讯信息
			
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup radiogroup, int i) {
		// TODO onCheckedChanged
		switch(i) {
		case R.id.activity_news_radiobutton_activity:
			viewPager.setCurrentItem(0);
			menu.setVisibility(View.VISIBLE);
			break;
		case R.id.activity_news_radiobutton_news:
			viewPager.setCurrentItem(1);
			menu.setVisibility(View.GONE);
			break;
		case R.id.activity_news_popup_activity_all:
			activityChange(3);
			break;
		case R.id.activity_news_popup_activity_weekly:
			activityChange(9);
			break;
		case R.id.activity_news_popup_activity_month:
			activityChange(1);
			break;
		case R.id.activity_news_popup_activity_nmonth:
			activityChange(2);
			break;
		case R.id.activity_news_popup_activity_over:
			activityChange(10);
			break;
		}
	}
	
	private void activityChange(int type) {
		activityPopup.dismiss();
		if(type < 0) {
			return;
		}
		activity.setTag(type);
		activity.startRun();
	}

	@Override
	public void onClick(View view) {
		// TODO onClick
		switch(view.getId()) {
		case R.id.activity_news_title_menu:// menu菜单
			openMenuPopup();
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		// TODO onEventRunEnd
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){// 图片下载完成
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				activityAdapter.notifyDataSetChanged();
			}
		}else if(nCode == EventCode.HTTPGET_GetCurrentActivities){// 获取活动列表
			activity.endRun();
			resetLoadMore(activity);
			GetActivitiesEvent getActivitiesEvent = (GetActivitiesEvent)event;
			if(getActivitiesEvent.isRequestSuccess() && getActivitiesEvent.getTotalCount() != 0){
				boolean hasMore = getActivitiesEvent.hasMore();
				List<Activity> activityList = (List<Activity>)getActivitiesEvent.getReturnParam();
				
				activityAdapter.setHasMore(hasMore);
				if(mIsLoadMoreActivity){
					activityAdapter.addAllItem(activityList);
				}else {
					activityAdapter.replaceAll(activityList);
				}
				
				if(hasMore){
					activity.loadMoreView.setVisibility(View.VISIBLE);
				}else {
					activity.loadMoreView.setVisibility(View.GONE);
				}
				
			}else {
				mToastManager.show(getActivitiesEvent.getDescribe());
				if(activityPage > 1) {
					activityPage--;
				}
			}
		} else if(nCode == EventCode.HTTPGET_GetNews) {
			news.endRun();
			resetLoadMore(news);
			GetNewsEvent mGetNewsEvent = (GetNewsEvent)event;
			if(mGetNewsEvent.isRequestSuccess()) {
				boolean hasMore = mGetNewsEvent.hasMore();
				List<News> newsList = (List<News>)mGetNewsEvent.getReturnParam();
				
				newsAdapter.setHasMore(hasMore);
				if(mIsLoadMoreNews){
					newsAdapter.addAllItem(newsList);
				}else {
					newsAdapter.replaceAll(newsList);
				}
				
				if(hasMore){
					news.loadMoreView.setVisibility(View.VISIBLE);
				}else {
					news.loadMoreView.setVisibility(View.GONE);
				}
			} else {
				mToastManager.show(mGetNewsEvent.getDescribe());
				if(newsPage > 1) {
					newsPage--;
				}
			}
		}
		/*else if(nCode == EventCode.HTTPGET_QuitActivity) {
			QuitAndAddActivityEvent addActivityEvent = (QuitAndAddActivityEvent)event;
			if(addActivityEvent.isRequestSuccess()){
				final Activity activity  = (Activity)addActivityEvent.getReturnParam();
				changeIsAdd(activity, 0);
			}
		}else if(nCode == EventCode.HTTPGET_AddActivity){
			QuitAndAddActivityEvent addActivityEvent = (QuitAndAddActivityEvent)event;
			if(addActivityEvent.isRequestSuccess()){
				final Activity activity  = (Activity)addActivityEvent.getReturnParam();
				changeIsAdd(activity, 1);
			}
		}else if(nCode == EventCode.IM_ReceiveSystemMessage){
			mImageViewBtnRight.setImageResource(R.anim.animlist_play_voice);
			AnimationDrawable ad = (AnimationDrawable)mImageViewBtnRight.getDrawable();
			ad.start();
		}*/
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 获取活动列表
	 * @param type 类型， 1为当月， 2为下月，3为全部
	 * @param page 需加载的页面
	 * @param isLoadMore 是否加载更多
	 */
	private void getActivities(int type, int page, boolean isLoadMore){
		activity.setTag(type);
		mIsLoadMoreActivity = isLoadMore;
		activityPage = page;
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetCurrentActivities, 0,
				String.format(URLUtils.URL_GetActivities, type, "", activityPage, 10));
	}
	
	/**
	 * 获取新闻资讯列表
	 * @param page 需加载的页面
	 * @param isLoadMore 是否加载更多
	 */
	private void getNews(int page, boolean isLoadMore) {
		newsPage = page;
		mIsLoadMoreNews = isLoadMore;
		String schoolCode = LocalInfoManager.getInstance().getmLocalInfo().getSchoolCode().toLowerCase();
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetNews, 0,
				String.format(URLUtils.URL_GetNews, schoolCode, page));
	}
	
	@Override
	public void onPageSelected(int arg0) {
		// TODO onPageSelected
		switch(arg0) {
		case 0:
			navigation.check(R.id.activity_news_radiobutton_activity);
			if(activityAdapter.getmListObject() == null || activityAdapter.getmListObject().size() == 0) {
				activity.startRun();
			}
			break;
		case 1:
			navigation.check(R.id.activity_news_radiobutton_news);
			if(newsAdapter.getmListObject() == null || newsAdapter.getmListObject().size() == 0) {
				news.startRun();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO onItemClick 跳转到活动或资讯详情页 
		Object object = parent.getItemAtPosition(position);
		if(object == null){
			return;
		}
		if (object instanceof Activity) {// 跳转到活动详情页
			Activity activity = (Activity)object;
		//	ActivityMainActivity.launchForResult(this, activity);
		} else {
			News news = (News)object;
			//NewsDetailActivity.launch(this, news);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == CreateOrEditActivityActivity.REQUEST_CODE_FOR_EDIT &&
//				resultCode == RESULT_OK && data != null) {
//			// 有活动被删除
//			boolean isDeleted = data.getBooleanExtra(CreateOrEditActivityActivity.RESULT_IS_DELETE, false);
//			if(isDeleted) {
//				Activity activity = (Activity)data.getSerializableExtra(CreateOrEditActivityActivity.EXTRA_ACTIVITY);
//				List<Activity> activityList = activityAdapter.getmListObject();
//				for(Activity mActivity : activityList) {
//					if(mActivity.getId() == activity.getId()) {
//						activityAdapter.removeItem(mActivity);
//						break;
//					}
//				}
//			}
//		}
	}
	@Override
	public void onStartRun(PulldownableListView view) {
		// TODO onStartRun
		if(view.equals(activity)) {
			int type = (Integer)view.getTag();
			getActivities(type, 1, false);
		} else {
			getNews(1, false);
		}
	}

	@Override
	public void onEndRun(PulldownableListView view) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMoreClicked(int nId, View v) {
		// TODO onLoadMoreClicked
		TextView loadMore = (TextView)v.findViewById(R.id.load_more_text);
		if(loadMore.getVisibility() != View.VISIBLE) {
			return;
		}
		ProgressBar progress = (ProgressBar)v.findViewById(R.id.load_more_progress);
		loadMore.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);
		
		if(v.equals(activity.loadMoreView)) {
			// 加载更多活动
			int type = (Integer)activity.getTag();
			getActivities(type, activityPage + 1, true);
		} else {
			// 加载更多新闻
			getNews(newsPage + 1, true);
		}
	}
	
	/**
	 * 加载更多视图恢复原样
	 * @param v
	 */
	private void resetLoadMore(View v) {
		TextView loadMore = (TextView)v.findViewById(R.id.load_more_text);
		ProgressBar progress = (ProgressBar)v.findViewById(R.id.load_more_progress);
		loadMore.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
	}
	
}
