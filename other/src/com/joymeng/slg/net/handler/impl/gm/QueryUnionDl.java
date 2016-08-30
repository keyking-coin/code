package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.data.Alliancemembers;
import com.joymeng.slg.union.data.Alliancetechlevel;
import com.joymeng.slg.union.data.RecordForGm;
import com.joymeng.slg.union.impl.UnionTech;

public class QueryUnionDl extends ServiceHandler implements Instances{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());
			int serverId = in.getInt();
	        Long unionId =in.getLong();
			params.put(serverId);
			params.put(unionId);
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
			long unionId = params.get(3);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000097;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			UnionBody body = unionManager.search(unionId);
			List<Object> baseList =new ArrayList<Object>(); //联盟基本信息
			List<Object> tecList =new ArrayList<Object>();  //联盟科技
			List<Object> goList = new ArrayList<Object>();  //联盟官职
			List<Object> cityList =new ArrayList<Object>(); //联盟城市
			List<Object> buildList =new ArrayList<Object>();//联盟建筑
			if (body == null) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				cMap.put("status", 0);
				cMap.put("msg", "联盟不存在");
				String backMsg = JsonUtil.ObjectToJsonString(cMap);
				resp.getParams().put(backMsg);
				return resp;
			}else{
				//联盟基本信息
				Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("unionId", body.getId());
                map.put("unionName", body.getName());
                map.put("shortName", body.getShortName());
                map.put("createTime", body.getCreateTime());
                map.put("leader", body.getLeaderName());
                map.put("fight", body.getFight());
                map.put("number", body.getMembers().size());
                map.put("language", body.getLanguage());
                RecordForGm  record = body.getRecord();
                map.put("fight", record.getFight());
                map.put("isWin", record.getIsWin());
                map.put("isFail", record.getIsFail());
                map.put("attWin", record.getAttWin());
                map.put("attFail", record.getAttFail());
                map.put("defWin", record.getDefWin());
                map.put("defFail", record.getDefFail());
                map.put("mass", record.getMass());
                baseList.add(map);
                //联盟科技
				Map<String,UnionTech> unionTechMap = body.getUnionTechMap();
				for (String str : unionTechMap.keySet()) {
					Map<String, Object> tecMap = new LinkedHashMap<String, Object>();
					UnionTech tech = unionTechMap.get(str);
					// 根据当前等级和经验去计算星级
					final String id = tech.getTechId();
					final int techlevel = tech.getTechlevel();
					Alliancetechlevel alliance = dataManager.serach(Alliancetechlevel.class,new SearchFilter<Alliancetechlevel>() {
								@Override
								public boolean filter(Alliancetechlevel data) {
									return data.getTechid().equals(id)
											&& data.getTechLevel() == ((techlevel + 1) > 20 ? 20 : techlevel + 1);
								}
							});
					int currentExp = tech.getCurrentExp();
					int currentStar = (int) (currentExp / alliance.getTechExp());
					tecMap.put("techId", id);
					tecMap.put("techStar", currentStar);
					tecMap.put("level", techlevel);
					tecList.add(tecMap);
				}
				//联盟官职
				List<Alliancemembers> members = dataManager.serachList(Alliancemembers.class);
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < members.size(); i++) {
					Alliancemembers member = members.get(i);
					if (member.getType() == 1) {
						list.add(String.valueOf(member.getRank()));
					}
				}
				Map<Integer, String> unionTitle = body.getUnionTitle();
				for (Integer in : unionTitle.keySet()) {
					Map<String, Object> tilMap = new LinkedHashMap<String, Object>();
					if (list.contains(String.valueOf(in))) {
						tilMap.put("gover", unionTitle.get(in));
						goList.add(tilMap);
					}
				}
				//联盟城市列表
				List<MapUnionCity> unionCitys = mapWorld.searchUnionCity(body.getId());
				for (int i = 0; i < unionCitys.size(); i++) {
					Map<String, Object> cityMap = new LinkedHashMap<String, Object>();
					MapUnionCity city = unionCitys.get(i);
					cityMap.put("cityName", city.getName());
					cityMap.put("position", city.getPosition());
					cityMap.put("level", city.getLevel());
					cityList.add(cityMap);
				}
				//联盟建筑列表
				for (int j = 0; j < unionCitys.size(); j++) {
					MapUnionCity city = unionCitys.get(j);
					List<MapUnionBuild> builds = city.searchBuilds();
					for(int  k=0;k<builds.size();k++){
						Map<String, Object> buildMap = new LinkedHashMap<String, Object>();
						MapUnionBuild build = builds.get(k);
						buildMap.put("buildName", build.getName());
						buildMap.put("buildPosition", build.getPosition());
						buildList.add(buildMap);
					}
				} 
			}	
			Map<String, Object> aMap = new HashMap<String, Object>();
			Map<String, Object> bMap = new LinkedHashMap<String, Object>();
			Map<String, Object> cMap = new HashMap<String, Object>();
			aMap.put("status", 1);
			aMap.put("msg", "success");
			aMap.put("version", 2);
			bMap.put("UnionBasic",baseList);
			bMap.put("UnionTech",tecList);
			bMap.put("UnionGover",goList);
			bMap.put("UnionCity",cityList);
			bMap.put("UnionBuild",buildList);
			cMap.put("AllianceDetails", bMap);
			aMap.put("data", cMap);
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
