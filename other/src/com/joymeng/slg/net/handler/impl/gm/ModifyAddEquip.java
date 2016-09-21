package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.TransmissionResp;

public class ModifyAddEquip extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String equipId =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String number =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(equipId);
			params.put(number);
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
			String equipId =params.get(4);
			String number =params.get(5);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000006C;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);

			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
		 
		    //equipId 装备 number 数量
			List<ItemCell> aList = new ArrayList<>();
			aList = role.getBagAgent().addEquip(equipId, Integer.valueOf(number));
			Equip  equip = dataManager.serach(Equip.class, equipId);
			LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "后台添加");
			if(role.isOnline()){
				RespModuleSet rms = new RespModuleSet();
				role.getBagAgent().sendItemsToClient(rms, aList);
				MessageSendUtil.sendModule(rms,role.getUserInfo());
			}
			
			Map<String,Object> map =new HashMap<String,Object>();
			map.put("status", 1);
			map.put("msg", "success");
			map.put("data", "操作成功");
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
