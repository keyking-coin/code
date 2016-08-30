package com.joymeng.slg.net.handler.impl.gm;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.imp.RoleStaticData;
import com.joymeng.slg.domain.object.role.imp.RoleStatisticInfo;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class QueryPlayerInfo extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			params.put(uid);
			params.put(serverId);
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
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000057;
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

			Map<String, Object> amap = new LinkedHashMap<String, Object>();
			amap.put("UID", role.getId());
			amap.put("name", role.getName());
			amap.put("kingdom", "中国");
			amap.put("level", role.getLevel());
			UnionBody unionBody = unionManager.search(role.getUnionId());
			if (unionBody == null) {
				amap.put("union", "没有联盟");
				amap.put("military", "没有军衔");
			} else {
				UnionMember member = unionBody.searchMember(role.getId());
				amap.put("union", unionBody.getName());
				amap.put("military", member.getAllianceKey());
			}
			
			float honor = (float)role.getHonorAgent().getMedalCount(false)/(float)role.getHonorAgent().getMedalCount(true);
			NumberFormat nt = NumberFormat.getPercentInstance();
			nt.setMinimumFractionDigits(2);
			int medal = role.getHonorAgent().getMedalCount(false);
			amap.put("achCompletion", nt.format(honor));
			amap.put("medalCollection", medal);  
			RoleStatisticInfo sInfo = role.getRoleStatisticInfo();
			amap.put("killNum", sInfo.getKillSoldsNum());
			amap.put("roleFight", sInfo.getRoleFight()); 
			
			Map<String, Object> cMap = new HashMap<String, Object>();
			List<RoleStaticData> infoList = RoleStaticData.getDetailList(role,0);
			for (int i = 0 ; i < infoList.size() ; i++){
				RoleStaticData roleStat = infoList.get(i);
				cMap.put(roleStat.getHfss().getStatisticName(),roleStat.getNum());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			List<Object> list =new ArrayList<Object>();
			List<Object> list1 =new ArrayList<Object>();
			Map<String, Object> one = new HashMap<String, Object>();
			Map<String, Object> two = new HashMap<String, Object>();
			list.add(amap);
			list1.add(cMap);
			one.put("1", list);
			two.put("2", list1);
			bMap.put("PlayerBasicInf", one);
			bMap.put("PlayerOtherInf",two);
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
