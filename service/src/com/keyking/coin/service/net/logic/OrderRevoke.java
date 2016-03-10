package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;

public class OrderRevoke extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid       = buffer.getLong();
		long dealId    = buffer.getLong();
		long orderId   = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null && (deal.getUid() == uid || order.getBuyId() == uid)){
				int flag = ((deal.getSellFlag() == 1 && order.getBuyId() == uid) || (deal.getSellFlag() == 0 && deal.getUid() == uid)) ? DealOrder.ORDER_REVOKE_BUYER : DealOrder.ORDER_REVOKE_SELLER;
				if (!order.checkRevoke(flag)){//撤销成功
					order.addRevoke(flag);
					ModuleResp modules = new ModuleResp();
					order.clientMessage(Module.UPDATE_FLAG,modules);
					NET.sendMessageToAllClent(modules,null);
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
