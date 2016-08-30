package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleStamina;
import com.joymeng.slg.domain.object.role.VipInfo;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;

public class QueryBykeyWord extends ServiceHandler {
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String parameter = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(parameter);
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt()); // 从哪个服务器来的
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			int serverId = params.get(3);
			String parameter = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setEid(serverId);
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000058;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			
			Map<String,Object> map = JsonUtil.JsonToObject(parameter, Map.class);
			StringBuffer sqlbuff = new StringBuffer(256);
			String role = DaoData.TABLE_RED_ALERT_ROLE;
			String city = DaoData.TABLE_RED_ALERT_CITY;
			sqlbuff.append("select role.joy_id from "+role+" inner join "+city+" on role.joy_id=city.uid  where 1=1 ");
			if (map.get("playerName") != null) {
				sqlbuff.append(" and " + role + ".name "+ map.get("playerName"));
			}
			if (map.get("playerUid") != null) {
				sqlbuff.append(" and " + role + ".joy_id "+ map.get("playerUid"));
			}
			if (map.get("playerBaseLevel") != null) {
				sqlbuff.append(" and " + city + ".centerLevel "+ map.get("playerBaseLevel"));
			}
			if (map.get("playerLevel") != null) {
				sqlbuff.append(" and " + role + ".level "+ map.get("playerLevel"));
			}
			if (map.get("playerMoney") != null) {
				sqlbuff.append(" and " + role + ".money "+ map.get("playerMoney"));
			}
			if (map.get("playerExp") != null) {
				sqlbuff.append(" and " + role + ".exp " + map.get("playerExp"));
			}
			if (map.get("playerChannel") != null) {
				sqlbuff.append(" and " + role + ".channelId " + map.get("playerChannel"));
			}

			List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasBySql(sqlbuff.toString());
			List<Long> result = new ArrayList<Long>();  //符合上述条件的
			for (int i = 0; i < datas.size(); i++) {
				Map<String, Object> data = datas.get(i);
				result.add((Long) data.get("joy_id"));
			}
			List<Long> remove = new ArrayList<Long>(); //需要删除的玩家uid
			for (int i = 0; i < remove.size(); i++) {
				Long joy_id = result.get(i);
				Role rl = world.getRole(joy_id);
				if (map.get("playerVipLevel") != null) {
					String vipLevel = (String) map.get("playerVipLevel");
					VipInfo vipInfo = rl.getVipInfo();
					byte vipLe = vipInfo.getVipLevel();
					investigate(vipLevel, vipLe, remove, joy_id);
				}

				if (map.get("playerStamina") != null) {
					String stam = (String) map.get("playerStamina");
					RoleStamina stamina = rl.getRoleStamina();
					short mina=stamina.getCurStamina();
					investigate(stam, mina, remove, joy_id);
				}
				
				int position = rl.getCity(0).getPosition();
				String point = MapUtil.getStrPosition(position);
				String[] xy = point.split(",");
				int x =Integer.valueOf(xy[0]);
				int y =Integer.valueOf(xy[1]);
		
				if(map.get("playerPosX")!=null){
					String posX = (String) map.get("playerPosX");
					investigate(posX, x, remove, joy_id);
				}
				
				if(map.get("playerPosY")!=null){
					String posY = (String) map.get("playerPosY");
					investigate(posY, y, remove, joy_id);
				}
				
				Map<ResourceTypeConst, Long> resources = rl.getCity(0).getResources();
				long food = resources.get(ResourceTypeConst.RESOURCE_TYPE_FOOD);
				long metal = resources.get(ResourceTypeConst.RESOURCE_TYPE_METAL);
				long oil = resources.get(ResourceTypeConst.RESOURCE_TYPE_OIL);
				long alloy = resources.get(ResourceTypeConst.RESOURCE_TYPE_ALLOY);
				
				if (map.get("playerFood") != null) {
					String plFood = (String) map.get("playerFood");
					investigate(plFood, food, remove, joy_id);
				}

				if (map.get("playerMetal") != null) {
					String plMetal = (String) map.get("playerMetal");
					investigate(plMetal, metal, remove, joy_id);
				}

				if (map.get("playerOil") != null) {
					String plOil = (String) map.get("playerOil");
					investigate(plOil, oil, remove, joy_id);
				}

				if (map.get("playerAlloy") != null) {
					String plAlloy = (String) map.get("playerAlloy");
					investigate(plAlloy, alloy, remove, joy_id);
				}
				
				if (map.get("playerUnion") != null) {
					String union = (String) map.get("playerUnion");
					String[] un = union.split("|");
					UnionBody unionBody = unionManager.search(joy_id);
					if (unionBody != null) {
						if (un[0].equals("is")) {
							if (!unionBody.getName().equals(un[1])) {
								remove.add(joy_id);
							}
						} else {
							if (!unionBody.getName().contains(un[1])) {
								remove.add(joy_id);
							}
						}
					} else {
						remove.add(joy_id);
					}
				}
				
				if (map.get("playerIsOnline") != null) {
					String isOnline = (String) map.get("playerIsOnline");
					String[] onLine = isOnline.split("|");
					if (onLine[1].equals("YES")) {
						if (!rl.isOnline()) {
							remove.add(joy_id);
						}
					} else {
						if (rl.isOnline()) {
							remove.add(joy_id);
						}
					}
				}	
				
			}		
			result.removeAll(remove);   //最终查询结果 List<Long> result 
			
			if(result==null||result.size()==0){
				resp.getParams().put("null");
				return resp;
			}else{
				List<Object> backList = new ArrayList<Object>();
				for (int i = 0; i < result.size(); i++) {
					long uid = result.get(i);
					Role rl = world.getRole(uid);
					Map<String,Object> strMap = new LinkedHashMap<String,Object>();
					strMap.put("UID", rl.getId());
					strMap.put("serverId", serverId);
					strMap.put("userName", rl.getName());
					UnionBody unionBody = unionManager.search(uid);
					if(unionBody!=null){
						strMap.put("union", unionBody.getName());
					}else{
						strMap.put("union", "没有联盟");
					}
					strMap.put("level", rl.getLevel());
					strMap.put("baseLevel", rl.getCity(0).getCityCenterLevel());
					int fight = rl.getRoleStatisticInfo().getRoleFight();
					strMap.put("fight", fight);
					if(rl.isOnline()){
						strMap.put("isOnline", "在线");
					}else{
						strMap.put("isOnline", "离线");
					}
					backList.add(strMap);
				}
				String bMsg = JsonUtil.ObjectToJsonString(backList);
				resp.getParams().put(bMsg);
				return resp;
			}
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(),sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(),next);
			}
			return null;
		}
	}
	
	
	public static void investigate(String pl,long count, List<Long> remove, Long joy_id) {
		String[] str = pl.split("|");
		long number = Long.valueOf(str[1]);
		switch (str[0]) {
		case "<":
			if (count >= number) {
				remove.add(joy_id);
			}
			break;
		case "<=":
			if (count > number) {
				remove.add(joy_id);
			}
			break;
		case "=":
			if (count > number || count < number) {
				remove.add(joy_id);
			}
			break;
		case ">=":
			if (count < number) {
				remove.add(joy_id);
			}
			break;
		case ">":
			if (count <= number) {
				remove.add(joy_id);
			}
			break;
		}
	}
	
}
