package com.joymeng.slg.domain.object.build;

public enum BuildName {
	BANNER("Banner"), //旗帜
	BILLBORAD("Billboard"),//布告栏
	SOCIAL_CENTER("SocialCenter"),//社交中心
	WELFARE_CENTER("WelfareCenter"),//福利中心
	NEWS_SERVICE("NewsService"),//新闻社
	ORE_PURIFIER("OrePurifier"),//矿石精炼厂
	GRANDE_CANNON("GrandeCannon"),//巨炮
	TESLA_COIL("TeslaCoil"),//磁暴线圈
	LASER_TOWER("LaserTower"),//光棱塔
	NUCLEAR_REACTOR("NuclearReactor"),//核子反应炉
	HOSPITAL("Hospital"),//医院
	REPAIRER("Repairer"),//维修厂
	EQUIP_LAB("EquipLab"),//装备实验室
	LOGISTICS_CENTER("LogisticsCenter"),//物流中心
	EMBASSY("Embassy"),//大使馆
	TRADE_CENTER("TradeCenter"),//贸易中心
	WAR_LOBBY("WarLobby"),//战争大厅
	COMMAND_POST("CommandPost"),//指挥所
	MILITARY_SCHOOL("MilitarySchool"),//军校
	SPY_SATELLITE("SpySatellite"),//
	RADAR("Radar"),//雷达
	FENCE("Fence"),//围墙
	TITANIUM_PLANT("TitaniumPlant"),//钛合金厂
	REFINERY("Refinery"),//炼油厂
	SMELTER("Smelter"),//冶炼厂
	FOOD_FACT("FoodFact"),//食品厂
	POWER_PLANT("PowerPlant"),//发电厂
	MILITARY_FACT("MilitaryFact"),//军工厂
	AIR_COM("AirCom"),//空中指挥部
	ARMORED_FACT("TankFact"),//坦克工厂
	WAR_FACT("WarFact"),//战车工厂
	SOLDIERS_CAMP("SoldiersCamp"),//兵营
	TECH_CENTER("TechCenter"),//科技中心
	CITY_CENTER("CityCenter"),//市政府
	ARMS_DEALER("ArmsDealer"),//黑市
	MAP_FORTRESS("fortress"),//大地图要塞
	MAP_BARRACKS("barracks"),//兵营
	MAP_BASE("base"),//基地
	MAP_UNION_STORAGE("alliancedepot"),//联盟仓库
	MAP_UNION_FOOD("superfood"),//联盟良田
	MAP_UNION_METAL("supermetal"),//联盟金属
	MAP_UNION_OIL("superoil"),//联盟石油
	MAP_UNION_ALLOY("superalloy"),//联盟钛合金
	Map_UNION_TOWER_AIR("airdefense"),//联盟爱国者导弹
	MAP_UNION_TOWER_TURRET("turret"),//联盟多功能防御塔
	MAP_UNION_TOWER_SATELLITE("satellite"),//联盟间谍卫星
	MAP_UNION_TOWER_DETECTOR("detector"),//联盟心灵探测仪
	MAP_UNION_TOWER_DELIVERY("delivery"),//联盟超时空传送阵
	MAP_UNION_TOWER_IRONCURTAIN("ironcurtain"),//联盟铁幕装置
	MAP_UNION_TOWER_SCIENCE("science"),//联盟科学研究所
	MAP_UNION_TOWER_WEATHER("weather"),//联盟气象中心
	MAP_UNION_TOWER_FOODRESEARCH("foodresearch"),//联盟食品研究所
	MAP_UNION_TOWER_METALRESEARCH("metalresearch"),//联盟金属研究所
	MAP_UNION_TOWER_OILRESEARCH("oilresearch"),//联盟石油研究所
	MAP_UNION_TOWER_ALLOYRESEARCH("alloyresearch"),//联盟太合金研究所
	MAP_UNION_TOWER_NUCLEARSILO("NuclearSilo"),//联盟核弹发射井
	MAP_UNION_CITY_NAME("city")//联盟npc城市
	;
	String key;
	private BuildName(String key){
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public static BuildName search(String key){
		BuildName[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			BuildName bn = datas[i];
			if (bn.key.equals(key)){
				return  bn;
			}
		}
		return null;
	}
	
	public static BuildName search(int index){
		BuildName[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			BuildName bn = datas[i];
			if (bn.ordinal() == index){
				return  bn;
			}
		}
		return null;
	}
	
}
