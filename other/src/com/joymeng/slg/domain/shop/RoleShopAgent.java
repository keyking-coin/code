package com.joymeng.slg.domain.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.activity.data.Activity;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.shop.data.Banner;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.domain.shop.data.ShopCell;
import com.joymeng.slg.domain.shop.data.ShopLayout;
import com.joymeng.slg.domain.shop.data.ShopLimit;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

/**
 * 玩家商城
 * @author tanyong
 */
public class RoleShopAgent implements Instances{
	
	static Map<String,ShopLimit> serviceLimits = new ConcurrentHashMap<String,ShopLimit>();//服务器限购
	
	Map<String,ShopLimit> personLimits = new HashMap<String,ShopLimit>();//限购次数
	
	public static void load(){
		GameLog.info("load shop service limits from database");
		serviceLimits.clear();
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_SHOP_LIMIT);
		if (datas != null) {
			for (Map<String, Object> map : datas) {
				ShopLimit limit = new ShopLimit();
				limit.loadFromData(new SqlData(map));
				serviceLimits.put(limit.getId(),limit);
			}
		}
	}
	
	public void deserialize(String str){
		if (!StringUtils.isNull(str)) {
			personLimits = JSON.parseObject(str,new TypeReference<Map<String,ShopLimit>>(){});
		}
	}
	
	public String serialize(){
		return JsonUtil.ObjectToJsonString(personLimits);
	}
	
	public int computServiceLimitNum(String id,int max){
		int result = max;
		if (result > 0 && serviceLimits.containsKey(id)){
			result = Math.max(0,max - serviceLimits.get(id).getNum());
		}
		return result;
	}
	
	public int computPersonLimitNum(String id,int max){
		int result = max;
		if (result > 0 && personLimits.containsKey(id)){
			result = Math.max(0,max - personLimits.get(id).getNum());
		}
		return result;
	}
	
	/**
	 * 通过栏获取玩家的商店显示数据
	 * @param sct
	 * @return
	 */
	public List<ShopCell> getList(final byte type){
		List<ShopCell> cells = new ArrayList<ShopCell>();
		List<Shop> datas = dataManager.serachList(Shop.class, new SearchFilter<Shop>(){
			@Override
			public boolean filter(Shop data) {
				return data.checkWeekDay() && data.getLabelType() == type;
			}
		});
		for (int i = 0 ; i < datas.size() ; i++){
			Shop data = datas.get(i);
			String key = data.getId();
			int num = -1;
			if (data.getServerLimitNum() > 0){
				if (serviceLimits.containsKey(key)){
					num = data.getServerLimitNum() - serviceLimits.get(key).getNum();
				}
				num = Math.max(0,num);
			}
			if (data.getPersonLimitNum() > 0){
				if (personLimits.containsKey(key)){
					num = data.getPersonLimitNum() - personLimits.get(key).getNum();
				}
				num = Math.max(0,num);
			}
			ShopCell cell = new ShopCell(key,num);
			cells.add(cell);
		}
		return cells;
	}
	
	public boolean tryToBuySomeThing(Role role,String id, int num, CommunicateResp resp){
		Shop data = dataManager.serach(Shop.class,id);
		if (!data.checkWeekDay() || data.getGoodsStatus() == 99){//已过期商品
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_OUT_DATE);
			resp.add(-1);
			return false;
		}
		int needMoney = data.sellPrice() * num;
		if (role.getMoney() < needMoney){//金币不足
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,needMoney);
			resp.add(-1);
			return false;
		}
		if (role.getVipInfo().getVipLevel() < data.getVipLevel()){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_VIP_LEVEL_NOT,data.getVipLevel());
			resp.add(-1);
			return false;
		}
		if (data.getServerLimitNum() > 0){//服务器限购的产品
			synchronized(serviceLimits){
				ShopLimit ssl = serviceLimits.get(id);
				if (ssl == null){
					ssl = new ShopLimit(id);
					serviceLimits.put(id,ssl);
				}
				if (ssl.getNum() + num > data.getServerLimitNum()){//服务器限购提示
					int el = Math.max(0,data.getServerLimitNum() - ssl.getNum());
					resp.add(el);//剩余数量
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_SERVICE_LIMITE,data.getServerLimitNum());
					return false;
				}
				if (data.getPersonLimitNum() > 0){//判断个人限购逻辑
					ShopLimit sl = personLimits.get(id);
					if (sl == null){
						sl = new ShopLimit(id);
						personLimits.put(id,sl);
					}
					if (sl.getNum() + num > data.getPersonLimitNum()){//个人限购提示
						int el = Math.max(0,data.getPersonLimitNum() - sl.getNum());
						resp.add(el);//剩余数量+
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_PERSON_LIMITE,data.getPersonLimitNum());
						return false;
					}
				}
				List<ItemCell> cells = role.getBagAgent().addGoods(data.getItemid(),num);
				Item it = dataManager.serach(Item.class, data.getItemid());
				LogManager.itemOutputLog(role, num, "tryToBuySomeThing", data.getItemid());
				LogManager.shopLog(role, num, needMoney / num, "gold", it.getBeizhuname(),"金币商城");
				try {
					NewLogManager.baseEventLog(role, "bug_goods",it.getId(),num,needMoney);
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
				if (cells.size() == 0){
					GameLog.error("固化编号错误");
					resp.add(-1);
					return false;
				}
				int left = 0;
				//服务器限购数量变化
				int newNum = ssl.getNum() + num;
				ssl.setNum(newNum);
				serviceLimits.put(id,ssl);
				ssl.save();//服务器限购数据存档
				//个人限购数量变化
				if (data.getPersonLimitNum() > 0){
					ShopLimit sl = personLimits.get(id);
					if (sl == null){
						sl = new ShopLimit(id);
						personLimits.put(id,sl);
					}
					newNum = sl.getNum() + num;
					sl.setNum(newNum);
					left = data.getPersonLimitNum() - newNum;
				}else{
					left = data.getServerLimitNum() - newNum;
				}
				role.redRoleMoney(needMoney);
				String event1 = "tryToBuySomeThing";
				LogManager.goldConsumeLog(role, needMoney, event1);
				RespModuleSet rms = new RespModuleSet();
				role.getBagAgent().sendItemsToClient(rms,cells);
				role.sendRoleToClient(rms);
				MessageSendUtil.sendModule(rms,role.getUserInfo());
				resp.add(left);//剩余数量
				//任务事件
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_BUY_ITEM, num);
				return true;
			}
		}else if (data.getPersonLimitNum() > 0){//个人限购
			ShopLimit sl = personLimits.get(id);
			if (sl == null){
				sl = new ShopLimit(id);
				personLimits.put(id,sl);
			}
			if (sl.getNum() + num > data.getPersonLimitNum()){//个人限购提示
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_PERSON_LIMITE,data.getPersonLimitNum());
				return false;
			}
			List<ItemCell> cells = role.getBagAgent().addGoods(data.getItemid(),num);
			Item it = dataManager.serach(Item.class, data.getItemid());
			LogManager.itemOutputLog(role, num, "tryToBuySomeThing", data.getItemid());
			LogManager.shopLog(role, num, needMoney / num, "gold", it.getBeizhuname(), "金币商城");
			try {
				NewLogManager.baseEventLog(role, "bug_goods", it.getId(), num, needMoney);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			if (cells.size() == 0){
				GameLog.error("固化编号错误");
				return false;
			}
			int newNum = sl.getNum() + num;
			sl.setNum(newNum);
			int left = data.getPersonLimitNum() - newNum;
			role.addRoleMoney(-needMoney);
			String event2 = "tryToBuySomeThing";
			LogManager.goldConsumeLog(role, needMoney, event2);
			RespModuleSet rms = new RespModuleSet();
			ItemCell[] items = new ItemCell[cells.size()];
			cells.toArray(items);
			role.getBagAgent().sendItemsToClient(rms,items);
			role.sendRoleToClient(rms);
			MessageSendUtil.sendModule(rms,role.getUserInfo());
			resp.add(left);//剩余数量
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_BUY_ITEM, num);
			return true;
		}else{//非服务器限购
			List<ItemCell> cells = role.getBagAgent().addGoods(data.getItemid(),num);
			Item it = dataManager.serach(Item.class, data.getItemid());
			LogManager.itemOutputLog(role, num, "tryToBuySomeThing", data.getItemid());
			LogManager.shopLog(role, num, needMoney / num, "gold", it.getBeizhuname(), "金币商城");
			try {
				NewLogManager.baseEventLog(role, "bug_goods", it.getId(), num, needMoney);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			if (cells.size() == 0){
				GameLog.error("固化编号错误");
				return false;
			}
			RespModuleSet rms = new RespModuleSet();
			ItemCell[] items = new ItemCell[cells.size()];
			cells.toArray(items);
			role.getBagAgent().sendItemsToClient(rms,items);
			role.addRoleMoney(-needMoney);
			String event3 = "tryToBuySomeThing";
			LogManager.goldConsumeLog(role, needMoney, event3);
			role.sendRoleToClient(rms);
			MessageSendUtil.sendModule(rms,role.getUserInfo());
			resp.add(-1);//剩余数量
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_BUY_ITEM, num);
			return true;
		}
	}
	
	public boolean tryToUseMoneyBuy(Role role,String activityId , String shopId , String bannerId,RespModuleSet rms,boolean tip){
		Activity activity = activityManager.searchActivity(activityId);
		if (activity == null || !activity.isAlive()){//活动已过期
			if (tip){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_OUT_DATE);
			}
			return false;
		}
		ShopLayout shop = activity.searchElement(shopId);
		if (shop == null){//找不到shop
			if (tip){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_OUT_DATE);
			}
			return false;
		}
		Banner banner = shop.search(bannerId);
		if (banner == null){//找不到banner
			if (tip){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_OUT_DATE);
			}
			return false;
		}
		if (!banner.checkTime(activity)){//限购时间已过
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_OUT_DATE);
			return false;
		}
		String key = banner.getBuyKey(activityId,shopId);
		boolean buyOk = false;
		if (banner.isServiceLimitNum()){//服务器限购
			synchronized(serviceLimits){
				if (banner.buyOk(role,rms)){
					ShopLimit sl = serviceLimits.get(key);
					if (sl == null){
						sl = new ShopLimit(key);
						serviceLimits.put(key,sl);
					}
					sl.addNum(1);
					buyOk = true;
				}
			}
		}else if (banner.isPersonLimitNum()){//个人限购
			if (!banner.checkPersonNum(shop,role)){//没有购买数量了
				int limit = Integer.parseInt(banner.getPersonLimit());
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_SHOP_PERSON_LIMITE,limit);
				return false;
			}
			if (banner.buyOk(role,rms)){
				ShopLimit sl = personLimits.get(key);
				if (sl == null){
					sl = new ShopLimit(key);
					personLimits.put(key,sl);
				}
				sl.addNum(1);
				buyOk = true;
			}
		}else{
			if (banner.buyOk(role,rms)){
				buyOk = true;
			}
		}
		if (buyOk){
			activityManager.sendShopLayoutToClient(rms,role);
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_RECHARGE, 0);
			return true;
		}
		return false;
	}
}
