package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.OperationButton;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.technology.Technology;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class QueryOneTech extends ServiceHandler {
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String techId = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(techId);
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt());// 从哪个服务器返回的数据
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			long uid = params.get(2);
			int serverId = params.get(3);
			String techId = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000069;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);

			List<Object> list = new ArrayList<Object>();
			Map<String, Technology> technology = role.getCity(0).getTechAgent()
					.getTechMap();
			Technology tech = role.getCity(0).getTechAgent().getTech(techId);
			if (technology.containsKey(techId)) {
				OperationButton ob2 = new OperationButton();
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("title", "state");
				map2.put("value", "已研究");
				ob2.setButton("一键加满");
				ob2.setUrl("logic=HttpModifyTechToMax&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerTechId="
						+ techId + "");
				List<Object> button2 = new ArrayList<Object>();
				button2.add(ob2);
				map2.put("operationButton", button2);
				list.add(map2);

				OperationButton ob3 = new OperationButton();
				Map<String, Object> map3 = new HashMap<String, Object>();
				map3.put("title", "level");
				map3.put("value", tech.getLevel());
				ob3.setButton("修改为");
				ob3.setChangeOrmodify("修改为");
				ob3.setInput("");
				ob3.setUrl("logic=HttpModifyTechLevel&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerTechId="
						+ techId + "");
				List<Object> button3 = new ArrayList<Object>();
				button3.add(ob3);
				map3.put("operationButton", button3);
				list.add(map3);

			} else {
				Tech data = dataManager.serach(Tech.class, techId); // 玩家特定科技
				OperationButton ob2 = new OperationButton();
				Map<String, Object> map2 = new HashMap<String, Object>();
				List<String> precedingTechList = data.getPrecedingTechList();
				if (role.getCity(0).getTechAgent()
						.JudgTechCondition(precedingTechList)) {
					map2.put("title", "state");
					map2.put("value", "已解锁");
					ob2.setButton("一键加满");
					ob2.setUrl("logic=HttpModifyTechToMax&playerUid=" + uid
							+ "&playerServerId=" + serverId + "&playerTechId="
							+ techId + "");
					List<Object> button2 = new ArrayList<Object>();
					button2.add(ob2);
					map2.put("operationButton", button2);
					list.add(map2);
				}

				OperationButton ob3 = new OperationButton();
				Map<String, Object> map3 = new HashMap<String, Object>();
				map3.put("title", "level");
				map3.put("value", 0);
				ob3.setButton("修改为");
				ob3.setChangeOrmodify("修改为");
				ob3.setInput("");
				ob3.setUrl("logic=HttpModifyTechLevel&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerTechId="
						+ techId + "");
				List<Object> button3 = new ArrayList<Object>();
				button3.add(ob3);
				map3.put("operationButton", button3);
				list.add(map3);

			}

			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			bMap.put("PlayerTech", list);
			map.put("status", 1);
			map.put("msg", "success");
			map.put("data", bMap);
			String bMsg = JsonUtil.ObjectToJsonString(map);
			resp.getParams().put(bMsg);
			return resp;
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(), sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(), next);
			}
			return null;
		}
	}
}
