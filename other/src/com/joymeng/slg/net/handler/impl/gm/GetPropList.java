package com.joymeng.slg.net.handler.impl.gm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class GetPropList extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());
			int serverId = in.getInt();
			params.put(serverId);
			
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt()); // 从哪个服务器来的
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			int serverId = params.get(2);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setEid(serverId);
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000087;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			List<Item> itemList = dataManager.serachList(Item.class);	
			List<Equip> eqList = dataManager.serachList(Equip.class);
			List<Army> armyList = dataManager.serachList(Army.class);
			Map<String, Object> sortMap = new LinkedHashMap<String, Object>();
			sortMap.put("Item", "物品");
			sortMap.put("Equip", "装备");
			sortMap.put("Resources", "资源");
			sortMap.put("Armys", "部队");
			Map<String, Object> listMap = new LinkedHashMap<String, Object>();
			Map<String, Object> itemMap = new HashMap<String, Object>();
			Map<String, Object> equipMap = new HashMap<String, Object>();
			Map<String, Object> resMap = new LinkedHashMap<String, Object>();
			Map<String, Object> armyMap = new HashMap<String, Object>();
			for (int i = 0 ; i < itemList.size() ; i++){
				Item it = itemList.get(i);
				itemMap.put(it.getId(), it.getBeizhuname());
			}
			for (int j = 0; j < eqList.size(); j++) {
				Equip eq = eqList.get(j);
				equipMap.put(eq.getId(), eq.getBeizhuname());
			}
			for (int k = 0; k < armyList.size(); k++) {
				Army army = armyList.get(k);
				armyMap.put(army.getId(), army.getBattleName());
			}
			resMap.put("goldcoin", "金币");
			resMap.put("food", "食品");
			resMap.put("metal", "金属");
			resMap.put("oil", "石油");
			resMap.put("alloy", "合金");
			resMap.put("gem", "金筹码");
			resMap.put("copper", "银筹码");
			resMap.put("krypton", "氪晶");
			resMap.put("silver", "银币");
			resMap.put("persContr", "联盟贡献度");
			resMap.put("allianceContr", "联盟积分");
			resMap.put("userexp", "经验值");
			
			listMap.put("Item", itemMap);
			listMap.put("Equip", equipMap);
			listMap.put("Resources", resMap);
			listMap.put("Armys", armyMap);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sort", sortMap);
			map.put("list", listMap);
			Map<String, Object> bMap = new HashMap<String, Object>();
			bMap.put("status", 1);
			bMap.put("msg", "success");
			bMap.put("data", map);
			resp.getParams().put(JsonUtil.ObjectToJsonString(bMap));
			return resp;
		
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
}
