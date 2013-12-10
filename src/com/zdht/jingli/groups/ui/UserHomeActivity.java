package com.zdht.jingli.groups.ui;



import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.adapter.BelongGroupAdapter;
import com.zdht.jingli.groups.event.BaseInfoGetEvent;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.event.GetGroupEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.Group;
import com.zdht.jingli.groups.model.User;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.jingli.groups.utils.DialogUtils;

/**
 * 用户主页
 * @author think
 *
 */
public class UserHomeActivity extends SCBaseActivity implements OnClickListener,
																OnItemClickListener,
																DialogInterface.OnClickListener {

	public static final String EXTRA_USER   = "user";
	
	/** 电话号码 */
	private TextView mTextViewPhone;
	private LinearLayout phoneLayout;
	
	private Gallery  mGallery;
	private User mUser;
	private BelongGroupAdapter mBelongGroupAdapter;
	private Button mButtonAddFriend;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = (User)getIntent().getSerializableExtra(EXTRA_USER);
		((ImageView)findViewById(R.id.ivUser_avatar)).setImageBitmap(AvatarBmpProvider.getInstance().loadImage(mUser.getAvatarUrl()));
		((TextView)findViewById(R.id.tvUser_name)).setText(mUser.getName());
		if(mUser.getSex().getIntValue() == 1){
			((ImageView)findViewById(R.id.ivUser_sex)).setImageResource(R.drawable.female);
		}else {
			((ImageView)findViewById(R.id.ivUser_sex)).setImageResource(R.drawable.male);
		}
		// 用户号码
		mTextViewPhone = (TextView)findViewById(R.id.tvUser_phone);
		mTextViewPhone.setText(mUser.getPhone());
		
		phoneLayout = (LinearLayout)findViewById(R.id.vPhone);
		phoneLayout.setOnClickListener(this);
		
		((TextView)findViewById(R.id.tvUser_faculty)).setText(getString(R.string.faculties) + mUser.getFaculties());
		((TextView)findViewById(R.id.tvUser_class)).setText(getString(R.string.my_class) + mUser.getmClass());
		((TextView)findViewById(R.id.tvUser_signature)).setText(mUser.getSignature());
		mButtonAddFriend = (Button)findViewById(R.id.btnAdd_friend);
		mButtonAddFriend.setOnClickListener(this);
		mBelongGroupAdapter = new BelongGroupAdapter(this);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		changeView();
		
	}
	
	
	private void changeView(){
		if(!mUser.isIsFriend()){
			phoneLayout.setVisibility(View.GONE);
			return;
		}
		phoneLayout.setVisibility(View.VISIBLE);
		mButtonAddFriend.setText(R.string.into_chat);
		
		AndroidEventManager.getInstance().addEventListener(EventCode.HTTPGET_GetUserAddedGroup, this, true);
		AndroidEventManager.getInstance().postEvent(EventCode.HTTPGET_GetUserAddedGroup, 0,
				String.format(URLUtils.URL_GetGroups, 4, mUser.getUid(), "", 1));
		
	}
	
	
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.home;
		ba.mAddBackButton = true;
	}
	
	public static void launch(Activity activity, User user){
		Intent intent = new Intent(activity, UserHomeActivity.class);
		intent.putExtra(EXTRA_USER, user);
		activity.startActivity(intent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				mBelongGroupAdapter.notifyDataSetChanged();
			}
		}else if(nCode == EventCode.HTTPGET_GetUserAddedGroup){
			GetGroupEvent getGroupEvent = (GetGroupEvent)event;
			if(getGroupEvent.isRequestSuccess()){
				findViewById(R.id.tvBelong_group).setVisibility(View.VISIBLE);
				mGallery = (Gallery)findViewById(R.id.glBelong_group);
				mGallery.setVisibility(View.VISIBLE);
				mGallery.setOnItemClickListener(this);
				mGallery.setAdapter(mBelongGroupAdapter);
				mBelongGroupAdapter.replaceAll((List<Group>)getGroupEvent.getReturnParam());
			}else {
//				mToastManager.show(getGroupEvent.getDescribe());
			}
		} else if(nCode == EventCode.HTTPGET_AddFriend) {
			BaseInfoGetEvent baseInfoGetEvent = (BaseInfoGetEvent)event;
			if(baseInfoGetEvent.isRequestSuccess()){
				mButtonAddFriend.setText(R.string.into_chat);
				if(!TextUtils.isEmpty(mUser.getPhone())) {
					phoneLayout.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndroidEventManager.getInstance().removeEventListener(EventCode.SC_DownloadImage, this);
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		if(nId == R.id.btnAdd_friend){
			if(mUser.isIsFriend()){
				final String userIdForInfo = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
				SingleChatActivity.launch(this, mUser.getStudentId(), mUser.getName(), 
						mUser.getAvatarUrl(), userIdForInfo);
			}else {
				addFriend(mUser);
			}
			return;
		}
		
		if(nId == R.id.vPhone){
			if(TextUtils.isEmpty(mUser.getPhone())) {
				return;
			}
			DialogUtils.showAlertDialog(this, "确定拨打电话:" + mUser.getPhone() + "吗?", this);
			return;
		}
		 
    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object != null){
			if (object instanceof Group) {
				Group group = (Group)object;
				GroupHomeActivity.launch(this, group.getId());
			}
		}
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+mTextViewPhone.getText())); 
			UserHomeActivity.this.startActivity(intent);
		} catch (Exception e) {
			mToastManager.show(R.string.phone_error);
			e.printStackTrace();
		}
	} 
	
}
