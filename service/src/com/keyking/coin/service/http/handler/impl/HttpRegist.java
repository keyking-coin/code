package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpRegist extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account   = request.getParameter("account");
		String pwd       = request.getParameter("pwd");
		String nickname  = request.getParameter("nick");
		String name      = request.getParameter("name");
		String address   = request.getParameter("address");
		String code      = request.getParameter("code");
		int codeResult = TOKEN.check(account,code);
		if (codeResult == 1){
			message(request,response,"验证码错误");
			return;
		}else if (codeResult == 2){
			message(request,response,"验证码已失效");
			return;
		}
		String registTime = TimeUtils.nowChStr();
		String result = CTRL.checkHttpAccout(account,nickname);
		if (result == null){
			UserCharacter user = new UserCharacter();
			user.setAccount(account);
			user.setPwd(pwd);
			user.setNikeName(nickname);
			if (!StringUtil.isNull(name)){
				user.setName(name);
			}
			if (!StringUtil.isNull(address)){
				user.addAddress(address);
			}
			String content = "欢迎您注册邮游包入库交易平台!1、交易前请先查看规则和解答，以免交易违规<br>2、注册账号即开通买家会员，可以直接下单求购或者确认卖方单子。如需认证卖家会员，请提交资料后联系客服微信：15325585606<br>3、平台管理员发出的站内信信息发件人管理员为红色字体，请提高警惕勿被其他高仿昵称欺骗<br>4、因平台没有确认提醒功能，请发帖方及时关注自己帖子信息，抢单后请及时电话联系开贴方。";
			CTRL.tryToSendEmailToUser(1,registTime,"欢迎注册邮游包入库交易平台",content,user);
			user.setRegistTime(registTime);
			CTRL.register(user);
			user.save();
			message(request,response,"ok");
			ServerLog.info(account + " regist at " + registTime);
		}else{
			message(request,response,result);
		}
	}
}
