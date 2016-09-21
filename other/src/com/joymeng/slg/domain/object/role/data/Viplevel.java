package com.joymeng.slg.domain.object.role.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Viplevel implements DataKey, Comparable<Viplevel>{
	String id; //id
	int vipLevel; //等级
	int exp; //经验值
	String name;
	String description;
	float FoodProd;//提升食品厂产量
	float MetalProd;//提升
	float BuildSpeed;//提升建造速度
	float ResSpeed;//提升研究速度
	int phybuynumber;//体力购买次数增加
	int SProdLimit;//全兵种训练量
	float ImpMobi;//全兵种机动力
	float ImpVitSp;//提升体力恢复速度
	float ImpColl;//提升采集速度
	float ImpOilProd;//提升炼油厂产量
	float ImpAtk;//全兵种火力
	float ImpDef;//全兵种防御力
	float ReduBearDMGAll;//全兵种承受伤害降低
	float ImpAlloyProd;//提升合金厂产量
	int TroopsLimit;//增加出征队伍数量
	float SoldLimit;//增加出征队伍空间
	float ReduProdTime;//减少部队生产时间
	float FreeBuildSpeed;//增加建造免费加速时间
	float FreeResearchSpeed;//增加研究免费加速时间	
	float ReduHospTime;//伤兵医疗加速
	float ReduRepaTime;//机械维修加速
	float ReduHospRes;//降低治疗伤兵的资源
	float ReduRepaRes;//降低维修机械的资源
	float AddHospCapa;//增加医院伤兵总容量
	float AddRepaCapa;//增加受损机械总容量
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getFoodProd() {
		return FoodProd;
	}
	public void setFoodProd(float foodProd) {
		FoodProd = foodProd;
	}
	public float getMetalProd() {
		return MetalProd;
	}
	public void setMetalProd(float metalProd) {
		MetalProd = metalProd;
	}
	public float getBuildSpeed() {
		return BuildSpeed;
	}
	public void setBuildSpeed(float buildSpeed) {
		BuildSpeed = buildSpeed;
	}
	public float getResSpeed() {
		return ResSpeed;
	}
	public void setResSpeed(float resSpeed) {
		ResSpeed = resSpeed;
	}
	public int getPhybuynumber() {
		return phybuynumber;
	}
	public void setPhybuynumber(int phybuynumber) {
		this.phybuynumber = phybuynumber;
	}
	public int getSProdLimit() {
		return SProdLimit;
	}
	public void setSProdLimit(int sProdLimit) {
		SProdLimit = sProdLimit;
	}
	public float getImpMobi() {
		return ImpMobi;
	}
	public void setImpMobi(float impMobi) {
		ImpMobi = impMobi;
	}
	public float getImpVitSp() {
		return ImpVitSp;
	}
	public void setImpVitSp(float impVitSp) {
		ImpVitSp = impVitSp;
	}
	public float getImpColl() {
		return ImpColl;
	}
	public void setImpColl(float impColl) {
		ImpColl = impColl;
	}
	public float getImpOilProd() {
		return ImpOilProd;
	}
	public void setImpOilProd(float impOilProd) {
		ImpOilProd = impOilProd;
	}
	public float getImpAtk() {
		return ImpAtk;
	}
	public void setImpAtk(float impAtk) {
		ImpAtk = impAtk;
	}
	public float getImpDef() {
		return ImpDef;
	}
	public void setImpDef(float impDef) {
		ImpDef = impDef;
	}
	public float getReduBearDMGAll() {
		return ReduBearDMGAll;
	}
	public void setReduBearDMGAll(float reduBearDMGAll) {
		ReduBearDMGAll = reduBearDMGAll;
	}
	public float getImpAlloyProd() {
		return ImpAlloyProd;
	}
	public void setImpAlloyProd(float impAlloyProd) {
		ImpAlloyProd = impAlloyProd;
	}
	public int getTroopsLimit() {
		return TroopsLimit;
	}
	public void setTroopsLimit(int troopsLimit) {
		TroopsLimit = troopsLimit;
	}
	public float getSoldLimit() {
		return SoldLimit;
	}
	public void setSoldLimit(float soldLimit) {
		SoldLimit = soldLimit;
	}
	public float getReduProdTime() {
		return ReduProdTime;
	}
	public void setReduProdTime(float reduProdTime) {
		ReduProdTime = reduProdTime;
	}
	
	public float getFreeBuildSpeed() {
		return FreeBuildSpeed;
	}
	public void setFreeBuildSpeed(float freeBuildSpeed) {
		FreeBuildSpeed = freeBuildSpeed;
	}
	public float getFreeResearchSpeed() {
		return FreeResearchSpeed;
	}
	public void setFreeResearchSpeed(float freeResearchSpeed) {
		FreeResearchSpeed = freeResearchSpeed;
	}
	public float getReduHospTime() {
		return ReduHospTime;
	}
	public void setReduHospTime(float reduHospTime) {
		ReduHospTime = reduHospTime;
	}
	public float getReduRepaTime() {
		return ReduRepaTime;
	}
	public void setReduRepaTime(float reduRepaTime) {
		ReduRepaTime = reduRepaTime;
	}
	public float getReduHospRes() {
		return ReduHospRes;
	}
	public void setReduHospRes(float reduHospRes) {
		ReduHospRes = reduHospRes;
	}
	public float getReduRepaRes() {
		return ReduRepaRes;
	}
	public void setReduRepaRes(float reduRepaRes) {
		ReduRepaRes = reduRepaRes;
	}
	public float getAddHospCapa() {
		return AddHospCapa;
	}
	public void setAddHospCapa(float addHospCapa) {
		AddHospCapa = addHospCapa;
	}
	public float getAddRepaCapa() {
		return AddRepaCapa;
	}
	public void setAddRepaCapa(float addRepaCapa) {
		AddRepaCapa = addRepaCapa;
	}
	@Override
	public Object key() {
		return vipLevel;
	}
	@Override
	public int compareTo(Viplevel o) {
		return o.vipLevel - vipLevel;
	}
	
	
}
