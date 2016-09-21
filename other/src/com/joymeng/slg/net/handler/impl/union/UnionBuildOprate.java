package com.joymeng.slg.net.handler.impl.union;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.common.util.expression.ProtoExpression;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.data.Npccity;
import com.joymeng.slg.domain.map.data.Worldbuilding;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionResource;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.data.UnionPostType;
import com.joymeng.slg.union.impl.UnionMember;

public class UnionBuildOprate extends ServiceHandler {
	static final byte UNION_BUILD_CREATE    = 0;//建造
	static final byte UNION_BUILD_LEVEL_UP  = 1;//升级
	static final byte UNION_BUILD_REMOVE    = 2;//拆除
	static final byte UNION_BUILD_C_REMOVE  = 3;//取消拆除
	
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		byte type = in.get();
		params.put(type);//操作类型
		switch (type){
			case UNION_BUILD_CREATE:{
				params.put(in.getInt());//城市位置
				params.put(in.getInt());//建造位置
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//建筑编号
				break;
			}
			case UNION_BUILD_LEVEL_UP:
			case UNION_BUILD_REMOVE:
			case UNION_BUILD_C_REMOVE:
			{
				params.put(in.getInt());//建筑的位置
				break;
			}
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)throws Exception {
		CommunicateResp resp = newResp(info);
		byte type  = params.get(0);
		resp.add(type);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		UnionBody union = unionManager.search(role.getUnionId());
		if (union == null){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
			resp.fail();
			return resp;
		}
		UnionMember operator = union.searchMember(info.getUid());
		if (operator == null){
			resp.fail();
			return resp;
		}
		switch (type){
			case UNION_BUILD_CREATE:{
				int position = params.get(1);
				int target = params.get(2);
				String buildKey = params.get(3);
				MapUnionCity city = mapWorld.searchObject(position);
				if (city == null || city.getUnionId() != union.getId()){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_CITY_NOT_OWNER);
					resp.fail();
					return resp;
				}
				if (!city.isMain()){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_CITY_NOT_OCCUPY);
					resp.fail();
					return resp;
				}
				Npccity cityData = city.getData();
				if (!operator.checkPost(UnionPostType.UNION_POST_BUILD_CREATE)) {
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				int couldBuildNum = 0;
				List<String> cityStrs = cityData.getArchitecture();
				for (String cs : cityStrs){
					String[] css = cs.split(":");
					if (css[0].equals(buildKey)){
						couldBuildNum = Integer.parseInt(css[2]);
					}
				}
				if (couldBuildNum == 0){
					Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_CITY_NOT_BUILD,wb.getBuildingName());
					resp.fail();
					return resp;
				}
				if (city.search(buildKey).size() >= couldBuildNum){
					Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_CITY_ONLY_BUILD,couldBuildNum,wb.getBuildingName());
					resp.fail();
					return resp;
				}
				if (!city.checkBuildPosition(target)){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_OUT_RANGE_POS);
					resp.fail();
					return resp;
				}
				Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,buildKey+1);
				if (wbl == null){
					GameLog.error("SB 客户端把建筑固化编号传错了");
					resp.fail();
					return resp;
				}
				List<String> needs1 = wbl.getBuildCostList();
				int needScore = 0;
				for (int i = 0 ; i < needs1.size() ; i += 2){
					String str = needs1.get(i);
					int index = str.lastIndexOf(">=");
					String ts = str.substring(index+2,str.length());
					needScore = Integer.parseInt(ts);
					String pes = str.replaceAll("unionId",String.valueOf(String.valueOf(role.getUnionId())));
					try {
						Object result = ProtoExpression.ExecuteExpression(pes);
						boolean flag = Boolean.parseBoolean(result.toString());
						if (!flag){
							MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_NO_SCORE);
							resp.fail();
							return resp;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				needs1 = wbl.getNeedBuildingIDList();
				for (int i = 0 ; i < needs1.size() ; i += 2){
					String str = needs1.get(i);
					int index = str.lastIndexOf(">=");
					String ts = str.substring(index+2,str.length());
					String pes = str.replaceAll("unionId",String.valueOf(String.valueOf(role.getUnionId())));
					try {
						Object result = ProtoExpression.ExecuteExpression(pes);
						boolean flag = Boolean.parseBoolean(result.toString());
						if (!flag){
							MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_NEED_UNION_CITY,ts);
							resp.fail();
							return resp;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				MapUnionBuild newBuild = MapUnionCity.createBuild(buildKey);
				if (buildKey.equals(BuildName.MAP_UNION_STORAGE.getKey())){
					if (!mapWorld.checkPosition(newBuild,target)){//不能放下
						Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_IN_ERROR_POS,wb.getBuildingName());
						resp.fail();
						return resp;
					}
				}else if (buildKey.equals(BuildName.MAP_UNION_FOOD.getKey()) ||
						   buildKey.equals(BuildName.MAP_UNION_METAL.getKey()) ||
						   buildKey.equals(BuildName.MAP_UNION_OIL.getKey()) ||
						   buildKey.equals(BuildName.MAP_UNION_ALLOY.getKey())){
					if (city.search(buildKey).size() > 0){
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_RESOURCE_ONLY_ONE);
						resp.fail();
						return resp;
					}
					if (!mapWorld.checkPosition(newBuild,target)){//不能放下
						Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_IN_ERROR_POS,wb.getBuildingName());
						resp.fail();
						return resp;
					}
					String[] ss = wbl.getParamList().get(0).split(":");
					long value = Long.parseLong(ss[1]);
					((MapUnionResource)newBuild).setTotal(value);
				}else if (buildKey.equals(BuildName.Map_UNION_TOWER_AIR.getKey()) || 
						  buildKey.equals(BuildName.MAP_UNION_TOWER_TURRET.getKey())){
					if (!mapWorld.checkPosition(newBuild,target)){//不能放下
						Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_IN_ERROR_POS,wb.getBuildingName());
						resp.fail();
						return resp;
					}
				}else if (buildKey.equals(BuildName.MAP_UNION_TOWER_SATELLITE.getKey()) ||
						  buildKey.equals(BuildName.MAP_UNION_TOWER_DETECTOR.getKey())){
					if (!mapWorld.checkPosition(newBuild,target)){//不能放下
						Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_IN_ERROR_POS,wb.getBuildingName());
						resp.fail();
						return resp;
					}
				}else if (buildKey.equals(BuildName.MAP_UNION_TOWER_NUCLEARSILO.getKey())){
					if (!mapWorld.checkPosition(newBuild,target)){//不能放下
						Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_IN_ERROR_POS,wb.getBuildingName());
						resp.fail();
						return resp;
					}
				}else{//所有buff类型的
					if (!mapWorld.checkPosition(newBuild,target)){//不能放下
						Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
						MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_IN_ERROR_POS,wb.getBuildingName());
						resp.fail();
						return resp;
					}
				}
				newBuild.setCityKey(cityData.getId());
				newBuild.setBuildKey(buildKey);
				newBuild.setUnionId(role.getUnionId());
				mapWorld.insertObj(newBuild);
				mapWorld.updatePosition(newBuild,target);
				long now = TimeUtils.nowLong() / 1000;
				long last = union.getGmBuildCreateTime() > 0 ? union.getGmBuildCreateTime() : wbl.getTime();
				TimerLast timer = new TimerLast(now,last,TimerLastType.TIME_CREATE);
				newBuild.registTimer(timer);
				newBuild.init();
				city.addBuild(target);
				union.useScore(needScore);
				union.sendMeToAllMembers(0);
			    String parameter = buildKey + "|" + target;
				LogManager.unionLog(role, union.getName(), EventName.creatUnionBuild.getName(),parameter);
				NewLogManager.unionLog(role, "alliance_build_building",newBuild.getId());
				break;
			}
			case UNION_BUILD_LEVEL_UP:{
				int target = params.get(1);
				MapUnionBuild build = mapWorld.searchObject(target);
				if (build == null){
					GameLog.error("SB 客户端把参数传错了");
					resp.fail();
					return resp;
				}
				if (build.isConst()){
					GameLog.error("是城市固定建筑无法升级");
					resp.fail();
					return resp;
				}
				if (!operator.checkPost(UnionPostType.UNION_POST_BUILD_LEVEL)){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				Npccity cityData = dataManager.serach(Npccity.class,build.getCityKey());
				List<String> cityStrs = cityData.getArchitecture();
				int couldLevel = 0;
				for (String cs : cityStrs){
					String[] css = cs.split(":");
					if (css[0].equals(build.getBuildKey())){
						couldLevel = Integer.parseInt(css[1]);
					}
				}
				Worldbuilding wb = build.getData();
				if (build.getLevel() >= couldLevel){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_LEVEL_MAX,wb.getBuildingName());
					resp.fail();
					return resp;
				}
				if (build.getState() == 1){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_CREATING,wb.getBuildingName());
					resp.fail();
					return resp;
				}else if (build.getState() == 2){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_LEVELING,wb.getBuildingName());
					resp.fail();
					return resp;
				}else if (build.getState() == 3){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_REMOVING,wb.getBuildingName());
					resp.fail();
					return resp;
				}
				Worldbuildinglevel wbl = build.getLevelData(build.getLevel() + 1);
				List<String> needs1 = wbl.getBuildCostList();
				int needScore = 0;
				for (int i = 0 ; i < needs1.size() ; i += 2){
					String str = needs1.get(i);
					int index = str.lastIndexOf(">=");
					String ts = str.substring(index+2,str.length());
					needScore = Integer.parseInt(ts);
					String pes = str.replaceAll("unionId",String.valueOf(role.getUnionId()));
					try {
						Object result = ProtoExpression.ExecuteExpression(pes);
						boolean flag = Boolean.parseBoolean(result.toString());
						if (!flag){
							MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_NO_SCORE);
							resp.fail();
							return resp;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				union.useScore(needScore);
				build.levelUp(union);
			    String parameter = build.getName() + "|" + target;
				LogManager.unionLog(role, union.getName(), EventName.levelUpunionBuild.getName(),parameter);
				break;
			}	
			case UNION_BUILD_REMOVE:{
				int target = params.get(1);
				MapUnionBuild build = mapWorld.searchObject(target);
				if (build == null){
					GameLog.error("SB 客户端把参数传错了");
					resp.fail();
					return resp;
				}
				if (build.isConst()){
					GameLog.error("是城市固定建筑无法拆除");
					resp.fail();
					return resp;
				}
				if (!operator.checkPost(UnionPostType.UNION_POST_BUILD_DESTROY)){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				Worldbuilding wb = build.getData();
				if (build.getState() == 1){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_CREATING,wb.getBuildingName());
					resp.fail();
					return resp;
				}else if (build.getState() == 2){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_LEVELING,wb.getBuildingName());
					resp.fail();
					return resp;
				}else if (build.getState() == 3){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_BUILD_REMOVING,wb.getBuildingName());
					resp.fail();
					return resp;
				}
				long now = TimeUtils.nowLong() / 1000;
				long last = union.getGmBuildDropTime() > 0 ? union.getGmBuildDropTime() : 60;
				TimerLast timer = new TimerLast(now,last,TimerLastType.TIME_REMOVE);
				build.registTimer(timer);
				UnionBody un = unionManager.search( build.getUnionId());
			    String parameter = build.getName() + "|" + target;
				LogManager.unionLog(role, un.getName(), EventName.removeUnionBuild.getName(),parameter);
				break;
			}
			case UNION_BUILD_C_REMOVE:{
				int target = params.get(1);
				if (!operator.checkPost(UnionPostType.UNION_POST_BUILD_DESTROY)){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
					resp.fail();
					return resp;
				}
				MapUnionBuild build = mapWorld.searchObject(target);
				if (build == null){
					GameLog.error("SB 客户端把参数传错了");
					resp.fail();
					return resp;
				}
				build.cancleRemove();
				UnionBody un = unionManager.search( build.getUnionId());
			    String parameter = build.getName() + "|" + target;
				LogManager.unionLog(role, un.getName(), EventName.cRemoveUnionBuild.getName(),parameter);
				break;
			}
		}
		return resp;
	}

}
