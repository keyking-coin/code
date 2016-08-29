package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpGmUserUpdate extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			long uid    = Long.parseLong(request.getParameter("id"));
			String nickName = request.getParameter("nickName");
			String title = request.getParameter("title");
			String sellFlag = request.getParameter("sellFlag");
			String hp = request.getParameter("hp");
			String zp = request.getParameter("zp");
			String cp = request.getParameter("cp");
			String breach = request.getParameter("breach");
			String forbid_r = request.getParameter("forbid_r");
			String forbid_t = request.getParameter("forbid_t");
			UserCharacter user = CTRL.search(uid);
			if (user == null){
				response.put("result","找不用户，请联系开发人员");
				return;
			}
			if (!StringUtil.isNull(nickName)){
				user.setNikeName(nickName);
			}
			if (!StringUtil.isNull(title)){
				user.setTitle(title);
			}
			if (sellFlag.equals("true")){
				user.getPermission().setType(PermissionType.seller);
			}else{
				user.getPermission().setType(PermissionType.buyer);
			}
			if (!StringUtil.isNull(hp)){
				int num = Integer.parseInt(hp);
				user.getCredit().setHp(num);
			}
			if (!StringUtil.isNull(zp)){
				int num = Integer.parseInt(zp);
				user.getCredit().setZp(num);
			}
			if (!StringUtil.isNull(cp)){
				int num = Integer.parseInt(cp);
				user.getCredit().setCp(num);
			}
			if (!StringUtil.isNull(breach)){
				byte num = Byte.parseByte(breach);
				user.setBreach(num);
			}
			if (!StringUtil.isNull(forbid_r) && !StringUtil.isNull(forbid_t) ){
				user.getForbid().setReason(forbid_r);
				long endTime = TimeUtils.getTimes(forbid_t);
				user.getForbid().setEndTime(endTime);
			}else{
				user.getForbid().setReason(null);
				user.getForbid().setEndTime(0);
			}
			user.save();
			response.put("result","修改成功");
		} catch (Exception e) {
			response.put("result","修改失败");
			ServerLog.error("更新用户异常",e);
		}
	}

}
