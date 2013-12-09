package com.zdht.jingli.groups;

public class URLUtils {

	
	public final static String KEY				= "jinliApp"; 
	
	private final static String COMMON_URL      = "http://180.96.23.114:3000/"; 
	
	/*
	 * 
	 * POST
	 * 
	 */
	public static String URL_Login  			= COMMON_URL + "login?";
	
	public static String URL_GetPhone  			= COMMON_URL + "getBindPhone?";
	
	public static String URL_FindPassword		= COMMON_URL + "retrievePassword?";
	
	public static String URL_PostFile			= COMMON_URL + "uploadFile?";
	
	public static String URL_EDITINFO			= COMMON_URL + "setUserInfo?";
	
	public static String URL_CreateGroup		= COMMON_URL + "foundCommunity?";
	
	public static String URL_HandlerAddFriend	= COMMON_URL + "act=friend&do=checkFriendRequest";
	
	public static String URL_CreateActivity		= COMMON_URL + "foundActivity?";
	
	public static String URL_EditActivity		= COMMON_URL + "modifyActivity?";
	
	public static String URL_EditGroup		    = COMMON_URL + "modifyCommunity?";
	
	public static String URL_SendMessage		= COMMON_URL + "act=friend&do=sendpms";
	
	public static String URL_SendGroupMessage	= COMMON_URL + "act=mtag&do=sendMtagGroupChat";
	/*
	 * 
	 * GET
	 * 
	 */
	
	public static String URL_GetUserinfo        = COMMON_URL + "getUserInfo?" + 
						"appId=" + KEY + "&queryId=%s";
	
	public static String URL_GetActivities      = COMMON_URL + "getActivity?" + 
						"appId=" + KEY + "&type=%s&communityId=%s&page=%s";
	
	public static String URL_GetGroups          = COMMON_URL + "inquireCommunity?" + 
						"appId=" + KEY + "&type=%s&queryUserId=%s&keyword=%s&page=%s";
	
	public static String URL_QuitActivity       = COMMON_URL + "quitActivity?" + 
						"appId=" + KEY + "&activityId=%s";
	
	public static String URL_AddGroup           = COMMON_URL + "joinCommunity?" + 
						"appId=" + KEY + "&communityId=%s";
	
	public static String URL_AddFriend          = COMMON_URL + "addRelationship?" + 
						"appId=" + KEY + "&destId=%s";
	
	public static String URL_QuitGroup          = COMMON_URL + "quitCommunity?" + 
						"appId=" + KEY + "&communityId=%s";
	
	public static String URL_AddActivity       =  COMMON_URL + "joinActivity?" + 
						"appId=" + KEY + "&activityId=%s&phone=%s&remark=%s";
	
	public static String URL_DelActivity	   = COMMON_URL + "delActivity?" + 
			"appId=" + KEY + "&activityId=%s";
	
	public static String URL_GetLiveInfo       = COMMON_URL + "getLiveBroadcast?" + 
						"appId=" + KEY + "&activityId=%s&page=%s";
	
	public static String URL_GetUser           = COMMON_URL + "inquireUser?" + 
						"appId=" + KEY + "&type=%s&specific=%s&page=%s";
	
	public static String URL_SendLive          = COMMON_URL + "sendLiveBroadcast?" + 
						"appId=" + KEY + "&activityId=%s&text=%s&image=%s";
	
	public static String URL_GetGroupInfo      = COMMON_URL + "getCommunityInfo?" + 
						"appId=" + KEY + "&communityId=%s";
	
	public static String URL_GetFaculty        = COMMON_URL + "act=student&do=getAllYuanXiBanJi" + 
						"appId=" + KEY + "&versions=%s";
	
	public static String URL_GetAddressList    = COMMON_URL + "getAddressList?" + 
						"appId=" + KEY + "&version=%s&type=%s";
	
	public static String URL_GetAddressGroup   = COMMON_URL + "act=student&do=getMtagAddress" + 
						"appId=" + KEY + "&versions=%s";
	
	public static String URL_Feedback          = COMMON_URL + "userFeedback?" + 
						"appId=" + KEY + "&feedback=%s";
	
	/** 删除用户关系 */
	public static String URL_DelRelationShip	=  COMMON_URL + "delRelationship?" + 
						"appId=" + KEY + "&destId=%s";
	/** 通过身份证查看学号 */
	public static String URL_GetStuNo = COMMON_URL + "getStuNo?" + "appId=" + KEY + "&identity=%s";
	
	/** 从圈子中删除成员 */
	public static String URL_DelComMember = COMMON_URL + "delComMember?" + "appId=" + KEY + "&communityId=%s" + "&members=%s";
	
	/** 查询资讯 */
	public static String URL_GetNews = "http://118.114.52.30:3000/news/%s/article?appId=schoolApp&page=%s";
	
	/** 通过活动id查看活动详情 */
	public static String URL_GetActivityById = COMMON_URL + "getActivityById?" + "appId=" + KEY + "&activityId=%s";
}
