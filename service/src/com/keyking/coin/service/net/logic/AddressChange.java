package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.StringUtil;

public class AddressChange extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		byte type = buffer.get();
		long uid = buffer.getLong();
		String address = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError(forbidStr);
				return resp;
			}
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
			resp.add(type);
			resp.add(user);
			resp.setSucces();
		}
		return resp;
	}

}
