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
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class QueryOneEquip extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String equipId = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(equipId);
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
			String equipId = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000067;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			
			// ?logic=HttpModifyEquipOp&playerUid=1711539&playerServerId=12293&playerEquipId=10&playerOperation=delete
			List<Object> list =new ArrayList<Object>();
			EquipItem equip =role.getBagAgent().getEquipById(Long.valueOf(equipId));
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("title", "removeEquip");
			OperationButton ob1 =new OperationButton();
			ob1.setButton("删除装备");
			ob1.setUrl("logic=HttpModifyEquipOp&playerUid=" + uid
					+ "&playerServerId=" + serverId + "&playerEquipId="
					+ equipId + "&playerOperation=delete");
			List<Object> button1 =new ArrayList<Object>();
			button1.add(ob1);
			map1.put("operationButton", button1);
			list.add(map1);
		
	/*		Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("title", "strengthenEquip");
			OperationButton ob2 =new OperationButton();
			ob2.setButton("强化装备");
			ob2.setUrl("logic=HttpModifyEquipOp&playerUid=" + uid
					+ "&playerServerId=" + serverId + "&playerEquipId="
					+ equipId + "&playerOperation=strengthen");
			List<Object> button2 =new ArrayList<Object>();
			button2.add(ob2);
			map2.put("operationButton", button2);
			list.add(map2);
			
			
			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("title", "recastEquip");
			OperationButton ob3 =new OperationButton();
			ob3.setButton("重铸装备");
			ob3.setUrl("logic=HttpModifyEquipOp&playerUid=" + uid
					+ "&playerServerId=" + serverId + "&playerEquipId="
					+ equipId + "&playerOperation=recast");
			List<Object> button3 =new ArrayList<Object>();
			button3.add(ob3);
			map3.put("operationButton", button3);
			list.add(map3);*/
			
			if(equip.getEquipState()==0){
				
				Map<String, Object> map4 = new HashMap<String, Object>();
				map4.put("title", "putOnEquip");
				OperationButton ob4 =new OperationButton();
				ob4.setButton("穿上装备");
				ob4.setUrl("logic=HttpModifyEquipOp&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerEquipId="
						+ equipId + "&playerOperation=wear");
				List<Object> button4 =new ArrayList<Object>();
				button4.add(ob4);
				map4.put("operationButton", button4);
				list.add(map4);
				
			}
			
			if (equip.getEquipState() == 1) {

				Map<String, Object> map5 = new HashMap<String, Object>();
				map5.put("title", "takeOffEquip");
				OperationButton ob5 = new OperationButton();
				ob5.setButton("卸下装备");
				ob5.setUrl("logic=HttpModifyEquipOp&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerEquipId="
						+ equipId + "&playerOperation=unload");
				List<Object> button5 = new ArrayList<Object>();
				button5.add(ob5);
				map5.put("operationButton", button5);
				list.add(map5);

			}
			
			
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			bMap.put("PlayerEquip", list);
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
