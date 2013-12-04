package com.zdht.jingli.groups.model;

import java.io.File;

import android.database.Cursor;

import com.zdht.hospital.messageprocessor.PhotoMessageDownloadProcessor;
import com.zdht.hospital.messageprocessor.PhotoMessageUploadProcessor;
import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.DBColumns;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.HPUserFilePathManager;
import com.zdht.jingli.groups.event.DBMessageEvent;
import com.zdht.jingli.groups.parampool.DBHandleType;
import com.zdht.jingli.groups.parampool.DBMessageParam;
import com.zdht.jingli.groups.utils.SchoolUtils;

public class HPMessage extends XMessage {
	
	//public static final int TYPE_ADDFRIENDASK = 20;
	
	private static final int EXTENSION_FRIENDASK_HANDLED = 0;
	
	public HPMessage(String strId,int nType){
		super(strId, nType);
	}
	
	public HPMessage(Cursor cursor){
		mId = cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_ID));
		mType = cursor.getInt(cursor.getColumnIndex(DBColumns.Message.COLUMN_TYPE));
		mUserId = cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_USERID));
		mUserIdForInfo = cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_USERIDFORINFO));
		mUserName = cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_USERNAME));
		mAvatar =  cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_AVATAR));
		mContent = cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_CONTENT));
		mIsFromSelf = SchoolUtils.getCursorBoolean(cursor, 
				cursor.getColumnIndex(DBColumns.Message.COLUMN_FROMSELF));
		mSendTime = cursor.getLong(cursor.getColumnIndex(DBColumns.Message.COLUMN_SENDTIME));
		mGroupTime = cursor.getLong(cursor.getColumnIndex(DBColumns.Message.COLUMN_GROUPTIME));
		final int nExtension = cursor.getInt(cursor.getColumnIndex(DBColumns.Message.COLUMN_EXTENSION));
		setExtension(nExtension);
		mTag = cursor.getString(cursor.getColumnIndex(DBColumns.Message.COLUMN_TAG));
		
		setStoraged();
	}
	
	public static HPMessage createTimeMessage(long lGroupTime){
		HPMessage m = new HPMessage(buildMessageId(), TYPE_TIME);
		m.mGroupTime = lGroupTime;
		return m;
	}

	@Override
	protected void onUpdateDB() {
		DBMessageParam param = new DBMessageParam(DBHandleType.WRITE);
		param.mSaveMessage = this;
		AndroidEventManager.getInstance().runEvent(
				new DBMessageEvent(EventCode.DB_MessageHandle), param);
	}
	
	public void setAddFriendAskHandled(boolean bHandled){
		setExtension(EXTENSION_FRIENDASK_HANDLED, bHandled);
	}
	
	public boolean isAddFriendAskHandled(){
		return mExtension[EXTENSION_FRIENDASK_HANDLED];
	}
	
	public boolean	isVoiceFileExists(){
		if(mType == HPMessage.TYPE_VOICE){
			return new File(HPUserFilePathManager.getInstance().getMessageVoiceFilePath(this)).exists();
		}else{
			throw new IllegalArgumentException("voiceFile just use type = voice");
		}
	}
	
	public boolean	isPhotoFileExists(){
		return new File(getPhotoFilePath()).exists();
	}
	
	public boolean 	isThumbPhotoFileExists(){
		return new File(getThumbPhotoFilePath()).exists();
	}
	
	public String	getPhotoFilePath(){
		if(mType == HPMessage.TYPE_PHOTO){
			return HPUserFilePathManager.getInstance().getMessagePhotoFilePath(this);
		}else{
			throw new IllegalArgumentException("photofile just use type = photo");
		}
	}
	
	public String	getThumbPhotoFilePath(){
		if(mType == HPMessage.TYPE_PHOTO){
			return HPUserFilePathManager.getInstance().getMessagePhotoThumbFilePath(this);
		}else{
			throw new IllegalArgumentException("photofile just use type = photo");
		}
	}

	@Override
	public boolean isThumbPhotoDownloading() {
		return PhotoMessageDownloadProcessor.getInstance().isThumbPhotoDownloading(this);
	}

	@Override
	public int getThumbPhotoDownloadPercentage() {
		return PhotoMessageDownloadProcessor.getInstance().getThumbPhotoDownloadPercentage(this);
	}

	@Override
	public boolean isPhotoUploading() {
		return PhotoMessageUploadProcessor.getInstance().isUploading(this);
	}

	@Override
	public int getPhotoUploadPercentage() {
		return PhotoMessageUploadProcessor.getInstance().getUploadPercentage(this);
	}

	/*@Override
	public boolean isVoiceDownloading() {
		//return VoiceMessageDownloadProcessor.getInstance().isDownloading(this);
	}

	@Override
	public boolean isVoiceUploading() {
		return VoiceMessageUploadProcessor.getInstance().isUploading(this);
	}

	@Override
	public String getVoiceFilePath() {
		return HPUserFilePathManager.getInstance().getMessageVoiceFilePath(this);
	}*/
}
