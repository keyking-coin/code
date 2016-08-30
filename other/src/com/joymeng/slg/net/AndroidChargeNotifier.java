package com.joymeng.slg.net;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.charge.ChargeNotifier;
import com.joymeng.services.core.message.JoyResponse;
import com.joymeng.services.core.message.trade.ChargeNotifyMessage;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.RoleShopAgent;
import com.joymeng.slg.domain.shop.data.Banner;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 充值业务逻辑处理
 */
public class AndroidChargeNotifier extends ChargeNotifier implements Instances {
	
	private static AndroidChargeNotifier instance = new AndroidChargeNotifier();

	private AndroidChargeNotifier() {
		
	}
	
	@Override
	public void onChargeNotify(ChargeNotifyMessage message) {
		final long orderId = message.getOrderId();
		final long joyId = message.getJoyId();
		final int value = (int) message.getValue();
		final byte orderType = message.getChargeType();
		final byte result = message.getResult();
		String msg = message.getReserve();
		String[] ss = msg.split("@");
		final String productId = ss[0];
		final String reward    = ss[4];
		if (result != JoyResponse.JOY_RESP_SUCC){//支付失败
			return;
		}
		Role role = world.getRole(joyId);
		RoleShopAgent shop = role.getShopAgent();
		RespModuleSet rms = new RespModuleSet();
		if (!shop.tryToUseMoneyBuy(role,ss[1],ss[2],ss[3],rms,false)){//如果没有买成功
			Banner._buyOk(reward,role,rms);//强制购买成功
		}
		LogManager.chargeLog(role,orderType,value);// 玩家充值日志  （role,付款方式，充值金额）
		try {
			NewLogManager.chargeLog(role, "recharge", value);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		activityManager.sendShopLayoutToClient(rms,role);
		MessageSendUtil.sendModule(rms,role.getUserInfo());
		new DaoData() {
			@Override
			public String[] wheres() {
				return new String[]{RED_ALERT_CHARGE_ORDER_ID};
			}
			@Override
			public String table() {
				return TABLE_RED_ALERT_CHARGE_ORDER;
			}
			
			@Override
			public void saveToData(SqlData data) {
				data.put(RED_ALERT_CHARGE_ORDER_ID, orderId);
				data.put(RED_ALERT_CHARGE_JOYID, joyId);
				data.put(RED_ALERT_CHARGE_VALUE, value);
				data.put(RED_ALERT_CHARGE_ORDERTYPE, orderType);
				data.put(RED_ALERT_CHARGE_ORDER_PRODUCTID,productId);
				data.put(RED_ALERT_CHARGE_ORDER_REWARD,reward);
				data.put(RED_ALERT_CHARGE_ORDER_TIME,TimeUtils.nowStr());
			}
			
			@Override
			public void save() {
				taskPool.saveThread.addSaveData(this);
			}
			@Override
			public void over() {
				
			}
			@Override
			public void loadFromData(SqlData data) {
				
			}
			@Override
			public void insertData(SqlData data) {
				saveToData(data);
			}
			@Override
			public boolean delete() {
				return false;
			}
			@Override
			public boolean saving() {
				return true;
			}
		}.save();
	}
    
	public static AndroidChargeNotifier getInstance() {
		return instance;
	}
}
