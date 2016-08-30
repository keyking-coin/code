package com.joymeng.slg.union;

import java.util.HashMap;
import java.util.Map;

public class UnionStaticInfo {
	long unionFight = 0;
	long killSoldNum = 0;
	Map<Integer, Integer> ocpCitysMap = new HashMap<Integer, Integer>();

	public UnionStaticInfo() {
	}
	
	public Map<Integer, Integer> getOcpCitysMap() {
		return ocpCitysMap;
	}
	public void setOcpCitysMap(Map<Integer, Integer> ocpCitysMap) {
		this.ocpCitysMap = ocpCitysMap;
	}
	public long getKillSoldNum() {
		return killSoldNum;
	}
	public void setKillSoldNum(long killSoldNum) {
		this.killSoldNum = killSoldNum;
	}
	public long getUnionFight() {
		return unionFight;
	}
	public void setUnionFight(long unionFight) {
		this.unionFight = unionFight;
	}
	
	public void addKillSoldNum(int num){
		killSoldNum += num;
	}
	public void updateUnionFight(int num){
		unionFight += num;
	}
	public int getCitysNumByLevel(int level){
		int num = 0;
		for(Map.Entry<Integer, Integer> mapSet : ocpCitysMap.entrySet()){
			if(mapSet.getKey() >= level){
				num += mapSet.getValue();
			}
		}
		return num;
	}
	
	public void updateOcpCitys(int level){
		synchronized (ocpCitysMap) {
			int newNum = 1;
			if (ocpCitysMap.get(level) != null) {
				newNum += ocpCitysMap.get(level);
			}
			ocpCitysMap.put(level, newNum);
		}
	}
	
//	public void deserialize(String str){
//		if(StringUtils.isNull(str)){
//			return;
//		}
////		Map<Integer,Object> map = (Map<Integer,Object>)JSON.parse(str);
////		Object obj = map.get("1");
////		if(obj != null){
////			killSoldNum = (long)obj;
////		}
////		obj = map.get(2);
////		if(obj != null){
////			unionFight = (long)obj;
////		}
////		obj = map.get(3);
////		if(obj != null){
////			ocpCitysMap = JSON.parseObject(obj.toString(),new TypeReference<Map<Integer,Integer>>(){});;
////		}
////		this = 
//	}
//	
//	public String serialize(){
////		Map<Integer,Object> datas = new HashMap<Integer, Object>();
////		datas.put(1, killSoldNum);
////		datas.put(2, unionFight);
////		datas.put(3, ocpCitysMap);
//		return JsonUtil.ObjectToJsonString(this);
//	}
}
