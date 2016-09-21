package com.joymeng.http.handler.impl.info.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class HttpQueryBykey extends AbstractHandler {
	@Override
	public String logic(HttpRequestMessage request) {
		String server = request.getParameter("playerServerId"); // 多个服务器
		String[] sserverId = server.split(",");	
		String nameType = request.getParameter("nameType");
		String playerName = request.getParameter("playerName");
		String uidType = request.getParameter("uidType");
		String playerUid = request.getParameter("playerUid");
		String baseLevelType = request.getParameter("baseLevelType");
		String playerBaseLevel = request.getParameter("playerBaseLevel");
		String levelType = request.getParameter("levelType");
		String playerLevel = request.getParameter("playerLevel");
		String moneyType = request.getParameter("moneyType");
		String playerMoney = request.getParameter("playerMoney");
		String expType = request.getParameter("expType");
		String playerExp = request.getParameter("playerExp");
		String channelType = request.getParameter("channelType");
		String playerChannel = request.getParameter("playerChannel");
		String vipLevelType = request.getParameter("vipLevelType");
		String playerVipLevel = request.getParameter("playerVipLevel");
		String staminaType = request.getParameter("staminaType");
		String playerStamina = request.getParameter("playerStamina");
		String posXType = request.getParameter("posXType");
		String playerPosX = request.getParameter("playerPosX");
		String posYType = request.getParameter("posYType");
		String playerPosY = request.getParameter("playerPosY");
		String foodType = request.getParameter("foodType");
		String playerFood = request.getParameter("playerFood");
		String metalType = request.getParameter("metalType");
		String playerMetal = request.getParameter("playerMetal");
		String oilType = request.getParameter("oilType");
		String playerOil = request.getParameter("playerOil");
		String alloyType = request.getParameter("alloyType");
		String playerAlloy = request.getParameter("playerAlloy");
		String unionType = request.getParameter("unionType");
		String playerUnion = request.getParameter("playerUnion");
		String onlineType = request.getParameter("onlineType");
		
		Map<String,String> parameter = new HashMap<String,String>();
		if(!StringUtils.isNull(playerName)){
			if(nameType.equals("is")){
				parameter.put("playerName", "=\""+playerName+"\"");
			}else{
				parameter.put("playerName", "like \"%"+playerName+"%\"");
			}
		}		
		if (!StringUtils.isNull(playerUid)) {
			judge(parameter, uidType, "playerUid",playerUid);
		}
		if (!StringUtils.isNull(playerBaseLevel)) {
			judge(parameter, baseLevelType, "playerBaseLevel",playerBaseLevel);
		}
		if (!StringUtils.isNull(playerLevel)) {
			judge(parameter, levelType, "playerLevel",playerLevel);
		}
		if (!StringUtils.isNull(playerMoney)) {
			judge(parameter, moneyType, "playerMoney",playerMoney);
		}
		if (!StringUtils.isNull(playerExp)) {
			judge(parameter, expType, "playerExp",playerExp);
		}
		if (!StringUtils.isNull(playerChannel)) {
			judge(parameter, channelType, "playerChannel",playerChannel);
		}
		
		if(!StringUtils.isNull(playerVipLevel)){
			judge2(parameter, vipLevelType, "playerVipLevel",playerVipLevel);
		}
		if(!StringUtils.isNull(playerStamina)){
			judge2(parameter, staminaType, "playerVipLevel",playerStamina);
		}
		if(!StringUtils.isNull(playerPosX)){
			judge2(parameter, posXType, "playerPosX",playerPosX);
		}
		if(!StringUtils.isNull(playerPosY)){
			judge2(parameter, posYType, "playerPosY",playerPosY);
		}
		if(!StringUtils.isNull(playerFood)){
			judge2(parameter, foodType, "playerFood",playerFood);
		}
		if(!StringUtils.isNull(playerMetal)){
			judge2(parameter, metalType, "playerMetal",playerMetal);
		}
		if(!StringUtils.isNull(playerOil)){
			judge2(parameter, oilType, "playerOil",playerOil);
		}
		if(!StringUtils.isNull(playerAlloy)){
			judge2(parameter, alloyType, "playerAlloy",playerAlloy);
		}
		
		if (!StringUtils.isNull(playerUnion)) {
			if (unionType.equals("is")) {
				parameter.put("playerUnion", "is|" + playerUnion);
			} else {
				parameter.put("playerUnion", "like|" + playerUnion);
			}
		}
		if(!StringUtils.isNull(onlineType)){
			if (onlineType.equals("yes")) {
				parameter.put("playerIsOnline", "YES");
			} else if(onlineType.equals("no")){
				parameter.put("playerIsOnline", "NO");
			}
		}
		String playerParameter=JsonUtil.ObjectToJsonString(parameter);
		long uid = GameConfig.SYSTEM_TRANFOEM_ID; 
		int protocolId = 0x00000058; // 多服务器玩家信息
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		if (!objs.containsKey(uid)){
			objs.put(uid,new HashMap<String,Object>());
		}
		for (int i = 0 ; i < sserverId.length ; i++){
			String servId = sserverId[i];
			final int serverId = Integer.parseInt(servId) + GameConfig.SERVER_LIST_ID;
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(uid);
			targetInfo.setCid(serverId); // 服务器,http传过来的服务器编号，你要访问哪个服务器
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); // 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);// 从哪里来的
			resp.getParams().put(uid);
			resp.getParams().put(serverId);
			resp.getParams().put(playerParameter);
			JoyServiceApp.getInstance().sendMessage(resp);
			insert(uid,serverId);
			handler.addNextDo(uid, new NeedContinueDoSomthing() {
				@Override
				public int getId() {
					return serverId;
				}
				@Override
				public JoyProtocol succeed(UserInfo info,ParametersEntity params) {
					synchronized(this){
						Map<String, Object> map = objs.get(info.getUid());
						map.put("result", "ok");
					    String data = params.get(3);
					    List<Object> list =JsonUtil.JsonToObjectList(data,Object.class);
					    if(list!=null&&list.size()!=0){
					    	List<Object> datas = get(info.getUid(),"datas");
					    	if(datas == null){
					    		datas = new ArrayList<Object>();
					    		map.put("datas",datas);
					    	}
					    	for(int i=0; i<list.size();i++){
					    		datas.add(list.get(i));
					    	}
					    }
					    int comeFrom = params.get(2);
						remove(info.getUid(),comeFrom);
						return null;
					}
				}

				@Override
				public JoyProtocol fail(UserInfo info, ParametersEntity params) {
					synchronized(this){
						Map<String, Object> map = objs.get(info.getUid());
						map.put("result", "通讯失败");
						int comeFrom = params.get(2);
						remove(info.getUid(),comeFrom);
						return null;
					}
				}
			});
		}
		interrupt(uid);
		Map<String, Object> map = objs.get(uid);
		objs.remove(uid);
		if (!map.containsKey("result")) {
			return "请求超时";
		} else {
			if (map.get("result").equals("ok")) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				Map<String, Object> dMap = new HashMap<String, Object>();
				Map<String, Object> one = new HashMap<String, Object>();
				if(map.get("datas")!=null){
					one.put("1", map.get("datas"));
					dMap.put("PlayerOfServers",one);
					cMap.put("status", 1);
					cMap.put("msg", "success");
					cMap.put("data", dMap);
					return JsonUtil.ObjectToJsonString(cMap);
				}else{
					cMap.put("status", 0);
					cMap.put("msg", "未查到符合条件的玩家");
					return JsonUtil.ObjectToJsonString(cMap);
				}
				
			} else { 
				return map.get("result").toString();
			}
		}
	}
	
	public void judge(Map<String, String> map, String type, String parameter, String number) {
		int num = Integer.valueOf(number);
		switch (type) {
		case "lt":
			map.put(parameter, "<" + num);
			break;
		case "elt":
			map.put(parameter, "<=" + num);
			break;
		case "eq":
			map.put(parameter, "=" + num);
			break;
		case "egt":
			map.put(parameter, ">=" + num);
			break;
		case "gt":
			map.put(parameter, ">" + num);
			break;
		default:
			break;
		}
	}

	public void judge2(Map<String, String> map, String type, String parameter, String number) {
		int num = Integer.valueOf(number);
		switch (type) {
		case "lt":
			map.put(parameter, "<|" + num);
			break;
		case "elt":
			map.put(parameter, "<=|" + num);
			break;
		case "eq":
			map.put(parameter, "=|" + num);
			break;
		case "egt":
			map.put(parameter, ">=|" + num);
			break;
		case "gt":
			map.put(parameter, ">|" + num);
			break;
		default:
			break;
		}
	}
}
