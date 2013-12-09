package com.zdht.jingli.groups.ui;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.zdht.core.Event;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.RecentChatManager;
import com.zdht.jingli.groups.adapter.RecentChatAdapter;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.model.RecentChat;

public class ChatListActivity extends SCBaseActivity implements OnItemLongClickListener,
																OnItemClickListener{

	private ListView mListViewRcentChat;
	private RecentChatAdapter mRecentChatAdapter;
	private View mViewPromptNoRecentChat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPromptNoRecentChat = findViewById(R.id.viewPromptNoRecentChat);
		mListViewRcentChat = (ListView)findViewById(R.id.lvChat_list);
		mRecentChatAdapter = new RecentChatAdapter(this);
		
		mRecentChatAdapter.addAllItem(RecentChatManager.getInstance().getAllRecentChat());
		
		mListViewRcentChat.setOnItemLongClickListener(this);
		mListViewRcentChat.setOnItemClickListener(this);
		mListViewRcentChat.setAdapter(mRecentChatAdapter);
		updatePromptUI();
		addAndManageEventListener(EventCode.HP_UnreadMessageCountChanged);
		addAndManageEventListener(EventCode.HP_RecentChatChanged);
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void updatePromptUI(){
		if(RecentChatManager.getInstance().getAllRecentChat().size() == 0){
			mViewPromptNoRecentChat.setVisibility(View.VISIBLE);
		}else{
			mViewPromptNoRecentChat.setVisibility(View.GONE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onEventRunEnd(Event event) {
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.HP_RecentChatChanged){
			List<RecentChat> listRecentChat = (List<RecentChat>)event.getReturnParam();
			mRecentChatAdapter.replaceAll(listRecentChat);
			updatePromptUI();
		}else if(nCode == EventCode.HP_UnreadMessageCountChanged){// 未读消息改变
//			RecentChatManager.print("unread message count:" + RecentChatManager.getInstance().getSingleUnreadMessageTotalCount());
			mRecentChatAdapter.notifyDataSetChanged();
		}else if(nCode == EventCode.SC_DownloadImage){
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				mRecentChatAdapter.notifyDataSetChanged();
			}
		}
	}
	
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mTitleTextStringId = R.string.chat;
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		
		
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object object = parent.getItemAtPosition(position);
		if(object != null){
			if(object instanceof RecentChat){
				RecentChat recentChat = (RecentChat)object;
				if(recentChat.getFromType() == HPMessage.FROMTYPE_SINGLE){// 单聊
					final String userIdForInfo = LocalInfoManager.getInstance().getmLocalInfo().getUserId();
					SingleChatActivity.launch(this, recentChat.getId(), recentChat.getName(), 
							recentChat.getLastMessage().getAvatar(), userIdForInfo);
				}else if(recentChat.getFromType() == HPMessage.FROMTYPE_CHATROOM){// 群聊
					//requestJoinRoom(new ChatRoom(recentChat.getId(), recentChat.getName()));
				}
			}
		}
	}
	
	
}
