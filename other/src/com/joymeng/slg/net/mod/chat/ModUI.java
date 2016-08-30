package com.joymeng.slg.net.mod.chat;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.mod.ClientModule;


/**
 * 所有客户端属性模块
 * @author Shaolong Wang
 *
 */
public abstract class ModUI implements ClientModule,Instances
{
//	public static final short UI_TYPE_CHAT_SOCAIL_PLAYER_BASIC_INFO_SEND = 0;//公用的UI模块
////	public static final short UI_TYPE_CHAT_GUILD_MSGS_SEND = 1;//联盟聊天消息的下发
//	public static final short UI_TYPE_CHAT_GROUP_MSG_MAX_ID_SEND = 2;//讨论组消息最大Id的发送	
//	public static final short UI_TYPE_CHAT_GROUP_MSGS_SEND = 3;//讨论组消息的发送
//	public static final short UI_TYPE_CHAT_GROUP_NAME_CHANGE = 4;//讨论组名字改变
//	public static final short UI_TYPE_CHAT_GROUPS_UPDATE = 5;//讨论组的更新
//	public static final short UI_TYPE_CHAT_ROLES_UPDATE = 6;//聊天成员的更新
//	public static final short UI_TYPE_CHAT_MSG_SEND = 7;//聊天消息的发送
//	public static final short UI_TYPE_WORLD_CHAT_MSGS_SEND = 8;//世界聊天消息的下发
	//...
	public static final short UI_TYPE_WORLD_CHAT_MSGS_SEND = 8;	 	//世界聊天消息的下发
	public static final short UI_TYPE_WORLD_CHAT_MSGS_UPDATE = 9; 	//世界更新聊天消息的下发
	public static final short UI_TYPE_UNION_CHAT_MSGS_SEND = 10;	//联盟聊天消息的下发
	public static final short UI_TYPE_UNION_CHAT_MSGS_UPDATE = 11; 	//联盟更新聊天消息的下发
	public static final short UI_TYPE_GROUP_CHAT_MSGS_SEND = 12;	//群组聊天消息的下发
	public static final short UI_TYPE_GROUP_CHAT_MSGS_UPDATE = 13; 	//群组更新聊天消息的下发
	public static final short UI_TYPE_ROLE_GROUP_SEND = 14; 		//用户组下发
	public static final short UI_TYPE_ROLE_GROUP_UPDATE = 15; 		//用户更新组下发
	public static final short UI_TYPE_REPORT_SEND = 6; 		//报告下发
	public static final short UI_TYPE_REPORT_UPDATE = 7; 	//报告更新下发
	
	public static final short UI_TYPE_WORLD_NOTICE_MSG_SEND = 5; //世界公告消息下发
	
	@Override
	public short getModuleType()
	{
		return NTC_DTCD_CHAT;
	}
	@Override
	public void serialize(JoyBuffer out)
	{
		out.putShort(getSubModuleType());
		subserialize(out);
	}
	
	public abstract void subserialize(JoyBuffer out);
	public abstract short getSubModuleType();
	
}
