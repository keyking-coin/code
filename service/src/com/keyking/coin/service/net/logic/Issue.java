package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class Issue extends AbstractLogic{
	//签、版、套、个、张、本、封、盒、箱、片、枚、包、条、捆、刀
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid  = buffer.getLong();
		byte sendType = buffer.get();//发布方式 1 推送发送;2普通发送
		byte flag = buffer.get();//卖家标示
		byte type = buffer.get();//交割类型
		String bourse = buffer.getUTF();
		String name = buffer.getUTF();
		String priceStr = buffer.getUTF();
		float price = 0;
		if (!StringUtil.isNull(priceStr)){
			price = Float.parseFloat(priceStr);
		}
		int num = buffer.getInt();
		String monad = buffer.getUTF();
		String validTime = buffer.getUTF();
		String createTime = TimeUtils.nowChStr();
		String other = buffer.getUTF();
		byte helpFlag = buffer.get();
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError(forbidStr);
			return resp;
		}
		Deal deal = new Deal();
		deal.setUid(uid);
		deal.setSellFlag(flag);
		deal.setType(type);
		deal.setBourse(bourse);
		deal.setName(name);
		deal.setPrice(price);
		deal.setNum(num);
		deal.setMonad(monad);
		deal.setValidTime(validTime);
		deal.setCreateTime(createTime);
		deal.setOther(other);
		deal.setHelpFlag(helpFlag);
		float total_value = num * price;
		if (user != null){
			float maxCredit = Math.max(user.computeMaxCredit(),user.computeTempCredit());
			if (user.computeUsedCredit() + total_value > maxCredit){
				resp.setError("你的信用不足");
				return resp;
			}
			Seller seller = user.getSeller();
			if (seller != null && seller.isPass()){
				deal.setNeedDeposit(total_value);
				if (CTRL.tryToInsert(deal)){
					if (sendType == 1){//强制推送
						if (user.getRecharge().getCurMoney() < 10){
							resp.setError("您的邮游币不足请先去充值");
							return resp;
						}
						NET.sendMessageToAllClent(deal.pushMessage(),user.getSessionAddress());
					}
					resp.setSucces();
					ServerLog.info(user.getAccount() + " deployed deal-sell ok ----> id is " + deal.getId());
				}
				NET.sendMessageToAllClent(deal.clientMessage(Module.ADD_FLAG),null);
			}else if (seller != null && !seller.isPass()){
				resp.setError("请等待卖家认证通过");
			}else{
				resp.setError("请先进行卖家认证");
			}
		}
		return resp;
	}
}
