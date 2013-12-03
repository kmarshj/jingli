package com.zdht.jingli.groups;

public class DBColumns {
	
	public static class FacultyClassesStructure{
		public static final String TABLENAME = "FacultyClasses";
		public static final String COLUMN_SIGN = "sign";
		public static final String COLUMN_JSON = "jsondata";
	}
	
	
	public static class AddressListClassFriend{
		public static final String TABLENAME = "ClassFriend";
		public static final String COLUMN_SIGN = "sign";
		public static final String COLUMN_JSON = "jsondata";
	}
	
	public static class AddressListFriend{
		public static final String TABLENAME = "Frined";
		public static final String COLUMN_SIGN = "sign";
		public static final String COLUMN_JSON = "jsondata";
	}
	
	public static class AddressListGroup{
		public static final String TABLENAME = "Group";
		public static final String COLUMN_SIGN = "sign";
		public static final String COLUMN_JSON = "jsondata";
	}
	
	public static class Message{
		public static final String COLUMN_AUTOID 	= "autoid";
		public static final String COLUMN_ID 		= "messageid";
		public static final String COLUMN_TYPE 		= "messagetype";
		public static final String COLUMN_USERID	= "userid";
		public static final String COLUMN_USERIDFORINFO	= "useridforinfo";
		public static final String COLUMN_USERNAME	= "username";
		public static final String COLUMN_AVATAR	= "avatar";
		public static final String COLUMN_CONTENT	= "content";
		public static final String COLUMN_FROMSELF	= "fromself";
		public static final String COLUMN_SENDTIME	= "sendtime";
		public static final String COLUMN_GROUPTIME	= "grouptime";
		public static final String COLUMN_EXTENSION	= "extension";
		public static final String COLUMN_TAG		= "tag";
	}
	
	public static class SystemMessage{
		public static final String COLUMN_AUTOID 	= "autoid";
		public static final String COLUMN_ID 		= "userid";
		public static final String COLUMN_NAME 		= "username";
		public static final String COLUMN_AVATAR 	= "avatar";
		public static final String COLUMN_MESSAGE	= "message";
	}
	
	public static class RecentChatDB{
		public static final String TABLENAME			= "recentchat";
		public static final String COLUMN_ID			= "userid";
		public static final String COLUMN_USERIDFORINFO	= "useridforinfo";
		public static final String COLUMN_TYPE			= "type";
		public static final String COLUMN_NAME			= "name";
		public static final String COLUMN_AVATAR	    = "avatar";
		public static final String COLUMN_UNREADCOUNT	= "unreadcount";
		public static final String COLUMN_UPDATETIME	= "updatetime";
	}
	
}
