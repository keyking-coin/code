package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.ArmyDetail;
import com.joymeng.list.EquipPosRarQue;
import com.joymeng.list.TroopState;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.data.RoleHonor;
import com.joymeng.slg.domain.object.technology.Technology;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class QueryInformation extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String project = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
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
			String otherType = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000059;
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

			switch (otherType) {
			case "Armys":

				List<ArmyInfo> armys = role.getCity(0).getArmyAgent().getAllCityArmy();
				if (armys == null || armys.size() == 0) {
					Map<String, Object> eMap = new HashMap<String, Object>();
					eMap.put("status", 0);
					eMap.put("msg", "没有任何兵种");
					String backMsg = JsonUtil.ObjectToJsonString(eMap);
					resp.getParams().put(backMsg);
					return resp;
				} else {
					List<ArmyDetail> armyList = role.getCity(0).getArmyAgent().getArmyDetails();
					Map<String, Object> aMap = new HashMap<String, Object>();
					Map<String, Object> bMap = new HashMap<String, Object>();
					List<Object> list = new ArrayList<Object>();
					for(ArmyDetail army: armyList){
						Map<String,Object> map =new LinkedHashMap<String,Object>();
						map.put("armyName", army.getArmyName());
						map.put("allNum", army.getAllNum());
						map.put("injureNum", army.getInjureNum());
						list.add(map);
					}
					Map<String, Object> one = new HashMap<String, Object>();
					one.put("1", list);
					aMap.put("status", 1);
					aMap.put("msg", "success");
					bMap.put("PlayerArmys", one);
					aMap.put("data", bMap);
					String bMsg = JsonUtil.ObjectToJsonString(aMap);
					resp.getParams().put(bMsg);
					return resp;
				}

			case "Builds":
				List<Object> list = new ArrayList<Object>();
				List<RoleBuild> builds = role.getCity(0).getBuilds();
				for (int i = 0 ; i < builds.size() ; i++){
					RoleBuild build = builds.get(i);
					Map<String,Object> map =new LinkedHashMap<String,Object>();
					map.put("buildSlot", build.getSlotID());
					map.put("buildName", build.getBuildId());
					map.put("level", build.getLevel());
					byte buildState = build.getState();
					String state =RoleBuild.getStatebyte(buildState);
					map.put("state", state);
					list.add(map);
				}

				Map<String, Object> aMap = new HashMap<String, Object>();
				Map<String, Object> bMap = new HashMap<String, Object>();
				Map<String, Object> one = new HashMap<String, Object>();
				one.put("1", list);
				aMap.put("status", 1);
				aMap.put("msg", "success");
				bMap.put("PlayerBuilds", one);
				aMap.put("data", bMap);
				String bMsg1 = JsonUtil.ObjectToJsonString(aMap);
				resp.getParams().put(bMsg1);
				return resp;

			case "Items":
				JSONObject outData2 = new JSONObject();// json对象
				JSONArray playerItems = new JSONArray();// 玩家物品
				outData2.put("PlayerItems", playerItems);
				List<ItemCell> items = role.getBagAgent().getRoleItems();//物品和材料，不包括装备
				if (items.size() == 0 || items == null) {
					Map<String, Object> dMap = new HashMap<String, Object>();
					dMap.put("status", 0);
					dMap.put("msg", "没有物品");
					String backMsg = JsonUtil.ObjectToJsonString(dMap);
					resp.getParams().put(backMsg);
					return resp;
				} else {
					for (int i = 0 ; i < items.size() ; i++){
						ItemCell item = items.get(i);
						JSONObject itNode = new JSONObject();
						itNode.put("itemName", item.getKey());
						itNode.put("itemNum", item.getNum());
						playerItems.add(itNode);
					}

					Map<String, Object> aMap2 = new HashMap<String, Object>();
					Map<String, Object> one1 = new HashMap<String, Object>();
					one1.put("1", outData2);
					aMap2.put("status", 1);
					aMap2.put("msg", "success");
					aMap2.put("data", one1);
					String bMsg2 = JsonUtil.ObjectToJsonString(aMap2);
					resp.getParams().put(bMsg2);
					return resp;
				}

			case "Resources":
				List<Object> list3 =new ArrayList<Object>();
				Map<ResourceTypeConst, Long> resources = role.getCity(0).getResources();
				for (ResourceTypeConst retype : resources.keySet()) {
					Map<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("resName", retype.getKey());
					map.put("resNum", resources.get(retype));
					list3.add(map);
				}
				Map<String, Object> aMap3 = new HashMap<String, Object>();
				Map<String, Object> bMap3 = new HashMap<String, Object>();
				Map<String, Object> three = new HashMap<String, Object>();
				three.put("2", list3);
				aMap3.put("status", 1);
				aMap3.put("msg", "success");
				bMap3.put("PlayerResources", three);
				aMap3.put("data", bMap3);  
				String bMsg3 = JsonUtil.ObjectToJsonString(aMap3);
				resp.getParams().put(bMsg3);
				return resp;

			case "Technology":
				List<Object> list4 = new ArrayList<Object>();
				Map<String, Technology> technology = role.getCity(0).getTechAgent().getTechMap();
				if (technology != null && technology.size() != 0) {
					for (Technology tech : technology.values()) {
						Map<String,Object> map =new LinkedHashMap<String,Object>();
						map.put("techName", tech.getTechId());
						map.put("tcehLevel", tech.getLevel());
						map.put("state", "已研究");
						list4.add(map);
					}
	
					List<Tech> data = dataManager.serachList(Tech.class); // 玩家所有的科技
					List<Tech> wData = new ArrayList<Tech>(); // 科技树getTechTreeID 为1的
					for (int i = 0 ; i < data.size() ; i++){
						Tech t = data.get(i);
						if (t.getTechTreeID().equals("1")) {
							wData.add(t);
						}
					}
                 
					for (int i = 0 ; i < data.size() ; i++){
						Tech c = data.get(i);
						if(technology.containsKey(c.getId())){
							continue;
						}
						Map<String,Object> map =new LinkedHashMap<String,Object>();
						map.put("techName", c.getId());
						map.put("tcehLevel", 0);
						List<String> precedingTechList = c.getPrecedingTechList();
						if (role.getCity(0).getTechAgent().JudgTechCondition(precedingTechList)) {
							map.put("state", "已解锁");
						} else {
							map.put("state", "未解锁");
						}
						list4.add(map);
					}
					
					Map<String, Object> aMap4 = new HashMap<String, Object>();
					Map<String, Object> bMap4 = new HashMap<String, Object>();
					Map<String, Object> four = new HashMap<String, Object>();
					four.put("1", list4);
					aMap4.put("status", 1);
					aMap4.put("msg", "success");
					bMap4.put("PlayerTechnology", four);
					aMap4.put("data", bMap4);
					String bMsg4 = JsonUtil.ObjectToJsonString(aMap4);
					resp.getParams().put(bMsg4);
					return resp;

				} else {
					List<Tech> data = dataManager.serachList(Tech.class); // 玩家所有的科技
					List<Tech> wData = new ArrayList<Tech>(); // 科技树Id 1 或2的
					for (int i = 0 ; i < data.size() ; i++){
						Tech t = data.get(i);
						if (t.getTechTreeID().equals("1")) {
							wData.add(t);
						}
					}
					
					for (int i = 0 ; i < data.size() ; i++){
						Tech c = data.get(i);
						Map<String,Object> map =new LinkedHashMap<String,Object>();
						map.put("techName", c.getId());
						map.put("tcehLevel", 0);
						List<String> precedingTechList = c.getPrecedingTechList();
						if (role.getCity(0).getTechAgent().JudgTechCondition(precedingTechList)) {
							map.put("state", "已解锁");
						} else {
							map.put("state", "未解锁");
						}
						list4.add(map);
					}
					
					Map<String, Object> aMap4 = new HashMap<String, Object>();
					Map<String, Object> bMap4 = new HashMap<String, Object>();
					Map<String, Object> four = new HashMap<String, Object>();
					four.put("1", list4);
					aMap4.put("status", 1);
					aMap4.put("msg", "success");
					bMap4.put("PlayerTechnology", four);
					aMap4.put("data", bMap4);
					String bMsg4 = JsonUtil.ObjectToJsonString(aMap4);
					resp.getParams().put(bMsg4);
					return resp;
				}

			case "Defense":
				JSONObject outData5 = new JSONObject();// json对象
				JSONArray playerDefense = new JSONArray();// 玩家防御设施
				outData5.put("5", playerDefense);
				List<RoleBuild> deBuilds = role.getCity(0).getBuilds();
				int gdNUm = 0;
				int tcNum = 0;
				int ltNUm = 0;
				for (int i = 0 ; i < deBuilds.size() ; i++){
					RoleBuild bd = deBuilds.get(i);
					BuildComponentDefense defenseComponent = bd
							.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
					if (defenseComponent != null) {
						if (bd.getName().equals("GrandeCannon")) {
							gdNUm++;
						}
						if (bd.getName().equals("TeslaCoil")) {
							tcNum++;
						}
						if (bd.getName().equals("LaserTower")) {
							ltNUm++;
						}
					}
				}
				JSONObject deNode = new JSONObject();
				deNode.put("defenseName", BuildName.GRANDE_CANNON.getKey());
				deNode.put("defenseNum", gdNUm);
				playerDefense.add(deNode);

				JSONObject deNode1 = new JSONObject();
				deNode1.put("defenseName", BuildName.TESLA_COIL.getKey());
				deNode1.put("defenseNum", tcNum);
				playerDefense.add(deNode1);

				JSONObject deNode2 = new JSONObject();
				deNode2.put("defenseName", BuildName.LASER_TOWER.getKey());
				deNode2.put("defenseNum", ltNUm);
				playerDefense.add(deNode2);

				Map<String, Object> aMap4 = new HashMap<String, Object>();
				Map<String, Object> five = new HashMap<String, Object>();
				five.put("PlayerDefense", outData5);
				aMap4.put("status", 1);
				aMap4.put("msg", "success");
				aMap4.put("data", five);
				String bMsg4 = JsonUtil.ObjectToJsonString(aMap4);
				resp.getParams().put(bMsg4);
				return resp;

			case "Equipment":
				JSONObject outData6 = new JSONObject();// json对象
				JSONArray playerEquipment = new JSONArray();// 玩家装备
				outData6.put("PlayerEquipment", playerEquipment);
				RoleBagAgent roleBagAgent = role.getBagAgent();
				if (roleBagAgent != null) {
					List<ItemCell> equips = role.getBagAgent().getEquip();
					if (equips.size() == 0 || equips == null) {
						Map<String, Object> fMap = new HashMap<String, Object>();
						fMap.put("status", 0);
						fMap.put("msg", "没有装备");
						String backMsg = JsonUtil.ObjectToJsonString(fMap);
						resp.getParams().put(backMsg);
						return resp;
					} else {
						List<Object> list6 = new ArrayList<Object>();
						for (int i = 0 ; i < equips.size() ; i++){
							ItemCell item = equips.get(i);
							EquipItem equip = (EquipItem) item;
							Equip eq = dataManager.serach(Equip.class,equip.getKey());
							Map<String,Object> map = new LinkedHashMap<String,Object>();
							map.put("equipId", equip.getId());
							map.put("equipName", eq.getId());
							map.put("position", EquipPosRarQue.getEquipPosition(eq.getEquipType()));
							map.put("rarity", EquipPosRarQue.getEquipRarity(eq.getRarity())); // 稀有度
							map.put("quality", EquipPosRarQue.getEquipQuality(eq.getEquipQuality()));
							map.put("levLimit", eq.getUseLimitation());
							List<String> buffEffect = new ArrayList<String>();
							buffEffect = equip.getEquipBuffIdLists();
							map.put("buffEffect", JsonUtil.ObjectToJsonString(buffEffect));
							map.put("state", EquipPosRarQue.getEquipState(equip.getEquipState()));
							list6.add(map);
						}
						Map<String, Object> aMap2 = new HashMap<String, Object>();
						Map<String, Object> bMap2 = new HashMap<String, Object>();
						Map<String, Object> six = new HashMap<String, Object>();
						six.put("6", list6);
						aMap2.put("status", 1);
						aMap2.put("msg", "success");
						bMap2.put("PlayerEquipment", six);
						aMap2.put("data", bMap2);
						String bMsg2 = JsonUtil.ObjectToJsonString(aMap2);
						resp.getParams().put(bMsg2);
						return resp;

					}
				}
			case "Medals":
				JSONObject outData8 = new JSONObject();// json对象
				JSONArray medals = new JSONArray();// 玩家勋章
				outData8.put("9", medals);
				Map<String ,RoleHonor> honMap=role.getHonorAgent().getFalgMap();
				for(RoleHonor roHoner:honMap.values()){
					JSONObject mdNode = new JSONObject();
					mdNode.put("medalName", roHoner.getId());
				    int star   =	 roHoner.getStarNum();
				    String strStar = null;
				    switch (star) {
					case 0:
						strStar = "0星";
						break;
					case 1:
						strStar = "1星";
						break;
					case 2:
						strStar = "2星";
						break;
					case 3:
						strStar = "3星";
						break;
					case 4:
						strStar = "4星";
						break;
					case 5:
						strStar = "5星";
						break;	
					default:
						break;
					}
					mdNode.put("star", strStar);
					medals.add(mdNode);
				}
				Map<String, Object> aMap9 = new HashMap<String, Object>();
				Map<String, Object> nine = new HashMap<String, Object>();
				nine.put("PlayerMedals", outData8);
				aMap9.put("status", 1);
				aMap9.put("msg", "success");
				aMap9.put("data", nine);
				String bMsg9 = JsonUtil.ObjectToJsonString(aMap9);
				resp.getParams().put(bMsg9);
				return resp;
				
			case "Troops":
				JSONObject outData7 = new JSONObject();// json对象
				JSONArray troops = new JSONArray();// 玩家部队
				outData7.put("7", troops);
				List<GarrisonTroops> gaList = mapWorld.getRelevanceGarrisons(role.getId()); // 部队在地图某点不动
				List<ExpediteTroops> exList = mapWorld.getMyRoleExpedites(role.getId()); // 行军部队
				if ((gaList == null || gaList.size() == 0)
						&& (exList == null || exList.size() == 0)) {
					Map<String, Object> fMap = new HashMap<String, Object>();
					fMap.put("status", 0);
					fMap.put("msg", "没有任何部队");
					String backMsg = JsonUtil.ObjectToJsonString(fMap);
					resp.getParams().put(backMsg);
					return resp;
				} else {
					if (gaList != null && gaList.size() != 0) {
						for (int i = 0 ; i < gaList.size() ; i++){
							GarrisonTroops gt = gaList.get(i);
							TroopsData gaTroops = gt.getTroops();
							List<ArmyEntity> arList = gaTroops.getArmys();
							for (int j = 0 ; j < arList.size() ; j++){
								ArmyEntity ae = arList.get(j);
								JSONObject trNode = new JSONObject();
//								trNode.put("state",	TimerLastType.TIME_MAP_STATION.getKey()); // 驻扎状态
								trNode.put("state",TroopState.getTroopStr(gt.getTimer().getType().getKey())	);
								String strP = MapUtil.getStrPosition(gt.getPosition());
								trNode.put("strPosition", strP);
								trNode.put("endPosition", strP);
								Army army = dataManager.serach(Army.class,
										ae.getKey());
								trNode.put("armyName", army.getId());
								trNode.put("armyNum",
										ae.getSane() + ae.getInjurie());
								troops.add(trNode);
							}
						}
					}
					if (exList != null && exList.size() != 0) {
						for (int i = 0 ; i < exList.size() ; i++){
							ExpediteTroops et = exList.get(i);
							List<TroopsData> teams = et.getTeams(); // 军团
							for (int j = 0 ; j < teams.size() ; j++){
								TroopsData td = teams.get(j);
								List<ArmyEntity> exAList = td.getArmys();
								for (int k = 0 ; k < exAList.size() ; k++){
									ArmyEntity ex = exAList.get(k);
									JSONObject trNode = new JSONObject();
									trNode.put("state", TroopState.getTroopStr(et.getTimer().getType().getKey())); // 部队状态
									trNode.put("strPosition",MapUtil.getStrPosition(et.getStartPosition()));
									trNode.put("endPosition", MapUtil.getStrPosition(et.getTargetPosition()));
									Army army = dataManager.serach(Army.class,
											ex.getKey());
									trNode.put("armyName", army.getId());
									trNode.put("armyNum",
											ex.getSane() + ex.getInjurie());
									troops.add(trNode);
								}
							}
						}
					}

					Map<String, Object> fMap = new HashMap<String, Object>();
					Map<String, Object> seven = new HashMap<String, Object>();
					seven.put("PlayerTroops", outData7);
					fMap.put("status", 1);
					fMap.put("msg", "success");
					fMap.put("data",seven);
					String fMsg = JsonUtil.ObjectToJsonString(fMap);
					resp.getParams().put(fMsg);
					return resp;

				}

			default:
				break;
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
		return null;
	}


}
