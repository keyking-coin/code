package com.joymeng.http.handler.impl.gm;

import java.util.List;

import com.joymeng.common.util.StringUtils;
import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.ActvtManager;
import com.joymeng.slg.domain.chat.ChannelType;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.ChatRole;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.domain.chat.MsgTitleType;
import com.joymeng.slg.world.GameConfig;

public class HttpSysMgr extends HttpHandler {

	@Override
	public boolean handle(HttpRequestMessage request,
			HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		if (ServiceApp.FREEZE){
			message(response,"服务器已关闭");
			return false;
		}
		String action = request.getParameter("action");
		switch (action){
			case "token":{
				if (request.getParameter("tokenCode").equals("keyking")){
					message(response,"ok");
				}else{
					message(response,"验证码错误");
				}
				break;
			}
			case "close":{
				world.tryToShutDown();
				message(response,"关闭成功");
				break;
			}
			case "loadActivity": {
				ActvtManager.getInstance().hotLoad();
				break;
			}
			case "loadFile":{
				String operate = request.getParameter("loadType");
				if (operate.equals("sys")){
					try {
						GameConfig.load();
						message(response,"load game.properties ok");
					} catch (Exception e) {
						e.printStackTrace();
						message(response,"load game.properties fail");
					}
				}else if (operate.equals("json")){
					dataManager.load(false);
					message(response,"load jsons ok");
				}else if (operate.equals("disallow")){
					nameManager.loadDisallow();
					message(response,"load disallow file ok");
				}else if (operate.equals("activity")) {
					ActvtManager.getInstance().hotLoad();
				}
				break;
			}
			case "sendEmail":{
				String content = request.getParameter("content");
				String sql = "select joy_id from role";
				List<SqlData> datas = dbMgr.getGameDao().getSqlDatas(sql);
				for (int i = 0 ; i < datas.size() ; i++){
					SqlData data = datas.get(i);
					long uid = data.getLong(DaoData.RED_ALERT_ROLE_ID);
					chatMgr.creatSystemEmail(content,uid);
				}
				message(response,"send ok");
				break;
			}
			case "sendNoticeMsg":{
				String content = request.getParameter("content");
				if (StringUtils.isNull(content)) {
					message(response, "notice_Content is null .");
					break;
				}
				content = "0" + content;
				int priorityLevel = Integer.valueOf(request.getParameter("priorityLevel"));
				ChatRole sys = new ChatRole();
				sys.setUid(-1L);
				sys.setName("系统公告");
				ChatMsg msg = new ChatMsg(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM,content,MsgTextColorType.COLOR_BLACK, ChannelType.MAIL_SYSTEM,(byte)3,(byte)0,sys,null);	
				//添加世界公告
				chatMgr.addSystemNotice(msg, priorityLevel);
				//加入世界聊天
				chatMgr.addWorldChat(msg);
				chatMgr.sendWorldChatMsgsUpdate(msg);
				message(response,"send ok");
				break;
			}
		}
		return false;
	}

}
