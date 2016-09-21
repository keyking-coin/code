package com.joymeng.slg.net.handler.impl.shop;

import java.util.List;

import org.joda.time.DateTime;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.trade.ChargeNotifyMessage;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.core.message.JoyResponse;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.activity.data.Activity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.RoleShopAgent;
import com.joymeng.slg.domain.shop.data.Banner;
import com.joymeng.slg.domain.shop.data.ShopCell;
import com.joymeng.slg.domain.shop.data.ShopLayout;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ShopUIOperateHandler extends ServiceHandler {
	
	static final byte SHOP_UI_NORMAL_LIST  = 0;//显示普通商店列表数据
	static final byte SHOP_UI_NORMAL_BUY   = 1;//普通商店购买
	static final byte SHOP_UI_MONEY_BUY    = 2;//人民币道具购买
	
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		byte type  = in.get();//操作类型
		params.put(type);
		switch (type){
			case SHOP_UI_NORMAL_LIST:{
				params.put(in.get());//查看什么栏
				break;
			}
			case SHOP_UI_NORMAL_BUY:{
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//购买编号
				params.put(in.getInt());//购买道具数量
				break;
			}
			case SHOP_UI_MONEY_BUY:{
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//活动编号
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//商店编号
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//banner编号
				break;
			}
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		byte type  = params.get(0);
		resp.add(type);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		switch (type){
			case SHOP_UI_NORMAL_LIST:{
				byte lableType = params.get(1);
				List<ShopCell> cells = role.getShopAgent().getList(lableType);
				resp.add(cells);//所有的列表数据
				break;
			}
			case SHOP_UI_NORMAL_BUY:{
				String key = params.get(1);
				int num  = params.get(2);
				resp.add(key);//本次购买的道具编号
				if (!role.getShopAgent().tryToBuySomeThing(role,key,num,resp)){
					resp.fail();
				}
				break;
			}
			case SHOP_UI_MONEY_BUY:{
				String activityId = params.get(1);
				String shopId = params.get(2);
				String bannerId = params.get(3);
				RespModuleSet rms = new RespModuleSet();
				if (!role.getShopAgent().tryToUseMoneyBuy(role,activityId,shopId,bannerId,rms,true)){
					activityManager.sendShopLayoutToClient(rms,role);
					resp.fail();
				}
				else {
					onChargeNotify(role, activityId, shopId, bannerId);
				}
				MessageSendUtil.sendModule(rms,info);
				break;
			}
		}
		return resp;
	}

	public void onChargeNotify(Role role, String activityId, String shopId, String bannerId) {
		Activity activity = activityManager.searchActivity(activityId);
		ShopLayout shop = activity.searchElement(shopId);
		Banner banner = shop.search(bannerId);
		
		final long joyId = role.getId();
		final long orderId = DateTime.now().getMillis();
		final int value = Integer.parseInt(banner.getPrice());
		final byte orderType = 0;
		final String productId = banner.getProductId();
		final String reward = "";

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
				data.put(RED_ALERT_CHARGE_ORDER_PRODUCTID, productId);
				data.put(RED_ALERT_CHARGE_ORDER_REWARD, reward);
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
}
