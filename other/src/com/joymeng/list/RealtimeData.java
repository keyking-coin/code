package com.joymeng.list;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.data.Building;
import com.joymeng.slg.domain.object.build.data.RoleBuildState;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.VipInfo;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.GameConfig;

public class RealtimeData implements Instances{
	
	static int appid = 1001;
	
	static int serverid = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
	/*
	 * 实时数据接口
	 */
	public static void liveData(){

		Map<String, ChargeData> channelList = new HashMap<String, ChargeData>();
		List<String> uuidList = new ArrayList<String>();
		List<String> newUuidList = new ArrayList<String>();
		try {
			List<SqlData> roleList = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE);
			if (roleList == null || roleList.size() == 0 ) {
				return;
			}
			for (int i = 0 ; i < roleList.size() ; i++){
				SqlData data = roleList.get(i);
				String channelid = data.getString(DaoData.RED_ALERT_ROLE_CHANNELID); // 渠道ID
				if (channelList.get(channelid) == null) {
					ChargeData  charge = new ChargeData();
					charge.setChannelId(channelid);
					channelList.put(channelid, charge);
				}
				String uidRegisTime = data.getString(DaoData.RED_ALERT_ROLE_UID_REGIS); // uid注册时间
				String uuidRegisTime = data.getString(DaoData.RED_ALERT_ROLE_UUID_REGIS); // uuid注册时间
				if (TimeUtils.nowLong() - TimeUtils.getTimes(uidRegisTime) <= Const.HOUR) { // 新增uid
					channelList.get(channelid).setRcount(
							channelList.get(channelid).getRcount() + 1);
				}
				if (TimeUtils.nowLong() - TimeUtils.getTimes(uuidRegisTime) <= Const.HOUR) { // 新增uuid
					channelList.get(channelid).setRjcount(
							channelList.get(channelid).getRjcount() + 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<SqlData> chargeList = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CHARGE_ORDER);
			while(chargeList != null){
				for (int i = 0 ; i < chargeList.size() ; i++){
					SqlData data = chargeList.get(i);
					int joyId = data.getInt(DaoData.RED_ALERT_CHARGE_JOYID);
					int value = data.getInt(DaoData.RED_ALERT_CHARGE_VALUE);
					String time = data.getString(DaoData.RED_ALERT_CHARGE_ORDER_TIME);
					if (TimeUtils.nowLong() - TimeUtils.getTimes(time) <= Const.HOUR) {// 1小时充值的人的数据
						List<SqlData> uuidlist = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE,
								DaoData.RED_ALERT_ROLE_ID, joyId);
						String uuId = uuidlist.get(0).getString(DaoData.RED_ALERT_ROLE_UUID);
						String regis = uuidlist.get(0).getString(DaoData.RED_ALERT_ROLE_UUID_REGIS);
						String channelid = data.getString(DaoData.RED_ALERT_ROLE_CHANNELID); // 渠道ID
						ChargeData charge = channelList.get(channelid);
						if (TimeUtils.nowLong() - TimeUtils.getTimes(regis) <= Const.HOUR) { // 新增uuid充值
							if (!uuidList.contains(uuId)) {
								uuidList.add(uuId);
								charge.setMcount(charge.getMcount() + 1);
							}
							charge.setNmoney(charge.getNmoney() + value);
						}
						if (!newUuidList.contains(uuId)) { // 新增充值
							newUuidList.add(uuId);
							charge.setNcount(charge.getNcount() + 1);
						}
						charge.setMoney(charge.getMoney() + value);
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<Role> role = world.getOnlineRoles();
		List<Object> list = new ArrayList<Object>();
		for (String charge : channelList.keySet()) {
			ChargeData data = channelList.get(charge);
			int rcount=data.getRcount(); // 新增UID数
			int rjcount=data.getRjcount();// 新增UUID数
			int mcount=data.getMcount();// UUID充值人数
			int ncount=data.getNcount();// UUID新用户充值人数
			int money=data.getMoney();// UUID用户充值金额
			int nmoney=data.getNmoney();// UUID新用户充值金额
			int size = role.size(); //在线人数
			Map<String, Object> map = new HashMap<String, Object>();
			if (rcount == 0 && rjcount == 0 && mcount == 0 && ncount == 0
					&& money == 0 && nmoney == 0 && size == 0) {
				continue;
			}
			map.put("time", TimeUtils.getIntegral()); 
			map.put("appid", appid);
			map.put("serverid", serverid);
			map.put("channelid", data.getChannelId());
			map.put("rcount", rcount);
			map.put("rjcount", rjcount);
			map.put("mcount", mcount);
			map.put("ncount", ncount);
			map.put("money", money);
			map.put("nmoney", nmoney);
			map.put("online", size);
			list.add(map);
		}  
		if(list.size()==0){
			return;
		}
		getConnection(list, "live_data");
	}
	
	
	/*
	 * 全区等级分布接口
	 */
	public static void vipLevel() {
		Map<String, Map<String, Integer>> strMap = new HashMap<String, Map<String, Integer>>();
		List<SqlData> list = new ArrayList<SqlData>();
		try {
			list = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE);
			for (int i = 0 ; i < list.size() ; i++){
				SqlData data = list.get(i);
				int level = data.getInt(DaoData.RED_ALERT_GENERAL_LEVEL);
				String strVip = data.getString(DaoData.RED_ALERT_ROLE_VIPINFO);
				String channelid = data.getString(DaoData.RED_ALERT_ROLE_CHANNELID);
				if (strMap.get(channelid) == null) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					strMap.put(channelid, map);
				}
				Map<String, Integer> str = strMap.get(channelid);
				VipInfo vipInfo = new VipInfo();
				vipInfo.deserialize(strVip);
				byte vipLevel = vipInfo.getVipLevel();
				String key = String.valueOf(level) + "_"+ String.valueOf(vipLevel);
				if(str.get(key)==null){
					str.put(key, 1);
				}else{
					str.put(key, str.get(key) + 1);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<Object> ls = new ArrayList<Object>();
		for (String str : strMap.keySet()) {
			for (String s : strMap.get(str).keySet()) {
				String[] ss = s.split("_");
				int number = strMap.get(str).get(s);
				if(number==0){
					continue;
				}
				Map<String, Object> smap = new HashMap<String, Object>();
				smap.put("time", TimeUtils.yesterday());
				smap.put("appid", appid);
				smap.put("serverid", serverid);
				smap.put("channelid", str);
				smap.put("vip", ss[1]);
				smap.put("level", ss[0]);
				smap.put("num", number);
				ls.add(smap);
			}
		}
		if(ls.size()==0){
			return;
		}
		getConnection(ls, "vip_level");
	}
		
	/*
	 * 道具库存接口
	 */
	public static void propStock() {
		
		Map<String,PropData> propmap = getPropMap();
		List<SqlData> list = new ArrayList<SqlData>();
		try {
			list = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CITY); // 玩家city表
			for(SqlData data : list){
				String str = data.getString(DaoData.RED_ALERT_CITY_RESOURCES);
				Map<ResourceTypeConst,Long> resources  = JSON.parseObject(str,new TypeReference<Map<ResourceTypeConst,Long>>(){});
                for(ResourceTypeConst resource:resources.keySet()){
                	String type= resource.getKey();
                	long number = resources.get(resource);
    				long resSyncTime = data.getLong(DaoData.RED_ALERT_CITY_RESSYNCTIME); // 离线时间
    				PropData prop = propmap.get(type);
    				if(prop!=null){
    					opData(prop, number, resSyncTime);
    				}    	
                }
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<SqlData> othList = new ArrayList<SqlData>();
		try {
			othList = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE); // 玩家表
			for(SqlData data : othList){
				
				long joy_id = data.getInt(DaoData.RED_ALERT_ROLE_ID); 
				List<SqlData> cityList = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CITY,
						DaoData.RED_ALERT_GENERAL_UID, joy_id);
				long resSyncTime = cityList.get(0).getLong(DaoData.RED_ALERT_CITY_RESSYNCTIME); // 离线时间
				int money = data.getInt(DaoData.RED_ALERT_ROLE_MONEY); //金币
				int krypton=  data.getInt(DaoData.RED_ALERT_ROLE_KRYPTON);//氪金
				int gen=  data.getInt(DaoData.RED_ALERT_ROLE_GEM); //宝石
				int copper=  data.getInt(DaoData.RED_ALERT_ROLE_COPPER); //铜币
				int silver=  data.getInt(DaoData.RED_ALERT_ROLE_SLIVER);//银币
				RoleBagAgent bagAgent = new RoleBagAgent();
				bagAgent.deserialize(data.get(DaoData.RED_ALERT_ROLE_BAGDATAS));
                int item = bagAgent.getItemsCount();
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("money", money);
				map.put("krypton", krypton);
				map.put("gen", gen);
				map.put("copper", copper);
				map.put("silver", silver);
				map.put("item", item);
				for(String str :map.keySet()){
                	long number = map.get(str);
                	PropData prop = propmap.get(str);
    				if(prop!=null){
    					opData(prop, number, resSyncTime);
    				} 
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<Object> ls = new ArrayList<Object>();
		for(String str:propmap.keySet()){
			byte type= getType(propmap.get(str).getType());
			PropData data  = propmap.get(str);
			long num=data.getNum();
			long active_num=data.getActive_num();
			long quiet_num=data.getQuiet_num();
			long uid_num=data.getUid_num();
			long active_uid_num=data.getActive_uid_num();
			long quiet_uid_num=data.getQuiet_uid_num();
			if(num==0){
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", TimeUtils.yesterday());
			map.put("appid", appid);
			map.put("serverid", serverid);
			map.put("prop_type",type);
			map.put("num",num );
			map.put("active_num",active_num);
			map.put("quiet_num", quiet_num);
			map.put("uid_num", uid_num);
			map.put("active_uid_num",active_uid_num);
			map.put("quiet_uid_num",quiet_uid_num);
			ls.add(map);
		}
		if(ls.size()==0){
			return;
		}
		getConnection(ls, "prop_stock");
	}	
	
	public static void opData(PropData prop,long number,long resSyncTime){
		long num = prop.getNum();// 所有元宝数量
		long active_num = prop.getActive_num();// 活跃元宝数量
		long quiet_num = prop.getQuiet_num();// 沉寂元宝数量(玩家连续15天未登录)
		long uid_num = prop.getUid_num();// 持有元宝人数
		long active_uid_num = prop.getActive_uid_num();// 持有活跃元宝人数
		long quiet_uid_num = prop.getQuiet_uid_num();// 持有沉寂元宝人数
		if(number!=0){
			prop.setUid_num(uid_num+1);
		}
		prop.setNum(num+number);
		if (TimeUtils.nowLong() - resSyncTime >= Const.DAY * 15) {
			prop.setQuiet_uid_num(quiet_uid_num+1);
			prop.setQuiet_num(quiet_num+number);
		} else {
			prop.setActive_uid_num(active_uid_num+1);
			prop.setActive_num(active_num+number);
		}
	}
	
	public static Map<String,PropData> getPropMap(){
		Map<String,PropData> propmap = new HashMap<String,PropData>();
		for(PropType type :PropType.values()){
			PropData data = new PropData();
			data.setType(type.getKey());
			propmap.put(type.getKey(),data);
		}
		return propmap;
	}
	
	/*
	 * 工会接口
	 */
	public static void gameAlly() {
		long lcount;// 工会数量
		long rcount = 0;// 工会总人数
		List<UnionBody> unions = world.getListObjects(UnionBody.class);
		lcount = unions.size();
		if (lcount == 0) {
			return;
		}
		for (int i = 0 ; i < lcount ; i++){
			UnionBody union = unions.get(i);
			List<UnionMember> members = union.getMembers();
			rcount += members.size();
		}
		List<Object> list = new ArrayList<Object>();
		if(lcount!=0){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", TimeUtils.yesterday());
			map.put("appid", appid);
			map.put("serverid", serverid);
			map.put("lcount", lcount);
			map.put("rcount", rcount);
			map.put("type", 1);
			list.add(map);
		}
		if(list.size()==0){
			return;
		}
		getConnection(list, "game_ally");
	}
	
	/*
	 * 流失玩家等级分布接口
	 */
	public static void levelLoss() {
		Map<String, Map<String, Integer>> strMap = new HashMap<String, Map<String, Integer>>();
		List<SqlData> list = new ArrayList<SqlData>();
		try {
			list = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE);
			for (int i = 0 ; i < list.size() ; i++){
				SqlData data = list.get(i);
				long joy_id = data.getInt(DaoData.RED_ALERT_ROLE_ID);
				List<SqlData> sqlData = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_GENERAL_UID, joy_id);
				SqlData sq = sqlData.get(0);
				long resSyncTime = sq.getLong(DaoData.RED_ALERT_CITY_RESSYNCTIME); // 离线时间
				int level = data.getInt(DaoData.RED_ALERT_GENERAL_LEVEL);
				String strVip = data.getString(DaoData.RED_ALERT_ROLE_VIPINFO);
				String channelid = data.getString(DaoData.RED_ALERT_ROLE_CHANNELID);
				if (strMap.get(channelid) == null) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					strMap.put(channelid, map);
				}
				Map<String, Integer> str = strMap.get(channelid);
				VipInfo vipInfo = new VipInfo();
				vipInfo.deserialize(strVip);
				byte vipLevel = vipInfo.getVipLevel();
				String key = String.valueOf(level) + "_"+ String.valueOf(vipLevel);
				if (TimeUtils.nowLong() - resSyncTime < Const.DAY * 15) {
					continue;
				} else if (str.get(key) == null) {
					str.put(key, 1);
				} else {
					str.put(key, str.get(key) + 1);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<Object> ls = new ArrayList<Object>();
		for (String str : strMap.keySet()) {
			for (String s : strMap.get(str).keySet()) {
				String[] ss = s.split("_");
				int number = strMap.get(str).get(s);
				if(number==0){
					continue;
				}
				Map<String, Object> smap = new HashMap<String, Object>();
				smap.put("time", TimeUtils.yesterday());
				smap.put("appid", appid);
				smap.put("serverid", serverid);
				smap.put("channelid", str);
				smap.put("vip", ss[1]);
				smap.put("level", ss[0]);
				smap.put("num", strMap.get(str).get(s));
				ls.add(smap);
			}
		}
		if(ls.size()==0){
			return;
		}
		getConnection(ls, "level_loss");
	}
	
	/*
	 * 其他道具物品(建筑)等级分布
	 */
	public static void levelOther() {
		Map<String, Map<String, Integer>> map = new HashMap<String,Map<String,Integer>>();
		try {
			List<SqlData> list = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CITY);
			for (int i = 0 ; i < list.size() ; i++){
				SqlData data = list.get(i);
				long uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
				List<SqlData> uidLs = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE, DaoData.RED_ALERT_ROLE_ID,uid);
				String channelid = uidLs.get(0).getString(DaoData.RED_ALERT_ROLE_CHANNELID);//渠道Id
				if(map.get(channelid)==null){
					Map<String, Integer> buildMap = new HashMap<String, Integer>();
					map.put(channelid,buildMap); //建筑_等级_数量
				}
				JoyBuffer buildData = JoyBuffer.wrap((byte[])data.get(DaoData.RED_ALERT_CITY_BUILDS));
				List<RoleBuild> builds = new CopyOnWriteArrayList<RoleBuild>();//玩家城市中所有的建筑
				int size = buildData.getInt();
				for(int j = 0 ; j < size; j++){
					RoleBuild build = new RoleBuild();
					build.deserialize(buildData);
					if(build.getState() == RoleBuildState.COND_DELETED.getKey()){
						continue;
					}
					if (build.isOnly()){
						for (int k = 0 ; k < builds.size() ; k++){
							RoleBuild rb = builds.get(k);
							if (rb.getBuildId().equals(build.getBuildId())){
								return;
							}
						}
					}
					builds.add(build);
				}
				for (int j = 0 ; j < builds.size() ; j++){
					RoleBuild build = builds.get(j);
					Building bd = dataManager.serach(Building.class, build.getBuildId());
					String name = bd.getName();
					byte level  = build.getLevel();
					String key = name+"_"+String.valueOf(level);
					Map<String,Integer> str = map.get(channelid);
					if(str.get(key)==null){
						str.put(key, 1);
					}else{
						str.put(key, str.get(key)+1);
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	    
		List<Object> ls = new ArrayList<Object>();
		for (String str : map.keySet()) {
			for (String s : map.get(str).keySet()) {
				String[] ss = s.split("_");
				int number = map.get(str).get(s);
				if(number==0){
					continue;
				}
				Map<String, Object> smap = new HashMap<String, Object>();
				smap.put("time", TimeUtils.yesterday());
				smap.put("appid", appid);
				smap.put("serverid", serverid);
				smap.put("channelid", str);
				smap.put("type", 1);
				smap.put("name", ss[0]);
				smap.put("level", ss[1]);
				smap.put("num", number);
				ls.add(smap);
			}
		}
	    if(ls.size()==0){
	    	return;
	    }
	    getConnection(ls, "level_other");
	}
	
	
	/*
	 * 类型分布接口
	 */
	public static void levelType() {
		Map<String, Map<String, Integer>> couMap = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> lanMap = new HashMap<String, Map<String, Integer>>();
		try {
			List<SqlData> list = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE);
			for (int i = 0 ; i < list.size() ; i++){
				SqlData data = list.get(i);
				String country = data.getString(DaoData.RED_ALERT_ROLE_INCOUNTRY);// 国家
				String language = data.getString(DaoData.RED_ALERT_ROLE_LANGUAGE);// 语言
				String channelid = data.getString(DaoData.RED_ALERT_ROLE_CHANNELID);// 渠道
				if (couMap.get(channelid) == null) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					couMap.put(channelid, map);
				} else if (couMap.get(channelid).get(country) == null) {
					couMap.get(channelid).put(country, 1);
				} else {
					Map<String, Integer> ct = couMap.get(channelid);
					ct.put(country, ct.get(country) + 1);
				}

				if (lanMap.get(channelid) == null) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					lanMap.put(channelid, map);
				} else if (lanMap.get(channelid).get(language) == null) {
					lanMap.get(channelid).put(language, 1);
				} else {
					Map<String, Integer> lg = lanMap.get(channelid);
					lg.put(language, lg.get(language) + 1);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<Object> ls = new ArrayList<Object>();
		for (String str : lanMap.keySet()) {
			for (String s : lanMap.get(str).keySet()) {
				int number = lanMap.get(str).get(s);
				if(number==0){
					continue;
				}
				Map<String, Object> smap = new HashMap<String, Object>();
				smap.put("time", TimeUtils.yesterday());
				smap.put("appid", appid);
				smap.put("serverid", serverid);
				smap.put("channelid", str);
				smap.put("type", 2);
				smap.put("name", s);
				smap.put("num", lanMap.get(str).get(s));
				ls.add(smap);
			}
		}
		
		for (String str : couMap.keySet()) {
			for (String s : couMap.get(str).keySet()) {
				int number = couMap.get(str).get(s);
				if(number==0){
					continue;
				}
				Map<String, Object> smap = new HashMap<String, Object>();
				smap.put("time", TimeUtils.yesterday());
				smap.put("appid", appid);
				smap.put("serverid", serverid);
				smap.put("channelid", str);
				smap.put("type", 1);
				smap.put("name", s);
				smap.put("num", couMap.get(str).get(s));
				ls.add(smap);
			}
		}
		if(ls.size()==0){
			return;
		}
		getConnection(ls, "level_type");
	}
	
	
	public static void getConnection(List<Object> list,String type){
		try {
			URL url = new URL(
					"http://netunion.joymeng.com/index.php?m=Api&c=Index&a=index");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true); // 是否输入参数
			StringBuffer params = new StringBuffer();
			params.append("&").append("api_type").append("=")
					.append(type).append("&").append("api_data")
					.append("=").append(java.net.URLEncoder.encode(JsonUtil.ObjectToJsonString(list), "UTF-8"));
			byte[] bypes = params.toString().getBytes();
			connection.getOutputStream().write(bypes);// 输入参数
			InputStream in = connection.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			in.close();
			String str = new String(bos.toByteArray(), "UTF-8");
			GameLog.info(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 获取道具类型byte
	 */
	public static byte getType(String item_type) {
		byte type;
		switch (item_type) {
		case "money":
			type = 1;
			break;
		case "food":
			type = 3;
			break;
		case "oil":
			type = 4;
			break;
		case "metal":
			type = 5;
			break;
		case "alloy":
			type = 6;
			break;
		case "copper":
			type = 7;
			break;
		case "gem":
			type = 8;
			break;
		case "silver":
			type = 9;
			break;
		case "krypton":
			type = 10;
			break;
		case "item":
			type = 11;
			break;
		default:
			type = 11;
			break;
		}
		return type;
	}
}
