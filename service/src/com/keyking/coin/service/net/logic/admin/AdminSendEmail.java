package com.keyking.coin.service.net.logic.admin;

import java.util.List;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class AdminSendEmail extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long  sendId   = buffer.getLong();//发送这边编号
		String name    = buffer.getUTF();//目标的昵称或者手机号
		String theme   = buffer.getUTF();//主题
		String content = buffer.getUTF();//内容
		String time = TimeUtils.nowChStr();
		if (StringUtil.isNull(theme)){
			resp.setError("主题不能为空");
			return resp;
		}
		if (StringUtil.isNull(content)){
			resp.setError("内容不能为空");
			return resp;
		}
		UserCharacter sender = CTRL.search(sendId);
		if (name.equals("@#all")){
			List<UserCharacter> users = CTRL.getUsers();
			for (UserCharacter user : users){
				CTRL.tryToSendEmailToUser(sender,time,theme,content,user);
				user.save();
			}
			resp.setSucces();
			ServerLog.info("admin send eamil to all user");
		}else{
			UserCharacter target = CTRL.searchByAccountOrNickName(name);
			if (CTRL.tryToSendEmailToUser(sender,time,theme,content,target)){
				target.save();
				resp.setSucces();
				ServerLog.info("admin send eamil to " + target.getAccount());
			}else{
				resp.setError("未找到收件人");
			}
		}
		return resp;
	}

}
