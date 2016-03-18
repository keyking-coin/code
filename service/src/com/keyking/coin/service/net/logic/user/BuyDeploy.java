package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class BuyDeploy extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName)throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		byte flag = buffer.get();
		long uid  = buffer.getLong();
		byte type = buffer.get();
		String address = buffer.getUTF();
		String name = buffer.getUTF();
		int num    = buffer.getInt();
		String monad = buffer.getUTF();
		float price = Float.parseFloat(buffer.getUTF());
		String validTime = buffer.getUTF();
		String createTime = TimeUtils.nowChStr();
		String other = buffer.getUTF();
		byte helpFlag = buffer.get();
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		float needCredit = num * price;
		float maxCredit = Math.max(user.computeMaxCredit(),user.computeTempCredit());
		if (user.computeUsedCredit() + needCredit > maxCredit){
			resp.setError("你的信用不足");
			return resp;
		}
		Deal deal = new Deal();
		deal.setType(type);
		deal.setUid(uid);
		deal.setBourse(address);
		deal.setName(name);
		deal.setNum(num);
		deal.setMonad(monad);
		deal.setPrice(price);
		deal.setValidTime(validTime);
		deal.setCreateTime(createTime);
		deal.setOther(other);
		deal.setHelpFlag(helpFlag);
		if (flag == 1 && user.getRecharge().getCurMoney() < 10){//强制推送
			resp.setError("您的邮游币不足请先去充值");
			return resp;
		}
		if (CTRL.tryToInsert(deal)){
			long dealId = PK.key("deal");
			deal.setId(dealId);
			if (flag == 1){//强制推送
				user.getRecharge().changeMoney(-10);
				deal.setLastIssue(TimeUtils.nowChStr());
				NET.sendMessageToAllClent(deal.pushMessage(),user.getSessionAddress());
			}
			resp.setSucces();
			NET.sendMessageToAllClent(deal.clientMessage(Module.ADD_FLAG),null);
			ServerLog.info(user.getAccount() + " deployed deal-buy ok ----> id is " + deal.getId());
			resp.setSucces();
		}
		return resp;
	}
}
