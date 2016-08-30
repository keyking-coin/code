package com.joymeng.slg.net.handler.impl.turntable;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.bag.data.ItemType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.turntable.Turntable;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

public class StartTurntableHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 转盘ID
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		RespModuleSet rms = new RespModuleSet();
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		String turntableId = params.get(0);
		Turntable turntable = dataManager.serach(Turntable.class, turntableId);
		if (turntable == null || turntable.getBuildingLevel() == null || turntable.getBuildingLevel().size() < 2) {
			GameLog.error("read turntable base date is fail");
			resp.fail();
			return resp;
		}
		List<RoleBuild> roleBuilds = role.getBuildsByBuildId(0, "CityCenter");
		if (roleBuilds == null || roleBuilds.size() < 1) {
			GameLog.error("getBuildsByBuildId is fail");
			resp.fail();
		}
		if (roleBuilds.get(0).getLevel() <= Byte.parseByte(turntable.getBuildingLevel().get(0))
				&& roleBuilds.get(0).getLevel() >= Byte.parseByte(turntable.getBuildingLevel().get(1))) {
			GameLog.error("异常数据");
			resp.fail();
			return resp;
		}
		// TODO 检测 扣除铜币
		int price = role.getTurntableBody().getTurnSum() == 0 ? 0 : GameConfig.TURNTABLE_PRICE;
		if (role.getCopper() < price) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_COPPER_INSUFFICIENT, GameConfig.TURNTABLE_PRICE);
			resp.fail();
			return resp;
		}
		if (!role.redRoleCopper(price)) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_COPPER_INSUFFICIENT, GameConfig.TURNTABLE_PRICE);
			resp.fail();
			return resp;
		}
		String event ="StartTurntableHandler";
		String items ="copper";
		LogManager.itemConsumeLog(role, GameConfig.TURNTABLE_PRICE, event, items);
		// 下发列表
		List<String> vaList = turntable.getItemList();
		String[] values = new String[vaList.size()];
		int[] rates = new int[vaList.size()];
		for (int i = 0; i < vaList.size(); i++) {
			String[] temp = vaList.get(i).split(":");
			if (temp.length < 2) {
				continue;
			}
			values[i] = temp[0];
			rates[i] = Integer.parseInt(temp[1]);
		}
		String result = MathUtils.getRandomObj(values, rates);
		//获取随机因子
		String rate = "";
		for(String str:vaList){
			if(str.contains(result)){
				String[] ss=str.split(":");
				rate =ss[1];
				break;
			}
		}
		// 获取物品
		Item item = dataManager.serach(Item.class, result);
		if (item == null) {
			GameLog.error("read Item base is fail in StartTurntableHandler");
			resp.fail();
			return resp;
		}
		if (item.getItemType() != ItemType.TYPE_TURNTABLE_BOX) { // 获得物品
			if (item.getMaterialType() == 0) {
				role.getBagAgent().addGoods(result, 1);
			} else {
				role.getBagAgent().addOther(result, 1);
			}
			String itemst  = result;
			LogManager.itemOutputLog(role, 1, event, itemst);
			role.getBagAgent().sendBagToClient(rms);
			LogManager.turnTableLog(role,rate,item.getId());
		} else { // 获得宝箱 进入 九宫格
			role.getTurntableBody().resetSudoku();
			role.getTurntableBody().setSudokuId(result);// 设置九宫格的ID
			role.getTurntableBody().setTurntableState(1);// 设置大转盘状态为九宫格未洗牌状态
			role.getTurntableBody().updateSudokuItems();// 初始化九宫格内容
			List<String> list = role.getTurntableBody().getRandomItems();
			List<String> ls = new ArrayList<String>();
			for(String str :list){
				String[] good = str.split(":");
				ls.add(good[0]);
			}
			LogManager.turnTableLog(role, rate, JsonUtil.ObjectToJsonString(ls));
			try {
				NewLogManager.baseEventLog(role, "active_nine_grid");
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}
		role.updateTurntableTurnSum(role.getTurntableBody().getTurnSum() + 1);//修改大转盘转动次数
		role.getTurntableBody().sendTurntableToClient(rms);// 下发大转盘模块
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_LUCKY_CNT, 0);
		try {
			NewLogManager.baseEventLog(role, "lucky_turntable");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role);
		resp.add(result);
		
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.LUCKY_WHEEL);
		return resp;
	}

}
