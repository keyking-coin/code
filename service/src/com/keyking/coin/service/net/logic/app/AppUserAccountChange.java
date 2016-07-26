package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.StringUtil;

public class AppUserAccountChange extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		byte type      = buffer.get();
		long uid       = buffer.getLong();
		String bourser = buffer.getUTF();
		String account = buffer.getUTF();
		String key = bourser + " " + account;
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			synchronized (user) {
				if (type == 0){
					if (StringUtil.isNull(bourser)){
						resp.setError("文交所不能为空");
						return resp;
					}
					if (StringUtil.isNull(account)){
						resp.setError("账号不能为空");
						return resp;
					}
					if (!user.addAddress(bourser + " " + account)){
						resp.setError("此地址已在列表");
						return resp;
					}
				}else{
					if (StringUtil.isNull(key)){
						resp.setError("参数不对");
						return resp;
					}
					if (!user.removeAddress(key)){
						resp.setError("找不到文交所账号");
						return resp;
					}
				}
				user.save();
				resp.put("list",user.getAddresses());
				resp.setSucces();
			}
		}else{
			resp.setError("找不到用户");
		}
		return resp;
	}

}
