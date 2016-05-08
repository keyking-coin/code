package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.StringUtil;

public class AppAddressChange extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		byte type = buffer.get();
		long uid  = buffer.getLong();
		String address = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			synchronized (user) {
				if (type == 0){
					if (StringUtil.isNull(address)){
						resp.setError("地址不能为空");
						return resp;
					}
					if (!user.addAddress(address)){
						resp.setError("此地址已在列表");
						return resp;
					}
				}else{
					if (StringUtil.isNull(address)){
						resp.setError("不能删除空的地址");
						return resp;
					}
					if (!user.removeAddress(address)){
						resp.setError("在列表中找不到要删除的地址");
						return resp;
					}
				}
				user.setNeedSave(true);
				user.save();
				resp.put("addresses",user.getAddresses());
				resp.setSucces();
			}
		}else{
			resp.setError("找不到用户");
		}
		return resp;
	}

}
