package com.joymeng.slg.net.handler.impl.gm;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.TransmissionResp;

public class ModifyBuildState extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String buildSlot =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String project =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(buildSlot);
			params.put(project);
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
			String buildSlot =params.get(4);
			String project =params.get(5);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000006B;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);

			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
		    // buildSlot  project
			RoleBuild rb =	role.getCity(0).getBuildBySlot(buildSlot);
			switch (project) {
			case "complete":  //一键完成
				rb.secondKill(role, 1);
				role.addRoleMoney(1);
				break;
			case"remove":
				role.getCity(0).removeBuild(role, rb.getId(), 0);
				break;
			case"repair":  //修复耐久
				BuildComponentDefense component = rb.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
				component.repairDefenseArmys(role, 0, rb.getId(), 0);
				break;
			case"outFire":
				role.getCity(0).outFireState(role);
				break;	
			default:
				break;
			}
			RoleCityAgent agent = role.getCity(0);
			if (role.isOnline()){
				RespModuleSet rms = new RespModuleSet();
				agent.sendToClient(rms, true);
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
