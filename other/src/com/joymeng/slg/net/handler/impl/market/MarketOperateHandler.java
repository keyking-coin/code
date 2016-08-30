package com.joymeng.slg.net.handler.impl.market;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.market.RoleBlackMarketAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class MarketOperateHandler extends ServiceHandler {
	
	static final byte MARKET_UI_SHOW_LIST       = 0;//显示黑市列表
	static final byte MARKET_UI_SHOW_BUY        = 1;//黑市购买
	static final byte MARKET_UI_SHOW_REFRESH    = 2;//刷新黑市
	
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		byte type = in.get();
		params.put(type);
		switch (type){
			case MARKET_UI_SHOW_BUY:{
				params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//购买编号
				break;
			}
			default:{
				break;
			}
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		byte type  = params.get(0);
		resp.add(type);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		RoleBlackMarketAgent market = role.getBlackMarketAgent();
		switch (type){
			case MARKET_UI_SHOW_LIST:{
				market.sendClient(resp);
				break;
			}
			case MARKET_UI_SHOW_BUY:{
				String buyId = params.get(1);
				if (!market.tryToBuyCell(role,buyId,resp)){
					resp.fail();
				}
				break;
			}
			case MARKET_UI_SHOW_REFRESH:{
				if (!market.tryToRefresh(role,resp)){
					resp.fail();
				}
				break;
			}
		}
		return resp;
	}

}
