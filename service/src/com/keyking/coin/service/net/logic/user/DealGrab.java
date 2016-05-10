package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.deal.SimpleOrderModule;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.ServerLog;

public class DealGrab extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long id = buffer.getLong();
		long uid = buffer.getLong();
		int num = buffer.getInt();
		Deal deal = CTRL.tryToSearch(id);
		if (deal == null){
			resp.setError("卖家已撤销");
			return resp;
		}
		if (!deal.checkValidTime()){
			resp.setError("此贴已过期");
			return resp;
		}
		if (deal.isLock()){
			resp.setError("此贴已被管理员锁定");
			return resp;
		}
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		synchronized (deal) {
			/*
			float need = num * deal.getPrice();
			float maxCredit = Math.max(user.computeMaxCredit(),user.computeTempCredit());
			float haveUsed = user.computeUsedCredit();
			if (deal.getSellFlag() == 0 && deal.getHelpFlag() == 0 && haveUsed + need > maxCredit){
				resp.setError("你的信用不足");
				return resp;
			}*/
			if (!deal.couldGrab(num)){
				resp.setError("剩余数量不足,抢单失败。");
				return resp;
			}
			DealOrder order = new DealOrder();
			order.setDealId(id);
			order.setBuyId(uid);
			order.addTimes((byte)0);
			order.setPrice(deal.getPrice());
			order.setNum(num);
			order.setHelpFlag(deal.getHelpFlag());
			long orderId = PK.key("deal_order");
			order.setId(orderId);
			deal.addOrder(order);
			resp.setSucces();
			SimpleOrderModule module = new SimpleOrderModule();
			order.simpleDes(module);
			ModuleResp modules = new ModuleResp();
			modules.addModule(module);
			order.clientMessage(Module.ADD_FLAG,modules,deal);
			deal.clientMessage(Module.UPDATE_FLAG,modules);
			NET.sendMessageToAllClent(modules,null);
			ServerLog.info(user.getAccount() + " grab deal ok ----> id is " + id);
		}
		return resp;
	}
}
