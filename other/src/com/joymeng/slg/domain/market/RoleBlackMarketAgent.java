package com.joymeng.slg.domain.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.market.data.Blackshop;
import com.joymeng.slg.domain.market.data.Blackshopcost;
import com.joymeng.slg.domain.market.data.DailyDiscount;
import com.joymeng.slg.domain.market.data.MarketCell;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

/**
 * 玩家黑市
 * @author tanyong
 *
 */
public class RoleBlackMarketAgent implements Instances,TimerOver{
	static Map<String,DailyDiscount> discountCells = new HashMap<String,DailyDiscount>();
	DailyDiscount discountCell = new DailyDiscount();//每日折扣
	List<MarketCell> cells = new ArrayList<MarketCell>();//出售格子列表
	int refreshNum = 1;//刷新次数,每日更新
	String daiyTime;//刷新次数重置时间
	TimerLast refreshTimer = new TimerLast(this);//刷新倒计时
	long uid;//玩家编号
	String loadTemp;
	
	
	/**
	 * 玩家创建的时候的逻辑
	 * @param role
	 */
	public void createInit(Role role){
		this.uid = role.getId();
		refreshCells(role);
		checkDiscountCell(role,null);
		resetTimer();
	}
	
	/**
	 * 黑市开服加载每日折扣数据
	 * @throws Exception
	 */
	public static void startInit() throws Exception{
		File file = new File(Const.MARKET_SAVE_FILE_NAME);
		if (file.exists()){
			InputStream in = new FileInputStream(file);
			JoyBuffer buffer = JoyBuffer.allocate(1024);
			byte[] data = new byte[1024];
			while (true){
				int len = in.read(data);
				if (len == -1){
					break;
				}
				buffer.put(data,0,len);
			}
	        in.close();
	        String str = new String(buffer.arrayToPosition());
	        Map<String,DailyDiscount> temp =  JSON.parseObject(str,new TypeReference<Map<String,DailyDiscount>>(){});
	        discountCells.putAll(temp);
		}
        refreshDailyDiscounts(false);
	}
	
	/**
	 * 刷新每日最低折扣
	 */
	public static void refreshDailyDiscounts(boolean mustRefresh){
		List<Blackshop> bses = dataManager.serachList(Blackshop.class,new SearchFilter<Blackshop>(){
			@Override
			public boolean filter(Blackshop data) {
				return data.getRandomtype() == MarketDiscountType.MARKET_DISCOUNT_TYPE_MIN.ordinal();
			}
        });
		if (mustRefresh){
			discountCells.clear();
		}
		List<String> keys = new ArrayList<String>();
		for (int i = 0 ; i < bses.size() ; i++){
			Blackshop bs = bses.get(i);
			String key   = bs.getBaselv();
			if (!keys.contains(key)){
				keys.add(key);
			}
		}
		for (int i = 0 ; i < keys.size() ; i++){
			String key = keys.get(i);
        	if (mustRefresh){
        		DailyDiscount ndd = random(MarketDiscountType.MARKET_DISCOUNT_TYPE_MIN.ordinal(),key,1) ;
        		if (ndd != null){
        			discountCells.put(key,ndd);
        		}
        	}else{
        		DailyDiscount dd = discountCells.get(key);
            	if (dd == null || !dd.check()){//新的一天
            		DailyDiscount ndd = random(MarketDiscountType.MARKET_DISCOUNT_TYPE_MIN.ordinal(),key,1) ;
        			if (ndd != null){
        				if (dd != null){
        					dd.copy(ndd);
        				}else{
        					discountCells.put(key,ndd);
        				}
        			}
            	}
        	}
        }
	}
	
	private static DailyDiscount random(final int rt,final String levelKey,final int refreshNum){
		List<Blackshop> bses = dataManager.serachList(Blackshop.class,new SearchFilter<Blackshop>(){
			@Override
			public boolean filter(Blackshop data) {
				String numStr = data.getDegree();
				String[] ns = numStr.split(":");
				int min = StringUtils.isNull(ns[0]) ? Integer.MAX_VALUE : Integer.parseInt(ns[0]);
				int max = StringUtils.isNull(ns[1]) ? Integer.MAX_VALUE : Integer.parseInt(ns[1]);
				return data.getRandomtype() == rt && data.getBaselv().equals(levelKey) && refreshNum >= min && refreshNum <= max;
			}
		});
		Blackshop[] tbs = new Blackshop[bses.size()];
		int[] rates = new int[bses.size()];
		for (int i = 0 ; i < bses.size() ; i++ ){
			Blackshop bs = bses.get(i);
			tbs[i] = bs;
			rates[i] = bs.getRate();
		}
		Blackshop bs = MathUtils.getRandomObj(tbs,rates);
		if (bs != null){
			DailyDiscount ndd = new DailyDiscount();
			ndd.init(bs);
			return ndd;
		}
		return null;
	}
	
	/**
	 * 黑市每日折扣关服保存
	 */
	public static void closeSave(){
		try {
			File file = new File(Const.MARKET_SAVE_FILE_NAME);
			if (!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			String saveStr = JsonUtil.ObjectToJsonString(discountCells);
			byte[] datas = saveStr.getBytes(Charset.forName("UTF-8"));
			fos.write(datas);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<Blackshop> getShopDatas(final int rt,final int bl){
		return dataManager.serachList(Blackshop.class,new SearchFilter<Blackshop>(){
			@Override
			public boolean filter(Blackshop data) {
				String numStr = data.getDegree();
				String[] ns = numStr.split(":");
				int nMin = StringUtils.isNull(ns[0]) ? Integer.MAX_VALUE : Integer.parseInt(ns[0]);
				int nMax = StringUtils.isNull(ns[1]) ? Integer.MAX_VALUE : Integer.parseInt(ns[1]);
				String bStr  = data.getBaselv();
				String[] bs = bStr.split(":");
				int bMin = StringUtils.isNull(ns[0]) ? Integer.MAX_VALUE : Integer.parseInt(bs[0]);
				int bMax = StringUtils.isNull(ns[1]) ? Integer.MAX_VALUE : Integer.parseInt(bs[1]);
				return data.getRandomtype() == rt && bl >=bMin && bl <= bMax && refreshNum >= nMin && refreshNum <= nMax;
			}
		});
	}
	
	private Blackshop randomCell(List<Blackshop> lis){
		Blackshop[] bss = new Blackshop[lis.size()];
		int[] rates = new int[lis.size()];
		for (int i = 0 ; i  < lis.size() ; i++){
			Blackshop bs = lis.get(i);
			bss[i]   = bs;
			rates[i] = bs.getRate();
		}
		return MathUtils.getRandomObj(bss,rates);
	}
	
	public void refreshCells(Role role){
		synchronized (cells) {
			cells.clear();
			int level = role.getBuildLevel(0,BuildName.CITY_CENTER.getKey());
			List<Blackshop> lis = getShopDatas(MarketDiscountType.MARKET_DISCOUNT_TYPE_NORMAL.ordinal(),level);
			Blackshop dbs = discountCell.shopData();
			if (dbs != null){
				lis.add(discountCell.shopData());
			}
			if(lis.size() == 0){
				return;
			}
			int count = 100;//防止死循环
			while (cells.size() < GameConfig.BLACK_MARKET_CELL_NUM && count > 0){
				Blackshop bs  = randomCell(lis);
				if (bs != null){
					MarketCell cell = new MarketCell();
					cell.init(bs);
					cells.add(cell);
					lis.remove(bs);
				}
				count--;
			}
		}
	}
	
	public void refresh(){
		Role role = world.getRole(uid);
		if (role != null){
			resetTimer(TimeUtils.nowLong() / 1000,GameConfig.BLACK_MARKET_REFRESH_TIME);
			refreshCells(role);
			if (role.isOnline()){
				RespModuleSet rms = new RespModuleSet();
				role.sendRoleToClient(rms);
				AbstractClientModule module = new AbstractClientModule() {
					@Override
					public short getModuleType() {
						return NTC_DTCD_MARKET_REFRESH;
					} 
				};
				rms.addModule(module);
				MessageSendUtil.sendModule(rms,role.getUserInfo());
			}
		}
	}
	
	private void resetTimer(){
		resetTimer(TimeUtils.nowLong()/1000,GameConfig.BLACK_MARKET_REFRESH_TIME);
	}
	
	private void resetTimer(long start,long last){
		refreshTimer.setType(TimerLastType.TIME_BLACK_MARKET_REFRESH);
		refreshTimer.setStart(start);
		refreshTimer.setLast(last);
	}
	
	private boolean checkDaiyTime(){
		if (!StringUtils.isNull(daiyTime)){
			String str = TimeUtils.formatDay(TimeUtils.now());
			return daiyTime.equals(str);
		}
		return false;
	}
	
	public void gmRefresh(Role role){
		int cbl = role.getBuildLevel(0,BuildName.CITY_CENTER.getKey());
		List<Blackshop> bses = dataManager.serachList(Blackshop.class,new SearchFilter<Blackshop>(){
			@Override
			public boolean filter(Blackshop data) {
				return data.getRandomtype() == MarketDiscountType.MARKET_DISCOUNT_TYPE_MIN.ordinal();
			}
        });
		List<String> keys = new ArrayList<String>();
		for (int i = 0 ; i < bses.size() ; i++){
			Blackshop bs = bses.get(i);
			String key   = bs.getBaselv();
			if (!keys.contains(key)){
				keys.add(key);
			}
		}
		for (int i = 0 ; i < keys.size() ; i++){
			String key = keys.get(i);
			String[] ss = key.split(":");
			int min = StringUtils.isNull(ss[0]) ? Integer.MAX_VALUE : Integer.parseInt(ss[0]);
			int max = StringUtils.isNull(ss[1]) ? Integer.MAX_VALUE : Integer.parseInt(ss[1]);
			if (cbl >= min && cbl <= max){//满足玩家等级条件的每日最低折扣
				discountCell = random(MarketDiscountType.MARKET_DISCOUNT_TYPE_MIN.ordinal(),key,1);
				break;
			}
        }
	}
	
	private void checkDiscountCell(Role role , DailyDiscount src){
		if (src != null && src.check()){//日期已过了
			discountCell.copy(src);
		}else{
			int cbl = role.getBuildLevel(0,BuildName.CITY_CENTER.getKey());
			for (String dkey : discountCells.keySet()){
				String[] ss = dkey.split(":");
				int min = StringUtils.isNull(ss[0]) ? Integer.MAX_VALUE : Integer.parseInt(ss[0]);
				int max = StringUtils.isNull(ss[1]) ? Integer.MAX_VALUE : Integer.parseInt(ss[1]);
				if (cbl >= min && cbl <= max){//满足玩家等级条件的每日最低折扣
					DailyDiscount sdd = discountCells.get(dkey);
					discountCell.copy(sdd);
					break;
				}
			}
		}
	}
	
	public void loadOver(Role role) {
		if (!StringUtils.isNull(loadTemp)) {
			JSONObject jObj = JSON.parseObject(loadTemp);
			String key = "dc";
			if (jObj.containsKey(key)){
				Object temp = jObj.get("dc");
				DailyDiscount src = JsonUtil.JsonToObject(temp.toString(),DailyDiscount.class);
				checkDiscountCell(role,src);
			}
			key = "cs";
			if (jObj.containsKey(key)){
				Object temp = jObj.get(key);
				List<MarketCell> tcells = JsonUtil.JsonToObjectList(temp.toString(),MarketCell.class);
				cells.addAll(tcells);	
			}
			key = "rrt";
			if (jObj.containsKey(key)){
				Object temp = jObj.get(key);
				daiyTime = temp.toString();
			}else{
				daiyTime = TimeUtils.formatDay(TimeUtils.now());
			}
			if (checkDaiyTime()){
				key = "rn";
				if (jObj.containsKey(key)){
					Object temp = jObj.get(key);
					refreshNum = Integer.parseInt(temp.toString());
				}
			}else{//充值刷新次数和事件
				refreshNum = 1;
				daiyTime = TimeUtils.formatDay(TimeUtils.now());
			}
			key = "rt";
			if (jObj.containsKey(key)){
				Object temp = jObj.get(key);
				TimerLast tempTimer = JsonUtil.JsonToObject(temp.toString(),TimerLast.class);
				resetTimer(tempTimer.getStart(),tempTimer.getLast());
			}else{
				resetTimer();
			}
		}else{
			resetTimer();
		}
	}
	
	public void deserialize(String loadTemp,long uid){
		this.uid     = uid;
		this.loadTemp = loadTemp;
	}
	
	public String serialize(){
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("dc",discountCell);
		data.put("cs",cells);
		data.put("rn",refreshNum);
		data.put("rrt",daiyTime);
		data.put("rt",refreshTimer);
		String str = JsonUtil.ObjectToJsonString(data);
		return str;
	}
	
	public int getRefreshMoney(){
		Blackshopcost bsc = dataManager.serach(Blackshopcost.class,refreshNum + "");
		if (bsc == null){
			bsc = dataManager.serach(Blackshopcost.class,100 + "");
		}
		return bsc.getCost();
	}
	
	public void tick(long now){
		if (refreshTimer.over(now)){
			refreshTimer.die();
		}
	}
	
	public void sendClient(CommunicateResp resp){
		resp.add(discountCell);
		resp.add(getRefreshMoney());
		resp.add(refreshTimer);
		resp.add(cells);
	}

	@Override
	public void finish() {
		refresh();
	}

	public boolean tryToRefresh(Role role , CommunicateResp resp){
		int needMoney = getRefreshMoney();
		if (role.getMoney() < needMoney){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,needMoney);
			return false;
		}
		role.redRoleMoney(needMoney);
		LogManager.goldConsumeLog(role, needMoney, EventName.tryToRefresh.getName());
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms,role);
		refreshCells(role);
		refreshNum ++;
		sendClient(resp);
		try {
			NewLogManager.buildLog(role, "refresh_market",needMoney);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}
	
	public boolean tryToBuyCell(Role role,String buyId, CommunicateResp resp) {
		MarketCell buyCell = null;
		for (int i = 0 ; i < cells.size() ; i++){
			MarketCell cell = cells.get(i);
			if (cell.getId().equals(buyId)){
				buyCell = cell;
				break;
			}
		}
		if (buyCell == null){
			return false;
		}
		if (buyCell.getNum() == 0){
			GameLog.error("数量不足");
			return false;
		}
		Item target = dataManager.serach(Item.class, buyCell.getItemId());
		if (target == null){
			GameLog.error("策划又吧数据填错了");
			return false;
		}
		RoleCityAgent city = role.getCity(0);
		ResourceTypeConst rtc  = ResourceTypeConst.search(buyCell.getCostKey());
		int need = buyCell.getCostNum();
		switch (rtc){
			case RESOURCE_TYPE_FOOD:{
				if (city.getResource(rtc) < need){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MUST_NEED_FOOD,need);
					return false;
				}
				break;
			}
			case RESOURCE_TYPE_METAL:{
				if (city.getResource(rtc) < need){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MUST_NEED_METAL,need);
					return false;
				}
				break;
			}
			case RESOURCE_TYPE_OIL:{
				if (city.getResource(rtc) < need){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MUST_NEED_OIL,need);
					return false;
				}
				break;
			}
			case RESOURCE_TYPE_ALLOY:{
				if (city.getResource(rtc) < need){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MUST_NEED_ALLOY,need);
					return false;
				}
				break;
			}
			case RESOURCE_TYPE_GOLD:{
				if (role.getMoney() < need){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,need);
					return false;
				}
				break;
			}
			default:{
				GameLog.info("warning");
				return false;
			}
		}
		List<ItemCell> itemCells = null;
		if (target.getMaterialType() > 0){
			itemCells = role.getBagAgent().addOther(target.getId(),1);
		}else{
			itemCells = role.getBagAgent().addGoods(target.getId(),1);
		}
		LogManager.itemOutputLog(role, 1, EventName.tryToBuyCell.getName(), target.getId());
		RespModuleSet rms = new RespModuleSet();
		role.getBagAgent().sendItemsToClient(rms,itemCells);
		if (rtc.ordinal() <= ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal()){
			role.redResourcesFromCity(rms,0,rtc,need);
			LogManager.itemConsumeLog(role, need, EventName.tryToBuyCell.getName(), rtc.getKey());
		}else if (rtc == ResourceTypeConst.RESOURCE_TYPE_GOLD){
			role.redRoleMoney(need);
			role.sendRoleToClient(rms);
			LogManager.goldConsumeLog(role, need, EventName.tryToBuyCell.getName());
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
		int newNum = Math.max(0,buyCell.getNum()-1);
		buyCell.setNum(newNum);
		boolean needRefresh = true;
		for (int i = 0 ; i < cells.size() ; i++){
			MarketCell cell = cells.get(i);
			if (cell.getNum() > 0){
				needRefresh = false;
			}
		}
		if (needRefresh){
			refreshCells(role);
		}
		String buy_type = "";
		switch (rtc) {
		case RESOURCE_TYPE_GOLD:
			buy_type = "gold";
			break;
		case RESOURCE_TYPE_FOOD:
			buy_type = ResourceTypeConst.RESOURCE_TYPE_FOOD.getKey();
			break;
		case RESOURCE_TYPE_METAL:
			buy_type = ResourceTypeConst.RESOURCE_TYPE_METAL.getKey();
			break;
		case RESOURCE_TYPE_OIL:
			buy_type = ResourceTypeConst.RESOURCE_TYPE_OIL.getKey();
			break;
		case RESOURCE_TYPE_ALLOY:
			buy_type = ResourceTypeConst.RESOURCE_TYPE_ALLOY.getKey();
			break;
		default:
			buy_type = "其他";
			break;
		}
		LogManager.shopLog(role, 1, need, buy_type, target.getBeizhuname(), "玩家黑市");
		try {
			if(buy_type.equals("gold")){
				NewLogManager.buildLog(role, "buy_market_item",need);
			}else{
				NewLogManager.buildLog(role, "buy_market_item",0);
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		sendClient(resp);
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_BUY_MARK, buyCell.getItemId(),1);
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.MUNITION_DEAL);
		return true;
	}
}
