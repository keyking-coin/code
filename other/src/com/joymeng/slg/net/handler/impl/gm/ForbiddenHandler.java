package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.forbidden.Forbidden;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class ForbiddenHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());
			int serverId = in.getInt();
			long playerUid = in.getLong();
			byte tp= in.get();
			String startTime = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String endTime = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(serverId);
			params.put(playerUid);
			params.put(tp);
			params.put(startTime);
			params.put(endTime);
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
			int serverId = params.get(2);
			long playerUid =params.get(3);
			byte tp = params.get(4);
			String startTime = params.get(5);
			String endTime = params.get(6);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x000000A2;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);	
			Role role = world.getRole(playerUid);
			if (role == null) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				cMap.put("status", 0);
				cMap.put("msg", "用户不存在");
				String backMsg = JsonUtil.ObjectToJsonString(cMap);
				resp.getParams().put(backMsg);
				return resp;
			}
			if (tp == (byte) 2 || tp == (byte) 4) { // 封停登录游戏
				if (tp == (byte) 4) {
					List<String> list = forbidden.getUuidList();
					if (list == null) {
						list = new ArrayList<String>();
					}
					list.add(role.getUuid());
				}
				if (TimeUtils.nowLong() >= TimeUtils.getTimes(startTime)) {
					world.kick(playerUid);
				}
			}
			Map<Long,Map<Byte, Forbidden>> fbn = forbidden.getForbidden();
			Map<Byte, Forbidden> map = fbn.get(playerUid);
			if (map == null) {
				map = new HashMap<Byte, Forbidden>();
			}
			Forbidden fb = map.get(tp);
			fb = new Forbidden(playerUid, tp, startTime, endTime, role.getUuid());
			map.put(tp, fb);
			fbn.put(playerUid, map);
			Map<String,Object> newMap = new HashMap<String,Object>();
			newMap.put("status", 1);
			newMap.put("msg", "success");
			String msg = JsonUtil.ObjectToJsonString(newMap);
			resp.getParams().put(msg);
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
