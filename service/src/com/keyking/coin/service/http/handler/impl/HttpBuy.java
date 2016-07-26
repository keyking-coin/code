package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpBuy extends HttpHandler {
	//http://127.0.0.1:32104/HttpBuy?uid=x&pwd=x&st=x&type=x&bourse=x&title=x&price=x&num=x&monad=x&valid=x&other=x&help=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid   = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd = request.getParameter("pwd");//验证码
		String sendType = request.getParameter("st");//发布方式 0普通发送,1 推送发送
		String typeSTr = request.getParameter("type");
		byte type = (byte)(typeSTr.equals("入库") ? 0 : 1);//交割类型
		String bourse = request.getParameter("bourse");//文交所
		String title = request.getParameter("title");//名称
		float price = Float.parseFloat(request.getParameter("price"));
		int num = Integer.parseInt(request.getParameter("num"));
		String monad = request.getParameter("monad");//单位
		String validTime = request.getParameter("valid");//有效时间
		String createTime = TimeUtils.nowChStr();
		String other = request.getParameter("other");//有效时间
		byte helpFlag = Byte.parseByte(request.getParameter("help"));//帮组标志
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			message(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		if (!user.getPwd().equals(pwd)){
			message(request,response,"非法的请求");
			return;
		}
		Deal deal = new Deal();
		deal.setUid(uid);
		deal.setType(type);
		deal.setBourse(bourse);
		deal.setName(title);
		deal.setPrice(price);
		deal.setNum(num);
		deal.setMonad(monad);
		try {
			TimeUtils.getTime(validTime);
		} catch (Exception e) {
			message(request,response,"时间格式不对");
			return;
		}
		deal.setValidTime(validTime);
		deal.setCreateTime(createTime);
		if (!StringUtil.isNull(other)){
			deal.setOther(other);
		}
		deal.setHelpFlag(helpFlag);
		boolean sendFlag = sendType.equals("1");
		if (sendFlag && user.getRecharge().getCurMoney() < 10){//强制推送
			message(request,response,"您的邮游币不足请先去充值");
			return;
		}
		if (CTRL.tryToInsert(deal)){
			if (sendFlag){//强制推送
				user.getRecharge().changeMoney(-10);
				deal.issue();
			}
			deal.save();
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("result","ok");
			data.put("dealId",deal.getId());
			String str = JsonUtil.ObjectToJsonString(data);
			data(request,response,str);
			ServerLog.info(user.getAccount() + " deployed deal-buy ok ----> id is " + deal.getId());
		}
	}
}
