package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class AppSellerApprove extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//认证的用户编号
		byte type  = buffer.get();//0 个人认证;1公司认证
		String keyCode = buffer.getUTF();//编号个人身份证号/公司营业执照编号
		String pic  = buffer.getUTF();//上传的证件正面的图片名称
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			if (user.getSeller() != null){
				resp.setError("您已经申请认证了等待审核中");
				return resp;
			}
			Seller seller = new Seller();
			String createTime = TimeUtils.nowChStr();
			seller.setTime(createTime);
			seller.setType(type);
			seller.setKey(keyCode);
			seller.setPic(pic);
			user.setSeller(seller);
			user.save();
			resp.put("result","已成功申请，等待管理员审核。");
			resp.setSucces();
			NET.sendMessageToAdmin(user.clientAdminMessage(Module.ADD_FLAG));
			ServerLog.info(user.getAccount() + " applyed seller  approve at " + createTime);
		}else{
			resp.setError("找不到用户");
		}
		return resp;
	}

}
