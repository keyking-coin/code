package com.joymeng.slg.net.handler.impl.gm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.BuildOperation;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Basebuildingslot;
import com.joymeng.slg.domain.object.build.data.Building;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class ModifyCreatBuild extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String buildSlot =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String buildId =in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(buildSlot);
			params.put(buildId);
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
			String buildId =params.get(5);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000068;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
		    if(!role.isOnline()){
		    	Basebuildingslot bs = dataManager.serach(Basebuildingslot.class, buildSlot);
				List<String> buildLimitation =bs.getBuildLimitation();
				if(buildLimitation.contains(buildId)){
					int count=role.getCity(0).getBuildCount(buildId);
					int maxCount =role.getCity(0).getBuildMaxCount(buildId);
					if(count<maxCount){
					RoleCityAgent city = role.getCity(0);
					Building building = dataManager.serach(Building.class,buildId);
				    int initializeLevel  = building.getInitializeLevel();//初始化等级
				    Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(buildId,initializeLevel);
					int	costMoney = city.getCostMoney(role, buildLevel.getBuildCostList(), buildLevel.getNeeditem(),0, (byte)1);
	                role.addRoleMoney(costMoney);
					role.getCity(0).createBuild(buildSlot, buildId, role, 2);
					LogManager.buildLog(role, buildSlot, buildId, (byte)1, BuildOperation.creatBuild.getKey());
					}else{
						Map<String,Object> map =new HashMap<String,Object>();
						map.put("status", 0);
						map.put("msg", "建造失败，建筑数量已达到最大值");
						String bMsg = JsonUtil.ObjectToJsonString(map);
						resp.getParams().put(bMsg);
						return resp;
					}
				}else{
					Map<String,Object> map =new HashMap<String,Object>();
					map.put("status", 0);
					map.put("msg", "选定槽不允许建造指定建筑");
					String bMsg = JsonUtil.ObjectToJsonString(map);
					resp.getParams().put(bMsg);
					return resp;
				}
				Map<String,Object> map =new HashMap<String,Object>();
				map.put("status", 1);
				map.put("msg", "success");
				map.put("data", "操作成功");
				String bMsg = JsonUtil.ObjectToJsonString(map);
				resp.getParams().put(bMsg);
				return resp;
		    }else{
		    	Map<String,Object> map =new HashMap<String,Object>();
				map.put("status", 0);
				map.put("msg", "请在玩家离线时进行操作!");
				String bMsg = JsonUtil.ObjectToJsonString(map);
				resp.getParams().put(bMsg);
				return resp;
		    }
			
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
