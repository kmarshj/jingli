package com.zdht.jingli.groups;



public class EventCode{
	
	private static int CODE_INC = 0;
	
	public static final int ZERO_CODE = CODE_INC++;
	
	
	
	public static final int HTTPPOST_Login                      = CODE_INC++;
	
	public static final int HTTPPOST_GetPhone                   = CODE_INC++;
	
	public static final int HTTPPOST_PostActivityImage          = CODE_INC++;
	
	public static final int HTTPPOST_PostLiveImage          	= CODE_INC++;
	
	public static final int HTTPPOST_PostGroupAvatar          	= CODE_INC++;
	
	public static final int HTTPPOST_PostAvatar                 = CODE_INC++;
	
	public static final int HTTPPOST_EditInfo                   = CODE_INC++;
	
	public static final int HTTPPOST_CreateGroup                = CODE_INC++;
	
	public static final int HTTPPOST_CreateActivity             = CODE_INC++;
	
	public static final int HTTPPOST_EditActivity               = CODE_INC++;
	
	public static final int HTTPPOST_HandlerAddFriendMessage    = CODE_INC++;
	
	public static final int HTTPGET_QuitActivity                = CODE_INC++;
	
	public static final int HTTPGET_DeleteActivityMember        = CODE_INC++;
	
	public static final int HTTPGET_DelActivity					= CODE_INC++;
	
	public static final int HTTPGET_AddActivity                 = CODE_INC++;
	
	public static final int HTTPGET_GetLiveInfo                 = CODE_INC++;
	
	public static final int HTTPGET_GetGroupInfo                = CODE_INC++;
	
	public static final int HTTPGET_SendLive                    = CODE_INC++;
	
	public static final int HTTPGET_GetActivityMember           = CODE_INC++;
	
	public static final int HTTPGET_GetClassUser                = CODE_INC++;
	
	public static final int HTTPGET_GetKeyWordUser              = CODE_INC++;
	
	public static final int HTTPPOST_EditGroup                  = CODE_INC++;
	
	public static final int HTTPGET_GetUserInfo					= CODE_INC++;
	
	public static final int HTTPGET_GetCurrentActivities		= CODE_INC++;
	
	public static final int HTTPGET_GetMyCreateActivioes		= CODE_INC++;
	
	public static final int HTTPGET_GetGroupOnGoingActivioes	= CODE_INC++;
	
	public static final int HTTPGET_GetUserAddedGroup	        = CODE_INC++;
	
	public static final int HTTPGET_GetGroupPastActivioes	    = CODE_INC++;
	
	public static final int HTTPGET_GetMyAddActivioes	        = CODE_INC++;
	
	public static final int HTTPGET_GetFacultiesClasses    	    = CODE_INC++;
	
	public static final int HTTPGET_GetAddressList      	    = CODE_INC++;
	
	public static final int HTTPGET_QuitGroup               	= CODE_INC++;
	
	public static final int HTTPGET_AddGroup               	    = CODE_INC++;
	
	public static final int HTTPGET_AddFriend              	    = CODE_INC++;
	
	public static final int HTTPGET_GetMyCreateGroups			= CODE_INC++;
	
	public static final int HTTPGET_Feedback   					= CODE_INC++;
	
	public static final int HTTPGET_GetGroups		        	= CODE_INC++;
	
	public static final int HTTPGET_GetKeyWordGroups        	= CODE_INC++;
	
	public static final int HTTPGET_DelRelationShip        		= CODE_INC++;
	
	public static final int HTTPGET_GetStuNo        			= CODE_INC++;
	
	public static final int HTTPGET_DelComMember        		= CODE_INC++;
	
	public static final int HTTPGET_GetNews						= CODE_INC++;
	
	public static final int HTTPGET_GetActivityById				= CODE_INC++;
	
	public static final int HTTPPOST_FindPassword				= CODE_INC++;
	
	public static final int SC_DownloadImage					= CODE_INC++;
	
	public static final int HP_LoginActivityCreated				= CODE_INC++;
	
	public static final int HP_RegisterActivityCreated		    = CODE_INC++;
	
	public static final int HP_RecentChatChanged				= CODE_INC++;
	
	public static final int HP_LoginGetOrChangeInfoSuccess      = CODE_INC++;
	
	public static final int HP_UnreadMessageCountChanged		= CODE_INC++;
	
	public static final int DB_FacultyClassesStructureHandle    = CODE_INC++;
	
	public static final int DB_ClassFriend    					= CODE_INC++;
	
	public static final int DB_FRIEND    						= CODE_INC++;
	
	public static final int DB_GROUP    					    = CODE_INC++;
	
	public static final int DB_MessageHandle				    = CODE_INC++;
	
	public static final int DB_RecentChat    				    = CODE_INC++;
	
	public static final int E_RecordStart						= CODE_INC++;
	public static final int E_RecordFail						= CODE_INC++;
	public static final int E_RecordStop						= CODE_INC++;
	public static final int E_RecordExceedMaxTime 				= CODE_INC++;
	public static final int E_RecordInterrupt					= CODE_INC++;
	
	
	public static final int IM_SystemStarted					= CODE_INC++;
	public static final int IM_StatusQuery 						= CODE_INC++;
	public static final int IM_SetLoginUser						= CODE_INC++;
	public static final int IM_Login							= CODE_INC++;
	public static final int IM_Register							= CODE_INC++;
	public static final int IM_LoginStart						= CODE_INC++;
	public static final int IM_ConnectionInterrupt				= CODE_INC++;
	public static final int IM_ChangePassword					= CODE_INC++;
	
	
	public static final int IM_LoginOuted						= CODE_INC++;
	
	public static final int IM_ReceiveMessage				    = CODE_INC++;
	
	public static final int IM_ReceiveSystemMessage			    = CODE_INC++;
	
	public static final int IM_SendMessage  				    = CODE_INC++;
	public static final int IM_SendMessageStart				    = CODE_INC++;
	
	
	
	public static final int HP_PostPhoto					    = CODE_INC++;
	
	public static final int HP_PostPhotoPercentChanged		    = CODE_INC++;
	
	public static final int HP_DownloadThumbPhoto			    = CODE_INC++;
	
	public static final int HP_DownloadThumbPhotoPercentChanged = CODE_INC++;
	
	public static final int HP_DownloadPhoto					= CODE_INC++;
	
	public static final int HP_DownloadPhotoPercentChanged 		= CODE_INC++;

}
