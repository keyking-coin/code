package com.joymeng.slg.domain.object.task;

import com.joymeng.Const;

public class RoleTaskType {
	
	public enum TaskConditionType {
		C_BD_LVL(1, Const.TASK_COND_TYPE_MAX),//1	某个内城建筑升到多少级
		C_BD_NUM(2, Const.TASK_COND_TYPE_MAX),//2	建造/升级某个建筑多少个/多少级
		C_TECH_LVL(3, Const.TASK_COND_TYPE_MAX),//3	研究某个科技达到多少级
		C_RS_MAX(4, Const.TASK_COND_TYPE_MAX),//4	研究X个科技到最高级
		C_RS_CNT(5, Const.TASK_COND_TYPE_TIMES),//5	科技研究成功累计多少次
		C_RL_SKL(6, Const.TASK_COND_TYPE_MAX),//6	升级成功某个主角技能
		C_SOLD_ULK(7, Const.TASK_COND_TYPE_MAX),//7	兵种科技树解锁某个兵种
		C_S_TRN_S(8, Const.TASK_COND_TYPE_TIMES),//8	某子类兵训练/生产多少个
		C_S_TRN_B(9, Const.TASK_COND_TYPE_TIMES),//9	某大类兵训练/生产多少个
		C_S_TRN_A(10, Const.TASK_COND_TYPE_TIMES),//10	所有兵训练/生产多少个
		C_S_CUE_T(11, Const.TASK_COND_TYPE_TIMES),//11	某类兵治疗/维修多少个
		C_S_CUE(12, Const.TASK_COND_TYPE_TIMES),//12	治疗成功多少士兵
		C_S_REP(13, Const.TASK_COND_TYPE_TIMES),//13	维修成功多少机械
//		C_S_TRP_T(14, Const.TASK_COND_TYPE_MAX),//14	建造某类陷阱多少个
//		C_S_TRP(15, Const.TASK_COND_TYPE_MAX),//15	建造所有陷阱多少个
		C_RESS_OTP(16, Const.TASK_COND_TYPE_MAX),//	16	某种资源当前产量达到多少/小时
		C_RESS_HAT(17, Const.TASK_COND_TYPE_TIMES),//17	收集城内建筑的某种资源达到多少
		C_RESS_CLT(18, Const.TASK_COND_TYPE_TIMES),//18	采集某种资源达到多少
		C_RESS_ROB(19, Const.TASK_COND_TYPE_TIMES),//19	掠夺某种资源达到多少
		C_ATK_WIN(20, Const.TASK_COND_TYPE_TIMES),//20	自己进攻玩家城市的战斗中胜利多少次
		C_DEF_WIN(21, Const.TASK_COND_TYPE_TIMES),//21	自己被玩家攻击的防御战中胜利多少次
		C_FIT_MST_T(22, Const.TASK_COND_TYPE_TIMES),//22 自己击杀某个怪物多少次
		C_FIT_MST(23, Const.TASK_COND_TYPE_TIMES),//23	自己击杀所有怪物次数
		C_ATK_S_NUM(24, Const.TASK_COND_TYPE_TIMES),//24    自己击杀所有玩家士兵的总数
		C_OCP_RES_T(25, Const.TASK_COND_TYPE_MAX),//25	占领某类某级以上的资源地块X个
		C_OCP_RES_A(26, Const.TASK_COND_TYPE_MAX),//26	占领资源地块总数达到X个
		C_FORT_NUM(27, Const.TASK_COND_TYPE_MAX),//27	自己拥有X个要塞
		C_ROLE_LVL(28, Const.TASK_COND_TYPE_MAX),//28	指挥官达到多少级
		C_VIP_LVL(29, Const.TASK_COND_TYPE_MAX),//29	指挥官VIP达到多少级
		C_RL_FF_A(30, Const.TASK_COND_TYPE_MAX),//30	个人部队战斗力达到多少
		C_RL_FF(31, Const.TASK_COND_TYPE_MAX),//31	个人总战斗力达到多少
		C_S_NUM_T(32, Const.TASK_COND_TYPE_MAX),//32	当前拥有某种类型的兵多少个
		
//		C_MS_WIN(33, Const.TASK_COND_TYPE_TIMES),//33	个人发起集结进攻并胜利1次
//		C_HLP_DEF(34, Const.TASK_COND_TYPE_TIMES),//34	帮助盟友驻防并胜利1次
//		C_HLP_MS_WIN(35, Const.TASK_COND_TYPE_TIMES),//35	帮助盟友进行集结战斗并胜利1次
		
		//集结进攻胜利35  驻防战斗胜利34
		C_HLP_MS_WIN(33, Const.TASK_COND_TYPE_TIMES),//33	帮助盟友进行集结战斗并胜利1次   不存在
		C_MS_WIN(35, Const.TASK_COND_TYPE_TIMES),//35	个人发起集结进攻并胜利1次
		C_HLP_DEF(34, Const.TASK_COND_TYPE_TIMES),//34	帮助盟友驻防并胜利1次
				
		C_HLP_KIL_NUM(36, Const.TASK_COND_TYPE_TIMES),//36	自己驻守的部队消灭敌军总数多少个
		C_MS_KIL_NUM(37, Const.TASK_COND_TYPE_TIMES),//37	自己集结的部队消灭敌军总数多少个
		C_HLP_ALLI(38, Const.TASK_COND_TYPE_TIMES),//38	帮助其它联盟成员进行加速X次
		C_UNLOK_BLD(39, Const.TASK_COND_TYPE_MAX),//39	解锁X块外城区域
		C_EQP_GAN_T(40, Const.TASK_COND_TYPE_TIMES),//40	获得某个品质的装备X件
		C_EQP_GAN_ALL(41, Const.TASK_COND_TYPE_TIMES),//41	获得多少件装备
		C_EQP_DRS_SUT(42, Const.TASK_COND_TYPE_MAX),//42	穿戴X件某个品质的装备
		C_EQP_UP_TM(43, Const.TASK_COND_TYPE_TIMES),//43	升级多少件装备
		C_EQP_REF_TM(44, Const.TASK_COND_TYPE_TIMES),//44	炼化多少次装备
		C_EQP_DCP_TM(45, Const.TASK_COND_TYPE_TIMES),//45	分解多少件装备
		C_MTL_PRD_NUM(46, Const.TASK_COND_TYPE_TIMES),//46	生产多少个材料
		C_A_LVL(47, Const.TASK_COND_TYPE_UNION),//47	联盟升级到X级
		C_A_MEM_NUM(48, Const.TASK_COND_TYPE_UNION),//48	联盟成员数量达到X个
		C_A_BUD_NUM(49, Const.TASK_COND_TYPE_UNION),//49	联盟拥有X座某个建筑物
		C_A_TECH_LVL(50, Const.TASK_COND_TYPE_UNION),//50	联盟某个科技升级到某级
		C_A_OCP_CT_CNT(51, Const.TASK_COND_TYPE_UNION),//51	联盟占领过某级以上的城市多少座
		C_A_FIT_FOC(52, Const.TASK_COND_TYPE_UNION),//52	联盟总战斗力达到多少
		C_A_KIL_NUM(53, Const.TASK_COND_TYPE_UNION),//53	联盟总击杀士兵达到多少数量
		C_CN_OCP_NUM_T(54, Const.TASK_COND_TYPE_COUNTRY),//54	王国内某级以上资源地被占领达到多少块
		C_CN_OCP_NUM(55, Const.TASK_COND_TYPE_COUNTRY),//55	王国内资源地总共被占领达到多少块
		C_CN_ALLI_NUM(56, Const.TASK_COND_TYPE_COUNTRY),//56	王国内人数超过50人的联盟达到X个
		C_CN_KIL_NUM(57, Const.TASK_COND_TYPE_COUNTRY),//57	王国内所有玩家共击杀某个怪X个
		C_CN_OCP_C_NUM_T(58, Const.TASK_COND_TYPE_COUNTRY),//58	王国内某级以上城市被占领多少个
		C_CN_OCP_C_NUM(59, Const.TASK_COND_TYPE_COUNTRY),//	59	王国内城市总计被占领多少个
		C_CN_FF_ANUM(60, Const.TASK_COND_TYPE_COUNTRY),//60	王国内联盟战斗力超过多少X的联盟达到多少个
		C_ALLI_HN(61, Const.TASK_COND_TYPE_TIMES),//61	获得X点联盟个人荣誉度
		C_SPY_NUM(62, Const.TASK_COND_TYPE_TIMES),//62	成功侦查X次
		C_RL_AL_POS(63, Const.TASK_COND_TYPE_UNION),//63	在联盟中担任某个职位
		C_RL_CN_POS(64, Const.TASK_COND_TYPE_COUNTRY),//64	在国家中担任某个职位
		C_BD_LVL_NUM(65, Const.TASK_COND_TYPE_MAX),//65	拥有X级的建筑物X个
		C_BD_FT_NUM(66, Const.TASK_COND_TYPE_TIMES),//66 累计建造过X座要塞
		C_S_TRN_ID(67, Const.TASK_COND_TYPE_TIMES),//67	某个ID的士兵训练/生产多少个
		C_KIL_MS_NUM_T(68, Const.TASK_COND_TYPE_TIMES),//68	自己击杀某类怪物兵种多少个
		C_KIL_MS_NUM(69, Const.TASK_COND_TYPE_TIMES),//	69	自己击杀所有怪物兵多少个
		C_CN_ALL_KIL_NUM(70, Const.TASK_COND_TYPE_COUNTRY),//	70	王国内所有玩家共击杀所有怪物兵多少个
		
		C_SIGN_CNT(100,Const.TASK_COND_TYPE_TIMES),		//100	30日签到
		C_ONLINE_CNT(101,Const.TASK_COND_TYPE_TIMES),		//101	领取在线奖励
		C_LOGIN_CNT(102,Const.TASK_COND_TYPE_TIMES),		//102	领取连续登陆奖励
		C_LUCKY_CNT(103,Const.TASK_COND_TYPE_TIMES),		//103	幸运转盘
		C_ITEM_USE_ID(104,Const.TASK_COND_TYPE_TIMES),		//104	使用道具
		C_ACC_TRAIN(105,Const.TASK_COND_TYPE_TIMES),		//105	训练生产加速
		C_ACC_CURE(106,Const.TASK_COND_TYPE_TIMES),		//106	治疗维修加速
		C_BUILD_LVL(107,Const.TASK_COND_TYPE_TIMES),		//107	升级建筑
		C_RESEARCH(108,Const.TASK_COND_TYPE_TIMES),		//108	研究科技
		C_ACC_BUILD(109,Const.TASK_COND_TYPE_TIMES),//109	建筑升级加速
		C_ACC_TECH(110,Const.TASK_COND_TYPE_TIMES),	//110	科技研究加速
		C_ALLI_JX(111,Const.TASK_COND_TYPE_TIMES),	//111	联盟科技捐献
		C_ALLI_LB(112,Const.TASK_COND_TYPE_TIMES),	//112	领取联盟礼包
		C_ALLI_PS(113,Const.TASK_COND_TYPE_TIMES),	//113	参加联盟跑商
		C_BUY_MARK(114,Const.TASK_COND_TYPE_TIMES),	//114	购买黑市商品
		C_MTL_SYNTH(115,Const.TASK_COND_TYPE_TIMES),	//115	合成材料
		C_CHAT_WORLD(116,Const.TASK_COND_TYPE_TIMES),	//116	世界聊天
		C_VIP_ACTIVE(117,Const.TASK_COND_TYPE_TIMES),	//117	激活VIP
		C_BUY_ITEM(118,Const.TASK_COND_TYPE_TIMES),	//118	购买道具
		C_RECHARGE(119,Const.TASK_COND_TYPE_TIMES),	//119	充值
		C_CARBON_N(120,Const.TASK_COND_TYPE_TIMES),	//120	通过普通副本
		C_CARBON_H(121,Const.TASK_COND_TYPE_TIMES),	//121	通关困难副本
		C_USE_ITEM_T(122,Const.TASK_COND_TYPE_TIMES),	//122	使用某类道具
		;

		int key;
		byte type;
		private TaskConditionType(int key, byte type){
			this.key = key;
			this.type = type;
		}

		public int getKey() {
			return key;
		}
		public void setKey(int key) {
			this.key = key;
		}
		public byte getType() {
			return type;
		}
		public void setType(byte type) {
			this.type = type;
		}
		
		public static TaskConditionType valueof(int key){
			TaskConditionType[] datas = values();
			for (int i = 0 ; i < datas.length ; i++){
				TaskConditionType mType = datas[i];
				if(mType.key == key){
					return mType;
				}
			}
			return null;
		}
	}
}
