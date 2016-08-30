package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.ArmyDetail;
import com.joymeng.list.EquipPosRarQue;
import com.joymeng.list.OperationButton;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.technology.Technology;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class QueryGenPart extends ServiceHandler {
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
			String project = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000006F;
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
			List<Object> list = new ArrayList<Object>();
			if (role.isOnline()) {
				OperationButton ob = new OperationButton();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", "状态");
				map.put("value", "在线");
				ob.setButton("踢出");
				ob.setUrl("logic=HttpModifyEcu&playerUid=" + uid
						+ "&playerServerId=" + serverId
						+ "&playerProject=state");
				List<Object> button = new ArrayList<Object>();
				button.add(ob);
				map.put("operationButton", button);
				list.add(map);

			} else {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", "状态");
				map.put("value", "离线");
				List<Object> button = new ArrayList<Object>();
				map.put("operationButton", button);
				list.add(map);

			}

			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("title", "昵称");
			map1.put("value", role.getName());
			List<Object> button1 = new ArrayList<Object>();
			map1.put("operationButton", button1);
			list.add(map1);

			switch (project) {
			case "Items":
				List<Item> itemL = dataManager.serachList(Item.class);
				Map<String,Object> map =new HashMap<String,Object>();
				for (int i = 0 ; i < itemL.size() ; i++){
					Item it = itemL.get(i);
					map.put(it.getId(), it.getBeizhuname());
				}
				Map<String, Object> map9 = new HashMap<String, Object>();
				map9.put("title", "添加物品");
				OperationButton ob9 = new OperationButton();
				ob9.setButton("添加物品");
				ob9.setChangeOrmodify("数量");
				ob9.setInput("");
				ob9.setSelect(map);
				ob9.setUrl("logic=HttpModifyAddItem&playerUid=" + uid
						+ "&playerServerId=" + serverId + "");
				List<Object> button9 = new ArrayList<Object>();
				button9.add(ob9);
				map9.put("operationButton", button9);
				list.add(map9);
				
				List<ItemCell> items = role.getBagAgent().getRoleItems(); // 玩家物品
				if (items == null || items.size() == 0) {
					GameLog.info("select item null");
				} else {
					for (int i = 0 ; i < items.size() ; i++){
						ItemCell ic = items.get(i);
						Map<String, Object> map2 = new LinkedHashMap<String, Object>();
						map2.put("itemName", ic.getKey());
						map2.put("ItemNum", ic.getNum());
						OperationButton ob2 = new OperationButton();
						ob2.setButton("修改");
						ob2.setChangeOrmodify("变化量");
						ob2.setInput("");
						ob2.setUrl("logic=HttpModifyItem&playerUid=" + uid
								+ "&playerServerId=" + serverId
								+ "&playerItem=" + ic.getKey()
								+ "");
						List<Object> button2 = new ArrayList<Object>();
						button2.add(ob2);
						map2.put("operationButton", button2);
						list.add(map2);
					}
				}

				break;
			case "Resources":
				Map<ResourceTypeConst, Long> resources = role.getCity(0).getResources();
				for (ResourceTypeConst retype : resources.keySet()) {
					Map<String, Object> map2 = new HashMap<String, Object>();
					map2.put("resName", retype.getKey());
					map2.put("resNum", resources.get(retype));
					OperationButton ob2 = new OperationButton();
					ob2.setButton("修改");
					ob2.setChangeOrmodify("变化量");
					ob2.setInput("");
					ob2.setUrl("logic=HttpModifyRes&playerUid=" + uid
							+ "&playerServerId=" + serverId
							+ "&playerResource=" + retype.getKey()
							+ "");
					List<Object> button2 = new ArrayList<Object>();
					button2.add(ob2);
					map2.put("operationButton", button2);
					list.add(map2);

				}

				break;
			case "Armys":
				List<ArmyInfo> armys = role.getCity(0).getArmyAgent()
						.getAllCityArmy();
				if (armys == null || armys.size() == 0) {
					Map<String, Object> eMap = new HashMap<String, Object>();
					eMap.put("status", 0);
					eMap.put("msg", "没有任何兵种");
					String backMsg = JsonUtil.ObjectToJsonString(eMap);
					resp.getParams().put(backMsg);
					return resp;
				} else {
					List<ArmyDetail> armyList = role.getCity(0).getArmyAgent().getArmyDetails();
					for (int i = 0 ; i < armyList.size() ; i++){
						ArmyDetail army = armyList.get(i);
						Map<String, Object> map2 = new HashMap<String, Object>();
						map2.put("title", army.getArmyName());
						map2.put("value", army.getAllNum());
						OperationButton ob2 = new OperationButton();
						ob2.setButton("修改");
						ob2.setChangeOrmodify("变化量");
						ob2.setInput("");
						ob2.setUrl("logic=HttpModifyArmy&playerUid=" + uid
								+ "&playerServerId=" + serverId
								+ "&playerArmy=" + army.getArmyName()
								+ "");
						List<Object> button2 = new ArrayList<Object>();
						button2.add(ob2);
						map2.put("operationButton", button2);
						list.add(map2);

					}
				}
				break;
			case "Builds":
				List<RoleBuild> builds = role.getCity(0).getBuilds();
				for (int i = 0 ; i < builds.size() ; i++){
					RoleBuild build = builds.get(i);
					Map<String, Object> map2 = new HashMap<String, Object>();
					map2.put("SLOT", build.getSlotID());
					map2.put("buildId", build.getBuildId());
					map2.put("buildLevel", build.getLevel());
					byte buildState = build.getState();
					String state =RoleBuild.getStatebyte(buildState);
					map2.put("state", state);
					List<Object> button2 = new ArrayList<Object>();
					OperationButton ob2 = new OperationButton();
					ob2.setButton("查询");
					ob2.setFlag("1");
					ob2.setUrl("logic=HttpQueryOneBuild&playerUid=" + uid
							+ "&playerServerId=" + serverId
							+ "&playerBuildSlot=" + build.getSlotID() + "");
					button2.add(ob2);
					map2.put("operationButton", button2);
					list.add(map2);
				}

				List<String> ls = role.getCity(0).getUnusedSlots();
				for (int i = 0 ; i < ls.size() ; i++){
					String s = ls.get(i);
					Map<String, Object> map3 = new HashMap<String, Object>();
					map3.put("SLOT", s);
					map3.put("buildId", "");
					map3.put("buildLevel", "");
					map3.put("state", "");
					List<Object> button3 = new ArrayList<Object>();
					OperationButton ob3 = new OperationButton();
					ob3.setButton("查询");
					ob3.setFlag("1");
					ob3.setUrl("logic=HttpQueryOneBuild&playerUid=" + uid
							+ "&playerServerId=" + serverId
							+ "&playerBuildSlot=" + s + "");
					button3.add(ob3);
					map3.put("operationButton", button3);
					list.add(map3);
				}
				break;

			case "Equipment":
				List<Equip> eq = dataManager.serachList(Equip.class);
				Map<String,Object> eqMap =new HashMap<String,Object>();
				for (int i = 0 ; i < eq.size() ; i++){
					Equip q = eq.get(i);
					eqMap.put(q.getId(), q.getBeizhuname());
				}
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("title", "添加装备");
				List<Object> button2 = new ArrayList<Object>();
				OperationButton ob2 = new OperationButton();
				ob2.setChangeOrmodify("数量");
				ob2.setInput("");
				ob2.setSelect(eqMap);
				ob2.setButton("添加装备");
				ob2.setUrl("logic=HttpModifyAddEquip&playerUid=" + uid
						+ "&playerServerId=" + serverId + "");
				button2.add(ob2);
				map2.put("operationButton", button2);
				list.add(map2);

				RoleBagAgent roleBagAgent = role.getBagAgent();
				if (roleBagAgent != null) {
					List<ItemCell> equips = role.getBagAgent().getEquip();
					if (equips.size() == 0 || equips == null) {
					GameLog.info("select equips null");
					} else {
						for (int i = 0 ; i < equips.size() ; i++){
							ItemCell item = equips.get(i);
							EquipItem equip = (EquipItem) item;
							Map<String, Object> map3 = new LinkedHashMap<String, Object>();
							Equip eqp = dataManager.serach(Equip.class,
									equip.getKey());
							map3.put("equipId", equip.getId());
							map3.put("Equip", eqp.getId());
							map3.put("position", EquipPosRarQue.getEquipPosition(eqp.getEquipType()));
							map3.put("rarity", eqp.getRarity()); // 稀有度
							map3.put("quality", eqp.getEquipQuality());
							map3.put("levLimit", eqp.getUseLimitation());
							map3.put("state", EquipPosRarQue.getEquipState(equip.getEquipState()));
							List<Object> button3 = new ArrayList<Object>();
							OperationButton ob3 = new OperationButton();
							ob3.setButton("查询");
							ob3.setFlag("1");
							ob3.setUrl("logic=HttpQueryOneEquip&playerUid="
									+ uid + "&playerServerId=" + serverId
									+ "&playerEquipId=" + equip.getId() + "");
							button3.add(ob3);
							map3.put("operationButton", button3);
							list.add(map3);
						}
					}
				}
				break;
			case "Technology":
				Map<String, Technology> technology = role.getCity(0)
						.getTechAgent().getTechMap();

				if (technology != null && technology.size() != 0) {
					for (Technology tech : technology.values()) {

						OperationButton ob4 = new OperationButton();
						Map<String, Object> map4 = new HashMap<String, Object>();
						map4.put("title", "科技");
						map4.put("value", tech.getTechId());
						ob4.setButton("查询");
						ob4.setFlag("1");
						ob4.setUrl("logic=HttpQueryOneTech&playerUid=" + uid
								+ "&playerServerId=" + serverId
								+ "&playerTechId=" + tech.getTechId() + "");
						List<Object> button4 = new ArrayList<Object>();
						button4.add(ob4);
						map4.put("operationButton", button4);
						list.add(map4);
					}
				}

				List<Tech> data = dataManager.serachList(Tech.class); // 玩家所有的科技
				List<Tech> wData = new ArrayList<Tech>(); // 科技树Id 1 或2的
				for (int i = 0 ; i < data.size() ; i++){
					Tech t = data.get(i);
					if (t.getTechTreeID().equals("1")) {
						wData.add(t);
					}
				}
				for (int i = 0 ; i < wData.size() ; i++){
					Tech c = wData.get(i);
					if (technology.containsKey(c.getId())) {
						continue;
					}
					List<String> precedingTechList = c.getPrecedingTechList();
					if (role.getCity(0).getTechAgent()
							.JudgTechCondition(precedingTechList)) {
						OperationButton ob5 = new OperationButton();
						Map<String, Object> map5 = new HashMap<String, Object>();
						map5.put("tecName", c.getId());
						ob5.setButton("查询");
						ob5.setFlag("1");
						ob5.setUrl("logic=HttpQueryOneTech&playerUid=" + uid
								+ "&playerServerId=" + serverId
								+ "&playerTechId=" + c.getId() + "");
						List<Object> button5 = new ArrayList<Object>();
						button5.add(ob5);
						map5.put("operationButton", button5);
						list.add(map5);
					}

				}
				break;
			default:
				break;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			Map<String, Object> one = new HashMap<String, Object>();
			one.put("1", list);
			bMap.put("ModifyPlayerOther", one);
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
