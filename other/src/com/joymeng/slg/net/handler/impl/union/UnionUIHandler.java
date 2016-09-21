package com.joymeng.slg.net.handler.impl.union;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.data.Alliance;
import com.joymeng.slg.union.data.Flageffects;
import com.joymeng.slg.union.data.UnionPostType;
import com.joymeng.slg.union.impl.UnionInviteInfo;
import com.joymeng.slg.union.impl.UnionInviteReport;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.GameConfig;

public class UnionUIHandler extends ServiceHandler {
	
	static final byte UNION_UI_UNION_LIST             = 0;//显示列表信息
	static final byte UNION_UI_UNION_SEARCH           = 1;//查找
	static final byte UNION_UI_UNION_JION             = 2;//申请加入
	static final byte UNION_UI_UNION_VERIFICATION     = 3;//验证消息
	static final byte UNION_UI_UNION_EXIT             = 4;//退出联盟
	static final byte UNION_UI_MEMBER_APPOINT         = 5;//官员任命
	static final byte UNION_UI_NAME_CHANGE            = 6;//联盟改名
	static final byte UNION_UI_RECRUIT_CHANGE         = 7;//招募条件修改
	static final byte UNION_UI_FLAG_CHANGE            = 8;//军旗修改
	static final byte UNION_UI_LEVEL_UP               = 9;//升级联盟
	static final byte UNION_UI_KICK_MEMBER            = 10;//踢出玩家
	static final byte UNION_UI_INVITE_MEMBER_LIST     = 11;//显示邀请加入列表
	static final byte UNION_UI_INVITE_MEMBER_SEARCH   = 12;//邀请查询
	static final byte UNION_UI_INVITE_MEMBER_IN       = 13;//邀请加入
	static final byte UNION_UI_ASSISTANCE_ADD         = 14;//联盟帮助添加
	static final byte UNION_UI_ASSISTANCE_HELP        = 15;//联盟帮助逻辑
	static final byte UNION_UI_TECH_DONATE			  = 16;//联盟科技捐赠
	static final byte UNION_UI_TECH_UPGRADE           = 17;//联盟科技升级 
	static final byte UNION_UI_CONVERT_GOODS		  = 18;//兑换联盟道具
	static final byte UNION_UI_BUY_GOODS			  = 19;//购买联盟仓库的物品
	static final byte UNION_UI_CHANGE_TITLE 				= 20;// 修改联盟的称谓
	static final byte UNION_UI_GET_UNIONMEMBER_BY_UNIONID 	= 21;// 根据unionId获取联盟成员信息
	static final byte UNION_UI_INVITE_MEMBER_IN_ACCEPT 		= 22;// 同意加入
	static final byte UNION_UI_ASSISTANCE_ALL_HELP 			= 23;// 联盟一键帮助逻辑
	static final byte UNION_UI_REMOVE_ITEM_TO_UNIONSTORAGE	= 25;//	出售物品到联盟商店
	static final byte UNION_UI_LOOK_MANOR_BUILD	= 26;//	查看联盟领地
	
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		byte type = in.get();
		params.put(type);//子指令编号
		switch(type){
		case UNION_UI_UNION_LIST:
		case UNION_UI_INVITE_MEMBER_LIST:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//本地已缓存的联盟编号
			params.put(in.getInt());//接下s来需要获得多少个数据 
			break;
		case UNION_UI_UNION_SEARCH:
		case UNION_UI_INVITE_MEMBER_SEARCH:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//要查找的联盟名称或者是编号
			break;
		case UNION_UI_UNION_JION:
		case UNION_UI_ASSISTANCE_HELP:
			params.put(in.getLong());//long 加入的联盟的编号
			break;
		case UNION_UI_ASSISTANCE_ALL_HELP:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//一键帮助的IDList
			break;
		case UNION_UI_UNION_VERIFICATION:
			params.put(in.get());//byte 0拒绝,1通过
			params.put(in.getLong());//long 操作的玩家编号
			break;
		case UNION_UI_MEMBER_APPOINT:
			params.put(in.getInt());//int 要任命到什么职位(联盟权限固化表的rank字段值)
			params.put(in.getLong());//long 要任命的玩家编号
			break;
		case UNION_UI_NAME_CHANGE:
			params.put(in.get());//byte 0简称,1全名
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//新名字
			break;
		case UNION_UI_RECRUIT_CHANGE:
		case UNION_UI_FLAG_CHANGE:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
			break;
		case UNION_UI_KICK_MEMBER:
		case UNION_UI_INVITE_MEMBER_IN:
			params.put(in.getLong());//long 要踢出的玩家编号
			break;
		case UNION_UI_INVITE_MEMBER_IN_ACCEPT :
			params.put(in.getInt()); //操作类型 0-同意 1-拒绝 
			params.put(in.getLong());
			params.put(in.getLong());
			break;
		case UNION_UI_ASSISTANCE_ADD:
			params.put(in.getInt());//int cityId;
			params.put(in.getLong());//long 帮组的建筑编号;
			break;
		case UNION_UI_TECH_DONATE:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//科技的Id
			params.put(in.getInt());										//捐献Id
			break;
		case UNION_UI_TECH_UPGRADE:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//科技的Id
			break;	
		case UNION_UI_CONVERT_GOODS:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//物品的Id
			params.put(in.getInt());                                       	//物品的数量
			break;
		case UNION_UI_BUY_GOODS:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//物品的Id
			params.put(in.getInt());                                       	//物品的数量
			break;
		case UNION_UI_CHANGE_TITLE:
			int size = in.getInt();		
			params.put(size);				//修改联盟称谓的个数
			for(int i=0; i< size; i++){
				params.put(in.getInt());										//rank
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//称谓
			}
			break;
		case UNION_UI_GET_UNIONMEMBER_BY_UNIONID:
			params.put(in.getLong());  //联盟Id
			break;
		case UNION_UI_UNION_EXIT : 
			break;
		case UNION_UI_REMOVE_ITEM_TO_UNIONSTORAGE:
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//物品的ItemId
			params.put(in.getInt());//物品出售的数量
			break;
		default:
			break;
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		byte type = params.get(0);
		resp.add(type);
		switch(type){
			case UNION_UI_UNION_LIST:{
				String ids      = params.get(1);
				int needBackNum = params.get(2);
				List<Long> unionIds = new ArrayList<Long>();
				if (!StringUtils.isNull(ids)){
					String[] ss = ids.split(",");
					for (int i = 0 ; i < ss.length ; i++){
						String s = ss[i];
						unionIds.add(Long.parseLong(s));
					}
				}
				if (role.getUnionId() != 0) {
					unionIds.add(role.getUnionId());
				}
				if (role.getCity(0) == null) {
					GameLog.error("getCity is fail , role.id = " + role.getId());
					resp.fail();
					return resp;
				}
				int myPosition = role.getCity(0).getPosition();
				List<UnionBody> unions = unionManager.search(myPosition,unionIds,needBackNum);
				resp.add(unions.size());
				for (int i = 0 ; i < unions.size() ; i++){
					UnionBody union = unions.get(i);
					union.listResp(resp);
				}
				break;
			}
			case UNION_UI_UNION_SEARCH:{
				String name = params.get(1);
				List<UnionBody> unions = unionManager.fuzzyBodySearch(name);
				resp.add(unions.size());
				for (int i = 0 ; i < unions.size() ; i++){
					UnionBody union = unions.get(i);
					union.listResp(resp);
				}
				if (unions.size() < 1) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_HAVE_NO_UNION);
				}
				break;
			}
			case UNION_UI_UNION_JION:{
				long unionId = params.get(1);
				UnionBody union = unionManager.search(role.getUnionId());
				if (!role.isCanJoinUnion()) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_DONT_JOIN_UNION_HAVE_CD);
					resp.fail();
					return resp;
				}
				if (union != null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_CREATE_ROLE_HAVE_IN);
					resp.fail();
					return resp;
				}
				union = unionManager.search(unionId);
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToApply(role,resp)){
					resp.fail();
					return resp;
				}
			    String parameter = role.getId()+"|"+"申请加入";
				LogManager.unionLog(role, union.getName(),EventName.applyJoinUnion.getName(),parameter);
				break;
			}
			case UNION_UI_UNION_VERIFICATION:{
				byte flag = params.get(1);
				long uid  = params.get(2);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToVerification(role,uid,flag)){
					resp.fail();
					return resp;
				}
				break;
			}
			case UNION_UI_UNION_EXIT:{
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					resp.fail();
					return resp;
				}
				if (union.checkLeader(role.getId())){
					if (union.getMembers().size() > 1){
						MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_DISSOLVE_FAIL);
						resp.fail();
						return resp;
					}
					String parameter = role.getId()+"|"+union.getName()+"|"+union.getShortName();
					LogManager.unionLog(role, union.getName(), EventName.dissolveUnion.getName(),parameter);
					union.dissolve();
				}else{
					if (!union.memberExit(role.getId())){
						resp.fail();
						return resp;
					}
					role.addJoinUnionTimer();// 主动退出增加加入所需要的倒计时
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms, role);
					String parameter = role.getId()+"|"+"自己退出";
					LogManager.unionLog(role, union.getName(), EventName.exitUnion.getName(),parameter);
				}
				break;
			}
			case UNION_UI_MEMBER_APPOINT: {
				int index = params.get(1);
				long uid = params.get(2);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToAppoint(role, uid, index)) {
					resp.fail();
					return resp;
				}
				Map<Integer, String> title = union.getUnionTitle();
				String parameter = role.getId()+"|"+uid+"|"+title.get(index);
				LogManager.unionLog(role, union.getName(), EventName.memberAppoint.getName(),parameter);
				break;
			}
			case UNION_UI_NAME_CHANGE:
			{				
				byte changeType = params.get(1);
				String name = params.get(2);
				if (changeType == 0) { //联盟简称
					if (StringUtils.countStringLength(name) != GameConfig.UNION_SHORTNAME_LIMIT) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_SHORTNAME_ILLEGALITY_LENGTH);
						resp.fail();
						return resp;
					}
					if (!nameManager.isNameCharLegal(name, GameConfig.REGEX_UPPER_LETTER_NUMBER)
							|| !nameManager.isNameLegal(name)) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE);
						resp.fail();
					}
					if (unionManager.searchShortName(name) != null) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_NAME_HAVE_USED, name);
						resp.fail();
						return resp;
					}
				}
				if (changeType == 1) {//联盟名称
					if (StringUtils.countStringLength(name) < GameConfig.UNION_NAME_MIN
							|| StringUtils.countStringLength(name) > GameConfig.UNION_NAME_MAX) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_ILLEGALITY_LENGTH);
						resp.fail();
						return resp;
					}
					if (!nameManager.isNameCharLegal(name, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
							|| !nameManager.isNameLegal(name)) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE);
						resp.fail();
					}
					if (unionManager.search(name) != null) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_NAME_HAVE_USED, name);
						resp.fail();
						return resp;
					}
				}
				if (changeType == 2) {//联盟宣言
					if (StringUtils.countStringLength(name) < GameConfig.UNION_NOTICE_MIN
							|| StringUtils.countStringLength(name) > GameConfig.UNION_NOTICE_MAX) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NOTICE_ILLEGALITY_LENGTH);
						resp.fail();
						return resp;
					}
					if (!nameManager.isNameLegal(name)) {
						MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOTICE_ILLEGALITY_SENSITIVE);
						resp.fail();
						return resp;
					}
				}
				if (changeType == 3) {//联盟宣言
					if (StringUtils.countStringLength(name) < GameConfig.UNION_NOTICE_MIN
							|| StringUtils.countStringLength(name) > GameConfig.UNION_NOTICE_MAX) {
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_IN_NOTICE_ILLEGALITY_LENGTH);
						resp.fail();
						return resp;
					}
					if (!nameManager.isNameLegal(name)) {
						MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_IN_NOTICE_ILLEGALITY_SENSITIVE);
						resp.fail();
						return resp;
					}
				}
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				String parameter =null;
				switch (changeType) {
				case 0:
				    parameter = union.getShortName()+"|"+name;
					LogManager.unionLog(role, union.getName(), EventName.changeUnionShort.getName(), parameter);
					break;
				case 1:
					parameter = union.getName()+"|"+name;
					LogManager.unionLog(role, union.getName(), EventName.changeUnionName.getName(), parameter);
					break;
				case 2:
					parameter = union.getNotice()+"|"+name;
					LogManager.unionLog(role, union.getName(), EventName.changeUnionDeclar.getName(), parameter);
					break;
				case 3:
					parameter = union.getInNotice()+"|"+name;
					LogManager.unionLog(role, union.getName(), EventName.changeUnionDeclar.getName(), parameter);
					break;
				default:
					break;
				}
				if (!union.tryToChangeName(role,changeType,name)){
					resp.fail();
				}
				break;
			}
			case UNION_UI_RECRUIT_CHANGE:
			{    
				String condition = params.get(1);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToChangeRecruit(role.getId(),condition)){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				if(union.getRecruits().equals("")){
					LogManager.unionLog(role, union.getName(), EventName.changeRecruit.getName(),"公开招募");
				}else{
					LogManager.unionLog(role, union.getName(), EventName.changeRecruit.getName(),"非公开招募");	
				}
				break;
			}
			case UNION_UI_FLAG_CHANGE:
			{
				boolean allow = true;
				String flagName = params.get(1);
				List<Flageffects> flageffects = dataManager.serachList(Flageffects.class, new SearchFilter<Flageffects>() {
					@Override
					public boolean filter(Flageffects data) {
						return (!data.getId().equals(""));
					}
				});
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.checkFlagName(flagName,flageffects)) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_FLAG_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (role.getMoney() < GameConfig.UNION_CHANGE_FLAG_PRICE) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NO_MONEY,GameConfig.UNION_CHANGE_FLAG_PRICE);
					resp.fail();
					return resp;
				}
				String flag = union.getIcon();
				if (!union.tryToChangeFlag(role,flagName)){
					allow = false;
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				if(allow){
					String parameter = flag+"|"+union.getIcon();
					LogManager.unionLog(role, union.getName(), EventName.changeFlag.getName(),parameter);	
				}
				break;
			}
			case UNION_UI_LEVEL_UP:
			{
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				List<Alliance> alliances = dataManager.serachList(Alliance.class);
				int maxLevel = 0;
				for (int i = 0 ; i < alliances.size() ; i++){
					Alliance alliance = alliances.get(i);
					if (alliance.getLevel() > maxLevel){
						maxLevel = alliance.getLevel();
					}
				}
				if (union.getLevel() == maxLevel){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_MAX_LEVEL);
					resp.fail();
					return resp;
				}
				if (!union.tryToLevelUp(role)){
					resp.fail();
					return resp;
				}
				LogManager.unionLog(role, union.getName(), EventName.levelUpUnion.getName(),String.valueOf(union.getLevel()));
				break;
			}
			case UNION_UI_KICK_MEMBER:
			{
				long memberId = params.get(1);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToKickMemeber(role,memberId)){
					resp.fail();
					return resp;
				}
				String parameter = memberId+"|"+"被踢出";
				LogManager.unionLog(role, union.getName(), EventName.kickMember.getName(),parameter);
				break;
			}
			case UNION_UI_INVITE_MEMBER_LIST:{
				String ids      = params.get(1);
				int needBackNum = params.get(2);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				List<Long> unionIds = new ArrayList<Long>();
				if (!StringUtils.isNull(ids)){
					String[] ss = ids.split(",");
					for (int i = 0 ; i < ss.length ; i++){
						String s = ss[i];
						unionIds.add(Long.parseLong(s));
					}
				}
				unionIds.addAll(union.getInvites());				
				unionIds.add(info.getUid());
				List<UnionInviteInfo> infos = unionManager.searchInvite(unionIds,needBackNum);
//				//TODO 提供客户端测试
//				List<UnionInviteInfo> result = new ArrayList<>();
//				if (!infos.isEmpty()) {
//					for (int i = 0; i < 10; i++) {
//						result.add(infos.get(0));
//					}
//				}				
				resp.add(infos);
				break;
			}
			case UNION_UI_INVITE_MEMBER_SEARCH:{
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				String name = params.get(1);
				List<UnionInviteInfo> infos = unionManager.fuzzyInviteSearch(name);
				List<UnionInviteInfo> result = new ArrayList<>();
				if (union.getInvites() == null) {
					result = infos;
				} else {
					for (int i = 0 ; i < infos.size() ; i++){
						UnionInviteInfo unionInviteInfo = infos.get(i);
						int temp = 0;
						for (int j = 0 ; j < union.getInvites().size() ; j++){
							Long uid = union.getInvites().get(j);
							if (uid == unionInviteInfo.getUid()) {
								temp = 1;
							}
						}
						if (temp == 0) {
							result.add(unionInviteInfo);
						}
					}
				}
				resp.add(result);
				if (result.size() < 1) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_NO_SEARCH_MEMBER);
				}
				break;
			}
			case UNION_UI_INVITE_MEMBER_IN:{
				long uid = params.get(1);	
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (union.isFull()) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_MEMBER_FULL);
					resp.fail();
					return resp;
				}
				UnionMember operator = union.searchMember(role.getId());
				if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_INVITE)){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				Role other = world.getRole(uid);
				if (other == null) {
					resp.fail();
					return resp;
				}
				if (unionManager.search(other.getUnionId()) != null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_MEMBER_HAVE_IN_OTHER);
					resp.fail();
					return resp;
				}
				if (union.getInvites().contains(uid)) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_HAS_INVITED_MEMBER);
					resp.fail();
					return resp;
				}
				UnionInviteReport unionInviteReport = new UnionInviteReport(role,union);				
				String unionInvite  = JsonUtil.ObjectToJsonString(unionInviteReport);	//用户UID
				chatMgr.creatBattleReportAndSend(unionInvite,ReportType.TYPE_UNION_INVITE,null,other);
				union.addInvitesList(uid);
				String parameter = role.getId()+"|"+uid;
				LogManager.unionLog(role, union.getName(), EventName.inviteMemberIn.getName(),parameter);
				NewLogManager.unionLog(role, "alliance_invite",uid);
				break;
			}
			case UNION_UI_INVITE_MEMBER_IN_ACCEPT :{
				int acceptType = params.get(1);
				long inviteUid = params.get(2);
				long unionId = params.get(3); // 同意加入的联盟Id
				RespModuleSet rms = new RespModuleSet();
				if (!role.isCanJoinUnion()) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_DONT_JOIN_UNION_HAVE_CD);
					resp.fail();
					return resp;
				}
				if (unionManager.search(role.getUnionId()) != null) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_JOINED_IN_UNION);
					resp.fail();
					return resp;
				}
				UnionBody union = unionManager.search(unionId);
				if (union == null) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (union.isFull()) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_MEMBER_FULL);
					resp.fail();
					return resp;
				}
				if(!union.getInvites().contains(role.getId())){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_INVITE_HAVE_OPERATED);
					resp.fail();
					return resp;
				}
				if (acceptType == 0) {
					if (!union.tryToAddmember(rms, role, union.computeMilitary(0))) {
						resp.fail();
						return resp;
					}
					union.redInvitesList(role.getId());
					if (role.isOnline()) {
						MessageSendUtil.tipModule(rms, MessageSendUtil.TIP_TYPE_NORMAL, I18nGreeting.MSG_UNION_NEW_MEMBER_IN, "(" + union.getShortName() + ")" + union.getName());
						MessageSendUtil.sendModule(rms, role.getUserInfo());
					}
				} else if (acceptType == 1){
					Role inviteRole = world.getRole(inviteUid);
					if (inviteRole != null && inviteRole.isOnline()) {	//TODO 后期可以添加为通知
						MessageSendUtil.sendNormalTip(inviteRole.getUserInfo(),I18nGreeting.MSG_OTHER_ROLE_REFUSE_INVITE,role.getName());
					}
					union.redInvitesList(role.getId());
				}
				String parameter = role.getId()+"|"+inviteUid;
				LogManager.unionLog(role, union.getName(), EventName.acceptJionIn.getName(),parameter);
				break;
			}
			case UNION_UI_ASSISTANCE_ADD:{
				int cityId   = params.get(1);
				long buildId = params.get(2);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MEMBER_NO_ASSISTANCE_PERMISSIONS);
					resp.fail();
					return resp;
				}
				RoleCityAgent city = role.getCity(cityId);
				if (city == null){
					GameLog.error("union help error cityId = " + cityId);
					resp.fail();
					return resp;
				}
				RoleBuild build = city.searchBuildById(buildId);
				if (build == null){
					GameLog.error("union help error buildId = " + buildId);
					resp.fail();
					return resp;
				}
				if (!union.tryToAddAssistance(role,city,build)){
					resp.fail();
					return resp;
				}
				String parameter = role.getId() + "|" + build.getBuildId();
				LogManager.unionLog(role, union.getName(), EventName.addAssistance.getName(), parameter);
				try {
					TimerLast timer = build.getBuildTimer();
					switch (timer.getType()) {
					case TIME_CREATE:
						NewLogManager.buildLog(role, "ask_help_for_build",build.getBuildId());
						break;
					case TIME_LEVEL_UP:
						NewLogManager.buildLog(role, "ask_help_for_upgrade",build.getBuildId());
						break;
					case TIME_RESEARCH:
						NewLogManager.buildLog(role, "ask_help_for_upgrade",build.getBuildId());
						break;
					default:
						break;
					}
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
				break;
			}
			case UNION_UI_ASSISTANCE_HELP:{
				long assistanceid  = params.get(1);
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToDoAssistance(role,assistanceid)){
					resp.fail();
					return resp;
				}
				break;
			}
			case UNION_UI_ASSISTANCE_ALL_HELP:{
				String string = params.get(1);
				List<Long> assistanceIds = new ArrayList<Long>();
				String[] strings = string.split(",");
				for (int i = 0 ; i < strings.length ; i++){
					String tempId = strings[i];
					assistanceIds.add(Long.valueOf(tempId));
				}
				UnionBody union = unionManager.search(role.getUnionId());
				if (union == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!union.tryToDoAssistanceList(role,assistanceIds)){
					resp.fail();
					return resp;
				}
				break;
			}
			case UNION_UI_TECH_DONATE:{
				String unionTechId =params.get(1);
				int donateId = params.get(2);
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!unionBody.unionDonate(role, unionTechId, donateId)) {
					resp.fail();
					return resp;
				}
				String parameter = role.getId()+"|"+unionTechId;
				LogManager.unionLog(role, unionBody.getName(),EventName.techDonate.getName(),parameter);
				//任务事件
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ALLI_JX, 0);
				break;
			}
			case UNION_UI_TECH_UPGRADE:{
				String unionTechId =params.get(1);
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!unionBody.UnionTechUpgrade(role, unionTechId)) {
					resp.fail();
					return resp;
				}
				String parameter = role.getId()+"|"+unionTechId;
				LogManager.unionLog(role, unionBody.getName(), EventName.techUpgrade.getName(),parameter);
				break;
			}
			case UNION_UI_CONVERT_GOODS:{
				String itemId = params.get(1);
				int num = params.get(2);
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!unionBody.convertUnionGoods(role, itemId, num)) {
					resp.fail();
					return resp;
				}
				String parameter = role.getId()+"|"+itemId;
				LogManager.unionLog(role, unionBody.getName(), EventName.convertGoods.getName(),parameter);
			    NewLogManager.unionLog(role, "alliance_shop_put", itemId, num);
				break;
			}
			case UNION_UI_BUY_GOODS:{
				String itemId = params.get(1);
				int num = params.get(2);
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if (!unionBody.buyUnionGoods(role, itemId, num)) {
					resp.fail();
					return resp;
				}
				String parameter = role.getId()+"|"+itemId+num;
				LogManager.unionLog(role, unionBody.getName(), EventName.buyGoods.getName(),parameter);
				NewLogManager.unionLog(role, "alliance_shop_buy",itemId,num);
				break;
			}
			case UNION_UI_CHANGE_TITLE:{
				int size = params.get(1);
				Map<Integer, String> changeTitle = new HashMap<Integer, String>();
				for (int i = 0, index = 2; i < size; ++i) {
					int rank = params.get(index++);
					String title = params.get(index++);
					changeTitle.put(rank, title);
				}
				if (role.getMoney() < GameConfig.CHANGE_UNION_TITLE_PRICE) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NO_MONEY,GameConfig.CHANGE_UNION_TITLE_PRICE);
					resp.fail();
					return resp;
				}
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				if(!unionBody.changeUnionTitle(role, changeTitle)){
					resp.fail();
					return resp;
				}
				if(!role.redRoleMoney(GameConfig.CHANGE_UNION_TITLE_PRICE)){
					resp.fail();
					return resp;
				}
				RespModuleSet rms = new RespModuleSet();
				role.sendRoleToClient(rms);
				MessageSendUtil.sendModule(rms, role);
                for(String str:changeTitle.values()){
    				LogManager.unionLog(role, unionBody.getName(), EventName.changeFlag.getName(),str);
                }
				break;
			}
			case UNION_UI_GET_UNIONMEMBER_BY_UNIONID:{
				long unionId = params.get(1);
				UnionBody unionBody = unionManager.search(unionId);
				if (unionBody != null && unionBody.getUnionTitle().size() > 0) {
					resp.add(unionBody.getLevel());//int 联盟的等级
					for (int i = 1; i <= 4; i++) {
						if (StringUtils.isNull(unionBody.getUnionTitle().get(i))) {
							continue;
						}
						resp.add(unionBody.getUnionTitle().get(i));//String "0/1|***" 0:固化数据(读取StringContent) 1:用户自定义
					}
					List<UnionMember> unionMembers = unionBody.getMembers();
					resp.add(unionMembers);
				}
				break;
			}	
			case UNION_UI_REMOVE_ITEM_TO_UNIONSTORAGE:{
				String itemId = params.get(1);
				int num = params.get(2);
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
//				if (unionBody.searchUnionBuildLevel(BuildName.MAP_UNION_STORAGE.getKey()) < 1) {
//					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_STORAGE_NOT_FIND);
//					resp.fail();
//					return resp;
//				}
				if(!unionBody.removeItemToUnionStorage(role,itemId,num)){
					resp.fail();
					return resp;
				}
			    String parameter = role.getId() + "|" + itemId + num;
				LogManager.unionLog(role, unionBody.getName(), EventName.removeItemTo.getName(),parameter);
				break;
			}
			case UNION_UI_LOOK_MANOR_BUILD:{
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody == null){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
					resp.fail();
					return resp;
				}
				List<MapUnionCity> ucs = mapWorld.searchUnionCity(unionBody.getId());
				resp.add(ucs.size());
				for (int i = 0 ; i < ucs.size() ; i++){
					MapUnionCity uc = ucs.get(i);
					List<Integer> builds = uc.getBuilds();
					resp.add(uc.getPosition());
					resp.add(uc.getKey());
					resp.add(uc.getState());
					resp.add(builds.size());
					for (int j = 0 ; j < builds.size() ; j++){
						Integer bp = builds.get(j);
						MapUnionBuild unionBuild = mapWorld.searchObject(bp.intValue());
						resp.add(unionBuild.getLevel());
						resp.add(unionBuild.getBuildKey());
						resp.add(bp.intValue());
						resp.add(unionBuild.getState());
						TimerLast timer = unionBuild.getBuildTimer();
						if (timer != null){
							resp.add(1);
							resp.add(timer);
						}else{
							resp.add(0);
						}
					}
				}
				break;
			}
		}
		return resp;
	}
}
