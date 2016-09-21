package com.joymeng.slg.domain.map.impl.still.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.map.impl.still.copy.data.Ruins;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

/**
 * 用户副本体
 * @author houshanping
 */
public class RoleRelic implements Instances{
	Map<Integer, Relic> relicArmys = new HashMap<Integer, Relic>(); // 在不同地点的副本部队
	byte isGotReward = 2;   //奖励状态 2:未通关 0:通关未领取 1:通关已领取 
	int type;// 副本类型
	List<Scene> scenes = new ArrayList<>();// 关卡信息
	List<String> finishReward = new ArrayList<>();//通关奖励
	int currentFreeResetNum = GameConfig.RELIC_MAX_FREE_RESET_NUM;//当前可用的免费的重置次数
	int currentItemResetNum = GameConfig.RELIC_MAX_ITEM_RESET_NUM;//当前可用的道具的重置次数
	String monsterIconId = "";//怪物头像的ID
	long saveTime = 0;//存储的时间戳
	
	public RoleRelic() {
	}

	public Map<Integer, Relic> getRelicArmys() {
		return relicArmys;
	}

	public List<Scene> getScenes() {
		return scenes;
	}

	public void setScenes(List<Scene> scenes) {
		this.scenes = scenes;
	}

	public void setRelicArmys(Map<Integer, Relic> relicArmys) {
		this.relicArmys = relicArmys;
	}

	public String getMonsterIconId() {
		return monsterIconId;
	}

	public void setMonsterIconId(String monsterIconId) {
		this.monsterIconId = monsterIconId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getFinishReward() {
		return finishReward;
	}

	public void setFinishReward(List<String> finishReward) {
		this.finishReward = finishReward;
	}

	public byte getIsGotReward() {
		return isGotReward;
	}

	public void setIsGotReward(byte isGotReward) {
		this.isGotReward = isGotReward;
	}

	public int getCurrentFreeResetNum() {
		return currentFreeResetNum;
	}

	public void setCurrentFreeResetNum(int currentFreeResetNum) {
		this.currentFreeResetNum = currentFreeResetNum;
	}

	public int getCurrentItemResetNum() {
		return currentItemResetNum;
	}

	public void setCurrentItemResetNum(int currentItemResetNum) {
		this.currentItemResetNum = currentItemResetNum;
	}

	public long getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}
	
	/**
	 * 判断副本时候已经通关
	 * 
	 * @return
	 */
	public boolean isFinish() {
		Ruins ruins = dataManager.serach(Ruins.class, new SearchFilter<Ruins>() {
			@Override
			public boolean filter(Ruins data) {
				return data.getType() == type;
			}
		});
		if (ruins == null) {
			GameLog.error("read Ruins is fail!----warning");
			return true;
		}
		if (scenes.size() == ruins.getCheckpoin().size() && searchLastScene().getState() == 1) {
			return true;
		}
		return false;
	}

	public Scene searchLastScene() {
		return scenes.get(scenes.size() - 1);
	}

	/**
	 * 重置关卡
	 */
	public void resetScenes() {
		Relic[] relics = new Relic[relicArmys.size()];
		relicArmys.values().toArray(relics);
		if (relics.length < 1) {
			GameLog.error("relicArmys is null !");
			return;
		}
		// 更新关卡的信息
		scenes.clear();
		Ruins ruin = dataManager.serach(Ruins.class, relics[0].getId());
		if (ruin == null) {
			GameLog.error("read Ruins table is fail");
			return;
		}
		Scene scene = mapWorld.createScene(ruin.getCheckpoin().get(0));
		scenes.add(scene);
		//TODO 更新奖励的列表
		List<String> rewards = randomRelicReward(ruin);
		finishReward = rewards;
		// 更新奖励领取的状态为 未领取
		isGotReward = 2;
	}

	public void serialize(JoyBuffer out) {
		out.putInt(relicArmys.size());
		for (Integer pos : relicArmys.keySet()) {
			out.putInt(pos);
			relicArmys.get(pos).serialize(out);
		}
		out.put(isGotReward);
		out.putInt(type);// 副本类型
		out.putInt(scenes.size());// 关卡信息
		for (int i = 0 ; i < scenes.size() ; i++){
			Scene scene = scenes.get(i);
			scene.serialize(out);
		}
		out.putPrefixedString(JsonUtil.ObjectToJsonString(finishReward), JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(currentFreeResetNum);
		out.putInt(currentItemResetNum);
		out.putPrefixedString(monsterIconId, JoyBuffer.STRING_TYPE_SHORT);
		saveTime = TimeUtils.nowLong();
		out.putLong(saveTime);
	}

	public void deserialize(JoyBuffer buffer) {
		int size = buffer.getInt();
		for (int i = 0; i < size; i++) {
			int pos = buffer.getInt();
			Relic relic = new Relic();
			relic.deserialize(buffer);
			relicArmys.put(pos, relic);
		}
		isGotReward = buffer.get();
		type = buffer.getInt();// 副本类型
		int ssize = buffer.getInt();
		for (int i = 0; i < ssize; i++) {
			Scene scene = new Scene();
			scene.deserialize(buffer);
			scenes.add(scene);
		}
		String datas = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		finishReward = JsonUtil.JsonToObjectList(datas, String.class);
		currentFreeResetNum = buffer.getInt();
		currentItemResetNum = buffer.getInt();
		monsterIconId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		saveTime = buffer.getLong();
		if (!TimeUtils.isSameDay(saveTime, TimeUtils.nowLong())) {
			resetRelicResetNum();
		}
	}
	
	/**
	 * 重置 副本重置次数
	 */
	public void resetRelicResetNum() {
		currentFreeResetNum = GameConfig.RELIC_MAX_FREE_RESET_NUM;// 当前可用的免费的重置次数
		currentItemResetNum = GameConfig.RELIC_MAX_ITEM_RESET_NUM;// 当前可用的道具的重置次数
	}

	/**
	 * 更新关卡的信息
	 */
	public void updateScene() {
		if (!isFinish() && searchLastScene().getState() == 1) {
			Relic[] relics = new Relic[relicArmys.size()];
			relicArmys.values().toArray(relics);
			if (relics.length < 1) {
				GameLog.error("relicArmys is null !");
				return;
			}
			Ruins ruins = dataManager.serach(Ruins.class, relics[0].getId());
			if (ruins == null) {
				return;
			}
			Scene scene = mapWorld.createScene(ruins.getCheckpoin().get(scenes.size()));
			if (scene == null) {
				return;
			}
			scenes.add(scene);
		}
	}

	/**
	 * 随机出一组奖励列表
	 * 
	 * @param ruin
	 * @return
	 */
	public List<String> randomRelicReward(Ruins ruin) {
		List<String> result = new ArrayList<>();
		List<String> numberWeight = ruin.getNumberweight();
		Integer[] nums = new Integer[numberWeight.size()];
		int[] numRate = new int[numberWeight.size()];
		for (int i = 0; i < numberWeight.size(); i++) {
			String data = numberWeight.get(i);
			if (StringUtils.isNull(data)) {
				GameLog.error("numberWeight is  null");
				continue;
			}
			String[] param = data.split(":");
			if (param.length < 2) {
				GameLog.error("numberWeight length is error");
				continue;
			}
			nums[i] = Integer.valueOf(param[0]);
			numRate[i] = Integer.valueOf(param[1]);
		}
		int rewardNum = MathUtils.getRandomObj(nums, numRate);
		List<String> allRewards = new ArrayList<>();
		allRewards.addAll(ruin.getReward());
		while (rewardNum > 0) {
			String[] values = new String[allRewards.size()];
			int[] rates = new int[allRewards.size()];
			for (int i = 0; i < allRewards.size(); i++) {
				String rData = allRewards.get(i);
				if (StringUtils.isNull(rData)) {
					GameLog.error("Rewards is null");
					continue;
				}
				String[] rParams = rData.split(":");
				if (rParams.length < 4) {
					GameLog.error("Rewards length is error");
					continue;
				}
				values[i] = rData;
				rates[i] = Integer.valueOf(rParams[2]);
			}
			String temp = MathUtils.getRandomObj(values, rates);
			if (StringUtils.isNull(temp) || temp.split(":").length < 4) {
				GameLog.error("MathUtils.getRandomObj(values, rates) is error");
				continue;
			}
			allRewards.remove(temp);
			String[] tStrings = temp.split(":");
			String oneReward = tStrings[0] + ":" + tStrings[1] + ":" + tStrings[3];
			result.add(oneReward);
			rewardNum--;
		}
		return result;
	}
	
	/**
	 * 领取通关奖励
	 * @param role
	 * @param rewards
	 */
	public void getFinishReward(Role role){
		RespModuleSet rms = new RespModuleSet();
		List<Object> objects = new ArrayList<Object>();
		List<ItemCell> showItems = new ArrayList<>();
		for (int i = 0 ; i < finishReward.size() ; i++){
			String item = finishReward.get(i);
			String[] datas = item.split(":");
			String itemDataType = datas[0];// 类型
			String itemId = datas[1];// 物品Id
			int itemNum = Integer.valueOf(datas[2]);// 数量
			itemDataType = itemDataType.toLowerCase();
			if (itemDataType.equals("equip")) { // 装备
				List<ItemCell> temp = role.getBagAgent().addEquip(itemId, itemNum);
				if (temp == null || temp.size() < 1) {
					GameLog.error("策划ruins的通关奖励表填错了,找洪少文去查表");
					continue;
				}
				role.getBagAgent().sendItemsToClient(rms, temp);
				showItems.add(temp.get(0));
			} else if (itemDataType.equals("item")) {// 物品
				List<ItemCell> temp = role.getBagAgent().addGoods(itemId, itemNum);
				if (temp == null || temp.size() < 1) {
					GameLog.error("策划ruins的通关奖励表填错了,找洪少文去查表");
					continue;
				}
				role.getBagAgent().sendItemsToClient(rms, temp);
				showItems.add(temp.get(0));
			} else if (itemDataType.equals("material")) {
				List<ItemCell> temp = role.getBagAgent().addOther(itemId, itemNum);
				if (temp == null || temp.size() < 1) {
					GameLog.error("策划ruins的通关奖励表填错了,找洪少文去查表");
					continue;
				}
				role.getBagAgent().sendItemsToClient(rms, temp);
				showItems.add(temp.get(0));
			} else if (itemDataType.equals("resourcestype")) {// 资源
				ResourceTypeConst resType = ResourceTypeConst.search(itemId);
				switch (resType) {
				case RESOURCE_TYPE_FOOD:
				case RESOURCE_TYPE_METAL:
				case RESOURCE_TYPE_OIL:
				case RESOURCE_TYPE_ALLOY: {
					if (itemNum < 0) {
						break;
					}
					objects.add(resType);
					objects.add(itemNum);
					break;
				}
				case RESOURCE_TYPE_GOLD: {
					if (itemNum > 0) {
						role.addRoleMoney(itemNum);
						role.sendRoleToClient(rms);
					}
					break;
				}
				case RESOURCE_TYPE_COIN: {
					if (itemNum > 0) {
						role.addRoleCopper(itemNum);
						role.sendRoleToClient(rms);
					}
					break;
				}
				case RESOURCE_TYPE_KRYPTON: {
					if (itemNum > 0) {
						role.addRoleKrypton(itemNum);
						role.sendRoleToClient(rms);
					}
					break;
				}
				case RESOURCE_TYPE_GEM: {
					if (itemNum > 0) {
						role.addRoleGem(itemNum);
						role.sendRoleToClient(rms);
					}
					break;
				}
				case RESOURCE_TYPE_SILVER: {
					if (itemNum > 0) {
						role.addRoleSilver(itemNum);
						role.sendRoleToClient(rms);
					}
					break;
				}
				default:
					GameLog.error("暂未添加该资源类型!请联系侯善平修改");
					break;
				}
			}
            LogManager.itemOutputLog(role, itemNum, EventName.ruinsHarvest.getName(), itemId);
		}
		ItemCell[] aCells = new ItemCell[showItems.size()];
		role.getBagAgent().sendShowItemsToClient(rms, showItems.toArray(aCells));
		if (objects.size() > 0) {
			Object[] temp = objects.toArray();
			role.addResourcesToCity(rms, 0, temp);
			role.sendResourceToClient(false, rms, 0, objects);
			for (int i = 0; i < temp.length; i += 2) {
				ResourceTypeConst restype = (ResourceTypeConst) temp[i];
				long value = Long.parseLong(temp[i + 1].toString());
				if (value <= 0) {
					GameLog.error(" add resource value must > 0");
					continue;
				}
				String item = restype.getKey();
				LogManager.itemOutputLog(role, value, EventName.ruinsHarvest.getName(), item);
			}
		}
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}
}
