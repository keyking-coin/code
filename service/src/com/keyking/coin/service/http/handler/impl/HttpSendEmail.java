package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.email.EmailModule;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpSendEmail extends HttpHandler {
	//http://139.196.30.53:32104/HttpSendEmail?uid=x&pwd=x&name=x&theme=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
        long uid   = Long.parseLong(request.getParameter("uid"));
		String pwd = request.getParameter("pwd");
		String name = request.getParameter("name");//接受这昵称或者手机号码
		String theme = request.getParameter("theme");//主题
		String content = request.getPostValue();
		UserCharacter user = CTRL.search(uid);
		if (!user.getPwd().equals(pwd)){
			message(request,response,"非法的请求");
			return;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			message(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		if (StringUtil.isNull(theme)){
			message(request,response,"主题不能为空");
			return;
		}
		UserCharacter target = CTRL.searchByAccountOrNickName(name);
		if (target != null){
			synchronized (target) {
				Email email = new Email();
				email.setSenderId(uid);
				email.setUserId(target.getId());
				String time = TimeUtils.nowChStr();
				email.setTime(time);
				email.setTheme(theme);
				email.setContent(content);
				long id = PK.key("email");
				email.setId(id);
				target.addEmail(email);
				EmailModule module = new EmailModule();
				module.add("eamil",email);
				ModuleResp modules = new ModuleResp();
				modules.addModule(module);
				NET.sendMessageToClent(modules,target);
				ServerLog.info(user.getAccount() + " send eamil to " + target.getAccount());
				Map<String,Object> datas = new HashMap<String,Object>();
				datas.put("result","ok");
				datas.put("num",user.getEmails().size());
				String str = formatJosn(request,JsonUtil.ObjectToJsonString(datas));
				response.appendBody(str);
			}
		}else{
			message(request,response,"找不到目标用户");
		}
	}

}
