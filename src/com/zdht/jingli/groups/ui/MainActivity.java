package com.zdht.jingli.groups.ui;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.adapter.MainTabAdapter;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color; 
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * <主界面>
 * 
 * @author luchengsong
 */
public class MainActivity extends ActivityGroup implements
		OnItemClickListener {
	/**
	 * 底部标签图片适配器
	 */
	private MainTabAdapter tabAdapter;

	/**
	 * 应用程序上下文
	 */
	private Context context;

	/**
	 * 放置子页面的容器
	 */
	private LinearLayout pageContainer;

	/**
	 * 页面跳转
	 */
	private Intent intents;
	private Window pageView = null;

	/**
	 * 初始标签图片的资源ID
	 */
	private final Integer[] tabImages = { R.drawable.huodong_n,R.drawable.liaotian_n, 
			R.drawable.shangjia_n, R.drawable.tongxulu_n,R.drawable.shezhi_n };

	/**
	 * 盛放底部图片的GridView
	 */
	private GridView bottomMainPage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		/**
		 * 初始化View
		 */
		initView(savedInstanceState);

		/**
		 * 设置监听事件
		 */
		setListener();

	}

	private void initView(Bundle savedInstanceState) {

		context = this;

		/**
		 * 获取子页面的容器布局
		 */
		pageContainer = (LinearLayout) this.findViewById(R.id.pageContainer);

		bottomMainPage = (GridView) this.findViewById(R.id.tabGridView);
		/**
		 * 选中时候为透明色
		 */
		bottomMainPage.setSelector(new ColorDrawable(Color.TRANSPARENT));

		/**
		 * 获取屏幕宽度并平分5份
		 */
		int width = getWindowManager().getDefaultDisplay().getWidth() / 5;

		/**
		 * 获取底部标签图片的最小高度
		 */
		int height = getResources().getDrawable(R.drawable.huodong_n)
				.getMinimumHeight();

		/**
		 * 填充底部标签适配器
		 */
		tabAdapter = new MainTabAdapter(context, tabImages, width, height);
		bottomMainPage.setAdapter(tabAdapter);

		/**
		 * 默认打开第0页
		 */
		switchPage(0);
	}

	private void setListener() {
		bottomMainPage.setOnItemClickListener(this);
	}
	public static void launch(Activity activity){
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switchPage(position);
	}

	/**
	 * 根据ID打开指定的PageActivity
	 * 
	 * @param id
	 *            选中项的tab序号
	 */
	private void switchPage(int id) {
		/* 必须先清除容器中所有的View */
		pageContainer.removeAllViews();
		tabAdapter.setFocus(id);
		switch (id) {
		/**
		 * 获取子页面View
		 */
		case 0:
			intents = new Intent(context,
					LoginActivity.class);
			pageView = getLocalActivityManager().startActivity("homepage",
					intents);
			break;
		case 1:
//			intents = new Intent(context,
//					MallSearchActivity.class);
//			pageView = getLocalActivityManager().startActivity("myself",
//					intents);
			break;
		case 2:
		
//				intents = new Intent(context,
//						ShoppingCartActivity.class);
//				pageView = getLocalActivityManager().startActivity(
//						"shoppingcar", intents);
			
			break;
		case 3:
//			intents = new Intent(context,
//					MarketPersonCenterActivity.class);
//			pageView = getLocalActivityManager().startActivity("personcenter",
//					intents);
			break;
		case 4:
//			intents = new Intent(context,
//					MarketPersonCenterActivity.class);
//			pageView = getLocalActivityManager().startActivity("personcenter",
//					intents);
			break;
		default:
			break;
		}
		intents.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		/**
		 * 装载子页面View到LinearLayout容器里面
		 */
		pageContainer.addView(pageView.getDecorView(),
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}


	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


}