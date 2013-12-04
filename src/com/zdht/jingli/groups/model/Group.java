package com.zdht.jingli.groups.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 圈子成员 */
	private static final String JSON_NAME_MEMBER = "members";
	
	private final int     mId;
	private final String  mAvatarUrl;
	private final String  mName;
	private List<String>  mListAdmin;
	private List<Integer>  mLisetAdminId;
	private String  mMemberNum;
	private final String  mExplanation;
	private boolean 	  mIsAdd;
	private String        mRejectReason;
	private boolean       mNeedVerification;
	
	private String mRemark;
	
	private ArrayList<GroupMember> members;

	public Group(JSONObject jsonObject, boolean isGetGroupInfo) throws JSONException {
		mId          = jsonObject.getInt("communityId");
		mAvatarUrl   = jsonObject.getString("avatar");
		mName        = jsonObject.getString("communityName");
		/*mAdmin       = jsonObject.getString("admins");
		mAdminId     = jsonObject.getInt("adminid");*/
		
		if(jsonObject.toString().contains("admins")){
			final JSONArray jsonArray = jsonObject.getJSONArray("admins");
			mListAdmin = new ArrayList<String>();
			mLisetAdminId = new ArrayList<Integer>();
			for (int i = 0; i < jsonArray.length(); i++) {
				final JSONObject js = jsonArray.getJSONObject(i);
				mListAdmin.add(js.getString("name"));
				mLisetAdminId.add(js.getInt("adminId"));
			}
		}
		
		if(jsonObject.has(JSON_NAME_MEMBER)){
			members = new ArrayList<GroupMember>();
			final JSONArray memberJArray = jsonObject.getJSONArray(JSON_NAME_MEMBER);
			for(int i = 0; i < memberJArray.length(); i++) {
				GroupMember mGroupMember = new GroupMember();
				final JSONObject member = memberJArray.getJSONObject(i);
				mGroupMember.mUserId = member.getInt("userId");
				mGroupMember.mUserName = member.getString("userName");
				mGroupMember.mName = member.getString("name");
				mGroupMember.mAvatar = member.getString("avatar");
				mGroupMember.mSex = member.getString("sex");
				mGroupMember.mFaculty = member.getString("faculty");
				mGroupMember.mMajor = member.getString("major");
				mGroupMember.mClass = member.getString("class");
				mGroupMember.mSignature = member.getString("signature");
				mGroupMember.mPhone = member.getString("phone");
				mGroupMember.isFriend = member.getBoolean("isFriend");
				mGroupMember.entryTime = member.getString("entryTime");
				mGroupMember.role = member.getString("role");
				members.add(mGroupMember);
			}
		}
		
		if(jsonObject.has("remark")) {
			mRemark = jsonObject.getString("remark");
		} else {
			mRemark = "";
		}
		
		mMemberNum   = jsonObject.getString("memberNum");
		mExplanation = jsonObject.getString("announcement");

		//mStatus      = jsonObject.getInt("verify");
		/*if(mStatus == 2){
			mRejectReason = jsonObject.getString("rejectreason");
		}*/
		
		if(jsonObject.toString().contains("permission")){
			mNeedVerification = jsonObject.getInt("permission") == 1;
		}
		
		if(jsonObject.toString().contains("isAdd")){
			mIsAdd = jsonObject.getBoolean("isAdd");
		}else if(jsonObject.toString().contains("isMem")) {
			mIsAdd = jsonObject.getBoolean("isMem");
		} else {
			mIsAdd = false;
		}
	}

	public int getId() {
		return mId;
	}

	public String getAvatarUrl() {
		return mAvatarUrl;
	}

	public String getName() {
		return mName;
	}

	public ArrayList<GroupMember> getMembers() {
		return members;
	}
	
	public void setMembers(ArrayList<GroupMember> members) {
		this.members = members;
	}
	/*public String getAdmin() {
		return mAdmin;
	}

	public int getAdminId() {
		return mAdminId;
	}*/
	
	
	

	public String getMemberNum() {
		return mMemberNum;
	}
	
	public void setMemberNum(int num) {
		mMemberNum = String.valueOf(num);
	}
	
	public void setMemberNum(String num) {
		mMemberNum = num;
	}

	public List<String> getmListAdmin() {
		return mListAdmin;
	}

	public List<Integer> getmLisetAdminId() {
		return mLisetAdminId;
	}

	public String getExplanation() {
		return mExplanation;
	}
	
	public String getRemark() {
		return mRemark;
	}

	public boolean isIsAdd() {
		return mIsAdd;
	}

	public void setIsAdd(boolean isAdd){
		mIsAdd = isAdd;
	}
	
	/*public int getStatus() {
		return mStatus;
	}*/

	public String getRejectReason() {
		return mRejectReason;
	}

	public boolean isNeedVerification() {
		return mNeedVerification;
	}


}
