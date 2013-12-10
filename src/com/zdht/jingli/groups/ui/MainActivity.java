package com.zdht.jingli.groups.ui;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.adapter.MainTabAdapter;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;

/**
 * <主界面>
 * 
 * @author luchengsong
 */
public class MainActivity extends ActivityGroup implements
		OnItemClickListener , OnClickListener, OnEventListener{
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
	
	private View[] views = new View[5];
	/** 当前的导航下标 */
	private int currentItem;
	
	/** 请求登录视图 */
	private View loginView;

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
		// 添加登录成功并完成所有相关操作的监听
		AndroidEventManager.getInstance().addEventListener(EventCode.HP_LoginSuccessAndEnd, this, false);
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
		createLoginView();
	}
	
	private void createLoginView() {
		loginView = getLayoutInflater().inflate(R.layout.activity_main_login, null);
		loginView.findViewById(R.id.main_rl_login).setOnClickListener(this);
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
			if(views[0] == null) {
				intents = new Intent(context,
						ActivityAndNewsActivity.class);
				views[0] = getLocalActivityManager().startActivity("news",
						intents).getDecorView();
			}
			break;
		case 1:
			if(!LocalInfoManager.getInstance().isLogined()) {
				// 未登录
				views[id] = loginView;
			} else if(views[id] == loginView || views[id] == null) {
				intents = new Intent(context,
						ChatListActivity.class);
				views[id] = getLocalActivityManager().startActivity("chat",
						intents).getDecorView();
			}
			break;
		case 2:
			if(views[id] == null) {
				views[id] = new View(this);
//				intents = new Intent(context,
//						ShoppingCartActivity.class);
//				views[2] = getLocalActivityManager().startActivity(
//						"shoppingcar", intents).getDecorView();
			}
			break;
		case 3:
			if(!LocalInfoManager.getInstance().isLogined()) {
				// 未登录
				views[id] = loginView;
			} else if(views[id] == loginView || views[id] == null) {
				intents = new Intent(context,
						AddressActivity.class);
				views[id] = getLocalActivityManager().startActivity("address",
						intents).getDecorView();
			}
			break;
		case 4:
			if(!LocalInfoManager.getInstance().isLogined()) {
				// 未登录
				views[id] = loginView;
			} else if(views[id] == loginView || views[id] == null) {
				intents = new Intent(context,
						SetActivity.class);
				views[id] = getLocalActivityManager().startActivity("set",
						intents).getDecorView();
			}
			break;
		default:
			break;
		}
//		intents.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		currentItem = id;
		/**
		 * 装载子页面View到LinearLayout容器里面
		 */
		pageContainer.addView(views[id],
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

	@Override
	public void onClick(View arg0) {
		// TODO onClick
		switch(arg0.getId()) {
		case R.id.main_rl_login:
			LoginActivity.launch(this);
			break;
		}
	}

	@Override
	public void onEventRunEnd(Event event) {
		// TODO Auto-generated method stub
		if(event.getEventCode() == EventCode.HP_LoginSuccessAndEnd) {
			if(currentItem == 1 || currentItem == 3 || currentItem == 4)
				switchPage(currentItem);
		}
	}


}
