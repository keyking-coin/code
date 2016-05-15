package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.BankAccount;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpBankAccountChange extends HttpHandler {
	//http://139.196.30.53:32104/HttpBankAccountChange?uid=x&pwd=x&flag=0&accountName=x&account=x&openName=x&peopleName=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid       = Long.parseLong(request.getParameter("uid"));//用户编号
		String pwd     = request.getParameter("pwd");//验证码
		String flag    = request.getParameter("flag");//0添加,1删除
		String accountName = request.getParameter("accountName");//开户银行名称
		String account     = request.getParameter("account");//开户银行卡号
		String openName    = request.getParameter("openName");//开户地址
		String peopleName  = request.getParameter("peopleName");//开户人名称
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
			synchronized (user) {
				if (flag.equals("0")){
					BankAccount bankAccount = user.getBankAccount();
					if (!bankAccount.add(accountName,account,openName,peopleName)){
						message(request,response,"此卡已添加过了");
						return;
					}else{
						message(request,response,"ok");
					}
				}else{
					BankAccount bank = user.getBankAccount();
					if (!bank.remove(account)){
						message(request,response,"未找到要删除的银行卡记录");
					}else{
						message(request,response,"ok");
					}
				}
				user.save();
			}
		}
	}

}
