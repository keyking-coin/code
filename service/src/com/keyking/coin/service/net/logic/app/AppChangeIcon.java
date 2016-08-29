package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.StringUtil;

public class AppChangeIcon extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//用户编号
		String faceName = buffer.getUTF();//调用php上传图片时候的图片名称的字符串
		if (StringUtil.isNull(faceName)){
			resp.setError("非法的头像文件名称");
			return resp;
		}
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不到用户");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		if (faceName.startsWith("http://")){
			int index = faceName.lastIndexOf("/");
			faceName = faceName.substring(index + 1,faceName.length());
		}
		user.setFace(faceName);
		user.save();
		resp.setSucces();
		resp.put("result","ok");
		return resp;
	}

}
