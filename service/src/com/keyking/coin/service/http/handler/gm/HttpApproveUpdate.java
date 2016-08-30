package com.keyking.coin.service.http.handler.gm;

import org.apache.http.client.methods.HttpGet;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.HttpDecoderUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpApproveUpdate extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			long id = Long.parseLong(request.getParameter("id"));
			String type = request.getParameter("type");
			String reason = request.getParameter("reason");
			UserCharacter admin = CTRL.search(1);
			UserCharacter user = CTRL.search(id);
			if (user != null && user.getSeller() != null){
				if (type.equals("0")){
					HttpGet get = new HttpGet("http://www.521uu.cc:321/del.php?fname=" + user.getSeller().getPic());
					HttpDecoderUtil.getHttpResponse(get);
					user.setSeller(null);
					CTRL.tryToSendEmailToUser(admin,TimeUtils.nowChStr(),"卖家认证未通过","你的卖家认证原因是:" + reason,user);
				}else{
					if (user.getSeller().isPass() || user.getPermission().seller()){
						response.put("result","目标已经是卖家了无需认证");
						return;
					}
					user.getSeller().setPass(true);
					CTRL.tryToSendEmailToUser(admin,TimeUtils.nowChStr(),"卖家认证通过","恭喜，您的卖家认证通过,谢谢使用本平台。",user);
				}
				user.save();
				response.put("result","ok");
			}else{
				response.put("result","找不到数据");
			}
		} catch (Exception e) {
			response.put("result","操作失败");
		}
	}

}
