package com.joymeng.slg.domain.object.bag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.still.copy.RoleRelic;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.bag.data.ItemType;
import com.joymeng.slg.domain.object.bag.data.Itembox;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.bag.impl.GoodsItem;
import com.joymeng.slg.domain.object.bag.impl.OtherItem;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.impl.BuildComponentProduction;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.data.Buff;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.VipInfo;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RoleBagAgent implements Instances {
	long uid;
	long maxItemKeyId = 1000;//
	Map<Class<? extends ItemCell>, Map<String, ItemCell>> cells = new HashMap<Class<? extends ItemCell>, Map<String, ItemCell>>();// 所有的背包数据，包括装备，材料，物品

	public void setUid(long uid) {
		this.uid = uid;
	}

	/**
	 * 
	 * @param item
	 */
	public void addItem(ItemCell item) {
		Map<String, ItemCell> items = cells.get(item.getClass());
		if (items == null) {
			items = new HashMap<String, ItemCell>();
			cells.put(item.getClass(), items);
		}
		items.put(item.primaryKey(), item);
	}

	/**
	 * 物品
	 * @param key
	 * @param num
	 * @return
	 */
	public List<ItemCell> addGoods(String key, int num) {
		List<ItemCell> result = new ArrayList<ItemCell>();
		try {
			Map<String, ItemCell> goodses = cells.get(GoodsItem.class);
			if (goodses == null) {
				goodses = new HashMap<String, ItemCell>();
				cells.put(GoodsItem.class, goodses);
			}
			if (dataManager.serach(Item.class, key) == null) {
				return result;
			}
			ItemCell cell = goodses.get(key);
			if (cell == null) {
				cell = new GoodsItem();
//				long id = keyData.key(DaoData.TABLE_RED_ALERT_BAG);
				maxItemKeyId += 1;
				long id = maxItemKeyId;
				cell.setId(id);
				cell.setKey(key);
				cell.setNum(num);
				cell.setUid(uid);
				addItem(cell);
			} else {
				cell.addNum(num);
			}
			result.add(cell);
		} catch (Exception e) {
			GameLog.error("addGoods error", e);
		}
		return result;
	}

	/**
	 * 装备
	 * @param key
	 * @param num
	 * @return
	 */
	public List<ItemCell> addEquip(String key, int num) {
		List<ItemCell> result = new ArrayList<ItemCell>();
		try {
			Map<String, ItemCell> equips = cells.get(EquipItem.class);
			if (equips == null) {
				equips = new HashMap<String, ItemCell>();
				cells.put(EquipItem.class, equips);
			}
			for (int i = 0; i < num; i++) {
				EquipItem equip = new EquipItem();
				List<String> randomBuffIdList = equip.randomBuffIdList(key);
				if (randomBuffIdList == null) {
					GameLog.error("randomBuffIdList is null!");
					return result;
				}
//				long id = keyData.key(DaoData.TABLE_RED_ALERT_BAG);
				maxItemKeyId += 1;
				long id = maxItemKeyId;
				equip.setId(id);
				equip.setKey(key);
				equip.setUid(uid);
				equip.setEquipBuffIdLists(randomBuffIdList);
				addItem(equip);
				result.add(equip);
				Role role = world.getObject(Role.class, uid);
				if (role != null) {
					// 任务事件
					role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_GET_EQUIP,
							key, 1);
				}
			}
		} catch (Exception e) {
			GameLog.error("addEquip error", e);
		}
		return result;
	}

	/**
	 * 材料
	 * @param key
	 * @param num
	 * @return
	 */
	public List<ItemCell> addOther(String key, int num) {
		List<ItemCell> result = new ArrayList<ItemCell>();
		try {
			Map<String, ItemCell> others = cells.get(OtherItem.class);
			if (others == null) {
				others = new HashMap<String, ItemCell>();
				cells.put(OtherItem.class, others);
			}
			if (dataManager.serach(Item.class, key) == null) {
				return result;
			}
			ItemCell cell = others.get(key);
			if (cell == null) {
				cell = new OtherItem();
				maxItemKeyId += 1;
				long id = maxItemKeyId;
				cell.setId(id);
				cell.setKey(key);
				cell.setNum(num);
				cell.setUid(uid);
				addItem(cell);
			} else {
				cell.addNum(num);
			}
			result.add(cell);
		} catch (Exception e) {
			GameLog.error("addGoods error", e);
		}
		return result;
	}

	public void deserialize(Object data){
		if(data == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap((byte[])data);
		maxItemKeyId = buffer.getLong();
		int size = buffer.getInt();
		for(int i = 0; i < size; i++){
			int itemSize = buffer.getInt();
			for(int j= 0 ; j < itemSize ; j++){
				long id   = buffer.getLong();
				String key  = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				long num  = buffer.getLong();
				byte type = buffer.get();
				String str    = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				ItemCell item = ItemCell.create(type);
				item.setId(id);
				item.setKey(key);
				item.setNum(num);
				item.deserializeEntiy(id, key, num, str);
				addItem(item);
			}
		}
	}
	
	public void serialize(SqlData data){
		JoyBuffer out = JoyBuffer.allocate(8192);
		out.putLong(maxItemKeyId);
		out.putInt(cells.size());
		for (Map<String, ItemCell> map : cells.values()){
			out.putInt(map.size());
			for (ItemCell item : map.values()){
				item.serializeEntiy(out);
			}
		}
		data.put(DaoData.RED_ALERT_ROLE_BAGDATAS, out.arrayToPosition());
	}

	public boolean checkItemFromBag(String itemId, long num) {
		for (Map<String, ItemCell> map : cells.values()) {
			ItemCell item = map.get(itemId);
			if (item != null && item.getNum() >= num) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param 获取装备
	 *            BY id
	 * @param equipId
	 * @return EquipItem
	 */
	public EquipItem getEquipById(long equipId) {
		Map<String, ItemCell> map = cells.get(EquipItem.class);
		EquipItem item = map == null ? null : (EquipItem) map.get(String.valueOf(equipId));
		if (item != null && item.num > 0 && item.getType() == ExpeditePackageType.PACKAGE_TYPE_EQUIP.ordinal()) {
			return item;
		}
		return null;
	}

	/**
	 * 获取物品信息 如果是装备key=id，如果是其他key=itemId
	 * 
	 * @param key
	 * @return
	 */
	public ItemCell getItemFromBag(String key) {
		for (Map<String, ItemCell> map : cells.values()) {
			ItemCell item = map.get(key);
			if (item != null) {
				return item;
			}
		}
		return null;
	}

	public boolean removeItems(String itemId, long num) {
		for (Map<String, ItemCell> map : cells.values()) {
			ItemCell item = map.get(itemId);
			if (item == null || item.getNum() < num) {
				continue;
			}
			if ((item.getNum() - num) == 0) {
				item.setNum(0); 
				map.remove(itemId);
			} else {
				item.setNum(item.getNum() - num);
				map.put(itemId, item);
			}
			return true;
		}
		return false;
	}

	public boolean removeEquip(String itemId) {
		Map<String, ItemCell> map = cells.get(EquipItem.class);
		ItemCell itemCell = map.get(itemId);
		if (itemCell == null) {
			return false;
		}
		itemCell.setState((byte) 1);
		map.remove(itemId);
		return true;
	}

	/**
	 * 获取玩家所有背包数据,包括装备和材料
	 * 
	 * @return
	 */
	public List<ItemCell> getRoleBag() {
		List<ItemCell> itemList = new ArrayList<ItemCell>();
		for (Map<String, ItemCell> map : cells.values()) {
			for (ItemCell item : map.values()) {
				itemList.add(item);
			}
		}
		return itemList;
	}

	
	/**
	 * 获取玩家背包数据,物品和材料
	 * @return
	 */
	public List<ItemCell> getRoleItems() {
		Map<String, ItemCell> gMap = cells.get(GoodsItem.class);
		Map<String, ItemCell> oMap = cells.get(OtherItem.class);
		List<ItemCell> itemList = new ArrayList<ItemCell>();
		if (gMap != null && gMap.size() > 0) {
			for (ItemCell item : gMap.values()) {
				itemList.add(item);
			}

		}
		if (oMap != null && oMap.size() > 0) {
			for (ItemCell item : oMap.values()) {
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	/**
	 * 获取玩家背包道具总数量
	 * @return
	 */
	
	public  int  getItemsCount() {
		Map<String, ItemCell> gMap = cells.get(GoodsItem.class);
		List<ItemCell> itemList = new ArrayList<ItemCell>();
		if (gMap != null && gMap.size() > 0) {
			for (ItemCell item : gMap.values()) {
				itemList.add(item);
			}

		}
		int count = 0;
		for(ItemCell item :itemList){
			count+=item.getNum();
		}
		return count;
	}

	/**
	 * 获取玩家所有的装备
	 */
	public List<ItemCell> getEquip() {
		Map<String, ItemCell> map = cells.get(EquipItem.class);
		List<ItemCell> result = new ArrayList<>();
		if (map !=null && map.size() > 0) {
			for (ItemCell item : map.values()) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * 获取玩家指定状态的装备
	 * 
	 * @return
	 */
	public List<EquipItem> getEquipByEquipState(byte equipState) {
		List<EquipItem> itemLst = new ArrayList<EquipItem>();
		Map<String, ItemCell> map = cells.get(EquipItem.class);
		if (map == null) {
			return null;
		}
		for (ItemCell tempItemCell : map.values()) {
			EquipItem tempEquipItem = (EquipItem) tempItemCell;
			if (tempEquipItem.getEquipState() == equipState) {
				itemLst.add(tempEquipItem);
			}
		}
		if (itemLst.size() > 0)
			return itemLst;
		else
			return null;
	}
	
	/*
	 * 获取穿在身上的装备数量
	 */
	public int getEquipedNum() 
	{
		Map<String, ItemCell> map = cells.get(EquipItem.class);
		if (map == null) {
			return 0;
		}
		
		int num = 0;
		for (ItemCell tempItemCell : map.values()) {
			EquipItem tempEquipItem = (EquipItem) tempItemCell;
			if (tempEquipItem.getEquipState() == 1) {
				num++;
			}
		}
		return num;
	}
	
	/**
	 * 根据获取玩家身上某品质装备的数量
	 */
	public int getEquipsNumByQuality(int type) {
		int num = 0;
		Map<String, ItemCell> map = cells.get(EquipItem.class);
		if (map == null) {
			return num;
		}
		for (ItemCell tempItemCell : map.values()) {
			EquipItem tempEquipItem = (EquipItem) tempItemCell;
			Equip equip = dataManager.serach(Equip.class, tempEquipItem.getKey());
			if (tempEquipItem.getEquipState() == 1 && equip.getEquipQuality() >= type) {
				num += 1;
			}
		}
		return num;
	}

	/**
	 * 获取指定item数量
	 * 
	 * @param itemId
	 * @return
	 */
	public long getItemNumFromBag(String key) {
		for (Map<String, ItemCell> map : cells.values()) {
			ItemCell item = map.get(key);
			if (item != null) {
				return item.getNum();
			}
		}
		return 0;
	}

	/**
	 * 根据类型获取玩家背包数据
	 */
	public Map<String, List<ItemCell>> getRoleBagByItemType(byte itemType) {
		return null;
	}

	/**
	 * @param 加载时加载全部的bag
	 * @param rms
	 */
	public void sendBagToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_BAG;
			}
		};
		List<ItemCell> itemList = getRoleBag();
		module.add(itemList.size());// int 物品个数
		for (int i = 0 ; i < itemList.size() ; i++){
			ItemCell item = itemList.get(i);
			item.sendClient(module.getParams());
		}
		rms.addModule(module);
	}

	/**
	 * @param 改变后需要发送的模块
	 * @param rms
	 * @param items
	 */
	public void sendItemsToClient(RespModuleSet rms, ItemCell... items) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ITEM_CHANGE;
			}
		};
		module.add(items.length);//int 物品种类数量
		for (int i = 0 ; i < items.length ; i++){
			ItemCell item = items[i];
			item.sendClient(module.getParams());
		}
		rms.addModule(module);
	}
	
	public void sendItemsToClient(RespModuleSet rms, List<ItemCell> items) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ITEM_CHANGE;
			}
		};
		module.add(items.size());//int 物品种类数量
		for (int i = 0 ; i < items.size() ; i++){
			ItemCell item = items.get(i);
			item.sendClient(module.getParams());
		}
		rms.addModule(module);
	}
	
	/**
	 * @param 发送用于显示的模块
	 * @param rms
	 * @param itemIds
	 */
	public void sendShowItemsToClient(RespModuleSet rms, ItemCell... itemIds) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_SHOW_ITEMS;
			}
		};
		module.add(itemIds.length); // int 物品种类数量
		for (int i = 0 ; i < itemIds.length; i++){
			ItemCell item = itemIds[i];
			item.sendClient(module.getParams());
		}
		rms.addModule(module);
	}
	
	/**
	 * 改变装备的状态
	 * @param equipItem
	 * @param equipState
	 * @return
	 */
	private boolean changeEquipState(EquipItem equipItem, byte equipState) {
		if (equipItem != null && equipItem.getEquipState() != 2) {
			equipItem.setEquipState(equipState);
			return true;
		}
		return false;
	}

	/**
	 * @param 根据装备Id获取同类装备
	 *            是否有没有在身上的
	 * @param equipType
	 * @return
	 */
	private EquipItem getRoleEquipByEquipType(EquipItem equipItem) {
		Equip equip = dataManager.serach(Equip.class, equipItem.getKey());
		Equip roleEquip = new Equip();
		if (equip == null) {
			GameLog.error("equip base data is null where keyId=" + equipItem.getKey());
			return null;
		}
		Map<String, ItemCell> equipMap = cells.get(EquipItem.class);
		EquipItem tempEquipItem = new EquipItem();
		for (ItemCell tempItemCell : equipMap.values()) {
			tempEquipItem = (EquipItem) tempItemCell;
			roleEquip = dataManager.serach(Equip.class, tempEquipItem.getKey());
			if (roleEquip == null) {
				GameLog.error("roleEquip base data is null where keyId=" + equipItem.getKey());
				return null;
			}
			if (roleEquip.getEquipType() == equip.getEquipType() && tempEquipItem.getEquipState() == 1
					&& tempEquipItem.getId() != equipItem.getId()) {
				return tempEquipItem;
			}
		}
		return null;
	}

	/**
	 * @param 装上装备
	 * @param equipId
	 *            正在操作的装备Id
	 * @param roleEquipId
	 *            角色身上的装备Id
	 */
	public boolean equipWield(Role role, long equipId) {
		RespModuleSet rms = new RespModuleSet();
		EquipItem equipItem = getEquipById(equipId);
		if (equipItem == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED, equipId);
			return false;
		}
		Equip equip = dataManager.serach(Equip.class, equipItem.getKey());
		if (equip == null) {
			GameLog.error("read base equip is fail");
			return false;
		}
		if (role.getLevel() < equip.getUseLimitation()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_LEVEL_INSUFFICIENT, equipId);
			return false;
		}
		EquipItem roleEquipItem = getRoleEquipByEquipType(equipItem);
		if (roleEquipItem == null) { // 身上未穿戴装备的情况
			if (!changeEquipState(equipItem, (byte) 1)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_WIELD_OR_UNWIELD_FAIL,equipId);
				return false;
			}
			sendItemsToClient(rms, equipItem);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			equipItem.addEquipBuffList(role);
			// 任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_WIELD);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.PUTON_EQUIP,getEquipedNum());
			LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "穿上装备");
			try {
				NewLogManager.baseEventLog(role, "puton_equip",equip.getId());
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			return true;
		} else { // 身上穿有装备的情况
			if (!changeEquipState(equipItem, (byte) 1)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_WIELD_OR_UNWIELD_FAIL,equipId);
				return false;
			}
			if (!changeEquipState(roleEquipItem,(byte) 0)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_WIELD_OR_UNWIELD_FAIL,equipId);
				return false;
			}
			// remove 之前的装备buff
			role.getEffectAgent().removeEquipBuff(role,roleEquipItem.getId());
			// add 当前装备的buff
			equipItem.addEquipBuffList(role);
			sendItemsToClient(rms, equipItem, roleEquipItem);
			MessageSendUtil.sendModule(rms, role.getUserInfo());

			// 任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_WIELD, equipItem.getKey());
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.PUTON_EQUIP,getEquipedNum());
			LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "穿上装备");
			try {
				NewLogManager.baseEventLog(role, "puton_equip",equip.getId());
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			return true;
		}
	}

	/**
	 * @param 卸下装备
	 * 
	 * @param equipId
	 */
	public boolean equipUnwield(Role role, long equipId) {
		EquipItem equipItem = getEquipById(equipId);
		if (equipItem == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED,equipId);
			return false;
		}
		if (!changeEquipState(equipItem,(byte) 0)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED,equipId);
			return false;
		}
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_WIELD, equipItem.getKey());
		role.getEffectAgent().removeEquipBuff(role,equipId);
		RespModuleSet rms = new RespModuleSet();
		sendItemsToClient(rms, getEquipById(equipId));
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		Equip equip = dataManager.serach(Equip.class, equipItem.getKey());
		LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "卸下装备");
		try {
			NewLogManager.baseEventLog(role, "putoff_equip", equip.getId());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * @param 装备炼化
	 * @param equipId
	 * @param costMaterialLst
	 * @return
	 */
	public boolean equipRefine(Role role, long equipId) {
		EquipItem equipItem = getEquipById(equipId);
		RoleCityAgent agent = role.getCity(0);
		RoleBagAgent bagAgent = role.getBagAgent();
		RespModuleSet rms = new RespModuleSet();
		if (equipItem == null || equipItem.getState() != 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED, equipId);
			return false;
		}
		String keyId = equipItem.getKey();
		// 检查装备炼化条件
		Equip equip = dataManager.serach(Equip.class, equipItem.getKey());
		List<String> costMaterialLst = equip.getRefineMaterial();// 花费的材料
		// 检测金币
		List<String> costItemList = equip.getRefineCost();
		for (int i = 0 ; i < costItemList.size(); i++){
			String cost = costItemList.get(i);
			String[] costArray = cost.split(":");
			if (costArray.length < 2) {
				GameLog.error("cannot find upgrade equip costs where equipId =" + equipItem.getKey());
				return false;
			}
			String resourceId = costArray[0];
			int num = Integer.parseInt(costArray[1]);
			//buff 
			float resourceBuff = 0.0F;
			List<RoleBuild> hBuilds = agent.searchBuildByBuildId(BuildName.EQUIP_LAB.getKey());
			if (hBuilds.size() > 0) {
				Buildinglevel hb = hBuilds.get(0).getBuildingLevel();
				if (hb != null) {
					resourceBuff = Float.valueOf(hb.getParamList().get(1));
				}
			}
			num = (int) (num * (1.0f - resourceBuff));
			if (!resourceId.equals("silver")) {
				GameLog.error("equip base data gem is error");
				return false;
			}
			if (num > role.getSilver()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_SILVER, num);
				return false;
			}
		}
		for (int index = 0; index < costMaterialLst.size(); index++) {
			String[] costMaterialArray = costMaterialLst.get(index).split(":");
			if (costMaterialArray.length < 2) {
				GameLog.error("cannot find costMaterialArray where equipId =" + equipItem.getKey());
				return false;
			}
			String itemId = costMaterialArray[0];
			int num = Integer.parseInt(costMaterialArray[1]);
			Item material = dataManager.serach(Item.class, itemId);
			if (material == null) {
				GameLog.error("cannot find material from item base data!");
				return false;
			}
			if (!bagAgent.checkItemFromBag(itemId, num)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT,
						material.getId());
				return false;
			}
		}
		// 扣除金币
		for (int i = 0 ; i < costItemList.size() ; i++){
			String cost = costItemList.get(i);
			String[] costArray = cost.split(":");
			if (costArray.length < 2) {
				GameLog.error("cannot find upgrade equip costs where equipId =" + equipItem.getKey());
				return false;
			}
			String resourceId = costArray[0];
			int num = Integer.parseInt(costArray[1]);
			// buff
			float resourceBuff = 0.0F;
			List<RoleBuild> hBuilds = agent.searchBuildByBuildId(BuildName.EQUIP_LAB.getKey());
			if (hBuilds.size() > 0) {
				Buildinglevel hb = hBuilds.get(0).getBuildingLevel();
				if (hb != null) {
					resourceBuff = Float.valueOf(hb.getParamList().get(1));
				}
			}
			num = (int) (num * (1.0f - resourceBuff));
			if (!resourceId.equals("silver")) {
				GameLog.error("equip base data gem is error");
				return false;
			}
			if (role.redRoleSilver(num) == false) {
				GameLog.error("gem is insufficient!");
				return false;
			}
			LogManager.itemConsumeLog(role, num, "equipRefine", resourceId);
		}
		// 删除消耗的物品Material
		ItemCell[] materials = new ItemCell[costMaterialLst.size()];
		for (int i = 0; i < costMaterialLst.size(); ++i) {
			String[] costArray = costMaterialLst.get(i).split(":");
			String itemId = costArray[0];
			int num = Integer.parseInt(costArray[1]);
			ItemCell cell = bagAgent.getItemFromBag(itemId);
			bagAgent.removeItems(itemId, num);
			LogManager.itemConsumeLog(role, num, "equipRefine", itemId);
			ItemCell tempCell = bagAgent.getItemFromBag(itemId);
			if (tempCell == null) {
				tempCell = cell;
				tempCell.setNum(0);
			}
			materials[i] = tempCell;
		}
		List<String> randomBuffIdList = equipItem.randomBuffIdList(keyId);
		if (randomBuffIdList == null) {
			GameLog.error("randomBuffIdList is null!");
			return false;
		}
		equipItem.setForgeAfterBuffIdLists(randomBuffIdList);
		role.sendRoleToClient(rms);
		sendItemsToClient(rms, equipItem);
		bagAgent.sendItemsToClient(rms, materials);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_REFIN);
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.RECAST);
		LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "重铸装备");
		
		try {
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<costMaterialLst.size();i++){
				String cost = costMaterialLst.get(i);
				String[] params = cost.split(":");
				for(int j=0;j<params.length;j++){
					sb.append(params[j]);
					sb.append(GameLog.SPLIT_CHAR);
				}	
			}
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			NewLogManager.buildLog(role, "equip_reset",equip.getId(),newStr);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * @param 装备buff的替换
	 * @param role
	 * @param equipId
	 * @return
	 */
	public boolean equipBuffReplace(Role role, long equipId) {
		EquipItem equipItem = getEquipById(equipId);
		RespModuleSet rms = new RespModuleSet();
		if (equipItem == null || equipItem.getState() != 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED, equipId);
			return false;
		}
		List<String> equipForgeAfterList = equipItem.getForgeAfterBuffIdLists();
		if (equipForgeAfterList == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_REFINE_BUFF_NULL, equipId);
			return false;
		}
		equipItem.setEquipBuffIdLists(equipForgeAfterList);
		equipItem.setForgeAfterBuffIdLists(null);
		sendItemsToClient(rms, equipItem);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	/**
	 * @param 装备分解
	 * @param equipId
	 * @return
	 */
	public boolean equipDecompose(Role role, long equipId) {
		EquipItem equipItem = getEquipById(equipId);
		RoleBagAgent bagAgent = role.getBagAgent();
		RespModuleSet rms = new RespModuleSet();
		if (equipItem == null || equipItem.getState() != 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED, equipId);
			return false;
		}
		String keyId = equipItem.getKey();
		List<String> fuseMateriaList = equipItem.randomMaterialList(keyId);
		if (fuseMateriaList == null) {
			GameLog.error("fuseMaterial base data is null where keyid=" + keyId);
			return false;
		}
		equipItem.setNum(0);
		bagAgent.sendItemsToClient(rms, equipItem);
		if (!bagAgent.removeEquip(String.valueOf(equipId))) { // 删除分解的装备
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED, equipId);
			return false;
		}
		ItemCell[] changeOthers = new OtherItem[fuseMateriaList.size()];
		int k = 0;
		for (int i = 0; i < fuseMateriaList.size(); i++) { // 打包分解得到的材料并下发
			bagAgent.addOther(fuseMateriaList.get(i), 1);
			String event = "equipDecompose";
			String itemst =fuseMateriaList.get(i);
			LogManager.itemOutputLog(role, 1, event, itemst);
			OtherItem changeOtherItem = new OtherItem();
			changeOtherItem.init(uid, fuseMateriaList.get(i), 1);
			int j = 0;
			while (j < k && !changeOthers[j].getKey().equals(changeOtherItem.getKey()))
				j++;
			if (j < k && changeOthers[j].getKey().equals(changeOtherItem.getKey())) {
				changeOthers[j].setNum(changeOthers[j].getNum() + 1);
			} else {
				changeOthers[k++] = changeOtherItem;
			}
		}
		ItemCell[] itemIds = new ItemCell[k];
		ItemCell[] others = new ItemCell[k];
		for (int i = 0; i < k; i++) {
			itemIds[i] = changeOthers[i];
			ItemCell otherItem = bagAgent.getItemFromBag(changeOthers[i].getKey());
			others[i] = otherItem;
		}
		bagAgent.sendShowItemsToClient(rms, itemIds);
		bagAgent.sendItemsToClient(rms, others);
		// role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_RESOLV);
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.DECOMPOSE,keyId);
		
		try {
			Equip equip = dataManager.serach(Equip.class,equipItem.getKey());
			LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "装备分解");
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<fuseMateriaList.size();i++){
				sb.append(fuseMateriaList.get(i));
				sb.append(GameLog.SPLIT_CHAR);	
				sb.append(1);
				sb.append(GameLog.SPLIT_CHAR);	
			}
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			NewLogManager.buildLog(role, "equip_decompose",equip.getId(),newStr);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		
		return true;
	}

	/**
	 * 材料的合成
	 * 
	 * @param role
	 * @param materialId
	 * @return
	 */
	public boolean materialSynthesis(Role role, String materialId) {
		final int SYNTHESIS_MATERIAL_COUNT = 4;

		RespModuleSet rms = new RespModuleSet();
		RoleBagAgent bagAgent = role.getBagAgent();
		// OtherItem otherItem = (OtherItem)
		// bagAgent.getItemFromBag(materialId);
		Item materials = dataManager.serach(Item.class, materialId);
		if (materials == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_UNUSED, materialId);
			return false;
		}
		OtherItem otherItem = (OtherItem) getItemFromBag(materialId);

		String upgradeMaterialID = materials.getUpgradeMaterialID();
		// 检测材料合成需要的材料及其个数
		if (!bagAgent.checkItemFromBag(materialId, SYNTHESIS_MATERIAL_COUNT - 1)) { // 材料大于4个
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT, materialId);
			return false;
		}
		// 删除材料合成所需要的材料
		if (!removeItems(materialId, SYNTHESIS_MATERIAL_COUNT)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT, materialId);
			return false;
		}
		LogManager.itemConsumeLog(role, SYNTHESIS_MATERIAL_COUNT, "materialSynthesis", materialId);
		if (upgradeMaterialID.equals("0")) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_UNUPGRADE, materialId);
			return false;
		}
		OtherItem tempOtherItem = (OtherItem) getItemFromBag(materialId);
		if (tempOtherItem == null) {
			tempOtherItem = otherItem;
			tempOtherItem.setNum(0);
		}
		OtherItem upgradeOtherItem = (OtherItem) addOther(upgradeMaterialID, 1).get(0);
		String event = "materialSynthesis";
		String itemst =upgradeMaterialID;
		LogManager.itemOutputLog(role, 1, event, itemst);
		if (upgradeOtherItem == null) {
			GameLog.error("材料合成失败!" + materialId + "-->" + upgradeMaterialID);
			return false;
		}
		bagAgent.sendItemsToClient(rms, otherItem, upgradeOtherItem);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_MTL_SYNTH, 0);
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.COMPOSE_MATERIAL, 1);
		try {
			NewLogManager.buildLog(role, "material_mergenumber");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	private boolean checkItemLimitation(Role role, List<String> limitations) {
		for (int i = 0 ; i < limitations.size() ; i++){
			String limitation = limitations.get(i);
			String[] params = limitation.split(":");
			if (params.length < 2) {
				GameLog.error("item limitations not right.");
				return false;
			}
			if (params[0].equals("RoleLevel")) {
				int level = Integer.parseInt(params[1]);
				if (role.getLevel() >= level) {
					return true;
				}
			}else if(params[0].equals(BuildName.CITY_CENTER.getKey())){
				int level = Integer.parseInt(params[1]);
				if(role.getCity(0).getCityCenterLevel() >= level){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 使用道具
	 * type =0 使用道具  不使用金币
	 */
	public boolean useItem(Role role, final String itemId, long num, byte type, long buildId) {
		try {
			NewLogManager.baseEventLog(role, "use_item",itemId,num);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		Item itemdata = dataManager.serach(Item.class, itemId);
		if (itemdata == null) {
			GameLog.error("item: " + itemId + "static data not found.");
			return false;
		}
		// 使用条件检查
		if (!checkItemLimitation(role, itemdata.getUseLimitation())) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ITEM_USE_LMT, itemId, num);
			return false;
		}
		Shop shopData = dataManager.serach(Shop.class, new SearchFilter<Shop>(){
			@Override
			public boolean filter(Shop data) {
				return data.getItemid().equals(itemId);
			}
		});
		if (type == 0) {
			if (!checkItemFromBag(itemId, num)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, num);
				return false;
			}
		} else {
			if(shopData == null){
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, num);
				return false;
			}
			if (role.getMoney() < shopData.getNormalPrice()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,
						shopData.getNormalPrice());
				return false;
			}
		}
		// 使用道具后的效果
		String buffId = itemdata.getBuffList();
		Buff buffData = dataManager.serach(Buff.class, buffId);
		TargetType active = null;
		if(buffData != null){
			String[] paramLst = buffData.getBuffTarget().split(":");
			active = TargetType.search(paramLst[1]);
		}
		String strEffect = itemdata.getEffectAfterUse();
		List<ItemCell> items = new ArrayList<ItemCell>();
		RespModuleSet rms = new RespModuleSet();
		switch (itemdata.getItemType()) {
		case ItemType.TYPE_CITY_RESOURCES:// 资源道具
		{
			if (active == null){
				GameLog.error("use item itemId = " + itemId + " fail.");
				return false;
			}
			String event ="useItem";
			switch (active) {
			case G_C_ADD_A: {//添加合金资源，固定数量
				long effectValue = Long.parseLong(strEffect) * num;
				role.addResourcesToCity(0, ResourceTypeConst.RESOURCE_TYPE_ALLOY, effectValue);
				String item = ResourceTypeConst.RESOURCE_TYPE_ALLOY.getKey();
				LogManager.itemOutputLog(role, effectValue, event, item);
				break;
			}
			case G_C_ADD_O: {//添加石油资源，固定数量
				long effectValue = Long.parseLong(strEffect) * num;
				role.addResourcesToCity(0, ResourceTypeConst.RESOURCE_TYPE_OIL, effectValue);
				String item = ResourceTypeConst.RESOURCE_TYPE_OIL.getKey();
				LogManager.itemOutputLog(role, effectValue, event, item);
				break;
			}
			case G_C_ADD_M: {//添加金属资源，固定数量
				long effectValue = Long.parseLong(strEffect) * num;
				role.addResourcesToCity(0, ResourceTypeConst.RESOURCE_TYPE_METAL, effectValue);
				String item = ResourceTypeConst.RESOURCE_TYPE_METAL.getKey();
				LogManager.itemOutputLog(role, effectValue, event, item);
				break;
			}
			case G_C_ADD_F: {//添加食品资源，固定数量
				long effectValue = Long.parseLong(strEffect) * num;
				role.addResourcesToCity(0, ResourceTypeConst.RESOURCE_TYPE_FOOD, effectValue);
				String item = ResourceTypeConst.RESOURCE_TYPE_FOOD.getKey();
				LogManager.itemOutputLog(role, effectValue, event, item);
				break;
			}
			default:
				GameLog.error("item: " + itemId + "static data not found.");
				break;
			}
			
			try {
				if(type==0){
					NewLogManager.baseEventLog(role, "use_res_goods",itemId);
					NewLogManager.buildLog(role, "quick_add_resource",itemId,num);
				}else{
					NewLogManager.baseEventLog(role, "use_res_goods",shopData.getSaleSprice());
					NewLogManager.buildLog(role, "quick_add_resource",shopData.getNormalPrice());
				}
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			break;
		}
		case ItemType.TYPE_SPEED_ITEM:// 加速道具
		{
			if (active == null){
				return false;
			}
			RoleBuild build = null;
			if (active != TargetType.G_C_RED_AMT && active != TargetType.G_C_RED_AMTH) {//行军加速，固定时间 && //高级行军加速，加速集结部队
				build = role.getCity(0).searchBuildById(buildId);
				if (build == null) {
					GameLog.error("role.getCity(0).searchBuildById is null where buildId=" + buildId);
					return false;
				}
			}
			boolean bSuc = false;
			switch (active) {
			case G_C_REDU_T://加速，固定时间
				bSuc = build.redBuildTimer(role,Long.parseLong(strEffect) * num, null);		
				try {
					TimerLast timer = build.getBuildTimer();
					switch (timer.getType()) {
					case TIME_CREATE:
						NewLogManager.buildLog(role, "build_accelerate",buildId,itemId,num);
						break;
					case TIME_LEVEL_UP:
						NewLogManager.buildLog(role, "upgrade_accelerate",buildId,itemId,num);
						break;
					case TIME_TRAIN:
						NewLogManager.buildLog(role, "train_accelerate",buildId,itemId,num);
						break;
					case TIME_RESEARCH:
						NewLogManager.buildLog(role, "study_accelerate",buildId,itemId,num);
						break;
					case TIME_CURE:
						NewLogManager.buildLog(role, "cure_accelerate",buildId,itemId,num);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}				
				break;
			case G_C_REDU_BT:// 建筑建造或升级加速
				List<TimerLast> timers = build.getTimers();
				TimerLast time = timers.get(0);
				if (time.getType() == TimerLastType.TIME_CREATE) {
					bSuc = build.redBuildTimer(role, Long.parseLong(strEffect) * num, TimerLastType.TIME_CREATE);
					NewLogManager.buildLog(role, "upgrade_accelerate", buildId, itemId, num);
				} else if (time.getType() == TimerLastType.TIME_LEVEL_UP) {
					bSuc = build.redBuildTimer(role, Long.parseLong(strEffect) * num, TimerLastType.TIME_LEVEL_UP);
					NewLogManager.buildLog(role, "build_accelerate", buildId, itemId, num);
				} else if (time.getType() == TimerLastType.TIME_REMOVE) {
					bSuc = build.redBuildTimer(role, Long.parseLong(strEffect) * num, TimerLastType.TIME_REMOVE);
				}else {
					GameLog.info("建筑加速道具使用出错");
				}				
				break;
			case G_C_RED_RT:// 研究科技加速
				bSuc = build.redBuildTimer(role,Long.parseLong(strEffect) * num, TimerLastType.TIME_RESEARCH);
				break;
			case G_C_RED_ST:// 士兵生产加速，固定时间
				bSuc = build.redBuildTimer(role,Long.parseLong(strEffect) * num, TimerLastType.TIME_TRAIN);
				break;
			case G_C_RED_CT:// 治疗加速，固定时间
				bSuc = build.redBuildTimer(role,Long.parseLong(strEffect) * num, TimerLastType.TIME_CURE);
				break;
			case G_C_RED_AMT:// 行军加速，固定时间
			case G_C_RED_AMTH://高级行军加速
				float seedRate = Float.parseFloat(strEffect);
				ExpediteTroops select = world.getObject(ExpediteTroops.class, buildId);
				if (select == null || select.isRemoving()) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_EXPEDITE_OVERED);
					return false;
				}
				if (select.getLeader().getInfo().getUid() != uid) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_EXPEDITE_NOT_LEADER);
					return false;
				}
				bSuc = true;
				select.trySpeedUp(seedRate);
				role.handleEvent(GameEvent.TROOPS_SEND);
				break;
			case C_RED_FORG_TIME:// 锻造加速,减少装备升级时间
				bSuc = build.redBuildTimer(role,Long.parseLong(strEffect) * num, TimerLastType.TIME_UP_EQUIP);
				break;
			default:
				GameLog.error("item: " + itemId + "static data error.");
				break;
			}
			if (!bSuc) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, num);
				return false;
			}
			if (build != null) {
				build.sendToClient(rms);
			}
			break;
		}
		case ItemType.TYPE_BUFF_ITEM:// buff道具,城池增益
		{
			if (active == null){
				return false;
			}
			RoleCityAgent cityAgent = role.getCity(0);
			MapCity mapCity = mapWorld.searchMapCity(uid, 0);
			switch (active) {
			case C_ADD_BUILD_QUEUE:// 增加队列时间
			{
				RoleCityAgent agent = role.getCity(0);
				agent.addCityNewQueue(itemdata.getHoldTime(),0);	
				try {
					if(type==0){
						NewLogManager.baseEventLog(role, "buy_worker",itemId);
						NewLogManager.baseEventLog(role, "active_base_gain","QUEUE_ADD",itemId);
					}else{
						NewLogManager.baseEventLog(role, "buy_worker",shopData.getSaleSprice());
						NewLogManager.baseEventLog(role, "active_base_gain","QUEUE_ADD",shopData.getSaleSprice());
					}
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
				break;
			}
			case G_C_NO_WAR: // 战争守护
				if(mapCity.getCityState().searchTimer(TimerLastType.TIME_CITY_NOWAR) == null){
					mapCity.getCityState().addTimer(itemdata.getHoldTime(), TimerLastType.TIME_CITY_NOWAR,0);
					mapCity.getCityState().setNowar(true);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","WAR_PROTECT",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","WAR_PROTECT",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case G_C_NO_SPY:// 反侦察
				if(mapCity.getCityState().searchTimer(TimerLastType.TIME_CITY_NOSPY) == null){
					mapCity.getCityState().addTimer(itemdata.getHoldTime(), TimerLastType.TIME_CITY_NOSPY,0);
					mapCity.getCityState().setNospy(true);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","ATISPY",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","ATISPY",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case G_C_DB_SPY:// 侦查双倍
				if(mapCity.getCityState().searchTimer(TimerLastType.TIME_CITY_DBSPY) == null){
					mapCity.getCityState().addTimer(itemdata.getHoldTime(), TimerLastType.TIME_CITY_DBSPY,0);
					mapCity.getCityState().setDbspy(true);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","CAMOUFLAGE",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","CAMOUFLAGE",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case T_B_ADD_SL:// 出征人数上限
				if(role.getEffectAgent().searchItemTimer(TimerLastType.TIME_ITEM_TROOPS_LIMIT) == null){
					role.getEffectAgent().addItemBuff(role,buffId, strEffect, itemId, buildId, itemdata.getHoldTime(),TimerLastType.TIME_ITEM_TROOPS_LIMIT);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","TROOPS_LIMIT",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","TROOPS_LIMIT",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case T_A_IMP_SA:// 部队攻击加成
				if(role.getEffectAgent().searchItemTimer(TimerLastType.TIME_ITEM_IMP_ATK) == null){
					role.getEffectAgent().addItemBuff(role,buffId, strEffect, itemId, buildId, itemdata.getHoldTime(),TimerLastType.TIME_ITEM_IMP_ATK);
					cityAgent.sendCityStateToClient(role, rms);
					if(type==0){
						NewLogManager.baseEventLog(role, "active_base_gain","ATK_ADD",itemId);
					}else{
						NewLogManager.baseEventLog(role, "active_base_gain","ATK_ADD",shopData.getSaleSprice());
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case T_A_IMP_SD:// 部队防御加成
				if(role.getEffectAgent().searchItemTimer(TimerLastType.TIME_ITEM_IMP_DEF) == null){
					role.getEffectAgent().addItemBuff(role,buffId, strEffect, itemId, buildId, itemdata.getHoldTime(),TimerLastType.TIME_ITEM_IMP_DEF);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","DEF_ADD",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","DEF_ADD",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case T_A_RED_SC:// 粮食消耗减少
				if(role.getEffectAgent().searchItemTimer(TimerLastType.TIME_ITEM_RED_FOOD) == null){
					role.getEffectAgent().addItemBuff(role,buffId, strEffect, itemId, buildId, itemdata.getHoldTime(),TimerLastType.TIME_ITEM_RED_FOOD);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","FOOD_CONSUME_RED",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","FOOD_CONSUME_RED",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case T_A_ADD_IC:// 采集加成
				if(role.getEffectAgent().searchItemTimer(TimerLastType.TIME_ITEM_IMP_COLL) == null){
					role.getEffectAgent().addItemBuff(role,buffId, strEffect, itemId, buildId, itemdata.getHoldTime(),TimerLastType.TIME_ITEM_IMP_COLL);
					role.handleEvent(GameEvent.ROLE_RES_BUFF_CHANGE);
					role.handleEvent(GameEvent.TROOPS_SEND);
					cityAgent.sendCityStateToClient(role, rms);
					try {
						if(type==0){
							NewLogManager.baseEventLog(role, "active_base_gain","RESOURCE_COLLECTION",itemId);
						}else{
							NewLogManager.baseEventLog(role, "active_base_gain","RESOURCE_COLLECTION",shopData.getSaleSprice());
						}
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
					return false;
				}
				break;
			case T_B_IMP_FP:// 食品增产
			case T_B_IMP_MP:// 金属增产
			case T_B_IMP_AP:// 钛合金增产
			case T_B_IMP_OP:// 石油增产
				RoleBuild buildp = role.getCity(0).searchBuildById(buildId);
				if (buildp == null) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ITEM_USE_LMT);
					return false;
				}
				BuildComponentProduction com = buildp.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
				if (com != null) {
					com.collectResource(role,buildp);//自动收取资源
					boolean isRate = true;
					Buff buff = dataManager.serach(Buff.class, buffId);
					if (buff != null) {
						isRate = buff.getBuffdatatype() == 0 ? true : false;
					}
					long now = TimeUtils.nowLong() / 1000;
					if (!com.addSpecialItemBuff(itemId, now, itemdata.getHoldTime(), strEffect, isRate)) {
						MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_USE_REPITATION);
						return false;
					}
					buildp.sendToClient(rms);
				}
				try {
					if(type==0){
						NewLogManager.buildLog(role, "increase_product_by_item",itemId,num);
					}else{
						NewLogManager.buildLog(role, "increase_product_by_gold",shopData.getNormalPrice());
					}if(type==0){
						NewLogManager.buildLog(role, "increase_product_by_item",itemId,num);
					}else{
						NewLogManager.buildLog(role, "increase_product_by_gold",shopData.getNormalPrice());
					}
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
				
				break;
			default:
				GameLog.error("item: " + itemId + "static data error.");
				break;
			}
			break;
		}
		case ItemType.ITEM_CHEST_ITEM:// 宝箱 TODO 根据掉落列表
		{
			List<ItemCell> itemLst = new ArrayList<ItemCell>();
			for(int i = 0; i < num; i++){
				List<ItemCell> cellLst = new ArrayList<ItemCell>();
				itemLst.addAll(getRandomItems(strEffect, cellLst,role));
				if(itemLst == null || itemLst.size() == 0){
					return false;
				}
				items.addAll(cellLst);
			}
			ItemCell[] itemAs = itemLst.toArray(new ItemCell[itemLst.size()]);
			sendShowItemsToClient(rms, itemAs);
			break;
		}
		case ItemType.TYPE_MOVE_CITY_ITEM:// 迁城令
			break;
		case ItemType.TYPE_VIP_TIME_ITEM:// vip时间
		{
			VipInfo info = role.getVipInfo();
			if (info == null) {
				return false;
			}
			info.ActiveVip(role,itemdata.getHoldTime()*num);
			role.sendRoleToClient(rms);
			try {
				if(type==0){
					NewLogManager.baseEventLog(role, "active_vip",role.getVipInfo().getVipLevel(),itemId);
				}else{
					NewLogManager.baseEventLog(role, "active_vip",role.getVipInfo().getVipLevel(),shopData.getSaleSprice());
				}
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			break;
		}
		case ItemType.TYPE_VIP_EXP_ITEM:// vip经验
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.getVipInfo().addExp(role,value);
			role.getVipInfo().sendVipToClient(rms);
			role.sendRoleToClient(rms);
			try {
				NewLogManager.baseEventLog(role, "add_vip_point",role.getVipInfo().getVipLevel(),itemId);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			break;
		}
		case ItemType.TYPE_FUNCTION_ITEM:// 功能型道具
		{
			if(active == null){
				return false;
			}
			switch (active) {
			case G_C_REP_FENCE:
			{
				List<RoleBuild> builds = role.getCity(0).searchBuildByBuildId(BuildName.FENCE.getKey());
				if(builds == null || builds.size() == 0){
					GameLog.error("城墙建筑找不到了。。。");
					return false;
				}
				RoleBuild build = builds.get(0);
				BuildComponentWall com = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
				if (com == null){
					GameLog.error("城墙组件找不到了。。。");
					return false;
				}
				if (com.getState() == 0){
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_CITY_WALL_FULL);
					return false;
				}else if(com.getState() == 1){
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_CITY_WALL_REPAIRING);
					return false;
				}
				com.reinforceWall(role,build);
				build.sendToClient(rms);
				break;
			}
			case C_RECALL_TRP_LOW:// 普通行军召回
			case C_RECALL_TRP_HIGH:// 高级行军召回
			{
				ExpediteTroops select = world.getObject(ExpediteTroops.class, buildId);
				if (select == null || select.isRemoving()) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_EXPEDITE_OVERED);
					return false;
				}
				if (select.getTimer().getType() == TimerLastType.TIME_ARMY_BACK || select.getTimer().getType() == TimerLastType.TIME_ARMY_BACK_FORTRESS) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EXPEDITE_IS_BACK);
					return false;
				}
				if (select.isMass()) {
					if (select.getLeader().getInfo().getUid() != role.getId()){
						MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EXPEDITE_CALL_BACK_NEED_LEADER);
						return false;
					}
					if (active != TargetType.C_RECALL_TRP_HIGH){
						MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EXPEDITE_CALL_BACK_NEED_HIGH);
						return false;
					}
				}
				if (select.isFighting()){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_EXPEDITE_FIGHTING);
					return false;
				}
				select.setCallBack(true);
				break;
			}
			case G_R_COPY: { // 重置副本当前挑战
				RoleRelic roleRelic = role.getRoleCopys().get((int)buildId);
				if (roleRelic == null) {
					GameLog.error("search roleRelic is fail where type = " + buildId);
					return false;
				}
				if (roleRelic.getCurrentItemResetNum() < 1) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BEYOND_ITEM_RESET_MAX);
					return false;
				}
				if(roleRelic.isFinish() && roleRelic.getIsGotReward() == 0){
					roleRelic.getFinishReward(role);
				}
				roleRelic.resetScenes();
				roleRelic.setCurrentItemResetNum(roleRelic.getCurrentItemResetNum() - 1);
				role.sendRoleCopysToClient(rms, true);
				break;
			}
			default:
				break;
			}
		
			break;
		}
		case ItemType.TYPE_GOLD: {
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.addRoleMoney(value);
			String event= "useItem";
			role.sendRoleToClient(rms);
			LogManager.goldOutputLog(role, value, event);
			break;
		}
		case ItemType.TYPE_GEM:
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.addRoleGem(value);
			role.sendRoleToClient(rms);
			String event ="useItem";
			String item = "gem";
			LogManager.itemOutputLog(role, value, event, item);
			try {
				if(type==0){
					NewLogManager.baseEventLog(role, "add_gold_chips",item);
				}else{
					NewLogManager.baseEventLog(role, "add_gold_chips",shopData.getSaleSprice());
				}
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			break;
		}
		case ItemType.TYPE_COPPER:
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.addRoleCopper(value);
			role.sendRoleToClient(rms);
			String event ="useItem";
			String item = "copper";
			LogManager.itemOutputLog(role, value, event, item);
			try {
				if(type==0){
					NewLogManager.baseEventLog(role, "add_silver_chips",item);
				}else{
					NewLogManager.baseEventLog(role, "add_silver_chips",shopData.getSaleSprice());
				}
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			break;
		}
		case ItemType.TYPE_SILVER:
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.addRoleSilver(value);
			role.sendRoleToClient(rms);
			String event ="useItem";
			String item = "silver";
			LogManager.itemOutputLog(role, value, event, item);
			break;
		}
		case ItemType.TYPE_KRYPTON: 
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.addRoleKrypton(value);
			role.sendRoleToClient(rms);
			String event ="useItem";
			String item = "krypton";
			LogManager.itemOutputLog(role, value, event, item);
			break;
		}
		case ItemType.TYPE_LEADER_EXP_ITEM: 
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			role.addExp(value);
			role.sendRoleToClient(rms);
			break;
		}
		case ItemType.TYPE_STAMINA:
		{
			int value = (int) (Integer.parseInt(strEffect) * num);
			if (role.getRoleStamina().getCurStamina() >= Const.MAXSTAMINA) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_STAMINA_NOUSE_MAX);
				return false;
			}
			role.getRoleStamina().updateCurStamina(value);
			//role.getRoleStamina().sendToClient(rms);
			break;
		}
		default:
			GameLog.error("item: " + itemId + "static data not found.");
			return false;
		}
		ItemCell item = getItemFromBag(itemId);
		if (type == 0) {
			if (!this.removeItems(itemId, num)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, num);
				return false;
			}
		} else {
			if(shopData == null){
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, num);
				return false;
			}
			role.redRoleMoney((int) (shopData.getNormalPrice() * num));
			String event = "useItem";
			LogManager.goldConsumeLog(role, shopData.getNormalPrice(), event);
			role.sendRoleToClient(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ITEM_USE, itemId, num);
			return true;
		}
		items.add(item);
		ItemCell[] itemArray = items.toArray(new ItemCell[items.size()]);
		// 下发
		sendItemsToClient(rms, itemArray);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		LogManager.itemConsumeLog(role, num, "useItem", itemId);
		//任务事件
		if(type == 0){
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ITEM_USE, itemId, num);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.USE_ITEM,itemId,num);
		}
		
		return true;
	}
	
	private List<ItemCell> getRandomItems(String strEffect, List<ItemCell> cells,Role role){
		List<ItemCell> items = new ArrayList<ItemCell>();
		Itembox boxData = dataManager.serach(Itembox.class, strEffect);
		if(boxData == null){
			GameLog.error("itembox statistic data not find!");
			return null;
		}
		if(boxData.getBoxtype() == 0){
			int num = 0;
			String[] strRange = boxData.getNumberlist().get(0).split(":");
			int min = Integer.parseInt(strRange[0]);
			int max = Integer.parseInt(strRange[1]);
			if(min == max){
				num = min;
			}else{
				num = MathUtils.random(min, max);
			}
			int maxRate = 0;
			for(String strRate : boxData.getWeightlist()){
				maxRate += Integer.parseInt(strRate);
			}
			for(int i=0; i < num; i++){
				int value = MathUtils.random(maxRate);
				int rate = 0;
				for(int j = 0; j < boxData.getWeightlist().size(); j++){
					rate += Integer.parseInt(boxData.getWeightlist().get(j));
					if(value < rate){
						String itemId = boxData.getItemlist().get(j);
						Item item = dataManager.serach(Item.class, itemId);
						if(item == null){
							Equip itemEquip  = dataManager.serach(Equip.class, itemId);
							if(itemEquip != null){
								List<ItemCell> newCells = this.addEquip(itemId, 1);
								items.addAll(getNewListNum((byte)1, newCells));
								cells.addAll(newCells);
								LogManager.itemOutputLog(role, 1, "useItem", itemId);
								LogManager.equipLog(role, itemEquip.getEquipType(), itemEquip.getBeizhuname(), "开启宝箱");
							}
						}
						if(item != null){
							if(item.getMaterialType() > 0){//材料
								List<ItemCell> newCells = this.addOther(itemId, 1);
								items.addAll(getNewListNum((byte)2, newCells));
								cells.addAll(newCells);
								LogManager.itemOutputLog(role, 1, "useItem", itemId);
							}else{
								List<ItemCell> newCells = this.addGoods(itemId, 1);
								items.addAll(getNewListNum((byte)0, newCells));
								cells.addAll(newCells);
								LogManager.itemOutputLog(role, 1, "useItem", itemId);
							}
						}
						break;
					}
				}
			}
		}else if(boxData.getBoxtype() == 1){
			for (int i = 0; i < boxData.getItemlist().size(); i++) {
				if (boxData.getNumberlist().get(i) != null) {
					String itemId = boxData.getItemlist().get(i);
					Item item = dataManager.serach(Item.class, itemId);
					if(item == null){
						Equip itemEquip  = dataManager.serach(Equip.class, itemId);
						if(itemEquip != null){
							int itemnum = Integer.parseInt(boxData.getNumberlist().get(i));
							List<ItemCell> newCells = this.addEquip(itemId, itemnum);
							items.addAll(getNewListNum((byte)1, newCells));
							cells.addAll(newCells);
							LogManager.itemOutputLog(role, 1, "useItem", itemId);
							LogManager.equipLog(role, itemEquip.getEquipType(), itemEquip.getBeizhuname(), "开启宝箱");
						}
					}
					if (item != null) {
						int itemnum = Integer.parseInt(boxData.getNumberlist().get(i));
						if (item.getItemType() == ItemType.TYPE_EQUIP_ITEM || item.getItemType() == ItemType.TYPE_EQUIP_DRAWING) {// 材料或图纸
							List<ItemCell> newItems = this.addOther(itemId, itemnum);
							items.addAll(getNewListNum((byte)2, newItems, itemnum));
							cells.addAll(newItems);
						} else {
							List<ItemCell> newItems = this.addGoods(itemId, itemnum);
							LogManager.itemOutputLog(role, 1, "useItem", itemId);
							items.addAll(getNewListNum((byte)0, newItems, itemnum));
							cells.addAll(newItems);
						}
					}
				}
			}
		}
		return items;
	}
	
	private List<ItemCell> getNewListNum(byte type, List<ItemCell> newCells){
		List<ItemCell> cells = new ArrayList<ItemCell>();
		for(ItemCell cell : newCells){
			ItemCell newcell = ItemCell.create(type);
			newcell.setId(cell.getId());
			newcell.setKey(cell.getKey());
			newcell.setNum(1);
			if(type == 1){
				((EquipItem)newcell).setEquipBuffIdLists(((EquipItem)cell).getEquipBuffIdLists());
			}
			cells.add(newcell);
		}
		return cells;
	}
	
	private List<ItemCell> getNewListNum(byte type, List<ItemCell> newCells, int itemnum){
		List<ItemCell> cells = new ArrayList<ItemCell>();
		for(ItemCell cell : newCells){
			ItemCell newcell = ItemCell.create(type);
			newcell.setId(cell.getId());
			newcell.setKey(cell.getKey());
			newcell.setNum(itemnum);
			if(type == 1){
				((EquipItem)newcell).setEquipBuffIdLists(((EquipItem)cell).getEquipBuffIdLists());
			}
			cells.add(newcell);
		}
		return cells;
	}
	
	public List<ItemCell> removeColumn(Class<? extends ItemCell> type){
		List<ItemCell> result = new ArrayList<ItemCell>();
		Map<String, ItemCell> temp = cells.remove(type);
		if (temp != null){
			for (ItemCell ic : temp.values()){
				ic.setNum(0);
				result.add(ic);
			}
			temp.clear();
		}
		return result;
	}
}
