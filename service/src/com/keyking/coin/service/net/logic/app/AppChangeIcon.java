package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.StringUtil;

public class AppChangeIcon extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String faceName = buffer.getUTF();//调用php上传图片时候的图片名称的字符串
		long uid = buffer.getLong();//用户编号
		if (StringUtil.isNull(faceName)){
			resp.setError("非法的头像文件名称");
			return resp;
		}
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			user.setFace(faceName);
		}
		resp.setSucces();
		return resp;
	}

}
