package com.joymeng.http.handler.impl.gm;

import java.util.List;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.role.MapCityMove;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.queue.TimeQueue;
import com.joymeng.slg.domain.object.daily.OnlineAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.world.GameConfig;

public class HttpGmDebug extends HttpHandler {

	@Override
	public boolean handle(HttpRequestMessage request,
			HttpResponseMessage response) {
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		if (ServiceApp.FREEZE){
			message(response,"服务器已关闭");
			return false;
		}
		String action = request.getParameter("action");
		switch (action){
			case "battleLog":{
				String flag = request.getParameter("battle_log_open");
				BattleField.loggerFlag = flag.equals("true");
				message(response,"设置成功");
				break;
			}
			case "refreshMarket":{
				try {
					String uStr = request.getParameter("market_uid");
					long uid = Long.parseLong(uStr);
					Role role = world.getRole(uid);
					if (role == null){
						message(response,"错误的用户编号");
						return false;
					}
					role.getBlackMarketAgent().gmRefresh(role);
					message(response,"刷新成功");
				} catch (Exception e) {
					e.printStackTrace();
					message(response,"刷新失败");
				}
				break;
			}
			case "attack_city_change":{
				String flag = request.getParameter("attack_city_op");
				GameConfig.ATTACK_CITY_MUST_WIN = flag.equals("true");
				message(response,"设置成功");
				break;
			}
			case "money_shop_change":{
				String flag = request.getParameter("money_shop_op");
				if (flag.equals("true")){
					GameConfig.CHARGE_SHOP_TIP = "null";
				}else{
					GameConfig.CHARGE_SHOP_TIP = "msg_rechargeShopView_no_open";
				}
				message(response,"设置成功");
				break;
			}
			case "online_reward_change":{
				String uStr = request.getParameter("online_reward_uid");
				String uNum  = request.getParameter("online_reward_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"数量不能为空");
					return false;
				}
				int num = Integer.parseInt(uNum);
				OnlineAgent online = role.getDailyAgent();
				online.setGmTime(num);
				if (num > 0){
					if (!online.isOver()){
						online.getTimer().setLast(num);
					}
					RespModuleSet rms = new RespModuleSet();
					online.sendToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"设置成功");
				break;
			}
			case "build_time_change":{
				String uStr  = request.getParameter("build_time_uid");
				String uNum  = request.getParameter("build_time_num");
				String uCity = request.getParameter("build_time_city");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"数量不能为空");
					return false;
				}
				if (StringUtils.isNull(uCity)){
					message(response,"请设置城市编号");
					return false;
				}
				RoleCityAgent city = role.getCity(Integer.parseInt(uCity));
				if (city == null){
					message(response,"错误的城市编号");
					return false;
				}
				if (city.getBuildQueueNum() == 1){
					message(response,"你没有购买工程队");
					return false;
				}
				int last = Integer.parseInt(uNum);
				List<TimeQueue> queues = city.getBuildQueue();
				for (int i = 1 ; i < queues.size() ; i ++){
					TimeQueue queue = queues.get(i);
					queue.getTimer().setLast(last);
				}
				RespModuleSet rms = new RespModuleSet();
				city.sendToClient(rms,true);
				MessageSendUtil.sendModule(rms,role.getUserInfo());
				message(response,"设置成功");
				break;
			}
			case "union_max_num_change":{
				String uStr  = request.getParameter("union_max_uid");
				String uNum  = request.getParameter("union_max_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				UnionBody body = unionManager.search(role.getUnionId());
				if (body == null){
					message(response,"你还没有加入联盟");
					return false;
				}
				body.setGmMemberNum(num);
				body.sendMeToAllMembers(0);
				message(response,"设置成功");
				break;
			}
			case "union_teach_time_change":{
				String uStr  = request.getParameter("union_teach_time_uid");
				String uNum  = request.getParameter("union_teach_time_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				UnionBody body = unionManager.search(role.getUnionId());
				if (body == null){
					message(response,"你还没有加入联盟");
					return false;
				}
				body.setGmTeachLevelUpTime(num);
				if (num > 0){
					TimerLast timer = body.getTimers();
					if (timer != null){
						body.getTimers().setLast(num);
					}
					body.sendMeToAllMembers(0);
				}
				message(response,"设置成功");
				break;
			}
			case "union_share_change":{
				String uStr  = request.getParameter("union_share_uid");
				String uNum  = request.getParameter("union_share_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				UnionBody body = unionManager.search(role.getUnionId());
				if (body == null){
					message(response,"你还没有加入联盟");
					return false;
				}
				body.setGmShareNum(num);
				message(response,"设置成功");
				break;
			}
			case "union_build_create_change":{
				String uStr  = request.getParameter("union_build_create_uid");
				String uNum  = request.getParameter("union_build_create_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				UnionBody body = unionManager.search(role.getUnionId());
				if (body == null){
					message(response,"你还没有加入联盟");
					return false;
				}
				body.setGmBuildCreateTime(num);
				if (num > 0){
					List<MapUnionBuild> builds = world.getListObjects(MapUnionBuild.class);
					for (int i = 0 ; i < builds.size() ; i++){
						MapUnionBuild build = builds.get(i);
						if (build == null || build.isRemoving()){
							continue;
						}
						TimerLast timer = build.getBuildTimer();
						if (timer != null && timer.getType() == TimerLastType.TIME_CREATE){
							timer.setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
			case "union_build_up_change":{
				String uStr  = request.getParameter("union_build_up_uid");
				String uNum  = request.getParameter("union_build_up_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				UnionBody body = unionManager.search(role.getUnionId());
				if (body == null){
					message(response,"你还没有加入联盟");
					return false;
				}
				body.setGmBuildLevelUpTime(num);
				if (num > 0){
					List<MapUnionBuild> builds = world.getListObjects(MapUnionBuild.class);
					for (int i = 0 ; i < builds.size() ; i++){
						MapUnionBuild build = builds.get(i);
						if (build == null || build.isRemoving()){
							continue;
						}
						TimerLast timer = build.getBuildTimer();
						if (timer != null && timer.getType() == TimerLastType.TIME_LEVEL_UP){
							timer.setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
			case "union_build_drop_change":{
				String uStr  = request.getParameter("union_build_drop_uid");
				String uNum  = request.getParameter("union_build_drop_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				UnionBody body = unionManager.search(role.getUnionId());
				if (body == null){
					message(response,"你还没有加入联盟");
					return false;
				}
				body.setGmBuildDropTime(num);
				if (num > 0){
					List<MapUnionBuild> builds = world.getListObjects(MapUnionBuild.class);
					for (int i = 0 ; i < builds.size() ; i++){
						MapUnionBuild build = builds.get(i);
						if (build == null || build.isRemoving()){
							continue;
						}
						TimerLast timer = build.getBuildTimer();
						if (timer != null && timer.getType() == TimerLastType.TIME_REMOVE){
							timer.setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
			case "admin_op_change":{
				String uStr1  = request.getParameter("admin_src_uid");
				String uStr2  = request.getParameter("admin_tar_uid");
				long uid1 = Long.parseLong(uStr1);
				long uid2 = Long.parseLong(uStr2);
				String op  = request.getParameter("admin_op_flag");
				if (op.equals("true")){
					world.debug(uid1,uid2);
				}else{
					world.debug(0,0);
				}
				message(response,"设置成功");
				break;
			}
			case "fortress_build_change":{
				String uStr  = request.getParameter("fortress_build_uid");
				String uNum  = request.getParameter("fortress_build_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				role.setGmFortressCreateTime(num);
				if (num > 0){
					List<MapFortress> mfs = mapWorld.getAllFortresses(uid);
					for (int i = 0 ; i < mfs.size() ; i++){
						MapFortress mf = mfs.get(i);
						TimerLast timer = mf.getBuildTimer();
						if (timer != null && timer.getType() == TimerLastType.TIME_CREATE){
							timer.setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
			case "fortress_level_change":{
				String uStr  = request.getParameter("fortress_level_uid");
				String uNum  = request.getParameter("fortress_level_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				role.setGmFortressLevelUpTime(num);
				if (num > 0){
					List<MapFortress> mfs = mapWorld.getAllFortresses(uid);
					for (int i = 0 ; i < mfs.size() ; i++){
						MapFortress mf = mfs.get(i);
						TimerLast timer = mf.getBuildTimer();
						if (timer != null && timer.getType() == TimerLastType.TIME_LEVEL_UP){
							timer.setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
			case "fortress_drop_change":{
				String uStr  = request.getParameter("fortress_drop_uid");
				String uNum  = request.getParameter("fortress_drop_num");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				int num = Integer.parseInt(uNum);
				role.setGmFortressDropTime(num);
				if (num > 0){
					List<MapFortress> mfs = mapWorld.getAllFortresses(uid);
					for (int i = 0 ; i < mfs.size() ; i++){
						MapFortress mf = mfs.get(i);
						TimerLast timer = mf.getBuildTimer();
						if (timer != null && timer.getType() == TimerLastType.TIME_REMOVE){
							timer.setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
			case "city_move_change":{
				String uStr  = request.getParameter("city_move_uid");
				String uNum  = request.getParameter("city_move_num");
				String cNum  = request.getParameter("city_move_city");
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				if (StringUtils.isNull(uNum)){
					message(response,"请设置数量");
					return false;
				}
				if (StringUtils.isNull(cNum)){
					message(response,"请设置城市编号");
					return false;
				}
				int num = Integer.parseInt(uNum);
				int cityId = Integer.parseInt(cNum);
				role.setGmCityMoveTime(num);
				if (num > 0){
					MapCityMove mcm = mapWorld.searchCityMove(uid,cityId);
					if (mcm != null ){
						GarrisonTroops creater = mcm.getOwner();
						if (creater != null){
							creater.getTimer().setLast(num);
						}
					}
				}
				message(response,"设置成功");
				break;
			}
		}
		return false;
	}

}
