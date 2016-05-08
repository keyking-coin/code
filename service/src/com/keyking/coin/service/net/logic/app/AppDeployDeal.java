package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class AppDeployDeal extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid   = buffer.getLong();//发布者编号
		byte sellFlag = buffer.get();//发布卖贴还是买帖
		byte deployType = buffer.get();//发布方式 0普通发送,1 推送发送
		String typeSTr = buffer.getUTF();
		byte type = (byte)(typeSTr.equals("入库") ? 0 : 1);//交割类型
		String bourse = buffer.getUTF();//文交所
		String title  = buffer.getUTF();//名称
		float price = Float.parseFloat(buffer.getUTF());
		int num = buffer.getInt();
		String monad = buffer.getUTF();//单位
		String validTime = buffer.getUTF();//有效时间
		String createTime = TimeUtils.nowChStr();
		String other = buffer.getUTF();//有效时间
		byte helpFlag = buffer.get();//帮组标志
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		try {
			TimeUtils.getTime(validTime);
		} catch (Exception e) {
			resp.setError("时间格式不对");
			return resp;
		}
		if (sellFlag == 1 && !user.getPermission().seller()){
			Seller seller = user.getSeller();
			if (seller != null && !seller.isPass()){
				resp.setError("请等待卖家认证通过");
			}else{
				resp.setError("请先进行卖家认证");
			}
			return resp;
		}
		Deal deal = new Deal();
		deal.setUid(uid);
		deal.setType(type);
		deal.setBourse(bourse);
		deal.setName(title);
		deal.setPrice(price);
		deal.setNum(num);
		deal.setMonad(monad);
		deal.setValidTime(validTime);
		deal.setCreateTime(createTime);
		if (!StringUtil.isNull(other)){
			deal.setOther(other);
		}
		deal.setHelpFlag(helpFlag);
		deal.setSellFlag(sellFlag);
		long dealId = PK.key("deal");
		deal.setId(dealId);
		deal.save();
		if (deployType == 1){//墙纸推送
			if (user.getRecharge().getCurMoney() < 10){//强制推送
				resp.setError("您的邮游币不足请先去充值");
				return resp;
			}
			user.getRecharge().changeMoney(-10);
			deal.setLastIssue(TimeUtils.nowChStr());
			NET.sendMessageToAllClent(deal.pushMessage(),user.getSessionAddress());
		}
		NET.sendMessageToAllClent(deal.clientMessage(Module.ADD_FLAG),null);
		resp.setSucces();
		ServerLog.info(user.getAccount() + " deployed deal ok ----> id is " + deal.getId());
		return resp;
	}
	
}
