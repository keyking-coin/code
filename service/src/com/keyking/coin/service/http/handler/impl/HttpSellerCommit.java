package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class HttpSellerCommit extends HttpHandler {
	//http://139.196.30.53:32104/HttpSellerCommit?uid=x&pwd=x&type=x&key=x&pic=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid   = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd = request.getParameter("pwd");//验证码
		byte type = Byte.parseByte(request.getParameter("type"));//0个人,1公司
		String keyCode = request.getParameter("key");//证件编号
		String pic = request.getParameter("pic");//图片名称
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				message(request,response,"您已经被封号原因是:" + forbidStr);
				return;
			}
			if (!user.getPwd().equals(pwd)){
				message(request,response,"非法的请求");
				return;
			}
			if (user.getSeller() != null){
				message(request,response,"您已经申请认证了等待审核中");
				return;
			}
			if (pic != null){
				Seller seller = new Seller();
				String createTime = TimeUtils.nowChStr();
				seller.setTime(createTime);
				seller.setType(type);
				seller.setKey(keyCode);
				seller.setPic(pic);
				user.setSeller(seller);
				user.save();
				message(request,response,"申请成功");
				NET.sendMessageToAdmin(user.clientAdminMessage(Module.ADD_FLAG));
				ServerLog.info(user.getAccount() + " applyed seller  approve at " + createTime);
			}else{
				message(request,response,"系统错误");
			}
		}
	}

}
