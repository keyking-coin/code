package com.joymeng.slg.net.handler.impl.gm;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.TransmissionResp;

public class ModifyEquipOp  extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String equipId =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String operation =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(equipId);
			params.put(operation);
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
			String operation =params.get(5);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000006D;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);

			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
		
            
			switch (operation) {
			case "delete":
		         role.getBagAgent().removeEquip(equipId);
				break;
			case "strengthen":  //强化和重铸暂时未做
				break;
			case "recast":	
                break;
			case "wear":	
			   role.getBagAgent().equipWield(role, Integer.valueOf(equipId));
				break;
				
			case "unload":	
				 role.getBagAgent().equipUnwield(role, Integer.valueOf(equipId));
				break;
			default:
				break;
			}
			
			if(role.isOnline()){
				RespModuleSet rms = new RespModuleSet();
				role.getBagAgent().sendBagToClient(rms);
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
