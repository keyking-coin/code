package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class SellerCommit extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		byte type  = buffer.get();
		String keyCode = buffer.getUTF();
		String pic  = buffer.getUTF();
		synchronized (this) {
			UserCharacter user = CTRL.search(uid);
			if (user != null){
				if (user.getSeller() != null){
					resp.setError("您已经申请认证了等待审核中");
					return resp;
				}
				if (pic != null){
					Seller seller = new Seller();
					String createTime = TimeUtils.nowChStr();
					seller.setTime(createTime);
					seller.setType(type);
					seller.setKey(keyCode);
					seller.setPic(pic);
					user.setSeller(seller);
					user.setNeedSave(true);
					resp.setSucces();
					resp.add(seller);
					ServerLog.info(user.getAccount() + " applyed seller  approve at " + createTime);
				}else{
					resp.setError("系统错误");
				}
			}
		}
		return resp;
	}
}
