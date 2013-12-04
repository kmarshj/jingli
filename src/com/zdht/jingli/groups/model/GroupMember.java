package com.zdht.jingli.groups.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupMember implements Parcelable {
	
	/** 用户id:业务id */
	public int mUserId;
	/** 用户登录账号 */
	public String mUserName;
	/** 用户名称 */
	public String mName;
	/** 头像地址 */
	public String mAvatar;
	/** 性别 */
	public String mSex;
	/** 学院 */
	public String mFaculty;
	/** 专业/主修 */
	public String mMajor;
	/** 班级 */
	public String mClass;
	/** 签名 */
	public String mSignature;
	/** 电话 */
	public String mPhone;
	/** 是否好友 */
	public boolean isFriend;
	/** 加入时间 */
	public String entryTime;
	/** 0：创建者；1：管理员；2：普通成员 */
	public String role;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mUserId);
		dest.writeString(mUserName);
		dest.writeString(mName);
		dest.writeString(mAvatar);
		dest.writeString(mSex);
		dest.writeString(mFaculty);
		dest.writeString(mMajor);
		dest.writeString(mClass);
		dest.writeString(mSignature);
		dest.writeString(mPhone);
		dest.writeString(String.valueOf(isFriend));
		dest.writeString(entryTime);
		dest.writeString(role);
	}
	
	public static final Parcelable.Creator<GroupMember> CREATOR = new Parcelable.Creator<GroupMember>() {

		@Override
		public GroupMember createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			GroupMember member = new GroupMember();
			member.mUserId  = arg0.readInt();
			member.mUserName = arg0.readString();
			member.mName = arg0.readString();
			member.mAvatar = arg0.readString();
			member.mSex = arg0.readString();
			member.mFaculty = arg0.readString();
			member.mMajor = arg0.readString();
			member.mClass = arg0.readString();
			member.mSignature = arg0.readString();
			member.mPhone = arg0.readString();
			member.isFriend = Boolean.valueOf(arg0.readString());
			member.entryTime = arg0.readString();
			member.role = arg0.readString();
			return member;
		}

		@Override
		public GroupMember[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new GroupMember[arg0];
		}   
	 
	};
}
