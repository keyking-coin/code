package com.joymeng.slg.net.handler.impl.gm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class PlayerItemLog extends ServiceHandler{
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
			params.put(in.getInt()); // 从哪个服务器来的
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
			targetInfo.setEid(serverId);
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000085;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			File allFile = new File("./log/ops/item_consume/");
			File[] fileList = allFile.listFiles();
			GameLog.info("该目录下对象个数："+fileList.length);
			List<Object> list = new ArrayList<Object>();
			for(File file : fileList){
				GameLog.info(file.getName());
				BufferedReader reader = null;  
				try {
					InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
					reader = new BufferedReader(isr);
					String tempString = null;
					while ((tempString = reader.readLine()) != null) {
						Map<String, Object> map = new HashMap<String, Object>();
						String[] itemLog = tempString.split("\\|");
						if (tempString.contains(String.valueOf(uid))) {
							map.put("uid", uid);
							map.put("event", itemLog[11]);
							map.put("item", itemLog[13]);
							list.add(map);
						}
					}
					isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	        String bMsg = JsonUtil.ObjectToJsonString(list);
			resp.getParams().put(bMsg);
			return resp;
		
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(),sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(),next);
			}
			return null;
		}
	}
}
