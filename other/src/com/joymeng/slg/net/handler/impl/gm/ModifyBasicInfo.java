package com.joymeng.slg.net.handler.impl.gm;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.EventName;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class ModifyBasicInfo extends ServiceHandler {
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String project =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String parameter = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(project);
			params.put(parameter);
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
			String project =params.get(4);
			String parameter=params.get(5);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000005E;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);

			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			if (role == null) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				cMap.put("status", 0);
				cMap.put("msg", "用户不存在");
				String backMsg = JsonUtil.ObjectToJsonString(cMap);
				resp.getParams().put(backMsg);
				return resp;
			}
            boolean send =true;
			switch (project) {
			case "name":
				if (!nameManager.isNameLegal(parameter)) {
					Map<String, Object> cMap = new HashMap<String, Object>();
					cMap.put("status", 0);
					cMap.put("msg", "用户名非法");
					String backMsg = JsonUtil.ObjectToJsonString(cMap);
					resp.getParams().put(backMsg);
					return resp;
				}
				if (nameManager.check(parameter) == 0 || role.getName().equals(parameter)) {
					Map<String, Object> cMap = new HashMap<String, Object>();
					cMap.put("status", 0);
					cMap.put("msg", "已被其他玩家使用");
					String backMsg = JsonUtil.ObjectToJsonString(cMap);
					resp.getParams().put(backMsg);
					return resp;
				}
				role.changeRoleName(parameter);
				break;
			case "money":
				role.addRoleMoney(Integer.valueOf(parameter));
				LogManager.goldOutputLog(role, Integer.valueOf(parameter), EventName.ModifyBasicInfo.getName());
				break;
			case "level":
				role.setLevel(Byte.valueOf(parameter));
				role.setExp(0);
				break;
			case "exp":
				role.addExp(Integer.valueOf(parameter));
				break;
			case"vipLevel":
				role.getVipInfo().setVipLevel(Byte.valueOf(parameter));
				role.getVipInfo().setVipExp(0);
				send =false;
				break;
			case"vipExp":
				role.getVipInfo().addExp(role,Integer.valueOf(parameter));
				send =false;
				break;
			case"stamina":  
				role.getRoleStamina().updateCurStamina(Short.valueOf(parameter));
				break;
			case"position":
				int position = MapUtil.getIntPosition(parameter);
				MapCity city = mapWorld.searchMapCity(uid,0);
				if(!city.moveAtOnece(position)){
					Map<String, Object> cMap = new HashMap<String, Object>();
					cMap.put("status", 0);
					cMap.put("msg", "该坐标已被占用");
					String backMsg = JsonUtil.ObjectToJsonString(cMap);
					resp.getParams().put(backMsg);
					return resp;
				}
				break;
			case"score":
				UnionBody unionBody = unionManager.search(role.getUnionId());
				UnionMember member = unionBody.searchMember(role.getId());
				member.setScore(member.getScore()+Integer.valueOf(parameter));
				RespModuleSet rms = member.sendToClient(ClientModule.DATA_TRANS_TYPE_UPDATE);
				MessageSendUtil.sendModule(rms,role.getUserInfo());
				break;
			case"destroyTroop":
				role.getRoleStatisticInfo().setKillSoldsNum(
						role.getRoleStatisticInfo().getKillSoldsNum()
								+ Integer.valueOf(parameter));
				
				break;
			case"troopLose":
				role.getRoleStatisticInfo().setDeadSoldNum(
						role.getRoleStatisticInfo().getDeadSoldNum()
								+ Integer.valueOf(parameter));
				break;
			
			case"attackWin":
				role.getRoleStatisticInfo().setAttackWinTimes(
						role.getRoleStatisticInfo().getAttackWinTimes()
								+ Integer.valueOf(parameter));
				break;
			case"attackFail":
				role.getRoleStatisticInfo().setAttackFailTimes(
						role.getRoleStatisticInfo().getAttackFailTimes()
								+ Integer.valueOf(parameter));	
				break;
			case"defenceWin":
				role.getRoleStatisticInfo().setDefenceWinTimes(
						role.getRoleStatisticInfo().getDefenceWinTimes()
								+ Integer.valueOf(parameter));
				break;
			case"defenceFail":
				role.getRoleStatisticInfo().setDefenceFailTimes(
						role.getRoleStatisticInfo().getDefenceFailTimes()
								+ Integer.valueOf(parameter));
				break;	
			default:
				break;
			}
			if (role.isOnline()){
				if(send){
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}else{
					RespModuleSet rms = new RespModuleSet();
					role.getVipInfo().sendVipToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
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
