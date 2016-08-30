package com.joymeng.slg.net.mod.chat;

import java.util.List;

import sun.misc.BASE64Decoder;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MyDes3;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatGroup;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.ChatRole;


public abstract class AbstractModChat extends ModUI {

	public void send_chat_roles(JoyBuffer out,List<ChatRole> roles) {
		out.put((byte) roles.size());
		for (int i = 0 ; i < roles.size() ; i++){
			ChatRole role = roles.get(i);
			out.putLong(role.getUid());//玩家ID
		}
	}
	//打包消息
	public void sendChatMsgs(JoyBuffer out,ChatMsg[] msgs) {
		int num = msgs.length;
		out.putInt(num);					//聊天数量
		for (int i = 0 ; i < num ; i++){
			ChatMsg msg = msgs[i];
			if (msg == null) {
				continue;
			}
			out.put((byte)msg.getChannelType().ordinal());	//聊天频道 	1-世界  2-联盟 3-群组
			out.putLong(msg.getGroupId());					//↓			0      0     groupId 
			out.putLong(msg.getId());						//信息的编号
			out.put(msg.getMsgType());		//聊天消息内容的类型
			out.put(msg.getReportType());	//报告的类型★
			ChatRole sender = msg.getSender();
			if (sender != null) {
				out.putInt(1);		//正常消息
				out.putLong(sender.getUid());
				out.putLong(sender.getUnionId());
				if(unionManager.search(sender.getUnionId()) == null){
					out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);
				}else {
					out.putPrefixedString(unionManager.search(sender.getUnionId()).getShortName(),JoyBuffer.STRING_TYPE_SHORT);//联盟简称
				}
				sender.getVipInfo().serialize(out);
				out.putPrefixedString(sender.getName(),JoyBuffer.STRING_TYPE_SHORT);
				out.put(sender.getIcon().getIconId());
				out.put(sender.getIcon().getIconType());
				if(sender.getIcon().getIconType() == 0){
					out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);
				}else if(sender.getIcon().getIconType() == 1){
					out.putPrefixedString(sender.getIcon().getIconName(),JoyBuffer.STRING_TYPE_SHORT);
				}
			}else {
				out.putInt(0);		//系统消息
			}
			out.putLong(msg.getSendDate());//发送时间
			byte[] datas = null;
			try {
				out.putPrefixedString(msg.getMsgTitle(), JoyBuffer.STRING_TYPE_SHORT);// 邮件的标题
				datas = msg.getToClientMsg().getBytes("UTF-8");
				out.putInt(datas.length);
				out.put(datas);//内容
				if (msg.getMsgAnnex().size() < 1) {
					out.putInt(0);
				}else{
					String string = JsonUtil.ObjectToJsonString(msg.getMsgAnnex());
					byte[] datas2 = string.getBytes("UTF-8");
					byte[] key = new BASE64Decoder().decodeBuffer("EC39A21EAFABA68F244340EACE4B0F0F");
					byte[] result = MyDes3.des3EncodeECB(key, datas2);
					out.putInt(result.length);
					out.put(result);//内容
				}
				out.putPrefixedString(msg.getMsgColor(),JoyBuffer.STRING_TYPE_SHORT); //字体颜色
			} catch (Exception e) {
				e.printStackTrace();
			}
		}            
	}
	
	//打包组消息
	public void sendRoleGroup(JoyBuffer out,ChatGroup[] groups){
		int size  = groups.length;
		out.putInt(size);  //组的个数
		for (int i = 0 ; i < groups.length ; i++){
			ChatGroup chatGroup = groups[i];
			if (chatGroup == null) {
				continue;
			}
			out.putLong(chatGroup.getId());	//组Id
			out.putPrefixedString(chatGroup.getName(),JoyBuffer.STRING_TYPE_SHORT);//组名称
			out.putLong(chatGroup.getCreatorUid());	//创始人UID
			List<ChatRole> roles = chatGroup.getRoles();
			int roleSize = roles.size();
			out.putInt(roleSize);  //组成员的个数
			for (int j = 0 ; j < roles.size() ; j++){
				ChatRole chatRole = roles.get(j);
				out.putLong(chatRole.getUid());	//用户Id
				out.putLong(chatRole.getUnionId()); //用户联盟Id
				if(unionManager.search(chatRole.getUnionId()) == null){ //用户联盟简称
					out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);
				}else {
					out.putPrefixedString(unionManager.search(chatRole.getUnionId()).getShortName(),JoyBuffer.STRING_TYPE_SHORT);//联盟简称
				}
				chatRole.getVipInfo().serialize(out);	//用户VIP信息
				out.putPrefixedString(chatRole.getName(),JoyBuffer.STRING_TYPE_SHORT);//用户的名称
				out.put(chatRole.getIcon().getIconId());	//iconId
				out.put(chatRole.getIcon().getIconType()); //iconType
				if(chatRole.getIcon().getIconType() == 0){//iconName
					out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);
				}else if(chatRole.getIcon().getIconType() == 1){
					out.putPrefixedString(chatRole.getIcon().getIconName(),JoyBuffer.STRING_TYPE_SHORT);
				}
			}
			out.putLong(chatGroup.getCreatDate()); //创建时间
		}
	}
}

