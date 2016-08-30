package com.joymeng.slg.net.handler.impl.gm;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class AssociatedAccount extends ServiceHandler{
	
	static int serverid = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
	
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String openId = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			int mark = in.getInt();
			params.put(uid);
			params.put(serverId);
			params.put(openId);
			params.put(mark);

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
			String openId = params.get(4);
			int mark = params.get(5);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000009A;
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
			Map<String,Object> map =new HashMap<String,Object>();
			map.put("serverid", serverid);
			map.put("uid", role.getId());
			switch (mark) {
			case 1:  //能够取到用户是否关联的信息
				//也就是：serverid	uid  name	binding（用户是否绑定标识） openid(微信号) 
				map.put("name", role.getName());
				String openid = role.getOpenId();
				if (StringUtils.isNull(openid)) {
					map.put("binding", 0);
				} else {
					map.put("binding", 1);
				}
				map.put("openid", openid);
				break;
			case 2:  //用户点击绑定，会在游戏内收到邮件，发送绑定的验证码，这时需要回馈给我验证码信息
				String random = JsonUtil.createRandom(true, 4);
				chatMgr.creatSystemEmail(random,uid);
				map.put("Random", random);
				break;
			case 3:  //服务器需要把用户的绑定标识改为已绑定。
				role.setOpenId(openId);
                map.put("msg", "ok");
				break;
			default:
				break;
			}
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
