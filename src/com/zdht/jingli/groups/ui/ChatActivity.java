package com.zdht.jingli.groups.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.AbsListView;

import com.zdht.core.Event;
import com.zdht.core.im.HPMessageFilter;
import com.zdht.core.im.IMMessageAdapter;
import com.zdht.core.im.IMMessageProtocol;
import com.zdht.core.im.IMMessageViewProvider;
import com.zdht.hospital.messageprocessor.PhotoMessageDownloadProcessor;
import com.zdht.hospital.messageprocessor.PhotoMessageUploadProcessor;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.FilePaths;
import com.zdht.jingli.groups.HPUserFilePathManager;
import com.zdht.jingli.groups.SCApplication;
import com.zdht.jingli.groups.event.CallbackEvent;
import com.zdht.jingli.groups.event.DBMessageEvent;
import com.zdht.jingli.groups.event.DownloadEvent;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.messageviewprovider.PhotoViewLeftProvider;
import com.zdht.jingli.groups.messageviewprovider.PhotoViewRightProvider;
import com.zdht.jingli.groups.messageviewprovider.TextViewLeftProvider;
import com.zdht.jingli.groups.messageviewprovider.TextViewRightProvider;
import com.zdht.jingli.groups.messageviewprovider.TimeViewProvider;
import com.zdht.jingli.groups.messageviewprovider.VoiceViewLeftProvider;
import com.zdht.jingli.groups.messageviewprovider.VoiceViewRightProvider;
import com.zdht.jingli.groups.model.HPMessage;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.parampool.DBHandleType;
import com.zdht.jingli.groups.parampool.DBMessageParam;
import com.zdht.jingli.groups.utils.MessageGroupTimeGenerator;
import com.zdht.parse.AmrParse;
import com.zdht.utils.FileHelper;
import com.zdht.view.HPChatEditView;
import com.zdht.view.HPChatEditView.OnEditListener;
import com.zdht.view.HPChatListView;
import com.zdht.view.PulldownableListView;

public class ChatActivity extends SCBaseActivity implements OnEditListener,
															AbsListView.OnScrollListener,
															PulldownableListView.OnPullDownListener,
															IMMessageViewProvider.OnViewClickListener{

	protected HPChatEditView 	mEditView;
	protected HPChatListView    mListView;
	protected IMMessageAdapter 	mMessageAdapter;
	
	protected boolean		mIsReaded;
	
	private   ChatAttribute mChatAttribute;
	
	protected int				mLastReadPosition;
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		mNotifyConnection = true;
		onInitChatAttribute(mChatAttribute = new ChatAttribute());
		onInit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void onInitChatAttribute(ChatAttribute attr){
	}
	
	
	@Override
	protected void onInitAttribute(BaseAttribute ba) {
		super.onInitAttribute(ba);
		ba.mAddBackButton = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mIsReaded = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIsReaded = false;
	}

	protected void onInit(){
		AndroidEventManager em = AndroidEventManager.getInstance();

		em.addEvent(new CallbackEvent(EventCode.IM_ReceiveMessage));
		
		mListView = (HPChatListView)findViewById(mChatAttribute.mIdListView);

		mEditView = (HPChatEditView)findViewById(R.id.chatEditView);
		mEditView.setOnEditListener(this);
		
		mMessageAdapter = new IMMessageAdapter(this);
		mMessageAdapter.addIMMessageViewProvider(new TextViewLeftProvider(this));
		mMessageAdapter.addIMMessageViewProvider(new TextViewRightProvider(this));
		mMessageAdapter.addIMMessageViewProvider(new TimeViewProvider());
		mMessageAdapter.addIMMessageViewProvider(new VoiceViewLeftProvider(this,this));
		mMessageAdapter.addIMMessageViewProvider(new VoiceViewRightProvider(this,this));
		mMessageAdapter.addIMMessageViewProvider(new PhotoViewLeftProvider(this));
		mMessageAdapter.addIMMessageViewProvider(new PhotoViewRightProvider(this));
		mMessageAdapter.setDefaultIMMessageViewProvider(new TextViewLeftProvider(this));
		
		mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mListView.setEditView(mEditView);
		mListView.setOnScrollListener(this);
		mListView.setOnPullDownListener(this);
		mListView.setAdapter(mMessageAdapter);
		
		if(!TextUtils.isEmpty(mChatAttribute.mFromId)){
			addAndManageEventListener(EventCode.IM_ReceiveMessage);
		}
		
		addAndManageEventListener(EventCode.IM_SendMessage);
		
		addAndManageEventListener(EventCode.HP_PostPhoto);
		addAndManageEventListener(EventCode.HP_PostPhotoPercentChanged);
		
		addAndManageEventListener(EventCode.HP_DownloadThumbPhoto);
		addAndManageEventListener(EventCode.HP_DownloadThumbPhotoPercentChanged);
		
		AndroidEventManager.getInstance().addEventListener(EventCode.SC_DownloadImage, this, false);
		
	}
	
	@Override
	public void onEventRunEnd(Event event) {

		//Log.d("mydebug", "聊天");
		super.onEventRunEnd(event);
		final int nCode = event.getEventCode();
		if(nCode == EventCode.SC_DownloadImage){
			//Log.d("mydebug", "图片下载完成");
			DownloadEvent dEvent = (DownloadEvent)event;
			if(dEvent.isSuccess()){
				mMessageAdapter.notifyDataSetChanged();
			}
		}else if(nCode == EventCode.IM_ReceiveMessage){
			//Log.d("mydebug", "收到消息");
			XMessage message = (XMessage)event.getReturnParam();
			if(HPMessageFilter.accept(mChatAttribute.mFromId, message)){
				onReceiveMessage(message);
			}
		}else if(nCode == EventCode.IM_SendMessage) {
			/*SendMessageEvent sendMessageEvent = (SendMessageEvent)event;
			final HPMessage hpMessage = sendMessageEvent.getmHpMessage();
			hpMessage.setSended();
			if(sendMessageEvent.isRequestSuccess()){
				hpMessage.setSendSuccess(true);
			}else {
				hpMessage.setSendSuccess(false);
			}
			hpMessage.updateDB();*/
			//Log.d("mydebug", "发送消息");
			redrawMessage(null);
		}else if(nCode == EventCode.HP_PostPhotoPercentChanged || 
				nCode == EventCode.HP_DownloadThumbPhoto ||
				nCode == EventCode.HP_DownloadThumbPhotoPercentChanged){
			redrawMessage((IMMessageProtocol)event.getReturnParam());
		}else if(nCode == EventCode.HP_PostPhoto){
			//Log.d("mydebug", "发送图片");
			redrawMessage(null);
		}
	}
	
	protected void onReceiveMessage(XMessage m){
		XMessage timeM = checkOrCreateTimeMessage(m);
		if(timeM != null){
			mMessageAdapter.addItem(timeM);
		}
		mMessageAdapter.addItem(m);
		
		m.setReaded(mIsReaded);
		if(m.getType() == XMessage.TYPE_PHOTO){
			//Log.d("mydebug", "收到图片");
			PhotoMessageDownloadProcessor.getInstance().requestDownload(m, true);
		}
	}
	
	protected XMessage checkOrCreateTimeMessage(XMessage m){
		final int nItemCount = mMessageAdapter.getCount();
		XMessage lastMessage = nItemCount > 0 ? 
				(XMessage)mMessageAdapter.getItem(nItemCount - 1) : null;
		return checkOrCreateTimeMessage(m, lastMessage);
	}
	
	protected void onNewMessageEdited(HPMessage m,boolean bScrollToBottom){
		onInitMessage(m);
		
		MessageGroupTimeGenerator.processGroupTime(m);
		
		if(bScrollToBottom){
			mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		}
		
		XMessage timeMessage = checkOrCreateTimeMessage(m);
		if(timeMessage != null){
			mMessageAdapter.addItem(timeMessage);
		}
		mMessageAdapter.addItem(m);
	}
	
	protected void onInitMessage(HPMessage m){
		m.setUserName(LocalInfoManager.getInstance().getmLocalInfo().getRealName());
		m.setFromSelf(true);
		m.setSendTime(System.currentTimeMillis());
	}
	
	protected void saveAndSendMessage(HPMessage m){
		onSendMessage(m);
		
		DBMessageParam param = new DBMessageParam(DBHandleType.WRITE);
		param.mSaveMessage = m;
		AndroidEventManager.getInstance().runEvent(new DBMessageEvent(0), param);
	}
	
	protected void onSendMessage(HPMessage m){
		final int nType = m.getType();
		if(nType == XMessage.TYPE_VOICE){
			/*VoiceMessageUploadProcessor.getInstance().requestUpload(m);
			redrawMessage(m);*/
		}else if(nType == XMessage.TYPE_PHOTO){
			PhotoMessageUploadProcessor.getInstance().requestUpload(m);
			redrawMessage(m);
		}else{
			AndroidEventManager.getInstance().postEvent(EventCode.IM_SendMessage, 0, m);
		}
	}
	
	@Override
	public boolean onSendCheck() {
		if(SCApplication.isIMConnectionSuccess()){
			return true;
		}else{
			startUnConnectionAnimation();
			return false;
		}
	}

	@Override
	public void onRecordFail(boolean bFailByNet) {
		if(bFailByNet){
			startUnConnectionAnimation();
		}else{
			mToastManager.show(R.string.prompt_record_fail);
		}
	}

	@Override
	public void onSendText(CharSequence s) {
		HPMessage message = new HPMessage(HPMessage.buildMessageId(), HPMessage.TYPE_TEXT);
		message.setContent(String.valueOf(s));
		
		onNewMessageEdited(message,true);
		saveAndSendMessage(message);
	}

	
	
	
	@Override
	public void onSendVoice(String strPathName) {
		HPMessage message = new HPMessage(HPMessage.buildMessageId(), HPMessage.TYPE_VOICE);
		message.setTag(String.valueOf(AmrParse.parseFrameCount(strPathName)));
		
		onNewMessageEdited(message, true);
		
		FileHelper.copyFile(
				HPUserFilePathManager.getInstance().getMessageVoiceFilePath(message),
				strPathName);
		
		saveAndSendMessage(message);
	}

	@Override
	public void onSendPhoto() {
		launchPictureChoose(false);
	}

	@Override
	public void onSendCamera() {
        String path = FilePaths.getChatPictureChooseFilePath();
        File file = new File(path);
		launchCameraForChat(Uri.fromFile(file));
	}
	
	@Override
	protected void onPictureChooseResult(Intent data) {
		super.onPictureChooseResult(data);
		if(data != null){
			//SCApplication.print( "");
			HPMessage m = new HPMessage(
					HPMessage.buildMessageId(), HPMessage.TYPE_PHOTO);
			onNewMessageEdited(m, true);
			final String strPhotoPath = m.getPhotoFilePath();
			FileHelper.copyFile(strPhotoPath, FilePaths.getChatPictureChooseFilePath());
			saveAndSendMessage(m);
			
			mEditView.hideAllPullUpView(true);
		}
	}
	
	
	protected void redrawMessage(IMMessageProtocol m){
		if(mMessageAdapter.isIMMessageViewVisible(m)){
			mMessageAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
			AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
					AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
					AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected static class ChatAttribute{
		protected boolean 	mKeepScreenOn 	= true;
		
		protected int		mIdListView 	= R.id.lv;
		
		protected String	mFromId;
	}

	@Override
	public void onViewClicked(IMMessageProtocol message, int nViewId) {
		SCApplication.print( "chatActivity---onViewClicked");
		XMessage m = (XMessage)message;
		if (nViewId == R.id.viewContent) {
			final int nType = m.getType();
			if (nType == XMessage.TYPE_VOICE) {
				onVoiceContentViewClicked(m);
			}else if(nType == XMessage.TYPE_PHOTO){
				onPhotoContentViewClicked(m);
			}
		}else if(nViewId == R.id.ivAvatar){
			onAvatarClicked(m);
		}
	}
	
	protected void onAvatarClicked(XMessage m){
		if(m.isFromSelf()){
			PersonalInfoActivity.launch(this);
		}else{
			SCApplication.print( "对方业务系统id:" + m.getUserIdForInfo());
			requestGetUserInfoWithId(m.getUserIdForInfo(), false);
		}
	}
	
	protected void onPhotoContentViewClicked(XMessage m){
		if(m.isFromSelf()){
			if(!PhotoMessageUploadProcessor.getInstance().isUploading(m)){
				if(m.isUploadSuccess()){
					//viewDetailPhoto(m);
				}else{
					PhotoMessageUploadProcessor.getInstance().requestUpload(m);
				}
			}
		}else{
			if(!PhotoMessageDownloadProcessor.getInstance().isThumbPhotoDownloading(m)){
				if(m.isThumbPhotoFileExists()){
					//viewDetailPhoto(m);
				}else{
					PhotoMessageDownloadProcessor.getInstance().requestDownload(m, true);
					redrawMessage(m);
				}
			}
		}
	}

	@Override
	public boolean onViewLongClicked(IMMessageProtocol message, int nViewId) {
		return false;
	}
	
	protected void onVoiceContentViewClicked(XMessage m){
		/*if (m.isFromSelf()) {
			if (!m.isVoiceUploading()) {
				if (m.isUploadSuccess()) {
					if (m.isVoiceFileExists()) {
						if(VoicePlayProcessor.getInstance().isPlaying(m)){
							VoicePlayProcessor.getInstance().stop();
						}else{
							VoicePlayProcessor.getInstance().play(m);
						}
					} else {
						if (HospitalUtils.checkExternalStorageAvailable()) {
							VoiceMessageDownloadProcessor.getInstance().requestDownload(m);
						}
					}
				} else {
					VoiceMessageUploadProcessor.getInstance().requestUpload(m);
					redrawMessage(m);
				}
			}
		} else if (!VoiceMessageDownloadProcessor.getInstance().isDownloading(m)) {
			if (m.isVoiceFileExists()) {
				if(VoicePlayProcessor.getInstance().isPlaying(m)){
					VoicePlayProcessor.getInstance().stop();
				}else{
					VoicePlayProcessor.getInstance().play(m);
				}
			} else {
				if (HospitalUtils.checkExternalStorageAvailable()) {
					VoiceMessageDownloadProcessor.getInstance().requestDownload(m);
				}
			}
		}*/
	}
	

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if((scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE || 
				scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING )&& 
				mListView.getLastVisiblePosition() == mListView.getCount() - 1){
			mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		}else{
			mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onStartRun(PulldownableListView view) {
		if(mLastReadPosition >= 0){
			final int nLoadCount = loadOnePage();
			
			mListView.setSelection(nLoadCount - 1 + mListView.getHeaderViewsCount());
			
			mListView.endRun();
		}
	}
	
	protected List<IMMessageProtocol> addGroupTimeMessage(List<IMMessageProtocol> listMessage){
		List<IMMessageProtocol> listTemp = new ArrayList<IMMessageProtocol>();
		XMessage lastMessage = null;
		for(IMMessageProtocol m : listMessage){
			XMessage hm = (XMessage)m;
			XMessage timeMessage = checkOrCreateTimeMessage(hm,lastMessage);
			if(timeMessage != null){
				listTemp.add(timeMessage);
			}
			listTemp.add(m);
			lastMessage = hm;
		}
		return listTemp;
	}
	
	protected XMessage checkOrCreateTimeMessage(XMessage m,XMessage lastMessage){
		long groupTimeLast = lastMessage == null ? 0 : lastMessage.getGroupTime();
		if (m.getGroupTime() != groupTimeLast) {
			return HPMessage.createTimeMessage(m.getGroupTime());
		}
		return null;
	}
	
	protected int loadOnePage(){
		if(mLastReadPosition >= 0){
			List<IMMessageProtocol> listMessage = new LinkedList<IMMessageProtocol>();
			onLoadOnePageMessage(listMessage, mLastReadPosition);
			List<IMMessageProtocol> listTemp = addGroupTimeMessage(listMessage);
			
			mMessageAdapter.addAllItem(0, listTemp);
			
			onOnePageLoaded(listMessage.size());
			
			return listTemp.size();
		}else{
			mListView.setCanRun(false);
		}
		return 0;
	}
	
	protected void onOnePageLoaded(int nCount){
		mLastReadPosition -= nCount;
		if(mLastReadPosition < 0){
			mListView.setCanRun(false);
		}
	}
	
	protected void onLoadOnePageMessage(List<IMMessageProtocol> listMessage,int nPosition){
	}
	
	@Override
	protected void onSetCropExtra(Intent intent) {
		super.onSetCropExtra(intent);
		intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1); 
        intent.putExtra("return-data", false);
        String path = FilePaths.getChatPictureChooseFilePath();
        File file = new File(path);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, 
				Uri.fromFile(file));
	}

	@Override
	public void onEndRun(PulldownableListView view) {
		// TODO Auto-generated method stub
		
	}
}
