package com.joymeng.list;

public enum EventName {
	HttpGmRoleMgr("红警后台"), 
	addResourceToCity("部队战斗收获"),           
	useItem( "背包使用道具"),                    
	getHonorReward( "荣誉墙奖励"),               
	getAwordFromMission("任务奖励"),           
	ModifyBasicInfo("Gm后台操作"), 
	completeCreate("地图要塞完成建造"),          
	resetArmysPoints("重置士兵技能点"),          
	secondKill("金币加速完成"),                  
	BuildLevelup("升级建筑"),                 
	createBuild("建造建筑"),                  
	removeBuild("拆除建筑"),                  
	trainArmy("训练士兵"),                    
	cureArmys("治疗伤兵"),                   
	repairDefenseArmys("维修防御设施"),        
	buyItemSize("矿石精炼厂购买格子"),           
	upgradeTech("升级科技"),                  
	resetSkills("重置技能"),                 
	tryToBuySomeThing("商城购买"),            
	UnlockLandIdHandler("解锁地块"), 
	UnionCreateHandler("新建联盟"), 
	tryToChangeFlag("修改联盟旗帜"),           
	unionDonate("联盟科技捐献"),               
	packageBack("出征部队收获"),              
	troopsArrive("部队到达"),                
	disMissArmy("解散士兵"),                 
	equipDecompose("装备分解"),              
	materialSynthesis("材料合成"),           
	cancelBuildLevelup("取消建筑升级"),       
	cancelCreateBuild("取消建造建筑"),        
	cancelTrainArmy("取消训练士兵"),          
	EquipUpgradeOver("装备升级完成"),         
	getGems("收获宝石"),                     
	collectResource("收集资源"),             
	cancelUpgradeTech("取消升级科技"),       
	getDailyReward("在线奖励"),            
	sudokuOpen("翻开九宫格"),                 
	roleSevenSignIn("7天连续签到"),           
	roleThirtySignIn("30天累计签到"),         
	ModifyAddItem("gm后台添加物品"), 
	ModifyPlayerItem("gm后台修改物品"), 
	StartTurntableHandler("使用大转盘"), 
	buyUnionGoods("购买联盟商品"),           
	equipRefine("装备炼化"),                
	getOutlineConsumption("离线粮食消耗"),   
	grainConsumption("部队10分钟消耗"),      
	redCostResource("资源相关消耗"),         
	upgradeEquipment("升级装备"),           
	tryToBuyCell("玩家黑市"), 
	tryToRefresh("黑市商品刷新"),
	creatUnion("创建联盟"),
	applyJoinUnion("申请加入联盟"),
	exitUnion("退出联盟"),
	memberAppoint("官员任命"),
	changeUnionName("修改联盟名称"),
	changeRecruit("招募条件修改"),
	changeFlag("军旗修改"),
	levelUpUnion("升级联盟"),
	kickMember("踢出联盟成员"),
	invitMemberSearch("邀请查询"),
	inviteMemberIn("邀请加入"),
	addAssistance("添加联盟帮助"),
	techDonate("联盟科技捐献"),
	techUpgrade("联盟科技升级"),
	convertGoods("兑换联盟物品"),
	buyGoods("购买联盟仓库物品"),
	changeTitle("修改联盟称谓"),
	acceptJionIn("同意加入"),
	removeItemTo("出售物品到联盟商店"),
	changeUnionShort("修改联盟简称"),
	changeUnionDeclar("修改联盟宣言"),
	dissolveUnion("解散联盟"),
	buildFortress("建造要塞"),
	buildCity("建造迁城点"),
	others("部队出征"),
	garrison("驻防"),
	spy("侦查"),
	allocation("调拨"),
	aggregation("集结"),
	toAggregation("去集结"),
	collection("采集"),
	getAwardDailyTask("日常任务奖励"),
	_buyOk("充值购买"),
	toEctype("去副本"),
	arriveAtGarrisonPoint("到达驻防点"),
	InvestCompletion("侦查完成"),
	endOfBattle("战斗结束"),
	startCollecting("开始采集"),
	endOfCollection("采集完成"),
	startBuildFortres("开始建造要塞"),
	startBuildCity("开始建造迁城点"),
	moveCityComplete("迁城完成"),
	buildFortComplete("建造要塞完成"),
	withdrawalForce("撤退部队"),
	investConsumption("侦查消耗"),
	startFighting("开始战斗"),
	creatUnionBuild("建造联盟建筑"),
	levelUpunionBuild("升级联盟建筑"),
	removeUnionBuild("拆除联盟建筑"),
	cRemoveUnionBuild("取消拆除联盟建筑"),
	ruinsHarvest("废墟通关奖励"),
	roleTradeCityResource("资源交易"),
	fortressAnd("建造要塞、迁城点"),
	FortressLevelUp("要塞升级"),
	MailItemsHandler("领取邮件附件"),
	Garrison("行军转固定"),
	Expedite("固定转行军"),
	BackCity("部队回城"),
	BackFortress("部队会要塞")
	;
	private String name;

	EventName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}