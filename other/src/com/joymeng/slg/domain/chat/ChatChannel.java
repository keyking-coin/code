package com.joymeng.slg.domain.chat;

/**
 * 聊天类接口
 * @author zhouyongjun
 *
 */
public interface ChatChannel {
/**
 * 玩家发送聊天Text文本信息
 * @param player
 * @param msg
 * @param objs
 */
public void sendChatTextMsg(ChatMsg msg,Object... objs);

/**
 * 玩家发送玩家语音聊天
 * @param msg
 * @param objs
 */
public void sendChatVoiceMsg(ChatMsg msg,Object... objs);

/**
 * 发送系统类滚屏信息
 * @param player
 * @param msg
 * @param objs
 */
public void sendSysRollScreenMsg(String msg,Object... objs);

/**
 * 发送系统文字信息
 * @param msg
 * @param objs
 */
public void sendSysChatTextMsg(String msg,Object... objs);
/**
 * 频道类型
 * @return
 */
public ChannelType getChannelType();

}
