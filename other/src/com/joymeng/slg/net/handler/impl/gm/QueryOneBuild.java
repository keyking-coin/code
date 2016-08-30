package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.OperationButton;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.data.Building;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class QueryOneBuild extends ServiceHandler {
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String slotId = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(slotId);
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
			String slotId = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000064;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);

	  
			// 玩家建筑槽 Id slotId
			RoleBuild build = role.getCity(0).searchBuildBySoltId(slotId);
			List<String> ls = role.getCity(0).getUnusedSlots(); //未被使用的槽
			List<Object> list =new ArrayList<Object>();
	       //url 修改建筑等级 logic=HttpModifyBuildLevel&playerUid=1741123&playerServerId=12293&playerBuildSlot=10&playerChange=2
			if(!ls.contains(slotId)){
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("title", "level");
				map1.put("value", build.getLevel());
				OperationButton ob1 =new OperationButton();
				ob1.setButton("修改级别");
				ob1.setInput("");
				ob1.setUrl("logic=HttpModifyBuildLevel&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerBuildSlot="
						+ slotId + "");
				List<Object> button1 =new ArrayList<Object>();
				button1.add(ob1);
				map1.put("operationButton", button1);
				list.add(map1);

				if(build.getState()==1||build.getState()==2 ||build.getState()==5){
					Map<String, Object> map3 = new HashMap<String, Object>();
					map3.put("title", "complete");
					OperationButton ob3 =new OperationButton();
					ob3.setButton("一键完成");
					ob3.setUrl("logic=HttpModifyBuildState&playerUid=" + uid
							+ "&playerServerId=" + serverId + "&playerBuildSlot="
							+ slotId + "&playerProject=complete");
					List<Object> button3 =new ArrayList<Object>();
					button3.add(ob3);
					map3.put("operationButton", button3);
					list.add(map3);
				}
				if (!build.isOnly() && build.getState() == 0) {
			    	Map<String, Object> map4 = new HashMap<String, Object>();
					map4.put("title", "remove");
					OperationButton ob4 =new OperationButton();
					ob4.setButton("拆除建筑");
					ob4.setUrl("logic=HttpModifyBuildState&playerUid=" + uid
							+ "&playerServerId=" + serverId + "&playerBuildSlot="
							+ slotId + "&playerProject=remove");
					List<Object> button4 =new ArrayList<Object>();
					button4.add(ob4);
					map4.put("operationButton", button4);
					list.add(map4);
			    }
				
				BuildComponentDefense defenseComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
				if(defenseComponent!=null){
					Map<String, Object> map5 = new HashMap<String, Object>();  //城墙 和防御建筑
					map5.put("title", "recovery");
					OperationButton ob5 =new OperationButton();
					ob5.setButton("恢复耐久");
					ob5.setUrl("logic=HttpModifyBuildState&playerUid=" + uid
							+ "&playerServerId=" + serverId + "&playerBuildSlot="
							+ slotId + "&playerProject=repair");
					List<Object> button5 =new ArrayList<Object>();
					button5.add(ob5);
					map5.put("operationButton", button5);
					list.add(map5);
				}
				BuildComponentWall wallComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
				MapCity mapCity = Instances.mapWorld.searchMapCity(uid,0);
				if (wallComponent != null && mapCity.getCityState().isFire()) {
					Map<String, Object> map6 = new HashMap<String, Object>();
					map6.put("title", "outfire");
					OperationButton ob6 =new OperationButton();
					ob6.setButton("灭火");
					ob6.setUrl("logic=HttpModifyBuildState&playerUid=" + uid
							+ "&playerServerId=" + serverId + "&playerBuildSlot="
							+ slotId + "&playerProject=outFire");
					List<Object> button6 =new ArrayList<Object>();
					button6.add(ob6);
					map6.put("operationButton", button6);
					list.add(map6);
				}			
			}else{
		
				Map<String, Object> map2 = new HashMap<String, Object>();
				List<Building> bList=dataManager.serachList(Building.class);
				Map<String,Object> map = new HashMap<String,Object>();
				for(Building b: bList){
					map.put(b.getId(), b.getId());
				}
				map2.put("title", "添加建筑");
				OperationButton ob2 =new OperationButton();
				ob2.setButton("添加建筑");
			    ob2.setSelect(map);
				ob2.setUrl("logic=HttpModifyCtBuild&playerUid=" + uid
						+ "&playerServerId=" + serverId + "&playerBuildSlot="
						+ slotId + ""); // 添加建筑 建筑id需要运维拼
				List<Object> button2 =new ArrayList<Object>();
				button2.add(ob2);
				map2.put("operationButton", button2);
				list.add(map2);
				
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			bMap.put("PlayerGenPart", list);
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
