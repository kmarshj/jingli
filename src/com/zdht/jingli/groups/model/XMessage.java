package com.zdht.jingli.groups.model;

import java.util.Locale;

import android.content.ContentValues;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.groups.DBColumns;
import com.zdht.utils.SystemUtils;

public abstract class XMessage implements IMMessageProtocol{
	public static final int TYPE_TIME 		= 0;
	public static final int TYPE_TEXT 		= 1;
	public static final int TYPE_PHOTO		= 2;
	public static final int TYPE_VOICE 		= 3;
	
	public static final int FROMTYPE_SINGLE 	= 1;
	public static final int FROMTYPE_CHATROOM 	= 2;
	
	protected static final int EXTENSION_COUNT = 8;
	
	protected static final int EXTENSION_SENDED 			= 0;
	protected static final int EXTENSION_SENDSUCCESS 		= 1;
	protected static final int EXTENSION_DOWNLOADED 		= 2;
	protected static final int EXTENSION_UPLOADSUCCESS 		= 3;
	protected static final int EXTENSION_PLAYED 			= 4;
	
	protected String 		mId;
	protected int			mType;
	
	protected String		mUserId;
	
	protected String		mUserName;
	
	protected String		mUserIdForInfo;
	
	protected String        mAvatar;
	
	protected String		mContent;
	
	protected boolean		mIsFromSelf;
	/** 单聊还是群聊 */
	protected int			mFromType;
	
	protected long			mSendTime;
	
	protected long			mGroupTime;
	
	protected String		mGroupId;
	
	protected boolean		mExtension[] = new boolean[EXTENSION_COUNT];
	
	protected String		mTag;
	
	protected boolean		mReaded;
	protected boolean 		mStoraged;
	
	protected ContentValues mContentValues = new ContentValues();
	
	public XMessage(){
		
	}
	
	public XMessage(String strId,int nType){
		mId = strId;
		mType = nType;
		
		mContentValues.put(DBColumns.Message.COLUMN_ID, mId);
		mContentValues.put(DBColumns.Message.COLUMN_TYPE, mType);
	}
	
	/** 构建消息id */
	public static String buildMessageId(){
		return String.valueOf(System.currentTimeMillis()) + SystemUtils.randomRange(100, 999);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o){
			return true;
		}
		if(o != null && o instanceof HPMessage){
			return ((HPMessage)o).getId().equals(getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mId.hashCode();
	}

	@Override
	public String 	getId() {
		return mId;
	}

	@Override
	public int 		getType() {
		return mType;
	}
	
	public String	getUserId(){
		return mUserId;
	}
	
	public String getUserIdForInfo(){
		return mUserIdForInfo;
	}
	
	public String	getUserName(){
		return mUserName;
	}
	
	public String	getAvatar(){
		return mAvatar;
	}
	
	public String	getContent(){
		return mContent;
	}

	public boolean	isFromSelf(){
		return mIsFromSelf;
	}
	
	public int		getFromType(){
		return mFromType;
	}
	
	public long		getSendTime(){
		return mSendTime;
	}
	
	public long 	getGroupTime(){
		return mGroupTime;
	}
	
	public String	getGroupId(){
		return mGroupId;
	}
	
	public String	getTag(){
		return mTag;
	}
	
	/** 获取接收者id, 群聊为群id,单聊为个人id */
	public String 	getSendToId(){
		if(mFromType == FROMTYPE_CHATROOM){
			return mGroupId;
		}else if(mFromType == FROMTYPE_SINGLE){
			return mUserId;
		}
		return null;
	}
	
	public String 	getFromId(){
		return getSendToId();
	}
	
	public String	getPhotoDownloadUrl(){
		return getTag();
	}
	
	public String	getThumbPhotoDownloadUrl(){
		return getContent();
	}
	
	public void		setUserId(String strUserId){
		mUserId = strUserId.toUpperCase(Locale.CHINA);
		mContentValues.put(DBColumns.Message.COLUMN_USERID, mUserId);
	}
	
	public void		setUserIdForInfo(String mUserIdForInfo){
		this.mUserIdForInfo = mUserIdForInfo;
		mContentValues.put(DBColumns.Message.COLUMN_USERIDFORINFO, mUserIdForInfo);
	}

	
	
	public void 	setUserName(String strUserName){
		mUserName = strUserName;
		mContentValues.put(DBColumns.Message.COLUMN_USERNAME, mUserName);
	}
	
	public void 	setAvatar(String strAvatar){
		mAvatar = strAvatar;
		mContentValues.put(DBColumns.Message.COLUMN_AVATAR, mAvatar);
	}
	
	public void		setContent(String strContent){
		mContent = strContent;
		mContentValues.put(DBColumns.Message.COLUMN_CONTENT, mContent);
	}
	
	public void		setFromSelf(boolean bFromSelf){
		mIsFromSelf = bFromSelf;
		mContentValues.put(DBColumns.Message.COLUMN_FROMSELF, mIsFromSelf);
	}
	
	public void		setFromType(int nFromType){
		mFromType = nFromType;
	}
	
	public void		setSendTime(long lTime){
		mSendTime = lTime;
		mContentValues.put(DBColumns.Message.COLUMN_SENDTIME, mSendTime);
	}
	
	public void		setGroupTime(long lTime){
		mGroupTime = lTime;
		mContentValues.put(DBColumns.Message.COLUMN_GROUPTIME, mGroupTime);
	}
	
	public void 	setGroupId(String strGroupId){
		if(mFromType == FROMTYPE_CHATROOM){
			mGroupId = strGroupId;
		}else{
			throw new IllegalArgumentException("GroupId just use for FromType = Group");
		}
	}
	
	public void		setTag(String strTag){
		mTag = strTag;
		mContentValues.put(DBColumns.Message.COLUMN_TAG, mTag);
	}
	
	public boolean	isSended(){
		return mExtension[EXTENSION_SENDED];
	}
	
	public boolean 	isSendSuccess(){
		return mExtension[EXTENSION_SENDSUCCESS];
	}
	
	public boolean 	isDownloaded(){
		return mExtension[EXTENSION_DOWNLOADED];
	}
	
	public boolean 	isPlayed(){
		return mExtension[EXTENSION_PLAYED];
	}
	
	public boolean 	isUploadSuccess(){
		return mExtension[EXTENSION_UPLOADSUCCESS];
	}
	
	public void setSended(){
		setExtension(EXTENSION_SENDED,true);
	}
	
	public void setSendSuccess(boolean bSuccess) {
		setExtension(EXTENSION_SENDSUCCESS, bSuccess);
	}

	public void setDownloaded() {
		setExtension(EXTENSION_DOWNLOADED, true);
	}

	public void setUploadSuccess(boolean bSuccess) {
		setExtension(EXTENSION_UPLOADSUCCESS, bSuccess);
	}

	public void setPlayed(boolean bPlayed) {
		setExtension(EXTENSION_PLAYED, bPlayed);
	}
	
	protected void setExtension(int nIndex,boolean bValue){
		if(mExtension[nIndex] != bValue){
			mExtension[nIndex] = bValue;
			mContentValues.put(DBColumns.Message.COLUMN_EXTENSION, getExtension());
		}
	}
	
	protected void setExtension(int nExtension){
		for(int nIndex = 0;nIndex < EXTENSION_COUNT;++nIndex){
			mExtension[nIndex] = (((nExtension >> nIndex) & 0x01) == 1);
		}
	}
	
	protected int getExtension(){
		int nExtension = 0;
		for(int nIndex = 0;nIndex < EXTENSION_COUNT;++nIndex){
			nExtension = ((mExtension[nIndex] ? 1 : 0) << nIndex) | nExtension;
		}
		return nExtension;
	}
	
	public boolean	isStoraged(){
		return mStoraged;
	}
	
	public ContentValues getSaveContentValues(){
		return mContentValues;
	}
	
	public boolean 	isReaded(){
		return mReaded;
	}
	
	public void 	setStoraged(){
		mContentValues.clear();
		mStoraged = true;
	}
	
	public void 	setReaded(boolean bReaded){
		mReaded = bReaded;
	}
	
	public void updateDB(){
		if(isStoraged()){
			onUpdateDB();
		}
	}
	
	protected abstract void onUpdateDB();
	
	public abstract boolean isVoiceFileExists();
	
	//public abstract String	getVoiceFilePath();
	
	//public abstract boolean	isVoiceDownloading();
	
	//public abstract boolean	isVoiceUploading();
	
	public abstract boolean	isPhotoFileExists();
	
	public abstract boolean isThumbPhotoFileExists();
	
	public abstract String	getPhotoFilePath();
	
	public abstract String	getThumbPhotoFilePath();
	
	public abstract boolean isThumbPhotoDownloading();
	
	public abstract int		getThumbPhotoDownloadPercentage();
	
	public abstract boolean	isPhotoUploading();
	
	public abstract int		getPhotoUploadPercentage();
}
