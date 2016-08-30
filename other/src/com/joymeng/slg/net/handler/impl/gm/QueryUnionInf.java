package com.joymeng.slg.net.handler.impl.gm;

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
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;

public class QueryUnionInf extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());
			int serverId = in.getInt();
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
			int serverId = params.get(2);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000096;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			List<UnionBody> unions = world.getListObjects(UnionBody.class);
			Map<String, Object> allMap = new HashMap<String, Object>();
            if(unions==null||unions.size()==0){
            	Map<String, Object> cMap = new HashMap<String, Object>();
				cMap.put("status", 0);
				cMap.put("msg", "没有任何联盟");
				String backMsg = JsonUtil.ObjectToJsonString(cMap);
				resp.getParams().put(backMsg);
				return resp;
            }else{
            	for(int i=0;i<unions.size();i++){
            		UnionBody body = unions.get(i);
        			List<Object> list =new ArrayList<Object>();
        			Map<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("unionId", body.getId());
                    map.put("unionName", body.getName());
                    map.put("shortName", body.getShortName());
                    map.put("createTime", body.getCreateTime());
                    map.put("leader", body.getLeaderName());
                    map.put("fight", body.getFight());
                    map.put("number", body.getMembers().size());
                    map.put("language", body.getLanguage());
                    list.add(map);
                    allMap.put(body.getName(), list);
            	}  	
            }
			Map<String, Object> aMap = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			aMap.put("status", 1);
			aMap.put("msg", "success");
			bMap.put("Unions", allMap);
			aMap.put("data", bMap);
			String bMsg = JsonUtil.ObjectToJsonString(aMap);
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
