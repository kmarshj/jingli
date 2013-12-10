package com.zdht.jingli.groups.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;

import com.zdht.core.Event;
import com.zdht.core.EventManager.OnEventListener;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.AddressGroupAdapter;
import com.zdht.jingli.groups.adapter.AddressUserAdapter;
import com.zdht.jingli.groups.adapter.SetBaseAdapter.OnChildViewClickListener;
import com.zdht.jingli.groups.adapter.ViewPagerAdapter;
import com.zdht.jingli.groups.event.DelFriendEvent;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.event.GetAddressListEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.AddressList;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.utils.DialogUtils;
import com.zdht.utils.ToastManager;
import com.zdht.view.PullToRefreshListView;
import com.zdht.view.PulldownableListView;
import com.zdht.view.PulldownableListView.OnPullDownListener;

public class AddressActivity extends NewBaseActivity implements 
											OnClickListener,
											OnEventListener,
											OnPageChangeListener,
											OnPullDownListener,
											OnItemClickListener,
											OnItemLongClickListener,
											OnChildViewClickListener{

	private static AddressActivity instance;
	
	private AutoCompleteTextView mEditTextSearch;
	
	/** AutoCompleteTextView中的匹配数据 */ 
	private List<String> autoCompleteUser = new ArrayList<String>();
	
	
	/** 标题栏中导航 */
	private RadioGroup navigation;
	
	/** 联系人page容器 */
	private ViewPager viewPager;
	
	/** PagerAdapter */
	private ViewPagerAdapter adapter;
	
	/** 班级列表 */
	private PullToRefreshListView classListView;
	/** 班级成员适配器 */
	private AddressUserAdapter classAdapter;

	/** 好友列表 */
	private PullToRefreshListView friendListView;
	/** 好友列表成员适配器 */
	private AddressUserAdapter friendAdapter;
	
	/** 圈子列表 */
	private PullToRefreshListView groupListView;
	/** 圈子列表成员适配器 */
	private AddressGroupAdapter groupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		instance = this;
		init();
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetAddressList, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_DelRelationShip, this, false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO onDestroy
		super.onDestroy();
		AndroidEventManager.getInstance().removeEventListener(EventCode.SC_DownloadImage, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_DelRelationShip, this);
		AndroidEventManager.getInstance().removeEventListener(EventCode.HTTPGET_GetAddressList, this);
		instance = null;
	}
	
	public static void addOKFriend(User user) {
		// TODO 添加好友
		if(instance != null) {
			List<User> friends = instance.friendAdapter.getmListObject();
			if(friends == null || friends.size() == 0) {
				instance.friendListView.startRun();
			} else {
				instance.friendAdapter.addItem(user);
			}
		}
	}
	
	
	
	public static void addOKGroup(Group group) {
		// TODO 添加好友
		if(instance != null) {
			List<Group> groups = instance.groupAdapter.getmListObject();
			if(groups == null || groups.size() == 0) {
				instance.groupListView.startRun();
			} else {
				instance.groupAdapter.addItem(group);
			}
		}
	}
	
	private void init() {
		mEditTextSearch = (AutoCompleteTextView)findViewById(R.id.address_search);
		
		navigation = (RadioGroup)findViewById(R.id.address_radiogroup);
//		navigation.setOnCheckedChangeListener(this);
		findViewById(R.id.address_radiobutton_class).setOnClickListener(this);
		findViewById(R.id.address_radiobutton_friend).setOnClickListener(this);
		findViewById(R.id.address_radiobutton_group).setOnClickListener(this);
		
		viewPager = (ViewPager)findViewById(R.id.address_viewpager);
		viewPager.setEnabled(false);
		
		List<View> viewList = new ArrayList<View>();
		// 班级
		LayoutInflater inflater = LayoutInflater.from(this);
		classListView = (PullToRefreshListView)inflater.inflate(R.layout.refresh_listview, null);
		classListView.setOnPullDownListener(this);
		classListView.setTag(2);
		classAdapter = new AddressUserAdapter(this);
		classAdapter.setOnChildViewClickListener(this);
		classListView.setAdapter(classAdapter);
		classListView.setOnItemClickListener(this);
		viewList.add(classListView);
		
		// 好友
		friendListView = (PullToRefreshListView)inflater.inflate(R.layout.refresh_listview, null);
		friendListView.setOnPullDownListener(this);
		friendListView.setTag(1);
		friendAdapter = new AddressUserAdapter(this);
		friendAdapter.setOnChildViewClickListener(this);
		friendListView.setAdapter(friendAdapter);
		friendListView.setOnItemClickListener(this);
		friendListView.setOnItemLongClickListener(this);
		viewList.add(friendListView);

		// 圈子
		groupListView = (PullToRefreshListView)inflater.inflate(R.layout.refresh_listview, null);
		groupListView.setOnPullDownListener(this);
		groupListView.setTag(3);
		groupAdapter = new AddressGroupAdapter(this);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(this);
		viewList.add(groupListView);
		
		// 设置viewpager
		adapter = new ViewPagerAdapter(viewList, null);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		
		navigation.check(R.id.address_radiobutton_class);
		classListView.startRun();
		
		// 清除匹配输入框内容
		findViewById(R.id.address_clean).setOnClickListener(this);
		findViewById(R.id.address_title_add).setOnClickListener(this);
	}

	/*@Override
	public void onCheckedChanged(RadioGroup radiogroup, int i) {
		// TODO onCheckedChanged
		switch(i) {
		case R.id.address_radiobutton_class:
			System.out.println("class");
			if(viewPager.getCurrentItem() != 0) {
				viewPager.setCurrentItem(0);
			}
			break;
		case R.id.address_radiobutton_friend:
			System.out.println("friend");
			if(viewPager.getCurrentItem() != 1) {
				viewPager.setCurrentItem(1);
			}
			break;
		case R.id.address_radiobutton_group:
			System.out.println("group");
			if(viewPager.getCurrentItem() != 2) {
				viewPager.setCurrentItem(2);
			}
			break;
		}
	}*/

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO onPageSelected
		switch(arg0) {
		case 0:
			if(navigation.getCheckedRadioButtonId() != R.id.address_radiobutton_class) {
				navigation.check(R.id.address_radiobutton_class);
			}
			if(classAdapter.getmListObject() == null || classAdapter.getmListObject().size() == 0) {
				classListView.startRun();
			}
			break;
		case 1:
			if(navigation.getCheckedRadioButtonId() != R.id.address_radiobutton_friend) {
				navigation.check(R.id.address_radiobutton_friend);
			}
			if(friendAdapter.getmListObject() == null || friendAdapter.getmListObject().size() == 0) {
				friendListView.startRun();
			}
			break;
		case 2:
			if(navigation.getCheckedRadioButtonId() != R.id.address_radiobutton_group) {
				navigation.check(R.id.address_radiobutton_group);
			}
			if(groupAdapter.getmListObject() == null || groupAdapter.getmListObject().size() == 0) {
				groupListView.startRun();
			}
			break;
		}
	}
	
	@Override
	public void onStartRun(PulldownableListView view) {
		// TODO onStartRun
		int type = (Integer)view.getTag();
		System.out.println("onStartRun：" + type);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetAddressList, 0, type);
	}

	@Override
	public void onEndRun(PulldownableListView view) {
	}

	@Override
	public void onItemClick(AdapterView<?> adapterview, View view, int id, long l) {
		// TODO onItemClick
		Object obj = adapterview.getAdapter().getItem(id);
		if(obj instanceof User) {
			User user = (User)obj;
			if(!isLocalUser(user.getUid())){
				UserHomeActivity.launch(this, user);
			}
		} else if(obj instanceof Group) {
			Group group = (Group)obj;
			GroupHomeActivity.launch(this, group.getId());
		} else if(obj instanceof String) {
			String username = (String)obj;
			User mUser = null;
			for(User user : classAdapter.getmListObject()) {
				if(user.getName().equals(username)) {
					mUser = user;
					break;
				}
			}
			if(mUser == null) {
				for(User user : friendAdapter.getmListObject()) {
					if(user.getName().equals(username)) {
						mUser = user;
						break;
					}
				}
			}
			if(mUser != null) {
				UserHomeActivity.launch(this, mUser);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterview, View view, int id, long l) {
		// TODO onItemLongClick 删除好友
		Object obj = adapterview.getAdapter().getItem(id);
		final User user = (User)obj;
		String msg = "您确定要删除好友 " + user.getName() + "吗?"; 
		DialogInterface.OnClickListener listenner = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int paramInt) {
				// TODO Auto-generated method stub
				delFriend(user);
			}
		};
		DialogUtils.showAlertDialog(this, R.string.delete_friend, msg, listenner);
		return true;
	}
	
	@Override
	public void onEventRunEnd(Event event) {
		// TODO onEventRunEnd
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){// 图片下载完成
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				classAdapter.notifyDataSetChanged();
				friendAdapter.notifyDataSetChanged();
				groupAdapter.notifyDataSetChanged();
			}
		} else if(nCode == EventCode.HTTPGET_GetAddressList) {
			GetAddressListEvent getAddressListEvent = (GetAddressListEvent)event;
			int type = getAddressListEvent.getType();
			System.out.println("onEventRunEnd：" + viewPager.getCurrentItem());
			switch(type) {
			case 2:
				classListView.endRun();
				break;
			case 1:
				friendListView.endRun();
				break;
			case 3:
				groupListView.endRun();
				break;
			}
			if(getAddressListEvent.isRequestSuccess()) {
				AddressList mAddressList = (AddressList)getAddressListEvent.getReturnParam();
				switch(type) {
				case 2:
					classAdapter.replaceAll(mAddressList.getmListCLassFriend());
					setAutoComplete();
					break;
				case 1:
					friendAdapter.replaceAll(mAddressList.getListFriend());
					setAutoComplete();
					break;
				case 3:
					groupAdapter.replaceAll(mAddressList.getListGroup());
					break;
				}
			} else {
				ToastManager.getInstance(this).show(getAddressListEvent.getmDescribe());
			}
		} else if(nCode == EventCode.HTTPGET_DelRelationShip){// 删除好友
			dismissProgressDialog();
			DelFriendEvent delFriendEvent = (DelFriendEvent)event;
			User user = (User)delFriendEvent.getReturnParam();
			if(delFriendEvent.isRequestSuccess()) {
				friendAdapter.removeItem(user);
				searchDataChanged(user, false);
			} else {
				ToastManager.getInstance(this).show(delFriendEvent.getDescribe());
			}
		}
	}
	
	@Override
	public void onClick(View view) {
		// TODO onClick
		switch(view.getId()) {
		case R.id.address_clean:
			mEditTextSearch.setText("");
			break;
//		case R.id.address_title_add:
//			startActivity(new Intent(this, AddFriendAndGroupActivity.class));
//			break;
		case R.id.address_radiobutton_class:
			System.out.println("class");
			if(viewPager.getCurrentItem() != 0) {
				viewPager.setCurrentItem(0);
			}
			break;
		case R.id.address_radiobutton_friend:
			System.out.println("friend");
			if(viewPager.getCurrentItem() != 1) {
				viewPager.setCurrentItem(1);
			}
			break;
		case R.id.address_radiobutton_group:
			System.out.println("group");
			if(viewPager.getCurrentItem() != 2) {
				viewPager.setCurrentItem(2);
			}
			break;
		}
	}

	@Override
	public void onChildViewClickListener(View v) {
		// TODO Auto-generated method stub
		Object object = v.getTag();
		if(object == null) {
			return;
		}
		switch(v.getId()) {
		case R.id.ivChat:
			final User user = (User)object;
			final String userIdForInfo = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
			SingleChatActivity.launch(this, String.valueOf(user.getStudentId()), user.getName(), 
					user.getAvatarUrl(), userIdForInfo);
			break;
		case R.id.ivPhone:
			final String strPhone = (String)object;
			DialogUtils.showAlertDialog(this, "确定拨打电话:" + strPhone + "吗?", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					try {
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+strPhone)); 
						startActivity(intent);
					} catch (Exception e) {
						mToastManager.show(R.string.phone_error);
						e.printStackTrace();
					}
				}
			});
			break;
		}
	}
	
	private void setAutoComplete() {
		// 班级同学或者好友存在时, 添加搜索匹配资源;
		if(classAdapter.getmListObject().size() != 0 || friendAdapter.getmListObject().size() != 0) {
			// TODO AutoCompleteTextView
			autoCompleteUser.clear();
			for(User user : classAdapter.getmListObject()) {
				autoCompleteUser.add(user.getName());
			}

			for(User user : friendAdapter.getmListObject()) {
				autoCompleteUser.add(user.getName());
			}
			setAutoAdapter();
		}
	}

	/**
	 * 用于搜索的AutoCompleteTextView的数据源发生变化
	 * @param user
	 * @param isAdd
	 */
	private void searchDataChanged(User user, boolean isAdd) {
		if(isAdd){
			autoCompleteUser.add(user.getName());
		} else {
			autoCompleteUser.remove(user.getName());
		}
		setAutoAdapter();
	}
	
	private void setAutoAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_dropdown_item_1line, autoCompleteUser);
		mEditTextSearch.setAdapter(adapter);
		mEditTextSearch.setOnItemClickListener(this);
	}
	
	/**
	 * 删除好友
	 * @param user
	 */
	protected void delFriend(User user) {
		showProgressDialog(null, R.string.submiting);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_DelRelationShip, 0,
				String.format(URLUtils.URL_DelRelationShip, user.getUid()), user);
	}
}
