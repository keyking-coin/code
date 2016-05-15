package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.net.resp.module.AdminModuleResp;
import com.keyking.coin.service.net.resp.module.Module;

public class AppOrderRevoke extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("系统错误");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		long dealId    = buffer.getLong();
		long orderId   = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null && (deal.getUid() == uid || order.getBuyId() == uid)){
				int flag = ((deal.getSellFlag() == 1 && order.getBuyId() == uid) || (deal.getSellFlag() == 0 && deal.getUid() == uid)) ? DealOrder.ORDER_REVOKE_BUYER : DealOrder.ORDER_REVOKE_SELLER;
				if (!order.checkRevoke(flag)){//撤销成功
					order.addRevoke(flag);
					if (order.checkRevoke()){//双方都同意了
						NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG,new AdminModuleResp()));
					}
					order.addRevoke(flag);
					resp.put("state",order.getRevoke());
					resp.setSucces();
					return resp;
				}else{
					resp.setError("已申请等待对方回应");
				}
			}else{
				resp.setError("操作失败");
			}
		}else{
			resp.setError("操作失败");
		}
		return resp;
	}

}
